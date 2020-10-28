/* (c) https://github.com/MontiCore/monticore */

package de.monticore.featurediagram;

import de.monticore.io.paths.ModelPath;

import java.nio.file.Path;
import java.nio.file.Paths;

public class ModelPaths {

  public static void addEntry(ModelPath mp, String p) {
    addEntry(mp, Paths.get(p));
  }

  public static void addEntry(ModelPath mp, Path p) {
    p = p.toAbsolutePath();
    if (!mp.getFullPathOfEntries().contains(p)) {
      mp.addEntry(p);
    }
  }

  public static void merge(ModelPath mp, ModelPath newEntries) {
    for (Path p : newEntries.getFullPathOfEntries()) {
      addEntry(mp, p);
    }
  }
}
