package com.arthurmeelo.hotbar.nms;

import net.minecraft.server.v1_8_R3.ItemStack;
import net.minecraft.server.v1_8_R3.PacketPlayOutSetSlot;
import org.bukkit.craftbukkit.v1_8_R3.entity.CraftPlayer;
import org.bukkit.craftbukkit.v1_8_R3.inventory.CraftItemStack;
import org.bukkit.entity.Player;
import org.bukkit.inventory.PlayerInventory;


public final class InventoryPackets {
    private InventoryPackets() {}


    public static void sendHotbarSlot(Player player, int hotbarSlot, org.bukkit.inventory.ItemStack bukkitStack) {
        if (hotbarSlot < 0 || hotbarSlot > 8) return;
        int windowId = 0;
        int nmsSlot = 36 + hotbarSlot;
        ItemStack nms = CraftItemStack.asNMSCopy(bukkitStack);
        PacketPlayOutSetSlot packet = new PacketPlayOutSetSlot(windowId, nmsSlot, nms);
        ((CraftPlayer) player).getHandle().playerConnection.sendPacket(packet);
    }


    public static void sendHotbarSwap(Player player, int a, int b) {
        PlayerInventory inv = player.getInventory();
        sendHotbarSlot(player, a, inv.getItem(a));
        sendHotbarSlot(player, b, inv.getItem(b));
    }
}

