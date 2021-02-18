/* (c) https://github.com/MontiCore/monticore */
package de.monticore.featureconfiguration.prettyprint;

import de.monticore.featureconfiguration.FeatureConfigurationMill;
import de.monticore.featureconfiguration._ast.ASTFCCompilationUnit;
import de.monticore.featureconfiguration._ast.ASTFeatureConfiguration;
import de.monticore.featureconfiguration._ast.ASTFeatures;
import de.monticore.featureconfiguration._visitor.FeatureConfigurationTraverser;
import de.monticore.featureconfiguration._visitor.FeatureConfigurationVisitor2;
import de.monticore.prettyprint.CommentPrettyPrinter;
import de.monticore.prettyprint.IndentPrinter;
import de.monticore.types.mcbasictypes._ast.ASTMCImportStatement;

import java.util.stream.Collectors;

/**
 * This Printer prints all AST nodes that are introduced by the
 * FeatueConfiguration grammar. It realizes basic formatting through
 * indentation and line breaks. No inherited language elements have to
 * be printed, thus not delegatorvisitor is required to realize this printer.
 */
public class FeatureConfigurationPrinter implements FeatureConfigurationVisitor2 {

  protected IndentPrinter printer;

  /**
   * prettyprints a formatted Strong from the passed AST
   * @param node
   * @return
   */
  public static String print(ASTFCCompilationUnit node) {
    IndentPrinter printer = new IndentPrinter();
    FeatureConfigurationPrinter visitor = new FeatureConfigurationPrinter(printer);
    FeatureConfigurationTraverser traverser = FeatureConfigurationMill.traverser();
    traverser.add4FeatureConfiguration(visitor);
    node.accept(traverser);
    return printer.getContent();
  }

  public FeatureConfigurationPrinter(IndentPrinter printer){
    this.printer = printer;
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
    for (ASTMCImportStatement imp : node.getMCImportStatementList()) {
      printer.println(imp.printType());
    }
    if (!node.isEmptyMCImportStatements()) {
      printer.println();
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

}
