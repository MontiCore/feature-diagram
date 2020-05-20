/* (c) https://github.com/MontiCore/monticore */
package featurediagram._symboltable;

import java.util.List;

public class CardinalitiyGroup extends FeatureGroup{
  public CardinalitiyGroup(FeatureSymbol parent, List<FeatureSymbol> members, int min, int max) {
    super(parent, members, min, max);
  }
}
