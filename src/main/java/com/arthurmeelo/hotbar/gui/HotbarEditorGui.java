package com.arthurmeelo.hotbar.gui;

import com.arthurmeelo.hotbar.config.CategoryDefinition;
import com.arthurmeelo.hotbar.config.ConfigLoader;
import com.arthurmeelo.hotbar.model.HotbarLayout;
import com.arthurmeelo.hotbar.service.CategoryService;
import com.arthurmeelo.hotbar.service.LayoutService;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.plugin.Plugin;

import java.util.*;

public final class HotbarEditorGui implements Listener {
    private final Plugin plugin;
    private final LayoutService layouts;
    private final CategoryService categories;

    private final String mainTitle;
    private final String selectTitle;

    private final ItemStack emptyPane;
    private final ItemStack configuredPane;
    private final ItemStack disabledPane;

    public HotbarEditorGui(Plugin plugin, LayoutService layouts, CategoryService categories, FileConfiguration cfg) {
        this.plugin = plugin;
        this.layouts = layouts;
        this.categories = categories;

        this.mainTitle = color(cfg.getString("gui.main-title", "&eEditando hotbar..."));
        this.selectTitle = color(cfg.getString("gui.select-title", "&eAdicionado no slot #%slot%..."));

        this.emptyPane = paneFrom(cfg.getString("gui.filler.empty", "STAINED_GLASS_PANE:7"), " ");
        this.configuredPane = paneFrom(cfg.getString("gui.filler.configured", "STAINED_GLASS_PANE:5"), " ");
        this.disabledPane = paneFrom(cfg.getString("gui.filler.disabled", "STAINED_GLASS_PANE:14"), " ");

        Bukkit.getPluginManager().registerEvents(this, plugin);
    }

    public void openMain(Player p) {

        Inventory inv = Bukkit.createInventory(p, 27, mainTitle);
        HotbarLayout layout = layouts.getCachedOrDefault(p.getUniqueId());

        for (int slot = 0; slot < 9; slot++) {
            Set<String> selected = layout.getSlot(slot);
            ItemStack item = (selected.isEmpty() ? emptyPane.clone() : configuredPane.clone());
            ItemMeta meta = item.getItemMeta();
            if (meta != null) {
                meta.setDisplayName("§fSlot #" + (slot + 1));
                List<String> lore = new ArrayList<>();

                if (selected.isEmpty()) {
                    lore.add("§7Botão Esquerdo: §aAdicionar");
                } else {
                    lore.add("§7Selecionado:");
                    for (String id : selected) {
                        CategoryDefinition def = categories.getDefinition(id);
                        lore.add("§f- " + (def != null ? def.getDisplayName() : id));
                    }
                    lore.add(" ");
                    lore.add("§7Botão Esquerdo: §aAdicionar");
                    lore.add("§7Botão Direito: §cRemover");
                }
                meta.setLore(lore);
                item.setItemMeta(meta);
            }

            inv.setItem(9 + slot, item);
        }
        p.openInventory(inv);
    }

    private void openSelect(Player p, int slot) {
        String title = selectTitle.replace("%slot%", String.valueOf(slot + 1));
        Inventory inv = Bukkit.createInventory(p, 27, title);


        for (int i = 0; i < inv.getSize(); i++) {
            inv.setItem(i, emptyPane.clone());
        }

        HotbarLayout layout = layouts.getCachedOrDefault(p.getUniqueId());


        int[] positions = {10, 11, 12, 13, 14, 15, 16};
        int idx = 0;
        for (CategoryDefinition def : categories.getDefinitions().values()) {
            if (idx >= positions.length) break;
            int pos = positions[idx++];

            boolean already = layout.hasCategory(slot, def.getId());
            ItemStack icon;
            if (already) {
                icon = disabledPane.clone();
            } else {
                icon = new ItemStack(def.getIconMaterial(), 1, def.getIconData());
            }

            ItemMeta meta = icon.getItemMeta();
            if (meta != null) {
                meta.setDisplayName(def.getDisplayName());
                List<String> lore = new ArrayList<>();
                if (already) {
                    lore.add("§cJá selecionado neste slot");
                } else {
                    lore.add("§7Clique para adicionar");
                }
                meta.setLore(lore);
                icon.setItemMeta(meta);
            }
            inv.setItem(pos, icon);
        }


        ItemStack back = new ItemStack(Material.ARROW);
        ItemMeta m = back.getItemMeta();
        if (m != null) {
            m.setDisplayName("§eVoltar");
            back.setItemMeta(m);
        }
        inv.setItem(22, back);

        p.openInventory(inv);
    }

    @EventHandler
    public void onClick(InventoryClickEvent e) {
        if (!(e.getWhoClicked() instanceof Player)) return;
        Player p = (Player) e.getWhoClicked();
        Inventory top = e.getView() != null ? e.getView().getTopInventory() : null;
        if (top == null) return;

        String title;
        try {
            title = top.getTitle();
        } catch (Throwable t) {
            return;
        }

        boolean isMain = mainTitle.equals(title);
        boolean isSelect = title != null && title.startsWith(selectTitle.split("%slot%")[0]);
        if (!isMain && !isSelect) return;

        e.setCancelled(true);
        ItemStack clicked = e.getCurrentItem();
        if (clicked == null || clicked.getType() == Material.AIR) return;
        int rawSlot = e.getRawSlot();

        if (isMain) {

            if (rawSlot < 9 || rawSlot > 17) return;
            int slot = rawSlot - 9;
            if (e.getClick() == ClickType.RIGHT) {
                layouts.clearSlot(p.getUniqueId(), slot);
                openMain(p);
            } else {
                openSelect(p, slot);
            }
            return;
        }

        if (isSelect) {

            int slot = 0;
            try {
                String digits = title.replaceAll("\\D+", "");
                slot = Integer.parseInt(digits) - 1;
            } catch (Exception ignored) {}
            if (slot < 0 || slot > 8) return;


            if (rawSlot == 22) {
                openMain(p);
                return;
            }


            int[] positions = {10, 11, 12, 13, 14, 15, 16};
            int index = -1;
            for (int i = 0; i < positions.length; i++) {
                if (positions[i] == rawSlot) {
                    index = i;
                    break;
                }
            }
            if (index == -1) return;


            int i = 0;
            String chosenId = null;
            for (CategoryDefinition def : categories.getDefinitions().values()) {
                if (i == index) {
                    chosenId = def.getId();
                    break;
                }
                i++;
            }
            if (chosenId == null) return;


            if (clicked.isSimilar(disabledPane)) return;

            layouts.addCategory(p.getUniqueId(), slot, chosenId);
            openMain(p);
        }
    }

    private ItemStack paneFrom(String spec, String name) {
        ConfigLoader.ParsedMaterial pm = ConfigLoader.parseMaterial(spec);
        Material mat = pm.material != null ? pm.material : Material.STAINED_GLASS_PANE;
        short data = pm.data != null ? pm.data : 0;
        ItemStack pane = new ItemStack(mat, 1, data);
        ItemMeta meta = pane.getItemMeta();
        if (meta != null) {
            meta.setDisplayName(name);
            pane.setItemMeta(meta);
        }
        return pane;
    }

    private String color(String input) {
        if (input == null) return "";
        return input.replace('&', '§');
    }
}

