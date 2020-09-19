/* (c) https://github.com/MontiCore/monticore */
package de.monticore.featureconfigurationpartial._cocos;

import de.monticore.featureconfiguration._ast.ASTFeatureConfiguration;
import de.monticore.featureconfiguration._ast.ASTFeatures;
import de.monticore.featureconfiguration._cocos.FeatureConfigurationASTFeatureConfigurationCoCo;
import de.monticore.featureconfigurationpartial._visitor.FeatureConfigurationPartialVisitor;
import de.se_rwth.commons.logging.Log;

/**
 * This CoCo checks whether a PartialFC model uses an ASTFeatures block to define selected features,
 * which is valid for the parser that inherits this from the FC language. However, PartialFC models
 * should use select and excludes blocks.
 */
public class UseSelectBlock implements FeatureConfigurationASTFeatureConfigurationCoCo,
    FeatureConfigurationPartialVisitor {

  protected boolean hasASTFeatures = false;

  @Override public void check(ASTFeatureConfiguration node) {
    node.accept(this);
    if (hasASTFeatures) {
      Log.error("0xFC203 The partial feature configuration '" + node.getName()
          + "' defines selected features outside of 'select' and 'exclude' blocks. This is forbidden!");
    }
  }

  @Override public void visit(ASTFeatures node) {
    hasASTFeatures = true;
  }
}
