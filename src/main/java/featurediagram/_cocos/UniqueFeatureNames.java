/* (c) https://github.com/MontiCore/monticore */

package featurediagram._cocos;

import de.se_rwth.commons.logging.Log;
import featurediagram._ast.ASTFeatureDiagram;
import featurediagram._symboltable.FeatureSymbol;
import featurediagram._symboltable.IFeatureDiagramScope;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class UniqueFeatureNames implements FeatureDiagramASTFeatureDiagramCoCo {

  @Override public void check(ASTFeatureDiagram node) {
    if (!node.isPresentSymbol() || null == node.getSymbol()) {
      Log.error("0xF0004 Feature diagram symbol table has to be created before cocos are checked!",
          node.get_SourcePositionStart());
    }
    List<FeatureSymbol> featureSymbols = resolveAllFeatureSymbolsDown(
        node.getSymbol().getSpannedScope());

    Set<String> visitedSymbolNames = new HashSet<>();
    for (FeatureSymbol f : featureSymbols) {
      if (visitedSymbolNames.contains(f.getName())) {
        Log.error("0xF0005 Feature names must be unique! Feature diagram '"
                + node + "' contains at least two features with the name '"
                + f.getName() + "'.",
            node.get_SourcePositionStart());
      }
      else {
        visitedSymbolNames.add(f.getName());
      }
    }
  }

  List<FeatureSymbol> resolveAllFeatureSymbolsDown(IFeatureDiagramScope currentScope) {
    List<FeatureSymbol> result = new ArrayList<>();
    result.addAll(currentScope.getLocalFeatureSymbols());
    for (IFeatureDiagramScope s : currentScope.getSubScopes()) {
      result.addAll(resolveAllFeatureSymbolsDown(s));
    }
    return result;
  }
}
