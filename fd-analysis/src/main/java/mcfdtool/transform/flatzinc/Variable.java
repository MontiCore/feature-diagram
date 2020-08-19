/* (c) https://github.com/MontiCore/monticore */
package mcfdtool.transform.flatzinc;

public class Variable {
  private String name;

  private Type type;

  private String lowerLimit;

  private String upperLimit;

  private String annotation = "";

  public Variable(String name, Type type) {
    this(name, type, "");
  }

  public Variable(String name, Type type, String annotation) {
    this.name = name;
    this.type = type;
    this.annotation = annotation;
  }

  public Variable() {
  }

  public static Variable newIntVariable(String name){
    return newIntVariable(name, "");
  }

  public static Variable newBoolVariable(String name){
    return newBoolVariable(name, "");
  }

  public static Variable newIntVariable(String name, String annotation){
    return new Variable(name, Type.INT, annotation);
  }

  public static Variable newBoolVariable(String name, String annotation){
    return new Variable(name, Type.BOOL, annotation);
  }

  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }

  public void setAnnotation(String annotation) {
    this.annotation = annotation;
  }

  public void setLowerLimit(String lowerLimit) {
    this.lowerLimit = lowerLimit;
  }

  public void setUpperLimit(String upperLimit) {
    this.upperLimit = upperLimit;
  }

  public Type getType() {
    return type;
  }

  public void setType(Type type) {
    this.type = type;
  }

  public void print(StringBuilder stringBuilder) {
    switch (type) {
      case INT:
      case FLOAT:
        if (lowerLimit != null && upperLimit != null) {
          stringBuilder.append("var ")
              .append(lowerLimit)
              .append("..")
              .append(upperLimit)
              .append(": ");
        }
        else {
          stringBuilder.append("var int: ");
        }

        break;
      case BOOL:
        stringBuilder.append("var bool: ");
    }
    stringBuilder
        .append(name)
        .append(annotation.isEmpty() ? "" : "  ::" + annotation)
        .append(";\r\n");

  }

  public enum Type {
    BOOL, INT, FLOAT
  }
}
