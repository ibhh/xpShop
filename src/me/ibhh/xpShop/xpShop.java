package me.ibhh.xpShop;

import java.io.File;
import java.sql.SQLException;
import java.util.logging.Level;
import me.ibhh.xpShopupdatehelper.xpShopupdatehelper;
import net.minecraft.server.EntityPlayer;
import net.minecraft.server.ItemInWorldManager;
import net.minecraft.server.MinecraftServer;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.craftbukkit.CraftServer;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

public class xpShop extends JavaPlugin {

    private String ActionxpShop;
    private int buy;
    private int sell;
    private int buylevel;
    private int selllevel;
    public double getmoney;
    public int SubstractedXP;
    public float Version = 0;
    int rounds1 = 0;
    int rounds = 0;
    private Help Help;
    public static String PrefixConsole = "[xpShop] ";
    public static String Prefix = "[xpShop] ";
    private PanelControl panel;
    public ConfigHandler config;
    public xpShopListener ListenerShop;
    public Update upd;
    public xpShop xpShop = this;
    public String Blacklistcode = "0000000000000";
    public String Blacklistmsg;
    public SQLConnectionHandler SQL;
    public String versionsfile = "http://ibhh.de:80/aktuelleversionxpShop.html";
    public String jarfile = "http://ibhh.de:80/xpShop.jar";
    public static boolean updateaviable = false;
    public xpShopupdatehelper helper;
    public StatsHandler Stats;
    public PermissionsChecker PermissionsHandler;
    public iConomyHandler MoneyHandler;

    public xpShop() {
    }

    /**
     * Called by Bukkit on stopping the server
     *
     * @param
     * @return
     */
    @Override
    public void onDisable() {
        long timetemp = System.currentTimeMillis();
        if (config.usedbtomanageXP) {
            SQL.CloseCon();
            SQL = null;
        }
//        Stats.CloseCon();
        try {
            finalize();
        } catch (Throwable ew) {
            Logger("cant finalize!", "Error");
        }
        forceUpdate();
        blacklistUpdate();
        timetemp = System.currentTimeMillis() - timetemp;
        Logger("disabled in " + timetemp + "ms", "");
    }

    public File getLocation() {
        File location = xpShop.getFile();
        return location;
    }

    /**
     * Delete an download new version of xpShop in the Update folder.
     *
     * @param
     * @return
     */
    public boolean autoUpdate(String url, String path, String name, String type) {

        try {
            upd.autoDownload(url, path, name, type);
            return true;
        } catch (Exception e) {
            Logger(e.getMessage(), "Error");
            try {
                upd.autoDownload(url, path + "xpShop" + File.separator, name, type);
                return true;
            } catch (Exception ex) {
                Logger(ex.getMessage(), "Error");
                return false;
            }
        }
    }

    public void forceUpdate() {
        String URL = "http://ibhh.de:80/aktuelleversion" + this.getDescription().getName() + ".html";
        if ((UpdateAvailable(URL, Version) == true)) {
            Logger("New version: " + upd.getNewVersion(URL) + " found!", "Warning");
            Logger("******************************************", "Warning");
            Logger("*********** Please update!!!! ************", "Warning");
            Logger("* http://ibhh.de/xpShop.jar *", "Warning");
            Logger("******************************************", "Warning");
            if (getConfig().getBoolean("autodownload") == true) {
                try {
                    String path = "plugins" + File.separator;
                    if (autoUpdate("http://ibhh.de/xpShop.jar", path, "xpShop.jar", "forceupdate")) {
                        Logger("Downloaded new Version!", "Warning");
                        Logger("xpShop will be updated on the next restart!", "Warning");
                    } else {
                        Logger(" Cant download new Version!", "Warning");
                    }
                } catch (Exception e) {
                    Logger("Error on donwloading new Version!", "Error");
                    e.printStackTrace();
                }
            } else {
                Logger("Please type [xpShop download] to download manual! ", "Warning");
            }
        }
    }

    public void blacklistcheck() {
        String temp[] = upd.getBlacklisted("http://ibhh.de/BlacklistxpShop.html");
        if (temp != null) {
            Blacklistcode = temp[1];
            Blacklistmsg = temp[2];
            Logger("Your version is blacklisted because of bugs, after restart an bugfix will be installed!", "Warning");
            Logger("Reason: " + Blacklistmsg, "Warning");
        }
        config.getBlacklistCode();
    }

    public void blacklistUpdate() {
        String temp[] = upd.getBlacklisted("http://ibhh.de/BlacklistxpShop.html");
        if (temp != null) {
            Blacklistcode = temp[1];
            Blacklistmsg = temp[2];
            Logger("Your version is blacklisted because of bugs, after restart an bugfix will be installed!", "Warning");
            Logger("Reason: " + Blacklistmsg, "Warning");
            String URL = "http://ibhh.de:80/aktuelleversion" + this.getDescription().getName() + ".html";
            if (UpdateAvailable(URL, Version)) {
                try {
                    String path = "plugins" + File.separator;
                    if (autoUpdate("http://ibhh.de/xpShop.jar", path, "xpShop.jar", "forceupdate")) {
                        Logger("Downloaded new Version!", "Warning");
                        Logger("xpShop will be updated on the next restart!", "Warning");
                    } else {
                        Logger(" Cant download new Version!", "Warning");
                    }
                } catch (Exception e) {
                    Logger("Error on donwloading new Version!", "Error");
                    e.printStackTrace();
                }
            } else {
                Logger("No Bugfix aviable yet!!", "Error");
            }
        }
    }

    /**
     * Gets version.
     *
     * @param
     * @return float: Version of the installed plugin.
     */
    public float aktuelleVersion() {
        try {
            Version = Float.parseFloat(getDescription().getVersion());
        } catch (Exception e) {
            Logger("Could not parse version in float", "");
        }
        return Version;
    }

    /**
     * Compares Version to newVersion
     *
     * @param url from newVersion file + currentVersion
     * @return true if newVersion recommend.
     */
    public boolean UpdateAvailable(String url, final float currVersion) {
        if (upd.getNewVersion(versionsfile) > currVersion) {
            xpShop.updateaviable = true;
        }
        if (updateaviable) {
            return true;
        } else {
            return false;
        }
    }

