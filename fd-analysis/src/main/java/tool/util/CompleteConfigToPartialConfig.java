/* (c) https://github.com/MontiCore/monticore */
package tool.util;

import featureconfiguration._ast.ASTFeatureConfiguration;
import featurediagram._symboltable.FeatureDiagramSymbol;

import java.util.HashMap;
import java.util.Map;

public class CompleteConfigToPartialConfig {

  public static Map<String, Boolean> getConfiguration(ASTFeatureConfiguration astConfiguration,
      FeatureDiagramSymbol featureDiagram, boolean notEqualsNull) {
    Map<String, Boolean> configuration = new HashMap<>();
    featureDiagram.getAllFeatures().stream().
        forEach(featureSymbol -> configuration
            .put(featureSymbol.getName(), notEqualsNull ? null : Boolean.FALSE));
    astConfiguration.getSelectedFeatureList().stream().
        forEach(feature -> configuration.replace(feature, Boolean.TRUE));
    return configuration;
  }

}
