package com.hert.referralplugin;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class InvitedCommand implements CommandExecutor {

    private final ReferralPlugin plugin;

    public InvitedCommand(ReferralPlugin plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (sender instanceof Player) {
            Player player = (Player) sender;
            int invitedCount = plugin.getDatabaseManager().getInvitedCount(player.getName());
            String invitedPlayers = plugin.getDatabaseManager().getInvitedPlayers(player.getName());
            player.sendMessage(plugin.getMessage("invitedCount", invitedCount, invitedPlayers));
        } else {
            sender.sendMessage(plugin.getMessage("commandOnlyPlayers"));
        }
        return true;
    }
}
