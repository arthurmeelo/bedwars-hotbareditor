package com.arthurmeelo.hotbar.storage;

import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.model.Filters;
import com.mongodb.client.model.ReplaceOptions;
import org.bson.Document;

import java.util.UUID;

public final class MongoLayoutStorage implements LayoutStorage {
    private final MongoClient client;
    private final MongoCollection<Document> collection;

    public MongoLayoutStorage(String uri, String database, String collection) {
        this.client = MongoClients.create(uri);
        MongoDatabase db = client.getDatabase(database);
        this.collection = db.getCollection(collection);
    }

    @Override
    public String load(UUID player) throws Exception {
        Document doc = collection.find(Filters.eq("_id", player.toString())).first();
        if (doc == null) return null;
        return doc.getString("layout");
    }

    @Override
    public void save(UUID player, String encodedLayout) throws Exception {
        Document doc = new Document("_id", player.toString()).append("layout", encodedLayout);
        collection.replaceOne(Filters.eq("_id", player.toString()), doc, new ReplaceOptions().upsert(true));
    }

    @Override
    public void shutdown() {
        if (client != null) client.close();
    }
}

