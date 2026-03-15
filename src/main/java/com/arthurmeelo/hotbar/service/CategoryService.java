package com.arthurmeelo.hotbar.service;

import com.arthurmeelo.hotbar.config.CategoryDefinition;
import com.arthurmeelo.hotbar.config.ItemKey;
import org.bukkit.inventory.ItemStack;

import java.util.LinkedHashMap;
import java.util.Map;

public final class CategoryService {
    private final Map<String, CategoryDefinition> definitions;

    public CategoryService(Map<String, CategoryDefinition> definitions) {

        this.definitions = new LinkedHashMap<>(definitions);
    }

    public CategoryDefinition getDefinition(String id) {
        if (id == null) return null;
        return definitions.get(id.toLowerCase());
    }

    public Map<String, CategoryDefinition> getDefinitions() {
        return definitions;
    }

    public String detect(ItemStack stack) {
        if (stack == null) return null;
        for (Map.Entry<String, CategoryDefinition> e : definitions.entrySet()) {
            for (ItemKey key : e.getValue().getItems()) {
                if (key.matches(stack)) return e.getKey();
            }
        }
        return null;
    }
}

