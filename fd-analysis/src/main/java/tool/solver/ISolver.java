package tool.solver;

import java.util.List;
import java.util.Map;

public interface ISolver {
  public List<Map<String, Boolean>> solve(String model, List<String> featureNames, Boolean isAllsSolutions);
}
