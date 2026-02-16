package io.github.some_example_name.save;

public interface ISaveable {
    String getSaveId();
    SaveData writeSaveData();
    void readSaveData(SaveData saveData);
}
