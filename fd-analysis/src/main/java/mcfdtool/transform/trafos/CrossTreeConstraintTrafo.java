/* (c) https://github.com/MontiCore/monticore */
package mcfdtool.transform.trafos;

import de.monticore.ast.ASTNode;
import de.monticore.expressions.commonexpressions._ast.*;
import de.monticore.expressions.expressionsbasis._ast.ASTExpression;
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
  public void endVisit(ASTLogicalNotExpression node) {
    addConstraint("bool_not", node, node.getExpression());
  }

  @Override
  public void endVisit(ASTEqualsExpression node) {
    String constraintName = getTypeFromName(node.getLeft()) + "_eq_reif";
    addConstraint(constraintName, node.getLeft(), node.getRight(), node);
  }

  @Override
  public void endVisit(ASTNotEqualsExpression node) {
    String constraintName = getTypeFromName(node.getLeft()) + "_xor";
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
    String conditionNegated = getExpressionVarName(node.getCondition(), true);
    String conditionVarName = variables.get(node.getCondition()).getName();
    String trueVarName = variables.get(node.getTrueExpression()).getName();
    String falseVarName = variables.get(node.getFalseExpression()).getName();
    String posName = varName+"pos";
    String negName = varName+"neg";
    Variable pos = new Variable(posName, Variable.Type.BOOL);
    Variable neg = new Variable(negName, Variable.Type.BOOL);
    flatZincModel.add(pos);
    flatZincModel.add(neg);

    flatZincModel.add(new Constraint("bool_eq_reif", trueVarName, varName, posName));
    flatZincModel.add(new Constraint("bool_eq_reif", falseVarName, varName, negName));
    flatZincModel.add(new Constraint("bool_or", conditionNegated, posName, "true"));
    flatZincModel.add(new Constraint("bool_or", conditionVarName, negName, "true"));
  }

  @Override
  public void endVisit(ASTRequires node) {
    String leftName = getExpressionVarName(node.getLeft(), true);
    String rightName = getExpressionVarName(node.getRight(), false);
    String name = variables.get(node).getName();
    flatZincModel.add(new Constraint("bool_or", leftName, rightName, name));
  }

  @Override
  public void endVisit(ASTExcludes node) {
    String leftName = getExpressionVarName(node.getLeft(), true);
    String rightName = getExpressionVarName(node.getRight(), true);
    String name = variables.get(node).getName();
    flatZincModel.add(new Constraint("bool_or", leftName, rightName, name));
  }

  protected String getExpressionVarName(ASTExpression ast, boolean negate) {
    Variable variable = variables.get(ast);
    if (Variable.Type.BOOL != variable.getType()) {
      if (!negate) {
        return variable.getName() + "IsSelected";
      }
      else {
        return variable.getName() + "IsUnselected";
      }
    }
    if (!negate) {
      return variable.getName();
    }
    else {
      return variable.getName() + "Negated";
    }
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
