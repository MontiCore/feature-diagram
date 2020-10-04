/* (c) https://github.com/MontiCore/monticore */

package mcfdtool.transform.trafos;

import de.monticore.ast.ASTNode;
import de.monticore.expressions.commonexpressions._ast.*;
import de.monticore.expressions.expressionsbasis._ast.ASTNameExpression;
import de.monticore.featurediagram._ast.ASTExcludes;
import de.monticore.featurediagram._ast.ASTRequires;
import de.monticore.featurediagram._visitor.FeatureDiagramVisitor;
import mcfdtool.transform.flatzinc.Constraint;
import mcfdtool.transform.flatzinc.FlatZincModel;
import mcfdtool.transform.flatzinc.Variable;

import java.util.HashMap;
import java.util.Map;

/**
 * This creates FlatZinc variables with unique names for parts of cross-tree constraints
 */
public class CTCVariableCreator implements FeatureDiagramVisitor {

  protected int i;

  FlatZincModel flatZincModel;

  private Map<ASTNode, Variable> names;

  public CTCVariableCreator(FlatZincModel flatZincModel) {
    i = 1;
    names = new HashMap<>();
    this.flatZincModel = flatZincModel;
  }

  protected void addNewVariable(ASTNode node, Variable.Type type, String name) {
    Variable variable = new Variable(name, type, "var_is_introduced");
    flatZincModel.add(variable);
    names.put(node, variable);
  }

  protected void addNewBoolVariable(ASTNode node, String name) {
    Variable variable = new Variable(name + "Negated", Variable.Type.BOOL, "var_is_introduced");
    flatZincModel.add(variable);
    flatZincModel.add(new Constraint("bool_not", name, name + "Negated"));
    addNewVariable(node, Variable.Type.BOOL, name);
  }

  @Override
  public void visit(ASTMultExpression node) {
    //attention: int is only a placeholder, actual type is set in endVisit method
    addNewVariable(node, Variable.Type.INT, "multExpr" + i++);
  }

  public void endVisit(ASTMultExpression node) {
    Variable.Type actualType = names.get(node.getLeft()).getType();
    names.get(node).setType(actualType);
  }

  @Override
  public void visit(ASTDivideExpression node) {
    //attention: int is only a placeholder, actual type is set in endVisit method
    addNewVariable(node, Variable.Type.INT, "divExpr" + i++);
  }

  public void endVisit(ASTDivideExpression node) {
    Variable.Type actualType = names.get(node.getLeft()).getType();
    names.get(node).setType(actualType);
  }

  @Override
  public void visit(ASTPlusExpression node) {
    //attention: int is only a placeholder, actual type is set in endVisit method
    addNewVariable(node, Variable.Type.INT, "plusExpr" + i++);
  }

  public void endVisit(ASTPlusExpression node) {
    Variable.Type actualType = names.get(node.getLeft()).getType();
    names.get(node).setType(actualType);
  }

  @Override
  public void visit(ASTMinusExpression node) {
    //attention: int is only a placeholder, actual type is set in endVisit method
    addNewVariable(node, Variable.Type.INT, "minusExpr" + i++);
  }

  public void endVisit(ASTMinusExpression node) {
    Variable.Type actualType = names.get(node.getLeft()).getType();
    names.get(node).setType(actualType);
  }

  @Override
  public void visit(ASTEqualsExpression node) {
    addNewBoolVariable(node, "eqExpr" + i++);
  }

  @Override
  public void visit(ASTNotEqualsExpression node) {
    addNewBoolVariable(node, "neqExpr" + i++);
  }

  @Override
  public void visit(ASTBooleanAndOpExpression node) {
    addNewBoolVariable(node, "boolAndExpr" + i++);
  }

  @Override
  public void visit(ASTBooleanOrOpExpression node) {
    addNewBoolVariable(node, "boolOrExpr" + i++);
  }

  @Override
  public void visit(ASTLogicalNotExpression node) {
    addNewBoolVariable(node, "logicalNotExpr" + i++);
  }

  @Override
  public void visit(ASTConditionalExpression node) {
    //attention: bool is only a placeholder, actual type is set in endVisit method
    addNewBoolVariable(node, "condExpr" + i++);
  }

  public void endVisit(ASTConditionalExpression node) {
    Variable.Type actualType = names.get(node.getTrueExpression()).getType();
    names.get(node).setType(actualType);
  }

  @Override
  public void visit(ASTExcludes node) {
    addNewBoolVariable(node, "excludes" + i++);
  }

  @Override
  public void visit(ASTRequires node) {
    addNewBoolVariable(node, "requires" + i++);
  }

  @Override
  public void visit(ASTNameExpression node) {
    Variable variable = Variable.newIntVariable(node.getName());
    names.put(node, variable);
  }

  @Override
  public void endVisit(ASTBracketExpression node) {
    names.put(node, names.get(node.getExpression()));
  }

  public Map<ASTNode, Variable> getVariables() {
    return names;
  }
}
