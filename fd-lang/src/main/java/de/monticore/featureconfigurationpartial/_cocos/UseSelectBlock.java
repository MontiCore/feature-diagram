/* (c) https://github.com/MontiCore/monticore */
package de.monticore.featureconfigurationpartial._cocos;

import de.monticore.featureconfiguration._ast.ASTFeatureConfiguration;
import de.monticore.featureconfiguration._ast.ASTFeatures;
import de.monticore.featureconfiguration._cocos.FeatureConfigurationASTFeatureConfigurationCoCo;
import de.monticore.featureconfiguration._visitor.FeatureConfigurationVisitor2;
import de.monticore.featureconfigurationpartial.FeatureConfigurationPartialMill;
import de.monticore.featureconfigurationpartial._visitor.FeatureConfigurationPartialTraverser;
import de.se_rwth.commons.logging.Log;

/**
 * This CoCo checks whether a PartialFC model uses an ASTFeatures block to define selected features,
 * which is valid for the parser that inherits this from the FC language. However, PartialFC models
 * should use select and excludes blocks.
 */
public class UseSelectBlock implements FeatureConfigurationASTFeatureConfigurationCoCo {

  @Override public void check(ASTFeatureConfiguration node) {
    Checker c = new Checker(node);
    if (c.hasASTFeatures()) {
      Log.error("0xFC203 The partial feature configuration '" + node.getName()
          + "' defines selected features outside of 'select' and 'exclude' blocks. This is forbidden!");
    }
  }

  class Checker implements FeatureConfigurationVisitor2 {
    protected boolean hasASTFeatures = false;

    protected FeatureConfigurationPartialTraverser traverser;

    public Checker(ASTFeatureConfiguration node) {
      this.traverser = FeatureConfigurationPartialMill.traverser();
      traverser.add4FeatureConfiguration(this);
      node.accept(traverser);
    }

    @Override public void visit(ASTFeatures node) {
      hasASTFeatures = true;
    }

    public boolean hasASTFeatures() {
      return hasASTFeatures;
    }
  }
}
