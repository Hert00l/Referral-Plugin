package com.hert.referralplugin;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class ReferralCommand implements CommandExecutor {

    private final ReferralPlugin plugin;

    public ReferralCommand(ReferralPlugin plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (sender instanceof Player) {
            Player player = (Player) sender;
            // Recupera o genera il codice referral
            String referralCode = plugin.getDatabaseManager().getOrGenerateReferralCode(player.getName());
            player.sendMessage(plugin.getMessage("referralCode", referralCode));
        } else {
            sender.sendMessage(plugin.getMessage("commandOnlyPlayers"));
        }
        return true;
    }
}
