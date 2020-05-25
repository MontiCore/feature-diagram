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

  @Override
  public List<String > getFeaturesList() {
    if(featuress == null){
      initFeaturesAndRoot();
    }
    return featuress;
  }

  @Override
  public String getRootFeature() {
    if(rootFeature == null){
      initFeaturesAndRoot();
    }
    return  rootFeature;
  }

  private void initFeaturesAndRoot(){
    FeatureNamesCollector collector = new FeatureNamesCollector();
    this.accept(collector);
    HashMap<String, FeatureNamesCollector.Occurrence> features = collector.getNames();
    featuress = new ArrayList<>();
    features.forEach((k,v)-> {
      featuress.add(k);
      if(v == FeatureNamesCollector.Occurrence.LEFT){
        setRootFeature(k);
      }
    });
    List<String> rootfeatures = features.entrySet().stream()
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
