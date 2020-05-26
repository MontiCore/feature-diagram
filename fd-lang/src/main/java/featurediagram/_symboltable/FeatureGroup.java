/* (c) https://github.com/MontiCore/monticore */
package featurediagram._symboltable;

import de.se_rwth.commons.logging.Log;
import featurediagram._visitor.FeatureDiagramVisitor;

import java.util.Collection;
import java.util.Iterator;
import java.util.List;

public abstract class FeatureGroup {

  protected FeatureSymbol parent;

  protected List<FeatureSymbol> members;

  protected int min;

  protected int max;

  public FeatureGroup(FeatureSymbol parent,
      List<FeatureSymbol> members, int min, int max) {
    this.parent = parent;
    this.members = members;
    this.min = min;
    this.max = max;
  }

  public FeatureSymbol getParent() {
    return parent;
  }

  public void setParent(FeatureSymbol parent) {
    this.parent = parent;
  }

  public List<FeatureSymbol> getMembers() {
    return members;
  }

  public void setMembers(List<FeatureSymbol> members) {
    this.members = members;
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

  public Iterator<FeatureSymbol> iterator() {
    return members.iterator();
  }

  public boolean add(FeatureSymbol featureSymbol) {
    return members.add(featureSymbol);
  }

  public boolean addAll(
      Collection<? extends FeatureSymbol> c) {
    return members.addAll(c);
  }

  public void clear() {
    members.clear();
  }

  public FeatureSymbol get(int index) {
    return members.get(index);
  }

  public FeatureSymbol remove(int index) {
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

  public void accept(FeatureDiagramVisitor visitor){
    visitor.handle(this);
  }

}
