/* (c) https://github.com/MontiCore/monticore */
package de.monticore.featureconfiguration._visitor;

import de.monticore.featureconfiguration.FeatureConfigurationMill;
import de.monticore.featureconfiguration._ast.ASTFeatureConfiguration;
import de.monticore.featureconfiguration._ast.ASTFeatures;

import java.util.ArrayList;
import java.util.List;

/**
 * Collects all features selected in a List<String> result.
 * (unsorted)
 */
public class SelectedFeatureCollector implements FeatureConfigurationVisitor2 {

  public static List<String> getSelectedFeatures(ASTFeatureConfiguration fc) {
    FeatureConfigurationTraverser traverser = FeatureConfigurationMill.traverser();
    SelectedFeatureCollector visitor = new SelectedFeatureCollector();
    traverser.add4FeatureConfiguration(visitor);
    fc.accept(traverser);
    return visitor.getSelectedFeatures();
  }

  protected List<String> selectedFeatures;

  public SelectedFeatureCollector() {
    this.selectedFeatures = new ArrayList<>();
  }

  /**
   * collect selected features from feature condiguration
   *
   * @param ast
   */
  @Override
  public void visit(ASTFeatures ast) {
    selectedFeatures.addAll(ast.getNameList());
  }

  public List<String> getSelectedFeatures() {
    return selectedFeatures;
  }

}
