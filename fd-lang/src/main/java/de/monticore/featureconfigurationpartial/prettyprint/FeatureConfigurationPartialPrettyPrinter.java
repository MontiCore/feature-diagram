/* (c) https://github.com/MontiCore/monticore */
package de.monticore.featureconfigurationpartial.prettyprint;

import de.monticore.featureconfiguration._ast.ASTFeatureConfiguration;
import de.monticore.featureconfiguration.prettyprint.FeatureConfigurationPrinter;
import de.monticore.featureconfigurationpartial.FeatureConfigurationPartialMill;
import de.monticore.featureconfigurationpartial._visitor.FeatureConfigurationPartialVisitor;
import de.monticore.prettyprint.IndentPrinter;

public class FeatureConfigurationPartialPrettyPrinter {

  public static String print(ASTFeatureConfiguration node){
    IndentPrinter printer = new IndentPrinter();
    FeatureConfigurationPartialVisitor visitor =
      FeatureConfigurationPartialMill.featureConfigurationPartialDelegatorVisitorBuilder()
      .setFeatureConfigurationPartialVisitor(new FeatureConfigurationPartialPrinter(printer))
      .setFeatureConfigurationVisitor(new FeatureConfigurationPrinter(printer))
      .build();
    node.accept(visitor);
    return printer.getContent();
  }
}
