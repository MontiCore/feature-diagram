/* (c) https://github.com/MontiCore/monticore */

package de.monticore.featurediagram;


public class FeatureDiagramMill extends FeatureDiagramMillTOP {

    public static void initMe(FeatureDiagramMill a) {
      FeatureDiagramMillTOP.initMe(a);
      millFeatureDiagramInheritanceHandler = a;
    }

    public static void reset() {
      FeatureDiagramMillTOP.reset();
      millFeatureDiagramInheritanceHandler = null;
    }
}
