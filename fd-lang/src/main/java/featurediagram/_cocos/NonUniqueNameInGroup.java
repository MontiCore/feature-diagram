/* (c) https://github.com/MontiCore/monticore */
package featurediagram._cocos;

import de.se_rwth.commons.logging.Log;
import featurediagram._ast.ASTFeature;
import featurediagram._ast.ASTFeatureGroup;
import featurediagram._symboltable.FeatureSymbol;

import java.util.ArrayList;
import java.util.List;

public class NonUniqueNameInGroup implements FeatureDiagramASTFeatureGroupCoCo {

  @Override public void check(ASTFeatureGroup node) {
    List<String> featureNames = new ArrayList<>();
    for (FeatureSymbol f : node.getFeatures()) {
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
