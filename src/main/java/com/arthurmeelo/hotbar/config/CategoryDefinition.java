package com.arthurmeelo.hotbar.config;

import org.bukkit.Material;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

public final class CategoryDefinition {
    private final String id;
    private final String displayName;
    private final Material iconMaterial;
    private final short iconData;
    private final Set<ItemKey> items;

    public CategoryDefinition(String id,
                              String displayName,
                              Material iconMaterial,
                              short iconData,
                              Set<ItemKey> items) {
        this.id = id.toLowerCase();
        this.displayName = displayName;
        this.iconMaterial = iconMaterial;
        this.iconData = iconData;
        this.items = Collections.unmodifiableSet(new HashSet<>(items));
    }

    public String getId() {
        return id;
    }

    public String getDisplayName() {
        return displayName;
    }

    public Material getIconMaterial() {
        return iconMaterial;
    }

    public short getIconData() {
        return iconData;
    }

    public Set<ItemKey> getItems() {
        return items;
    }
}

