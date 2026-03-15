package com.arthurmeelo.hotbar.listener;

import com.arthurmeelo.hotbar.service.LayoutService;
import org.bukkit.Bukkit;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.plugin.Plugin;

public final class PlayerSessionListener implements Listener {
    private final LayoutService layouts;

    public PlayerSessionListener(Plugin plugin, LayoutService layouts) {
        this.layouts = layouts;
        Bukkit.getPluginManager().registerEvents(this, plugin);
    }

    @EventHandler
    public void onJoin(PlayerJoinEvent e) {
        layouts.preloadAsync(e.getPlayer().getUniqueId());
    }

    @EventHandler
    public void onQuit(PlayerQuitEvent e) {
        layouts.evict(e.getPlayer().getUniqueId());
    }
}

