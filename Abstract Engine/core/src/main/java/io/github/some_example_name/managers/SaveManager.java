package io.github.some_example_name.managers;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.utils.Json;
import com.badlogic.gdx.utils.JsonReader;
import com.badlogic.gdx.utils.JsonValue;
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
        if (saveable == null) {
            throw new IllegalArgumentException("saveable cannot be null.");
        }

        String id = normalizeSaveId(saveable.getSaveId());
        if (id == null) {
            throw new IllegalArgumentException("saveable id cannot be null or blank.");
        }

        if (saveables.containsKey(id)) {
            throw new IllegalArgumentException(
                "A saveable with id '" + id + "' is already registered.");
        }
        saveables.put(id, saveable);
    }

    public void unregister(String saveId) {
        String id = normalizeSaveId(saveId);
        if (id == null) {
            return;
        }
        saveables.remove(id);
    }

    public void save(String fileName) {
        Map<String, SaveData> dataMap = new HashMap<>();
        for (Map.Entry<String, ISaveable> entry : saveables.entrySet()) {
            dataMap.put(entry.getKey(), entry.getValue().writeSaveData());
        }
        writeToFile(fileName, dataMap);
    }

    public void load(String fileName) {
        Map<String, SaveData> dataMap = readFromFile(fileName);
        for (Map.Entry<String, SaveData> entry : dataMap.entrySet()) {
            String saveId = normalizeSaveId(entry.getKey());
            if (saveId == null) {
                continue;
            }

            ISaveable saveable = saveables.get(saveId);
            if (saveable != null) {
                saveable.readSaveData(entry.getValue());
            }
        }
    }

    public void delete(String fileName) {
        deleteFile(fileName);
    }

    public boolean hasSaveFile(String fileName) {
        FileHandle file = Gdx.files.local(resolveFilePath(fileName));
        return file.exists();
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

        JsonValue payload;
        try {
            payload = new JsonReader().parse(file.readString("UTF-8"));
        } catch (SerializationException ex) {
            System.out.println("[SaveManager] Failed to parse save file at " + file.path());
            return result;
        }
        if (payload == null || !payload.isObject()) {
            return result;
        }

        for (JsonValue saveNode = payload.child; saveNode != null; saveNode = saveNode.next) {
            String saveId = normalizeSaveId(saveNode.name);
            if (saveId == null) {
                continue;
            }

            SaveData saveData = new SaveData(saveId);
            JsonValue entriesObj = saveNode.get("entries");
            if (entriesObj != null && entriesObj.isObject()) {
                for (JsonValue saveEntry = entriesObj.child; saveEntry != null; saveEntry = saveEntry.next) {
                    String entryKey = saveEntry.name;
                    if (entryKey != null) {
                        saveData.put(entryKey, readJsonValue(saveEntry));
                    }
                }
            }
            result.put(saveId, saveData);
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

    private String normalizeSaveId(String saveId) {
        if (saveId == null || saveId.isBlank()) {
            return null;
        }
        return saveId.trim();
    }

    private Object readJsonValue(JsonValue rawNode) {
        if (rawNode == null) {
            return null;
        }

        // Old saves may be wrapped as { class: ..., value: ... }.
        JsonValue node = rawNode;
        JsonValue wrappedValue = rawNode.get("value");
        if (wrappedValue != null) {
            node = wrappedValue;
        }

        if (node.isNumber()) {
            return node.asDouble();
        }
        if (node.isBoolean()) {
            return node.asBoolean();
        }
        if (node.isString()) {
            return node.asString();
        }
        if (node.isNull()) {
            return null;
        }

        return node.toString();
    }
}
