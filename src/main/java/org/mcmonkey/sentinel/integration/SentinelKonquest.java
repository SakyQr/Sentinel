package org.mcmonkey.sentinel.integration;

import net.citizensnpcs.api.CitizensAPI;
import net.citizensnpcs.api.trait.trait.Owner;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.mcmonkey.sentinel.SentinelPlugin;
import org.mcmonkey.sentinel.SentinelTrait;
import org.mcmonkey.sentinel.SentinelUtilities;
import org.mcmonkey.sentinel.targeting.SentinelTarget;
import org.mcmonkey.sentinel.utilities.SentinelAPIBreakageFix;
import com.github.rumsfield.konquest.api.*;
import com.github.rumsfield.konquest.api.model.KonquestDiplomacyType;
import com.github.rumsfield.konquest.api.model.KonquestKingdom;
import com.github.rumsfield.konquest.api.manager.KonquestKingdomManager;
import org.bukkit.plugin.Plugin;
import org.bukkit.Bukkit;

import java.util.*;

public class SentinelKonquest extends SentinelTarget {

    public static SentinelTarget NPCS = new SentinelKonquest(new EntityType[]{}, "NPC");
    public static SentinelTarget OWNER = new SentinelKonquest(new EntityType[]{}, "OWNER");
    public static SentinelTarget PLAYERS = new SentinelKonquest(new EntityType[]{EntityType.PLAYER}, "PLAYER");
    public static SentinelTarget ENEMY_KINGDOM = new SentinelKonquest(new EntityType[]{EntityType.PLAYER}, "ENEMY_KINGDOM", "ENEMYKINGDOM");
    public static HashSet<EntityType> NATIVE_COMBAT_CAPABLE_TYPES = new HashSet<>(Arrays.asList(EntityType.ZOMBIE, EntityType.SKELETON));

    public static HashSet<SentinelTarget> forEntityType(EntityType type) {
        return SentinelPlugin.entityToTargets.get(type);
    }

    public static SentinelTarget forName(String name) {
        return SentinelPlugin.targetOptions.get(name.toUpperCase());
    }

    private String[] names;
    private HashSet<EntityType> types;

    public SentinelKonquest(EntityType[] types, String... names) {
        super(types, names);
        this.names = names;
        this.types = new HashSet<>(Arrays.asList(types));
        for (String name : names) {
            SentinelPlugin.targetOptions.put(name, this);
            SentinelPlugin.targetOptions.put(name + "S", this);
        }
        for (EntityType type : types) {
            SentinelPlugin.entityToTargets.get(type).add(this);
        }
    }

    public String name() {
        return names[0];
    }

    public boolean isTarget(LivingEntity entity) {
        return isTarget(entity, null);
    }

    public boolean isTarget(LivingEntity entity, SentinelTrait sentinel) {
        if (this.names[0].equals("ENEMY_KINGDOM") && entity instanceof Player && sentinel != null) {
            Plugin konquestPlugin = Bukkit.getServer().getPluginManager().getPlugin("Konquest");

            if (konquestPlugin != null && konquestPlugin.isEnabled()) {
                KonquestAPI konquestAPI = (KonquestAPI) konquestPlugin.getServer().getPluginManager().getPlugin("Konquest");

                if (konquestAPI != null) {
                    KonquestKingdomManager kingdomManager = konquestAPI.getKingdomManager();

                    Player targetPlayer = (Player) entity;
                    String ownerPlayerName = sentinel.getNPC().getOrAddTrait(Owner.class).getOwner();

                    if (ownerPlayerName != null) {
                        Player ownerPlayer = (Player) sentinel.getNPC().getEntity();
                        String ownerKingdomName = "SomeKingdomName";
                        String targetKingdomName = "SomeTargetKingdomName";

                        KonquestKingdom ownerKingdom = kingdomManager.getKingdom(ownerKingdomName);
                        KonquestKingdom targetKingdom = kingdomManager.getKingdom(targetKingdomName);

                        if (ownerKingdom != null && targetKingdom != null) {
                            KonquestDiplomacyType relationship = kingdomManager.getDiplomacy(ownerKingdom, targetKingdom);
                            return relationship == KonquestDiplomacyType.WAR;
                        }
                    }
                }
            }

            return false;
        }

        if (types.contains(entity.getType())) {
            return true;
        }
        if (this == NPCS && CitizensAPI.getNPCRegistry().isNPC(entity)) {
            return true;
        }
        if (this == OWNER && sentinel != null
                && SentinelUtilities.uuidEquals(entity.getUniqueId(), sentinel.getNPC().getOrAddTrait(Owner.class).getOwnerId())) {
            return true;
        }

        return false;
    }
}

