/* (c) https://github.com/MontiCore/monticore */

package de.monticore.featureconfiguration._symboltable;

import de.monticore.featureconfiguration.FeatureConfigurationMill;
import de.monticore.featureconfigurationpartial.FeatureConfigurationPartialMill;
import de.monticore.featurediagram.FeatureDiagramMill;
import de.monticore.featurediagram.ModelPaths;
import de.monticore.featurediagram._symboltable.FeatureDiagramSymbol;
import de.monticore.featurediagram._symboltable.IFeatureDiagramSymbolResolver;
import de.monticore.symboltable.modifiers.AccessModifier;

import java.util.List;
import java.util.function.Predicate;

public class FeatureDiagramResolver implements IFeatureDiagramSymbolResolver {

  @Override public List<FeatureDiagramSymbol> resolveAdaptedFeatureDiagramSymbol(
      boolean foundSymbols, String name, AccessModifier modifier,
      Predicate<FeatureDiagramSymbol> predicate) {
    ModelPaths.merge(FeatureDiagramMill.globalScope().getModelPath(),
        FeatureConfigurationMill.globalScope().getModelPath());
    ModelPaths.merge(FeatureDiagramMill.globalScope().getModelPath(),
        FeatureConfigurationPartialMill.globalScope().getModelPath());
    return FeatureDiagramMill.globalScope()
        .resolveFeatureDiagramMany(foundSymbols, name, modifier, predicate);
  }

}
