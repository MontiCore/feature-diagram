/* (c) https://github.com/MontiCore/monticore */
package de.monticore.featurediagram._cocos;

import de.monticore.expressions.commonexpressions.CommonExpressionsMill;
import de.monticore.expressions.commonexpressions._ast.*;
import de.monticore.expressions.commonexpressions._visitor.CommonExpressionsVisitor2;
import de.monticore.expressions.expressionsbasis._ast.ASTExpression;
import de.monticore.featurediagram.FeatureDiagramMill;
import de.monticore.featurediagram._ast.ASTFeatureConstraint;
import de.monticore.featurediagram._visitor.FeatureDiagramTraverser;
import de.se_rwth.commons.SourcePosition;
import de.se_rwth.commons.logging.Log;

/**
 * This CoCo checks, whether an Expression used as cross- tree constraint
 * uses a forbidden kind of expression.
 */
public class ValidConstraintExpression implements FeatureDiagramASTFeatureConstraintCoCo {


  @Override public void check(ASTFeatureConstraint node) {
    ASTExpression expression = node.getConstraint();
    Checker checker = new Checker(expression);

    FeatureDiagramTraverser traverser = FeatureDiagramMill.inheritanceTraverser();
    traverser.add4CommonExpressions(checker);
    expression.accept(traverser);
  }

  class Checker implements CommonExpressionsVisitor2 {

    protected ASTExpression expression;

    public Checker(ASTExpression expression){
      this.expression = expression;
    }

    private void error(String expressionKind, SourcePosition pos) {
      // pretty print expression for error message
      String exprString = CommonExpressionsMill.prettyPrint(expression, false);
      Log.error("0xFD011 The cross-tree constraint '" + exprString + "' uses a " + expressionKind
          + ", which is forbidden!", pos);
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

    @Override
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

}
