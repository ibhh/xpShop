package me.ibhh.xpShop;

import java.util.HashMap;

import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

/**
 * 
 * @author ibhh
 */
public class BottleManager {

    private xpShop plugin;
    private HashMap<Player, Integer> Playermap = new HashMap<Player, Integer>();

    public BottleManager(xpShop pl) {
	plugin = pl;
    }

    public void removefromMap(Player player) {
	Playermap.remove(player);
    }

    public void addinmap(Player player, int anzahl) {
	Playermap.put(player, anzahl);
    }

    public boolean isinmap(Player player) {
	if (Playermap.containsKey(player)) {
	    return true;
	} else {
	    return false;
	}
    }

    public boolean hasEnoughBottles(Player player, int anzahl) {
	Inventory inv = player.getInventory();
	boolean hasBottle = inv.contains(Material.GLASS_BOTTLE, anzahl);
	return hasBottle;
    }

    public boolean replaceBottles(Player player, int anzahl) {
	boolean ret = false;
	Inventory inv = player.getInventory();
	ItemStack[] stacks = inv.getContents();
	int temp = anzahl;
	for (ItemStack stack : stacks) {
	    if (stack != null) {
		int stackanzahl = stack.getAmount();
		if (stack.getType().equals(Material.GLASS_BOTTLE)) {
		    if (temp > 0) {
			if (temp < stackanzahl) {
			    inv.remove(stack);
			    ItemStack rest = new ItemStack(Material.GLASS_BOTTLE, stackanzahl - temp);
			    inv.addItem(rest);
			    ItemStack XPBottles = new ItemStack(Material.EXP_BOTTLE, temp);
			    inv.addItem(XPBottles);
			    player.saveData();
			    temp = 0;
			    plugin.Logger("Filled " + stackanzahl + " Bottles of " + player.getName(), "Debug");
			} else {
			    temp = temp - stackanzahl;
			    inv.remove(stack);
			    ItemStack XPBottles = new ItemStack(Material.EXP_BOTTLE, stackanzahl);
			    inv.addItem(XPBottles);
			    player.saveData();
			    plugin.Logger("Filled " + stackanzahl + " Bottles of " + player.getName(), "Debug");
			}
		    }
		}
	    }
	}
	return ret;
    }

    public boolean registerCommandXPBottles(final Player player, int anzahl) {
	if(anzahl < 1) {
	    plugin.PlayerLogger(player, "Invalid amount!", "ERROR");
	    return false;
	}
	if (!Playermap.containsKey(player)) {
	    if (player.getTotalExperience() >= anzahl * plugin.getConfig().getInt("XPperBottle")) {
		if (hasEnoughBottles(player, anzahl)) {
		    Playermap.put(player, anzahl);
		    plugin.Logger("Player: " + player.getName() + " has enough XP to fill " + anzahl + " Bottles (each " + plugin.getConfig().getInt("XPperBottle") + " XP)", "Debug");
		    plugin.PlayerLogger(player, String.format(plugin.getConfig().getString("BottleFilling.Info." + plugin.config.language), anzahl * plugin.getConfig().getInt("XPperBottle"), anzahl,
			    plugin.getConfig().getInt("XPperBottle")), "Warning");
		    plugin.PlayerLogger(player, plugin.getConfig().getString("BottleFilling.PleaseConfirm1." + plugin.config.language), "");
		    plugin.PlayerLogger(player, plugin.getConfig().getString("BottleFilling.PleaseConfirm2." + plugin.config.language), "");
		    plugin.getServer().getScheduler().runTaskLater(plugin, new Runnable() {

			@Override
			public void run() {
			    if (Playermap.containsKey(player)) {
				plugin.Logger("Player: " + player.getName() + " Command (BottleFilling) timed out", "Debug");
				Playermap.remove(player);
				plugin.PlayerLogger(player,
					String.format(plugin.getConfig().getString("BottleFilling.ConfirmTimeOut." + plugin.config.language), plugin.getConfig().getInt("CooldownofBottleFilling")),
					"Warning");
			    }
			}
		    }, plugin.getConfig().getInt("CooldownofBottleFilling") * 20);
		    return true;
		} else {
		    plugin.PlayerLogger(player, String.format(plugin.getConfig().getString("BottleFilling.notenoughbottles." + plugin.config.language), anzahl), "Error");
		    return false;
		}
	    } else {
		plugin.PlayerLogger(player, String.format(plugin.getConfig().getString("BottleFilling.NotEnoughXP." + plugin.config.language), anzahl * plugin.getConfig().getInt("XPperBottle"),
			anzahl, plugin.getConfig().getInt("XPperBottle")), "Error");
		return false;
	    }
	} else {
	    plugin.PlayerLogger(player, plugin.getConfig().getString("BottleFilling.nocommand." + plugin.config.language), "");
	    return false;
	}
    }

    public void cancelChange(Player player) {
	if (Playermap.containsKey(player)) {
	    Playermap.remove(player);
	}
	plugin.PlayerLogger(player, plugin.getConfig().getString("BottleFilling.canceled." + plugin.config.language), "");
    }

    public void confirmChange(Player player) {
	if(Playermap.containsKey(player)) {
	    plugin.PlayerLogger(player, "Can not confirm because no entry was found!", "ERROR");
	    return;
	}
	int anzahl = Playermap.get(player);
	if (player.getTotalExperience() >= anzahl * plugin.getConfig().getInt("XPperBottle")) {
	    if (hasEnoughBottles(player, anzahl)) {
		plugin.UpdateXP(player, -anzahl * plugin.getConfig().getInt("XPperBottle"), "Bottle");
		executeCommandXPBottles(player, anzahl);
		if (Playermap.containsKey(player)) {
		    Playermap.remove(player);
		}
		plugin.PlayerLogger(player, plugin.getConfig().getString("BottleFilling.confirmed." + plugin.config.language), "");
		plugin.PlayerLogger(player, String.format(plugin.getConfig().getString("BottleFilling.executed." + plugin.config.language), anzahl * plugin.getConfig().getInt("XPperBottle"), anzahl,
			plugin.getConfig().getInt("XPperBottle")), "");
	    } else {
		plugin.PlayerLogger(player, String.format(plugin.getConfig().getString("BottleFilling.notenoughbottles." + plugin.config.language), anzahl), "Error");
	    }
	} else {
	    plugin.PlayerLogger(player, String.format(plugin.getConfig().getString("BottleFilling.NotEnoughXp." + plugin.config.language), anzahl * plugin.getConfig().getInt("XPperBottle"), anzahl,
		    plugin.getConfig().getInt("XPperBottle")), "Error");
	}
    }

    public boolean executeCommandXPBottles(Player player, int anzahl) {
	if (hasEnoughBottles(player, anzahl)) {
	    replaceBottles(player, anzahl);
	    return true;
	} else {
	    return false;
	}
    }
}
