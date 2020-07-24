/* (c) https://github.com/MontiCore/monticore */
package de.monticore.featureconfiguration.prettyprint;

import de.monticore.featureconfiguration._ast.ASTFCCompilationUnit;
import de.monticore.featureconfiguration._ast.ASTFeatureConfiguration;
import de.monticore.featureconfiguration._ast.ASTFeatures;
import de.monticore.featureconfiguration._visitor.FeatureConfigurationVisitor;
import de.monticore.prettyprint.CommentPrettyPrinter;
import de.monticore.prettyprint.IndentPrinter;
import de.monticore.types.mcbasictypes._ast.ASTMCImportStatement;

import java.util.stream.Collectors;

/**
 * This Printer prints all AST nodes that are introduced by the
 * FeatueConfiguration grammar. It realizes basic formatting through
 * indentation and line breaks.
 */
public class FeatureConfigurationPrinter implements FeatureConfigurationVisitor {

  protected IndentPrinter printer;

  protected FeatureConfigurationVisitor realThis;

  public FeatureConfigurationPrinter(IndentPrinter printer){
    this.printer = printer;
    this.realThis = this;
  }

  @Override
  public void visit(ASTFCCompilationUnit node) {
    CommentPrettyPrinter.printPreComments(node, printer);
    if (node.isPresentPackage()) {
      printer.print("package ");
      printer.print(node.getPackage().getQName());
      printer.println(";");
      printer.println();
    }
    for (ASTMCImportStatement imp : node.getMCImportStatementsList()) {
      printer.println(imp.printType());
    }
    if (!node.isEmptyMCImportStatements()) {
      printer.println();
    }
  }

  public void traverse(ASTFCCompilationUnit node){
    if (node.getFeatureConfiguration() != null){
      node.getFeatureConfiguration().accept(getRealThis());
    }
  }

  @Override
  public void visit(ASTFeatureConfiguration node) {
    printer.print("featureconfig ");
    printer.print(node.getName());
    printer.print(" for ");
    printer.print(node.getFdName());
    printer.println(" {");
    printer.indent();
  }

  @Override
  public void endVisit(ASTFeatureConfiguration node){
    printer.unindent();
    printer.println("}");
  }

  public void visit(ASTFeatures node){
    printer.print(
      node.streamNames().collect(Collectors.joining(","))
    );
    printer.println();
  }

  @Override
  public FeatureConfigurationVisitor getRealThis() {
    return realThis;
  }

  @Override
  public void setRealThis(FeatureConfigurationVisitor realThis) {
    this.realThis = realThis;
  }
}