/* (c) https://github.com/MontiCore/monticore */
package mcfdtool.transform.trafos;

import de.monticore.ast.ASTNode;
import de.monticore.expressions.commonexpressions._ast.*;
import de.monticore.featurediagram._ast.ASTExcludes;
import de.monticore.featurediagram._ast.ASTFeatureConstraint;
import de.monticore.featurediagram._ast.ASTFeatureDiagram;
import de.monticore.featurediagram._ast.ASTRequires;
import de.monticore.featurediagram._visitor.FeatureDiagramVisitor;
import de.se_rwth.commons.logging.Log;
import mcfdtool.transform.flatzinc.Constraint;
import mcfdtool.transform.flatzinc.FlatZincModel;
import mcfdtool.transform.flatzinc.Variable;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * This translated cross-tree constraints of feature diagrams into flatzinc constraints
 */
public class CrossTreeConstraintTrafo implements FeatureDiagramVisitor {

  protected FlatZincModel flatZincModel;

  private Map<ASTNode, Variable> variables;

  public static void apply(ASTFeatureDiagram fd, FlatZincModel result) {
    //create all variables for cross-tree constraints (at once, to yield unique names)
    CTCVariableCreator varCreator = new CTCVariableCreator(result);
    fd.accept(varCreator);

    //then create all flatzinc constraints realizing the cross-tree constraints
    CrossTreeConstraintTrafo trafo = new CrossTreeConstraintTrafo(result,
        varCreator.getVariables());
    fd.accept(trafo);
  }

  protected CrossTreeConstraintTrafo(FlatZincModel result, Map<ASTNode, Variable> variables) {
    this.flatZincModel = result;
    this.variables = variables;
  }

  @Override
  public void endVisit(ASTFeatureConstraint node) {
    Variable var = variables.get(node.getConstraint());
    flatZincModel.add(new Constraint("bool_eq", "true", var.getName()));
  }

  @Override
  public void endVisit(ASTBooleanNotExpression node) {
    addConstraint("bool_not", node, node.getExpression());
  }

  @Override
  public void endVisit(ASTLogicalNotExpression node) {
    addConstraint("bool_not", node, node.getExpression());
  }

  @Override
  public void endVisit(ASTMultExpression node) {
    String constraintName = getTypeFromName(node.getLeft()) + "_times";
    addConstraint(constraintName, node.getLeft(), node.getRight(), node);
  }

  @Override
  public void endVisit(ASTDivideExpression node) {
    String constraintName = getTypeFromName(node.getLeft()) + "_div";
    addConstraint(constraintName, node.getLeft(), node.getRight(), node);
  }

  @Override
  public void endVisit(ASTModuloExpression node) {
    addConstraint("int_mod", node.getLeft(), node.getRight(), node);
  }

  @Override
  public void endVisit(ASTPlusExpression node) {
    String constraintName = getTypeFromName(node.getLeft()) + "_plus";
    addConstraint(constraintName, node.getLeft(), node.getRight(), node);
  }

  @Override
  public void endVisit(ASTMinusExpression node) {
    // l-r=d <=> d+r=l
    String constraintName = getTypeFromName(node.getLeft()) + "_plus";
    addConstraint(constraintName, node.getRight(), node.getLeft(), node);
  }

  @Override
  public void endVisit(ASTEqualsExpression node) {
    String constraintName = getTypeFromName(node.getLeft()) + "_eq_reif";
    addConstraint(constraintName, node.getLeft(), node.getRight(), node);
  }

  @Override
  public void endVisit(ASTLessEqualExpression node) {
    String constraintName = getTypeFromName(node.getLeft()) + "_le_reif";
    addConstraint(constraintName, node.getLeft(), node.getRight(), node);
  }

  @Override
  public void endVisit(ASTGreaterEqualExpression node) {
    // l>=r <=> r<=l
    String constraintName = getTypeFromName(node.getLeft()) + "_le_reif";
    addConstraint(constraintName, node.getRight(), node.getLeft(), node);
  }

  @Override
  public void endVisit(ASTLessThanExpression node) {
    String constraintName = getTypeFromName(node.getLeft()) + "_lt_reif";
    addConstraint(constraintName, node.getLeft(), node.getRight(), node);
  }

  @Override
  public void endVisit(ASTGreaterThanExpression node) {
    // l>r <=> r<l
    String constraintName = getTypeFromName(node.getLeft()) + "_le_reif";
    addConstraint(constraintName, node.getRight(), node.getLeft(), node);
  }

  @Override
  public void endVisit(ASTNotEqualsExpression node) {
    String constraintName = getTypeFromName(node.getLeft()) + "_ne_reif";
    addConstraint(constraintName, node.getLeft(), node.getRight(), node);
  }

  @Override
  public void endVisit(ASTBooleanAndOpExpression node) {
    addConstraint("bool_and", node.getLeft(), node.getRight(), node);
  }

  @Override
  public void endVisit(ASTBooleanOrOpExpression node) {
    addConstraint("bool_or", node.getLeft(), node.getRight(), node);
  }

  @Override
  public void endVisit(ASTConditionalExpression node) {
    String varName = variables.get(node).getName();
    String helperVarName = "helper" + varName;
    String conditionVarName = variables.get(node.getCondition()).getName();
    String trueVarName = variables.get(node.getTrueExpression()).getName();
    String falseVarName = variables.get(node.getFalseExpression()).getName();

    Variable helpervariable = new Variable("helper" + varName, Variable.Type.BOOL,
        "var_is_introduced");
    flatZincModel.add(helpervariable);

    String type = getTypeFromName(node.getTrueExpression());
    flatZincModel.add(new Constraint("bool_not", conditionVarName, helperVarName));
    flatZincModel.add(new Constraint(type + "_eq_reif", trueVarName, varName, conditionVarName));
    flatZincModel.add(new Constraint(type + "_eq_reif", falseVarName, varName, helperVarName));

  }

  @Override
  public void endVisit(ASTRequires node) {
    String leftName = variables.get(node.getLeft()).getName() + "IsUnselected";
    String rightName = variables.get(node.getRight()).getName() + "IsSelected";
    String name = variables.get(node).getName();
    flatZincModel.add(new Constraint("bool_or", leftName, rightName, name));
  }

  @Override
  public void endVisit(ASTExcludes node) {
    String leftName = variables.get(node.getLeft()).getName() + "IsUnselected";
    String rightName = variables.get(node.getRight()).getName() + "IsUnselected";
    String name = variables.get(node).getName();
    flatZincModel.add(new Constraint("bool_or", leftName, rightName, name));
  }

  private String getTypeFromName(ASTNode node) {
    String type = "int"; //by default assume int
    if (variables.containsKey(node)) {
      Variable.Type t = variables.get(node).getType();
      if (Variable.Type.BOOL == t) {
        type = "bool";
      }
      else if (Variable.Type.FLOAT == t) {
        type = "float";
      }
    }
    return type;
  }

  protected void addConstraint(String name, ASTNode... vars) {
    List<String> varNames = new ArrayList<>();
    for (ASTNode node : vars) {
      if (variables.containsKey(node)) {
        String varName = variables.get(node).getName();
        varNames.add(varName);
      }
      else {
        Log.error("0xFC239 Could not find variable for '" + node + "'!");
        return;
      }
    }
    flatZincModel.add(new Constraint(name, varNames));
  }

}
