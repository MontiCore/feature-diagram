/* (c) https://github.com/MontiCore/monticore */
package de.monticore.featureconfigurationpartial.prettyprint;

import de.monticore.featureconfigurationpartial._ast.ASTSelect;
import de.monticore.featureconfigurationpartial._ast.ASTUnselect;
import de.monticore.featureconfigurationpartial._visitor.FeatureConfigurationPartialVisitor;
import de.monticore.prettyprint.IndentPrinter;

import java.util.stream.Collectors;

public class FeatureConfigurationPartialPrinter implements FeatureConfigurationPartialVisitor {

  protected IndentPrinter printer;
  protected FeatureConfigurationPartialVisitor realThis;


  public FeatureConfigurationPartialPrinter(IndentPrinter printer) {
    this.printer = printer;
    this.realThis = this;
  }

  @Override
  public void visit(ASTSelect node) {
    printer.print("select");
    printer.print("{");
    printer.print(
      node.streamNames().collect(Collectors.joining(","))
    );
    printer.print("}");
  }

  public void visit(ASTUnselect node){
    printer.print("exclude");
    printer.print("{");
    printer.print(
      node.streamNames().collect(Collectors.joining(","))
    );
    printer.print("}");
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
