/*
 * Copyright (c) 2019 RWTH Aachen. All rights reserved.
 *
 * http://www.se-rwth.de/
 */
package featurediagram._symboltable;

/**
 * Language for Feature Diagrams
 */
public class FeatureDiagramLanguage extends FeatureDiagramLanguageTOP {

  public FeatureDiagramLanguage() {
    super("Feature Diagram", "fd");
  }

  @Override
  protected FeatureDiagramModelLoader provideModelLoader() {
    return new FeatureDiagramModelLoader(this);
  }
}
