/* (c) https://github.com/MontiCore/monticore */
package de.monticore.featurediagram._cocos;

import de.monticore.expressions.commonexpressions._ast.*;
import de.monticore.expressions.expressionsbasis._ast.ASTExpression;
import de.monticore.expressions.prettyprint.CommonExpressionsPrettyPrinter;
import de.monticore.featurediagram._ast.ASTFeatureConstraint;
import de.monticore.featurediagram._visitor.FeatureDiagramVisitor;
import de.monticore.prettyprint.IndentPrinter;
import de.se_rwth.commons.SourcePosition;
import de.se_rwth.commons.logging.Log;

/**
 * This CoCo checks, whether an Expression used as cross- tree constraint
 * uses a forbidden kind of expression.
 */
public class ValidConstraintExpression
    implements FeatureDiagramASTFeatureConstraintCoCo, FeatureDiagramVisitor {

  protected ASTExpression expression;

  private void error(String expressionKind, SourcePosition pos) {
    // pretty print expression for error message
    CommonExpressionsPrettyPrinter pp = new CommonExpressionsPrettyPrinter(new IndentPrinter());
    String exprString = pp.prettyprint(expression);
    Log.error("0xFD011 The cross-tree constraint '" + exprString + "' uses a " + expressionKind
        + ", which is forbidden!", pos);
  }

  @Override public void check(ASTFeatureConstraint node) {
    expression = node.getConstraint();
    expression.accept(this);
  }

  @Override public void visit(ASTCallExpression node) {
    error("method call", node.get_SourcePositionStart());
  }

  @Override public void visit(ASTFieldAccessExpression node) {
    error("'field access'", node.get_SourcePositionStart());
  }
  
  @Override
  public void visit(ASTPlusPrefixExpression node){
    error("'+'", node.get_SourcePositionStart());
  }
  
  public void visit(ASTMinusPrefixExpression node){
    error("'-'", node.get_SourcePositionStart());
  }

  @Override public void visit(ASTBooleanNotExpression node) {
    error("'~'", node.get_SourcePositionStart());
  }

  @Override public void visit(ASTMultExpression node) {
    error("'*'", node.get_SourcePositionStart());
  }

  @Override public void visit(ASTDivideExpression node) {
    error("'/'", node.get_SourcePositionStart());
  }

  @Override public void visit(ASTModuloExpression node) {
    error("'%'", node.get_SourcePositionStart());
  }

  @Override public void visit(ASTPlusExpression node) {
    error("'+'", node.get_SourcePositionStart());
  }

  @Override public void visit(ASTMinusExpression node) {
    error("'-'", node.get_SourcePositionStart());
  }

  @Override public void visit(ASTLessEqualExpression node) {
    error("'<='", node.get_SourcePositionStart());
  }

  @Override public void visit(ASTGreaterEqualExpression node) {
    error("'>='", node.get_SourcePositionStart());
  }

  @Override public void visit(ASTLessThanExpression node) {
    error("'<'", node.get_SourcePositionStart());
  }

  @Override public void visit(ASTGreaterThanExpression node) {
    error("'>'", node.get_SourcePositionStart());
  }

  @Override public void visit(ASTLogicalNotExpression node) {
    // this is allowed
  }

  @Override public void visit(ASTEqualsExpression node) {
    // this is allowed
  }

  @Override public void visit(ASTNotEqualsExpression node) {
    // this is allowed
  }

  @Override public void visit(ASTBooleanAndOpExpression node) {
    // this is allowed
  }

  @Override public void visit(ASTBooleanOrOpExpression node) {
    // this is allowed
  }

  @Override public void visit(ASTConditionalExpression node) {
    // this is allowed
  }

  @Override public void visit(ASTBracketExpression node) {
    // this is allowed
  }

}
