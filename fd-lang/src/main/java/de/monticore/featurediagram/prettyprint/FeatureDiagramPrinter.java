/* (c) https://github.com/MontiCore/monticore */

package de.monticore.featurediagram.prettyprint;

import de.monticore.featurediagram._ast.*;
import de.monticore.featurediagram._visitor.FeatureDiagramVisitor;
import de.monticore.prettyprint.IndentPrinter;
import de.monticore.types.mcbasictypes._ast.ASTMCImportStatement;

import java.util.List;

public class FeatureDiagramPrinter implements FeatureDiagramVisitor {

  protected IndentPrinter printer;

  protected FeatureDiagramVisitor realThis;

  public FeatureDiagramPrinter(IndentPrinter printer) {
    this.printer = printer;
    this.realThis = this;
  }

  @Override public void visit(ASTFDCompilationUnit node) {
    if (node.isPresentPackage()) {
      printer.print("package ");
      printer.print(node.getPackage());
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
    printGroup(node.getGroupPartList(), " , ");
    printer.print("}");
  }

  @Override public void visit(ASTRequires node) {
    printer.print(" requires ");
  }

  @Override public void visit(ASTExcludes node) {
    printer.print(" excludes ");
  }

  public void handle(de.monticore.featurediagram._ast.ASTExcludes node) {
    if (null != node.getLeft()) {
      node.getLeft().accept(getRealThis());
    }
    getRealThis().visit(node);
    if (null != node.getRight()) {
      node.getRight().accept(getRealThis());
    }
    getRealThis().endVisit(node);
  }

  public void handle(de.monticore.featurediagram._ast.ASTRequires node) {
    if (null != node.getLeft()) {
      node.getLeft().accept(getRealThis());
    }
    getRealThis().visit(node);
    if (null != node.getRight()) {
      node.getRight().accept(getRealThis());
    }
    getRealThis().endVisit(node);
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

  @Override public FeatureDiagramVisitor getRealThis() {
    return realThis;
  }

  @Override
  public void setRealThis(FeatureDiagramVisitor realThis) {
    this.realThis = realThis;
  }
}
