/* (c) https://github.com/MontiCore/monticore */
package featurediagram._symboltable;

import featurediagram._visitor.FeatureDiagramVisitor;

import java.util.List;

public class CardinalityGroup extends FeatureGroup{
  public CardinalityGroup(FeatureSymbol parent, List<FeatureSymbol> members, int min, int max) {
    super(parent, members, min, max);
  }

  public void accept(FeatureDiagramVisitor visitor){
    visitor.handle(this);
  }
}
