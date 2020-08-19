/* (c) https://github.com/MontiCore/monticore */

package mcfdtool.transform.trafos;

import de.monticore.ast.ASTNode;
import de.monticore.expressions.commonexpressions._ast.*;
import de.monticore.expressions.expressionsbasis._ast.ASTNameExpression;
import de.monticore.featurediagram._ast.ASTExcludes;
import de.monticore.featurediagram._ast.ASTRequires;
import de.monticore.featurediagram._visitor.FeatureDiagramVisitor;
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
  public void visit(ASTModuloExpression node) {
    addNewVariable(node, Variable.Type.INT, "modExpr" + i++);
  }

  @Override
  public void visit(ASTLessEqualExpression node) {
    addNewVariable(node, Variable.Type.BOOL, "leqExpr" + i++);
  }

  @Override
  public void visit(ASTGreaterEqualExpression node) {
    addNewVariable(node, Variable.Type.BOOL, "geqExpr" + i++);
  }

  @Override
  public void visit(ASTLessThanExpression node) {
    addNewVariable(node, Variable.Type.BOOL, "lessExpr" + i++);
  }

  @Override
  public void visit(ASTGreaterThanExpression node) {
    addNewVariable(node, Variable.Type.BOOL, "greaterExpr" + i++);
  }

  @Override
  public void visit(ASTEqualsExpression node) {
    addNewVariable(node, Variable.Type.BOOL, "eqExpr" + i++);
  }

  @Override
  public void visit(ASTNotEqualsExpression node) {
    addNewVariable(node, Variable.Type.BOOL, "neqExpr" + i++);
  }

  @Override
  public void visit(ASTBooleanAndOpExpression node) {
    addNewVariable(node, Variable.Type.BOOL, "boolAndExpr" + i++);
  }

  @Override
  public void visit(ASTBooleanOrOpExpression node) {
    addNewVariable(node, Variable.Type.BOOL, "boolOrExpr" + i++);
  }

  @Override
  public void visit(ASTBooleanNotExpression node) {
    addNewVariable(node, Variable.Type.BOOL, "boolNotExpr" + i++);
  }

  @Override
  public void visit(ASTLogicalNotExpression node) {
    addNewVariable(node, Variable.Type.BOOL, "logicalNotExpr" + i++);
  }

  @Override
  public void visit(ASTConditionalExpression node) {
    //attention: bool is only a placeholder, actual type is set in endVisit method
    addNewVariable(node, Variable.Type.BOOL, "condExpr" + i++);
  }

  public void endVisit(ASTConditionalExpression node) {
    Variable.Type actualType = names.get(node.getTrueExpression()).getType();
    names.get(node).setType(actualType);
  }

  @Override
  public void visit(ASTExcludes node) {
    addNewVariable(node, Variable.Type.BOOL, "excludes" + i++);
  }

  @Override
  public void visit(ASTRequires node) {
    addNewVariable(node, Variable.Type.BOOL, "requires" + i++);
  }

  @Override
  public void visit(ASTNameExpression node) {
    Variable variable = Variable.newIntVariable(node.getName());
    names.put(node, variable);
  }

  public Map<ASTNode, Variable> getVariables() {
    return names;
  }
}
