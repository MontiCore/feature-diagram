/* (c) https://github.com/MontiCore/monticore */

package de.monticore.featureconfigurationpartial;

//TODO remove if these are resetted in generated mill
public class FeatureConfigurationPartialMill extends FeatureConfigurationPartialMillTOP {

  public  static  void reset ()  {
    FeatureConfigurationPartialMillTOP.reset();
    millFeatureConfigurationPartialGlobalScope = null;
    millFeatureConfigurationPartialArtifactScope = null;
    millParser = null;
    millFeatureConfigurationPartialScope = null;
    millFeatureConfigurationPartialScopesGenitor = null;
    millFeatureConfigurationPartialScopesGenitorDelegator = null;
  }

}
