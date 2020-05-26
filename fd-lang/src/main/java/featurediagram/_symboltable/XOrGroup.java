/* (c) https://github.com/MontiCore/monticore */
package featurediagram._symboltable;

import featurediagram._visitor.FeatureDiagramVisitor;

import java.util.List;

public class XOrGroup extends FeatureGroup{
  public XOrGroup(FeatureSymbol parent, List<FeatureSymbol> members) {
    super(parent, members, 1, 1);
  }

  public void accept(FeatureDiagramVisitor visitor){
    visitor.handle(this);
  }
}
