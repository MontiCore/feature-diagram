/* (c) https://github.com/MontiCore/monticore */
package tool.analyses;

import java.util.Collection;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class AllProducts extends Analysis<Set<Map<String, Boolean>>> {

  public AllProducts() {
    super();
    builder.setAllSolutions(true);
  }

  @Override
  public void perform(Collection<Map<String, Boolean>> configurations) {
    setResult(new HashSet<>(configurations));
  }

}
