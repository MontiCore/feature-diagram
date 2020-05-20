/* (c) https://github.com/MontiCore/monticore */
package featurediagram._ast;

import featurediagram._symboltable.FeatureSymbol;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

public interface ASTFeatureGroup extends ASTFeatureGroupTOP {

  public default List<FeatureSymbol> getFeatures(){
    List<FeatureSymbol> features = getNameList().stream()
            .map(n -> getEnclosingScope().resolveFeature(n))
            .filter(Optional::isPresent)
            .map(Optional::get)
            .collect(Collectors.toList());
    return features;
  }
}
