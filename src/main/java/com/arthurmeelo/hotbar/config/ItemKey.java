package com.arthurmeelo.hotbar.config;

import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

import java.util.Objects;


public final class ItemKey {
    private final Material material;
    private final Short data;

    public ItemKey(Material material, Short data) {
        this.material = material;
        this.data = data;
    }

    public Material getMaterial() {
        return material;
    }

    public Short getData() {
        return data;
    }

    public boolean matches(ItemStack stack) {
        if (stack == null) return false;
        if (stack.getType() != material) return false;
        if (data == null) return true;
        return stack.getDurability() == data;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof ItemKey)) return false;
        ItemKey itemKey = (ItemKey) o;
        return material == itemKey.material && Objects.equals(data, itemKey.data);
    }

    @Override
    public int hashCode() {
        return Objects.hash(material, data);
    }
}

