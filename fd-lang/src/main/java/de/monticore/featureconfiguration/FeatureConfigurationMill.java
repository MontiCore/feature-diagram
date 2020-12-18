/* (c) https://github.com/MontiCore/monticore */

package de.monticore.featureconfiguration;

public class FeatureConfigurationMill extends FeatureConfigurationMillTOP {

  public  static  void reset ()  {
    FeatureConfigurationMillTOP.reset();
    millFeatureConfigurationGlobalScope = null;
    millFeatureConfigurationArtifactScope = null;
    millParser = null;
    millFeatureConfigurationScope = null;
    millFeatureConfigurationScopesGenitor = null;
    millFeatureConfigurationScopesGenitorDelegator = null;
  }

}
