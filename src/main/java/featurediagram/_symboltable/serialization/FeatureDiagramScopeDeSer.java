/* (c) https://github.com/MontiCore/monticore */

package featurediagram._symboltable.serialization;

import featurediagram._symboltable.FeatureDiagramArtifactScope;
import featurediagram._symboltable.FeatureDiagramLanguage;

public class FeatureDiagramScopeDeSer extends FeatureDiagramScopeDeSerTOP {

  public static void store(FeatureDiagramLanguage lang, FeatureDiagramArtifactScope modelTopScope){
    new FeatureDiagramScopeDeSer().store(modelTopScope, lang, "target/symbols");
  }
}
