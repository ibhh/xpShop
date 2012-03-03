/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package me.ibhh.xpShop;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

/**
 *
 * @author Simon
 */
public class Help {
    private xpShop plugin;
    public Help(xpShop pl){
        plugin = pl;
    }
    
    /**
     * Returns help.
     *
     * @param sender, action(String[])
     * @return
     */
    public void help(CommandSender sender, String[] args) {
        Player player = (Player) sender;
        if (!plugin.Blacklistcode.startsWith("1", 7)) {
        if (args.length == 0 || args.length >= 3) {
            plugin.PlayerLogger(player, "/xpShop infoxp <player>","");
            plugin.PlayerLogger(player, "/xpShop infolevel <player>","");
            plugin.PlayerLogger(player, "/xpShop info <action> <amount>","");
            plugin.PlayerLogger(player, "/xpShop sell <amount>","");
            plugin.PlayerLogger(player, "/xpShop buy <money>","");
            plugin.PlayerLogger(player, "/xpShop buylevel <amount>","");
            plugin.PlayerLogger(player, "/xpShop selllevel <amount>","");
            plugin.PlayerLogger(player, "/xpShop send <player> <amount>","");
        } else if (args.length == 2) {
            if (!(args[1].equals("buy") || args[1].equals("sell") || args[1].equals("selllevel") || args[1].equals("buylevel") || args[1].equals("send") || args[1].equals("info") || args[0].equals("infoxp") || args[0].equals("infolevel"))) {
                plugin.PlayerLogger(player, "Cant find command!", "Error");
            } else if (args[1].equals("buy")) {
                if (!plugin.Permission.checkpermissions(player, "xpShop.buy")) {
                    plugin.PlayerLogger(player, plugin.config.permissionserror, "Error");
                }
                plugin.PlayerLogger(player, "/xpShop buy <xp>","");
                plugin.PlayerLogger(player, plugin.config.helpbuy,"");
            } else if (args[1].equals("buylevel")) {
                if (!plugin.Permission.checkpermissions(player, "xpShop.buylevel")) {
                    plugin.PlayerLogger(player, plugin.config.permissionserror, "Error");
                }
                plugin.PlayerLogger(player, "/xpShop buylevel <amount>","");
                plugin.PlayerLogger(player, plugin.config.helpbuylevel,"");
            } else if (args[1].equals("sell")) {
                if (!plugin.Permission.checkpermissions(player, "xpShop.sell")) {
                    plugin.PlayerLogger(player, plugin.config.permissionserror, "Error");
                }
                plugin.PlayerLogger(player, "/xpShop sell <amount>","");
                plugin.PlayerLogger(player, plugin.config.helpsell,"");
            } else if (args[1].equals("selllevel")) {
                if (!plugin.Permission.checkpermissions(player, "xpShop.selllevel")) {
                    plugin.PlayerLogger(player, plugin.config.permissionserror, "Error");
                }
                plugin.PlayerLogger(player, "/xpShop selllevel <amount>","");
                plugin.PlayerLogger(player, plugin.config.helpsell,"");
            } else if (args[1].equals("info")) {
                if (!plugin.Permission.checkpermissions(player, "xpShop.info")) {
                    plugin.PlayerLogger(player, plugin.config.permissionserror, "Error");
                }
                plugin.PlayerLogger(player, "/xpShop info <action> <amount>","");
                plugin.PlayerLogger(player, plugin.config.helpinfo,"");
            } else if (args[1].equals("send")) {
                if (!plugin.Permission.checkpermissions(player, "xpShop.send")) {
                    plugin.PlayerLogger(player, plugin.config.permissionserror, "Error");
                }
                plugin.PlayerLogger(player, "/xpShop send <player> <amount>","");
                plugin.PlayerLogger(player, plugin.config.helpsend,"");
            } else if (args[1].equals("infoxp")) {
                if (!plugin.Permission.checkpermissions(player, "xpShop.infoxp")) {
                    plugin.PlayerLogger(player, plugin.config.permissionserror, "Error");
                }
                plugin.PlayerLogger(player, "/xpShop infoxp <player>","");
                plugin.PlayerLogger(player, plugin.config.helpinfoxp,"");
            } else if (args[1].equals("infolevel")) {
                if (!plugin.Permission.checkpermissions(player, "xpShop.infoxp")) {
                    plugin.PlayerLogger(player, plugin.config.permissionserror, "Error");
                }
                plugin.PlayerLogger(player, "/xpShop infolevel <player>","");
                plugin.PlayerLogger(player, plugin.config.helpinfolevel,"");
            }
        } else {
            if (!(args[0].equals("buy") || args[0].equals("sell") || args[0].equals("selllevel") || args[0].equals("buylevel") || args[0].equals("send") || args[0].equals("info") || args[0].equals("infoxp") || args[0].equals("infolevel"))) {
                plugin.PlayerLogger(player, "/xpShop infoxp <player>","");
                plugin.PlayerLogger(player, "/xpShop infolevel <player>","");
                plugin.PlayerLogger(player, "/xpShop info <action> <amount>","");
                plugin.PlayerLogger(player, "/xpShop sell <amount>","");
                plugin.PlayerLogger(player, "/xpShop buy <money>","");
                plugin.PlayerLogger(player, "/xpShop buylevel <amount>","");
                plugin.PlayerLogger(player, "/xpShop selllevel <amount>","");
                plugin.PlayerLogger(player, "/xpShop send <player> <amount>","");
            } else if (args[0].equals("buy")) {
                if (!plugin.Permission.checkpermissions(player, "xpShop.buy")) {
                    plugin.PlayerLogger(player, plugin.config.permissionserror, "Error");
                }
                plugin.PlayerLogger(player, "/xpShop buy <money>","");
                plugin.PlayerLogger(player, plugin.config.helpbuy,"");
            } else if (args[0].equals("buylevel")) {
                if (!plugin.Permission.checkpermissions(player, "xpShop.buylevel")) {
                    plugin.PlayerLogger(player, plugin.config.permissionserror, "Error");
                }
                plugin.PlayerLogger(player, "/xpShop buy <money>","");
                plugin.PlayerLogger(player, plugin.config.helpbuylevel,"");
            } else if (args[0].equals("sell")) {
                if (!plugin.Permission.checkpermissions(player, "xpShop.sell")) {
                    plugin.PlayerLogger(player, plugin.config.permissionserror, "Error");
                }
                plugin.PlayerLogger(player, "/xpShop sell <amount>","");
                plugin.PlayerLogger(player, plugin.config.helpsell,"");
            } else if (args[0].equals("selllevel")) {
                if (!plugin.Permission.checkpermissions(player, "xpShop.selllevel")) {
                    plugin.PlayerLogger(player, plugin.config.permissionserror, "Error");
                }
                plugin.PlayerLogger(player, "/xpShop selllevel <amount>","");
                plugin.PlayerLogger(player, plugin.config.helpselllevel,"");
            } else if (args[0].equals("info")) {
                if (!plugin.Permission.checkpermissions(player, "xpShop.info")) {
                    plugin.PlayerLogger(player, plugin.config.permissionserror, "Error");
                }
                plugin.PlayerLogger(player, "/xpShop info <action> <amount>","");
                plugin.PlayerLogger(player, plugin.config.helpinfo,"");
            } else if (args[0].equals("send")) {
                if (!plugin.Permission.checkpermissions(player, "xpShop.send")) {
                    plugin.PlayerLogger(player, plugin.config.permissionserror, "Error");
                }
                plugin.PlayerLogger(player, "/xpShop send <player> <amount>","");
                plugin.PlayerLogger(player, plugin.config.helpsend,"");
            } else if (args[0].equals("infoxp")) {
                if (!plugin.Permission.checkpermissions(player, "xpShop.infoxp")) {
                    plugin.PlayerLogger(player, plugin.config.permissionserror, "Error");
                }
                plugin.PlayerLogger(player, "/xpShop infoxp <player>","");
                plugin.PlayerLogger(player, plugin.config.helpinfoxp,"");
            } else if (args[0].equals("infolevel")) {
                if (!plugin.Permission.checkpermissions(player, "xpShop.infoxp")) {
                    plugin.PlayerLogger(player, plugin.config.permissionserror, "Error");
                }
                plugin.PlayerLogger(player, "/xpShop infolevel <player>","");
                plugin.PlayerLogger(player, plugin.config.helpinfolevel,"");
            }
        }
        } else {
            plugin.blacklistLogger(sender);
        }
    }
}
