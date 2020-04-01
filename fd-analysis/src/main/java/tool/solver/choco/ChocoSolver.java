package tool.solver.choco;

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

  public ChocoSolver() {
    super();
    settings = new FznSettings();
    defineSettings(settings);
  }

  static Map<String, Boolean> transformModelToASTConfiguration(Model model, List<String> features) {
    Map<String, Boolean> config = new HashMap<>();

    Arrays.stream(model.getVars())
            .filter(variable -> features.contains(variable.getName()))
            .filter(v -> v instanceof IntVar)
            .map(v -> ((IntVar) v))
            .map(ChocoSolver::map)
            .forEach(vars -> config.putAll(vars));
    return config;
  }

  static Map<String , Boolean> map(IntVar chocoVariable) {
    Map<String, Boolean> ret = new HashMap<>();
    switch (chocoVariable.getValue()){
      case 0: ret.put(chocoVariable.getName(), false);
      case 1: ret.put(chocoVariable.getName(), true);
    }
    return ret;
  }

  @Override
  public List<Map<String, Boolean>> solve(String model, List<String> features, Boolean allSolutions) {
    List<Map<String, Boolean>> ret = new ArrayList<>();
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
    } else {
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