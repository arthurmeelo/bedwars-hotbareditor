package com.arthurmeelo.hotbar;

import co.aikar.commands.BukkitCommandManager;
import com.arthurmeelo.hotbar.commands.HotbarEditorCommand;
import com.arthurmeelo.hotbar.config.CategoryDefinition;
import com.arthurmeelo.hotbar.config.ConfigLoader;
import com.arthurmeelo.hotbar.gui.HotbarEditorGui;
import com.arthurmeelo.hotbar.listener.ItemSpawnListener;
import com.arthurmeelo.hotbar.listener.PlayerSessionListener;
import com.arthurmeelo.hotbar.service.AutoOrganizer;
import com.arthurmeelo.hotbar.service.CategoryService;
import com.arthurmeelo.hotbar.service.LayoutService;
import com.arthurmeelo.hotbar.storage.LayoutStorage;
import com.arthurmeelo.hotbar.storage.MongoLayoutStorage;
import com.arthurmeelo.hotbar.storage.MySqlLayoutStorage;
import com.arthurmeelo.hotbar.storage.PostgresLayoutStorage;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.Map;

public class HotbarManagerPlugin extends JavaPlugin {
    private LayoutStorage storage;
    private LayoutService layoutService;
    private CategoryService categoryService;
    private HotbarEditorGui hotbarEditorGui;

    @Override
    public void onEnable() {
        saveDefaultConfig();
        FileConfiguration cfg = getConfig();


        Map<String, CategoryDefinition> defs = ConfigLoader.loadCategories(cfg);
        categoryService = new CategoryService(defs);


        String type = cfg.getString("database.type", "mysql").trim().toLowerCase();
        if ("mongodb".equals(type)) {
            storage = new MongoLayoutStorage(
                    cfg.getString("database.mongodb.uri", "mongodb://localhost:27017"),
                    cfg.getString("database.mongodb.database", "hotbar_db"),
                    cfg.getString("database.mongodb.collection", "hotbar_layouts")
            );
        } else if ("postgresql".equals(type) || "postgres".equals(type)) {
            storage = new PostgresLayoutStorage(
                    cfg.getString("database.postgresql.host", "localhost"),
                    cfg.getInt("database.postgresql.port", 5432),
                    cfg.getString("database.postgresql.database", "hotbar_db"),
                    cfg.getString("database.postgresql.user", "postgres"),
                    cfg.getString("database.postgresql.password", ""),
                    cfg.getInt("database.postgresql.maximumPoolSize", 10)
            );
        } else {
            storage = new MySqlLayoutStorage(
                    cfg.getString("database.mysql.host", "localhost"),
                    cfg.getInt("database.mysql.port", 3306),
                    cfg.getString("database.mysql.database", "hotbar_db"),
                    cfg.getString("database.mysql.user", "root"),
                    cfg.getString("database.mysql.password", ""),
                    cfg.getInt("database.mysql.maximumPoolSize", 10)
            );
        }

        layoutService = new LayoutService(this, storage);


        hotbarEditorGui = new HotbarEditorGui(this, layoutService, categoryService, cfg);


        new PlayerSessionListener(this, layoutService);

        boolean organizerEnabled = cfg.getBoolean("settings.organizer", true);
        if (organizerEnabled) {
            boolean usePackets = cfg.getBoolean("settings.use-packets", true);
            AutoOrganizer organizer = new AutoOrganizer(layoutService, categoryService, usePackets);
            new ItemSpawnListener(this, organizer);
        }

        BukkitCommandManager manager = new BukkitCommandManager(this);
        manager.registerCommand(new HotbarEditorCommand(this));
    }

    @Override
    public void onDisable() {
        if (storage != null) storage.shutdown();
    }

    public LayoutService getLayoutService() {
        return layoutService;
    }

    public CategoryService getCategoryService() {
        return categoryService;
    }

    public HotbarEditorGui getHotbarEditorGui() {
        return hotbarEditorGui;
    }
}
