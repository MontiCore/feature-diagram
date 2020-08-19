/* (c) https://github.com/MontiCore/monticore */
package mcfdtool.visitors;

import de.monticore.featureconfiguration._ast.ASTFeatureConfiguration;
import de.monticore.featureconfiguration._ast.ASTFeatures;
import de.monticore.featureconfiguration._visitor.FeatureConfigurationVisitor;
import de.monticore.featureconfigurationpartial._ast.ASTSelect;
import de.monticore.featureconfigurationpartial._cocos.FeatureConfigurationPartialCoCoChecker;
import de.monticore.featureconfigurationpartial._visitor.FeatureConfigurationPartialVisitor;

import java.util.ArrayList;
import java.util.List;

/**
 * Collects all features selected in a List<String> result.
 * (unsorted)
 */
public class SelectedFeatureCollector implements FeatureConfigurationVisitor,
    FeatureConfigurationPartialVisitor {

  public static List<String> getSelectedFeatures(ASTFeatureConfiguration fc){
    SelectedFeatureCollector visitor = new SelectedFeatureCollector();
    fc.accept(visitor);
    return visitor.getSelectedFeatures();
  }

  List<String> selectedFeatures = new ArrayList<>();

  /**
   * collect selected features from feature condiguration
   * @param ast
   */
  @Override
  public void visit(ASTFeatures ast) {
    selectedFeatures.addAll(ast.getNamesList());
  }

  /**
   * collect selected features from partial feature condiguration
   * @param ast
   */
  @Override
  public void visit(ASTSelect ast) {
    selectedFeatures.addAll(ast.getNamesList());
  }

  public List<String> getSelectedFeatures() {
    return selectedFeatures;
  }
}
