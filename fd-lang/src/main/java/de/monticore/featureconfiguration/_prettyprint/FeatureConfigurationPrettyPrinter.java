/* (c) https://github.com/MontiCore/monticore */
package de.monticore.featureconfiguration._prettyprint;

import de.monticore.prettyprint.IndentPrinter;

public class FeatureConfigurationPrettyPrinter extends FeatureConfigurationPrettyPrinterTOP {

  public FeatureConfigurationPrettyPrinter(IndentPrinter printer, boolean printComments) {
    super(printer, printComments);
  }

  @Override
  public void handle(de.monticore.featureconfiguration._ast.ASTFeatureConfiguration node) {

    if (this.isPrintComments()) {
      de.monticore.prettyprint.CommentPrettyPrinter.printPreComments(node, getPrinter());
    }


    getPrinter().print("featureconfig ");


    getPrinter().print(node.getName() + " ");
    getPrinter().print("for ");

    // HC work-around for de.monticore.featureconfiguration._symboltable.FeatureConfigurationScopesGenitor#handleImportStatements
    if (node.isPresentFdNameSymbol()) {
      getPrinter().print(node.getFdNameSymbol().getName() + " ");
    }else{
      getPrinter().print(node.getFdName() + " ");
    }
    // END HC

    getPrinter().println("{ ");
    getPrinter().indent();

    node.getFCElementList().forEach(n -> n.accept(getTraverser()));

    getPrinter().unindent();
    getPrinter().println();
    getPrinter().println("} ");

    if (this.isPrintComments()) {
      de.monticore.prettyprint.CommentPrettyPrinter.printPostComments(node, getPrinter());
    }
  }
}
