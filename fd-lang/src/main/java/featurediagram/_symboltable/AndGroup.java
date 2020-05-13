package featurediagram._symboltable;

import java.util.List;

public class AndGroup extends FeatureGroup{

  private List<Boolean> optionalFeatures;

  public AndGroup(FeatureSymbol parent, List<FeatureSymbol> members, List<Boolean> optionalFeatures) {
    super(parent, members, (int) optionalFeatures.stream().filter(Boolean::booleanValue).count(), members.size());
    this.optionalFeatures = optionalFeatures;
  }
}
