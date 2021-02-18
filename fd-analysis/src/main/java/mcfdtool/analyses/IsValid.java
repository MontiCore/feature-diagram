/* (c) https://github.com/MontiCore/monticore */
package mcfdtool.analyses;

import de.monticore.featureconfiguration._ast.ASTFCElement;
import de.monticore.featureconfiguration._ast.ASTFeatureConfiguration;
import de.monticore.featureconfiguration._visitor.SelectedFeatureCollector;
import de.monticore.featureconfigurationpartial.FeatureConfigurationPartialMill;
import de.monticore.featurediagram._ast.ASTFeatureDiagram;
import mcfdtool.solver.Solvers;
import mcfdtool.transform.flatzinc.FlatZincModel;
import mcfdtool.transform.trafos.FlatZincTrafo;

import java.util.ArrayList;
import java.util.List;

/**
 * This analysis returns true, if the passed configuration is valid with regard to the passed
 * feature diagram and false otherwise
 */
public class IsValid {

  public Boolean perform(ASTFeatureDiagram fd, ASTFeatureConfiguration fc) {
    //To avoid that the solver finds a solution that completes the passed fc to a valid
    // fc, we need to explicitly mark features that are not selected as excluded:
    excludeNotSelectedFeatures(fd, fc);

    FlatZincModel model = FlatZincTrafo.addFeatureDiagram(fd)._addFeatureConfiguration(fc).build();
    return Solvers.getSolver().hasSolution(model);
  }

  /**
   * This method marks all features that are not selected in fc as explicitly unselected
   * @param fd
   * @param fc
   * @return
   */
  protected void excludeNotSelectedFeatures(ASTFeatureDiagram fd, ASTFeatureConfiguration fc) {
    List<String> selectedFeatures = SelectedFeatureCollector.getSelectedFeatures(fc);

    //initialize excluded features with all features of the FD
    List<String> excludedFeatures = new ArrayList<>(fd.getAllFeatures());
    //then remove all features that are selected in the FC
    excludedFeatures.removeAll(selectedFeatures);

    //then adjust the list of FC elements accordingly
    List<ASTFCElement> newElements = new ArrayList<>();
    newElements.add(FeatureConfigurationPartialMill.selectBuilder().addAllNames(selectedFeatures).build());
    newElements.add(FeatureConfigurationPartialMill.unselectBuilder().addAllNames(excludedFeatures).build());
    fc.setFCElementList(newElements);

  }

}
