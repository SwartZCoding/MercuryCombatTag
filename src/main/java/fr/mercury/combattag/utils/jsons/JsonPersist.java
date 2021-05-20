package fr.mercury.combattag.utils.jsons;

import java.io.File;

public abstract interface JsonPersist {
  public abstract File getFile();
  public abstract void loadData();
  public abstract void saveData(boolean sync);
}
