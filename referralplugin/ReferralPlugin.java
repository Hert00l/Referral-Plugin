package com.hert.referralplugin;

import org.bukkit.plugin.java.JavaPlugin;

public class ReferralPlugin extends JavaPlugin {

    private DatabaseManager databaseManager;

    @Override
    public void onEnable() {
        // Carica la configurazione
        saveDefaultConfig();

        // Inizializza il DatabaseManager
        databaseManager = new DatabaseManager(this);
        databaseManager.connect();

        // Registrazione dei comandi
        getCommand("mioinvito").setExecutor(new ReferralCommand(this));
        getCommand("invitati").setExecutor(new InvitedCommand(this));
        getCommand("registra").setExecutor(new RegisterCommand(this));
    }

    @Override
    public void onDisable() {
        // Disconnessione dal database
        databaseManager.disconnect();
    }

    public DatabaseManager getDatabaseManager() {
        return databaseManager;
    }

    public String getMessage(String key, Object... args) {
        String message = getConfig().getString("messages." + key);
        if (message == null) {
            message = "Messaggio non trovato per la chiave: " + key;
        }
        return String.format(message, args);
    }
}
