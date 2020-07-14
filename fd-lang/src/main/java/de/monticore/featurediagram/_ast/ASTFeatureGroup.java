/* (c) https://github.com/MontiCore/monticore */
package de.monticore.featurediagram._ast;

import de.monticore.featurediagram._symboltable.FeatureSymbol;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * This class extends the generated class {@link ASTFeatureGroupTOP}
 * with a method for obtaining all feature symbols for the
 * features whithin this group.
 */
public interface ASTFeatureGroup extends ASTFeatureGroupTOP {

  default List<FeatureSymbol> getSubFeatureSymbols() {
    List<FeatureSymbol> features = getGroupPartList().stream()
        .map(n -> getEnclosingScope().resolveFeature(n.getName()))
        .filter(Optional::isPresent)
        .map(Optional::get)
        .collect(Collectors.toList());
    return features;
  }
}
