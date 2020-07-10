/* (c) https://github.com/MontiCore/monticore */
package de.monticore.featurediagram._ast;

import de.monticore.featurediagram._visitor.FeatureNamesCollector;
import de.monticore.featurediagram._visitor.Occurrence;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class ASTFeatureDiagram extends ASTFeatureDiagramTOP {

//  private List<String> features;

  private String rootFeature;

//  /**
//   * Lazy calculation of list of features
//   *
//   * @return
//   */
//  public List<String> getFeaturesList() {
//    if (features == null) {
//      initFeaturesAndRoot();
//    }
//    return features;
//  }

  /**
   * Lazy calculation of root feature
   *
   * @return
   */
  public String getRootFeature() {
    if (rootFeature == null) {
      initFeaturesAndRoot();
    }
    return rootFeature;
  }

  /**
   * Performs a visitor-based analysis that traverses the AST and for each feature name,
   * calculates whether it exists on the left-hand side, the right-hand side, or both sides
   * of feature rules. A root feature is the only featue that occurs only on the left-hand
   * side of rules (and not on the right-hand side). As by-product, all feature names are
   * identified.
   */
  protected void initFeaturesAndRoot() {
    FeatureNamesCollector collector = new FeatureNamesCollector();
    this.accept(collector);
    HashMap<String, Occurrence> featuresMap = collector.getOccurrences();
//    features = new ArrayList<>();
    featuresMap.forEach((k, v) -> {
//      features.add(k);
      if (v == Occurrence.LEFT) {
        // the CoCo "HasTreeShape" checks if no root or more than one root exists
        rootFeature = k;
      }
    });
  }
}