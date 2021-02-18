/* (c) https://github.com/MontiCore/monticore */

package de.monticore.featureconfigurationpartial;

public class FeatureConfigurationPartialMill extends FeatureConfigurationPartialMillTOP {

  public static void initMe(FeatureConfigurationPartialMill a) {
    FeatureConfigurationPartialMillTOP.initMe(a);
    millFeatureConfigurationPartialInheritanceHandler = a;
  }

  public static void reset() {
    FeatureConfigurationPartialMillTOP.reset();
    millFeatureConfigurationPartialInheritanceHandler = null;
  }

}
