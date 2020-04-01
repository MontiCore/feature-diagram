package tool.util;

import featurediagram._symboltable.FeatureSymbol;
import featurediagram._visitor.FeatureDiagramSymbolVisitor;

import java.util.ArrayList;
import java.util.List;

public class OptionalFeatureFinder implements FeatureDiagramSymbolVisitor {
  private List<FeatureSymbol> optionalFeatures = new ArrayList<>();
  @Override
  public void visit(FeatureSymbol symbol) {
    if (symbol.isIsOptional()){
      optionalFeatures.add(symbol);
    }
  }

  public List<FeatureSymbol> getOptionalFeatures() {
    return optionalFeatures;
  }
}
