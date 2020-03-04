/* (c) https://github.com/MontiCore/monticore */
package featurediagram._symboltable;

import de.se_rwth.commons.logging.Log;

import java.util.Collection;
import java.util.Iterator;
import java.util.List;

public class FeatureGroup {

  protected FeatureSymbolLoader parent;

  protected List<FeatureSymbolLoader> members;

  protected GroupKind kind;

  protected int min;

  protected int max;

  public FeatureGroup(FeatureSymbolLoader parent,
      List<FeatureSymbolLoader> members, int min, int max) {
    this.parent = parent;
    this.members = members;
    this.kind = GroupKind.CARDINALITY;
    this.min = min;
    this.max = max;
  }

  public FeatureGroup(FeatureSymbolLoader parent,
      List<FeatureSymbolLoader> members, GroupKind kind) {
    this.parent = parent;
    this.members = members;
    this.kind = kind;

    switch (kind) {
      case AND:
        this.min = Integer.MAX_VALUE;
        this.max = Integer.MAX_VALUE;
        break;
      case OR:
        this.min = 1;
        this.max = Integer.MAX_VALUE;
        break;
      case XOR:
        this.min = 1;
        this.max = 1;
        break;
      case CARDINALITY:
      default:
        Log.error("0xFD1002 Feature group with parent '" + parent.getName()
            + "' is a cardinality group, but its cardinality has not been set!");
        break;
    }
  }

  public FeatureSymbolLoader getParent() {
    return parent;
  }

  public void setParent(FeatureSymbolLoader parent) {
    this.parent = parent;
  }

  public List<FeatureSymbolLoader> getMembers() {
    return members;
  }

  public void setMembers(List<FeatureSymbolLoader> members) {
    this.members = members;
  }

  public GroupKind getKind() {
    return kind;
  }

  public void setKind(GroupKind kind) {
    this.kind = kind;
  }

  public int size() {
    return members.size();
  }

  public boolean isEmpty() {
    return members.isEmpty();
  }

  public boolean contains(Object o) {
    return members.contains(o);
  }

  public Iterator<FeatureSymbolLoader> iterator() {
    return members.iterator();
  }

  public boolean add(FeatureSymbolLoader featureSymbolLoader) {
    return members.add(featureSymbolLoader);
  }

  public boolean addAll(
      Collection<? extends FeatureSymbolLoader> c) {
    return members.addAll(c);
  }

  public void clear() {
    members.clear();
  }

  public FeatureSymbolLoader get(int index) {
    return members.get(index);
  }

  public FeatureSymbolLoader remove(int index) {
    return members.remove(index);
  }

  public int getMin() {
    return min;
  }

  public void setMin(int min) {
    this.min = min;
  }

  public int getMax() {
    return max;
  }

  public void setMax(int max) {
    this.max = max;
  }
}
