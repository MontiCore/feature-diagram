/* (c) https://github.com/MontiCore/monticore */

package de.monticore.featureconfiguration._symboltable;

import de.monticore.featurediagram.FeatureDiagramMill;
import de.monticore.featurediagram._symboltable.FeatureDiagramSymbol;
import de.monticore.featurediagram._symboltable.IFeatureDiagramGlobalScope;
import de.monticore.featurediagram._symboltable.IFeatureDiagramSymbolResolvingDelegate;
import de.monticore.io.paths.ModelPath;
import de.monticore.symboltable.modifiers.AccessModifier;

import java.util.List;
import java.util.function.Predicate;

public class FeatureDiagramResolvingDelegate implements IFeatureDiagramSymbolResolvingDelegate {

  IFeatureDiagramGlobalScope globalScope;

  public FeatureDiagramResolvingDelegate(ModelPath mp) {
    globalScope = FeatureDiagramMill
        .featureDiagramGlobalScopeBuilder()
        .setModelFileExtension("fd")
        .setModelPath(mp)
        .build();
  }

  @Override public List<FeatureDiagramSymbol> resolveAdaptedFeatureDiagramSymbol(
      boolean foundSymbols, String name, AccessModifier modifier,
      Predicate<FeatureDiagramSymbol> predicate) {
    return globalScope.resolveFeatureDiagramMany(foundSymbols, name, modifier, predicate);
  }
}
