/* (c) https://github.com/MontiCore/monticore */
package featurediagram._cocos;

import de.se_rwth.commons.logging.Log;
import featurediagram._ast.ASTFeatureDiagram;
import featurediagram._symboltable.FeatureSymbol;

import java.util.HashSet;
import java.util.Set;

@Deprecated //This coco is currently covered by other cocos, therefore useless
public class UniqueFeatureNames implements FeatureDiagramASTFeatureDiagramCoCo {

  @Override public void check(ASTFeatureDiagram node) {
    if (!node.isPresentSymbol() || null == node.getSymbol()) {
      Log.error("0xF0004 Feature diagram symbol table has to be created before cocos are checked!",
          node.get_SourcePositionStart());
    }
    Set<String> visitedSymbolNames = new HashSet<>();
    for (FeatureSymbol f : node.getSymbol().getAllFeatures()) {
      if (visitedSymbolNames.contains(f.getName())) {
        Log.error("0xFD0005 Feature names must be unique! Feature diagram '"
                + node.getName() + "' contains at least two features with the name '"
                + f.getName() + "'.",
            node.get_SourcePositionStart());
      }
      else {
        visitedSymbolNames.add(f.getName());
      }
    }
  }

}
