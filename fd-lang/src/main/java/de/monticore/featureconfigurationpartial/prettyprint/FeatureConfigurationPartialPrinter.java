/* (c) https://github.com/MontiCore/monticore */
package de.monticore.featureconfigurationpartial.prettyprint;

import de.monticore.featureconfigurationpartial._ast.ASTSelect;
import de.monticore.featureconfigurationpartial._ast.ASTUnselect;
import de.monticore.featureconfigurationpartial._visitor.FeatureConfigurationPartialVisitor;
import de.monticore.prettyprint.IndentPrinter;

import java.util.stream.Collectors;

/**
 * This printer prints language elements introduced in the FeatureConfigurationPartial, namely
 * select and exclude blocks. It realizes basic formatting through indentation and line breaks.
 */
public class FeatureConfigurationPartialPrinter implements FeatureConfigurationPartialVisitor {

  protected IndentPrinter printer;

  protected FeatureConfigurationPartialVisitor realThis;


  public FeatureConfigurationPartialPrinter(IndentPrinter printer) {
    this.printer = printer;
    this.realThis = this;
  }

  @Override
  public void visit(ASTSelect node) {
    if(!node.isEmptyNames()) {
      printer.print("select ");
      printer.print("{ ");
      printer.print(
        node.streamNames().collect(Collectors.joining(","))
      );
      printer.println(" }");
    }
  }

  public void visit(ASTUnselect node){
    if(!node.isEmptyNames()) {
      printer.print("exclude ");
      printer.print("{ ");
      printer.print(
        node.streamNames().collect(Collectors.joining(","))
      );
      printer.println(" }");
    }
  }

  @Override
  public FeatureConfigurationPartialVisitor getRealThis() {
    return realThis;
  }

  @Override
  public void setRealThis(FeatureConfigurationPartialVisitor realThis) {
    this.realThis = realThis;
  }
}
