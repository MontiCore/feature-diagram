/* (c) https://github.com/MontiCore/monticore */
package tool.analyses;

import featurediagram._symboltable.FeatureSymbol;
import tool.transform.FZNModelBuilder;
import tool.util.OptionalFeatureFinder;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class FalseOptional extends Analysis<List<String>> {
  @Override
  public void perform(Collection<Map<String, Boolean>> configurations) {
    OptionalFeatureFinder finder = new OptionalFeatureFinder();
    getFeatureModel().accept(finder);
    List<String> optionalFeatures = finder.getOptionalFeatures().stream()
        .map(FeatureSymbol::getName).collect(Collectors.toList());
    for (Map<String, Boolean> config : configurations) {
      List<String> selected = config.entrySet().stream()
          .filter(e -> e.getValue() != null && e.getValue()).map(Map.Entry::getKey)
          .collect(Collectors.toList());
      optionalFeatures = optionalFeatures.stream().filter(selected::contains)
          .collect(Collectors.toList());
      if (optionalFeatures.isEmpty()) {
        break;
      }
    }
    setResult(optionalFeatures);
  }

  @Override
  public FZNModelBuilder getModelBuilder() {
    return new FZNModelBuilder(true);
  }
}
