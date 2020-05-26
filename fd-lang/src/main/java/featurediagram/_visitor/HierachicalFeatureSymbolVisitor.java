/* (c) https://github.com/MontiCore/monticore */
package featurediagram._visitor;

import featurediagram._symboltable.FeatureDiagramSymbol;
import featurediagram._symboltable.FeatureGroup;
import featurediagram._symboltable.FeatureSymbol;

public interface HierachicalFeatureSymbolVisitor extends FeatureDiagramVisitor {

  @Override
  default public void visit(FeatureSymbol node) {

  }

  @Override
  default public void traverse(FeatureSymbol node) {
    node.streamChildren().forEach(child ->
            child.accept(getRealThis())
    );
  }

  @Override
  default public void traverse(FeatureDiagramSymbol node) {
    node.getRootFeature().accept(getRealThis());
  }

  @Override
  default void traverse(FeatureGroup node) {
    node.getMembers().forEach(featureSymbol -> featureSymbol.accept(getRealThis()));
  }
}
