/* (c) https://github.com/MontiCore/monticore */
package mcfdtool.transform.flatzinc;

public class Parameter {
  public void print(StringBuilder stringBuilder) {
  }

  @Override public String toString() {
    StringBuilder sb = new StringBuilder();
    print(sb);
    return sb.toString();
  }
}