    public Player getmyOfflinePlayer(String[] args, int index) {
        String playername = args[index];
        if (config.debug) {
            Logger("Empfaenger: " + playername, "Debug");
        }
        Player player = getServer().getPlayerExact(playername);
        if (player == null) {
            player = getServer().getPlayer(playername);
        }
        if (player == null) {
            for (OfflinePlayer p : Bukkit.getServer().getOfflinePlayers()) {
                OfflinePlayer offp = p;
                if (offp.getName().toLowerCase().equals(playername.toLowerCase())) {
                    if (config.debug) {
                        Logger("Player has same name: " + offp.getName(), "Debug");
                    }
                    if (offp != null) {
                        if (offp.hasPlayedBefore()) {
                            player = (Player) offp.getPlayer();
                            if (config.debug) {
                                Logger("Player has Played before: " + offp.getName(), "Debug");
                            }
                        }
                        break;
                    }
                }
            }
        }
        if (player == null) {
            MinecraftServer server = ((CraftServer) this.getServer()).getServer();
            EntityPlayer entity = new EntityPlayer(server, server.getWorldServer(0), args[index], new ItemInWorldManager(server.getWorldServer(0)));
            player = entity == null ? null : (Player) entity.getBukkitEntity();
            if (player != null) {
                player.loadData();
                return player;
            }
        }
        if (config.debug && player != null) {
            Logger("Empfaengername after getting Player: " + player.getName(), "Debug");
        }
        return player;
    }
//        public Player getmyOfflinePlayer(String[] args, int index) {
//        String playername = args[index];
//        Player player = getServer().getPlayerExact(playername);
//        if (player == null) {
//            player = getServer().getPlayer(playername);
//        }
//        if (player == null) {
//            for (OfflinePlayer p : Bukkit.getServer().getOfflinePlayers()) {
//                OfflinePlayer offp = p;
//                if (offp.getName().toLowerCase().equals(playername.toLowerCase())) {
//                    if (p != null) {
//                        if (offp.hasPlayedBefore()) {
//                            player = offp.getPlayer();
//                            return player;
//                        }
//                    }
//                }
//            }
//        }
////        if (player == null) {
////            MinecraftServer server = ((CraftServer)this.getServer()).getServer();
////            EntityPlayer entity = new EntityPlayer(server, server.getWorldServer(0), args[index], new ItemInWorldManager(server.getWorldServer(0)));
////            player = entity == null ? null : (Player) entity.getBukkitEntity();
////            if (player != null) {
////                player.loadData();
////                return player;
////            }
////        }
//        return player;
//    }
    //        Player player;
//        player = (Player) getServer().getOfflinePlayer(name);
//        return player;

    public void openGUI() {
        panel = new PanelControl(this);
        panel.setSize(400, 300);
        panel.setLocation(200, 300);
        panel.setVisible(true);
    }

    /**
     * Called by Bukkit on starting the server
     *
     * @param
     * @return
     */
    @Override
    public void onEnable() {
        long timetemp1 = System.nanoTime();
        Standartstart(1);
        ListenerShop = new xpShopListener(this);
        Standartstart(2);
//        if (xpShop.getServer().getPluginManager().getPlugin("xpShopupdatehelper") == null) {
//            String path = "plugins" + File.separator;
//            Logger("Download xpShophelper !", "Warning");
//            try {
//                upd.autoDownload("http://ibhh.de/xpShopupdatehelper.jar", path, "xpShopupdatehelper.jar", "forceupdate");
//            } catch (Exception ex) {
//                Logger("xpShop server currently not aviable!", "Error");
//            }
//            Logger("Downloaded xpShophelper successfully!", "Warning");
//            Logger(".. Done!", "Warning");
//        }
        if (!(Blacklistcode.startsWith("1"))) {
            if (getConfig().getBoolean("firstRun")) {
//                try{
//                upd.autoDownload("http://ibhh.de/xpShopconfigdefault.yml", getDataFolder().toString(), "xpShopconfigdefault.yml", "forceupdate");
//                } catch(Exception w){
//                    Logger("Cant save Default config!", "Error");
//                }
                try {
                    openGUI();
                } catch (Exception e) {
                    Logger("You cant use the gui, notice that.", "Error");
                    getConfig().set("firstRun", false);
                    saveConfig();
                    reloadConfig();
                    config.reload();
                }
            }
            Help = new Help(this);
            MoneyHandler = new iConomyHandler(this);
            PermissionsHandler = new PermissionsChecker(this, "xpShop");
            Standartstart(3);
            if (config.usedbtomanageXP) {
                SQL = new SQLConnectionHandler(this);
                SQL.createConnection();
                SQL.PrepareDB();
            }
//            Stats = new StatsHandler(this);
//            this.getServer().getScheduler().scheduleAsyncDelayedTask(this, new Runnable() {
//
//                @Override
//                public void run() {
//                    
//                    Stats.createConnection();
//                    Stats.PrepareDB();
//                }
//            }, 1);


        } else {
            Logger(this.getDescription().getName() + " version " + Version + " is blacklisted because of bugs, after restart an bugfix will be installed!", "Warning");
            Logger("All funktions deactivated to prevent the server!", "Warning");
        }
//        if (this.getServer().getPluginManager().getPlugin("PermissionsHandler") == null) {
//            String path = "plugins" + File.separator;
//            Logger("Download PermissionsHandler !", "Warning");
//            try {
//                upd.autoDownload("http://ibhh.de/PermissionsHandler.jar", path, "PermissionsHandler.jar", "forceupdate");
//            } catch (Exception ex) {
//                Logger("ibhhs server currently not aviable!", "Error");
//            }
//            Logger("Downloaded PermissionsHandler successfully!", "Warning");
//            Logger(".. Done!", "Warning");
//            Logger("Restart the server to enable Permissions support, other wise it wont work!", "Error");
//            Blacklistcode = "1111111111111";
//            Blacklistmsg = "A needed plugin isnt installed! PermissionsHandler or MoneyHandler";
//        }
//        if (this.getServer().getPluginManager().getPlugin("MoneyHandler") == null) {
//            String path = "plugins" + File.separator;
//            Logger("Download MoneyHandler !", "Warning");
//            try {
//                upd.autoDownload("http://ibhh.de/MoneyHandler.jar", path, "MoneyHandler.jar", "forceupdate");
//            } catch (Exception ex) {
//                Logger("ibhhs server currently not aviable!", "Error");
//            }
//            Logger("Downloaded MoneyHandler successfully!", "Warning");
//            Logger(".. Done!", "Warning");
//            Logger("Restart the server to enable any money support, other wise it wont work!", "Error");
//            Blacklistcode = "1111111111111";
//            Blacklistmsg = "A needed plugin isnt installed! PermissionsHandler or MoneyHandler";
//        }
        timetemp1 = (System.nanoTime() - timetemp1) / 1000000;

        Logger("Enabled in " + timetemp1 + "ms", "");
    }

