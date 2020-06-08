/* (c) https://github.com/MontiCore/monticore */
package featurediagram._ast;

import featurediagram._symboltable.FeatureSymbol;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

public interface ASTFeatureGroup extends ASTFeatureGroupTOP {

  default List<FeatureSymbol> getSubFeatureSymbols(){
    List<FeatureSymbol> features = getGroupPartList().stream()
            .map(n -> getEnclosingScope().resolveFeature(n.getName()))
            .filter(Optional::isPresent)
            .map(Optional::get)
            .collect(Collectors.toList());
    return features;
  }
}
