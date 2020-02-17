/* (c) https://github.com/MontiCore/monticore */
package featurediagram._cocos;

import de.se_rwth.commons.logging.Log;
import featurediagram._ast.ASTFeature;
import featurediagram._ast.ASTFeatureTreeRule;

public class NoSelfLoopsInFeatureTree implements FeatureDiagramASTFeatureTreeRuleCoCo {

  @Override public void check(ASTFeatureTreeRule node) {
    String lhs = node.getName();
    for (ASTFeature f : node.getFeatureGroup().getFeatureList()) {
      if (f.getName().equals(lhs)) {
        Log.error("0xFD0003 Feature diagram rules must not introduce self loops!",
            node.get_SourcePositionStart());
      }
    }
  }
}