    public void Standartstart(int run) {
        if (run == 2) {
            aktuelleVersion();
            upd = new Update(this);
            blacklistcheck();
            this.getServer().getScheduler().scheduleAsyncRepeatingTask(this, new Runnable() {

                @Override
                public void run() {
                    if (config.debug) {
                        Logger("checking Blacklist!", "");
                    }
                    blacklistcheck();
                    if (config.debug) {
                        if (Blacklistcode.equals("0000000000000")) {
                            Logger("Result: false", "");
                        } else {
                            Logger("Result: true", "");
                        }
                    }
                }
            }, 200L, 50000L);
            if (config.usedbtomanageXP) {
                this.getServer().getScheduler().scheduleAsyncRepeatingTask(this, new Runnable() {

                    @Override
                    public void run() {
                        long time = 0;
                        if (config.debug) {
                            Logger("Setting Player XP!", "");
                            time = System.nanoTime();
                        }
                        int XP = 0;
                        int neu = 0;
                        for (Player p : Bukkit.getServer().getOnlinePlayers()) {
                            XP = (int) xpShop.this.getTOTALXP(p);
                            try {
                                neu = xpShop.this.SQL.getXP(p.getName());




                            } catch (SQLException ex) {
                                java.util.logging.Logger.getLogger(xpShop.class.getName()).log(Level.SEVERE, null, ex);
                            }
                            if (config.debug) {
                                Logger("Player " + p.getName() + "saved in db with " + XP + " XP!", Prefix);
                            }
                            if (XP != neu) {
                                p.setLevel(0);
                                p.setExp(0);
                                xpShop.this.UpdateXP(p, neu, "AutoUpdate");
                                if (XP < neu) {
                                    xpShop.this.PlayerLogger(p, String.format(config.addedxp, neu - XP), "");
                                } else if (neu < XP) {
                                    xpShop.this.PlayerLogger(p, String.format(config.substractedxp, XP - neu), "");
                                }
                            }
                        }
                        if (config.debug) {
                            time = (System.nanoTime() - time) / 1000000;
                            Logger("Synced XP with DB in " + time + " ms!", "Debug");
                        }

                    }
                }, (long) (config.DelayTimeTask * 20), (long) (config.TaskRepeat) * 20);
                this.getServer().getScheduler().scheduleAsyncRepeatingTask(this, new Runnable() {

                    @Override
                    public void run() {
                        if (getConfig().getBoolean("debug")) {
                            Logger("Searching update for xpShop!", "");
                        }
                        float newversion = upd.getNewVersion("http://ibhh.de:80/aktuelleversionxpShop.html");
                        if (getConfig().getBoolean("debug")) {
                            Logger("installed xpShop version: " + Version + ", latest version: " + newversion, "");
                        }
                        if (newversion > Version) {
                            Logger("New version: " + newversion + " found!", "Warning");
                            Logger("******************************************", "Warning");
                            Logger("*********** Please update!!!! ************", "Warning");
                            Logger("* http://ibhh.de/xpShop.jar *", "Warning");
                            Logger("******************************************", "Warning");
                            xpShop.updateaviable = true;
//                        if (getConfig().getBoolean("autodownload") == true) {
//                            try {
//                                String path = "plugins" + File.separator;
//                                if (getConfig().getBoolean("autoinstall")) {
//                                    Logger("Started install!", "");
//                                    xpShopupdatehelper.this.getServer().broadcastMessage(ChatColor.DARK_BLUE + Prefix + ChatColor.RED + "Warning: " + ChatColor.GOLD + "Installing new xpShop version!");
//                                    xpShopupdatehelper.this.getServer().broadcastMessage(ChatColor.DARK_BLUE + Prefix + ChatColor.RED + "Warning: " + ChatColor.GOLD + "Expect some lags!");
//                                    xpShopupdatehelper.this.getPluginLoader().disablePlugin(xpShopplugin);
//                                    upd.autoDownload("http://ibhh.de/xpShop.jar", path, "xpShop.jar", "forceupdate");
//                                    xpShopupdatehelper.this.getPluginLoader().loadPlugin(new File("plugins" + File.separator + "xpShop.jar"));
//                                    xpShopupdatehelper.this.getPluginLoader().enablePlugin(xpShopplugin);
//                                    xpShopupdatehelper.this.getServer().broadcastMessage(ChatColor.DARK_BLUE + Prefix + ChatColor.RED + "Warning: " + ChatColor.GOLD + "Install done!");
//                                    Logger("Install done!", "");
//                                } else {
//                                    Logger("xpShop will be updated on the next restart!", "Warning");
//                                }
//                            } catch (Exception e) {
//                                Logger("Error on donwloading new Version!", "Error");
//                                e.printStackTrace();
//                            }
//                        } else {
//                            Logger("Please type [xpShop download] to download manual! ", "Warning");
//                        }
                        } else {
                            if (getConfig().getBoolean("debug")) {
                                Logger("No update found!", "");
                            }
                        }

                    }
                }, 400L, 50000L);
            }
        } else if (run == 1) {
            try {
                config = new ConfigHandler(this);
                config.loadConfigonStart();
            } catch (Exception e1) {
                Logger("Error on loading config: " + e1.getMessage(), Prefix);
                e1.printStackTrace();
                Logger("Version: " + Version + " failed to enable!", "Error");
                onDisable();
            }
        } else if (run == 3) {
            String URL = "http://ibhh.de:80/aktuelleversion" + this.getDescription().getName() + ".html";
            if ((UpdateAvailable(URL, Version) == true)) {
                Logger("New version: " + upd.getNewVersion(URL) + " found!", "Warning");
                Logger("******************************************", "Warning");
                Logger("*********** Please update!!!! ************", "Warning");
                Logger("* http://ibhh.de/xpShop.jar *", "Warning");
                Logger("******************************************", "Warning");
            }
        }

    }

    /**
     * Called by Bukkit on reloading the server
     *
     * @param
     * @return
     */
    public void onReload() {
        onDisable();
        onEnable();
    }

