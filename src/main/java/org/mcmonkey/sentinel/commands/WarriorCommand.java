package org.mcmonkey.sentinel.commands;

import com.github.rumsfield.konquest.api.KonquestAPI;
import com.github.rumsfield.konquest.api.model.KonquestDiplomacyType;
import com.github.rumsfield.konquest.api.model.KonquestKingdom;
import com.github.rumsfield.konquest.api.manager.KonquestKingdomManager;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.Bukkit;
import org.mcmonkey.sentinel.SentinelPlugin;
import org.mcmonkey.sentinel.SentinelTrait;
import org.mcmonkey.sentinel.targeting.SentinelTarget;
import net.citizensnpcs.api.CitizensAPI;
import net.citizensnpcs.api.trait.trait.Owner;

public class WarriorCommand extends JavaPlugin {

    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        if (cmd.getName().equalsIgnoreCase("warrior") && args.length == 2 && args[0].equalsIgnoreCase("attack")) {
            if (sender instanceof Player) {
                Player player = (Player) sender;
                String kingdomName = args[1];

                // Access the KonquestAPI instance via PluginManager
                Plugin konquestPlugin = Bukkit.getServer().getPluginManager().getPlugin("Konquest");
                if (konquestPlugin != null && konquestPlugin.isEnabled()) {
                    KonquestAPI konquestAPI = (KonquestAPI) konquestPlugin.getServer().getPluginManager().getPlugin("Konquest");
                    if (konquestAPI != null) {
                        KonquestKingdomManager kingdomManager = konquestAPI.getKingdomManager();

                        // Get the NPC owner (you may want to adjust how the NPC is identified here)
                        SentinelTrait sentinel = getSentinelForPlayer(player);
                        if (sentinel != null) {
                            String ownerPlayerName = sentinel.getNPC().getOrAddTrait(Owner.class).getOwner();
                            if (ownerPlayerName != null) {
                                // Get the kingdoms by name (replace this logic with actual kingdom data)
                                KonquestKingdom ownerKingdom = kingdomManager.getKingdom("OwnerKingdom");  // Get your owner's kingdom here
                                KonquestKingdom targetKingdom = kingdomManager.getKingdom(kingdomName);

                                if (ownerKingdom != null && targetKingdom != null) {
                                    KonquestDiplomacyType relationship = kingdomManager.getDiplomacy(ownerKingdom, targetKingdom);
                                    if (relationship == KonquestDiplomacyType.WAR) {
                                        // The NPC attacks the target kingdom because the kingdoms are at war
                                        attackKingdom(sentinel, targetKingdom);
                                        player.sendMessage("Your warrior is attacking the kingdom: " + kingdomName);
                                    } else {
                                        player.sendMessage("You cannot attack this kingdom. The kingdoms are not at war.");
                                    }
                                } else {
                                    player.sendMessage("Invalid kingdoms.");
                                }
                            }
                        }
                    }
                }
            }
        }
        return false;
    }

    private SentinelTrait getSentinelForPlayer(Player player) {
        // Implement logic to retrieve the SentinelTrait associated with the player or their NPC
        // Example: You can use CitizensAPI to find the NPC of the player and retrieve the SentinelTrait
        return null;  // You need to implement this to get the actual SentinelTrait
    }

    private void attackKingdom(SentinelTrait sentinel, KonquestKingdom targetKingdom) {
        // Implement the logic to command the NPC to attack the specified kingdom
        // This could be by triggering an attack action, notifying other components, etc.
    }
}
