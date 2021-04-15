/* (c) https://github.com/MontiCore/monticore */

package de.monticore.featurediagram.prettyprint;

import de.monticore.expressions.prettyprint.CommonExpressionsPrettyPrinter;
import de.monticore.expressions.prettyprint.ExpressionsBasisPrettyPrinter;
import de.monticore.featurediagram.FeatureDiagramMill;
import de.monticore.featurediagram._ast.ASTFDCompilationUnit;
import de.monticore.featurediagram._ast.ASTFeatureDiagramNode;
import de.monticore.featurediagram._visitor.FeatureDiagramHandler;
import de.monticore.featurediagram._visitor.FeatureDiagramTraverser;
import de.monticore.literals.prettyprint.MCCommonLiteralsPrettyPrinter;
import de.monticore.prettyprint.CardinalityPrettyPrinter;
import de.monticore.prettyprint.IndentPrinter;
import de.monticore.prettyprint.MCBasicsPrettyPrinter;
import de.monticore.types.prettyprint.MCBasicTypesPrettyPrinter;

/**
 * The pretty printer for feature diagrams reuses pretty printers of the languages it inherits from.
 */
public class FeatureDiagramPrettyPrinter implements FeatureDiagramHandler {

  protected FeatureDiagramTraverser traverser;

  @Override
  public void setTraverser(FeatureDiagramTraverser traverser) {
    this.traverser = traverser;
  }

  @Override
  public FeatureDiagramTraverser getTraverser() {
    return traverser;
  }

  public static String print(ASTFeatureDiagramNode node) {
    IndentPrinter printer = new IndentPrinter();
    FeatureDiagramTraverser t = FeatureDiagramMill.traverser();

    t.add4MCBasics(new MCBasicsPrettyPrinter(printer));

    CardinalityPrettyPrinter cardinality = new CardinalityPrettyPrinter(printer);
    t.setCardinalityHandler(cardinality);
    t.add4Cardinality(cardinality);

    CommonExpressionsPrettyPrinter commonExpressions = new CommonExpressionsPrettyPrinter(printer);
    t.setCommonExpressionsHandler(commonExpressions);
    t.add4CommonExpressions(commonExpressions);

    ExpressionsBasisPrettyPrinter expressionsBasis = new ExpressionsBasisPrettyPrinter(printer);
    t.setExpressionsBasisHandler(expressionsBasis);
    t.add4ExpressionsBasis(expressionsBasis);

    MCBasicTypesPrettyPrinter mCBasicTypes = new MCBasicTypesPrettyPrinter(printer);
    t.setMCBasicTypesHandler(mCBasicTypes);
    t.add4MCBasicTypes(mCBasicTypes);

    MCCommonLiteralsPrettyPrinter mCCommonLiterals = new MCCommonLiteralsPrettyPrinter(printer);
    t.setMCCommonLiteralsHandler(mCCommonLiterals);
    t.add4MCCommonLiterals(mCCommonLiterals);

    t.add4FeatureDiagram(new FeatureDiagramPrinter(printer));
    t.setFeatureDiagramHandler(new FeatureDiagramPrettyPrinter());


    node.accept(t);
    return printer.getContent();
  }

  public void handle(de.monticore.featurediagram._ast.ASTRequires node) {
    if (null != node.getLeft()) {
      node.getLeft().accept(getTraverser());
    }
    getTraverser().visit(node);
    if (null != node.getRight()) {
      node.getRight().accept(getTraverser());
    }
    getTraverser().endVisit(node);
  }

  public void handle(de.monticore.featurediagram._ast.ASTExcludes node) {
    if (null != node.getLeft()) {
      node.getLeft().accept(getTraverser());
    }
    getTraverser().visit(node);
    if (null != node.getRight()) {
      node.getRight().accept(getTraverser());
    }
    getTraverser().endVisit(node);
  }

  public void traverse(ASTFDCompilationUnit node) {
    if (null != node.getFeatureDiagram()) {
      node.getFeatureDiagram().accept(getTraverser());
    }
  }

}
