package tool.analyses;

import featureconfiguration._ast.ASTFeatureConfiguration;
import featurediagram._symboltable.FeatureDiagramSymbol;
import featurediagram._symboltable.FeatureSymbol;
import tool.transform.FZNModelBuilder;

import java.util.Collection;
import java.util.Map;
import java.util.Optional;

public abstract class Analysis<T> {
  protected Optional<T> result;
  private FeatureDiagramSymbol featureModel;

  protected FZNModelBuilder builder = new FZNModelBuilder(false);

  public FeatureDiagramSymbol getFeatureModel() {
    return featureModel;
  }

  public Analysis() {
    this.result = Optional.empty();
    this.featureModel = null;
    this.builder = new FZNModelBuilder(false);
  }

  public void setFeatureModel(FeatureDiagramSymbol featureModel) {
    this.featureModel = featureModel;
  }

  public abstract void perform(Collection<Map<String, Boolean>> configurations);

  public FZNModelBuilder getModelBuilder(){
    return builder;
  }

  public Optional<T> getResult() {
    return result;
  }

  protected void setResult(T result) {
    this.result = Optional.of(result);
  }


}
