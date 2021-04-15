/* (c) https://github.com/MontiCore/monticore */

package mcfdtool.transform.trafos;

import de.monticore.featureconfiguration._ast.ASTFeatureConfiguration;
import de.monticore.featurediagram._ast.ASTFeatureDiagram;
import mcfdtool.transform.flatzinc.FlatZincModel;

/**
 * This singleton class manages Trafos for feature diagrams, feature configurations, and
 * partial feature configurations. If different transformations should be applied, the trafos
 * can be replaced by subclassing this class and set the instance via setInstance()
 */
public class FlatZincTrafo {

  protected FDTrafo fdTrafo = new FDTrafo();

  protected FCTrafo fcTrafo = new FCTrafo();

  protected FlatZincModel model;

  protected static FlatZincTrafo INSTANCE;

  protected FlatZincTrafo() {
    //do not access constructor, this is a singleton
  }

  public static FlatZincTrafo getInstance() {
    if (null == INSTANCE) {
      INSTANCE = new FlatZincTrafo();
    }
    return INSTANCE;
  }

  public static void setInstance(FlatZincTrafo instance) {
    INSTANCE = instance;
  }

  public FDTrafo getFdTrafo() {
    return fdTrafo;
  }

  public void setFdTrafo(FDTrafo fdTrafo) {
    this.fdTrafo = fdTrafo;
  }

  public FCTrafo getFcTrafo() {
    return fcTrafo;
  }

  public void setFcTrafo(FCTrafo fcTrafo) {
    this.fcTrafo = fcTrafo;
  }

  public FlatZincModel getModel() {
    if (null == model) {
      model = new FlatZincModel();
    }
    return model;
  }

  public void setModel(FlatZincModel model) {
    this.model = model;
  }

  public FlatZincTrafo addFeatureDiagram(ASTFeatureDiagram fd) {
    getFdTrafo().apply(fd, getModel());
    return this;
  }

  public FlatZincTrafo addFeatureConfiguration(ASTFeatureConfiguration fc) {
    getFcTrafo().apply(fc, getModel());
    return this;
  }

  public FlatZincModel build() {
    FlatZincModel model = getInstance().getModel();
    setModel(null);
    return model;
  }

}
