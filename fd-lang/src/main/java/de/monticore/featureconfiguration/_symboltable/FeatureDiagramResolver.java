/* (c) https://github.com/MontiCore/monticore */

package de.monticore.featureconfiguration._symboltable;

import de.monticore.featurediagram.FeatureDiagramMill;
import de.monticore.featurediagram.ModelPaths;
import de.monticore.featurediagram._symboltable.FeatureDiagramSymbol;
import de.monticore.featurediagram._symboltable.IFeatureDiagramGlobalScope;
import de.monticore.featurediagram._symboltable.IFeatureDiagramSymbolResolver;
import de.monticore.io.paths.ModelPath;
import de.monticore.symboltable.modifiers.AccessModifier;

import java.util.List;
import java.util.function.Predicate;

public class FeatureDiagramResolver implements IFeatureDiagramSymbolResolver {

  public FeatureDiagramResolver(ModelPath mp) {
    IFeatureDiagramGlobalScope gs = FeatureDiagramMill.globalScope();
    if (null == gs.getFileExt() || gs.getFileExt().isEmpty()) {
      gs.setFileExt("fd");
    }
    ModelPaths.merge(gs.getModelPath(), mp);
  }

  @Override public List<FeatureDiagramSymbol> resolveAdaptedFeatureDiagramSymbol(
      boolean foundSymbols, String name, AccessModifier modifier,
      Predicate<FeatureDiagramSymbol> predicate) {
    return FeatureDiagramMill.globalScope()
        .resolveFeatureDiagramMany(foundSymbols, name, modifier, predicate);
  }

}
