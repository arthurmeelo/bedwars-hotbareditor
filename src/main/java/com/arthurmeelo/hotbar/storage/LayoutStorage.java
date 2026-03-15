package com.arthurmeelo.hotbar.storage;

import java.util.UUID;


public interface LayoutStorage {
    String load(UUID player) throws Exception;

    void save(UUID player, String encodedLayout) throws Exception;

    void shutdown();
}

