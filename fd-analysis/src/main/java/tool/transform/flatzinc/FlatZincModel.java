/* (c) https://github.com/MontiCore/monticore */
package tool.transform.flatzinc;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class FlatZincModel {
  private List<Predicate> predicates = new ArrayList<>();

  private List<Parameter> parameters = new ArrayList<>();

  private List<Variable> variables = new ArrayList<>();

  private List<Constraint> constraints = new ArrayList<>();

  private Objective objective = new Objective();

  public List<Predicate> getPredicates() {
    return predicates;
  }

  public void setPredicates(List<Predicate> predicates) {
    this.predicates = predicates;
  }

  public void addPredicate(Predicate predicate) {
    if (predicates == null) {
      predicates = new ArrayList<>();
    }
    this.predicates.add(predicate);
  }

  public List<Parameter> getParameters() {
    return parameters;
  }

  public void setParameters(Parameter parameter) {
    if (parameters == null) {
      parameters = new ArrayList<>();
    }
    this.parameters.add(parameter);
    ;
  }

  public void setParameters(List<Parameter> parameters) {
    this.parameters = parameters;
  }

  public List<Variable> getVariables() {
    return variables;
  }

  public void setVariables(List<Variable> variables) {
    this.variables = variables;
  }

  public void addVariable(Variable variable) {
    if (variable == null) {
      return;
    }
    if (this.variables == null) {
      this.variables = new ArrayList<>();
    }
    this.variables.add(variable);
  }

  public void addVariables(Collection<Variable> variables) {
    if (variables == null) {
      return;
    }
    if (this.variables == null) {
      this.variables = new ArrayList<>();
    }
    this.variables.addAll(variables);
  }

  public List<Constraint> getConstraints() {
    return constraints;
  }

  public void setConstraints(List<Constraint> constraints) {
    this.constraints = constraints;
  }

  public void addConstraint(Constraint constraint) {
    if (constraint == null) {
      return;
    }
    if (this.constraints == null) {
      this.constraints = new ArrayList<>();
    }
    this.constraints.add(constraint);
  }

  public void addConstraints(Collection<Constraint> constraints) {
    if (constraints == null) {
      return;
    }
    if (this.constraints == null) {
      this.constraints = new ArrayList<>();
    }
    this.constraints.addAll(constraints);
  }

  public Objective getObjective() {
    return objective;
  }

  public void setObjective(Objective objective) {
    this.objective = objective;
  }

  public String print() {
    StringBuilder stringBuilder = new StringBuilder();
    predicates.forEach(p -> p.append(stringBuilder));
    parameters.forEach(p -> p.append(stringBuilder));
    variables.forEach(v -> v.append(stringBuilder));
    constraints.forEach(c -> c.append(stringBuilder));
    objective.append(stringBuilder);
    return stringBuilder.toString();
  }
}
