/* (c) https://github.com/MontiCore/monticore */
package tool.solver.choco;

import de.monticore.featureconfiguration._ast.ASTFeatureConfiguration;
import de.monticore.featureconfiguration._ast.ASTFeatureConfigurationBuilder;
import de.monticore.featureconfigurationpartial.FeatureConfigurationPartialMill;
import de.monticore.featureconfigurationpartial._ast.ASTSelectBuilder;
import de.monticore.featureconfigurationpartial._ast.ASTUnselectBuilder;
import org.apache.commons.io.IOUtils;
import org.chocosolver.parser.flatzinc.Flatzinc;
import org.chocosolver.parser.flatzinc.FznSettings;
import org.chocosolver.parser.flatzinc.ast.Datas;
import org.chocosolver.solver.Model;
import org.chocosolver.solver.variables.IntVar;
import tool.solver.ISolver;

import java.util.*;

public class ChocoSolver extends Flatzinc implements ISolver {
  private FznSettings settings;

  private String featureModelName;

  public ChocoSolver() {
    super();
    settings = new FznSettings();
    defineSettings(settings);
  }

  @Override
  public void setFeatureDiagrammName(String name) {
    featureModelName = name;
  }

  ASTFeatureConfiguration transformModelToASTConfiguration(Model model, List<String> features) {
    ASTFeatureConfigurationBuilder builder = FeatureConfigurationPartialMill
        .featureConfigurationBuilder();
    ASTSelectBuilder selectBuilder = FeatureConfigurationPartialMill.selectBuilder();
    ASTUnselectBuilder unselectBuilder = FeatureConfigurationPartialMill.unselectBuilder();

    Arrays.stream(model.getVars())
        .filter(variable -> features.contains(variable.getName()))
        .filter(v -> v instanceof IntVar)
        .map(v -> ((IntVar) v))
        .map(ChocoSolver::map)
        .forEach(stringBooleanMap -> {
          stringBooleanMap.forEach((name, value) -> {
            if (value) {
              selectBuilder.addName(name);
            }
            else {
              unselectBuilder.addName(name);
            }
          });
        });
    return builder
        .setFdName(featureModelName)
        .setName("Analysis")
        .addFCElement(selectBuilder.build())
        .addFCElement(unselectBuilder.build())
        .build();
  }

  static Map<String, Boolean> map(IntVar chocoVariable) {
    Map<String, Boolean> ret = new HashMap<>();
    switch (chocoVariable.getValue()) {
      case 0:
        ret.put(chocoVariable.getName(), false);
        break;
      case 1:
        ret.put(chocoVariable.getName(), true);
    }
    return ret;
  }

  @Override
  public List<ASTFeatureConfiguration> solve(String model, List<String> features,
      Boolean allSolutions) {
    List<ASTFeatureConfiguration> ret = new ArrayList<>();
    portfolio.addModel(new Model());
    Model m = getModel();
    m.set(settings);
    if (datas == null || datas.length == 0) {
      datas = new Datas[1];
      datas[0] = new Datas();
    }
    Datas d = datas[0];
    parse(this.getModel(), d, IOUtils.toInputStream(model));

    if (allSolutions) {
      while (m.getSolver().solve()) {
        ret.add(transformModelToASTConfiguration(m, features));
      }
    }
    else {
      if (m.getSolver().solve()) {
        ret.add(transformModelToASTConfiguration(m, features));
      }
    }
    return ret;
  }

  @Override
  public Thread actionOnKill() {
    return new Thread();
  }
}