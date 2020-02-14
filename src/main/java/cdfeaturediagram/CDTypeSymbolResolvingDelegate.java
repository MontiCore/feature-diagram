/* (c) https://github.com/MontiCore/monticore */
package cdfeaturediagram;

import de.monticore.cd.cd4analysis._symboltable.ICD4AnalysisScope;
import de.monticore.symboltable.modifiers.AccessModifier;
import featurediagram._symboltable.FeatureSymbol;
import featurediagram._symboltable.IFeatureSymbolResolvingDelegate;

import java.util.List;
import java.util.function.Predicate;
import java.util.stream.Collectors;

public class CDTypeSymbolResolvingDelegate implements IFeatureSymbolResolvingDelegate {

  protected ICD4AnalysisScope scope;

  CDTypeSymbolResolvingDelegate(ICD4AnalysisScope scope) {
    this.scope = scope;
  }

  @Override public List<FeatureSymbol> resolveAdaptedFeatureSymbol(boolean foundSymbols,
      String name, AccessModifier modifier, Predicate<FeatureSymbol> predicate) {
    return scope.resolveCDTypeMany(name, modifier).stream()
        .map(cdsym -> new CDType2FeatureAdapter(cdsym))
        .collect(Collectors.toList());
  }

}
