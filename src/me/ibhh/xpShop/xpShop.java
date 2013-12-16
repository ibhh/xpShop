/*
 * Copyright 2012 ibhh. All rights reserved.
 * 
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 * 
 * 1. Redistributions of source code must retain the above copyright notice,
 * this list of conditions and the following disclaimer.
 * 
 * 2. Redistributions in binary form must reproduce the above copyright notice,
 * this list of conditions and the following disclaimer in the documentation
 * and/or other materials provided with the distribution.
 * 
 * THIS SOFTWARE IS PROVIDED BY THE AUTHOR ''AS IS'' AND ANY EXPRESS OR IMPLIED
 * WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES OF
 * MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE DISCLAIMED. IN NO
 * EVENT SHALL THE AUTHOR OR CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT,
 * INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT
 * LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA,
 * OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF
 * LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING
 * NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE,
 * EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 * 
 * The views and conclusions contained in the software and documentation are
 * those of the authors and contributors and should not be interpreted as
 * representing official policies, either expressed or implied, of anybody else.
 */
package me.ibhh.xpShop;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.logging.Level;

import me.ibhh.UpdaterLib.Updater;
import me.ibhh.UpdaterLib.Updater.UpdateResult;
import me.ibhh.UpdaterLib.Updater.UpdateType;
import me.ibhh.xpShop.Exceptions.InvalidXPAmountException;
import me.ibhh.xpShop.Exceptions.NoiConomyPluginFound;
import me.ibhh.xpShop.Exceptions.PlayerNotOnlineException;
import me.ibhh.xpShop.Exceptions.PlayerWasNeverOnlineException;
import me.ibhh.xpShop.Tools.Tools;
import me.ibhh.xpShop.send.sql.SendDatabase;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
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
    public float newversion = 0;
    int rounds1 = 0;
    int rounds = 0;
    public Utilities plugman;
    private Help Help;
    public static String PrefixConsole = "[xpShop] ";
    public static String Prefix = "[xpShop] ";
    private PanelControl panel;
    public ConfigHandler config;
    public ReportToHost report;
    public xpShopListener ListenerShop;
    public xpShop xpShop = this;
    public String Blacklistcode = "0000000000000";
    public String Blacklistmsg;
    public static boolean updateaviable = false;
    public PermissionsChecker PermissionsHandler;
    public iConomyHandler MoneyHandler;
    public BottleManager bottle;
    public Logger Loggerclass;
    public boolean toggle = true;
    public MetricsHandler metricshandler;
    public PlayerManager playerManager;
    private SendDatabase sendDatabase;
    public Repair repair;
    private HashMap<Player, Player> requested = new HashMap<Player, Player>();
    public HashMap<Player, Boolean> commandexec = new HashMap<Player, Boolean>();
    public HashMap<String, Boolean> DebugMsg = new HashMap<String, Boolean>();
    private HashMap<Player, String> Config = new HashMap<Player, String>();
    private HashMap<Player, String> Set = new HashMap<Player, String>();
    public String[] commands = { "help", "bottle", "bottleconfirm", "bottlecancel", "buy", "sell", "buylevel", "selllevel", "info", "send", "infoxpown", "infoxpother", "infolevelown",
	    "infolevelother", "toolinfo", "repair", "grand", "showdebug", "debugfile", "version", "update", "deletedebug", "log", "toggle", "deletetable", "language", "resetplayer", "setXP", "tpto",
	    "tpme", "yestp", "notp", "accept", "deny", "repaircancel", "repairconfirm" };
    public TeleportManager TP;

    public ReportToHost getReportHandler() {
	if (report == null) {
	    report = new ReportToHost(this);
	}
	return report;
    }

    public static String getRawBukkitVersion() {
	String[] a = Bukkit.getServer().getBukkitVersion().split("-");
	return a[0];
    }

    public xpShop() {
    }

    /**
     * Called by Bukkit on stopping the server
     */
    @Override
    public void onDisable() {
	toggle = true;
	long timetemp = System.currentTimeMillis();
//	if (config != null) {
//	    if (config.Internet) {
//		UpdateAvailable(Version);
//	    }
//	}
	if (metricshandler != null) {
	    metricshandler.saveStatsFiles();
	}
	timetemp = System.currentTimeMillis() - timetemp;
	Logger("disabled in " + timetemp + " ms", "");
    }

    /**
     * Called by Bukkit on starting the server
     * 
     */
    @Override
    public void onEnable() {
	long timetemp1 = System.nanoTime();
	Loggerclass = new Logger(this);

	Exception ex1 = null;
	try {
	    config = new ConfigHandler(this);
	    config.loadConfigonStart();
	    Logger("Version: " + aktuelleVersion(), "Debug");
	    if (getConfig().getString("teleport.tpconfirm.de").equalsIgnoreCase("Bitte tippe /xpShop yes zum betaetigen.")) {
		Logger("Version == 8.1", "Debug");
		getConfig().set("teleport.tpconfirm.de", "Bitte tippe /xpShop yestp zum betaetigen.");
		getConfig().set("teleport.tpconfirm.en", "Please execute the command /xpShop yestp to confirm the teleport.");
		getConfig().set("teleport.tpconfirmdeny.de", "Bitte tippe /xpShop notp um abzubrechen.");
		getConfig().set("teleport.tpconfirmdeny.en", "Please execute the command /xpShop notp to cancel the command.");
		saveConfig();
		reloadConfig();
		config.loadConfigonStart();
		config.reload();
	    }
	} catch (Exception e1) {
	    Logger("Error on loading config: " + e1.getMessage(), "Error");
	    ex1 = e1;
	    e1.printStackTrace();
	    Logger("Version: " + Version + " failed to enable!", "Error");
	    onDisable();
	}
	report = new ReportToHost(this);
	if (ex1 != null) {
	    report.report(332, "Config loading failed", ex1.getMessage(), "xpShop", ex1);
	}
	ListenerShop = new xpShopListener(this);

	if (config.Internet) {
	    this.getServer().getScheduler().runTaskTimerAsynchronously(this, new Runnable() {

		@Override
		public void run() {
		    Logger("Searching update for xpShop!", "Debug");
		    Updater updater = new Updater(xpShop.this, 34732, xpShop.getFile(), UpdateType.NO_DOWNLOAD, true);
		    Logger("Latest: " + updater.getLatestName(), "Debug");
		    if (updater.getResult() == UpdateResult.UPDATE_AVAILABLE) {
			Logger("New version: " + updater.getLatestName() + " found!", "Warning");
			Logger("******************************************", "Warning");
			Logger("*********** Please update!!!! ************", "Warning");
			Logger("* http://dev.bukkit.org/server-mods/xpshop *", "Warning");
			Logger("******************************************", "Warning");
		    }
		}
	    }, 400L, 50000L);
	}
	if (getConfig().getBoolean("firstRun")) {
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
	getSendDatabase();
	bottle = new BottleManager(this);
	TP = new TeleportManager(this);
	playerManager = new PlayerManager(this);
	plugman = new Utilities(this);
	repair = new Repair(this);
	metricshandler = new MetricsHandler(this);
	metricshandler.loadStatsFiles();
	this.getServer().getScheduler().runTaskTimerAsynchronously(this, new Runnable() {

	    @Override
	    public void run() {
		metricshandler.saveStatsFiles();
	    }
	}, 200L, 50000L);
	this.getServer().getScheduler().runTaskLaterAsynchronously(this, new Runnable() {

	    @Override
	    public void run() {
		toggle = false;
		metricshandler.onStart();
	    }
	}, 20);
	timetemp1 = (System.nanoTime() - timetemp1) / 1000000;
	Logger("Enabled in " + timetemp1 + "ms", "");
    }


    /**
     * Gets version.
     * 
     * @return float: Version of the installed plugin.
     */
    public float aktuelleVersion() {
	try {
	    Version = Float.parseFloat(getDescription().getVersion());
	} catch (Exception e) {
	    Logger("Could not parse version in float", "");
	    Logger("Error getting version of xpShop! Message: " + e.getMessage(), "Error");
	}
	return Version;
    }

    public int getEntfernung(Location loc1, Location loc2) {
	int entfernung = 0;
	int x1 = loc1.getBlockX();
	int y1 = loc1.getBlockY();
	int z1 = loc1.getBlockZ();
	int x2 = loc2.getBlockX();
	int y2 = loc2.getBlockY();
	int z2 = loc2.getBlockZ();
	int temp = (x1 - x2) * (x1 - x2) + (y1 - y2) * (y1 - y2) + (z1 - z2) * (z1 - z2);
	temp = (int) Math.sqrt(temp);
	entfernung = Math.round(temp);
	return entfernung;
    }

    /**
     * Opens xpShop gui
     * 
     */
    public void openGUI() {
	panel = new PanelControl(this);
	panel.setSize(400, 300);
	panel.setLocation(200, 300);
	panel.setVisible(true);
    }

//    public void install() {
//	try {
//	    if (config.Internet) {
//		try {
//		    String path = "plugins" + File.separator;
//		    if (upd.download(path)) {
//			Logger("Downloaded new Version!", "Warning");
//			Logger("xpShop will be updated on the next restart!", "Warning");
//		    } else {
//			Logger(" Cant download new Version!", "Warning");
//		    }
//		} catch (Exception e) {
//		    Logger("Error on downloading new Version!", "Error");
//		    report.report(3313, "Error on downloading new Version", e.getMessage(), "xpShop", e);
//		    e.printStackTrace();
//		    Logger("Uncatched Exeption!", "Error");
//		}
//	    }
//	    if (getConfig().getBoolean("installondownload")) {
//		Logger("Found Update! Installing now because of 'installondownload = true', please wait!", "Warning");
//		playerManager.BroadcastMsg("xpShop.update", "Found Update! Installing now because of 'installondownload = true', please wait!");
//	    }
//	    try {
//		plugman.unloadPlugin("xpShop");
//	    } catch (NoSuchFieldException ex) {
//		Logger("Error on installing! Please check the log!", "Error");
//		playerManager.BroadcastMsg("xpShop.update", "Error on installing! Please check the log!");
//		java.util.logging.Logger.getLogger(xpShop.class.getName()).log(Level.SEVERE, null, ex);
//	    } catch (IllegalAccessException ex) {
//		Logger("Error on installing! Please check the log!", "Error");
//		playerManager.BroadcastMsg("xpShop.update", "Error on installing! Please check the log!");
//		java.util.logging.Logger.getLogger(xpShop.class.getName()).log(Level.SEVERE, null, ex);
//	    }
//	    try {
//		plugman.loadPlugin("xpShop");
//	    } catch (InvalidPluginException ex) {
//		Logger("Error on loading after installing! Please check the log!", "Error");
//		playerManager.BroadcastMsg("xpShop.update", "Error on loading after installing! Please check the log!");
//		java.util.logging.Logger.getLogger(xpShop.class.getName()).log(Level.SEVERE, null, ex);
//	    } catch (InvalidDescriptionException ex) {
//		Logger("Error on loading after installing! Please check the log!", "Error");
//		playerManager.BroadcastMsg("xpShop.update", "Error on loading after installing! Please check the log!");
//		java.util.logging.Logger.getLogger(xpShop.class.getName()).log(Level.SEVERE, null, ex);
//	    }
//	    Logger("Installing finished!", "");
//	    playerManager.BroadcastMsg("xpShop.update", "Installing finished!");
//	} catch (Exception w) {
//	    w.printStackTrace();
//	    Logger("Uncatched Exeption!", "Error");
//	    report.report(3314, "Uncatched Exeption on installing", w.getMessage(), "xpShop", w);
//	}
//    }

    /**
     * Called by Bukkit on reloading the server
     * 
     */
    public void onReload() {
	onDisable();
	onEnable();
    }

    /**
     * Called by Bukkit if player posts a command
     * 
     * @param sender
     * @param cmd
     * @param label
     * @param args
     * @return true if no errors happened else return false to Bukkit, then
     *         Bukkit prints /xpShop buy <xp|money>
     */
    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
	try {
	    if (!toggle) {
		if (!Blacklistcode.startsWith("1")) {
		    if (sender instanceof Player) {
			Player player = (Player) sender;
			if (cmd.getName().equalsIgnoreCase("xpShop")) {
			    long temptime = 0;
			    temptime = System.nanoTime();
			    switch (args.length) {
			    case 1:
				ActionxpShop = args[0];
				if (args[0].equalsIgnoreCase("help")) {
				    if (PermissionsHandler.checkpermissions(player, getConfig().getString("help.commands." + ActionxpShop.toLowerCase() + ".permission"))) {
					Help.help(sender, args);
				    }
				} else if (args[0].equalsIgnoreCase("repairconfirm")) {
				    if (PermissionsHandler.checkpermissions(player, getConfig().getString("help.commands." + ActionxpShop.toLowerCase() + ".permission"))) {
					repair.RepairConfirm(player);
					return true;
				    }
				} else if (args[0].equalsIgnoreCase("repaircancel")) {
				    if (PermissionsHandler.checkpermissions(player, getConfig().getString("help.commands." + ActionxpShop.toLowerCase() + ".permission"))) {
					repair.RepairCancel(player);
					return true;
				    }
				} else if (args[0].equalsIgnoreCase("repair")) {
				    if (PermissionsHandler.checkpermissions(player, getConfig().getString("help.commands." + ActionxpShop.toLowerCase() + ".permission"))) {
					repair.registerRepair(player, 0);
					metricshandler.repair++;
				    }
				    return true;
				} else if (args[0].equalsIgnoreCase("toolinfo")) {
				    if (PermissionsHandler.checkpermissions(player, getConfig().getString("help.commands." + ActionxpShop.toLowerCase() + ".permission"))) {
					metricshandler.toolinfo++;
					PlayerLogger(
						player,
						String.format(getConfig().getString("Repair.damage." + config.language), repair.getDamage(player.getItemInHand()),
							repair.maxDurability(player.getItemInHand())), "");
				    }
				    return true;
				    // } else if
				    // (args[0].equalsIgnoreCase("reload")) {
				    // if
				    // (PermissionsHandler.checkpermissions(player,
				    // getConfig().getString("help.commands." +
				    // ActionxpShop.toLowerCase() +
				    // ".permission"))) {
				    // PlayerLogger(player,
				    // "Please wait: Reloading this plugin!",
				    // "Warning");
				    // plugman.unloadPlugin("xpShop");
				    // plugman.loadPlugin("xpShop");
				    // PlayerLogger(player, "Reloaded!", "");
				    // }
				    // return true;
				} else if (args[0].equalsIgnoreCase("showdebug")) {
				    if (PermissionsHandler.checkpermissions(player, getConfig().getString("help.commands." + ActionxpShop.toLowerCase() + ".permission"))) {
					if (DebugMsg.containsKey(player.getName())) {
					    DebugMsg.remove(player.getName());
					} else {
					    DebugMsg.put(player.getName(), true);
					}
					return true;
				    }
				} else if (args[0].equalsIgnoreCase("debugfile")) {
				    if (PermissionsHandler.checkpermissions(player, getConfig().getString("help.commands." + ActionxpShop.toLowerCase() + ".permission"))) {
					getConfig().set("debugfile", !getConfig().getBoolean("debugfile"));
					PlayerLogger(player, "debugfile: " + getConfig().getBoolean("debugfile"), "");
					saveConfig();
					reloadConfig();
					config.reload();
					return true;
				    }
				} else if (args[0].equalsIgnoreCase("internet")) {
				    if (PermissionsHandler.checkpermissions(player, getConfig().getString("help.commands." + ActionxpShop.toLowerCase() + ".permission"))) {
					getConfig().set("check-for-updates", !getConfig().getBoolean("check-for-updates"));
					PlayerLogger(player, "check-for-updates: " + getConfig().getBoolean("check-for-updates"), "");
					saveConfig();
					reloadConfig();
					config.reload();
					return true;
				    }
				} else if (ActionxpShop.equalsIgnoreCase("infoxp")) {
				    if (PermissionsHandler.checkpermissions(player, getConfig().getString("help.commands." + "infoxpown" + ".permission"))) {
					metricshandler.infoxp++;
					infoxp(sender, args);
					temptime = (System.nanoTime() - temptime) / 1000000;
					Logger("Command: " + cmd.getName() + " " + args.toString() + " executed in " + temptime + "ms", "Debug");
					return true;
				    } else {
					return false;
				    }
				} else if (ActionxpShop.equalsIgnoreCase("version")) {
				    PlayerLogger(player, "Version: " + getDescription().getVersion(), "");
				    temptime = (System.nanoTime() - temptime) / 1000000;
				    Logger("Command: " + cmd.getName() + " " + args.toString() + " executed in " + temptime + "ms", "Debug");
				    return true;
				} else if (ActionxpShop.equalsIgnoreCase("bottleconfirm")) {
				    if (PermissionsHandler.checkpermissions(player, getConfig().getString("help.commands." + ActionxpShop.toLowerCase() + ".permission"))) {
					bottle.confirmChange(player);
					return true;
				    }
				} else if (ActionxpShop.equalsIgnoreCase("bottlecancel")) {
				    if (PermissionsHandler.checkpermissions(player, getConfig().getString("help.commands." + ActionxpShop.toLowerCase() + ".permission"))) {
					bottle.cancelChange(player);
					return true;
				    }
				} else if (ActionxpShop.equalsIgnoreCase("accept")) {
				    if (PermissionsHandler.checkpermissions(player, getConfig().getString("help.commands." + ActionxpShop.toLowerCase() + ".permission"))) {
					TP.acceptteleport(player);
					return true;
				    }
				} else if (ActionxpShop.equalsIgnoreCase("deny")) {
				    if (PermissionsHandler.checkpermissions(player, getConfig().getString("help.commands." + ActionxpShop.toLowerCase() + ".permission"))) {
					TP.denyteleport(player);
					return true;
				    }
				} else if (ActionxpShop.equalsIgnoreCase("notp")) {
				    if (PermissionsHandler.checkpermissions(player, getConfig().getString("help.commands." + ActionxpShop.toLowerCase() + ".permission"))) {
					if (commandexec.containsKey(player)) {
					    commandexec.remove(player);
					    requested.remove(player);
					    TP.stopteleport(player);
					} else {
					    PlayerLogger(player, getConfig().getString("teleport.noteleport." + config.language), "Error");
					}
					return true;
				    }
				} else if (ActionxpShop.equalsIgnoreCase("yestp")) {
				    if (PermissionsHandler.checkpermissions(player, getConfig().getString("help.commands." + ActionxpShop.toLowerCase() + ".permission"))) {
					if (commandexec.containsKey(player)) {
					    if (commandexec.get(player)) {
						TP.registerTeleport(requested.get(player), true, player);
						PlayerLogger(
							player,
							String.format(getConfig().getString("teleport.teleportmustbeaccepted." + config.language), requested.get(player).getName(),
								(int) (getConfig().getDouble("Cooldownoftp"))), "Warning");
						PlayerLogger(
							requested.get(player),
							String.format(getConfig().getString("teleport.teleportrequesttome." + config.language), player.getName(),
								(int) (getConfig().getDouble("Cooldownoftp"))), "Warning");
						PlayerLogger(requested.get(player), getConfig().getString("teleport.acceptconfirm." + config.language), "Warning");
						PlayerLogger(requested.get(player), getConfig().getString("teleport.tpconfirmdeny." + config.language), "Warning");
						PlayerLogger(player, String.format(getConfig().getString("teleport.infoconfirmme." + config.language), requested.get(player).getName()), "");
						requested.remove(player);
						commandexec.remove(player);
					    } else {
						TP.registerTeleport(requested.get(player), false, player);
						PlayerLogger(
							player,
							String.format(getConfig().getString("teleport.teleportmustbeaccepted." + config.language), requested.get(player).getName(),
								(int) (getConfig().getDouble("Cooldownoftp"))), "Warning");
						PlayerLogger(
							requested.get(player),
							String.format(getConfig().getString("teleport.teleportrequesttoplayer." + config.language), requested.get(player).getName(),
								(int) (getConfig().getDouble("Cooldownoftp"))), "Warning");
						PlayerLogger(requested.get(player), getConfig().getString("teleport.acceptconfirm." + config.language), "Warning");
						PlayerLogger(requested.get(player), getConfig().getString("teleport.tpconfirmdeny." + config.language), "Warning");
						PlayerLogger(player, String.format(getConfig().getString("teleport.infoconfirmto." + config.language), requested.get(player).getName()), "");
						requested.remove(player);
						commandexec.remove(player);
					    }
					} else {
					    PlayerLogger(player, getConfig().getString("teleport.noteleport." + config.language), "Error");
					}
					return true;
				    }
				} else if (ActionxpShop.equalsIgnoreCase("update")) {
				    if (PermissionsHandler.checkpermissions(player, getConfig().getString("help.commands." + ActionxpShop.toLowerCase() + ".permission"))) {
					//install();
					PlayerLogger(player, "Temporary removed!", "Error");
					return true;
				    }
				} else if (ActionxpShop.equalsIgnoreCase("deletedebug")) {
				    if (PermissionsHandler.checkpermissions(player, getConfig().getString("help.commands." + ActionxpShop.toLowerCase() + ".permission"))) {
					File file = new File("plugins" + File.separator + "xpShop" + File.separator + "debug.txt");
					if (file.exists()) {
					    if (file.delete()) {
						PlayerLogger(player, "file deleted!", "Warning");
						try {
						    file.createNewFile();
						} catch (IOException ex) {
						    java.util.logging.Logger.getLogger(xpShop.class.getName()).log(Level.SEVERE, null, ex);
						}
					    } else {
						PlayerLogger(player, "Error on deleting file!", "Error");
					    }
					}
					return true;
				    }
				} else if (ActionxpShop.equalsIgnoreCase("log")) {
				    if (PermissionsHandler.checkpermissions(player, getConfig().getString("help.commands." + ActionxpShop.toLowerCase() + ".permission"))) {
					File file = new File("plugins" + File.separator + "xpShop" + File.separator + "debug.txt");
					if (file.exists()) {
					    PlayerLogger(player, "debug.txt is " + file.length() + " Byte big!", "Warning");
					    PlayerLogger(player, "Type /xpShop deletedebug to delete the debug.txt!", "Warning");
					}
					return true;
				    }
				} else if (ActionxpShop.equalsIgnoreCase("toggle")) {
				    if (PermissionsHandler.checkpermissions(player, getConfig().getString("help.commands." + ActionxpShop.toLowerCase() + ".permission"))) {
					if (toggle) {
					    toggle = false;
					} else {
					    toggle = true;
					}
					PlayerLogger(player, "xpShop offline: " + toggle, "");
					return true;
				    }
				} else if (ActionxpShop.equalsIgnoreCase("infolevel")) {
				    if (PermissionsHandler.checkpermissions(player, getConfig().getString("help.commands." + "infolevelown" + ".permission"))) {
					metricshandler.infolevel++;
					infolevel(sender, args);
					temptime = (System.nanoTime() - temptime) / 1000000;
					Logger("Command: " + cmd.getName() + " " + args.toString() + " executed in " + temptime + "ms", "Debug");
					return true;
				    }
				} else if (args[0].equalsIgnoreCase("configconfirm")) {
				    if (PermissionsHandler.checkpermissions(player, getConfig().getString("help.commands." + ActionxpShop.toLowerCase() + ".permission"))) {
					if (Config.containsKey(player)) {
					    String temp = getConfig().getString(Config.get(player));
					    Logger("Temp: " + temp, "Debug");
					    boolean isboolean = false;
					    if (temp.equalsIgnoreCase("true") || temp.equalsIgnoreCase("false")) {
						isboolean = true;
						Logger("Config is boolean!", "Debug");
					    }
					    boolean istTrue = false;
					    if (isboolean) {
						if (Set.get(player).equalsIgnoreCase("true")) {
						    istTrue = true;
						    Logger("Config is true!", "Debug");
						}
					    }
					    if (!isboolean) {
						getConfig().set(Config.get(player), Set.get(player));
					    } else {
						getConfig().set(Config.get(player), istTrue);
						Logger("Set boolean", "Debug");
					    }
					    saveConfig();
					    reloadConfig();
					    config.reload();
					    PlayerLogger(player, "You set  " + Config.get(player) + " from " + temp + " to " + getConfig().getString(Config.get(player)) + " !", "Warning");
					    Set.remove(player);
					    Config.remove(player);
					} else {
					    PlayerLogger(player, "Please enter a command first!", "Error");
					}
					return true;
				    }
				} else if (args[0].equalsIgnoreCase("configcancel")) {
				    if (PermissionsHandler.checkpermissions(player, getConfig().getString("help.commands." + ActionxpShop.toLowerCase() + ".permission"))) {
					if (Config.containsKey(player)) {
					    PlayerLogger(player, "Command canceled!", "Warning");
					    Set.remove(player);
					    Config.remove(player);
					} else {
					    PlayerLogger(player, "Please enter a command first!", "Error");
					}
					return true;
				    }
				} else {
				    Help.help(sender, args);
				}
				break;
			    case 2:
				ActionxpShop = args[0];
				if (ActionxpShop.equals("selllevel")) {
				    if (PermissionsHandler.checkpermissions(player, getConfig().getString("help.commands." + ActionxpShop.toLowerCase() + ".permission"))) {
					if (Tools.isInteger(args[1])) {
					    selllevel = Integer.parseInt(args[1]);
					    selllevel(player, this.selllevel, true);
					    temptime = (System.nanoTime() - temptime) / 1000000;
					    metricshandler.selllevel++;
					    Logger("Command: " + cmd.getName() + " " + args.toString() + " executed in " + temptime + "ms", "Debug");
					    return true;
					}
					PlayerLogger(player, config.commanderrornoint, "Error");
					return true;
				    }
				}
				if (args[0].equalsIgnoreCase("repair")) {
				    if (PermissionsHandler.checkpermissions(player, getConfig().getString("help.commands." + ActionxpShop.toLowerCase() + ".permission"))) {
					if (Tools.isInteger(args[1])) {
					    repair.registerRepair(player, Integer.parseInt(args[1]));
					    metricshandler.repair++;
					    return true;
					} else {
					    PlayerLogger(player, config.commanderrornoint, "Error");
					    return true;
					}
				    }
				}
				if (ActionxpShop.equals("bottle")) {
				    if (PermissionsHandler.checkpermissions(player, getConfig().getString("help.commands." + ActionxpShop.toLowerCase() + ".permission"))) {
					if (Tools.isInteger(args[1])) {
					    bottle.registerCommandXPBottles(player, Integer.parseInt(args[1]));
					    metricshandler.bottle++;
					    return true;
					}
					PlayerLogger(player, config.commanderrornoint, "Error");
					return false;
				    }
				} else if (args[0].equalsIgnoreCase("tpme")) {
				    if (PermissionsHandler.checkpermissions(player, getConfig().getString("help.commands." + ActionxpShop.toLowerCase() + ".permission"))) {
					Player teler = (Player) getServer().getPlayer(args[1]);
					if (teler != null) {
					    int entfernung = getEntfernung(player.getLocation(), teler.getLocation());
					    int xpneeded = (int) (getEntfernung(player.getLocation(), teler.getLocation()) * getConfig().getDouble("teleport.xpperblock"));
					    if (player.getTotalExperience() >= xpneeded) {
						if (commandexec.containsKey(player)) {
						    PlayerLogger(player, getConfig().getString("teleport.teleportrequest1." + config.language), "Error");
						} else {
						    final Player player1 = player;
						    getServer().getScheduler().runTaskLater(this, new Runnable() {

							@Override
							public void run() {
							    if (commandexec.containsKey(player1)) {
								Player dest = requested.get(player1);
								requested.remove(player1);
								commandexec.remove(player1);
								PlayerLogger(
									dest,
									String.format(getConfig().getString("teleport.teleportrequesttimeoutaccept." + config.language),
										getConfig().getInt("Cooldownoftp")), "Warning");
								PlayerLogger(player1,
									String.format(getConfig().getString("teleport.teleportrequesttimeout." + config.language), getConfig().getInt("Cooldownoftp")),
									"Warning");
							    }
							}
						    }, getConfig().getInt("Cooldownoftp") * 20);
						    commandexec.put(player, true);
						    requested.put(player, teler);
						    PlayerLogger(player, String.format(getConfig().getString("teleport.info1." + config.language), xpneeded, entfernung), "Warning");
						    PlayerLogger(player, getConfig().getString("teleport.tpconfirm." + config.language), "Warning");
						    PlayerLogger(player, getConfig().getString("teleport.tpconfirmdeny." + config.language), "Warning");
						    metricshandler.tpme++;
						}
					    } else {
						PlayerLogger(player, String.format(getConfig().getString("teleport.notenoughxp." + config.language), entfernung, xpneeded), "Error");
					    }
					}

					return true;
				    }
				} else if (args[0].equalsIgnoreCase("tpto")) {
				    if (PermissionsHandler.checkpermissions(player, getConfig().getString("help.commands." + ActionxpShop.toLowerCase() + ".permission"))) {
					Player teler = (Player) getServer().getPlayer(args[1]);
					if (teler != null) {
					    int entfernung = getEntfernung(player.getLocation(), teler.getLocation());
					    int xpneeded = (int) (getEntfernung(player.getLocation(), teler.getLocation()) * getConfig().getDouble("teleport.xpperblock"));
					    if (player.getTotalExperience() >= xpneeded) {
						if (commandexec.containsKey(player)) {
						    PlayerLogger(player, getConfig().getString("teleport.teleportrequest1." + config.language), "Error");
						} else {
						    final Player player1 = player;
						    getServer().getScheduler().runTaskLater(this, new Runnable() {

							@Override
							public void run() {
							    if (commandexec.containsKey(player1)) {
								Player dest = requested.get(player1);
								requested.remove(player1);
								commandexec.remove(player1);
								PlayerLogger(
									dest,
									String.format(getConfig().getString("teleport.teleportrequesttimeoutaccept." + config.language),
										getConfig().getInt("Cooldownoftp")), "Warning");
								PlayerLogger(player1,
									String.format(getConfig().getString("teleport.teleportrequesttimeout." + config.language), getConfig().getInt("Cooldownoftp")),
									"Warning");
							    }
							}
						    }, getConfig().getInt("Cooldownoftp") * 20);
						    commandexec.put(player, false);
						    requested.put(player, teler);
						    PlayerLogger(player, String.format(getConfig().getString("teleport.info1." + config.language), xpneeded, entfernung), "Warning");
						    PlayerLogger(player, getConfig().getString("teleport.tpconfirm." + config.language), "Warning");
						    PlayerLogger(player, getConfig().getString("teleport.tpconfirmdeny." + config.language), "Warning");
						    metricshandler.tpto++;
						}
					    } else {
						PlayerLogger(player, String.format(getConfig().getString("teleport.notenoughxp." + config.language), entfernung, xpneeded), "Error");
					    }
					}

					return true;
				    }
				} else if (args[0].equalsIgnoreCase("language")) {
				    if (PermissionsHandler.checkpermissions(player, getConfig().getString("help.commands." + ActionxpShop.toLowerCase() + ".permission"))) {
					getConfig().set("language", args[1]);
					PlayerLogger(player, "language set to: " + args[1], "");
					saveConfig();
					Logger("Config saved!", "Debug");
					reloadConfig();
					Logger("Config reloaded!", "Debug");
					Logger("debug reloaded!", "Debug");
					config.reload();
					Logger("Config reloaded!", "Debug");
					return true;
				    }
				} else if (ActionxpShop.equalsIgnoreCase("testsetxp")) {
				    if (PermissionsHandler.checkpermissions(player, "xpShop.testsetxp")) {
					player.setTotalExperience(Integer.parseInt(args[1]));
					PlayerLogger(player, "This are: " + player.getTotalExperience(), "");
					temptime = (System.nanoTime() - temptime) / 1000000;
					Logger("Command: " + cmd.getName() + " " + args.toString() + " executed in " + temptime + "ms", "Debug");
					return true;
				    }
				} else if (ActionxpShop.equals("buylevel")) {
				    if (PermissionsHandler.checkpermissions(player, getConfig().getString("help.commands." + ActionxpShop.toLowerCase() + ".permission"))) {
					if (Tools.isInteger(args[1])) {
					    buylevel = Integer.parseInt(args[1]);
					    buylevel(player, this.buylevel, true);
					    temptime = (System.nanoTime() - temptime) / 1000000;
					    Logger("Command: " + cmd.getName() + " " + args.toString() + " executed in " + temptime + "ms", "Debug");
					    metricshandler.buylevel++;
					    return true;
					}
					PlayerLogger(player, config.commanderrornoint, "Error");
					return false;
				    }
				} else if (ActionxpShop.equals("sell")) {
				    if (PermissionsHandler.checkpermissions(player, getConfig().getString("help.commands." + ActionxpShop.toLowerCase() + ".permission"))) {
					if (Tools.isInteger(args[1])) {
					    sell = Integer.parseInt(args[1]);
					    sell(player, this.sell, true, "sell");
					    temptime = (System.nanoTime() - temptime) / 1000000;
					    Logger("Command: " + cmd.getName() + " " + args.toString() + " executed in " + temptime + "ms", "Debug");
					    metricshandler.sell++;
					    return true;
					}
					PlayerLogger(player, config.commanderrornoint, "Error");
					return false;
				    }
				} else if (ActionxpShop.equals("buy")) {
				    if (PermissionsHandler.checkpermissions(player, getConfig().getString("help.commands." + ActionxpShop.toLowerCase() + ".permission"))) {
					if (Tools.isInteger(args[1])) {
					    buy = Integer.parseInt(args[1]);
					    buy(player, this.buy, true, "buy");
					    temptime = (System.nanoTime() - temptime) / 1000000;
					    Logger("Command: " + cmd.getName() + " " + args.toString() + " executed in " + temptime + "ms", "Debug");
					    metricshandler.buy++;
					    return true;
					}
					return false;
				    }
				} else if (ActionxpShop.equalsIgnoreCase("infoxp")) {
				    if (PermissionsHandler.checkpermissions(player, getConfig().getString("help.commands." + "infoxpother" + ".permission"))) {
					infoxp(sender, args);
					temptime = (System.nanoTime() - temptime) / 1000000;
					Logger("Command: " + cmd.getName() + " " + args.toString() + " executed in " + temptime + "ms", "Debug");
					metricshandler.infoxp++;
					return true;
				    }
				} else if (ActionxpShop.equalsIgnoreCase("infolevel")) {
				    if (PermissionsHandler.checkpermissions(player, getConfig().getString("help.commands." + "infolevelother" + ".permission"))) {
					infolevel(sender, args);
					temptime = (System.nanoTime() - temptime) / 1000000;
					Logger("Command: " + cmd.getName() + " " + args.toString() + " executed in " + temptime + "ms", "Debug");
					metricshandler.infolevel++;
					return true;
				    }
				} else if (ActionxpShop.equalsIgnoreCase("help")) {
				    if (PermissionsHandler.checkpermissions(player, "xpShop.help")) {
					Help.help(player, args);
					temptime = (System.nanoTime() - temptime) / 1000000;
					Logger("Command: " + cmd.getName() + " " + args.toString() + " executed in " + temptime + "ms", "Debug");
					return true;
				    }
				} else if (ActionxpShop.equalsIgnoreCase("report")) {
				    if (PermissionsHandler.checkpermissions(player, getConfig().getString("help.commands." + ActionxpShop.toLowerCase() + ".permission"))) {
					PlayerLogger(player, report.report(331, "Reported issue", args[1], "xpShop", ""), "");
					temptime = (System.nanoTime() - temptime) / 1000000;
					Logger("Command: " + cmd.getName() + " " + args.toString() + " executed in " + temptime + "ms", "Debug");
					return true;
				    }
				} else {
				    Help.help(sender, args);
				}
				break;
			    case 3:
				ActionxpShop = args[0];
				if (ActionxpShop.equalsIgnoreCase("info")) {
				    if (PermissionsHandler.checkpermissions(player, getConfig().getString("help.commands." + ActionxpShop.toLowerCase() + ".permission"))) {
					info(player, args);
					temptime = (System.nanoTime() - temptime) / 1000000;
					Logger("Command: " + cmd.getName() + " " + args.toString() + " executed in " + temptime + "ms", "Debug");
					metricshandler.info++;
					return true;
				    }
				} else if (ActionxpShop.equalsIgnoreCase("send")) {
				    if (PermissionsHandler.checkpermissions(player, getConfig().getString("help.commands." + ActionxpShop.toLowerCase() + ".permission"))) {
					if (Tools.isInteger(args[2])) {
					    int xp = Integer.parseInt(args[2]);
					    sendxp(sender, xp, args[1], args);
					    temptime = (System.nanoTime() - temptime) / 1000000;
					    Logger("Command: " + cmd.getName() + " " + args.toString() + " executed in " + temptime + "ms", "Debug");
					    metricshandler.send++;
					    return true;
					}
					PlayerLogger(player, config.commanderrornoint, "Error");
					return false;
				    }
				} else if (ActionxpShop.equalsIgnoreCase("grand")) {
				    if (PermissionsHandler.checkpermissions(player, getConfig().getString("help.commands." + ActionxpShop.toLowerCase() + ".permission"))) {
					Player empfaengerPlayer = null;
					int amount = 0;
					try {
					    amount = Integer.parseInt(args[2]);
					} catch (NumberFormatException e) {
					    PlayerLogger(player, e.getMessage(), "Error");
					    return true;
					}
					try {
					    empfaengerPlayer = Tools.getmyOfflinePlayer(this, args, 1);
					} catch (PlayerWasNeverOnlineException e) {
					    PlayerLogger(player, e.getMessage(), "Error");
					    return true;
					} catch (PlayerNotOnlineException e) {
					    if (config.onlysendxptoonlineplayers) {
						PlayerLogger(player, e.getMessage(), "Error");
						return true;
					    }
					}
					if (empfaengerPlayer != null) {
					    UpdateXP(empfaengerPlayer, amount, "grand");
					    empfaengerPlayer.saveData();
					    try {
						PlayerLogger(player, (String.format(config.commandsuccesssentxp, Integer.parseInt(args[2]), empfaengerPlayer.getName())), "");
					    } catch (NullPointerException e) {
						PlayerLogger(player, "Error!", "Error");
					    }
					    metricshandler.grand++;
					    return true;
					} else {
					    Tools.addDB(this, args[1], player.getName(), amount);
					    try {
						PlayerLogger(player, (String.format(config.commandsuccesssentxp, Integer.parseInt(args[2]), args[1])), "");
					    } catch (NullPointerException e) {
						PlayerLogger(player, "Error!", "Error");
					    }
					    metricshandler.grand++;
					    return true;
					}
				    }
				    return false;
				} else if (args[0].equalsIgnoreCase("config")) {
				    if (PermissionsHandler.checkpermissions(player, getConfig().getString("help.commands." + ActionxpShop.toLowerCase() + ".permission"))) {
					if (!Config.containsKey(player)) {
					    Config.put(player, args[1]);
					    String Configtext = args[2];
					    for (int i = 3; i < args.length; i++) {
						Configtext.concat(args[i]);
					    }
					    Set.put(player, Configtext);
					    PlayerLogger(player, "Do you want to edit " + args[1] + " from " + getConfig().getString(args[1]) + " to " + Configtext + " ?", "Warning");
					    PlayerLogger(player, String.format("Please confirm within %1$d sec!", getConfig().getInt("Cooldownoftp")), "Warning");
					    PlayerLogger(player, "Please confirm with \"/xpShop configconfirm\" !", "Warning");
					    PlayerLogger(player, "Please cancel with \"/xpShop configcancel\" !", "Warning");
					    final Player player1 = player;
					    getServer().getScheduler().runTaskLater(this, new Runnable() {

						@Override
						public void run() {
						    if (Config.containsKey(player1)) {
							Config.remove(player1);
							Set.remove(player1);
							PlayerLogger(player1, String.format("You havent confirmed within %1$d sec!", getConfig().getInt("Cooldownoftp")), "Warning");
						    }
						}
					    }, getConfig().getInt("Cooldownoftp") * 20);
					    return true;
					} else {
					    PlayerLogger(player, "Please confirm or cancel your last command first!", "Error");
					    return true;
					}
				    }
				} else if (args[0].equalsIgnoreCase("report")) {
				    if (PermissionsHandler.checkpermissions(player, "xpShop.report")) {
					String text = "";
					for (int i = 1; i < args.length; i++) {
					    text = text.concat(" " + args[i]);
					}
					PlayerLogger(player, report.report(331, "Reported issue", text, "BookShop", "No stacktrace because of command"), "");
					temptime = (System.nanoTime() - temptime) / 1000000;
					Logger("Command: " + cmd.getName() + " " + args.toString() + " executed in " + temptime + "ms", "Debug");
					return true;
				    }
				} else {
				    Help.help(sender, args);
				}
				break;
			    default:
				if (args.length > 3) {
				    if (args[0].equalsIgnoreCase("config")) {
					if (PermissionsHandler.checkpermissions(player, getConfig().getString("help.commands." + ActionxpShop.toLowerCase() + ".permission"))) {
					    if (!Config.containsKey(player)) {
						Config.put(player, args[1]);
						String Configtext = args[2];
						for (int i = 3; i < args.length; i++) {
						    Configtext.concat(args[i]);
						}
						Set.put(player, Configtext);
						PlayerLogger(player, "Do you want to edit " + args[1] + " from " + getConfig().getString(args[1]) + " to " + Configtext + " ?", "Warning");
						PlayerLogger(player, String.format("Please confirm within %1$d sec!", getConfig().getInt("Cooldownoftp")), "Warning");
						PlayerLogger(player, "Please confirm with \"/xpShop configconfirm\" !", "Warning");
						PlayerLogger(player, "Please cancel with \"/xpShop configcancel\" !", "Warning");
						final Player player1 = player;
						getServer().getScheduler().runTaskLater(this, new Runnable() {

						    @Override
						    public void run() {
							if (Config.containsKey(player1)) {
							    Config.remove(player1);
							    Set.remove(player1);
							    PlayerLogger(player1, String.format("You havent confirmed within %1$d sec!", getConfig().getInt("Cooldownoftp")), "Warning");
							}
						    }
						}, getConfig().getInt("Cooldownoftp") * 20);
						return true;
					    } else {
						PlayerLogger(player, "Please confirm or cancel your last command first!", "Error");
						return true;
					    }
					}
				    } else if (args[0].equalsIgnoreCase("report")) {
					if (PermissionsHandler.checkpermissions(player, "xpShop.report")) {
					    String text = "";
					    for (int i = 1; i < args.length; i++) {
						text = text.concat(" " + args[i]);
					    }
					    PlayerLogger(player, report.report(331, "Reported issue", text, "BookShop", "No stacktrace because of command"), "");
					    temptime = (System.nanoTime() - temptime) / 1000000;
					    Logger("Command: " + cmd.getName() + " " + args.toString() + " executed in " + temptime + "ms", "Debug");
					    return true;
					}
				    }
				}
				Help.help(player, args);
				return false;
			    }
			}
		    } else if (cmd.getName().equalsIgnoreCase("xpShop")) {
			if (args.length == 1) {
			    if (args[0].equalsIgnoreCase("download")) {
//				String path = "plugins" + File.separator;
//				upd.download(path);
//				Logger("Downloaded new Version!", "Warning");
//				Logger("xpShop will be updated on the next restart!", "Warning");
				Logger("Temporary removed!", "Error");
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
				Logger("Config saved!", "Debug");
				reloadConfig();
				Logger("Config reloaded!", "Debug");
				Logger("debug reloaded!", "Debug");
				config.reload();
				Logger("Config reloaded!", "Debug");
				return true;
			    } else if (args[0].equalsIgnoreCase("debugfile")) {
				getConfig().set("debugfile", !getConfig().getBoolean("debugfile"));
				Logger("debugfile set to: " + getConfig().getBoolean("debugfile"), "");
				saveConfig();
				Logger("Config saved!", "Debug");
				reloadConfig();
				Logger("Config reloaded!", "Debug");
				Logger("debugfile reloaded!", "Debug");
				config.reload();
				Logger("Config reloaded!", "Debug");
				return true;
			    } else if (args[0].equalsIgnoreCase("toggle")) {
				if (toggle) {
				    toggle = false;
				} else {
				    toggle = true;
				}
				Logger("xpShop offline: " + toggle, "");
				return true;
			    } else if (args[0].equalsIgnoreCase("autodownload")) {
				getConfig().set("autodownload", !getConfig().getBoolean("autodownload"));
				Logger("autodownload set to: " + getConfig().getBoolean("autodownload"), "");
				saveConfig();
				Logger("Config saved!", "Debug");
				reloadConfig();
				Logger("Config reloaded!", "Debug");
				Logger("debug reloaded!", "Debug");
				config.reload();
				Logger("Config reloaded!", "Debug");
				return true;
			    } else if (args[0].equalsIgnoreCase("firstRun")) {
				getConfig().set("firstRun", !getConfig().getBoolean("firstRun"));
				Logger("firstRun set to: " + getConfig().getBoolean("firstRun"), "");
				saveConfig();
				Logger("Config saved!", "Debug");
				reloadConfig();
				Logger("Config reloaded!", "Debug");
				Logger("debug reloaded!", "Debug");
				config.reload();
				Logger("Config reloaded!", "Debug");
				return true;
			    } else if (args[0].equalsIgnoreCase("report")) {
				String text = "";
				for (int i = 1; i < args.length; i++) {
				    text = text.concat(" " + args[i]);
				}
				Logger(report.report(331, "Reported issue", text, "BookShop", "No stacktrace because of command"), "");
				return true;
			    }
			} else if (args.length == 2) {
			    if (args[0].equalsIgnoreCase("language")) {
				getConfig().set("language", args[1]);
				Logger("language set to: " + args[1], "");
				saveConfig();
				Logger("Config saved!", "Debug");
				reloadConfig();
				Logger("Config reloaded!", "Debug");
				Logger("debug reloaded!", "Debug");
				config.reload();
				Logger("Config reloaded!", "Debug");
				return true;
			    } else if (args[0].equalsIgnoreCase("report")) {
				String text = "";
				for (int i = 1; i < args.length; i++) {
				    text = text.concat(" " + args[i]);
				}
				Logger(report.report(331, "Reported issue", text, "BookShop", "No stacktrace because of command"), "");
				return true;
			    }
			} else if (args[0].equalsIgnoreCase("report")) {
			    String text = "";
			    for (int i = 1; i < args.length; i++) {
				text = text.concat(" " + args[i]);
			    }
			    Logger(report.report(331, "Reported issue", text, "BookShop", "No stacktrace because of command"), "");
			    return true;
			}
		    }
		    return false;
		} else {
		    blacklistLogger(sender);
		    return true;
		}
	    } else if (args.length == 1) {
		if (args[0].equalsIgnoreCase("toggle")) {
		    if (sender instanceof Player) {
			Player p = (Player) sender;
			if (PermissionsHandler.checkpermissions(p, "xpShop.admin")) {
			    if (toggle) {
				toggle = false;
			    } else {
				toggle = true;
			    }
			    PlayerLogger(p, "xpShop offline: " + toggle, "");
			    return true;
			}
		    } else {
			if (toggle) {
			    toggle = false;
			} else {
			    toggle = true;
			}
			Logger("xpShop offline: " + toggle, "");
			return true;
		    }
		} else {
		    return false;
		}
	    }
	} catch (Exception e1) {
	    sender.sendMessage("Unknown Error: " + e1.getMessage());
	    System.out.println("[xpShop] Unknown Error: " + e1.getMessage());
	    System.out.println("[xpShop] error of minor priority! Do not report if this happens only one time");
	    e1.printStackTrace();
	}
	return false;
    }

    /**
     * Intern logger to send player message if xpShop is blacklisted
     * 
     * @param sender
     */
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

    /**
     * Intern logger to send player message if xpShop is blacklisted
     * 
     * @param sender
     */
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

    /**
     * Intern logger to send player messages and log it into file
     * 
     * @param msg
     * @param TYPE
     */
    public void Logger(String msg, String TYPE) {
	try {
	    if (TYPE.equalsIgnoreCase("Warning") || TYPE.equalsIgnoreCase("Error")) {
		System.err.println(PrefixConsole + TYPE + ": " + msg);
		if (config.debugfile) {
		    Loggerclass.log("Error: " + msg);
		}
		if (playerManager != null) {
		    playerManager.BroadcastconsoleMsg("xpShop.consolemsg", " Warning: " + msg);
		}
	    } else if (TYPE.equalsIgnoreCase("Debug")) {
		if (config.debug) {
		    System.out.println(PrefixConsole + "Debug: " + msg);
		}
		if (config.debugfile) {
		    Loggerclass.log("Debug: " + msg);
		}
		if (playerManager != null) {
		    playerManager.BroadcastconsoleMsg("xpShop.consolemsg", " Debug: " + msg);
		}
	    } else {
		if (playerManager != null) {
		    playerManager.BroadcastconsoleMsg("xpShop.consolemsg", msg);
		}
		System.out.println(PrefixConsole + msg);
		if (config.debugfile) {
		    Loggerclass.log(msg);
		}
	    }
	} catch (Exception e) {
	    e.printStackTrace();
	    System.out.println("[xpShop] Error: Uncatch Exeption!");
	    report.report(3317, "Logger doesnt work", e.getMessage(), "xpShop", e);
	}
    }

    /**
     * Intern logger to send player messages and log it into file
     * 
     * @param p
     * @param msg
     * @param TYPE
     */
    public void PlayerLogger(Player p, String msg, String TYPE) {
	try {
	    if (TYPE.equalsIgnoreCase("Error")) {
		if (config.UsePrefix) {
		    p.sendMessage(config.Prefix + Prefix + ChatColor.RED + "Error: " + config.Text + msg);
		    if (config.debugfile) {
			Loggerclass.log("Player: " + p.getName() + " Error: " + msg);
		    }
		} else {
		    p.sendMessage(ChatColor.RED + "Error: " + config.Text + msg);
		    if (config.debugfile) {
			Loggerclass.log("Player: " + p.getName() + " Error: " + msg);
		    }
		}
		if (playerManager != null) {
		    playerManager.BroadcastconsoleMsg("xpShop.gamemsg", "Player: " + p.getName() + " Error: " + msg);
		}
	    } else {
		if (config.UsePrefix) {
		    p.sendMessage(config.Prefix + Prefix + config.Text + msg);
		    if (config.debugfile) {
			Loggerclass.log("Player: " + p.getName() + " Msg: " + msg);
		    }
		} else {
		    p.sendMessage(config.Text + msg);
		    if (config.debugfile) {
			Loggerclass.log("Player: " + p.getName() + " Msg: " + msg);
		    }
		}
		if (playerManager != null) {
		    playerManager.BroadcastconsoleMsg("xpShop.gamemsg", "Player: " + p.getName() + " " + msg);
		}
	    }
	} catch (Exception e) {
	    e.printStackTrace();
	    System.out.println("[xpShop] Error: Uncatch Exeption!");
	    report.report(3317, "PlayerLogger doesnt work", e.getMessage(), "xpShop", e);
	}
    }

    /**
     * manages sending of XP to players
     * 
     * @param sender
     * @param giveamount
     * @param empfaenger
     * @param args
     */
    public void sendxp(final CommandSender sender, final int giveamount, final String empfaenger, final String[] args) {
	final Player player = (Player) sender;
	if (!(Blacklistcode.startsWith("1", 3))) {
	    Player empfaengerPlayer = null;
	    long time = 0;
	    time = System.nanoTime();

	    if (giveamount < 1) {
		try {
		    throw new InvalidXPAmountException(giveamount);
		} catch (Exception e) {
		    PlayerLogger(player, e.getMessage(), "Error");
		    return;
		}
	    }
	    try {
		empfaengerPlayer = Tools.getmyOfflinePlayer(this, args, 1);
	    } catch (PlayerNotOnlineException e2) {
		if (config.onlysendxptoonlineplayers) {
		    PlayerLogger(player, e2.getMessage(), "Error");
		    return;
		}
	    } catch (PlayerWasNeverOnlineException e2) {
		PlayerLogger(player, e2.getMessage(), "Error");
		return;
	    }

	    int temp = sell(sender, giveamount, false, "sendxp");

	    if (empfaengerPlayer != null) {
		try {
		    buy(empfaengerPlayer, temp, false, "sendxp");
		    // Gives other player XP wich were substracted.
		    empfaengerPlayer.saveData();
		} catch (Exception e1) {
		    buy(player, temp, false, "sendxp");
		    e1.printStackTrace();
		    report.report(1516, "", "Could not send xp", "xpShop", e1);
		    PlayerLogger(player, args[1] + " " + config.playernotonline, "Error");
		    return;
		}
		try {
		    PlayerLogger(player, (String.format(config.commandsuccesssentxp, temp, args[1])), "");
		    PlayerLogger(empfaengerPlayer, (String.format(config.commandsuccessrecievedxp, temp, sender.getName())), "");
		} catch (NullPointerException e) {
		    PlayerLogger(player, "Error!", "Error");
		}
	    } else {

		// Trys to substract amount, else stop.
		try {
		    Tools.addDB(this, empfaenger, player.getName(), temp);
		} catch (InvalidXPAmountException e) {
		    buy(player, temp, false, "sendxp");
		    PlayerLogger(player, e.getMessage(), "Error");
		    return;
		}
		try {
		    PlayerLogger(player, (String.format(config.commandsuccesssentxp, temp, args[1])), "");
		} catch (NullPointerException e) {
		    PlayerLogger(player, "Error!", "Error");
		}
	    }
	    time = (System.nanoTime() - time) / 1000000;
	    Logger("Send xp executed in " + time + " ms!", "Debug");
	} else {
	    blacklistLogger(player);
	}
    }

    public static int getExp(int level) {
	level -= 1;
	int exp = 0;
	for (int x = 0; x <= level; x++) {
	    exp += nextLevelAt(x);
	}
	return exp;
    }

    public static int nextLevelAt(int level) {
	if (level >= 30) {
	    return 62 + (level - 30) * 7;
	}
	if (level >= 15) {
	    return 17 + (level - 15) * 3;
	} else {
	    return 17;
	}
    }

    /**
     * Get the XP you have if you have this amount of levels
     * 
     * @param level
     * @return Total XP if someone have this level
     */
    public double getLevelXP(int level) {
	return getExp(level);
    }

    /**
     * Updates the players amount (grands)
     * 
     * @param sender
     * @param amount
     * @param von
     */
    public void UpdateXP(CommandSender sender, int amount, String von) {
	Player player = (Player) sender;
	Logger("Old TOTALXP of " + player.getName() + ": " + player.getTotalExperience(), "Debug");
	Logger("Old TOTALXP of (Bukkit) " + player.getName() + ": " + player.getTotalExperience(), "Debug");
	double Expaktuell = player.getTotalExperience() + amount;
	try {
	    if (Expaktuell >= 0) {
		player.setTotalExperience(0);
		player.setExp(0);
		player.setLevel(0);
		player.giveExp((int) Expaktuell);
	    } else {
		PlayerLogger(player, "Invalid exp count: " + amount, "Error");
	    }
	} catch (NumberFormatException ex) {
	    PlayerLogger(player, "Invalid exp count: " + amount, "Error");
	}
	Logger("New TOTALXP of " + player.getName() + ": " + player.getTotalExperience(), "Debug");
	Logger("New TOTALXP (Bukkit) of " + player.getName() + ": " + player.getTotalExperience(), "Debug");
    }

    /**
     * Send message to player and says him how many level some have
     * 
     * @param sender
     * @param args
     */
    public void infolevel(CommandSender sender, String[] args) {
	Player player = (Player) sender;
	if (!Blacklistcode.startsWith("1", 9)) {
	    if (args.length == 1) {
		PlayerLogger(player, String.format(config.infoownLevel, player.getLevel()), "");
	    } else if (args.length == 2) {
		Player empfaenger1;
		try {
		    try {
			empfaenger1 = Tools.getmyOfflinePlayer(this, args, 1);
		    } catch (Exception e1) {
			PlayerLogger(player, args[1] + " " + config.playernotonline, "Error");
			return;
		    }
		    if (empfaenger1 != null) {
			if (empfaenger1.hasPlayedBefore()) {
			    PlayerLogger(player, String.format(config.infootherLevel, empfaenger1.getName(), empfaenger1.getLevel()), "");
			} else {
			    PlayerLogger(player, args[1] + " " + config.playerwasntonline, "Error");
			}
		    } else {
			PlayerLogger(player, args[1] + " " + config.playernotonline, "Error");
		    }
		} catch (Exception e) {
		    PlayerLogger(player, args[1] + " " + config.playernotonline, "Error");
		}
	    }
	} else {
	    blacklistLogger(player);
	}
    }

    /**
     * Send message to player and says him how many XP some have
     * 
     * @param sender
     * @param args
     */
    public void infoxp(CommandSender sender, String[] args) {
	Player player = (Player) sender;
	if (!Blacklistcode.startsWith("1", 8)) {
	    if (args.length == 1) {
		PlayerLogger(player, String.format(config.infoownXP, (int) player.getTotalExperience()), "");
	    } else if (args.length == 2) {
		Player empfaenger1;
		try {
		    try {
			empfaenger1 = Tools.getmyOfflinePlayer(this, args, 1);
		    } catch (Exception e1) {
			if (config.debug) {
			    e1.printStackTrace();
			    e1.getMessage();
			}
			PlayerLogger(player, args[1] + " " + config.playernotonline, "Error");
			return;
		    }
		    if (empfaenger1 != null) {
			if (empfaenger1.hasPlayedBefore()) {
			    PlayerLogger(player, String.format(config.infootherXP, empfaenger1.getName(), empfaenger1.getTotalExperience()), "");
			}
		    } else {
			PlayerLogger(player, args[1] + " " + config.playernotonline, "Error");
			Logger("Player == null", "Debug");
		    }
		} catch (Exception e) {
		    if (config.debug) {
			e.printStackTrace();
			e.getMessage();
		    }
		    PlayerLogger(player, args[1] + " " + config.playernotonline, "Error");
		}
	    }
	} else {
	    blacklistLogger(player);
	}
    }

    /**
     * Called by onCommand and buylevel, buys XP.
     * 
     * @param sender
     * @param buyamount
     * @param moneyactive
     *            sets if player has to pay
     * @param von
     *            changes the message if its equals buy or info
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
		try {
		    if (MoneyHandler.getBalance(player) >= TOTALXPDOUBLE) {
			valid = true;
		    } else {
			PlayerLogger(player, config.commanderrornotenoughmoney, "Error");
		    }
		} catch (NoiConomyPluginFound e) {
		    PlayerLogger(player, e.getMessage(), "Error");
		    return false;
		}
	    } else if (von.equals("sendxp")) {
		valid = true;
	    }
	    if (valid) {
		if (buyamount > 0) {
		    UpdateXP(sender, buyamount, "buy");
		    if (moneyactive) {
			MoneyHandler.substract(TOTALXPDOUBLE, player);
		    }
		} else {
		    try {
			if (!von.equals("buylevel")) {
			    PlayerLogger(player, "Invalid exp count: " + buyamount, "Error");
			    PlayerLogger(player, String.format(config.commanderrorinfo, MoneyHandler.getBalance(player), (int) (MoneyHandler.getBalance(player) / getmoney)), "Error");
			}
		    } catch (NoiConomyPluginFound e) {
			PlayerLogger(player, e.getMessage(), "Error");
			return false;
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

    /**
     * Called by onCommand and selllevel, sells XP.
     * 
     * @param sender
     * @param sellamount
     * @param moneyactive
     *            sets if the player has to pay
     * @param von
     *            changes the message if its equals buy or info
     * @return true if no error occurred.
     */
    public int sell(CommandSender sender, int sellamount, boolean moneyactive, String von) {
	SubstractedXP = 0;
	Player player = (Player) sender;
	if (!Blacklistcode.startsWith("1", 2)) {
	    if (sellamount <= 0) {
		if (!von.equals("sendxp")) {
		    PlayerLogger(player, "Invalid Amount!", "Error");
		}
		return 0;
	    }
	    try {
		double TOTAL = player.getTotalExperience();
		int TOTALint = (int) TOTAL;
		getmoney = config.xptomoney;
		if (sellamount <= TOTAL) {
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
     * @param sender
     *            , amount, moneyactive = true if you want that player have to
     *            buy XP, false if there is an info what that would cost.
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
	    double xpAktuell = player.getTotalExperience();
	    double neededXP = xpNeededForLevel - xpAktuell;
	    try {
		if (MoneyHandler.getBalance(player) < (money1 * neededXP)) {
		    PlayerLogger(player, "Stopped because of not having enough money!", "Error");
		    PlayerLogger(player, "Invalid exp count: " + levelamontbuy, "Error");
		} else {
		    if (moneyactive) {
			buy(sender, (int) (neededXP), true, "buylevel");
		    }
		}
	    } catch (NoiConomyPluginFound e) {
		PlayerLogger(player, e.getMessage(), "Error");
		return;
	    }
	    if (ActionxpShop.equalsIgnoreCase("buylevel")) {

		Logger("String: " + config.commandsuccessbuylevel + "moneytoxp: " + config.moneytoxp + "needed xp: " + neededXP, "Debug");

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
     * @param sender
     * @param levelamountsell
     * @param moneyactive
     *            moneyactive = true if you want that player have to buy XP,
     *            false if there is an info what that would cost.
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
		double xpAktuell = player.getTotalExperience();
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
     * @param sender
     * @param args
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

    public SendDatabase getSendDatabase() {
	if (sendDatabase == null) {
	    sendDatabase = new SendDatabase(this);
	    sendDatabase.createConnection();
	    sendDatabase.PrepareDB();
	}
	return sendDatabase;
    }
}
