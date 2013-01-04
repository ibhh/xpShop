package me.ibhh.xpShop;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import ru.tehkode.permissions.PermissionManager;
import ru.tehkode.permissions.PermissionUser;
import ru.tehkode.permissions.bukkit.PermissionsEx;

public class PermissionsChecker {

    private xpShop plugin;
    public int PermPlugin = 0;

    public PermissionsChecker(xpShop pl, String von) {
        this.plugin = pl;
        final String von2 = von;

        plugin.getServer().getScheduler().runTaskLaterAsynchronously(plugin, new Runnable() {
            @Override
            public void run() {
                try {
                    plugin.Logger("checking PermissionsPlugin!", "Debug");
                    searchpermplugin();
                } catch (Exception e) {
                    plugin.report.report(3324, "Checking Permissions plugin failed", e.getMessage(), "PermissionsChecker", e);
                }
            }
        }, 0);
    }

    public void searchpermplugin() {
        try {
            plugin.getServer().getServicesManager().getRegistration(ru.tehkode.permissions.bukkit.PermissionsEx.class);
            PermPlugin = 2;
            plugin.Logger("Permissions: Hooked into PermissionsEX!", "Debug");
            return;
        } catch (NoClassDefFoundError e) {
        }
        try {
            plugin.getServer().getServicesManager().getRegistration(org.anjocaido.groupmanager.GroupManager.class);
            PermPlugin = 3;
            plugin.Logger("Permissions: Hooked into GroupManager!", "Debug");
            return;
        } catch (NoClassDefFoundError e) {
        }
        try {
            plugin.getServer().getServicesManager().getRegistration(de.bananaco.bpermissions.api.util.CalculableType.class);
            PermPlugin = 4;
            plugin.Logger("Permissions: Hooked into bPermissions!", "Debug");
            return;
        } catch (NoClassDefFoundError e) {
        }
        PermPlugin = 1;
    }

