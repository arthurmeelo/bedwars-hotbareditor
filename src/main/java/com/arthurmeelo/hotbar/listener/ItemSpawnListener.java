package com.arthurmeelo.hotbar.listener;

import com.arthurmeelo.hotbar.service.AutoOrganizer;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.player.PlayerPickupItemEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.Plugin;


public final class ItemSpawnListener implements Listener {
    private final Plugin plugin;
    private final AutoOrganizer organizer;

    public ItemSpawnListener(Plugin plugin, AutoOrganizer organizer) {
        this.plugin = plugin;
        this.organizer = organizer;
        Bukkit.getPluginManager().registerEvents(this, plugin);
    }

    @EventHandler
    public void onPickup(PlayerPickupItemEvent e) {
        final Player p = e.getPlayer();
        final ItemStack picked = e.getItem() != null ? e.getItem().getItemStack() : null;
        if (picked == null) return;


        Bukkit.getScheduler().runTask(plugin, () -> organizer.organize(p, picked));
    }

    @EventHandler
    public void onInventoryClick(InventoryClickEvent e) {
        if (!(e.getWhoClicked() instanceof Player)) return;
        final Player p = (Player) e.getWhoClicked();


        Bukkit.getScheduler().runTask(plugin, () -> {
            ItemStack cursor = p.getItemOnCursor();
            if (cursor != null) organizer.organize(p, cursor);
            ItemStack current = e.getCurrentItem();
            if (current != null) organizer.organize(p, current);
        });
    }
}

