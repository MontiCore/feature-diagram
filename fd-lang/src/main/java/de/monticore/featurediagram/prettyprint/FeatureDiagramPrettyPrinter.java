/* (c) https://github.com/MontiCore/monticore */

package de.monticore.featurediagram.prettyprint;

import de.monticore.MCCommonLiteralsPrettyPrinter;
import de.monticore.expressions.prettyprint.CommonExpressionsPrettyPrinter;
import de.monticore.expressions.prettyprint.ExpressionsBasisPrettyPrinter;
import de.monticore.featurediagram.FeatureDiagramMill;
import de.monticore.featurediagram._ast.ASTFeatureDiagramNode;
import de.monticore.featurediagram._visitor.FeatureDiagramDelegatorVisitor;
import de.monticore.prettyprint.CardinalityPrettyPrinter;
import de.monticore.prettyprint.IndentPrinter;
import de.monticore.prettyprint.MCBasicsPrettyPrinter;
import de.monticore.types.prettyprint.MCBasicTypesPrettyPrinter;

public class FeatureDiagramPrettyPrinter {

  public static String print(ASTFeatureDiagramNode node) {
    IndentPrinter printer = new IndentPrinter();
    FeatureDiagramDelegatorVisitor visitor = FeatureDiagramMill
        .featureDiagramDelegatorVisitorBuilder()
        .setCardinalityVisitor(new CardinalityPrettyPrinter(printer))
        .setCommonExpressionsVisitor(new CommonExpressionsPrettyPrinter(printer))
        .setExpressionsBasisVisitor(new ExpressionsBasisPrettyPrinter(printer))
        .setFeatureDiagramVisitor(new FeatureDiagramPrinter(printer))
        .setMCBasicsVisitor(new MCBasicsPrettyPrinter(printer))
        .setMCBasicTypesVisitor(new MCBasicTypesPrettyPrinter(printer))
        .setMCCommonLiteralsVisitor(new MCCommonLiteralsPrettyPrinter(printer))
        .build();
    node.accept(visitor);
    return printer.getContent();
  }

}
