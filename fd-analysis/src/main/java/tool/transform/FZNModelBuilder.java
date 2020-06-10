/* (c) https://github.com/MontiCore/monticore */
package tool.transform;

import de.monticore.expressions.expressionsbasis._ast.ASTExpression;
import featurediagram._symboltable.FeatureDiagramSymbol;
import tool.transform.flatzinc.FlatZincModel;
import tool.transform.trafos.BasicTrafo;
import tool.transform.trafos.ComplexConstraint2FZN;
import tool.transform.trafos.RootFeatureSelected;

import java.util.*;

public class FZNModelBuilder {
  private List<FeatureModel2FlatZincModelTrafo> trafos = new ArrayList<>();

  private List<String> names = new ArrayList<>();

  private Map<String, Boolean> configuration;

  private boolean allSolutions;

  private StringBuilder stringBuilder;

  private FlatZincModel flatZincModel = new FlatZincModel();

  private Set<String> booleanVars;

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

  public void buildFlatZincModel(FeatureDiagramSymbol featureModel) {
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
