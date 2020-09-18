/* (c) https://github.com/MontiCore/monticore */
package de.monticore.featureconfigurationpartial.prettyprint;

import de.monticore.featureconfiguration._ast.ASTFeatureConfiguration;
import de.monticore.featureconfiguration._ast.ASTFeatureConfigurationNode;
import de.monticore.featureconfiguration.prettyprint.FeatureConfigurationPrinter;
import de.monticore.featureconfigurationpartial.FeatureConfigurationPartialMill;
import de.monticore.featureconfigurationpartial._visitor.FeatureConfigurationPartialVisitor;
import de.monticore.prettyprint.IndentPrinter;

import java.util.List;

/**
 * The pretty printer for feature partial feature configurations reuses pretty printers of the
 * languages it inherits from.
 */
public class FeatureConfigurationPartialPrettyPrinter {

  public static String print(List<ASTFeatureConfiguration> nodes) {
    IndentPrinter printer = new IndentPrinter();
    FeatureConfigurationPartialVisitor visitor =
        FeatureConfigurationPartialMill.featureConfigurationPartialDelegatorVisitorBuilder()
            .setFeatureConfigurationPartialVisitor(new FeatureConfigurationPartialPrinter(printer))
            .setFeatureConfigurationVisitor(new FeatureConfigurationPrinter(printer))
            .build();

    for (ASTFeatureConfigurationNode node : nodes) {
      node.accept(visitor);
      printer.println();
    }
    return printer.getContent();
  }

  public static String print(ASTFeatureConfigurationNode node) {
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
