/* (c) https://github.com/MontiCore/monticore */
package tool.analyses;

import tool.transform.FZNModelBuilder;
import tool.util.FeatureNameCollector;

import java.util.Collection;
import java.util.List;
import java.util.Map;

public class DeadFeature extends Analysis<List<String>> {
  @Override
  public void perform(Collection<Map<String, Boolean>> configurations) {
    FeatureNameCollector collector = new FeatureNameCollector();
    getFeatureModel().accept(collector);
    List<String> features = collector.getNames();
    configurations.forEach(configuration -> {
      configuration.forEach((k, v) -> {
        if (v != null && v) {
          features.remove(k);
        }
      });
    });
    setResult(features);
  }

  @Override
  public FZNModelBuilder getModelBuilder() {
    return new FZNModelBuilder(true);
  }
}
