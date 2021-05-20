package fr.mercury.combattag.utils;

import fr.mercury.combattag.MercuryTag;

import java.io.File;

public class Utils {

    public static File getFormatedFile(String fileName) {
        return new File(MercuryTag.getInstance().getDataFolder(), fileName);
    }
}
