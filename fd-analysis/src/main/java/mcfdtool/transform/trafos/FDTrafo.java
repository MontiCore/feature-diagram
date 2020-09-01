/* (c) https://github.com/MontiCore/monticore */
package mcfdtool.transform.trafos;

import de.monticore.featurediagram._ast.*;
import de.monticore.featurediagram._symboltable.FeatureSymbol;
import de.monticore.featurediagram._visitor.FeatureDiagramVisitor;
import mcfdtool.transform.flatzinc.Constraint;
import mcfdtool.transform.flatzinc.FlatZincModel;
import mcfdtool.transform.flatzinc.Variable;

import java.util.List;
import java.util.Random;
import java.util.stream.Collectors;

/**
 * This class transforms a feature diagram into a flatzinc model
 */
public class FDTrafo implements FeatureDiagramVisitor {

  protected FlatZincModel flatZincModel;

  protected String currentGroupParent;

  public void apply(ASTFeatureDiagram fd, FlatZincModel result) {
    this.flatZincModel = result;
    fd.accept(this);

    //add contraint for selecting the root feature
    Constraint rootFeature = new Constraint("int_eq", "1", fd.getRootFeature());
    flatZincModel.add(rootFeature);

    //add cross tree constraints
    CrossTreeConstraintTrafo.apply(fd, result);
  }

  public void visit(FeatureSymbol feature) {
    //create and add variable holding how many times a feature is selected (only needed for features with cardinalities)
    Variable variable = Variable.newIntVariable(feature.getName(), "output_var");
    variable.setLowerLimit("0");
    variable.setUpperLimit("1");
    flatZincModel.add(variable);

    //create and add variables holding whether a feature is selected or not
    Variable isSelected = Variable.newBoolVariable(feature.getName() + "IsSelected");
    flatZincModel.add(isSelected);
    Variable isUnselected = Variable.newBoolVariable(feature.getName() + "IsUnselected");
    flatZincModel.add(isUnselected);

    //create and add constraints on the feature variables
    Constraint isSelectedConstraint = new Constraint("bool2int", isSelected.getName(), variable.getName());
    flatZincModel.add(isSelectedConstraint);
    Constraint isUnselectedConstraint = new Constraint("bool_not",isSelected.getName(), isUnselected.getName());
    flatZincModel.add(isUnselectedConstraint);
  }

  @Override
  public void visit(ASTFeatureTreeRule node) {
    currentGroupParent = node.getName();
  }

  public void visit(ASTAndGroup andGroup) {
    for (int i = 0; i < andGroup.sizeGroupParts(); i++) {
      ASTGroupPart childFeature = andGroup.getGroupPart(i);
      if (childFeature.isOptional()) {
        addFeature(currentGroupParent, childFeature.getName(), "0", "1");
      }
      else {
        addFeature(currentGroupParent, childFeature.getName(), "1", "1");
      }
    }
  }

  public void visit(ASTOrGroup orGroup) {
    addGroup(currentGroupParent, orGroup, "1",
        "" + orGroup.sizeGroupParts());
  }

  public void visit(ASTXorGroup xOrGroup) {
    addGroup(currentGroupParent, xOrGroup, "1", "1");
  }

  public void visit(ASTCardinalizedGroup cardinalityGroup) {
    addGroup(currentGroupParent, cardinalityGroup,
        "" + cardinalityGroup.getCardinality().getLowerBound(),
        "" + cardinalityGroup.getCardinality().getUpperBound());
  }

  protected void addFeature(String parent, String child, String min, String max) {
    //@see https://link.springer.com/content/pdf/10.1007%2F11877028_16.pdf
    //if(parent = 0) then child = 0
    Constraint constraint = new Constraint("bool_le", child + "IsSelected",
        parent + "IsSelected");
    flatZincModel.add(constraint);

    //else child in {min, max}
    Constraint constraint2 = new Constraint("int_le_reif", min, child,
        parent + "IsSelected");
    flatZincModel.add(constraint2);
  }

  private void addGroup(String parent, ASTFeatureGroup children, String min, String max) {
    //create a comma-separated list of features that are member of this group
    String subfeatures = "[" + children.streamGroupParts().map(ASTGroupPart::getName).collect(Collectors.joining(",") )+ "]";

    String hasZeroChildren = createUniqueVariable(parent + "HasZeroChildren");

    //create Srings with comma-separated lists containing as many 1 (or -1) as the number of members of this group
    String factors = "[" + children.streamGroupParts().map(t -> "1").collect(Collectors.joining(",")) + "]";
    String negativeFactors = "[" + children.streamGroupParts().map(t -> "-1").collect(Collectors.joining(","))+ "]";

    //if (parent = 0) then children = 0
    Constraint constraint1 = new Constraint("int_lin_eq_reif", factors , subfeatures, "0", hasZeroChildren);
    flatZincModel.add(constraint1);

    Constraint constraint2 = new Constraint("bool_or", hasZeroChildren, parent + "IsSelected", "true");
    flatZincModel.add(constraint2);

    //else sum(children) in {min, max}
    Constraint constraint3 = new Constraint("int_lin_le_reif", negativeFactors, subfeatures , "-" + min, parent + "IsSelected");
    flatZincModel.add(constraint3);

    Constraint constraint4 = new Constraint("int_lin_le", factors, subfeatures, max);
    flatZincModel.add(constraint4);
  }

  private String createUniqueVariable(String name) {
    List<String> allVariableNames = flatZincModel.getVariables()
        .stream().map(Variable::getName)
        .collect(Collectors.toList());

    int suffix = 0;
    String uniqueName = name;
    while (allVariableNames.contains(uniqueName)) {
      uniqueName = name + (++suffix);
    }

    Variable helper = Variable.newBoolVariable(uniqueName, "var_is_introduced");
    flatZincModel.add(helper);
    return uniqueName;
  }

}
