package me.MnMaxon.OnlineLevel;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Calendar;

import org.bukkit.ChatColor;
import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;

public class PlayerListener implements Listener {
	private Main plugin;

	public PlayerListener(Main main) {
		plugin = main;
	}

	@EventHandler
	public void onJoin(PlayerJoinEvent e) {
		if (!new File(Main.dataFolder + "/Players.yml").exists())
			try {
				new File(Main.dataFolder + "/Players.yml").createNewFile();
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
		pCfg.set(e.getPlayer().getName() + ".Time", time);
		try {
			pCfg.save(new File(Main.dataFolder + "/Players.yml"));
		} catch (IOException e1) {
			e1.printStackTrace();
		}
	}

	@EventHandler
	public void onLeave(PlayerQuitEvent e) {
		if (!new File(Main.dataFolder + "/Players.yml").exists())
			try {
				new File(Main.dataFolder + "/Players.yml").createNewFile();
			} catch (IOException e1) {
				e1.printStackTrace();
			}
		YamlConfiguration pCfg = new YamlConfiguration();
		try {
			pCfg.load(new File(Main.dataFolder + "/Players.yml"));
		} catch (IOException | InvalidConfigurationException e1) {
			e1.printStackTrace();
		}
		Main.updateTime(e.getPlayer().getName());
		pCfg.set(e.getPlayer().getName() + ".Time", null);
		try {
			pCfg.save(new File(Main.dataFolder + "/Players.yml"));
		} catch (IOException e1) {
			e1.printStackTrace();
		}
	}

	@EventHandler
	public void onTalk(AsyncPlayerChatEvent e) {
		plugin.reloadConfig();
		Main.updateTime(e.getPlayer().getName());
		if (!new File(Main.dataFolder + "/Players.yml").exists())
			try {
				new File(Main.dataFolder + "/Players.yml").createNewFile();
			} catch (IOException e1) {
				e1.printStackTrace();
			}
		YamlConfiguration pCfg = new YamlConfiguration();
		try {
			pCfg.load(new File(Main.dataFolder + "/Players.yml"));
		} catch (IOException | InvalidConfigurationException e1) {
			e1.printStackTrace();
		}
		int level = Main.getLevel(e.getPlayer().getName());
		if (plugin.getConfig().get("Format") == null)
			plugin.getConfig().set("Format", "&f[&3Level &#&f]");
		e.setFormat(plugin.getConfig().getString("Format").replaceAll("&0", ChatColor.BLACK + "")
				.replaceAll("&1", ChatColor.DARK_BLUE + "").replaceAll("&2", ChatColor.DARK_GREEN + "")
				.replaceAll("&3", ChatColor.DARK_AQUA + "").replaceAll("&4", ChatColor.DARK_RED + "")
				.replaceAll("&5", ChatColor.DARK_PURPLE + "").replaceAll("&6", ChatColor.GOLD + "")
				.replaceAll("&7", ChatColor.GRAY + "").replaceAll("&8", ChatColor.DARK_GRAY + "")
				.replaceAll("&9", ChatColor.BLUE + "").replaceAll("&a", ChatColor.GREEN + "")
				.replaceAll("&b", ChatColor.AQUA + "").replaceAll("&c", ChatColor.RED + "")
				.replaceAll("&d", ChatColor.LIGHT_PURPLE + "").replaceAll("&e", ChatColor.YELLOW + "")
				.replaceAll("&f", ChatColor.WHITE + "").replaceAll("&#", level + "")
				+ e.getFormat());
	}
}
