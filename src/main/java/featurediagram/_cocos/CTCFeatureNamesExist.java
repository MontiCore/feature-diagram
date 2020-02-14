/* (c) https://github.com/MontiCore/monticore */

package featurediagram._cocos;

import de.se_rwth.commons.logging.Log;
import featurediagram._ast.ASTConstraintExpression;

public class CTCFeatureNamesExist implements FeatureDiagramASTConstraintExpressionCoCo {

  @Override public void check(ASTConstraintExpression node) {
    for (String name : node.getNameList()) {
      if (!node.getEnclosingScope().resolveFeature(name).isPresent()) {
        Log.error(
            "0xFD0006 Constraint '" + node + "' operates on undefined feature '" + name + "'!");
      }
    }
  }

}