    /**
     * Called by Bukkit if player posts a command
     *
     * @param none, cause of Bukkit.
     * @return true if no errors happened else return false to Bukkit, then
     * Bukkit prints /xpShop buy <xp|money>.
     */
    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        if (!Blacklistcode.startsWith("1")) {
            if (sender instanceof Player) {
                Player player = (Player) sender;
                if (cmd.getName().equalsIgnoreCase("xpShop")) {
                    long temptime = 0;
                    if (config.debug) {
                        temptime = System.nanoTime();
                    }
                    switch (args.length) {
                        case 1:
                            ActionxpShop = args[0];
                            if (ActionxpShop.equalsIgnoreCase("infoxp")) {
                                if (PermissionsHandler.checkpermissions(player, "xpShop.infoxp.own")) {
                                    infoxp(sender, args);
                                    if (config.debug) {
                                        temptime = (System.nanoTime() - temptime) / 1000000;
                                        Logger("Command: " + cmd.getName() + " " + args.toString() + " executed in " + temptime + "ms", "");
                                    }
                                    return true;
                                } else {
                                    return false;
                                }
                            } else if (ActionxpShop.equalsIgnoreCase("version")) {
                                PlayerLogger(player, "Version: " + getDescription().getVersion(), "");
                                if (config.debug) {
                                    temptime = (System.nanoTime() - temptime) / 1000000;
                                    Logger("Command: " + cmd.getName() + " " + args.toString() + " executed in " + temptime + "ms", "");
                                }
                                return true;
                            } else if (ActionxpShop.equalsIgnoreCase("update")) {
                                if (PermissionsHandler.checkpermissions(player, "xpShop.admin")) {
                                    String path = "plugins" + File.separator;
                                    autoUpdate("http://ibhh.de/xpShop.jar", path, "xpShop.jar", "forceupdate");
                                    PlayerLogger(player, "Downloaded new Version!", "Warning");
                                    PlayerLogger(player, "xpShop will be updated on the next restart!", "Warning");
                                    return true;
                                }
                            } else if (ActionxpShop.equalsIgnoreCase("stats")) {
                                final Player pl = player;
                                this.getServer().getScheduler().scheduleAsyncDelayedTask(this, new Runnable() {

                                    @Override
                                    public void run() {
                                        try {
                                            Stats.getStats(pl);
                                        } catch (SQLException ex) {
                                            java.util.logging.Logger.getLogger(xpShop.class.getName()).log(Level.SEVERE, null, ex);
                                        }
                                    }
                                }, 1);

                                if (config.debug) {
                                    temptime = (System.nanoTime() - temptime) / 1000000;
                                    Logger("Command: " + cmd.getName() + " " + args.toString() + " executed in " + temptime + "ms", "");
                                }
                                return true;
                            } else if (ActionxpShop.equalsIgnoreCase("infolevel")) {
                                if (PermissionsHandler.checkpermissions(player, "xpShop.infolevel.own")) {
                                    infolevel(sender, args);
                                    if (config.debug) {
                                        temptime = (System.nanoTime() - temptime) / 1000000;
                                        Logger("Command: " + cmd.getName() + " " + args.toString() + " executed in " + temptime + "ms", "");
                                    }
                                    return true;
                                } else {
                                    return false;
                                }
                            } else {
                                Help.help(sender, args);
                            }
                            break;
                        case 2:
                            ActionxpShop = args[0];
                            if (ActionxpShop.equals("selllevel")) {
                                if (PermissionsHandler.checkpermissions(player, "xpShop.selllevel")) {
                                    if (Tools.isInteger(args[1])) {
                                        selllevel = Integer.parseInt(args[1]);
                                        selllevel(player, this.selllevel, true);
                                        if (config.debug) {
                                            temptime = (System.nanoTime() - temptime) / 1000000;
                                            Logger("Command: " + cmd.getName() + " " + args.toString() + " executed in " + temptime + "ms", "");
                                        }
                                        return true;
                                    }
                                    PlayerLogger(player, config.commanderrornoint, "Error");
                                    return false;
                                }
                            } else if (ActionxpShop.equalsIgnoreCase("testsetxp")) {
                                player.setTotalExperience(Integer.parseInt(args[1]));
                                PlayerLogger(player, "This are: " + getTOTALXP(player), "");
                                if (config.debug) {
                                    temptime = (System.nanoTime() - temptime) / 1000000;
                                    Logger("Command: " + cmd.getName() + " " + args.toString() + " executed in " + temptime + "ms", "");
                                }
                                return true;
                            } else if (ActionxpShop.equals("buylevel")) {
                                if (PermissionsHandler.checkpermissions(player, "xpShop.buylevel")) {
                                    if (Tools.isInteger(args[1])) {
                                        buylevel = Integer.parseInt(args[1]);
                                        buylevel(player, this.buylevel, true);
                                        if (config.debug) {
                                            temptime = (System.nanoTime() - temptime) / 1000000;
                                            Logger("Command: " + cmd.getName() + " " + args.toString() + " executed in " + temptime + "ms", "");
                                        }
                                        return true;
                                    }
                                    PlayerLogger(player, config.commanderrornoint, "Error");
                                    return false;
                                }
                            } else if (ActionxpShop.equals("sell")) {
                                if (PermissionsHandler.checkpermissions(player, "xpShop.sell")) {
                                    if (Tools.isInteger(args[1])) {
                                        sell = Integer.parseInt(args[1]);
                                        sell(player, this.sell, true, "sell");
                                        if (config.debug) {
                                            temptime = (System.nanoTime() - temptime) / 1000000;
                                            Logger("Command: " + cmd.getName() + " " + args.toString() + " executed in " + temptime + "ms", "");
                                        }
                                        return true;
                                    }
                                    PlayerLogger(player, config.commanderrornoint, "Error");
                                    return false;
                                }
                            } else if (ActionxpShop.equals("buy")) {
                                if (PermissionsHandler.checkpermissions(player, "xpShop.buy")) {
                                    if (Tools.isInteger(args[1])) {
                                        buy = Integer.parseInt(args[1]);
                                        buy(player, this.buy, true, "buy");
                                        if (config.debug) {
                                            temptime = (System.nanoTime() - temptime) / 1000000;
                                            Logger("Command: " + cmd.getName() + " " + args.toString() + " executed in " + temptime + "ms", "");
                                        }
                                        return true;
                                    }
                                    return false;
                                }
                            } else if (ActionxpShop.equalsIgnoreCase("infoxp")) {
                                if (PermissionsHandler.checkpermissions(player, "xpShop.infoxp.other")) {
                                    if (!Tools.isInteger(args[1])) {
                                        infoxp(sender, args);
                                        if (config.debug) {
                                            temptime = (System.nanoTime() - temptime) / 1000000;
                                            Logger("Command: " + cmd.getName() + " " + args.toString() + " executed in " + temptime + "ms", "");
                                        }
                                        return true;
                                    }
                                    PlayerLogger(player, config.commanderrornoint, "Error");
                                    return false;
                                }
                            } else if (ActionxpShop.equalsIgnoreCase("infolevel")) {
                                if (PermissionsHandler.checkpermissions(player, "xpShop.infolevel.other")) {
                                    if (!Tools.isInteger(args[1])) {
                                        infolevel(sender, args);
                                        if (config.debug) {
                                            temptime = (System.nanoTime() - temptime) / 1000000;
                                            Logger("Command: " + cmd.getName() + " " + args.toString() + " executed in " + temptime + "ms", "");
                                        }
                                        return true;
                                    }
                                    PlayerLogger(player, config.commanderrornoint, "Error");
                                    return false;
                                }
                            } else if (ActionxpShop.equalsIgnoreCase("help")) {
                                if (PermissionsHandler.checkpermissions(player, "xpShop.help")) {
                                    if (!Tools.isInteger(args[1])) {
                                        Help.help(player, args);
                                        if (config.debug) {
                                            temptime = (System.nanoTime() - temptime) / 1000000;
                                            Logger("Command: " + cmd.getName() + " " + args.toString() + " executed in " + temptime + "ms", "");
                                        }
                                        return true;
                                    }
                                    PlayerLogger(player, config.commanderrornoint, "Error");
                                    return false;
                                }
                            } else if (ActionxpShop.equalsIgnoreCase("resetplayer")) {
                                if (config.usedbtomanageXP) {
                                    if (PermissionsHandler.checkpermissions(player, "xpShop.resetplayer")) {
                                        SQL.UpdateXP(args[1], 0);
                                        PlayerLogger(player, String.format(config.Playerreset, args[1]), "");
                                        return true;
                                    }
                                } else {
                                    PlayerLogger(player, config.dbnotused, "");
                                    return false;
                                }
                            } else {
                                Help.help(sender, args);
                            }
                            break;
                        case 3:
                            ActionxpShop = args[0];
                            if (ActionxpShop.equalsIgnoreCase("info")) {
                                if (PermissionsHandler.checkpermissions(player, "xpShop.info")) {
                                    if ((!Tools.isInteger(args[1])) && (Tools.isInteger(args[2]))) {
                                        info(player, args);
                                        if (config.debug) {
                                            temptime = (System.nanoTime() - temptime) / 1000000;
                                            Logger("Command: " + cmd.getName() + " " + args.toString() + " executed in " + temptime + "ms", "");
                                        }
                                        return true;
                                    }
                                    PlayerLogger(player, config.commanderrornoint, "Error");
                                    return false;
                                }
                            } else if (ActionxpShop.equalsIgnoreCase("send")) {
                                if (PermissionsHandler.checkpermissions(player, "xpShop.send")) {
                                    if ((!Tools.isInteger(args[1])) && (Tools.isInteger(args[2]))) {
                                        int xp = Integer.parseInt(args[2]);
                                        sendxp(sender, xp, args[1], args);
                                        if (config.debug) {
                                            temptime = (System.nanoTime() - temptime) / 1000000;
                                            Logger("Command: " + cmd.getName() + " " + args.toString() + " executed in " + temptime + "ms", "");
                                        }
                                        return true;
                                    }
                                    PlayerLogger(player, config.commanderrornoint, "Error");
                                    return false;
                                }
                            } else if (ActionxpShop.equalsIgnoreCase("setXP")) {
                                if (config.usedbtomanageXP) {
                                    if (PermissionsHandler.checkpermissions(player, "xpShop.setxp")) {
                                        if (Tools.isInteger(args[2])) {
                                            SQL.UpdateXP(args[1], Integer.parseInt(args[2]));
                                            PlayerLogger(player, String.format(config.Playerxpset, args[1], Integer.parseInt(args[2])), "");
                                            return true;
                                        }
                                    }
                                } else {
                                    PlayerLogger(player, config.dbnotused, "");
                                }
                                return false;
                            } else {
                                Help.help(sender, args);
                            }
                            break;




                        default:
                            Help.help(player, args);
                            return false;
                    }
                }
            } else if (cmd.getName().equalsIgnoreCase("xpShop")) {
                if (args.length == 1) {
                    if (args[0].equalsIgnoreCase("download")) {
                        String path = "plugins" + File.separator;
                        autoUpdate("http://ibhh.de/xpShop.jar", path, "xpShop.jar", "forceupdate");
                        Logger("Downloaded new Version!", "Warning");
                        Logger("xpShop will be updated on the next restart!", "Warning");
                        return true;
                    } else if (args[0].equalsIgnoreCase("gui")) {
                        openGUI();
                        return true;
                    } else if (args[0].equalsIgnoreCase("reload")) {
                        onReload();
                        return true;
                    } else if (args[0].equalsIgnoreCase("debug")) {
                        getConfig().set("debug", !getConfig().getBoolean("debug"));
                        Logger("debug set to: " + getConfig().getBoolean("debug"), "");
                        saveConfig();
                        if (config.debug) {
                            Logger("Config saved!", "Debug");
                        }
                        reloadConfig();
                        if (config.debug) {
                            Logger("Config reloaded!", "Debug");
                        }
                        if (config.debug) {
                            Logger("debug reloaded!", "Debug");
                        }
                        config.reload();
                        if (config.debug) {
                            Logger("Config reloaded!", "Debug");
                        }
                        return true;
                    } else if (args[0].equalsIgnoreCase("autodownload")) {
                        getConfig().set("autodownload", !getConfig().getBoolean("autodownload"));
                        Logger("autodownload set to: " + getConfig().getBoolean("autodownload"), "");
                        saveConfig();
                        if (config.debug) {
                            Logger("Config saved!", "Debug");
                        }
                        reloadConfig();
                        if (config.debug) {
                            Logger("Config reloaded!", "Debug");
                        }
                        if (config.debug) {
                            Logger("debug reloaded!", "Debug");
                        }
                        config.reload();
                        if (config.debug) {
                            Logger("Config reloaded!", "Debug");
                        }
                        return true;
                    } else if (args[0].equalsIgnoreCase("firstRun")) {
                        getConfig().set("firstRun", !getConfig().getBoolean("firstRun"));
                        Logger("firstRun set to: " + getConfig().getBoolean("firstRun"), "");
                        saveConfig();
                        if (config.debug) {
                            Logger("Config saved!", "Debug");
                        }
                        reloadConfig();
                        if (config.debug) {
                            Logger("Config reloaded!", "Debug");
                        }
                        if (config.debug) {
                            Logger("debug reloaded!", "Debug");
                        }
                        config.reload();
                        if (config.debug) {
                            Logger("Config reloaded!", "Debug");
                        }
                        return true;
                    } else if (args[0].equalsIgnoreCase("install")) {
                        if (UpdateAvailable(versionsfile, Version)) {
                            Logger("Started install!", "");
                            xpShop.this.getServer().broadcastMessage(ChatColor.DARK_BLUE + Prefix + ChatColor.RED + "Warning: " + ChatColor.GOLD + "Installing new xpShop version!");
                            xpShop.this.getServer().broadcastMessage(ChatColor.DARK_BLUE + Prefix + ChatColor.RED + "Warning: " + ChatColor.GOLD + "Expect some lags!");
                            xpShop.this.getServer().reload();
                            xpShop.this.getServer().broadcastMessage(ChatColor.DARK_BLUE + Prefix + ChatColor.RED + "Warning: " + ChatColor.GOLD + "Install done!");
                            Logger("Install done!", "");
                        } else {
                            Logger("No new version found!", "");
                        }
                        return true;
                    }
                }
            }

            return false;
        } else {
            blacklistLogger(sender);
            return true;
        }
    }

    public void blacklistLogger(Player sender) {
        if (sender instanceof Player && sender != null) {
            Player player = (Player) sender;
            PlayerLogger(player, "this xpShop version is blacklisted because of bugs, after restart an bugfix will be installed!", "Warning");
            PlayerLogger(player, "Some funktions deactivated to prevent the server!", "Warning");
            PlayerLogger(player, "Reason: " + Blacklistmsg, "Warning");
        } else {
            Logger("this xpShop version is blacklisted because of bugs, after restart an bugfix will be installed!", "Warning");
            Logger("Some funktions deactivated to prevent the server!", "Warning");
            Logger("Reason: " + Blacklistmsg, "Warning");
        }
    }

    public void blacklistLogger(CommandSender sender) {
        if (sender instanceof Player && sender != null) {
            Player player = (Player) sender;
            PlayerLogger(player, "This xpShop version is blacklisted because of bugs, after restart an bugfix will be installed!", "Warning");
            PlayerLogger(player, "Some funktions deactivated to prevent the server!", "Warning");
            PlayerLogger(player, "Reason: " + Blacklistmsg, "Warning");
        } else {
            Logger("This xpShop version is blacklisted because of bugs, after restart an bugfix will be installed!", "Warning");
            Logger("Some funktions deactivated to prevent the server!", "Warning");
            Logger("Reason: " + Blacklistmsg, "Warning");
        }
    }

    public void Logger(String msg, String TYPE) {
        if (TYPE.equalsIgnoreCase("Warning") || TYPE.equalsIgnoreCase("Error")) {
            System.err.println(PrefixConsole + TYPE + ": " + msg);
        } else if (TYPE.equalsIgnoreCase("Debug")) {
            System.out.println(PrefixConsole + "Debug: " + msg);
        } else {
            System.out.println(PrefixConsole + msg);
        }
    }

    public void PlayerLogger(Player p, String msg, String TYPE) {
        if (TYPE.equalsIgnoreCase("Error")) {
            p.sendMessage(ChatColor.DARK_BLUE + Prefix + ChatColor.RED + "Error: " + ChatColor.GOLD + msg);
        } else {
            p.sendMessage(ChatColor.DARK_BLUE + Prefix + ChatColor.GOLD + msg);
        }
    }

