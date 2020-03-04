/* (c) https://github.com/MontiCore/monticore */
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
