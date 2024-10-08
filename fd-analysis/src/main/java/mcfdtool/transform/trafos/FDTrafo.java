/* (c) https://github.com/MontiCore/monticore */
package mcfdtool.transform.trafos;

import de.monticore.featurediagram.FeatureDiagramMill;
import de.monticore.featurediagram._ast.*;
import de.monticore.featurediagram._symboltable.FeatureSymbol;
import de.monticore.featurediagram._visitor.FeatureDiagramTraverser;
import de.monticore.featurediagram._visitor.FeatureDiagramVisitor2;
import mcfdtool.transform.flatzinc.Constraint;
import mcfdtool.transform.flatzinc.FlatZincModel;
import mcfdtool.transform.flatzinc.Variable;

import java.util.List;
import java.util.stream.Collectors;

/**
 * This class transforms a feature diagram into a flatzinc model
 */
public class FDTrafo implements FeatureDiagramVisitor2 {

  protected FlatZincModel flatZincModel;

  protected String currentGroupParent;

  public void apply(ASTFeatureDiagram fd, FlatZincModel result) {
    this.flatZincModel = result;

    FeatureDiagramTraverser traverser = FeatureDiagramMill.inheritanceTraverser();
    traverser.add4FeatureDiagram(this);
    fd.accept(traverser);

    //add contraint for selecting the root feature
    Constraint rootFeature = new Constraint("bool_eq", "true", fd.getRootFeature());
    flatZincModel.add(rootFeature);

    //add cross tree constraints
    CrossTreeConstraintTrafo.apply(fd, result);
  }

  public void visit(FeatureSymbol feature) {
    //create and add variable holding if a feature is selected
    Variable variable = Variable.newBoolVariable(feature.getName(),
      "output_var");
    flatZincModel.add(variable);

    //create and add variable holding if a feature is unselected
    Variable negated = Variable.newBoolVariable(feature.getName() + "Negated");
    flatZincModel.add(negated);

    //create and add constraints on the feature variables
    Constraint negatedConstraint = new Constraint("bool_not",variable.getName()
      , negated.getName());
    flatZincModel.add(negatedConstraint);
  }

  @Override
  public void visit(ASTFeatureTreeRule node) {
    currentGroupParent = node.getName();
  }

  public void visit(ASTAndGroup andGroup) {
    for (int i = 0; i < andGroup.sizeGroupParts(); i++) {
      ASTGroupPart childFeature = andGroup.getGroupPart(i);
      addFeature(currentGroupParent, childFeature.getName(),childFeature.isOptional());
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

  protected void addFeature(String parent, String child, boolean optional) {
    //@see https://link.springer.com/content/pdf/10.1007%2F11877028_16.pdf
    //if(!parent) then !child
    Constraint constraint = new Constraint("bool_le", child,
        parent);
    flatZincModel.add(constraint);

    //else child in {min, max}
    Constraint constraint2 = new Constraint("bool_le_reif", optional?"false":"true", child,
        parent);
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

    Constraint constraint2 = new Constraint("bool_or", hasZeroChildren, parent, "true");
    flatZincModel.add(constraint2);

    //else sum(children) in {min, max}
    Constraint constraint3 = new Constraint("int_lin_le_reif", negativeFactors, subfeatures , "-" + min, parent);
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
