/* (c) https://github.com/MontiCore/monticore */
package tool.transform.flatzinc;

public class Objective {

  private String name = "satisfy";

  public void setName(String name) {
    this.name = name;
  }

  public void append(StringBuilder stringBuilder) {
    stringBuilder.append("solve ")
        .append(name)
        .append(";");
  }
}
