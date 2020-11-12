/* (c) https://github.com/MontiCore/monticore */

package de.monticore.featureconfiguration._symboltable;

import de.monticore.featurediagram.FeatureDiagramMill;
import de.monticore.featurediagram.ModelPaths;
import de.monticore.featurediagram._symboltable.FeatureDiagramSymbol;
import de.monticore.featurediagram._symboltable.IFeatureDiagramSymbolResolver;
import de.monticore.io.paths.ModelPath;
import de.monticore.symboltable.modifiers.AccessModifier;

import java.util.List;
import java.util.function.Predicate;

public class FeatureDiagramResolver implements IFeatureDiagramSymbolResolver {

  public FeatureDiagramResolver(ModelPath mp) {
    FeatureDiagramMill.featureDiagramGlobalScope().setModelFileExtension("fd");
    ModelPaths.merge(FeatureDiagramMill.featureDiagramGlobalScope().getModelPath(), mp);
  }

  @Override public List<FeatureDiagramSymbol> resolveAdaptedFeatureDiagramSymbol(
      boolean foundSymbols, String name, AccessModifier modifier,
      Predicate<FeatureDiagramSymbol> predicate) {
    return FeatureDiagramMill.featureDiagramGlobalScope()
        .resolveFeatureDiagramMany(foundSymbols, name, modifier, predicate);
  }

}
