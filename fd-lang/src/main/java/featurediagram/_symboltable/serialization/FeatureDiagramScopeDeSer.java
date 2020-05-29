/* (c) https://github.com/MontiCore/monticore */
package featurediagram._symboltable.serialization;

import featurediagram._symboltable.FeatureDiagramArtifactScope;

import java.nio.file.Paths;

public class FeatureDiagramScopeDeSer extends FeatureDiagramScopeDeSerTOP {

  private static FeatureDiagramScopeDeSer instance;

  public static FeatureDiagramScopeDeSer getInstance() {
    if (null == instance) {
      instance = new FeatureDiagramScopeDeSer();
    }
    return instance;
  }

  public static void setInstance(FeatureDiagramScopeDeSer instance) {
    FeatureDiagramScopeDeSer.instance = instance;
  }

  public static void store(FeatureDiagramArtifactScope modelTopScope) {
    getInstance().store(modelTopScope, Paths.get("target/symbols"));
  }

}
