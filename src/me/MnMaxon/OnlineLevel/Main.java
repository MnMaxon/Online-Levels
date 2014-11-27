package me.MnMaxon.OnlineLevel;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Calendar;

import net.milkbowl.vault.economy.Economy;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.RegisteredServiceProvider;
import org.bukkit.plugin.java.JavaPlugin;

public final class Main extends JavaPlugin {
	public static String dataFolder;
	Boolean on;
	public static Economy money = null;

	@Override
	public void onEnable() {
		on = false;
		getServer().getPluginManager().registerEvents(new PlayerListener(this), this);
		dataFolder = this.getDataFolder().getAbsolutePath();
		YamlConfiguration exampleCfg = new YamlConfiguration();
		setupMoney();
		try {
			exampleCfg.load(new File(dataFolder + "/Example Config.yml"));
		} catch (IOException | InvalidConfigurationException e1) {
			e1.printStackTrace();
		}
		exampleCfg.set("Level.1.Time", 5);
		exampleCfg.set("Level.1.Money Reward", 100);
		exampleCfg.set("Level.1.Item Reward Name", "DIAMOND");
		exampleCfg.set("Level.1.Item Reward Amount", 0);
		exampleCfg.set("Level.2.Time", 10);
		exampleCfg.set("Level.2.Money Reward", 0);
		exampleCfg.set("Level.2.Item Reward Name", "DIAMOND");
		exampleCfg.set("Level.2.Item Reward Amount", 1);
		exampleCfg.set("Level.3.Time", 20);
		exampleCfg.set("Level.3.Money Reward", 100);
		exampleCfg.set("Level.3.Item Reward Name", "DIAMOND");
		exampleCfg.set("Level.3.Item Reward Amount", 1);
		try {
			exampleCfg.save(new File(Main.dataFolder + "/Example Config.yml"));
		} catch (IOException e1) {
			e1.printStackTrace();
		}
		if (this.getConfig().get("BracketColor") != null)
			this.getConfig().set("BracketColor", null);
		if (this.getConfig().get("Color") != null)
			this.getConfig().set("Color", null);
		if (this.getConfig().get("Format") == null)
			this.getConfig().set("Format", "&f[&3Level &#&f]");
		if (this.getConfig().get("Level." + 1) == null) {
			this.getConfig().set("Level." + 1 + ".Time", 5);
			this.getConfig().set("Level." + 1 + ".Money Reward", 100);
			this.getConfig().set("Level." + 1 + ".Item Reward Name", "DIAMOND");
			this.getConfig().set("Level." + 1 + ".Item Reward Amount", 0);
		}
		if (this.getConfig().get("Level." + 2) == null) {
			this.getConfig().set("Level." + 2 + ".Time", 10);
			this.getConfig().set("Level." + 2 + ".Money Reward", 0);
			this.getConfig().set("Level." + 2 + ".Item Reward Name", "DIAMOND");
			this.getConfig().set("Level." + 2 + ".Item Reward Amount", 1);
		}
		if (this.getConfig().get("Level." + 3) == null) {
			this.getConfig().set("Level." + 3 + ".Time", 20);
			this.getConfig().set("Level." + 3 + ".Money Reward", 100);
			this.getConfig().set("Level." + 3 + ".Item Reward Name", "DIAMOND");
			this.getConfig().set("Level." + 3 + ".Item Reward Amount", 1);
		}
		try {
			this.getConfig().save(Main.dataFolder + "/Config.yml");
		} catch (IOException e1) {
			e1.printStackTrace();
		}
		new Thread(new Runnable() {
			public void run() {
				while (on) {
					if (getServer().getOnlinePlayers().length != 0)
						for (int i = 0; i < getServer().getOnlinePlayers().length; i++)
							updateTime(Arrays.asList(Bukkit.getServer().getOnlinePlayers()).get(i).getName());
					try {
						Thread.sleep(60 * 1000);
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
				}
			}
		}).start();
	}

	private void setupMoney() {
		RegisteredServiceProvider<Economy> economyProvider = getServer().getServicesManager().getRegistration(
				net.milkbowl.vault.economy.Economy.class);
		if (economyProvider != null) {
			money = economyProvider.getProvider();
		}
	}

	@Override
	public void onDisable() {
		for (int i = 0; i < getServer().getOnlinePlayers().length; i++) {
			updateTime(Arrays.asList(Bukkit.getServer().getOnlinePlayers()).get(i).getName());
		}
		on = false;
	}

	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
		return false;
	}

