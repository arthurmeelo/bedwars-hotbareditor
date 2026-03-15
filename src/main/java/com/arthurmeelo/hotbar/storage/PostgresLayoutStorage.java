package com.arthurmeelo.hotbar.storage;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.UUID;

public final class PostgresLayoutStorage implements LayoutStorage {
    private final HikariDataSource ds;

    public PostgresLayoutStorage(String host, int port, String database, String user, String pass, int maxPoolSize) {
        HikariConfig cfg = new HikariConfig();
        cfg.setJdbcUrl("jdbc:postgresql://" + host + ":" + port + "/" + database);
        cfg.setUsername(user);
        cfg.setPassword(pass);
        cfg.setDriverClassName("org.postgresql.Driver");
        cfg.setMaximumPoolSize(maxPoolSize);
        cfg.addDataSourceProperty("cachePrepStmts", "true");
        cfg.addDataSourceProperty("prepStmtCacheSize", "250");
        cfg.addDataSourceProperty("prepStmtCacheSqlLimit", "2048");
        ds = new HikariDataSource(cfg);
        createTable();
    }

    private void createTable() {
        String ddl = "CREATE TABLE IF NOT EXISTS hotbar_layouts (" +
                "uuid VARCHAR(36) PRIMARY KEY," +
                "layout TEXT NOT NULL" +
                ");";
        try (Connection c = ds.getConnection(); Statement st = c.createStatement()) {
            st.execute(ddl);
        } catch (Exception e) {
            throw new RuntimeException("Failed to create hotbar_layouts table (PostgreSQL)", e);
        }
    }

    @Override
    public String load(UUID player) throws Exception {
        String sql = "SELECT layout FROM hotbar_layouts WHERE uuid = ?";
        try (Connection c = ds.getConnection(); PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setString(1, player.toString());
            try (ResultSet rs = ps.executeQuery()) {
                if (!rs.next()) return null;
                return rs.getString(1);
            }
        }
    }

    @Override
    public void save(UUID player, String encodedLayout) throws Exception {
        String sql = "INSERT INTO hotbar_layouts (uuid, layout) VALUES (?, ?) " +
                "ON CONFLICT (uuid) DO UPDATE SET layout = EXCLUDED.layout";
        try (Connection c = ds.getConnection(); PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setString(1, player.toString());
            ps.setString(2, encodedLayout);
            ps.executeUpdate();
        }
    }

    @Override
    public void shutdown() {
        if (ds != null) ds.close();
    }
}

