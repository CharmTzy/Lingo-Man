package io.github.some_example_name.save;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

public class SaveData {

    private final String id;
    private final Map<String, Object> entries;

    public SaveData(String id) {
        this.id = id;
        this.entries = new HashMap<>();
    }

    public String getId() {
        return id;
    }

    public void put(String key, Object value) {
        entries.put(key, value);
    }

    public Object get(String key) {
        return entries.get(key);
    }

    public <T> T get(String key, Class<T> type) {
        return type.cast(entries.get(key));
    }

    public boolean containsKey(String key) {
        return entries.containsKey(key);
    }

    public Map<String, Object> getAllEntries() {
        return Collections.unmodifiableMap(entries);
    }
}
