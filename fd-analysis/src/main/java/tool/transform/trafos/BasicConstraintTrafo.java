package tool.transform.trafos;

import featurediagram._ast.ASTExcludesConstraint;
import featurediagram._ast.ASTFeatureConstraint;
import featurediagram._ast.ASTRequiresConstraint;
import featurediagram._symboltable.FeatureDiagramSymbol;
import featurediagram._visitor.FeatureDiagramVisitor;
import tool.transform.FeatureModel2FlatZincModelTrafo;
import tool.transform.flatzinc.Constraint;
import tool.transform.flatzinc.Variable;

import java.util.ArrayList;
import java.util.List;

public class BasicConstraintTrafo implements FeatureModel2FlatZincModelTrafo, FeatureDiagramVisitor {

  private FeatureDiagramSymbol featureModel;
  private List<Constraint> constraints =new ArrayList<>();
  private List<Variable> variables =new ArrayList<>();
  private List<String> names;
  private int varCount = 0;
  @Override
  public void setNames(List<String> names) {
    this.names = names;
  }

  @Override
  public void setFeatureModel(FeatureDiagramSymbol featureModel) {
    this.featureModel = featureModel;
  }

  @Override
  public FeatureDiagramSymbol getFeatureModel() {
    return featureModel;
  }

  @Override
  public List<Constraint> getConstraints() {
    return constraints;
  }

  @Override
  public List<Variable> getVariables() {
    return variables;
  }

  @Override
  public void perform() {
    featureModel.getAstNode().streamFDElements().forEach(
            astfdElement -> {
              if (astfdElement instanceof ASTFeatureConstraint){
                astfdElement.accept(this);
              }
            }
    );
  }

  @Override
  public void visit(ASTRequiresConstraint node) {
    String name = createBoolVar("requires");
    String helperName = createBoolVar("helperRequires");
    constraints.add(new Constraint("bool_not", node.getName(0), helperName));
    constraints.add(new Constraint("bool_or", helperName, node.getName(1), name));
    constraints.add(new Constraint("bool_eq", name, "true"));
  }

  @Override
  public void visit(ASTExcludesConstraint node) {
    String name = createBoolVar("excludes");
    String helperName = createBoolVar("helperExcludes");
    constraints.add(new Constraint("bool_not", name, helperName));
    constraints.add(new Constraint("bool_and", node.getName(0), node.getName(1), helperName));
    constraints.add(new Constraint("bool_eq", name, "true"));
  }

  private String createBoolVar(String name){
    name = name+varCount++;
    Variable variable = new Variable();
    variable.setType(Variable.Type.BOOL);
    variable.setName(name);
    variables.add(variable);
    return name;
  }
}
