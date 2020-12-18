/* (c) https://github.com/MontiCore/monticore */

package de.monticore.featureconfigurationpartial;

import de.monticore.featureconfiguration.FeatureConfigurationMillTOP;

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
