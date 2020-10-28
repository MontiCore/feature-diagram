/* (c) https://github.com/MontiCore/monticore */

package de.monticore.featureconfiguration._symboltable;

import de.monticore.featurediagram.FeatureDiagramMill;
import de.monticore.featurediagram.ModelPaths;
import de.monticore.featurediagram._symboltable.FeatureDiagramGlobalScope;
import de.monticore.featurediagram._symboltable.FeatureDiagramSymbol;
import de.monticore.featurediagram._symboltable.IFeatureDiagramSymbolResolvingDelegate;
import de.monticore.io.paths.ModelPath;
import de.monticore.symboltable.modifiers.AccessModifier;

import java.util.List;
import java.util.function.Predicate;

public class FeatureDiagramResolvingDelegate implements IFeatureDiagramSymbolResolvingDelegate {

  public FeatureDiagramResolvingDelegate(ModelPath mp) {
    FeatureDiagramMill.getFeatureDiagramGlobalScope().setModelFileExtension("fd");
    ModelPaths.merge(FeatureDiagramMill.getFeatureDiagramGlobalScope().getModelPath(), mp);

    // TODO: the following two lines can be removed when switching to MC 6.5.0
    FeatureDiagramMill.getFeatureDiagramGlobalScope().setSymbolFileExtension("fdsym");
    ((FeatureDiagramGlobalScope)FeatureDiagramMill.getFeatureDiagramGlobalScope()).enableModelLoader();
  }

  @Override public List<FeatureDiagramSymbol> resolveAdaptedFeatureDiagramSymbol(
      boolean foundSymbols, String name, AccessModifier modifier,
      Predicate<FeatureDiagramSymbol> predicate) {
    return FeatureDiagramMill.getFeatureDiagramGlobalScope()
        .resolveFeatureDiagramMany(foundSymbols, name, modifier, predicate);
  }

}
