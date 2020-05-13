package featurediagram._symboltable;

import java.util.List;

public class OrGroup extends FeatureGroup{
  public OrGroup(FeatureSymbol parent, List<FeatureSymbol> members) {
    super(parent, members, 1, members.size());
  }
}
