/* (c) https://github.com/MontiCore/monticore */
package tool.analyses;

import featurediagram._symboltable.FeatureDiagramSymbol;
import tool.transform.FZNModelBuilder;

import java.util.Collection;
import java.util.Map;
import java.util.Optional;

public abstract class Analysis<T> {
  protected Optional<T> result;

  protected FZNModelBuilder builder = new FZNModelBuilder(false);

  private FeatureDiagramSymbol featureModel;

  public Analysis() {
    this.result = Optional.empty();
    this.featureModel = null;
    this.builder = new FZNModelBuilder(false);
  }

  public FeatureDiagramSymbol getFeatureModel() {
    return featureModel;
  }

  public void setFeatureModel(FeatureDiagramSymbol featureModel) {
    this.featureModel = featureModel;
  }

  public abstract void perform(Collection<Map<String, Boolean>> configurations);

  public FZNModelBuilder getModelBuilder() {
    return builder;
  }

  public Optional<T> getResult() {
    return result;
  }

  protected void setResult(T result) {
    this.result = Optional.of(result);
  }

}
