package com.hert.referralplugin;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class RegisterCommand implements CommandExecutor {

    private final ReferralPlugin plugin;

    public RegisterCommand(ReferralPlugin plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (sender instanceof Player) {
            Player player = (Player) sender;
            if (args.length != 1) {
                player.sendMessage(plugin.getMessage("usageRegister"));
                return false;
            }
            String referralCode = args[0];
            if (plugin.getDatabaseManager().isReferralCodeValid(referralCode)) {
                boolean success = plugin.getDatabaseManager().registerReferral(player.getName(), referralCode);
                if (success) {
                    player.sendMessage(plugin.getMessage("referralSuccess", referralCode));
                } else {
                    player.sendMessage(plugin.getMessage("referralAlreadyRegistered"));
                }
            } else {
                player.sendMessage(plugin.getMessage("referralCodeInvalid"));
            }
        } else {
            sender.sendMessage(plugin.getMessage("commandOnlyPlayers"));
        }
        return true;
    }
}
