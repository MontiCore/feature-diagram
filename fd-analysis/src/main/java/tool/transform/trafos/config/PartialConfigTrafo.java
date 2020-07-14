/* (c) https://github.com/MontiCore/monticore */
package tool.transform.trafos.config;

import de.monticore.featureconfiguration._ast.ASTFeatureConfiguration;
import de.monticore.featureconfigurationpartial._ast.ASTSelect;
import de.monticore.featureconfigurationpartial._ast.ASTUnselect;
import de.monticore.featureconfigurationpartial._visitor.FeatureConfigurationPartialVisitor;
import de.monticore.featurediagram._symboltable.FeatureDiagramSymbol;
import tool.transform.FeatureModel2FlatZincModelTrafo;
import tool.transform.flatzinc.Constraint;
import tool.transform.flatzinc.Variable;

import java.util.ArrayList;
import java.util.List;

public class PartialConfigTrafo
    implements FeatureModel2FlatZincModelTrafo, FeatureConfigurationPartialVisitor {
  private List<Constraint> constraints = new ArrayList<>();

  private ASTFeatureConfiguration configuration;

  public PartialConfigTrafo(ASTFeatureConfiguration configuration) {
    this.configuration = configuration;
  }

  @Override
  public void setNames(List<String> names) {
  }

  @Override
  public FeatureDiagramSymbol getFeatureModel() {
    return null;
  }

  @Override
  public void setFeatureModel(FeatureDiagramSymbol featureModel) {
  }

  @Override
  public List<Constraint> getConstraints() {
    return constraints;
  }

  @Override
  public List<Variable> getVariables() {
    return new ArrayList<>();
  }

  @Override
  public void perform() {

  }

  @Override
  public void visit(ASTSelect node) {
    node.streamNames().
        forEach(f -> constraints.add(new Constraint("int_eq", f, "1")));
  }

  @Override
  public void visit(ASTUnselect node) {
    node.streamNames().
        forEach(f -> constraints.add(new Constraint("int_eq", f, "0")));
  }
}
