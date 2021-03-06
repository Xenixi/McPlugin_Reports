package com.xenixi.conquermc.plugins.reports;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;
import java.util.logging.Logger;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

public class Reports extends JavaPlugin {
	Logger l = null;
	public static File reportsFile;

	@Override
	public void onEnable() {
		saveResource("reports.lst", false);
		reportsFile = new File(getDataFolder(), "reports.lst");
		l = Bukkit.getServer().getLogger();
		l.info(ChatColor.translateAlternateColorCodes('&', "&aEnabling: &dReportsPlugin &6by &4_ThisOrThat_&f"));

	}

	@Override
	public void onDisable() {
		l.info("Disabling: ReportsPlugin");
	}

	public void logConsole(String info) {
		l.info(ChatColor.translateAlternateColorCodes('&', "&dReportsPlugin: &6" + info));
	}

	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {

		if (cmd.getName().equalsIgnoreCase("report")
				&& (sender.hasPermission("reports.send") || sender instanceof ConsoleCommandSender)) {
			if (args.length < 2) {
				sender.sendMessage(ChatColor.translateAlternateColorCodes('&',
						"&dReports: &4Invalid Command. Syntax: /report <player> <reason>&f"));
				return false;
			}
			if (getServer().getPlayer(args[0]) == null && !(args[0].equalsIgnoreCase("-o"))) {
				sender.sendMessage(ChatColor.translateAlternateColorCodes('&',
						"&dReports: &4Error: Player not found. Player offline? Try /report -o <player> <reason>&f"));
				return false;
			}

			StringBuilder sb = new StringBuilder();
			String playerName = args[0].equalsIgnoreCase("-o") ? args[1] : args[0];
			boolean offlineOverride = args[0].equalsIgnoreCase("-o");

			for (int i = args[0].equalsIgnoreCase("-o") ? 2 : 1; i < args.length; i++) {
				sb.append(args[i]);
				sb.append(" ");
			}

			String reason = sb.toString();

			for (Player p : getServer().getOnlinePlayers()) {
				if (p.hasPermission("reports.receive")) {
					p.sendMessage(ChatColor.translateAlternateColorCodes('&',
							"&d(!!) &4A report has been filed for player &7" + (offlineOverride ? "&a$&7" : "")
									+ playerName + " &4for reason: &c' " + reason + "'&4 by &a" + sender.getName()));
					addReport(sender.getName(), playerName, reason + (offlineOverride ? "&e**POSSIBLY OFFLINE**" : ""));
				}
			}
			sender.sendMessage(ChatColor.translateAlternateColorCodes('&',
					"&dReports: &4Report filed for player &7" + playerName + "&f"));
			return true;
		} else if (cmd.getName().equalsIgnoreCase("reports")
				&& (sender.hasPermission("reports.receive") || sender instanceof ConsoleCommandSender)) {
			sender.sendMessage(ChatColor.translateAlternateColorCodes('&', "&dShowing Reports:\n"));
			sender.sendMessage(ChatColor.translateAlternateColorCodes('&', retrieveReportsString()));
			return true;
		} else if (cmd.getName().equalsIgnoreCase("resolve")
				&& (sender.hasPermission("reports.receive") || sender instanceof ConsoleCommandSender)) {
			if (args.length < 1) {
				return false;
			}
			String toResolve = args[0];
			if (removeReports(toResolve)) {
				sender.sendMessage(
						ChatColor.translateAlternateColorCodes('&', "&cReports for &7" + toResolve + "&c Removed."));
				return true;
			} else {
				sender.sendMessage(ChatColor.translateAlternateColorCodes('&',
						"&4Error: No reports for &7" + toResolve + "&4 exist."));
			}

		}

		return false;
	}

	public boolean removeReports(String reported) {
		List<String> preexisting = getReportData();
		int removed = 0;
		for (int i = 0; i < preexisting.size(); i++) {
			if (preexisting.get(i).split(",")[1].equalsIgnoreCase(reported)) {
				preexisting.remove(i);
				removed++;
			}
		}
		if (removed > 0) {
			try {
				PrintWriter pw = new PrintWriter(reportsFile);
				for (String s : preexisting) {
					pw.println(s);
				}
				pw.flush();
				pw.close();
				return true;
			} catch (FileNotFoundException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}

		return false;
	}

	public List<String> getReportData() {
		Scanner s;
		try {

			s = new Scanner(reportsFile);

			List<String> readData = new ArrayList<String>();
			while (s.hasNextLine()) {
				readData.add(s.nextLine());
			}

			s.close();
			return readData;

		} catch (FileNotFoundException e) {
			l.info("Reports: Failed to read reports file.");
			e.printStackTrace();
			return null;
		}

	}

	public void addReport(String player, String reported, String reason) {
		String lineToWrite = (player + "," + reported + "," + reason);

		List<String> preexisting = getReportData();

		preexisting.add(lineToWrite);
		try {
			PrintWriter pw = new PrintWriter(reportsFile);
			for (String s : preexisting) {
				pw.println(s);
			}
			pw.flush();
			pw.close();
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

	public String retrieveReportsString() {
		List<String> reportsRead = getReportData();

		StringBuilder sb = new StringBuilder();
		sb.append("&d----------\n");
		for (String s : reportsRead) {
			String[] values = s.split(",");
			String player = values[0], reported = values[1], reason = values[2];
			sb.append("   ");
			sb.append("\n&dReport for &7" + reported + " &dfor reason: &c' " + reason + "'&d by &7" + player + "\n");

		}
		if(reportsRead.size() < 1) {
			sb.append("&4No Reports.");
		}
		return sb.toString();
	}
}
