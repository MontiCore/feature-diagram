/* (c) https://github.com/MontiCore/monticore */
package de.monticore.featurediagram._cocos;

import de.monticore.featurediagram._ast.ASTFeatureDiagramNode;

/**
 * This class aggregates all Context Conditions of the FD language
 * and provides a method for checking all of these.
 */
public class FeatureDiagramCoCos {

  public static FeatureDiagramCoCoChecker getCheckerForAllCoCos() {
    FeatureDiagramCoCoChecker checker = new FeatureDiagramCoCoChecker();
    checker.addCoCo((FeatureDiagramASTFeatureTreeRuleCoCo) new HasTreeShape());
    checker.addCoCo((FeatureDiagramASTFeatureDiagramCoCo) new HasTreeShape());
    checker.addCoCo(new NonUniqueNameInGroup());
    checker.addCoCo(new CTCFeatureNameExists());
    checker.addCoCo(new ValidConstraintExpression());
    return checker;
  }

  public static void checkAll(ASTFeatureDiagramNode node) {
    getCheckerForAllCoCos().checkAll(node);
  }

}
