/* (c) https://github.com/MontiCore/monticore */
package complexconstraintfeaturediagram._symboltable;

public class ComplexConstraintFeatureDiagramLanguage
    extends ComplexConstraintFeatureDiagramLanguageTOP {
  public ComplexConstraintFeatureDiagramLanguage() {
    super("Complex Constraint Feature Diagram", "fd");
  }

  @Override
  protected ComplexConstraintFeatureDiagramModelLoader provideModelLoader() {
    return new ComplexConstraintFeatureDiagramModelLoader(this);
  }
}
