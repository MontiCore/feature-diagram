/* (c) https://github.com/MontiCore/monticore */
package tool.util;

import featureconfiguration._ast.ASTFeatureConfiguration;
import featureconfiguration._ast.ASTFeatures;
import featureconfiguration._visitor.FeatureConfigurationVisitor;
import de.monticore.featurediagram._symboltable.FeatureDiagramSymbol;
import featurediagram._visitor.FeatureDiagramVisitor;

import java.util.HashMap;
import java.util.Map;

public class CompleteConfigToPartialConfig implements FeatureConfigurationVisitor {

  private Map<String, Boolean> config = new HashMap<>();

  public static Map<String, Boolean> getConfiguration(ASTFeatureConfiguration astConfiguration,
      FeatureDiagramSymbol featureDiagram, boolean notEqualsNull) {
    CompleteConfigToPartialConfig visitor = new CompleteConfigToPartialConfig();
    featureDiagram.getAllFeatures().stream().
            forEach(featureSymbol -> visitor.config
                    .put(featureSymbol.getName(), notEqualsNull ? Boolean.FALSE: null));
    astConfiguration.accept(visitor);
    return visitor.config;
  }

  public void visit(ASTFeatures features){
    features.streamNames().forEach(feature ->
      config.replace(feature, Boolean.TRUE));
  }

}
