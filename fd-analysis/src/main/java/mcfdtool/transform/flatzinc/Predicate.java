/* (c) https://github.com/MontiCore/monticore */
package mcfdtool.transform.flatzinc;

import java.util.ArrayList;
import java.util.List;

public class Predicate {
  private String identifier;

  private List<String> parameters = new ArrayList<>();

  private String expression;

  public void setIdentifier(String identifier) {
    this.identifier = identifier;
  }

  public void addParameter(String parameter) {
    parameters.add(parameter);
  }

  public void setExpression(String expression) {
    this.expression = expression;
  }

  public void print(StringBuilder stringBuilder) {
    stringBuilder
        .append("predicate ")
        .append(identifier)
        .append("(")
        .append(String.join(", ", parameters))
        .append(") = ")
        .append(expression)
        .append(";\n");
  }

  @Override public String toString() {
    StringBuilder sb = new StringBuilder();
    print(sb);
    return sb.toString();
  }
}
