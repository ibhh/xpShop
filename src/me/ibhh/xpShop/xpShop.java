package me.ibhh.xpShop;

import me.ibhh.xpShop.Tools;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;
import java.util.List;

//import net.milkbowl.vault.Vault;
//import net.milkbowl.vault.economy.Economy;
//import net.milkbowl.vault.economy.EconomyResponse;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

//import org.bukkit.plugin.Plugin;
//import org.bukkit.plugin.RegisteredServiceProvider;
import org.bukkit.plugin.java.JavaPlugin;

import ru.tehkode.permissions.PermissionManager;
import ru.tehkode.permissions.bukkit.PermissionsEx;

import com.iCo6.system.Accounts;

import com.iConomy.iConomy;
import com.iConomy.system.Account;
import com.iConomy.system.Holdings;

import com.nijikokun.register.payment.Methods;

public class xpShop extends JavaPlugin {



	private String ActionxpShop;
	private Holdings balance5;
	private Double balance;
	private int buy;
	private int sell;
	private int buylevel;
	private int selllevel;
	public double getmoney;
	public int SubstractedXP;
	public iConomy iConomy = null;
	public int iConomyversion = 0;
	public float Version = 0;
	int rounds1 = 0;
	int rounds = 0;
	//	private Vault vault;
	//    public static Economy economy;
	//
	//    private Boolean setupEconomy()
	//    {
	//        RegisteredServiceProvider<Economy> economyProvider = getServer().getServicesManager().getRegistration(net.milkbowl.vault.economy.Economy.class);
	//        if (economyProvider != null) {
	//            economy = economyProvider.getProvider();
	//        }
	//
	//        return (economy != null);
	//    }

	/**
	 * Called by Bukkit on stopping the server
	 *
	 * @param 
	 * @return 
	 */
	@Override
	public void onDisable() 
	{

		System.out.println("[xpShop] disabled!");

	}

