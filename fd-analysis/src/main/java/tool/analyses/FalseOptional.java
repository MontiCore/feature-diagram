/* (c) https://github.com/MontiCore/monticore */
package tool.analyses;

import featureconfiguration._ast.ASTFeatureConfiguration;
import featureconfigurationpartial._ast.ASTUnselect;
import featureconfigurationpartial._visitor.FeatureConfigurationPartialVisitor;
import tool.transform.FZNModelBuilder;
import tool.util.OptionalFeatureFinder;

import java.util.Collection;
import java.util.List;

public class FalseOptional extends Analysis<List<String>> implements FeatureConfigurationPartialVisitor {

  private List<String> falseOptionals;
  @Override
  public void perform(Collection<ASTFeatureConfiguration> configurations) {
    OptionalFeatureFinder finder = new OptionalFeatureFinder();
    getFeatureModel().accept(finder);
    falseOptionals = finder.getOptionalFeatures();
    for (ASTFeatureConfiguration config : configurations) {
      config.accept(this);
    }
    setResult(falseOptionals);
  }

  @Override
  public FZNModelBuilder getModelBuilder() {
    return new FZNModelBuilder(true);
  }

  public void visit(ASTUnselect node){
    node.streamNames().forEach(name -> falseOptionals.remove(name));
  }
}
