package com.hert.referralplugin;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.UUID;

public class DatabaseManager {

    private final ReferralPlugin plugin;
    private Connection connection;

    public DatabaseManager(ReferralPlugin plugin) {
        this.plugin = plugin;
    }

    public void connect() {
        try {
            connection = DriverManager.getConnection("jdbc:sqlite:referralplugin.db");
            String createTableSQL = "CREATE TABLE IF NOT EXISTS referrals (player TEXT PRIMARY KEY, referral_code TEXT)";
            try (PreparedStatement stmt = connection.prepareStatement(createTableSQL)) {
                stmt.execute();
            }
        } catch (SQLException e) {
            plugin.getLogger().severe("Errore di connessione al database: " + e.getMessage());
        }
    }

    public void disconnect() {
        try {
            if (connection != null && !connection.isClosed()) {
                connection.close();
            }
        } catch (SQLException e) {
            plugin.getLogger().severe("Errore durante la disconnessione dal database: " + e.getMessage());
        }
    }

    public String getOrGenerateReferralCode(String playerName) {
        String referralCode = getReferralCode(playerName);
        if (referralCode == null) {
            referralCode = generateUniqueReferralCode();
            registerReferral(playerName, referralCode);
        }
        return referralCode;
    }

    public String getReferralCode(String playerName) {
        String query = "SELECT referral_code FROM referrals WHERE player = ?";
        try (PreparedStatement stmt = connection.prepareStatement(query)) {
            stmt.setString(1, playerName);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                return rs.getString("referral_code");
            }
        } catch (SQLException e) {
            plugin.getLogger().severe("Errore durante il recupero del codice referral: " + e.getMessage());
        }
        return null;
    }

    public String generateUniqueReferralCode() {
        String code;
        do {
            code = generateRandomCode();
        } while (codeExists(code));
        return code;
    }

    private String generateRandomCode() {
        // Genera un codice di 8 caratteri alfanumerici casuali
        return UUID.randomUUID().toString().replaceAll("[^a-zA-Z0-9]", "").substring(0, 8);
    }

    private boolean codeExists(String code) {
        String query = "SELECT COUNT(*) FROM referrals WHERE referral_code = ?";
        try (PreparedStatement stmt = connection.prepareStatement(query)) {
            stmt.setString(1, code);
            ResultSet rs = stmt.executeQuery();
            return rs.getInt(1) > 0;
        } catch (SQLException e) {
            plugin.getLogger().severe("Errore durante il controllo dell'esistenza del codice referral: " + e.getMessage());
        }
        return false;
    }

    public boolean registerReferral(String playerName, String referralCode) {
        String query = "INSERT OR REPLACE INTO referrals (player, referral_code) VALUES (?, ?)";
        try (PreparedStatement stmt = connection.prepareStatement(query)) {
            stmt.setString(1, playerName);
            stmt.setString(2, referralCode);
            stmt.executeUpdate();
            return true;
        } catch (SQLException e) {
            plugin.getLogger().severe("Errore durante la registrazione del codice referral: " + e.getMessage());
        }
        return false;
    }

    public boolean isReferralCodeValid(String referralCode) {
        String query = "SELECT COUNT(*) FROM referrals WHERE referral_code = ?";
        try (PreparedStatement stmt = connection.prepareStatement(query)) {
            stmt.setString(1, referralCode);
            ResultSet rs = stmt.executeQuery();
            return rs.getInt(1) > 0;
        } catch (SQLException e) {
            plugin.getLogger().severe("Errore durante la verifica del codice referral: " + e.getMessage());
        }
        return false;
    }

    public int getInvitedCount(String playerName) {
        String query = "SELECT COUNT(*) FROM referrals WHERE referral_code IN (SELECT referral_code FROM referrals WHERE player = ?)";
        try (PreparedStatement stmt = connection.prepareStatement(query)) {
            stmt.setString(1, playerName);
            ResultSet rs = stmt.executeQuery();
            return rs.getInt(1);
        } catch (SQLException e) {
            plugin.getLogger().severe("Errore durante il recupero del numero di invitati: " + e.getMessage());
        }
        return 0;
    }

    public String getInvitedPlayers(String playerName) {
        StringBuilder invitedPlayers = new StringBuilder();
        String query = "SELECT player FROM referrals WHERE referral_code IN (SELECT referral_code FROM referrals WHERE player = ?)";
        try (PreparedStatement stmt = connection.prepareStatement(query)) {
            stmt.setString(1, playerName);
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                if (invitedPlayers.length() > 0) {
                    invitedPlayers.append(", ");
                }
                invitedPlayers.append(rs.getString("player"));
            }
        } catch (SQLException e) {
            plugin.getLogger().severe("Errore durante il recupero dei giocatori invitati: " + e.getMessage());
        }
        return invitedPlayers.toString();
    }
}
