/* (c) https://github.com/MontiCore/monticore */

package mcfdtool.transform.trafos;

import de.monticore.featureconfiguration._ast.ASTFeatureConfiguration;
import de.monticore.featurediagram._ast.ASTFeatureDiagram;
import mcfdtool.transform.flatzinc.FlatZincModel;

public class FlatZincTrafo {

  protected FDTrafo fdTrafo = new FDTrafo();

  protected FCTrafo fcTrafo = new FCTrafo();

  protected FCPTrafo fcpTrafo = new FCPTrafo();

  protected FlatZincModel model;

  protected static FlatZincTrafo INSTANCE;

  protected FlatZincTrafo() {

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

  public FCPTrafo getFcpTrafo() {
    return fcpTrafo;
  }

  public void setFcpTrafo(FCPTrafo fcpTrafo) {
    this.fcpTrafo = fcpTrafo;
  }

  public FlatZincModel getModel() {
    if(null == model){
      model = new FlatZincModel();
    }
    return model;
  }

  public void setModel(FlatZincModel model) {
    this.model = model;
  }

  public static FlatZincTrafo addFeatureDiagram(ASTFeatureDiagram fd) {
    return getInstance()._addFeatureDiagram(fd);
  }

  public FlatZincTrafo _addFeatureDiagram(ASTFeatureDiagram fd) {
    getFdTrafo().apply(fd, getModel());
    return this;
  }

  public static FlatZincTrafo addFeatureConfiguration(ASTFeatureDiagram fd, ASTFeatureConfiguration fc) {
    return getInstance()._addFeatureConfiguration(fd, fc);
  }

  public FlatZincTrafo _addFeatureConfiguration(ASTFeatureDiagram fd, ASTFeatureConfiguration fc) {
    getFcTrafo().apply(fd, fc, getModel());
    return this;
  }

  public static FlatZincTrafo addFeatureConfigurationPartial(ASTFeatureDiagram fd, ASTFeatureConfiguration fc) {
    return getInstance()._addFeatureConfigurationPartial(fd, fc);
  }

  public FlatZincTrafo _addFeatureConfigurationPartial(ASTFeatureDiagram fd, ASTFeatureConfiguration fc) {
    getFcpTrafo().apply(fd, fc, getModel());
    return this;
  }

  public static FlatZincModel build() {
    FlatZincModel model = getInstance().getModel();
    getInstance().setModel(null);
    return model;
  }

}
