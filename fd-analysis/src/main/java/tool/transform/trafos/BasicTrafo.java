/* (c) https://github.com/MontiCore/monticore */
package tool.transform.trafos;

import de.monticore.featurediagram._ast.ASTFeatureGroup;
import de.monticore.featurediagram._symboltable.FeatureDiagramSymbol;
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
    featureModel.getAstNode().accept(this);
  }

  public void visit(FeatureSymbol feature) {
    Variable variable = new Variable();
    variable.setName(feature.getName());
    variable.setType(Variable.Type.INT);
    variables.add(variable);
    variable.setLowerLimit("0");
    variable.setUpperLimit("1");
    variable.setAnnotation("output_var");
    Variable isSelected = new Variable();
    isSelected.setName(feature.getName()+"IsSelected");
    isSelected.setType(Variable.Type.BOOL);
    variables.add(isSelected);
    Variable isUnselected = new Variable();
    isUnselected.setName(feature.getName()+"IsUnselected");
    isUnselected.setType(Variable.Type.BOOL);
    variables.add(isUnselected);
    Constraint isSelectedConstraint = new Constraint("bool2int", feature.getName()+"IsSelected", feature.getName());
    constraints.add(isSelectedConstraint);
    Constraint isUnselectedConstraint = new Constraint("bool_not", feature.getName()+"IsUnselected", feature.getName()+"IsSelected");
    constraints.add(isUnselectedConstraint);
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
    //@see https://link.springer.com/content/pdf/10.1007%2F11877028_16.pdf
    //if(parent = 0) then child = 0
    Constraint constraint = new Constraint("bool_le", child.getName()+"IsSelected", parent.getName()+"IsSelected");
    //else child in {min, max}
    Constraint constraint2 = new Constraint("int_le_reif", min, child.getName(), parent.getName()+"IsSelected");
    constraints.add(constraint);
    constraints.add(constraint2);
  }

  private void addCardinalGroup(FeatureSymbol parent, ASTFeatureGroup children, String min,
      String max) {
    String helperName2 = createNewHelper(parent.getName() + "HasZeroChildren", Variable.Type.BOOL);
    String subfeatures = children.streamGroupParts()
        .map(ASTGroupPart::getName).collect(Collectors.joining(","));
    String factors = children.streamGroupParts().map(t -> "1").collect(Collectors.joining(","));
    String negativeFactors = children.streamGroupParts().map(t -> "-1")
        .collect(Collectors.joining(","));
    //if (parent = 0) then children = 0
    Constraint constraint1 = new Constraint("int_lin_eq_reif", "[" + factors + "]",
        "[" + subfeatures + "]", "0", helperName2);
    Constraint constraint2 = new Constraint("bool_or", helperName2, parent.getName()+"IsSelected", "true" );
    //else sum(children) in {min, max}
    Constraint constraint3 = new Constraint("int_lin_le_reif", "[" + negativeFactors + "]",
        "[" + subfeatures + "]", "-" + min, parent.getName()+"IsSelected");
    Constraint constraint4 = new Constraint("int_lin_le", "[" + factors + "]",
        "[" + subfeatures + "]", max);
    constraints.add(constraint1);
    constraints.add(constraint2);
    constraints.add(constraint3);
    constraints.add(constraint4);
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
