/* (c) https://github.com/MontiCore/monticore */
package de.monticore.featurediagram._prettyprint;

import de.monticore.prettyprint.IndentPrinter;

public class FeatureDiagramPrettyPrinter extends FeatureDiagramPrettyPrinterTOP {

  public FeatureDiagramPrettyPrinter(IndentPrinter printer, boolean printComments) {
    super(printer, printComments);
  }

  @Override
  public void handle(de.monticore.featurediagram._ast.ASTCardinalizedGroup node) {
    // HC: Change the spacing between group parts
    if (this.isPrintComments()) {
      de.monticore.prettyprint.CommentPrettyPrinter.printPreComments(node, getPrinter());
    }
    java.util.Iterator<de.monticore.featurediagram._ast.ASTGroupPart> iter_groupPart = node.getGroupPartList().iterator();


    if (iter_groupPart.hasNext()) {
      node.getCardinality().accept(getTraverser());
      getPrinter().print("of ");
      getPrinter().print("{");
      if (iter_groupPart.hasNext()) {
        iter_groupPart.next().accept(getTraverser());
        while (iter_groupPart.hasNext()) {
          getPrinter().stripTrailing();
          getPrinter().print(",");
          iter_groupPart.next().accept(getTraverser());
        }
      }
      getPrinter().print("}");
    }
    if (this.isPrintComments()) {
      de.monticore.prettyprint.CommentPrettyPrinter.printPostComments(node, getPrinter());
    }
  }

  public void handle(de.monticore.featurediagram._ast.ASTGroupPart node) {
    // HC: Change the spacing between group parts
    if (this.isPrintComments()) {
      de.monticore.prettyprint.CommentPrettyPrinter.printPreComments(node, getPrinter());
    }

    getPrinter().print(node.getName());

    if (node.isOptional()) {
      getPrinter().print("?");
    }
    getPrinter().print(" ");


    if (this.isPrintComments()) {
      de.monticore.prettyprint.CommentPrettyPrinter.printPostComments(node, getPrinter());
    }

  }


}