	@SuppressWarnings("deprecation")
	public static void updateTime(String name) {
		if (!new File(Main.dataFolder + "/Players.yml").exists())
			try {
				new File(Main.dataFolder + "/Players.yml").createNewFile();
			} catch (IOException e1) {
				e1.printStackTrace();
			}
		if (!new File(Main.dataFolder + "/Example Config.yml").exists())
			try {
				new File(Main.dataFolder + "/Example Config.yml").createNewFile();
			} catch (IOException e1) {
				e1.printStackTrace();
			}
		YamlConfiguration pCfg = new YamlConfiguration();
		try {
			pCfg.load(new File(Main.dataFolder + "/Players.yml"));
		} catch (IOException | InvalidConfigurationException e1) {
			e1.printStackTrace();
		}
		int time = (Integer.parseInt(new SimpleDateFormat("HH").format(Calendar.getInstance().getTime())) * 60)
				+ Integer.parseInt(new SimpleDateFormat("mm").format(Calendar.getInstance().getTime()));
		if (pCfg.getInt(name + ".Time") - time < 60) {
			if (pCfg.get(name + ".TimeOnline") == null)
				pCfg.set(name + ".TimeOnline", 0);
			if (pCfg.get(name + ".Time") == null)
				pCfg.set(name + ".Time", time);
			if (pCfg.get(name + ".Level") == null)
				pCfg.set(name + ".Level", getLevel(name));
			int initialLevel = pCfg.getInt(name + ".Level");
			int finalLevel = getLevel(name);
			pCfg.set(name + ".TimeOnline", pCfg.getInt(name + ".TimeOnline") + time - pCfg.getInt(name + ".Time"));
			pCfg.set(name + ".Time", time);
			pCfg.set(name + ".Level", finalLevel);
			try {
				pCfg.save(new File(Main.dataFolder + "/Players.yml"));
			} catch (IOException e) {
				e.printStackTrace();
			}
			if (finalLevel - initialLevel != 0) {
				giveReward(Bukkit.getServer().getPlayer(name));
			}
		}
	}

	private static void giveReward(Player p) {
		YamlConfiguration cfg = new YamlConfiguration();
		try {
			cfg.load(new File(Main.dataFolder + "/Config.yml"));
		} catch (IOException | InvalidConfigurationException e1) {
			e1.printStackTrace();
		}
		int level = getLevel(p.getName());
		if (level == 0)
			return;
		p.sendMessage(ChatColor.DARK_GREEN + "LEVEL UP!");
		if (cfg.get("Level." + level + ".Money Reward") == null)
			cfg.set("Level." + level + ".Money Reward", 0);
		if (cfg.get("Level." + level + ".Item Reward Name") == null)
			cfg.set("Level." + level + ".Item Reward Name", "DIAMOND");
		if (cfg.get("Level." + level + ".Item Reward Amount") == null)
			cfg.set("Level." + level + ".Item Reward Amount", 0);
		try {
			cfg.save(new File(Main.dataFolder + "/Config.yml"));
		} catch (IOException e) {
			e.printStackTrace();
		}
		money.depositPlayer(p.getName(), cfg.getInt("Level." + level + ".Money Reward"));
		if (cfg.getInt("Level." + level + ".Item Reward Amount") != 0) {
			Boolean done = false;
			int rewardAmount = cfg.getInt("Level." + level + ".Item Reward Amount");
			while (!done) {
				ItemStack item = new ItemStack(Material.matchMaterial(cfg.getString("Level." + level
						+ ".Item Reward Name")));
				if (rewardAmount >= 64) {
					item.setAmount(64);
					rewardAmount = rewardAmount - 64;
				} else
					done = true;
				p.getInventory().addItem(item);
			}
		}
	}

	public static ChatColor Color(String color) {
		if (color.equalsIgnoreCase("aqua"))
			return ChatColor.AQUA;
		if (color.equalsIgnoreCase("BLACK"))
			return ChatColor.BLACK;
		if (color.equalsIgnoreCase("BLUE"))
			return ChatColor.BLUE;
		if (color.equalsIgnoreCase("DARK_AQUA"))
			return ChatColor.DARK_AQUA;
		if (color.equalsIgnoreCase("DARK_BLUE"))
			return ChatColor.DARK_BLUE;
		if (color.equalsIgnoreCase("DARK_GRAY"))
			return ChatColor.DARK_GRAY;
		if (color.equalsIgnoreCase("DARK_GREEN"))
			return ChatColor.DARK_GREEN;
		if (color.equalsIgnoreCase("DARK_PURPLE"))
			return ChatColor.DARK_PURPLE;
		if (color.equalsIgnoreCase("DARK_RED"))
			return ChatColor.DARK_RED;
		if (color.equalsIgnoreCase("GOLD"))
			return ChatColor.GOLD;
		if (color.equalsIgnoreCase("GRAY"))
			return ChatColor.GRAY;
		if (color.equalsIgnoreCase("GREEN"))
			return ChatColor.GREEN;
		if (color.equalsIgnoreCase("LIGHT_PURPLE"))
			return ChatColor.LIGHT_PURPLE;
		if (color.equalsIgnoreCase("RED"))
			return ChatColor.RED;
		if (color.equalsIgnoreCase("WHITE"))
			return ChatColor.WHITE;
		if (color.equalsIgnoreCase("YELLOW"))
			return ChatColor.YELLOW;
		return ChatColor.WHITE;
	}

	public static int getLevel(String name) {
		int level = 0;
		YamlConfiguration pCfg = new YamlConfiguration();
		YamlConfiguration cfg = new YamlConfiguration();
		try {
			pCfg.load(new File(Main.dataFolder + "/Players.yml"));
		} catch (IOException | InvalidConfigurationException e1) {
			e1.printStackTrace();
		}
		try {
			cfg.load(new File(Main.dataFolder + "/Config.yml"));
		} catch (IOException | InvalidConfigurationException e1) {
			e1.printStackTrace();
		}
		for (int i = 1; cfg.get("Level." + i) != null; i++) {
			if (pCfg.getInt(name + ".TimeOnline") >= cfg.getInt("Level." + i + ".Time"))
				level = i;
		}
		return level;
	}
}