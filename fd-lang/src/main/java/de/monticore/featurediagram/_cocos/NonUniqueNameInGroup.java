/* (c) https://github.com/MontiCore/monticore */
package de.monticore.featurediagram._cocos;

import de.monticore.featurediagram._ast.ASTFeatureGroup;
import de.monticore.featurediagram._symboltable.FeatureSymbol;
import de.se_rwth.commons.logging.Log;

import java.util.ArrayList;
import java.util.List;

/**
 * Checks, whether a feature is unique among the members of a feature group.
 */
public class NonUniqueNameInGroup implements FeatureDiagramASTFeatureGroupCoCo {

  @Override public void check(ASTFeatureGroup node) {
    List<String> featureNames = new ArrayList<>();
    for (FeatureSymbol f : node.getSubFeatureSymbols()) {
      if (featureNames.contains(f.getName())) {
        Log.error(
            "0xFD009 A Feature group must not contain a feature more than once! '" + f.getName()
                + "' is contained in a group multiple times.",
            node.get_SourcePositionStart());
      }
      else {
        featureNames.add(f.getName());
      }
    }
  }
}
