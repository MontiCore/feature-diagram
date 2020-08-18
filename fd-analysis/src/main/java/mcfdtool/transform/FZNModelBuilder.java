/* (c) https://github.com/MontiCore/monticore */
package mcfdtool.transform;

import de.monticore.featurediagram._ast.ASTFeatureDiagram;
import mcfdtool.transform.flatzinc.FlatZincModel;
import mcfdtool.transform.trafos.BasicTrafo;
import mcfdtool.transform.trafos.RootFeatureSelected;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

// TODO: comment, explain class

public class FZNModelBuilder {

  private List<FeatureModel2FlatZincModelTrafo> trafos;

  private List<String> names = new ArrayList<>();

  private boolean allSolutions;

  private FlatZincModel flatZincModel = new FlatZincModel();

  public FZNModelBuilder(List<FeatureModel2FlatZincModelTrafo> trafos, boolean allSolutions) {
    this.allSolutions = allSolutions;
    this.trafos = trafos;
  }

  public FZNModelBuilder(boolean allSolutions) {
    this.allSolutions = allSolutions;
    this.trafos = new ArrayList<>();
  }

  public Boolean isAllSolutions() {
    return allSolutions;
  }

  public void setAllSolutions(boolean allSolutions) {
    this.allSolutions = allSolutions;
  }

  public void addFeatureModelFZNTrafo(FeatureModel2FlatZincModelTrafo trafo) {
    trafos.add(trafo);
  }

  public void addAllFeatureModelFZNTrafos(Collection<FeatureModel2FlatZincModelTrafo> trafos) {
    this.trafos.addAll(trafos);
  }

  public void buildFlatZincModel(ASTFeatureDiagram featureModel) {
    trafos.forEach(trafo -> trafo.setNames(names));
    trafos.stream().filter(t -> t.getFeatureModel() == null)
        .forEach(t -> t.setFeatureModel(featureModel));
    trafos.forEach(FeatureModel2FlatZincModelTrafo::perform);
    trafos.forEach(featureModel2FlatZincModelTrafo -> flatZincModel
        .addVariables(featureModel2FlatZincModelTrafo.getVariables()));
    trafos.forEach(featureModel2FlatZincModelTrafo -> flatZincModel
        .addConstraints(featureModel2FlatZincModelTrafo.getConstraints()));
  }

  public FlatZincModel getFlatZincModel() {
    return flatZincModel;
  }

  public void addDefaultFMTrafos() {
    trafos.add(new BasicTrafo());
    trafos.add(new RootFeatureSelected());
  }

}