//    protected Player getPlayer(CommandSender sender, String[] args, int index) {
////        if (args.length > index) {
////            OfflinePlayer players = sender.getServer().getOfflinePlayer(args[index]);
////            return (Player) players.getPlayer();
////        }
//        Player newplayer = null;
////        MinecraftServer server = ((CraftServer)this.getServer()).getServer();
////        EntityPlayer entity = new EntityPlayer(server, server.getWorldServer(0), args[index], new ItemInWorldManager(server.getWorldServer(0)));
////        newplayer = entity == null ? null : (Player)entity.getBukkitEntity();
//        if (args[index] != null) {
//            Boolean exist = false;
//            for (OfflinePlayer p : sender.getServer().getOfflinePlayers()) {
//                if (p.getName().toLowerCase().equals(args[index].toLowerCase())) {
//                    exist = true;
//                    newplayer = (Player) p;
//                    break;
//                }
//            }
//            if (!exist) {
//                return null;
//            }
//        }
//        if(!(sender instanceof Player))
//            return null;
//        return (Player) newplayer;
//    }
    public void sendxp(CommandSender sender, int giveamount, String empfaenger, String[] args) {
        Player player = (Player) sender;
        if (!(Blacklistcode.startsWith("1", 3))) {
            Player empfaenger1 = null;
            long time = 0;
            if (config.debug) {
                time = System.nanoTime();
            }
//            empfaenger1 = getServer().getPlayer(empfaenger);
//            if (empfaenger1 != null) {
//                sell(sender, giveamount, false, "sendxp"); //Trys to substract amount, else stop.
//                try {
//                    buy(empfaenger1, SubstractedXP, false, "sendxp"); //Gives other player XP wich were substracted.
//                    empfaenger1.saveData();
//                } catch (Exception e1) {
//                    buy(player, giveamount, false, "sendxp");
//                    PlayerLogger(player, args[1] + " " + config.playerwasntonline, "Error");
//                    return;
//                }
//                try {
//                    PlayerLogger(player, (String.format(config.commandsuccesssentxp, SubstractedXP, args[1])), "");
//                    PlayerLogger(empfaenger1, (String.format(config.commandsuccessrecievedxp, SubstractedXP, sender.getName())), "");
//                } catch (NullPointerException e) {
//                    PlayerLogger(player, "Error!", "Error");
//                }
//            }
//            if (empfaenger1 == null) {
//                if (config.getPlayerConfig(empfaenger1, player)) {
//                    empfaenger1 = getmyOfflinePlayer(args, 1);
//                    sell(sender, giveamount, false, "sendxp"); //Trys to substract amount, else stop.
//                    try {
//                        buy(empfaenger1, SubstractedXP, false, "sendxp"); //Gives other player XP wich were substracted.
//                        empfaenger1.saveData();
//                    } catch (Exception e1) {
//                        buy(player, giveamount, false, "sendxp");
//                        PlayerLogger(player, args[1] + " " + config.playerwasntonline, "Error");
//                        return;
//                    }
//                    try {
//                        PlayerLogger(player, (String.format(config.commandsuccesssentxp, SubstractedXP, args[1])), "");
//                        PlayerLogger(empfaenger1, (String.format(config.commandsuccessrecievedxp, SubstractedXP, sender.getName())), "");
//                    } catch (NullPointerException e) {
//                        PlayerLogger(player, "Error!", "Error");
//                    }
//                }
//            }
            if (config.usedbtomanageXP) {
                try {
                    if (SQL.isindb(empfaenger)) {
                        String playername = player.getName();
                        int XPempf = SQL.getXP(empfaenger);
                        int XPplayer = SQL.getXP(playername);
                        sell(sender, giveamount, false, "sendxp"); //Trys to substract amount, else stop.
                        SQL.UpdateXP(empfaenger, XPempf + SubstractedXP);
                        SQL.UpdateXP(playername, (XPplayer - SubstractedXP));
                        try {
                            PlayerLogger(player, (String.format(config.commandsuccesssentxp, SubstractedXP, args[1])), "");
                            if (getServer().getPlayer(empfaenger) != null) {
                                empfaenger1 = getServer().getPlayer(empfaenger);
                                buy(empfaenger1, SubstractedXP, false, "sendxp");
                                PlayerLogger(empfaenger1, (String.format(config.commandsuccessrecievedxp, SubstractedXP, playername)), "");
                            }
                        } catch (NullPointerException e) {
                            PlayerLogger(player, "Error!", "Error");
                        }
                    } else {
                        PlayerLogger(player, args[1] + " " + config.playerwasntonline, "Error");
                    }
                } catch (SQLException ew) {
                    PlayerLogger(player, args[1] + " " + config.playerwasntonline, "Error");
                }
            } else {
                try {
                    empfaenger1 = getmyOfflinePlayer(args, 1);
                } catch (Exception e1) {
                    PlayerLogger(player, args[1] + " " + config.playerwasntonline, "Error");
                    return;
                }
                if (empfaenger1 != null) {
                    if (empfaenger1.hasPlayedBefore()) {
                        if (config.getPlayerConfig(empfaenger1, player)) {
                            sell(sender, giveamount, false, "sendxp"); //Trys to substract amount, else stop.
                            try {
                                buy(empfaenger1, SubstractedXP, false, "sendxp"); //Gives other player XP wich were substracted.
                                empfaenger1.saveData();
                            } catch (Exception e1) {
                                buy(player, giveamount, false, "sendxp");
                                PlayerLogger(player, args[1] + " " + config.playerwasntonline, "Error");
                                return;
                            }
                            try {
                                PlayerLogger(player, (String.format(config.commandsuccesssentxp, SubstractedXP, args[1])), "");
                                PlayerLogger(empfaenger1, (String.format(config.commandsuccessrecievedxp, SubstractedXP, sender.getName())), "");
                            } catch (NullPointerException e) {
                                PlayerLogger(player, "Error!", "Error");
                            }
                        }
                    } else {
                        PlayerLogger(player, args[1] + " " + config.playerwasntonline, "Error");
                    }
                } else {
                    PlayerLogger(player, args[1] + " " + config.playerwasntonline, "Error");
                }
            }

            if (config.debug) {
                time = (System.nanoTime() - time) / 1000000;
                Logger("Send xp executed in " + time + " ms!", "Debug");
            }
        } else {
            blacklistLogger(player);
        }
    }

    public double getLevelXP(int level) {
        return 3.5 * level * (level + 1);
    }

    public double getTOTALXP(Player player) {
        int level = player.getLevel();
        float playerExpp = player.getExp();
        int XPinLevel = (int) (((level + 1) * 7) * playerExpp);
        double Exp1 = (3.5 * level * (level + 1)) + XPinLevel;
        return Exp1;

    }

    public void UpdateXP(CommandSender sender, int amount, String von) {
        Player player = (Player) sender;
        double Expaktuell = getTOTALXP(player) + amount;
        double neuesLevel;
        int neuesLevelx;
        double neueXpp;
        try {
            if (Expaktuell >= 0) {
                neuesLevel = (Math.pow((Expaktuell / 3.5 + 0.25), 0.5) - 0.5);
                neuesLevelx = (int) neuesLevel;
                neueXpp = (neuesLevel - neuesLevelx);
                player.setLevel(neuesLevelx);
                player.setExp((float) neueXpp);
            } else {
                PlayerLogger(player, "Invalid exp count: " + amount, "Error");
            }
        } catch (NumberFormatException ex) {
            PlayerLogger(player, "Invalid exp count: " + amount, "Error");
        }
    }

    public void infolevel(CommandSender sender, String[] args) {
        Player player = (Player) sender;
        if (!Blacklistcode.startsWith("1", 9)) {
            if (args.length == 1) {
                PlayerLogger(player, String.format(config.infoownLevel, player.getLevel()), "");
            } else if (args.length == 2) {
                Player empfaenger1;
                try {
                    try {
                        empfaenger1 = getmyOfflinePlayer(args, 1);
                    } catch (Exception e1) {
                        PlayerLogger(player, args[1] + " " + config.playerwasntonline, "Error");
                        return;
                    }
                    if (empfaenger1 != null) {
                        if (empfaenger1.hasPlayedBefore()) {
                            PlayerLogger(player, String.format(config.infootherLevel, empfaenger1.getName(), empfaenger1.getLevel()), "");
                        } else {
                            PlayerLogger(player, args[1] + " " + config.playerwasntonline, "Error");
                        }
                    } else {
                        PlayerLogger(player, args[1] + " " + config.playerwasntonline, "Error");
                    }
                } catch (Exception e) {
                    PlayerLogger(player, args[1] + " " + config.playerwasntonline, "Error");
                }
            }
        } else {
            blacklistLogger(player);
        }
    }

    public void infoxp(CommandSender sender, String[] args) {
        Player player = (Player) sender;
        if (!Blacklistcode.startsWith("1", 8)) {
            if (args.length == 1) {
                PlayerLogger(player, String.format(config.infoownXP, (int) getTOTALXP(player)), "");
            } else if (args.length == 2) {
                if (config.usedbtomanageXP) {
                    try {
                        if (SQL.isindb(args[1])) {
                            PlayerLogger(player, String.format(config.infootherXP, args[1].toString(), SQL.getXP(args[1])), "");
                        } else {
                            PlayerLogger(player, args[1] + " " + config.playerwasntonline, "Error");
                        }
                    } catch (SQLException ew) {
                        PlayerLogger(player, args[1] + " " + config.playerwasntonline, "Error");
                    }
                } else {
                    Player empfaenger1;
                    try {
                        try {
                            empfaenger1 = getmyOfflinePlayer(args, 1);
                        } catch (Exception e1) {
                            if (config.debug) {
                                e1.printStackTrace();
                                e1.getMessage();
                            }
                            PlayerLogger(player, args[1] + " " + config.playerwasntonline, "Error");
                            return;
                        }
                        if (empfaenger1 != null) {
                            if (empfaenger1.hasPlayedBefore()) {
                                PlayerLogger(player, String.format(config.infootherXP, empfaenger1.getName(), (int) getTOTALXP(empfaenger1)), "");
                            }
                        } else {
                            PlayerLogger(player, args[1] + " " + config.playerwasntonline, "Error");
                            if (config.debug) {
                                Logger("Player == null", "Debug");
                            }
                        }
                    } catch (Exception e) {
                        if (config.debug) {
                            e.printStackTrace();
                            e.getMessage();
                        }
                        PlayerLogger(player, args[1] + " " + config.playerwasntonline, "Error");
                    }
                }
            }
        } else {
            blacklistLogger(player);
        }
    }

    /**
     * Called by onCommand and buylevel, buys XP.
     *
     * @param sender, amount, moneyactive = true if you want that player have to
     * buy XP, false if there is an info what that would cost.
     * @return true if no error occurred.
     */
    public boolean buy(CommandSender sender, int buyamount, boolean moneyactive, String von) {
        Player player = (Player) sender;
        if (!Blacklistcode.startsWith("1", 1)) {
            double TOTALXPDOUBLE = (buyamount * config.moneytoxp);

            if (buyamount <= 0) {
                if (!von.equals("sendxp")) {
                    PlayerLogger(player, "Invalid Amount!", "Error");
                }
                return false;
            }
            boolean valid;
            valid = false;
            if (moneyactive) {
                if (MoneyHandler.getBalance(player) >= TOTALXPDOUBLE) {
                    valid = true;
                } else {
                    PlayerLogger(player, config.commanderrornotenoughmoney, "Error");
                }
            } else if (von.equals("sendxp")) {
                valid = true;
            }
            if (valid) {
                if (buyamount > 0) {
                    if (config.usedbtomanageXP) {
                        SQL.UpdateXP(player.getName(), ((int) getTOTALXP(player) + buyamount));
                    }
                    UpdateXP(sender, buyamount, "buy");
                    if (moneyactive) {
                        MoneyHandler.substract(TOTALXPDOUBLE, player);
                    }
                } else {
                    if (!von.equals("buylevel")) {
                        PlayerLogger(player, "Invalid exp count: " + buyamount, "Error");
                        PlayerLogger(player, String.format(config.commanderrorinfo, MoneyHandler.getBalance(player), (int) (MoneyHandler.getBalance(player) / getmoney)), "Error");
                    }
                }
                if (ActionxpShop.equalsIgnoreCase("buy")) {
                    PlayerLogger(player, String.format(config.commandsuccessbuy, (int) TOTALXPDOUBLE, (int) buyamount), "");

                } else if (ActionxpShop.equalsIgnoreCase("info") && von.equals("buylevel") == false) {
                    PlayerLogger(player, String.format(config.infoPrefix + " " + config.commandsuccessbuy, (int) TOTALXPDOUBLE, (int) buyamount), "");
                }
                player.saveData();
                return true;
            }
            return false;
        } else {
            blacklistLogger(player);
        }
        return true;
    }

    public int sell(CommandSender sender, int sellamount, boolean moneyactive, String von) {

        Player player = (Player) sender;
        if (!Blacklistcode.startsWith("1", 2)) {
            if (sellamount <= 0) {
                if (!von.equals("sendxp")) {
                    PlayerLogger(player, "Invalid Amount!", "Error");
                }
                return 0;
            }
            try {
                SubstractedXP = 0;
                double TOTAL = getTOTALXP(player);
                int TOTALint = (int) TOTAL;
                getmoney = config.xptomoney;
                if (sellamount <= TOTAL) {
                    if (config.usedbtomanageXP) {
                        SQL.UpdateXP(player.getName(), ((int) TOTALint - sellamount));
                    }
                    UpdateXP(sender, -sellamount, "sell");
                    if (moneyactive) {
                        MoneyHandler.addmoney(sellamount * getmoney, player);
                    }
                    SubstractedXP = sellamount;
                } else {
                    PlayerLogger(player, "Invalid exp count: " + sellamount, "Error");
                    PlayerLogger(player, config.commanderrornotenoughxp, "Error");
                    PlayerLogger(player, String.format(config.commanderrorinfo, TOTALint, (int) (TOTAL * getmoney)), "Error");
                    return 0;
                }
            } catch (NumberFormatException ex) {
                PlayerLogger(player, "Invalid exp count: " + sellamount, "Error");
                return 0;
            }
            player.saveData();
            if (ActionxpShop.equalsIgnoreCase("sell")) {
                PlayerLogger(player, String.format(config.commandsuccesssell, SubstractedXP, (int) (sellamount * getmoney)), "");
            } else if (ActionxpShop.equalsIgnoreCase("info") && von.equals("selllevel") == false) {
                PlayerLogger(player, String.format(config.infoPrefix + " " + config.commandsuccesssell, SubstractedXP, (int) (sellamount * getmoney)), "");
            }
        } else {
            blacklistLogger(player);
        }
        return SubstractedXP;
    }

    /**
     * Buys level for a player.
     *
     * @param sender, amount, moneyactive = true if you want that player have to
     * buy XP, false if there is an info what that would cost.
     * @return
     */
    public void buylevel(CommandSender sender, int levelamontbuy, boolean moneyactive) {
        Player player = (Player) sender;
        if (!Blacklistcode.startsWith("1", 4)) {
            int level = player.getLevel();
            if (levelamontbuy <= 0) {
                PlayerLogger(player, "Invalid Amount!", "Error");
                return;
            }
            double money1 = config.moneytoxp;
            double xpNeededForLevel = getLevelXP(levelamontbuy + level);
            double xpAktuell = getTOTALXP(player);
            double neededXP = xpNeededForLevel - xpAktuell;
            if (MoneyHandler.getBalance(player) < (money1 * neededXP)) {
                PlayerLogger(player, "Stopped because of not having enough money!", "Error");
                PlayerLogger(player, "Invalid exp count: " + levelamontbuy, "Error");
            } else {
                if (moneyactive) {
                    buy(sender, (int) (neededXP), true, "buylevel");
                }
            }
            if (ActionxpShop.equalsIgnoreCase("buylevel")) {
                if (config.debug) {
                    Logger("String: " + config.commandsuccessbuylevel + "moneytoxp: " + config.moneytoxp + "needed xp: " + neededXP, "Debug");
                }
                PlayerLogger(player, String.format(config.commandsuccessbuylevel, (int) (config.moneytoxp * neededXP), ((int) neededXP)), "");
            } else if (ActionxpShop.equalsIgnoreCase("info")) {
                PlayerLogger(player, String.format(config.infoPrefix + " " + config.commandsuccessbuylevel, (int) (config.moneytoxp * neededXP), (int) neededXP), "");
            }
        } else {
            blacklistLogger(player);
        }
    }

    /**
     * Sells level from a player.
     *
     * @param sender, amount, moneyactive = true if you want that player have to
     * buy XP, false if there is an info what that would cost.
     * @return
     */
    public void selllevel(CommandSender sender, int levelamountsell, boolean moneyactive) {
        Player player = (Player) sender;
        if (!Blacklistcode.startsWith("1", 5)) {
            if (levelamountsell <= 0) {
                PlayerLogger(player, "Invalid Amount!", "Error");
                return;
            }
            if (player.getLevel() + player.getExp() <= 0.20) {
                PlayerLogger(player, config.commanderrornotenoughxp, "Error");
            } else {
                int level = player.getLevel();
                double money1 = config.moneytoxp;
                double xpNeededForLevel = getLevelXP(level - levelamountsell);
                double xpAktuell = getTOTALXP(player);
                double XP2Sell = xpAktuell - xpNeededForLevel;
                if (XP2Sell >= 0) {
                    if (moneyactive) {
                        sell(sender, (int) XP2Sell, true, "selllevel");
                    }
                } else {
                    PlayerLogger(player, "Invalid exp count: " + levelamountsell, "Error");
                    return;
                }
                if (ActionxpShop.equalsIgnoreCase("selllevel")) {
                    PlayerLogger(player, String.format(config.commandsuccessselllevel, (int) XP2Sell, (int) (XP2Sell * money1)), "");
                } else if (ActionxpShop.equalsIgnoreCase("info")) {
                    PlayerLogger(player, String.format(config.infoPrefix + " " + config.commandsuccessselllevel, (int) XP2Sell, (int) (XP2Sell * money1)), "");
                }
            }
        } else {
            blacklistLogger(player);
        }
    }

    /**
     * Shows a player how much a action would cost.
     *
     * @param sender, action, amount.
     * @return
     */
    public void info(CommandSender sender, String[] args) {
        if (!Blacklistcode.startsWith("1", 6)) {
            if (args.length == 3 && sender instanceof Player) {
                Player player = (Player) sender;
                int nowlevel = player.getLevel();
                float nowxp = player.getExp();
                int temp = Integer.parseInt(args[2]);
                if (args[1].equals("buy")) {
                    buy(player, temp, false, "info");
                } else if (args[1].equals("sell")) {
                    sell(player, temp, false, "info");
                } else if (args[1].equals("buylevel")) {
                    buylevel(player, temp, false);
                } else if (args[1].equals("selllevel")) {
                    selllevel(player, temp, false);
                } else if (args[1].equals("send")) {
                    PlayerLogger(player, "There is no info for (send)!", "Error");
                } else {
                    PlayerLogger(player, "Command not found!", "Error");
                }
                player.setLevel(nowlevel);
                player.setExp(nowxp);
            } else {
                Help.help(sender, args);
            }
        } else {
            blacklistLogger(sender);
        }
    }
}
