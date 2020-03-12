package tool.transform.flatzinc;

import java.util.ArrayList;
import java.util.List;

public class Predicate {
  public void append(StringBuilder stringBuilder) {
    stringBuilder
        .append("predicate ")
        .append(identifier)
        .append("(")
        .append(String.join(", ", parameters))
        .append(") = ")
        .append(experession)
        .append(";\n");
  }

  private String identifier;
  private List<String> parameters = new ArrayList<>();
  private String experession;

  public void setIdentifier(String identifier) {
    this.identifier = identifier;
  }

  public void addParameter(String parameter) {
    parameters.add(parameter);
  }

  public void setExperession(String experession) {
    this.experession = experession;
  }
}
