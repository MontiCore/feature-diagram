package featurediagram._symboltable;

import featurediagram._visitor.FeatureDiagramVisitor;

import java.util.List;

public class OrGroup extends FeatureGroup{
  public OrGroup(FeatureSymbol parent, List<FeatureSymbol> members) {
    super(parent, members, 1, members.size());
  }

  public void accept(FeatureDiagramVisitor visitor){
    visitor.handle(this);
  }
}
