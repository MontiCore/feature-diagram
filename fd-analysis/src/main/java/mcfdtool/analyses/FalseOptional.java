/* (c) https://github.com/MontiCore/monticore */
package mcfdtool.analyses;

import de.monticore.featureconfiguration._ast.ASTFeatureConfiguration;
import de.monticore.featureconfigurationpartial._ast.ASTUnselect;
import de.monticore.featureconfigurationpartial._visitor.FeatureConfigurationPartialVisitor;
import mcfdtool.transform.FZNModelBuilder;
import mcfdtool.visitors.OptionalFeatureFinder;

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