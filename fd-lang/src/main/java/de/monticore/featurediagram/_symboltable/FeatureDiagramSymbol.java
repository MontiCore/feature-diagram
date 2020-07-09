/* (c) https://github.com/MontiCore/monticore */
package de.monticore.featurediagram._symboltable;

import java.util.List;

/**
 * This class extends the generated FeatureDiagram and provides a method
 * for obtaining all features of the feature diagram in form of a list of
 * FeatureSymbols
 */
public class FeatureDiagramSymbol extends FeatureDiagramSymbolTOP {

  public FeatureDiagramSymbol(String name) {
    super(name);
  }

  public List<FeatureSymbol> getAllFeatures() {
    return getSpannedScope().getLocalFeatureSymbols();
  }

}
