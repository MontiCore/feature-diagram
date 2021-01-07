/* (c) https://github.com/MontiCore/monticore */

package de.monticore.featurediagram;

//TODO remove if these are resetted in generated mill
public class FeatureDiagramMill extends FeatureDiagramMillTOP {

  public  static  void reset ()  {
    FeatureDiagramMillTOP.reset();
    millFeatureDiagramGlobalScope = null;
    millFeatureDiagramArtifactScope = null;
    millParser = null;
    millFeatureDiagramScope = null;
    millFeatureDiagramScopesGenitor = null;
    millFeatureDiagramScopesGenitorDelegator = null;
  }

}
