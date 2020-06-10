/* (c) https://github.com/MontiCore/monticore */
package featurediagram._ast;

import de.se_rwth.commons.logging.Log;
import featurediagram.FeatureDiagramMill;
import featurediagram._visitor.FeatureNamesCollector;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;


public class ASTFeatureDiagram extends ASTFeatureDiagramTOP{

  private List<String> features;
  private String rootFeature;



  public List<String > getFeaturesList() {
    if(features == null){
      initFeaturesAndRoot();
    }
    return features;
  }

  public String getRootFeature() {
    if(rootFeature == null){
      initFeaturesAndRoot();
    }
    return  rootFeature;
  }

  private void initFeaturesAndRoot(){
    FeatureNamesCollector collector = new FeatureNamesCollector();
    this.accept(collector);
    HashMap<String, FeatureNamesCollector.Occurrence> featuresMap = collector.getOccurrences();
    features= new ArrayList<>();
    featuresMap.forEach((k,v)-> {
      features.add(k);
      if(v == FeatureNamesCollector.Occurrence.LEFT){
        rootFeature = k;
      }
    });
    List<String> rootfeatures = featuresMap.entrySet().stream()
            .filter(e -> FeatureNamesCollector.Occurrence.LEFT == e.getValue())
            .map(Map.Entry::getKey)
            .collect(Collectors.toList());
    if(rootfeatures.size() == 0){
      Log.error("0xFD003 Featurediagram" + getName() +
              "has no root node.");
    }
    if (rootfeatures.size() > 1) {
      Log.error("0xFD001 Featurediagram" + getName() +
              "has multiple root nodes.");
    }
  }
}