    public boolean checkpermissionssilent(Player player, String action) {
        if (plugin.toggle) {
            return false;
        }
        try {
            if (player.isOp()) {
                return true;
            }
            if (PermPlugin == 1) {
                try {
                    return player.hasPermission(action) || player.hasPermission(action.toLowerCase());
                } catch (Exception e) {
                    e.printStackTrace();
                    plugin.report.report(3325, "Couldnt check permission with BukkitPermission", e.getMessage(), "PermissionsChecker", e);
                    return false;
                }
            } else if (PermPlugin == 2) {
                if (!Bukkit.getPluginManager().isPluginEnabled("PermissionsEx")) {
                    return false;
                }
                try {
                    PermissionUser user = PermissionsEx.getUser(player);;
                    if (user.has(action)) {
                        return true;
                    }
                    return false;
                } catch (Exception e) {
                    if (plugin.config.debug) {
                        e.printStackTrace();
                    }
                    try {
                        PermissionManager permissions = PermissionsEx.getPermissionManager();
                        if (permissions.has(player, action)) {
                            return true;
                        }
                        return false;
                    } catch (Exception e1) {
                        e.printStackTrace();
                        plugin.Logger("Error on checking Permission with PermissionsEx!", "Error");
                        plugin.Logger("May the /reload command caused this issue!", "Error");
                        plugin.Logger("May your permissions.yml is wrong, please check it!", "Error");
                        plugin.Logger("------------", "Error");
                        plugin.Logger("If you mean this is an error, use /" + plugin.getName() + " report myissueisthis", "Error");
                        plugin.Logger("------------", "Error");
                        return false;
                    }
                }
            } else if (PermPlugin == 3) {
                if (!Bukkit.getPluginManager().isPluginEnabled("GroupManager")) {
                    return false;
                }
                try {
                    return player.hasPermission(action) || player.hasPermission(action.toLowerCase());
                } catch (Exception e) {
                    e.printStackTrace();
                    plugin.report.report(3327, "Couldnt check permission with GroupManager", e.getMessage(), "PermissionsChecker", e);
                    return false;
                }
            } else if (PermPlugin == 4) {
                if (!Bukkit.getPluginManager().isPluginEnabled("bPermissions")) {
                    return false;
                }
                try {
                    if (player.hasPermission(action) || player.hasPermission(action.toLowerCase())) {
                        return true;
                    } else {
                        return false;
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                    plugin.report.report(3327, "Couldnt check permission with bPermissions", e.getMessage(), "PermissionsChecker", e);
                    return false;
                }
            } else {
                System.out.println("PermissionsEx plugin are not found.");
                return false;
            }
        } catch (Exception e) {
            plugin.Logger("Error on checking permissions!", "Error");
            plugin.report.report(3328, "Error on checking permissions", e.getMessage(), "PermissionsChecker", e);
            e.printStackTrace();
            return false;
        }
    }

    public boolean checkpermissions(Player player, String action) {
        if (plugin.toggle) {
            return false;
        }
        try {
            if (player.isOp()) {
                return true;
            }
            if (PermPlugin == 1) {
                try {
                    if (player.hasPermission(action) || player.hasPermission(action.toLowerCase())) {
                        return true;
                    } else {
                        plugin.PlayerLogger(player, player.getName() + " " + plugin.getConfig().getString("permissions.error." + plugin.getConfig().getString("language")) + " (" + action + ")", "Error");
                        return false;
                    }
                } catch (Exception e) {
                    plugin.Logger("Error on checking permissions with BukkitPermissions!", "Error");
                    plugin.report.report(3329, "Couldnt check permission with BukkitPermissions", e.getMessage(), "PermissionsChecker", e);
                    plugin.PlayerLogger(player, "Error on checking permissions with BukkitPermissions!", "Error");
                    e.printStackTrace();
                    return false;
                }
            } else if (PermPlugin == 2) {
                if (!Bukkit.getPluginManager().isPluginEnabled("PermissionsEx")) {
                    plugin.PlayerLogger(player, "PermissionsEX is not enabled!", "Error");
                    return false;
                }
                try {
                    PermissionUser user = PermissionsEx.getUser(player);
                    if (user.has(action)) {
                        return true;
                    } else {
                        plugin.PlayerLogger(player, player.getName() + " " + plugin.getConfig().getString("permissions.error." + plugin.getConfig().getString("language")) + " (" + action + ")", "Error");
                        return false;
                    }
                } catch (Exception e) {
                    if (plugin.getConfig().getBoolean("debug")) {
                        e.printStackTrace();
                    }
                    try {
                        PermissionManager permissions = PermissionsEx.getPermissionManager();

                        if (permissions.has(player, action)) {
                            return true;
                        } else {
                            plugin.PlayerLogger(player, player.getName() + " " + plugin.getConfig().getString("permissions.error." + plugin.getConfig().getString("language")) + " (" + action + ")", "Error");
                            return false;
                        }
                    } catch (Exception e1) {
                        plugin.PlayerLogger(player, "Error on checking Permission with PermissionsEx! Please inform an Admin!", "Error");
                        plugin.Logger("Error on checking Permission with PermissionsEx!", "Error");
                        plugin.Logger("May the /reload command caused this issue!", "Error");
                        plugin.Logger("May your permissions.yml is wrong, please check it!", "Error");
                        plugin.Logger("------------", "Error");
                        plugin.Logger("If you mean this is an error, use /" + plugin.getName() + " report myissueisthis", "Error");
                        plugin.Logger("------------", "Error");
                        e.printStackTrace();
                        return false;
                    }
                }
            } else if (PermPlugin == 3) {
                if (!Bukkit.getPluginManager().isPluginEnabled("GroupManager")) {
                    plugin.PlayerLogger(player, "GroupManager is not enabled!", "Error");
                    return false;
                }
                try {
                    if (player.hasPermission(action) || player.hasPermission(action.toLowerCase())) {
                        return true;
                    } else {
                        plugin.PlayerLogger(player, player.getName() + " " + plugin.getConfig().getString("permissions.error." + plugin.getConfig().getString("language")) + " (" + action + ")", "Error");
                        return false;
                    }
                } catch (Exception e) {
                    plugin.Logger("Error on checking permissions with GroupManager!", "Error");
                    plugin.report.report(3331, "Couldnt check permission with GroupManager", e.getMessage(), "PermissionsChecker", e);
                    plugin.PlayerLogger(player, "Error on checking permissions with GroupManager!", "Error");
                    e.printStackTrace();
                    return false;
                }
            } else if (PermPlugin == 4) {
                if (!Bukkit.getPluginManager().isPluginEnabled("bPermissions")) {
                    plugin.PlayerLogger(player, "bPermissions is not enabled!", "Error");
                    return false;
                }
                try {
                    if (player.hasPermission(action) || player.hasPermission(action.toLowerCase())) {
                        return true;
                    } else {
                        plugin.PlayerLogger(player, player.getName() + " " + plugin.getConfig().getString("permissions.error." + plugin.getConfig().getString("language")) + " (" + action + ")", "Error");
                        return false;
                    }
                } catch (Exception e) {
                    plugin.Logger("Error on checking permissions with bPermissions!", "Error");
                    plugin.report.report(3332, "Couldnt check permission with bPermissions", e.getMessage(), "PermissionsChecker", e);
                    plugin.PlayerLogger(player, "Error on checking permissions with bPermissions!", "Error");
                    e.printStackTrace();
                    return false;
                }
            } else {
                plugin.PlayerLogger(player, player.getName() + " " + plugin.getConfig().getString("permissions.error." + plugin.getConfig().getString("language")) + " (" + action + ")", "Error");
                System.out.println("PermissionsEx plugin are not found.");
                return false;
            }
        } catch (Exception e) {
            plugin.Logger("Error on checking permissions!", "Error");
            plugin.report.report(3333, "Error on checking permissions", e.getMessage(), "PermissionsChecker", e);
            plugin.PlayerLogger(player, "Error on checking permissions!", "Error");
            e.printStackTrace();
            return false;
        }
    }
}