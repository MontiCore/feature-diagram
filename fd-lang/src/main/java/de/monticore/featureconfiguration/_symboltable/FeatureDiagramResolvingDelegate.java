/* (c) https://github.com/MontiCore/monticore */

package de.monticore.featureconfiguration._symboltable;

import de.monticore.featurediagram.FeatureDiagramMill;
import de.monticore.featurediagram._symboltable.FeatureDiagramSymbol;
import de.monticore.featurediagram._symboltable.IFeatureDiagramGlobalScope;
import de.monticore.featurediagram._symboltable.IFeatureDiagramSymbolResolvingDelegate;
import de.monticore.io.paths.ModelPath;
import de.monticore.symboltable.modifiers.AccessModifier;

import java.nio.file.Path;
import java.util.List;
import java.util.function.Predicate;

public class FeatureDiagramResolvingDelegate implements IFeatureDiagramSymbolResolvingDelegate {

  public FeatureDiagramResolvingDelegate(ModelPath mp) {
    for (Path p : mp.getFullPathOfEntries()) {
      FeatureDiagramMill.getFeatureDiagramGlobalScope().getModelPath().addEntry(p);
    }
  }

  @Override public List<FeatureDiagramSymbol> resolveAdaptedFeatureDiagramSymbol(
      boolean foundSymbols, String name, AccessModifier modifier,
      Predicate<FeatureDiagramSymbol> predicate) {
    return FeatureDiagramMill.getFeatureDiagramGlobalScope()
        .resolveFeatureDiagramMany(foundSymbols, name, modifier, predicate);
  }

}
