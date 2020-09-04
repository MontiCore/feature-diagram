package de.monticore.featureconfigurationpartial._cocos;

import de.monticore.featureconfiguration._ast.ASTFeatureConfigurationNode;

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
