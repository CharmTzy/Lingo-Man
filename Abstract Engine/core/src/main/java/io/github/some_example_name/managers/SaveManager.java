package io.github.some_example_name.managers;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.utils.Json;
import com.badlogic.gdx.utils.SerializationException;
import io.github.some_example_name.save.ISaveable;
import io.github.some_example_name.save.SaveData;

import java.util.HashMap;
import java.util.Map;

public class SaveManager {

    private final Map<String, ISaveable> saveables;

    public SaveManager() {
        this.saveables = new HashMap<>();
    }

    public void register(ISaveable saveable) {
        String id = saveable.getSaveId();
        if (saveables.containsKey(id)) {
            throw new IllegalArgumentException(
                "A saveable with id '" + id + "' is already registered.");
        }
        saveables.put(id, saveable);
    }

    public void unregister(String saveId) {
        saveables.remove(saveId);
    }

    public void save(String fileName) {
        Map<String, SaveData> dataMap = new HashMap<>();
        for (ISaveable saveable : saveables.values()) {
            dataMap.put(saveable.getSaveId(), saveable.writeSaveData());
        }
        writeToFile(fileName, dataMap);
    }

    public void load(String fileName) {
        Map<String, SaveData> dataMap = readFromFile(fileName);
        for (Map.Entry<String, SaveData> entry : dataMap.entrySet()) {
            ISaveable saveable = saveables.get(entry.getKey());
            if (saveable != null) {
                saveable.readSaveData(entry.getValue());
            }
        }
    }

    public void delete(String fileName) {
        deleteFile(fileName);
    }

    protected void writeToFile(String fileName, Map<String, SaveData> dataMap) {
        FileHandle file = Gdx.files.local(resolveFilePath(fileName));
        file.parent().mkdirs();

        Map<String, Object> payload = new HashMap<>();
        for (Map.Entry<String, SaveData> entry : dataMap.entrySet()) {
            Map<String, Object> saveNode = new HashMap<>();
            saveNode.put("entries", new HashMap<>(entry.getValue().getAllEntries()));
            payload.put(entry.getKey(), saveNode);
        }

        Json json = new Json();
        file.writeString(json.prettyPrint(payload), false, "UTF-8");
        System.out.println("[SaveManager] Saved session to " + file.path());
    }

    protected Map<String, SaveData> readFromFile(String fileName) {
        Map<String, SaveData> result = new HashMap<>();
        FileHandle file = Gdx.files.local(resolveFilePath(fileName));
        if (!file.exists()) {
            System.out.println("[SaveManager] No save file found at " + file.path());
            return result;
        }

        Json json = new Json();
        Map<String, Object> payload;
        try {
            @SuppressWarnings("unchecked")
            Map<String, Object> parsed = json.fromJson(HashMap.class, file.readString("UTF-8"));
            payload = parsed;
        } catch (SerializationException ex) {
            System.out.println("[SaveManager] Failed to parse save file at " + file.path());
            return result;
        }
        if (payload == null) {
            return result;
        }

        for (Map.Entry<String, Object> entry : payload.entrySet()) {
            SaveData saveData = new SaveData(entry.getKey());
            if (entry.getValue() instanceof Map<?, ?> saveNode) {
                Object entriesObj = saveNode.get("entries");
                if (entriesObj instanceof Map<?, ?> entriesMap) {
                    for (Map.Entry<?, ?> saveEntry : entriesMap.entrySet()) {
                        if (saveEntry.getKey() != null) {
                            saveData.put(String.valueOf(saveEntry.getKey()), saveEntry.getValue());
                        }
                    }
                }
            }
            result.put(entry.getKey(), saveData);
        }

        System.out.println("[SaveManager] Loaded session from " + file.path());
        return result;
    }

    protected void deleteFile(String fileName) {
        FileHandle file = Gdx.files.local(resolveFilePath(fileName));
        if (!file.exists()) {
            System.out.println("[SaveManager] No session to delete at " + file.path());
            return;
        }
        file.delete();
        System.out.println("[SaveManager] Deleted session at " + file.path());
    }

    private String resolveFilePath(String fileName) {
        String normalized = (fileName == null || fileName.isBlank()) ? "session.json" : fileName.trim();
        if (!normalized.endsWith(".json")) {
            normalized += ".json";
        }
        return "saves/" + normalized;
    }
}
