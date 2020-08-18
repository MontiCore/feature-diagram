/* (c) https://github.com/MontiCore/monticore */
package mcfdtool.visitors;

import de.monticore.featureconfiguration._ast.ASTFeatureConfiguration;
import de.monticore.featureconfiguration._ast.ASTFeatures;
import de.monticore.featureconfiguration._visitor.FeatureConfigurationVisitor;
import de.monticore.featurediagram._ast.ASTFeatureDiagram;

import java.util.HashMap;
import java.util.Map;

// TODO: comment, explain class

public class CompleteConfigToPartialConfig implements FeatureConfigurationVisitor {

  private Map<String, Boolean> config = new HashMap<>();

  public static Map<String, Boolean> getConfiguration(ASTFeatureConfiguration astConfiguration,
      ASTFeatureDiagram featureDiagram, boolean notEqualsNull) {
    CompleteConfigToPartialConfig visitor = new CompleteConfigToPartialConfig();
    featureDiagram.getSymbol().getAllFeatures().stream().
        forEach(featureSymbol -> visitor.config
            .put(featureSymbol.getName(), notEqualsNull ? Boolean.FALSE : null));
    astConfiguration.accept(visitor);
    return visitor.config;
  }

  public void visit(ASTFeatures features) {
    features.streamNames().forEach(feature ->
        config.replace(feature, Boolean.TRUE));
  }

}
