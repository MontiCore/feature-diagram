/* (c) https://github.com/MontiCore/monticore */
package featurediagram._cocos;

import de.se_rwth.commons.logging.Log;
import featurediagram._ast.ASTConstraintExpression;
import featurediagram._ast.ASTExcludesConstraint;
import featurediagram._ast.ASTRequiresConstraint;

public class CTCFeatureNamesExist implements FeatureDiagramASTRequiresConstraintCoCo, FeatureDiagramASTExcludesConstraintCoCo {

  @Override public void check(ASTRequiresConstraint node) {
    for (String name : node.getNameList()) {
      if (!node.getEnclosingScope().resolveFeature(name).isPresent()) {
        Log.error(
            "0xFD0006 Constraint '" + node + "' operates on undefined feature '" + name + "'!");
      }
    }
  }

  @Override
  public void check(ASTExcludesConstraint node) {
    for (String name : node.getNameList()) {
      if (!node.getEnclosingScope().resolveFeature(name).isPresent()) {
        Log.error(
                "0xFD0006 Constraint '" + node + "' operates on undefined feature '" + name + "'!");
      }
    }
  }
}
