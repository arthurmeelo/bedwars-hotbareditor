package com.arthurmeelo.hotbar.codec;

import com.arthurmeelo.hotbar.model.HotbarLayout;


public final class HotbarLayoutCodec {
    private HotbarLayoutCodec() {}

    public static String encode(HotbarLayout layout) {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < 9; i++) {
            if (i > 0) sb.append(';');
            sb.append(i).append('=');
            boolean first = true;
            for (String id : layout.getSlot(i)) {
                if (!first) sb.append(',');
                sb.append(id);
                first = false;
            }
        }
        return sb.toString();
    }

    public static HotbarLayout decode(String data) {
        HotbarLayout layout = new HotbarLayout();
        if (data == null || data.trim().isEmpty()) return layout;

        String[] parts = data.split(";");
        for (String part : parts) {
            String[] kv = part.split("=", 2);
            if (kv.length != 2) continue;
            int slot;
            try {
                slot = Integer.parseInt(kv[0].trim());
            } catch (NumberFormatException e) {
                continue;
            }
            if (slot < 0 || slot > 8) continue;
            String cats = kv[1].trim();
            if (cats.isEmpty()) continue;
            String[] ids = cats.split(",");
            for (String id : ids) {
                layout.addCategory(slot, id.trim());
            }
        }
        return layout;
    }
}

