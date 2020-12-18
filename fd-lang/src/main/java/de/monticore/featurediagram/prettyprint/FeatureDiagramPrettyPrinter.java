/* (c) https://github.com/MontiCore/monticore */

package de.monticore.featurediagram.prettyprint;

import de.monticore.expressions.prettyprint.CommonExpressionsPrettyPrinter;
import de.monticore.expressions.prettyprint.ExpressionsBasisPrettyPrinter;
import de.monticore.featurediagram.FeatureDiagramMill;
import de.monticore.featurediagram._ast.ASTFDCompilationUnit;
import de.monticore.featurediagram._ast.ASTFeatureDiagramNode;
import de.monticore.featurediagram._visitor.FeatureDiagramTraverser;
import de.monticore.literals.prettyprint.MCCommonLiteralsPrettyPrinter;
import de.monticore.prettyprint.CardinalityPrettyPrinter;
import de.monticore.prettyprint.IndentPrinter;
import de.monticore.prettyprint.MCBasicsPrettyPrinter;
import de.monticore.types.prettyprint.MCBasicTypesPrettyPrinter;

/**
 * The pretty printer for feature diagrams reuses pretty printers of the languages it inherits from.
 */
public class FeatureDiagramPrettyPrinter implements FeatureDiagramTraverser {

  public static String print(ASTFeatureDiagramNode node) {
    IndentPrinter printer = new IndentPrinter();
    FeatureDiagramTraverser t = FeatureDiagramMill.traverser();
    t.add4Cardinality(new CardinalityPrettyPrinter(printer));
    t.add4CommonExpressions(new CommonExpressionsPrettyPrinter(printer));
    t.add4ExpressionsBasis(new ExpressionsBasisPrettyPrinter(printer));
    t.add4FeatureDiagram(new FeatureDiagramPrinter(printer));
    t.add4MCBasics(new MCBasicsPrettyPrinter(printer));
    t.add4MCBasicTypes(new MCBasicTypesPrettyPrinter(printer));
    t.add4MCCommonLiterals(new MCCommonLiteralsPrettyPrinter(printer));
    node.accept(t);
    return printer.getContent();
  }

  public void handle(de.monticore.featurediagram._ast.ASTRequires node) {
    if (null != node.getLeft()) {
      node.getLeft().accept(this);
    }
    this.visit(node);
    if (null != node.getRight()) {
      node.getRight().accept(this);
    }
    this.endVisit(node);
  }

  public void handle(de.monticore.featurediagram._ast.ASTExcludes node) {
    if (null != node.getLeft()) {
      node.getLeft().accept(this);
    }
    this.visit(node);
    if (null != node.getRight()) {
      node.getRight().accept(this);
    }
    this.endVisit(node);
  }

  public void traverse(ASTFDCompilationUnit node) {
    if (null != node.getFeatureDiagram()) {
      node.getFeatureDiagram().accept(this);
    }
  }

}
