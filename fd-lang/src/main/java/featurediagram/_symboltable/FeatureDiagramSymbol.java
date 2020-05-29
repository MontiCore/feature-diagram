/* (c) https://github.com/MontiCore/monticore */
package featurediagram._symboltable;

import java.util.ArrayList;
import java.util.List;

public class FeatureDiagramSymbol extends FeatureDiagramSymbolTOP {

  public FeatureDiagramSymbol(String name) {
    super(name);
  }

  public List<FeatureSymbol> getAllFeatures() {
    return resolveAllFeatureSymbolsDown(getSpannedScope());
  }

  /**
   * This method considers nexted subscopes, which can occur through imported feature tree parts
   * @param currentScope
   * @return
   */
  protected List<FeatureSymbol> resolveAllFeatureSymbolsDown(IFeatureDiagramScope currentScope) {
    List<FeatureSymbol> result = new ArrayList<>();
    result.addAll(currentScope.getLocalFeatureSymbols());
    for (IFeatureDiagramScope s : currentScope.getSubScopes()) {
      result.addAll(resolveAllFeatureSymbolsDown(s));
    }
    return result;
  }

}
