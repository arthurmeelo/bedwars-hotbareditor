package com.arthurmeelo.hotbar.service;

import com.arthurmeelo.hotbar.codec.HotbarLayoutCodec;
import com.arthurmeelo.hotbar.model.HotbarLayout;
import com.arthurmeelo.hotbar.storage.LayoutStorage;
import org.bukkit.Bukkit;
import org.bukkit.plugin.Plugin;

import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;


public final class LayoutService {
    private final Plugin plugin;
    private final LayoutStorage storage;
    private final Map<UUID, HotbarLayout> cache = new ConcurrentHashMap<>();

    public LayoutService(Plugin plugin, LayoutStorage storage) {
        this.plugin = plugin;
        this.storage = storage;
    }

    public HotbarLayout getCachedOrDefault(UUID player) {
        HotbarLayout l = cache.get(player);
        return l != null ? l : new HotbarLayout();
    }

    public void preloadAsync(UUID player) {
        if (cache.containsKey(player)) return;
        Bukkit.getScheduler().runTaskAsynchronously(plugin, () -> {
            try {
                String raw = storage.load(player);
                HotbarLayout layout = HotbarLayoutCodec.decode(raw);
                cache.put(player, layout);
            } catch (Exception e) {
                plugin.getLogger().warning("Failed to load layout for " + player + ": " + e.getMessage());
                cache.put(player, new HotbarLayout());
            }
        });
    }

    public void evict(UUID player) {
        cache.remove(player);
    }

    public void addCategory(UUID player, int slot, String categoryId) {
        HotbarLayout layout = cache.computeIfAbsent(player, k -> new HotbarLayout());
        layout.addCategory(slot, categoryId);
        saveAsync(player, layout);
    }

    public void clearSlot(UUID player, int slot) {
        HotbarLayout layout = cache.computeIfAbsent(player, k -> new HotbarLayout());
        layout.clearSlot(slot);
        saveAsync(player, layout);
    }

    public void saveAsync(UUID player, HotbarLayout layout) {
        String encoded = HotbarLayoutCodec.encode(layout);
        Bukkit.getScheduler().runTaskAsynchronously(plugin, () -> {
            try {
                storage.save(player, encoded);
            } catch (Exception e) {
                plugin.getLogger().warning("Failed to save layout for " + player + ": " + e.getMessage());
            }
        });
    }
}

