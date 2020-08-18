/* (c) https://github.com/MontiCore/monticore */
package mcfdtool.analyses;

import de.monticore.featureconfiguration._ast.ASTFeatureConfiguration;
import de.monticore.featureconfigurationpartial._ast.ASTSelect;
import de.monticore.featureconfigurationpartial._visitor.FeatureConfigurationPartialVisitor;
import mcfdtool.transform.FZNModelBuilder;
import mcfdtool.visitors.FeatureNameCollector;

import java.util.Collection;
import java.util.List;

public class DeadFeature extends Analysis<List<String>>
    implements FeatureConfigurationPartialVisitor {

  private List<String> deadFeatures;

  @Override
  public void perform(Collection<ASTFeatureConfiguration> configurations) {
    FeatureNameCollector collector = new FeatureNameCollector();
    getFeatureModel().accept(collector);
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
    node.streamNames().forEach(name -> deadFeatures.remove(name));
  }
}
