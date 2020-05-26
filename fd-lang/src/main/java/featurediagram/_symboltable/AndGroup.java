/* (c) https://github.com/MontiCore/monticore */
package featurediagram._symboltable;

import featurediagram._visitor.FeatureDiagramVisitor;

import java.util.List;

public class AndGroup extends FeatureGroup{

  private List<Boolean> optionalFeatures;

  public AndGroup(FeatureSymbol parent, List<FeatureSymbol> members, List<Boolean> optionalFeatures) {
    super(parent, members, (int) optionalFeatures.stream().filter(Boolean::booleanValue).count(), members.size());
    this.optionalFeatures = optionalFeatures;
  }

  public List<Boolean> getOptionalFeatures() {
    return optionalFeatures;
  }

  public void accept(FeatureDiagramVisitor visitor){
    visitor.handle(this);
  }
}
