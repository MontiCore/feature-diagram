package de.monticore.featureconfigurationpartial._cocos;

import de.monticore.featureconfiguration._ast.ASTFeatureConfigurationNode;

/**
 * This class aggregates all Context Conditions of the PartialFC language
 * and provides a method for checking all of these. Currently, a
 * single context condition is sufficient for checking the well-formedness
 * of PartialFC models.
 */
public class FeatureConfigurationPartialCoCos {

  public static FeatureConfigurationPartialCoCoChecker getCheckerForAllCoCos(){
    FeatureConfigurationPartialCoCoChecker checker = new FeatureConfigurationPartialCoCoChecker();
    checker.addCoCo(new UseSelectBlock());
    return checker;
  }

  public static void checkAll(ASTFeatureConfigurationNode node){
    getCheckerForAllCoCos().checkAll(node);
  }
}
