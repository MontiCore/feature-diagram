/* (c) https://github.com/MontiCore/monticore */

package de.monticore.featureconfiguration;

public class FeatureConfigurationMill extends FeatureConfigurationMillTOP {

  public static void initMe(FeatureConfigurationMill a) {
    FeatureConfigurationMillTOP.initMe(a);
    millFeatureConfigurationInheritanceHandler = a;
  }

  public static void reset() {
    FeatureConfigurationMillTOP.reset();
    millFeatureConfigurationInheritanceHandler = null;
  }

}
