/* (c) https://github.com/MontiCore/monticore */
package mcfdtool.transform.flatzinc;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class Constraint {
  private String name;

  private List<String> parameters = new ArrayList<>();

  public Constraint() {
  }

  public Constraint(String name, List<String> parameters) {
    this.name = name;
    this.parameters = parameters;
  }

  public Constraint(String name, String... parameters) {
    this.name = name;
    this.parameters = Arrays.asList(parameters);
  }

  public void setName(String name) {
    this.name = name;
  }

  public void addParameter(String parameter) {
    this.parameters.add(parameter);
  }

  public void append(StringBuilder stringBuilder) {
    stringBuilder.append("constraint ")
        .append(name)
        .append("(");
    parameters.forEach(parameter -> stringBuilder.append(parameter).append(","));
    stringBuilder.deleteCharAt(stringBuilder.length() - 1)
        .append(");\r\n");
  }
}
