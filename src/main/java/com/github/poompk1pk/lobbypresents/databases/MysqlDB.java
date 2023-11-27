package com.github.poompk1pk.lobbypresents.databases;

import com.github.poompk1pk.lobbypresents.Main;
import com.github.poompk1pk.lobbypresents.utils.PresentsUtils;
import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import java.sql.*;
import java.util.UUID;

public class MysqlDB implements LobbyPresentsDataManager {

    private String host;

    private String port;

    private String database;

    private String username;

    private String password;

    //private static Connection con;
    private HikariDataSource dataSource;

    private String tb_name;
    public String getTb_name() {
        return tb_name;
    }

    @Override
    public boolean initialData() {
        host = Main.getInstance().getConfig().getString("MYSQL.host");
        port = Main.getInstance().getConfig().getString("MYSQL.port");
        database = Main.getInstance().getConfig().getString("MYSQL.database");
        username = Main.getInstance().getConfig().getString("MYSQL.username");
        password = Main.getInstance().getConfig().getString("MYSQL.password");
        tb_name = Main.getInstance().getConfig().getString("MYSQL.table_name");

        PresentsUtils.chat((CommandSender) Bukkit.getConsoleSender(), "&a Loaded: mysql");
        HikariConfig hikariConfig = new HikariConfig();
        hikariConfig.setJdbcUrl("jdbc:mysql://" + host + ":" + port + "/" + database +
                "?useSSL=false&serverTimezone=UTC&characterEncoding=UTF-8&autoReconnect=true");
        hikariConfig.setDriverClassName("com.mysql.cj.jdbc.Driver"); // Updated driver class name
        hikariConfig.setUsername(username);
        hikariConfig.setPassword(password);
        hikariConfig.addDataSourceProperty("cachePrepStmts", "true");
        hikariConfig.addDataSourceProperty("prepStmtCacheSize", "250");
        hikariConfig.addDataSourceProperty("prepStmtCacheSqlLimit", "2048");

        // Additional properties for MySQL 8
        hikariConfig.addDataSourceProperty("useUnicode", "true");
        hikariConfig.addDataSourceProperty("useJDBCCompliantTimezoneShift", "true");
        hikariConfig.addDataSourceProperty("useLegacyDatetimeCode", "false");
        hikariConfig.addDataSourceProperty("serverTimezone", "UTC");

        // HikariCP setup
        dataSource = new HikariDataSource(hikariConfig);

        if(isHikariCPConnected()) {
            dataSource.close();
            dataSource = null;
        }
        dataSource = new HikariDataSource(hikariConfig);


        try (Connection con = dataSource.getConnection()) {
            String sql = "CREATE TABLE IF NOT EXISTS " + tb_name +
                    "(id INTEGER PRIMARY KEY AUTO_INCREMENT, uuid VARCHAR(50), claimed VARCHAR(250));";
            try (PreparedStatement stmt = con.prepareStatement(sql)) {
                stmt.executeUpdate();
                for (Player pls : Bukkit.getOnlinePlayers()) {
                    insertDataDefault(pls.getUniqueId());
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }
            return true;
        } catch (SQLException e) {
            e.printStackTrace();
            Main.color("&aLobbyPresents: &cDatabase connection failed!");
            Main.color("&aLobbyPresents: &cPlease check! host port databasename username password in &eConfig.yml! &cand restart your server");
            return false;
        }
    }
    public void clearTable(String tableName) {
        try (Connection connection = dataSource.getConnection()) {
            String deleteQuery = "DELETE FROM " + tableName;

            try (PreparedStatement preparedStatement = connection.prepareStatement(deleteQuery)) {
                preparedStatement.executeUpdate();
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
    public void insertDataDefault(UUID uuid) {
        String sqlSelect = "SELECT * FROM " + tb_name + " WHERE uuid = ?";
        String sqlInsert = "INSERT INTO " + tb_name + "(uuid, claimed) VALUES (?, '')";

        try (Connection connection = dataSource.getConnection();
             PreparedStatement stmtSelect = connection.prepareStatement(sqlSelect)) {

            stmtSelect.setString(1, uuid.toString());

            try (ResultSet results = stmtSelect.executeQuery()) {
                if (!results.next()) {
                    try (PreparedStatement stmtInsert = connection.prepareStatement(sqlInsert)) {
                        stmtInsert.setString(1, uuid.toString());
                        stmtInsert.executeUpdate();
                    }
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
    public void update(String qry) {
        try (Connection connection = dataSource.getConnection();
             Statement stmt = connection.createStatement()) {

            stmt.executeUpdate(qry);
        } catch (SQLException e) {
            handleSQLException(e);
        }
    }
    public boolean isHikariCPConnected() {
        if (dataSource != null) {
            return !dataSource.isClosed();
        }
        return false;
    }

    private static void handleSQLException(SQLException ex) {
        ex.printStackTrace();
    }

    @Override
    public void clearAllData(CommandSender sender) {

        new BukkitRunnable() {
            @Override
            public void run() {
                try {
                    clearTable(tb_name);
                    for (Player pls : Bukkit.getOnlinePlayers()) {
                        insertDataDefault(pls.getUniqueId());
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }.runTaskAsynchronously(Main.getInstance());

    }
    public String getClaimedDB(UUID uuid) {
        String Claimed = "";
        try (Connection connection = dataSource.getConnection();
             Statement stmt = connection.createStatement();
             ResultSet rs = stmt.executeQuery("SELECT `claimed` FROM `" + tb_name + "` WHERE `uuid`='" + uuid + "'")) {

            while (rs.next()) {
                Claimed = rs.getString(1);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return Claimed;
    }

    @Override
    public String getClaimed(UUID uuid) {
        return getClaimedDB(uuid);
    }

    @Override
    public void updateClaim(UUID uuid, String data) {
        update("UPDATE `" + tb_name + "` SET `claimed`='" + data + "' WHERE `uuid`='" + uuid + "'");
    }

    @Override
    public boolean insertDefaultData(UUID uuid) {
        insertDataDefault(uuid);
        return true;
    }

    @Override
    public void shutdown() {
        dataSource.close();
         dataSource = null;
    }
}
