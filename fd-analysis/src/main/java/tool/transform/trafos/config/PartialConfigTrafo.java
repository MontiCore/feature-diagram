package tool.transform.trafos.config;

import featureconfigurationpartial._ast.ASTFeatureConfiguration;
import featureconfigurationpartial._ast.ASTSelect;
import featureconfigurationpartial._ast.ASTUndecided;
import featureconfigurationpartial._ast.ASTUnselect;
import featureconfigurationpartial._visitor.FeatureConfigurationPartialVisitor;
import featurediagram._ast.ASTExcludesConstraint;
import featurediagram._symboltable.FeatureDiagramSymbol;
import tool.transform.FeatureModel2FlatZincModelTrafo;
import tool.transform.flatzinc.Constraint;
import tool.transform.flatzinc.Variable;

import java.util.ArrayList;
import java.util.List;

public class PartialConfigTrafo implements FeatureModel2FlatZincModelTrafo, FeatureConfigurationPartialVisitor {
  private List<Constraint> constraints = new ArrayList<>();
  private ASTFeatureConfiguration configuration;

  public PartialConfigTrafo(ASTFeatureConfiguration configuration) {
    this.configuration = configuration;
  }

  @Override
  public void setNames(List<String> names) {
  }

  @Override
  public void setFeatureModel(FeatureDiagramSymbol featureModel) {
  }

  @Override
  public FeatureDiagramSymbol getFeatureModel() {
    return null;
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
    node.streamFeatures().
            forEach(f-> constraints.add(new Constraint("int_eq", f, "1")));
  }

  @Override
  public void visit(ASTUnselect node) {
    node.streamFeatures().
            forEach(f-> constraints.add(new Constraint("int_eq", f, "1")));
  }

  @Override
  public void visit(ASTUndecided node) {

  }
}
