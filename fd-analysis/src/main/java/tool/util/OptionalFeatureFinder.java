/* (c) https://github.com/MontiCore/monticore */
package tool.util;

import featurediagram._symboltable.AndGroup;
import featurediagram._symboltable.FeatureSymbol;
import featurediagram._visitor.FeatureDiagramVisitor;

import java.util.ArrayList;
import java.util.List;

public class OptionalFeatureFinder implements FeatureDiagramVisitor {
  private List<FeatureSymbol> optionalFeatures = new ArrayList<>();

  @Override
  public void visit(AndGroup andGroup) {
    for(int i = 0; i < andGroup.size(); i++){
      if(andGroup.getOptionalFeatures().get(i)){
        optionalFeatures.add(andGroup.get(i));
      }
    }
  }

  public List<FeatureSymbol> getOptionalFeatures() {
    return optionalFeatures;
  }
}
