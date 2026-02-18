package io.github.some_example_name.managers;

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
        throw new UnsupportedOperationException("writeToFile must be implemented by a subclass.");
    }

    protected Map<String, SaveData> readFromFile(String fileName) {
        throw new UnsupportedOperationException("readFromFile must be implemented by a subclass.");
    }

    protected void deleteFile(String fileName) {
        throw new UnsupportedOperationException("deleteFile must be implemented by a subclass.");
    }
}