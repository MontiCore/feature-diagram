package featurediagram._visitor;

import featurediagram._symboltable.FeatureDiagramSymbol;
import featurediagram._symboltable.FeatureSymbol;

public interface HierachicalFeatureSymbolVisitor extends FeatureDiagramSymbolVisitor {

  @Override
  default public void visit(FeatureSymbol node) {

  }

  @Override
  default public void traverse(FeatureSymbol node) {
    node.streamChildren().forEach(
            featureGroup -> featureGroup.getMembers().forEach(
                    featureSymbolLoader -> featureSymbolLoader.loadSymbol().
                            ifPresent(featureSymbol -> featureSymbol.accept(getRealThis()))));
  }

  @Override
  default public void traverse(FeatureDiagramSymbol node) {
    String root = node.getRootFeature().getName();
    node.getAllFeatures().stream().filter(featureSymbol -> featureSymbol.getName().equals(root)).forEach(featureSymbol -> featureSymbol.accept(this));
  }
}
