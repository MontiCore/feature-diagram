/* (c) https://github.com/MontiCore/monticore */
package featurediagram._symboltable.serialization;

import featurediagram._symboltable.FeatureDiagramArtifactScope;

import java.nio.file.Paths;

public class FeatureDiagramScopeDeSer extends FeatureDiagramScopeDeSerTOP {

  public static void store(FeatureDiagramArtifactScope modelTopScope) {
    new FeatureDiagramScopeDeSer().store(modelTopScope, Paths.get("target/symbols"));
  }

}
