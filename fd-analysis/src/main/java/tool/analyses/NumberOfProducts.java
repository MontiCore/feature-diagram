package tool.analyses;

import java.util.Collection;
import java.util.Map;

public class NumberOfProducts extends Analysis<Integer> {
  public NumberOfProducts() {
    super();
    builder.setAllSolutions(true);
  }

  @Override
  public void perform(Collection<Map<String, Boolean>> configurations) {
    setResult(configurations.size());
  }



}
