/* (c) https://github.com/MontiCore/monticore */

package featurediagram._symboltable;

import java.util.Collection;
import java.util.Iterator;
import java.util.List;

public class FeatureGroup {

  protected FeatureSymbolLoader parent;

  protected List<FeatureSymbolLoader> members;

  protected GroupKind kind;

  public FeatureGroup(FeatureSymbolLoader parent,
      List<FeatureSymbolLoader> members, GroupKind kind) {
    this.parent = parent;
    this.members = members;
    this.kind = kind;
  }

  enum GroupKind {
    XOR, OR, AND, CARDINALITY;
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
}
