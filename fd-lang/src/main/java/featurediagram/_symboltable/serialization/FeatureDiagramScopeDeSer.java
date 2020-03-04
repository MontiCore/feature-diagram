/* (c) https://github.com/MontiCore/monticore */
package featurediagram._symboltable.serialization;

import de.se_rwth.commons.logging.Log;
import featurediagram._symboltable.FeatureDiagramArtifactScope;
import featurediagram._symboltable.FeatureDiagramLanguage;
import featurediagram._symboltable.FeatureDiagramSymbol;

import java.util.Optional;

public class FeatureDiagramScopeDeSer extends FeatureDiagramScopeDeSerTOP {

  public static void store(FeatureDiagramLanguage lang, FeatureDiagramArtifactScope modelTopScope){
    new FeatureDiagramScopeDeSer().store(modelTopScope, lang, "target/symbols");
  }

}
