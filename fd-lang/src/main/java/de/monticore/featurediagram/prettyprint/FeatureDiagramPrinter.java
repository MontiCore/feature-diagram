/* (c) https://github.com/MontiCore/monticore */

package de.monticore.featurediagram.prettyprint;

import de.monticore.featurediagram._ast.*;
import de.monticore.featurediagram._visitor.FeatureDiagramVisitor2;
import de.monticore.prettyprint.CommentPrettyPrinter;
import de.monticore.prettyprint.IndentPrinter;
import de.monticore.types.mcbasictypes._ast.ASTMCImportStatement;

import java.util.List;

/**
 * This Printer prints all AST nodes that are introduced by the FeatueDiagram grammar. It realizes
 * basic formatting through indentation and line breaks.
 */
public class FeatureDiagramPrinter implements FeatureDiagramVisitor2 {

  protected IndentPrinter printer;

  public FeatureDiagramPrinter(IndentPrinter printer) {
    this.printer = printer;
  }

  @Override public void visit(ASTFDCompilationUnit node) {
    CommentPrettyPrinter.printPreComments(node, printer);
    if (node.isPresentPackage()) {
      printer.print("package ");
      printer.print(node.getPackage().getQName());
      printer.println(";");
      printer.println();
    }
    for (ASTMCImportStatement imp : node.getMCImportStatementList()) {
      printer.println(imp.printType());
    }
    if (!node.isEmptyMCImportStatements()) {
      printer.println();
    }
  }

  @Override public void visit(ASTFeatureDiagram node) {
    printer.print("featurediagram ");
    printer.print(node.getName());
    printer.println(" {");
    printer.indent();
  }

  @Override public void endVisit(ASTFeatureDiagram node) {
    printer.unindent();
    printer.println("}");
  }

  @Override public void visit(ASTFeatureTreeRule node) {
    printer.print(node.getName());
    printer.print(" -> ");
  }

  @Override public void endVisit(ASTFeatureTreeRule node) {
    printer.println(";");
  }

  @Override public void endVisit(ASTFeatureConstraint node) {
    printer.println(";");
  }

  @Override public void visit(ASTXorGroup node) {
    printGroup(node.getGroupPartList(), " ^ ");
  }

  @Override public void visit(ASTOrGroup node) {
    printGroup(node.getGroupPartList(), " | ");
  }

  @Override public void visit(ASTAndGroup node) {
    printGroup(node.getGroupPartList(), " & ");
  }

  @Override public void endVisit(ASTCardinalizedGroup node) {
    printer.print(" of {");
    printGroup(node.getGroupPartList(), ", ");
    printer.print("}");
  }

  protected void printGroup(List<ASTGroupPart> groupPartList, String separator) {
    String sep = "";
    for (ASTGroupPart p : groupPartList) {
      printer.print(sep);
      printer.print(p.getName());
      if (p.isOptional()) {
        printer.print("?");
      }
      sep = separator;
    }
  }

  @Override public void visit(ASTRequires node) {
    printer.print(" requires ");
  }

  @Override public void visit(ASTExcludes node) {
    printer.print(" excludes ");
  }

}
