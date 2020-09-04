/* (c) https://github.com/MontiCore/monticore */
package de.monticore.featureconfiguration.prettyprint;

import de.monticore.featureconfiguration.FeatureConfigurationMill;
import de.monticore.featureconfiguration._ast.ASTFeatureConfiguration;
import de.monticore.featureconfiguration._ast.ASTFeatureConfigurationNode;
import de.monticore.featureconfiguration._visitor.FeatureConfigurationDelegatorVisitor;
import de.monticore.prettyprint.IndentPrinter;

public class FeatureConfigurationPrettyPrinter{

  public static String print(ASTFeatureConfigurationNode node){
    IndentPrinter printer = new IndentPrinter();
    FeatureConfigurationDelegatorVisitor visitor =
      FeatureConfigurationMill.featureConfigurationDelegatorVisitorBuilder()
        .setFeatureConfigurationVisitor(new FeatureConfigurationPrinter(printer))
        .build();
    visitor.handle(node);
    return printer.getContent();
  }

}
