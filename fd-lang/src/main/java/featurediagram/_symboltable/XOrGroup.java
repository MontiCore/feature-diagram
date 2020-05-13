package featurediagram._symboltable;

import java.util.List;

public class XOrGroup extends FeatureGroup{
  public XOrGroup(FeatureSymbol parent, List<FeatureSymbol> members) {
    super(parent, members, 1, 1);
  }
}
