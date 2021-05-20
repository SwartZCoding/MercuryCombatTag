package fr.mercury.combattag.utils.jsons;

import fr.mercury.combattag.MercuryTag;

import java.io.File;

public abstract class Saveable implements JsonPersist {

    public boolean needDir, needFirstSave;

    public Saveable(MercuryTag plugin, String name) {
        this(plugin, name, false, false);
    }

    public Saveable(MercuryTag plugin, String name, boolean needDir, boolean needFirstSave) {
        this.needDir = needDir;
        this.needFirstSave = needFirstSave;
        if (this.needDir) {
            if (this.needFirstSave) {
                saveData(false);
            } else {
                File directory = getFile();
                if (!directory.exists()) {
                    try {
                        directory.mkdir();
                    } catch (Exception exception) {
                        exception.printStackTrace();
                    }
                }
            }
        }
    }
}
