/* (c) https://github.com/MontiCore/monticore */
package cdfeaturediagram;

import de.monticore.symboltable.modifiers.AccessModifier;
import featurediagram._symboltable.FeatureDiagramScope;
import featurediagram._symboltable.FeatureSymbol;
import featurediagram._symboltable.IFeatureDiagramScope;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Predicate;

public class CDTypeFeatureDiagramScope extends FeatureDiagramScope {
  public CDTypeFeatureDiagramScope() {
  }

  public CDTypeFeatureDiagramScope(boolean shadowing) {
    super(shadowing);
  }

  public CDTypeFeatureDiagramScope(IFeatureDiagramScope enclosingScope) {
    super(enclosingScope);
  }

  public CDTypeFeatureDiagramScope(IFeatureDiagramScope enclosingScope,
      boolean shadowing) {
    super(enclosingScope, shadowing);
  }

  //  @Override
  //  public List<FeatureSymbol> resolveAdaptedFeatureLocallyMany(boolean foundSymbols,
  //      String name, AccessModifier modifier, Predicate<FeatureSymbol> predicate) {
  //    return null;
  //  }

  @Override public List<FeatureSymbol> resolveFeatureLocallyMany(boolean foundSymbols, String name,
      AccessModifier modifier, Predicate<FeatureSymbol> predicate) {
    // Avoid finding local feature symbols. Instead, only find adapted ones
    // that are acutally cdtype symbols
    return new ArrayList<>();
  }
}
