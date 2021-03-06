package com.xenixi.conquermc.plugins.reports;

import java.util.logging.Logger;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

public class Reports extends JavaPlugin{
	Logger l = null;
	@Override
	public void onEnable() {
		l = Bukkit.getServer().getLogger();
		l.info(ChatColor.translateAlternateColorCodes('&',"&aEnabling: &dReportsPlugin &6by &4_ThisOrThat_&f" ));
		
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
			
			if(cmd.getName().equalsIgnoreCase("report") && (sender.hasPermission("reports.receive") || sender instanceof ConsoleCommandSender)) {
				if(args.length<2) {
					sender.sendMessage(ChatColor.translateAlternateColorCodes('&', "&dReports: &4Invalid Command. Syntax: /report <player> <reason>&f"));
					return false;
				}
				if(getServer().getPlayer(args[0]) == null && !(args[0].equalsIgnoreCase("-o"))) {
					sender.sendMessage(ChatColor.translateAlternateColorCodes('&', "&dReports: &4Error: Player not found. Player offline? Try /report -o <player> <reason>&f"));
					return false;
				}
				
				
				StringBuilder sb = new StringBuilder();
				String playerName = args[0].equalsIgnoreCase("-o") ? args[1] : args[0];
				for(int i = args[0].equalsIgnoreCase("-o") ? 2 : 1; i < args.length; i++) {
					sb.append(args[i]);
					sb.append(" ");
				}
			
				String reason = sb.toString();
				
				for(Player p : getServer().getOnlinePlayers()) {
					if(p.hasPermission("reports.receive")) {
						p.sendMessage(ChatColor.translateAlternateColorCodes('&', "&4A report has been filed for player &7" + playerName + " &4for reason: &c" + reason));
					}
				}
				sender.sendMessage(ChatColor.translateAlternateColorCodes('&', "&dReports: &4Report filed for player &7" + playerName + "&f"));
				
			}
		
		
		return false;
	}
}
