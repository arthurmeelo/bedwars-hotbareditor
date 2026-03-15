package com.arthurmeelo.hotbar.config;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;

import java.util.*;

public final class ConfigLoader {
    private ConfigLoader() {}

    public static Map<String, CategoryDefinition> loadCategories(FileConfiguration cfg) {
        Map<String, CategoryDefinition> defs = new LinkedHashMap<>();

        ConfigurationSection cats = cfg.getConfigurationSection("categories");
        if (cats == null) return defs;

        for (String key : cats.getKeys(false)) {
            ConfigurationSection sec = cats.getConfigurationSection(key);
            if (sec == null) continue;

            String rawName = sec.getString("name", "&f" + key);
            String name = ChatColor.translateAlternateColorCodes('&', rawName);

            ParsedMaterial icon = parseMaterial(sec.getString("icon", "STONE"));
            if (icon.material == null) icon.material = Material.STONE;

            Set<ItemKey> items = new HashSet<>();
            List<String> list = sec.getStringList("items");
            for (String raw : list) {
                ParsedMaterial pm = parseMaterial(raw);
                if (pm.material != null) {
                    items.add(new ItemKey(pm.material, pm.data));
                }
            }

            defs.put(key.toLowerCase(), new CategoryDefinition(key, name, icon.material, icon.data == null ? 0 : icon.data, items));
        }

        return defs;
    }

    public static ParsedMaterial parseMaterial(String input) {
        if (input == null) return new ParsedMaterial(null, null);
        String s = input.trim();
        if (s.isEmpty()) return new ParsedMaterial(null, null);


        try {
            if (s.matches("^\\d+$")) {
                int id = Integer.parseInt(s);
                return new ParsedMaterial(Material.getMaterial(id), null);
            }
        } catch (Exception ignored) {}


        String[] parts = s.split(":", 2);
        String matPart = parts[0].trim();
        Short data = null;
        if (parts.length == 2) {
            try {
                data = Short.parseShort(parts[1].trim());
            } catch (Exception ignored) {}
        }

        Material mat = Material.matchMaterial(matPart);
        if (mat == null) {

            try {
                int id = Integer.parseInt(matPart);
                mat = Material.getMaterial(id);
            } catch (Exception ignored) {}
        }

        return new ParsedMaterial(mat, data);
    }

    public static final class ParsedMaterial {
        public Material material;
        public Short data;

        public ParsedMaterial(Material material, Short data) {
            this.material = material;
            this.data = data;
        }
    }
}

