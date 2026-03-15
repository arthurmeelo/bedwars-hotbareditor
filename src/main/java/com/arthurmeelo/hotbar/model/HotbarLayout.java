package com.arthurmeelo.hotbar.model;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;


public final class HotbarLayout {
    private final Set<String>[] slots;

    @SuppressWarnings("unchecked")
    public HotbarLayout() {
        this.slots = new Set[9];
        for (int i = 0; i < 9; i++) {
            this.slots[i] = new HashSet<>();
        }
    }

    public Set<String> getSlot(int slot) {
        if (slot < 0 || slot > 8) return Collections.emptySet();
        return Collections.unmodifiableSet(slots[slot]);
    }

    public boolean hasCategory(int slot, String categoryId) {
        if (slot < 0 || slot > 8 || categoryId == null) return false;
        return slots[slot].contains(categoryId);
    }

    public void addCategory(int slot, String categoryId) {
        if (slot < 0 || slot > 8 || categoryId == null) return;
        slots[slot].add(categoryId.toLowerCase());
    }

    public void removeCategory(int slot, String categoryId) {
        if (slot < 0 || slot > 8 || categoryId == null) return;
        slots[slot].remove(categoryId.toLowerCase());
    }

    public void clearSlot(int slot) {
        if (slot < 0 || slot > 8) return;
        slots[slot].clear();
    }


    public int findSlotFor(String categoryId) {
        if (categoryId == null) return -1;
        String norm = categoryId.toLowerCase();
        for (int i = 0; i < 9; i++) {
            if (slots[i].contains(norm)) return i;
        }
        return -1;
    }
}

