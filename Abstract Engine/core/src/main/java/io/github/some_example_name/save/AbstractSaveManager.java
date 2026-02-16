package io.github.some_example_name.save;

import java.util.HashMap;
import java.util.Map;


public abstract class AbstractSaveManager {

    /* Store saveables by their ID. */
    private final Map<String, ISaveable> saveables;

    protected AbstractSaveManager() {
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


    protected abstract void writeToFile(String fileName, Map<String, SaveData> dataMap);

    protected abstract Map<String, SaveData> readFromFile(String fileName);

    protected abstract void deleteFile(String fileName);
}
