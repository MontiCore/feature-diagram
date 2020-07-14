/* (c) https://github.com/MontiCore/monticore */
package tool.transform.trafos;

import de.monticore.ast.ASTNode;
import de.monticore.expressions.commonexpressions._ast.*;
import de.monticore.expressions.expressionsbasis._ast.ASTArguments;
import de.monticore.expressions.expressionsbasis._ast.ASTNameExpression;
import de.monticore.featurediagram._ast.ASTExcludes;
import de.monticore.featurediagram._ast.ASTFeatureConstraint;
import de.monticore.featurediagram._ast.ASTRequires;
import de.monticore.featurediagram._symboltable.FeatureDiagramSymbol;
import de.monticore.featurediagram._visitor.FeatureDiagramVisitor;
import tool.transform.FeatureModel2FlatZincModelTrafo;
import tool.transform.flatzinc.Constraint;
import tool.transform.flatzinc.Variable;
import tool.util.VariableDeterminator;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

public class ComplexConstraint2FZN
    implements FeatureModel2FlatZincModelTrafo, FeatureDiagramVisitor {

  private List<Constraint> fznConstraints = new ArrayList<>();

  private Map<String, Variable> variables = new HashMap<>();

  private Map<String, Variable> otherVariables;

  private List<String> usedNames;

  private Map<ASTNode, String> names = new HashMap<>();

  private Map<ASTNode, Variable.Type> types = new HashMap<>();

  private int i = 1;

  private FeatureDiagramSymbol featureModel;

  public List<Variable> getVariables() {
    return new ArrayList<>(variables.values());
  }

  @Override
  public void perform() {
    VariableDeterminator det = new VariableDeterminator();
    featureModel.accept(det);
    otherVariables = det.getVariables().stream()
        .collect(Collectors.toMap(Variable::getName, Function.identity()));
    NameCalculator calculator = new NameCalculator();
    featureModel.getAstNode().accept(calculator);
    names = calculator.getNames();
    featureModel.getAstNode().accept(this);
  }

  @Override
  public void setNames(List<String> names) {
    usedNames = names;
  }

  @Override
  public FeatureDiagramSymbol getFeatureModel() {
    return featureModel;
  }

  @Override
  public void setFeatureModel(FeatureDiagramSymbol featureModel) {
    this.featureModel = featureModel;
  }

  public List<Constraint> getConstraints() {
    return fznConstraints;
  }

  public void setNames(Map<ASTNode, String> names) {
    this.names = names;
  }

  @Override
  public void endVisit(ASTNode node) {

  }

  @Override
  public void endVisit(ASTFeatureConstraint node) {
    fznConstraints.add(new Constraint("bool_eq", "true", names.get(node.getConstraint())));
  }

  @Override
  public void endVisit(ASTCallExpression node) {
    throw new UnsupportedOperationException();
  }

  @Override
  public void endVisit(ASTBooleanNotExpression node) {
    String name = createVariable(node, Variable.Type.BOOL);
    fznConstraints.add(new Constraint("bool_not", name, names.get(node.getExpression())));
  }

  @Override
  public void endVisit(ASTLogicalNotExpression node) {
    String name = createVariable(node, Variable.Type.BOOL);
    fznConstraints.add(new Constraint("bool_not", name, names.get(node.getExpression())));
  }

  @Override
  public void endVisit(ASTMultExpression node) {
    String name = createVariable(node, Variable.Type.INT);
    String constraintName = getTypeFromName(node.getLeft()) + "_times";
    fznConstraints.add(
        new Constraint(constraintName, names.get(node.getLeft()), names.get(node.getRight()),
            name));
  }

  @Override
  public void endVisit(ASTDivideExpression node) {
    String name = createVariable(node, variables.get(names.get(node.getLeft())).getType());
    String constraintName = getTypeFromName(node.getLeft()) + "_div";
    fznConstraints.add(
        new Constraint(constraintName, names.get(node.getLeft()), names.get(node.getRight()),
            name));
  }

  @Override
  public void endVisit(ASTModuloExpression node) {
    String name = createVariable(node, Variable.Type.INT);
    fznConstraints.add(
        new Constraint("int_mod", names.get(node.getLeft()), names.get(node.getRight()), name));
  }

  @Override
  public void endVisit(ASTPlusExpression node) {
    String name = createVariable(node, variables.get(names.get(node.getLeft())).getType());
    String constraintName = getTypeFromName(node.getLeft()) + "_plus";
    fznConstraints.add(
        new Constraint(constraintName, names.get(node.getLeft()), names.get(node.getRight()),
            name));
  }

  @Override
  public void endVisit(ASTMinusExpression node) {
    String name = createVariable(node, variables.get(names.get(node.getLeft())).getType());
    // l-r=d <=> d+r=l
    String constraintName = getTypeFromName(node.getLeft()) + "_plus";
    fznConstraints.add(new Constraint(constraintName, names.get(node.getRight()), name,
        names.get(node.getLeft())));
  }

  @Override
  public void endVisit(ASTEqualsExpression node) {
    String name = createVariable(node, Variable.Type.BOOL);
    String constraintName = getTypeFromName(node.getLeft()) + "_eq_reif";
    fznConstraints.add(
        new Constraint(constraintName, names.get(node.getLeft()), names.get(node.getRight()),
            name));
  }

  @Override
  public void endVisit(ASTLessEqualExpression node) {
    String name = createVariable(node, Variable.Type.BOOL);
    String constraintName = getTypeFromName(node.getLeft()) + "_le_reif";
    fznConstraints.add(
        new Constraint(constraintName, names.get(node.getLeft()), names.get(node.getRight()),
            name));
  }

  @Override
  public void endVisit(ASTGreaterEqualExpression node) {
    String name = createVariable(node, Variable.Type.BOOL);
    String constraintName = getTypeFromName(node.getLeft()) + "_le_reif";
    // l>=r <=> r<=l
    fznConstraints.add(
        new Constraint(constraintName, names.get(node.getRight()), names.get(node.getLeft()),
            name));
  }

  @Override
  public void endVisit(ASTLessThanExpression node) {
    String name = createVariable(node, Variable.Type.BOOL);
    String constraintName = getTypeFromName(node.getLeft()) + "_lt_reif";
    fznConstraints.add(
        new Constraint(constraintName, names.get(node.getLeft()), names.get(node.getRight()),
            name));
  }

  @Override
  public void endVisit(ASTGreaterThanExpression node) {
    String name = createVariable(node, Variable.Type.BOOL);
    String constraintName = getTypeFromName(node.getLeft()) + "_le_reif";
    // l>r <=> r<l
    fznConstraints.add(
        new Constraint(constraintName, names.get(node.getRight()), names.get(node.getLeft()),
            name));
  }

  @Override
  public void endVisit(ASTNotEqualsExpression node) {
    String name = createVariable(node, Variable.Type.BOOL);
    String constraintName = getTypeFromName(node.getLeft()) + "_ne_reif";
    fznConstraints.add(
        new Constraint(constraintName, names.get(node.getLeft()), names.get(node.getRight()),
            name));
  }

  @Override
  public void endVisit(ASTBooleanAndOpExpression node) {
    String name = createVariable(node, Variable.Type.BOOL);
    fznConstraints.add(
        new Constraint("bool_and", names.get(node.getLeft()), names.get(node.getRight()), name));
  }

  @Override
  public void endVisit(ASTBooleanOrOpExpression node) {
    String name = createVariable(node, Variable.Type.BOOL);
    fznConstraints.add(
        new Constraint("bool_or", names.get(node.getLeft()), names.get(node.getRight()), name));
  }

  @Override
  public void endVisit(ASTConditionalExpression node) {
    String name = createVariable(node,
        variables.get(names.get(node.getTrueExpression())).getType());
    String helperName = "helper" + name;
    Variable helpervariable = new Variable();
    helpervariable.setType(Variable.Type.BOOL);
    helpervariable.setName(helperName);
    helpervariable.setAnnotation("var_is_introduced");
    variables.put(helperName, helpervariable);
    String type = getTypeFromName(node.getTrueExpression());
    fznConstraints.add(new Constraint("bool_not", names.get(node.getCondition()), helperName));
    fznConstraints.add(new Constraint(type + "_eq_reif", names.get(node.getTrueExpression()), name,
        names.get(node.getCondition())));
    fznConstraints.add(
        new Constraint(type + "_eq_reif", names.get(node.getFalseExpression()), name, helperName));

  }

  @Override
  public void endVisit(ASTRequires node) {
    String name = createVariable(node, Variable.Type.BOOL);
    fznConstraints.add(new Constraint("bool_or", names.get(node.getLeft()) + "IsUnselected",
        names.get(node.getRight()) + "IsSelected", name));
  }

  @Override
  public void endVisit(ASTExcludes node) {
    String name = createVariable(node, Variable.Type.BOOL);
    fznConstraints.add(
        new Constraint("bool_or", names.get(node.getLeft()) + "IsUnselected",
            names.get(node.getRight()) + "IsUnselected",
            name));
  }

  @Override
  public void endVisit(ASTBracketExpression node) {
    //Is handled by Namescalcualtor and Parser
  }

  @Override
  public void endVisit(ASTArguments node) {
    throw new UnsupportedOperationException();
  }

  @Override
  public void endVisit(ASTInfixExpression node) {
    throw new UnsupportedOperationException();
  }

  @Override
  public void endVisit(ASTCommonExpressionsNode node) {
    throw new UnsupportedOperationException();
  }

  private String getTypeFromName(ASTNode node) {
    String constraintname = "";
    try {
      switch (variables.containsKey(names.get(node)) ?
          variables.get(names.get(node)).getType() :
          otherVariables.get(names.get(node)).getType()) {
        case BOOL:
          constraintname = "bool";
          break;
        case INT:
          constraintname = "int";
          break;
        case FLOAT:
          constraintname = "float";
          break;
      }
    }
    catch (NullPointerException e) {
      return "int";
    }
    return constraintname;
  }

  private String createVariable(ASTNode node, Variable.Type type) {
    String name = names.get(node);
    Variable variable = new Variable();
    variable.setType(type);
    variable.setName(name);
    variable.setAnnotation("var_is_introduced");
    variables.put(name, variable);
    return name;
  }

  private class NameCalculator implements FeatureDiagramVisitor {

    private int i = 1;

    private Map<ASTNode, String> names = new HashMap<>();

    @Override
    public void visit(ASTNode node) {

    }

    @Override
    public void visit(ASTCallExpression node) {
      names.put(node, "callExpr" + i++);
    }

    @Override
    public void visit(ASTBooleanNotExpression node) {
      names.put(node, "boolNotExpr" + i++);
    }

    @Override
    public void visit(ASTLogicalNotExpression node) {
      names.put(node, "logicalNotExpr" + i++);
    }

    @Override
    public void visit(ASTMultExpression node) {
      names.put(node, "multExpr" + i++);
    }

    @Override
    public void visit(ASTDivideExpression node) {
      names.put(node, "divExpr" + i++);
    }

    @Override
    public void visit(ASTModuloExpression node) {
      names.put(node, "modExpr" + i++);
    }

    @Override
    public void visit(ASTPlusExpression node) {
      names.put(node, "plusExpr" + i++);
    }

    @Override
    public void visit(ASTMinusExpression node) {
      names.put(node, "minusExpr" + i++);
    }

    @Override
    public void visit(ASTLessEqualExpression node) {
      names.put(node, "leqExpr" + i++);
    }

    @Override
    public void visit(ASTGreaterEqualExpression node) {
      names.put(node, "geqExpr" + i++);
    }

    @Override
    public void visit(ASTLessThanExpression node) {
      names.put(node, "lessExpr" + i++);
    }

    @Override
    public void visit(ASTGreaterThanExpression node) {
      names.put(node, "greaterExpr" + i++);
    }

    @Override
    public void visit(ASTEqualsExpression node) {
      names.put(node, "eqExpr" + i++);
    }

    @Override
    public void visit(ASTNotEqualsExpression node) {
      names.put(node, "neqExpr" + i++);
    }

    @Override
    public void visit(ASTBooleanAndOpExpression node) {
      names.put(node, "boolAndExpr" + i++);
    }

    @Override
    public void visit(ASTBooleanOrOpExpression node) {
      names.put(node, "boolOrExpr" + i++);
    }

    @Override
    public void visit(ASTConditionalExpression node) {
      names.put(node, "condExpr" + i++);
    }

    @Override
    public void visit(ASTBracketExpression node) {
      //
      names.put(node, names.get(node.getExpression()));
    }

    @Override
    public void visit(ASTArguments node) {
      names.put(node, "argumExpr" + i++);
    }

    @Override
    public void visit(ASTInfixExpression node) {
      names.put(node, "infixExpr" + i++);
    }

    @Override
    public void visit(ASTCommonExpressionsNode node) {
      names.put(node, "commonExpr" + i++);
    }

    @Override
    public void visit(ASTNameExpression node) {
      names.put(node, node.getName());
    }

    @Override
    public void visit(ASTExcludes node) {
      names.put(node, "excludes" + i++);
    }

    @Override
    public void visit(ASTRequires node) {
      names.put(node, "requires" + i++);
    }

    public Map<ASTNode, String> getNames() {
      return names;
    }
  }
}
