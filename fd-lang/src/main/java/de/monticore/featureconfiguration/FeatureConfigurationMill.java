/* (c) https://github.com/MontiCore/monticore */

package de.monticore.featureconfiguration;

//TODO remove if these are resetted in generated mill
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
