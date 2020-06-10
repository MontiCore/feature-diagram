/* (c) https://github.com/MontiCore/monticore */
package tool.analyses;

import featureconfiguration._ast.ASTFeatureConfiguration;
import featureconfiguration._ast.ASTFeatures;
import featureconfiguration._visitor.FeatureConfigurationVisitor;
import featureconfigurationpartial._ast.ASTSelect;
import featureconfigurationpartial._visitor.FeatureConfigurationPartialVisitor;
import tool.transform.FZNModelBuilder;
import tool.util.FeatureNameCollector;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;

public class DeadFeature extends Analysis<List<String>> implements FeatureConfigurationPartialVisitor {

  private List<String> deadFeatures;
  @Override
  public void perform(Collection<ASTFeatureConfiguration> configurations) {
    FeatureNameCollector collector = new FeatureNameCollector();
    getFeatureModel().getAstNode().accept(collector);
    deadFeatures = collector.getNames();
    configurations.forEach(configuration -> {
      configuration.accept(this);
    });
    setResult(deadFeatures);
  }

  @Override
  public FZNModelBuilder getModelBuilder() {
    return new FZNModelBuilder(true);
  }

  @Override
  public void visit(ASTSelect node) {
    node.streamNames().forEach(name-> deadFeatures.remove(name));
  }
}
