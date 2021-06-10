/* (c) https://github.com/MontiCore/monticore */

package de.monticore.featurediagram;


import de.monticore.io.paths.MCPath;

import java.nio.file.Path;
import java.nio.file.Paths;

public class ModelPaths {

  public static void addEntry(MCPath mp, String p) {
    addEntry(mp, Paths.get(p));
  }

  public static void addEntry(MCPath mp, Path p) {
    p = p.toAbsolutePath();
    if (!mp.getEntries().contains(p)) {
      mp.addEntry(p);
    }
  }

  public static void merge(MCPath mp, MCPath newEntries) {
    for (Path p : newEntries.getEntries()) {
      addEntry(mp, p);
    }
  }
}
