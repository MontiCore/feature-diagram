/*
 * Copyright (c) 2019 RWTH Aachen. All rights reserved.
 *
 * http://www.se-rwth.de/
 */
package featureconfiguration._symboltable;

/**
 * Language for feature configurations
 */
public class FeatureConfigurationLanguage extends FeatureConfigurationLanguageTOP {

  public FeatureConfigurationLanguage() {
    super("Feature Configuration", "fc");
  }

  @Override
  protected FeatureConfigurationModelLoader provideModelLoader() {
    return new FeatureConfigurationModelLoader(this);
  }

}
