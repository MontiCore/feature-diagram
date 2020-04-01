package tool.analyses;

import tool.transform.FZNModelBuilder;

import java.util.Collection;
import java.util.Map;

public class IsVoidFeatureModel extends Analysis<Boolean> {

  @Override
  public void perform(Collection<Map<String, Boolean>> configurations) {
    setResult(!configurations.isEmpty());
  }

  @Override
  public FZNModelBuilder getModelBuilder() {
    return new FZNModelBuilder(false);
  }


}
