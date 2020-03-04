/* (c) https://github.com/MontiCore/monticore */

package cdfeaturediagram;

import featurediagram._symboltable.FeatureDiagramScopeBuilder;
import featurediagram._symboltable.FeatureDiagramSymTabMill;

public class CDTypeFeatureDiagramSymTabMill extends FeatureDiagramSymTabMill {

  @Override protected FeatureDiagramScopeBuilder _featureDiagramScopeBuilder() {
    return new CDTypeFeatureDiagramScopeBuilder();
  }
}
