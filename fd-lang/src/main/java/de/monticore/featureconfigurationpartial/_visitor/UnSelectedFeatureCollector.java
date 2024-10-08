/* (c) https://github.com/MontiCore/monticore */
package de.monticore.featureconfigurationpartial._visitor;

import de.monticore.featureconfiguration._ast.ASTFeatureConfiguration;
import de.monticore.featureconfiguration._visitor.SelectedFeatureCollector;
import de.monticore.featureconfigurationpartial.FeatureConfigurationPartialMill;
import de.monticore.featureconfigurationpartial._ast.ASTSelect;
import de.monticore.featureconfigurationpartial._ast.ASTUnselect;

import java.util.ArrayList;
import java.util.List;

/**
 * Collects all features selected in a List<String> result.
 * (unsorted)
 */
public class UnSelectedFeatureCollector implements FeatureConfigurationPartialVisitor2 {

  public static List<String> getSelectedFeatures(ASTFeatureConfiguration fc){
    FeatureConfigurationPartialTraverser traverser = FeatureConfigurationPartialMill.inheritanceTraverser();
    UnSelectedFeatureCollector fcpVisitor = new UnSelectedFeatureCollector();
    SelectedFeatureCollector fcVisitor = new SelectedFeatureCollector();
    traverser.add4FeatureConfigurationPartial(fcpVisitor);
    traverser.add4FeatureConfiguration(fcVisitor);
    fc.accept(traverser);

    List<String> result = fcpVisitor.getSelectedFeatures();
    result.addAll(fcVisitor.getSelectedFeatures());
    return result;
  }

  public static List<String> getExcludedFeatures(ASTFeatureConfiguration fc){
    FeatureConfigurationPartialTraverser traverser = FeatureConfigurationPartialMill.inheritanceTraverser();
    UnSelectedFeatureCollector visitor = new UnSelectedFeatureCollector();
    traverser.add4FeatureConfigurationPartial(visitor);
    fc.accept(traverser);
    return visitor.getExcludedFeatures();
  }

  protected List<String> selectedFeatures;

  protected List<String> excludedFeatures;

  public UnSelectedFeatureCollector() {
    this.selectedFeatures = new ArrayList<>();
    this.excludedFeatures = new ArrayList<>();
  }

  /**
   * collect selected features from partial feature configuration
   * @param ast
   */
  @Override
  public void visit(ASTSelect ast) {
    selectedFeatures.addAll(ast.getNameList());
  }

  @Override
  public void visit(ASTUnselect ast) {
    this.excludedFeatures.addAll(ast.getNameList());
  }

  public List<String> getSelectedFeatures() {
    return selectedFeatures;
  }

  public List<String> getExcludedFeatures() {
    return excludedFeatures;
  }
}
