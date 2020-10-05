package de.monticore.featureconfigurationpartial._visitor;

import de.monticore.featureconfiguration._visitor.FeatureConfigurationVisitor;
import de.monticore.featureconfigurationpartial._ast.ASTSelect;
import de.monticore.featureconfigurationpartial._ast.ASTUnselect;

import java.util.HashSet;
import java.util.Set;

public class UnSelectedFeatureCollector implements FeatureConfigurationPartialVisitor {
  private Set<String> selectedFeatures;
  private Set<String> unselectedFeatures;
  private FeatureConfigurationPartialVisitor realFeatureConfigurationPartialVisitorThis;
  private FeatureConfigurationVisitor realFeatureConfigurationVisitorThis;


  public UnSelectedFeatureCollector() {
    this.selectedFeatures = new HashSet<>();
    this.unselectedFeatures = new HashSet<>();
    this.realFeatureConfigurationPartialVisitorThis = this;
    this.realFeatureConfigurationVisitorThis = this;
  }

  @Override
  public void setRealThis(FeatureConfigurationPartialVisitor realThis) {
    this.realFeatureConfigurationPartialVisitorThis = realThis;
  }

  @Override
  public void setRealThis(FeatureConfigurationVisitor realThis) {
    this.realFeatureConfigurationVisitorThis = realThis;
  }

  public FeatureConfigurationPartialVisitor getRealThis() {
    return this.realFeatureConfigurationPartialVisitorThis;
  }

  public Set<String> getSelectedFeatures() {
    return selectedFeatures;
  }

  public Set<String> getUnselectedFeatures() {
    return unselectedFeatures;
  }

  @Override
  public void visit(ASTSelect ast) {
    this.selectedFeatures.addAll(ast.getNameList());
  }

  @Override
  public void visit(ASTUnselect ast) {
    this.unselectedFeatures.addAll(ast.getNameList());
  }
}
