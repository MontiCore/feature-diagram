package tool.analyses;

import java.util.Collection;
import java.util.Map;

public class CompleteToValidConfig extends Analysis<Map<String, Boolean>>{
  @Override
  public void perform(Collection<Map<String, Boolean>> configurations) {
    if(configurations.isEmpty()){
      setResult(null);
    }else {
      setResult(configurations.iterator().next());
    }
  }
}
