package com.arthurmeelo.hotbar.service;

import com.arthurmeelo.hotbar.model.HotbarLayout;
import com.arthurmeelo.hotbar.nms.InventoryPackets;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

public final class AutoOrganizer {
    private final LayoutService layouts;
    private final CategoryService categories;
    private final boolean usePackets;

    public AutoOrganizer(LayoutService layouts, CategoryService categories, boolean usePackets) {
        this.layouts = layouts;
        this.categories = categories;
        this.usePackets = usePackets;
    }


    public void organize(Player p, ItemStack sampleStack) {
        if (p == null || sampleStack == null) return;
        if (sampleStack.getType() == Material.AIR) return;

        String catId = categories.detect(sampleStack);
        if (catId == null) return;

        HotbarLayout layout = layouts.getCachedOrDefault(p.getUniqueId());
        int destHotbar = layout.findSlotFor(catId);
        if (destHotbar < 0 || destHotbar > 8) return;


        int from = findFirstSimilarSlot(p, sampleStack);
        if (from == -1) return;



        if (from > 35) return;

        if (from == destHotbar) return;

        ItemStack fromStack = p.getInventory().getItem(from);
        ItemStack destStack = p.getInventory().getItem(destHotbar);


        if (destStack != null && destStack.getType() != Material.AIR) {
            int free = findFirstEmptySlot(p, from);
            if (free == -1) {

                return;
            }
            p.getInventory().setItem(free, destStack);
        }


        p.getInventory().setItem(destHotbar, fromStack);
        p.getInventory().setItem(from, null);

        if (usePackets) {
            InventoryPackets.sendHotbarSlot(p, destHotbar, p.getInventory().getItem(destHotbar));
        } else {
            p.updateInventory();
        }
    }

    private int findFirstSimilarSlot(Player p, ItemStack sample) {
        ItemStack[] contents = p.getInventory().getContents();
        for (int i = 0; i < contents.length; i++) {
            ItemStack it = contents[i];
            if (it == null || it.getType() == Material.AIR) continue;
            if (it.isSimilar(sample)) return i;
        }
        return -1;
    }

    private int findFirstEmptySlot(Player p, int ignoreIndex) {
        ItemStack[] contents = p.getInventory().getContents();
        for (int i = 0; i < 36; i++) {
            if (i == ignoreIndex) continue;
            ItemStack it = contents[i];
            if (it == null || it.getType() == Material.AIR) return i;
        }
        return -1;
    }
}

