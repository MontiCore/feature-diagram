/* (c) https://github.com/MontiCore/monticore */

package cdfeaturediagram;

import featurediagram.FeatureDiagramMill;
import featurediagram._symboltable.FeatureDiagramScopeBuilder;

public class CDTypeFeatureDiagramSymTabMill extends FeatureDiagramMill {

  @Override protected FeatureDiagramScopeBuilder _featureDiagramScopeBuilder() {
    return new CDTypeFeatureDiagramScopeBuilder();
  }
}