	/**
	 * Delete an download new version of xpShop in the Update folder.
	 *
	 * @param 
	 * @return 
	 */
	public void autoUpdate(String url, String path, String name)
	{
		try {
			Update.autoDownload(url, path, "xpShop.jar");
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * Gets version.
	 *
	 * @param 
	 * @return float: Version of the installed plugin.
	 */
	public float aktuelleVersion()
	{
		try
		{
			Version = Float.parseFloat(getDescription().getVersion());
		}
		catch(Exception e)
		{
			System.out.println("[xpShop]Could not parse version in float");
		}
		return Version;
	}

	/**
	 * Checks version with a http-connection
	 *
	 * @param 
	 * @return float: latest recommend build.
	 */
	public float getNewVersion(String url)
	{
		float rt2 = 0;
		String zeile;

		try {
			URL myConnection = new URL(url);
			URLConnection connectMe = myConnection.openConnection();

			InputStreamReader lineReader = new InputStreamReader(connectMe.getInputStream());
			//BufferedReader buffer = new BufferedReader(lineReader);
			BufferedReader br = new BufferedReader(new BufferedReader(lineReader));
			zeile = br.readLine();
			rt2 = Float.parseFloat(zeile);
		} catch(IOException ioe) {
			ioe.printStackTrace();
			System.out.println("[xpShop]Exception: IOException!");
			return -1;
		} catch (Exception e) {
			e.printStackTrace();
			System.out.println("[xpShop]Exception: Exception!");
			return 0;
		}
		return rt2;
	}

	/**
	 * Compares Version to newVersion
	 *
	 * @param url from newVersion file + currentVersion
	 * @return true if newVersion recommend.
	 */
	public boolean UpdateAvailable(String url, float currVersion) {
		boolean a = false;
		if(getNewVersion(url) > currVersion)
		{
			a = true;
		}
		return a;
	}

	/**
	 * Called by Bukkit on starting the server
	 *
	 * @param 
	 * @return 
	 */
	@Override
	public void onEnable()
	{
		try
		{
			this.getConfig().options().copyDefaults(true);
			saveConfig();
			reloadConfig();
			System.out.println("[xpShop] Config file found!");
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}


		try 
		{
			aktuelleVersion();
		} catch (Exception e) {
			e.printStackTrace();
		}


		try 
		{
			iConomyversion();
		} catch (Exception e) {
			e.printStackTrace();
		}

		//        Plugin x = this.getServer().getPluginManager().getPlugin("Vault");
		//        if(x != null & x instanceof Vault) {
		//            vault = (Vault) x;
		//            System.out.println("[xpShop] hooked into Vault.");
		//        } else {
		//            /**
		//             * Throw error & disable because we have Vault set as a dependency, you could give a download link
		//             * or even download it for the user.  This is all up to you as a developer to decide the best option
		//             * for your users!  For our example, we assume that our audience (developers) can find the Vault
		//             * plugin and properly install it.  It's usually a bad idea however.
		//             */
		//        	if(iConomyversion == 2)
		//        	{
		//        	System.out.println("[xpShop] Vault was NOT found! Disabling plugin. Please check the config.yml");
		//            getPluginLoader().disablePlugin(this);
		//        	}
		//        }
		//this.getServer().getPluginManager().registerEvent(Type.PLUGIN_ENABLE, new server(this), Priority.Monitor, this);
		//this.getServer().getPluginManager().registerEvent(Type.PLUGIN_DISABLE, new server(this), Priority.Monitor, this);

		System.out.println("[xpShop] Version: " + Version + " successfully enabled!");

		String URL = "http://ibhh.de:80/aktuelleversion.html";
		if((UpdateAvailable(URL, Version) == true))
		{
			System.out.println("[xpShop]New version: " + getNewVersion(URL) + " found!");
			System.out.println("[xpShop]******************************************");
			System.out.println("[xpShop]*********** Please update!!!! ************");
			System.out.println("[xpShop]* http://ibhh.de/xpShop.jar *");
			System.out.println("[xpShop]******************************************");
			if(getConfig().getBoolean("autodownload") == true)
			{
				String path = getDataFolder().toString() + "/Update/";
				autoUpdate("http://ibhh.de/xpShop.jar", path, "xpShop.jar");
			}
			else
			{
				System.out.println("[xpShop] Please type [xpShop download] to download manual! ");
			}
		}
	}


	/**
	 * Called by Bukkit on reloading the server
	 *
	 * @param 
	 * @return 
	 */
	public void onReload()
	{
		onEnable();
	}


	/**
	 * Called by Bukkit if player posts a command
	 *
	 * @param none, cause of Bukkit.
	 * @return true if no errors happened else return false to Bukkit, then Bukkit prints /xpShop buy <xp|money>.
	 */
	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args)
	{
		if (sender instanceof Player)
		{
			Player player = (Player) sender;
			if (cmd.getName().equalsIgnoreCase("xpShop"))
			{		
				if(args.length >= 1)
				{
					ActionxpShop = args[0];
					if (checkpermissions(sender, args[0]))
					{
						if (args.length == 3)
						{
							this.ActionxpShop = args[0];
							if (args[0].equalsIgnoreCase("info"))
							{
								if ((!Tools.isInteger(args[1])) && (Tools.isInteger(args[2])))
								{
									info(player, args);
									return true;
								}

								player.sendMessage(ChatColor.GRAY + "[xpShop]" + ChatColor.RED + getConfig().getString(new StringBuilder("command.error.noint.").append(getConfig().getString("language")).toString()));
								return false;
							}
							else
								if (args[0].equalsIgnoreCase("send"))
								{
									if ((!Tools.isInteger(args[1])) && (Tools.isInteger(args[2])))
									{
										int xp = Integer.parseInt(args[2]);
										sentxp(sender, xp, args[1], args);
										return true;
									}

									player.sendMessage(ChatColor.GRAY + "[xpShop]" + ChatColor.RED + getConfig().getString(new StringBuilder("command.error.noint.").append(getConfig().getString("language")).toString()));
									return false;
								}
						}
						else
						{
							if (args.length == 2)
							{
								if (args[0].equals("help"))
								{
									if (!Tools.isInteger(args[1]))
									{
										help(player, args);
										return true;
									}

									player.sendMessage(ChatColor.GRAY + "[xpShop]" + ChatColor.RED + getConfig().getString(new StringBuilder("command.error.noint.").append(getConfig().getString("language")).toString()));
									return false;
								}
								else
									if (args[0].equals("buy"))
									{
										if (Tools.isInteger(args[1]))
										{
											buy = Integer.parseInt(args[1]);
											buy(player, this.buy, true, "buy");
											return true;
										}

										player.sendMessage(ChatColor.GRAY + "[xpShop]" + ChatColor.RED + getConfig().getString(new StringBuilder("command.error.noint.").append(getConfig().getString("language")).toString()));
										return false;
									}
									else
										if (args[0].equals("sell"))
										{
											if (Tools.isInteger(args[1]))
											{
												sell = Integer.parseInt(args[1]);
												sell(player, this.sell, true, "sell");
												return true;
											}

											player.sendMessage(ChatColor.GRAY + "[xpShop]" + ChatColor.RED + getConfig().getString(new StringBuilder("command.error.noint.").append(getConfig().getString("language")).toString()));
											return false;
										}
										else
											if (args[0].equals("buylevel"))
											{
												if (Tools.isInteger(args[1]))
												{
													buylevel = Integer.parseInt(args[1]);
													buylevel(player, this.buylevel, true);
													return true;
												}

												player.sendMessage(ChatColor.GRAY + "[xpShop]" + ChatColor.RED + getConfig().getString(new StringBuilder("command.error.noint.").append(getConfig().getString("language")).toString()));
												return false;
											}
											else
												if (args[0].equals("selllevel"))
												{
													if (Tools.isInteger(args[1]))
													{
														selllevel = Integer.parseInt(args[1]);
														selllevel(player, this.selllevel, true);
														return true;
													}

													player.sendMessage(ChatColor.GRAY + "[xpShop]" + ChatColor.RED + getConfig().getString(new StringBuilder("command.error.noint.").append(getConfig().getString("language")).toString()));
													return false;
												}
												else
												{
													help(player, args);
													return false;
												}
							}
							help(player, args);
							return false;
						}
					}

					//						}
					//						else
					//						{
					//							System.out.println("JRE Error!");
					//						}
					//					}
				} //if (args.length == 2)
				else
				{
					player.sendMessage(ChatColor.GRAY + "[xpShop]" + ChatColor.RED + (getConfig().getString("command.error.toomanyarguments." + getConfig().getString("language"))));
					help(player, args);
					return false;
				}
			} //if (cmd.getName().equalsIgnoreCase("xpShop"))
		} //if (sender instanceof Player)
		else
			if (cmd.getName().equalsIgnoreCase("xpShop"))
			{		
				if(args.length == 1)
				{
					if (args[0].equals("download"))
					{
						String path = getDataFolder().toString() + "/Update/";
						autoUpdate("http://ibhh.de/xpShop.jar", path, "xpShop.jar");
					}
				}
			}
		return true;
	} //public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args)

	protected static boolean isConsole(CommandSender sender) {
		return !(sender instanceof Player);
	}

	public static Player getPlayer(CommandSender sender, String[] args, int index) {
		if (args.length > index) {
			List<Player> players = sender.getServer().matchPlayer(args[index]);

			if (players.isEmpty()) {
				sender.sendMessage("Could not find player with the name: " + args[index]);
				return null;
			}
			return (Player)players.get(0);
		}

		if (isConsole(sender)) {
			return null;
		}
		return (Player)sender;
	}

	public void sentxp(CommandSender sender, int giveamount, String empfaenger, String[] args)
	{
		Player player = (Player) sender;
		if(getPlayer(sender, args, 1).hasPlayedBefore())
		{
			Player empfaenger1 = (Player) getPlayer(sender, args, 1);
			sell(sender, giveamount, false, "sentxp");	//Trys to substract amount, else stop.
			buy(empfaenger1, SubstractedXP, false, "sentxp");	//Gives other player XP wich were substracted.
			try
			{
				player.sendMessage(ChatColor.GRAY + "[xpShop]" + ChatColor.RED + (getConfig().getString("command.success." + "sentxp" + "." + getConfig().getString("language") + ".1")) + " " + SubstractedXP + " " + (getConfig().getString("command.success." + "sentxp" + "." + getConfig().getString("language") + ".2")) + " " + args[1] + " " + (getConfig().getString("command.success." + "sentxp" + "." + getConfig().getString("language") + ".3")));
				empfaenger1.sendMessage(ChatColor.GRAY + "[xpShop]" + ChatColor.RED + (getConfig().getString("command.success." + "recievedxp" + "." + getConfig().getString("language") + ".1")) + " " + SubstractedXP + " " + (getConfig().getString("command.success." + "recievedxp" + "." + getConfig().getString("language") + ".2")) + " " + args[1] + " " + (getConfig().getString("command.success." + "recievedxp" + "." + getConfig().getString("language") + ".3")));
			}
			catch (NullPointerException e)
			{
				player.sendMessage("Error!");
			}
		}
		else
		{
			player.sendMessage(ChatColor.GRAY + "[xpShop]" + ChatColor.RED + "Player doesnt exist!");
			player.sendMessage(ChatColor.GRAY + "[xpShop]" + ChatColor.RED + "Player may should leave and join the game.");
		}

	}

	/**
	 * Called by onCommand and buylevel, buys XP.
	 *
	 * @param sender, amount, moneyactive = true if you want that player have to buy XP, false if there is an info what that would cost.
	 * @return true if no error occurred.
	 */
	public boolean buy(CommandSender sender, int buyamount, boolean moneyactive, String von)
	{
		Player player = (Player) sender;
		double TOTALXPDOUBLE = (buyamount * (getConfig().getDouble("moneytoxp")));
		int TOTALXP = (int) TOTALXPDOUBLE;
		if(buyamount <= 0)
		{
			if(!von.equals("sentxp"))
			{
				player.sendMessage(ChatColor.GRAY + "[xpShop]" + ChatColor.RED + "Invalid Amount!");
			}
			return false;
		}
		if(getBalance156(player) >= buyamount)
		{
			try
			{
				int Turns = 0;
				while((TOTALXP -1) >= Turns++)
				{
					player.giveExp(1);
					if(moneyactive == true)
					{
						substractmoney156(getmoney, player);
					}			
				}
				if(ActionxpShop.equalsIgnoreCase("buy"))
				{
					player.sendMessage(ChatColor.GRAY + "[xpShop]" + ChatColor.RED + String.format(getConfig().getString("command.success." + "buy" + "." + getConfig().getString("language")), buyamount, TOTALXP));

				}
				else if (ActionxpShop.equalsIgnoreCase("info") && von.equals("buylevel") == false)
				{
					player.sendMessage(ChatColor.GRAY + "[xpShop]" + ChatColor.RED + String.format((getConfig().getString("info.prefix." + getConfig().getString("language"))) + " " + (getConfig().getString("command.success." + "buy" + "." + getConfig().getString("language"))), buyamount, TOTALXP));
				}
			}
			catch (NumberFormatException ex)
			{
				player.sendMessage("Invalid exp count: " + buyamount);
			}
			player.saveData();
			if(moneyactive == true)
			{
				substractmoney156(buyamount, player);
			}
			return true;
		} //if (Balance >= money)
		else
		{
			player.sendMessage(ChatColor.GRAY + "[xpShop]" + ChatColor.RED + (getConfig().getString("command.error.notenoughmoney." + getConfig().getString("language"))));
		}
		return false;
	}
	public int sell(CommandSender sender, int sellamount, boolean moneyactive, String von)
	{
		Player player = (Player) sender;
		if (sellamount == 0)
		{
			SubstractedXP = 0;
			return SubstractedXP;
		}
		if(player.getLevel() + player.getExp() <= 0.20)
		{
			player.sendMessage(ChatColor.GRAY + "[xpShop]" + ChatColor.RED + (getConfig().getString("command.error.notenoughxp." + getConfig().getString("language"))));
			SubstractedXP = 0;
			return SubstractedXP;
		}
		else
		{
			try
			{
				/** Noch nicht von Bukkit implementiert!
	if(player.getTotalExperience() >= sellamount)
	{
	getmoney = (getConfig().getDouble("xptomoney"));
	int neuXP = player.getTotalExperience() - sellamount;
	player.setTotalExperience(neuXP);
	addmoney156((getmoney * sellamount), player);
	if(ActionxpShop.equalsIgnoreCase("sell"))
	{
	player.sendMessage(ChatColor.GRAY + "[xpShop]" + ChatColor.RED + (getConfig().getString("command.success." + ActionxpShop + "." + getConfig().getString("language") + ".1")) + " " + sellamount + " " + (getConfig().getString("command.success." + ActionxpShop + "." + getConfig().getString("language") + ".2")) + " " + addmoney + " " + (getConfig().getString("command.success." + ActionxpShop + "." + getConfig().getString("language") + ".3")));
	}
	}
	else
	{
	player.sendMessage(ChatColor.GRAY + "[xpShop]" + ChatColor.RED + (getConfig().getString("command.error.notenoughxp." + getConfig().getString("language"))));
	}
				 **/
				if(SubstractedXP != 0)
				{
					SubstractedXP = 0;
				}
				getmoney = (getConfig().getDouble("xptomoney"));
				while((SubstractedXP < sellamount) && ((player.getLevel() + player.getExp() >= 0.20) && (SubstractedXP < sellamount)))
				{
					if(player.getExp() <= 0.1)
					{
						try
						{
							SubstractedXP++;
							int level = player.getLevel();
							level = level - 1;
							player.setLevel(level);
							player.setExp( (float) 0.999999);
							if(moneyactive == true)
							{
								addmoney156(getmoney * 2, player);
							}
						}
						catch (Exception E)
						{
							E.printStackTrace();
							player.sendMessage(ChatColor.GRAY + "[xpShop]" + ChatColor.RED + (getConfig().getString("command.error.else." + getConfig().getString("language"))));
							return SubstractedXP;
						}
					}
					else
					{
						player.giveExp(-1);
						if(moneyactive == true)
						{
							addmoney156(getmoney, player);
						}
						SubstractedXP++;
					}
				} //while(SubstractedXP > TOTALXP)
			}
			catch (NumberFormatException ex)
			{
				player.sendMessage("Invalid exp count: " + sellamount);
				SubstractedXP = 0;
				return SubstractedXP;
			}
			player.saveData();
		}
		double x = (getConfig().getDouble("xptomoney"))* SubstractedXP;
		int addmoney = (int) Math.round(x);
		if(ActionxpShop.equalsIgnoreCase("sell"))
		{
			player.sendMessage(ChatColor.GRAY + "[xpShop]" + ChatColor.RED + String.format((getConfig().getString("command.success." + "sell" + "." + getConfig().getString("language"))), SubstractedXP, addmoney));
		}
		else if (ActionxpShop.equalsIgnoreCase("info") && von.equals("selllevel") == false)
		{
			player.sendMessage(ChatColor.GRAY + "[xpShop]" + ChatColor.RED + String.format((getConfig().getString("info.prefix." + getConfig().getString("language"))) + " " + (getConfig().getString("command.success.sell." + getConfig().getString("language"))), SubstractedXP, addmoney));
		}
		return SubstractedXP;
	}


	/**
	 * Checks players permissions.
	 * Works with Bukkitpermissions and PermissionsEX
	 *
	 * @param sender, action, which should be performed.
	 * @return true if player has permission.
	 */
	public boolean checkpermissions(CommandSender sender, String action)
	{
		if (sender instanceof Player)
		{
			Player player = (Player) sender;
			if(!Bukkit.getServer().getPluginManager().isPluginEnabled("PermissionsEx"))
			{
				if(player.hasPermission("xpShop." + action))
				{
					return true;
				} //if(permissions.has(player, "xpShop." + action))
				else
				{
					player.sendMessage(ChatColor.GRAY + "[xpShop]" + ChatColor.RED + (getConfig().getString("permissions.error." + getConfig().getString("language"))));
					return false;
				}
			}
			else
			{
				if(Bukkit.getServer().getPluginManager().isPluginEnabled("PermissionsEx"))
				{
					PermissionManager permissions = PermissionsEx.getPermissionManager();

					// Permission check
					if(permissions.has(player, "xpShop." + action)){
						// yay!
						return true;
					} 
					else 
					{
						// houston, we have a problem :)
						player.sendMessage(ChatColor.GRAY + "[xpShop]" + ChatColor.RED + (getConfig().getString("permissions.error." + getConfig().getString("language"))));
						return false;
					}
				} 
				else 
				{
					System.out.println("PermissionsEx plugin are not found.");
					return false;
				}
			}
		} //if (sender instanceof Player)
		else
		{
			System.out.println("[xpShop]" + (getConfig().getString("command.error.noplayer" + getConfig().getString("language"))));
			return false;
		}

	} //public boolean checkpermissions(CommandSender sender, String action)

	private static boolean packageExists(String[] packages)
	{
		try
		{
			for (String pkg : packages) {
				Class.forName(pkg);
			}
			return true; } catch (Exception e) {
			}
		return false;
	}

	/**
	 * Checks the config.yml wich moneyplugin should be used.
	 *
	 * @param 
	 * @return 1: Register 5: iConomy5 6: iConomy6
	 */
	public int iConomyversion()
	{
		try
		{
			if (packageExists(new String[] { "com.nijikokun.register.payment.Methods" }))
			{
				iConomyversion = 1;
				System.out.println("[xpShop] xpShop hooked into Register");
			}
			else
				if (packageExists(new String[] { "com.iCo6.system.Accounts", "me.ashtheking.currency.CurrencyList" })) 
				{
					iConomyversion = 6;
					System.out.println("[xpShop] xpShop hooked into iConomy6");
				}
				else
					if (packageExists(new String[] { "com.iConomy.iConomy", "com.iConomy.system.Account", "com.iConomy.system.Holdings" }))
					{
						iConomyversion = 5;
						System.out.println("[xpShop] xpShop hooked into iConomy5");
					}
					else
					{
						System.out.println("[xpShop] xpShop cant hook into iConomy5, iConomy6 or Register. Downloading Register!");
						try 
						{
							String path = "plugins/";
							Update.autoDownload("http://mirror.nexua.org/Register/latest/stable/Register.jar", path, "Register.jar");
						} catch (Exception e) {
							e.printStackTrace();
						}

					}
		}
		catch (Exception E)
		{
			E.printStackTrace();
			iConomyversion = 0;
		}
		return iConomyversion;
	}

	/**
	 * Gets Balance of a player.
	 *
	 * @param player, iConomyversion() must be performed before.
	 * @return Double balance.
	 */
	public Double getBalance156(Player player)
	{
		String name = player.getName();
		if(iConomyversion == 5)
		{
			try
			{
				if(hasAccount5(name) == true)
				{
					balance5 = getAccount5(name).getHoldings();
				}
			}
			catch (Exception E)
			{
				System.out.println("[xpShop]" + "No Account!");
				balance5 = null;
				return balance;
			}
			try
			{
				balance = (double) balance5.balance();
			}
			catch (Exception E)
			{
				System.out.println("[xpShop]" + "No Account!");
				balance5 = null;
				return balance;
			}
			return balance;
		}
		else if(iConomyversion == 6)
		{
			balance = new Accounts().get(player.getName()).getHoldings().getBalance();
		}
		else if(iConomyversion == 1)
		{
			balance = Methods.getMethod().getAccount(player.getName()).balance();
		}
		//		else if(iConomyversion == 2)
		//		{
		//            player.sendMessage(String.format("You have %s", vault.getEconomy().format(vault.getEconomy().getBalance(player.getName()).amount)));
		//            EconomyResponse r = vault.getEconomy().depositPlayer(player.getName(), 1.05);
		//            if(r.transactionSuccess()) {
		//                player.sendMessage(String.format("You were given %s and now have %s", vault.getEconomy().format(r.amount), vault.getEconomy().format(r.balance)));
		//            } else {
		//                player.sendMessage(String.format("An error occured: %s", r.errorMessage));
		//            }
		//		}
		return balance;
	}

	private Account getAccount5(String name) {
		return com.iConomy.iConomy.getAccount(name);
	}

	private boolean hasAccount5(String name) {
		return com.iConomy.iConomy.hasAccount(name);
	}


	/**
	 * Substracts money.
	 *
	 * @param player, amount
	 * @return 
	 */
	public void substractmoney156(double amountsubstract, Player player)
	{
		String name = player.getName();
		if(iConomyversion == 5)
		{
			getAccount5(name).getHoldings().subtract(amountsubstract);
		}
		else if(iConomyversion == 6)
		{
			com.iCo6.system.Account account = new Accounts().get(player.getName());
			account.getHoldings().subtract(amountsubstract);
		}
		else if(iConomyversion == 1)
		{
			Methods.getMethod().getAccount(player.getName()).subtract(amountsubstract);
		}
		return;
	}

	/**
	 * Adds money
	 *
	 * @param player, amount
	 * @return 
	 */
	public void addmoney156(double amountadd, Player player)
	{
		String name = player.getName();
		if(iConomyversion == 5)
		{
			getAccount5(name).getHoldings().add(amountadd);
		}
		else if(iConomyversion == 6)
		{
			com.iCo6.system.Account account = new Accounts().get(player.getName());
			account.getHoldings().add(amountadd);
		}
		else if(iConomyversion == 1)
		{
			Methods.getMethod().getAccount(player.getName()).add(amountadd);
		}
		return;
	}


	/**
	 * Buys level for a player.
	 *
	 * @param sender, amount, moneyactive = true if you want that player have to buy XP, false if there is an info what that would cost.
	 * @return 
	 */
	public void buylevel(CommandSender sender, int levelamontbuy, boolean moneyactive)
	{
		Player player = (Player) sender;
		int levelbuy = player.getLevel();
		while ((getBalance156(player) > (getConfig().getDouble("moneytoxp") + 0.2)) && ((player.getLevel() - levelbuy) <= levelamontbuy) || (player.getLevel() - levelbuy) <= levelamontbuy)
		{
			if(moneyactive == true)
			{
				if(buy(sender, 1, true, "buylevel"))
				{
					rounds1 = rounds1 + 1;
				}
			}
			else if (moneyactive == false)
			{
				if(buy(sender, 1, false, "buylevel"))
				{
					rounds1 = rounds1 + 1;
				}
			}
		}
		if(ActionxpShop.equalsIgnoreCase("buylevel"))
		{
			player.sendMessage(ChatColor.GRAY + "[xpShop]" + ChatColor.RED + String.format((getConfig().getString("command.success." + "buylevel" + "." + getConfig().getString("language"))), ((getConfig().getDouble("moneytoxp")) * rounds1), rounds1));
		}
		else if (ActionxpShop.equalsIgnoreCase("info"))
		{
			player.sendMessage(ChatColor.GRAY + "[xpShop]" + ChatColor.RED + String.format((getConfig().getString("info.prefix." + getConfig().getString("language"))) + " " + (getConfig().getString("command.success.buylevel" + "." + getConfig().getString("language"))), ((getConfig().getDouble("moneytoxp")) * rounds1), rounds1));
		}
	}

	/**
	 * Sells level from a player.
	 *
	 * @param sender, amount, moneyactive = true if you want that player have to buy XP, false if there is an info what that would cost.
	 * @return 
	 */
	public void selllevel(CommandSender sender, int levelamontsell, boolean moneyactive)
	{
		Player player = (Player) sender;
		if(player.getLevel() + player.getExp() <= 0.20)
		{
			player.sendMessage(ChatColor.GRAY + "[xpShop]" + ChatColor.RED + (getConfig().getString("command.error.notenoughxp." + getConfig().getString("language"))));
			return;
		}
		else
		{
			int levelsell = player.getLevel();
			while ((player.getLevel() + player.getExp() >= 0.14) && ((levelsell - player.getLevel()) <= levelamontsell))
			{
				if(moneyactive == true)
				{
					rounds = rounds + sell(sender, 1, true, "selllevel");
				}
				else if(moneyactive == false)
				{
					rounds = rounds + sell(sender, 1, false, "selllevel");
				}
			}
			if(ActionxpShop.equalsIgnoreCase("selllevel"))
			{
				player.sendMessage(ChatColor.GRAY + "[xpShop]" + ChatColor.RED + String.format((getConfig().getString("command.success." + "selllevel" + "." + getConfig().getString("language"))), rounds, (int) ((getConfig().getDouble("xptomoney")) * rounds)));
			}
			else if (ActionxpShop.equalsIgnoreCase("info"))
			{
				player.sendMessage(ChatColor.GRAY + "[xpShop]" + ChatColor.RED + String.format((getConfig().getString("info.prefix." + getConfig().getString("language"))) + " " + (getConfig().getString("command.success.selllevel" + "." + getConfig().getString("language"))), rounds, (int) ((getConfig().getDouble("xptomoney")) * rounds)));
			}
		}
	}

	/**
	 * Returns help.
	 *
	 * @param sender, action(String[])
	 * @return 
	 */
	public void help(CommandSender sender, String[] args)
	{
		Player player = (Player) sender;
		if(args.length == 0 || args.length >= 3)
		{
			player.sendMessage(ChatColor.GRAY + "[xpShop]" + ChatColor.RED + "/xpShop info <action> <amount>");
			player.sendMessage(ChatColor.GRAY + "[xpShop]" + ChatColor.RED + "/xpShop sell <amount>");
			player.sendMessage(ChatColor.GRAY + "[xpShop]" + ChatColor.RED + "/xpShop buy <money>");
			player.sendMessage(ChatColor.GRAY + "[xpShop]" + ChatColor.RED + "/xpShop buylevel <amount>");
			player.sendMessage(ChatColor.GRAY + "[xpShop]" + ChatColor.RED + "/xpShop selllevel <amount>");
			player.sendMessage(ChatColor.GRAY + "[xpShop]" + ChatColor.RED + "/xpShop send <player> <amount>");
		}
		else
			if(args.length == 2)
			{
				if(!(args[1].equals("buy") || args[1].equals("sell") || args[1].equals("selllevel") || args[1].equals("buylevel") || args[1].equals("send") || args[1].equals("info")))
				{
					player.sendMessage(ChatColor.GRAY + "[xpShop]" + ChatColor.RED + "/xpShop info <action> <amount>");
					player.sendMessage(ChatColor.GRAY + "[xpShop]" + ChatColor.RED + "/xpShop sell <amount>");
					player.sendMessage(ChatColor.GRAY + "[xpShop]" + ChatColor.RED + "/xpShop buy <money>");
					player.sendMessage(ChatColor.GRAY + "[xpShop]" + ChatColor.RED + "/xpShop buylevel <amount>");
					player.sendMessage(ChatColor.GRAY + "[xpShop]" + ChatColor.RED + "/xpShop selllevel <amount>");
					player.sendMessage(ChatColor.GRAY + "[xpShop]" + ChatColor.RED + "/xpShop send <player> <amount>");
				}
				else
					if (args[1].equals("buy"))
					{
						player.sendMessage(ChatColor.GRAY + "[xpShop]" + ChatColor.RED + "/xpShop buy <money>");
						player.sendMessage(ChatColor.GRAY + "[xpShop]" + ChatColor.RED + (getConfig().getString("help.buy" + "." + getConfig().getString("language"))));
					}
					else if (args[1].equals("buylevel"))
					{
						player.sendMessage(ChatColor.GRAY + "[xpShop]" + ChatColor.RED + "/xpShop buy <money>");
						player.sendMessage(ChatColor.GRAY + "[xpShop]" + ChatColor.RED + (getConfig().getString("help.buylevel" + "." + getConfig().getString("language"))));
					}
					else if (args[1].equals("sell"))
					{
						player.sendMessage(ChatColor.GRAY + "[xpShop]" + ChatColor.RED + "/xpShop sell <amount>");
						player.sendMessage(ChatColor.GRAY + "[xpShop]" + ChatColor.RED + (getConfig().getString("help.sell" + "." + getConfig().getString("language"))));
					}
					else if (args[1].equals("selllevel"))
					{
						player.sendMessage(ChatColor.GRAY + "[xpShop]" + ChatColor.RED + "/xpShop sell <amount>");
						player.sendMessage(ChatColor.GRAY + "[xpShop]" + ChatColor.RED + (getConfig().getString("help.selllevel" + "." + getConfig().getString("language"))));
					}
					else if (args[1].equals("info"))
					{
						player.sendMessage(ChatColor.GRAY + "[xpShop]" + ChatColor.RED + "/xpShop info <action> <amount>");
						player.sendMessage(ChatColor.GRAY + "[xpShop]" + ChatColor.RED + (getConfig().getString("help.info" + "." + getConfig().getString("language"))));
					}
					else if (args[1].equals("send"))
					{
						player.sendMessage(ChatColor.GRAY + "[xpShop]" + ChatColor.RED + "/xpShop send <player> <amount>");
						player.sendMessage(ChatColor.GRAY + "[xpShop]" + ChatColor.RED + (getConfig().getString("help.info" + "." + getConfig().getString("language"))));
					}
			}
			else
			{
				if(!(args[0].equals("buy") || args[0].equals("sell") || args[0].equals("selllevel") || args[0].equals("buylevel") || args[0].equals("send") || args[0].equals("info")))
				{
					player.sendMessage(ChatColor.GRAY + "[xpShop]" + ChatColor.RED + "/xpShop info <action> <amount>");
					player.sendMessage(ChatColor.GRAY + "[xpShop]" + ChatColor.RED + "/xpShop sell <amount>");
					player.sendMessage(ChatColor.GRAY + "[xpShop]" + ChatColor.RED + "/xpShop buy <money>");
					player.sendMessage(ChatColor.GRAY + "[xpShop]" + ChatColor.RED + "/xpShop buylevel <amount>");
					player.sendMessage(ChatColor.GRAY + "[xpShop]" + ChatColor.RED + "/xpShop selllevel <amount>");
					player.sendMessage(ChatColor.GRAY + "[xpShop]" + ChatColor.RED + "/xpShop send <player> <amount>");
				}
				else
					if (args[0].equals("buy"))
					{
						player.sendMessage(ChatColor.GRAY + "[xpShop]" + ChatColor.RED + "/xpShop buy <money>");
						player.sendMessage(ChatColor.GRAY + "[xpShop]" + ChatColor.RED + (getConfig().getString("help.buy" + "." + getConfig().getString("language"))));
					}
					else if (args[0].equals("buylevel"))
					{
						player.sendMessage(ChatColor.GRAY + "[xpShop]" + ChatColor.RED + "/xpShop buy <money>");
						player.sendMessage(ChatColor.GRAY + "[xpShop]" + ChatColor.RED + (getConfig().getString("help.buylevel" + "." + getConfig().getString("language"))));
					}
					else if (args[0].equals("sell"))
					{
						player.sendMessage(ChatColor.GRAY + "[xpShop]" + ChatColor.RED + "/xpShop sell <amount>");
						player.sendMessage(ChatColor.GRAY + "[xpShop]" + ChatColor.RED + (getConfig().getString("help.sell" + "." + getConfig().getString("language"))));
					}
					else if (args[0].equals("selllevel"))
					{
						player.sendMessage(ChatColor.GRAY + "[xpShop]" + ChatColor.RED + "/xpShop sell <amount>");
						player.sendMessage(ChatColor.GRAY + "[xpShop]" + ChatColor.RED + (getConfig().getString("help.selllevel" + "." + getConfig().getString("language"))));
					}
					else if (args[0].equals("info"))
					{
						player.sendMessage(ChatColor.GRAY + "[xpShop]" + ChatColor.RED + "/xpShop info <action> <amount>");
						player.sendMessage(ChatColor.GRAY + "[xpShop]" + ChatColor.RED + (getConfig().getString("help.info" + "." + getConfig().getString("language"))));
					}
					else if (args[0].equals("send"))
					{
						player.sendMessage(ChatColor.GRAY + "[xpShop]" + ChatColor.RED + "/xpShop send <player> <amount>");
						player.sendMessage(ChatColor.GRAY + "[xpShop]" + ChatColor.RED + (getConfig().getString("help.info" + "." + getConfig().getString("language"))));
					}
			}
	}


	/**
	 * Shows a player how much a action would cost.
	 *
	 * @param sender, action, amount.
	 * @return 
	 */
	public void info(CommandSender sender, String[] args)
	{
		if(args.length == 3 && sender instanceof Player)
		{
			Player player = (Player)sender;
			int nowlevel = player.getLevel();
			float nowxp = player.getExp();
			int temp = Integer.parseInt(args[2]);
			if (args[1].equals("buy"))
			{
				buy(player, temp, false, "info");
			}
			else
				if (args[1].equals("sell"))
				{
					sell(player, temp, false, "info");
				}
				else
					if (args[1].equals("buylevel"))
					{
						buylevel(player, temp, false);
					}
					else
						if (args[1].equals("selllevel"))
						{
							selllevel(player, temp, false);
						}
						else
							if (args[1].equals("send"))
							{
								player.sendMessage(ChatColor.GRAY + "[xpShop]" + ChatColor.RED + "There is no info for (send)!");
							}
			player.setLevel(nowlevel);
			player.setExp(nowxp);
		}
		else
		{
			help(sender, args);
			return;
		}
	}
}