/* (c) https://github.com/MontiCore/monticore */
package tool.transform.trafos;

import featurediagram._ast.*;
import featurediagram._symboltable.FeatureDiagramSymbol;
import featurediagram._symboltable.FeatureSymbol;
import featurediagram._visitor.FeatureDiagramVisitor;
import tool.transform.FeatureModel2FlatZincModelTrafo;
import tool.transform.flatzinc.Constraint;
import tool.transform.flatzinc.Variable;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.stream.Collectors;

public class BasicTrafo
    implements FeatureModel2FlatZincModelTrafo, FeatureDiagramVisitor {
  private FeatureDiagramSymbol featureModel;

  private List<String> names = new ArrayList<>();

  private List<Constraint> constraints = new ArrayList<>();

  private List<Variable> variables = new ArrayList<>();

  private Random random = new Random();

  private ASTFeatureTreeRule currentGroupparent;

  @Override
  public void setNames(List<String> names) {
    this.names = names;
  }

  @Override
  public FeatureDiagramSymbol getFeatureModel() {
    return this.featureModel;
  }

  @Override
  public void setFeatureModel(FeatureDiagramSymbol featureModel) {
    this.featureModel = featureModel;
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
    featureModel.getSpannedScope().accept(this);
  }

  public void visit(FeatureSymbol feature) {
    Variable variable = new Variable();
    variable.setName(feature.getName());
    variable.setType(Variable.Type.INT);
    variables.add(variable);
    variable.setLowerLimit("0");
    variable.setUpperLimit("1");
    variable.setAnnotation("output_var");
  }

  @Override
  public void visit(ASTFeatureTreeRule node){
    currentGroupparent = node;
  }

  public void visit(ASTAndGroup andGroup){
    for(int i = 0; i < andGroup.sizeGroupParts(); i++) {
      ASTGroupPart childFeature = andGroup.getGroupPart(i);
      String min;
      String max;
      if (childFeature.isOptional()) {
        min = "0";
        max = "1";
      }
      else {
        min = "1";
        max = "1";
      }
      addCardinalFeature(currentGroupparent.getNameSymbol(), childFeature.getNameSymbol(), min, max);
    }
  }

  public void visit(ASTOrGroup orGroup){
    addCardinalGroup(currentGroupparent.getNameSymbol(), orGroup, "1", ""+orGroup.sizeGroupParts());
  }

  public void visit(ASTXorGroup xOrGroup){
    addCardinalGroup(currentGroupparent.getNameSymbol(), xOrGroup, "1", "1");
  }

  public void visit(ASTCardinalizedGroup cardinalityGroup){
    addCardinalGroup(currentGroupparent.getNameSymbol(), cardinalityGroup, ""+cardinalityGroup.getCardinality().getLowerBound(), ""+cardinalityGroup.getCardinality().getUpperBound());
  }

  private void addCardinalFeature(FeatureSymbol parent, FeatureSymbol child, String min,
      String max) {
    String helperName1 = createNewHelper(parent.getName() + "IsUnselected", Variable.Type.BOOL);
    String helperName2 = createNewHelper(child.getName() + "IsUnselected", Variable.Type.BOOL);
    String helperName3 = createNewHelper(parent.getName() + "IsSelected", Variable.Type.BOOL);
    //@see https://link.springer.com/content/pdf/10.1007%2F11877028_16.pdf
    //if(parent = 0) then child = 0
    Constraint constraint1 = new Constraint("int_eq_reif", "0", parent.getName(), helperName1);
    Constraint constraint2 = new Constraint("int_eq_reif", "0", child.getName(), helperName2);
    Constraint constraint3 = new Constraint("bool_not", helperName1, helperName3);
    Constraint constraint4 = new Constraint("bool_or", helperName3, helperName2, "true");
    //else child in {min, max}
    Constraint constraint5 = new Constraint("int_le_reif", min, child.getName(), helperName3);
    constraints.add(constraint1);
    constraints.add(constraint2);
    constraints.add(constraint3);
    constraints.add(constraint4);
    constraints.add(constraint5);
  }

  private void addCardinalGroup(FeatureSymbol parent, ASTFeatureGroup children, String min,
      String max) {
    String helperName1 = createNewHelper(parent.getName() + "IsZero", Variable.Type.BOOL);
    String helperName2 = createNewHelper(parent.getName() + "HasZeroChildren", Variable.Type.BOOL);
    String helperName3 = createNewHelper(parent.getName() + "IsNotZero", Variable.Type.BOOL);
    String subfeatures = children.streamGroupParts()
        .map(ASTGroupPart::getName).collect(Collectors.joining(","));
    String factors = children.streamGroupParts().map(t -> "1").collect(Collectors.joining(","));
    String negativeFactors = children.streamGroupParts().map(t -> "-1")
        .collect(Collectors.joining(","));
    //if (parent = 0) then children = 0
    Constraint constraint1 = new Constraint("int_eq_reif", "0", parent.getName(), helperName1);
    Constraint constraint2 = new Constraint("int_lin_eq_reif", "[" + factors + "]",
        "[" + subfeatures + "]", "0", helperName2);
    Constraint constraint3 = new Constraint("bool_clause", "[" + helperName1 + "]",
        "[" + helperName2 + "]");
    //else sum(children) in {min, max}
    Constraint constraint4 = new Constraint("bool_not", helperName1, helperName3);
    Constraint constraint5 = new Constraint("int_lin_le_reif", "[" + negativeFactors + "]",
        "[" + subfeatures + "]", "-" + min, helperName3);
    Constraint constraint6 = new Constraint("int_lin_le", "[" + factors + "]",
        "[" + subfeatures + "]", max);
    constraints.add(constraint1);
    constraints.add(constraint2);
    constraints.add(constraint3);
    constraints.add(constraint4);
    constraints.add(constraint5);
    constraints.add(constraint6);
  }

  private String createNewHelper(String name, Variable.Type type) {
    while (names.contains(name)) {
      name = name + random.nextInt(10);
    }
    Variable helper = new Variable();
    helper.setType(type);
    helper.setName(name);
    helper.setAnnotation("var_is_introduced");
    names.add(name);
    variables.add(helper);
    return name;
  }
}
