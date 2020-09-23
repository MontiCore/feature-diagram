package fddiff;

import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
public class Feature {

  @EqualsAndHashCode.Exclude
  private Feature parent;

  private final String name;

  public Feature(String name) {
    this.name = name;
  }

  @Override
  public String toString() {
    return name + " [parent = " + (parent != null ? parent.name : null) + "]";
  }
}
