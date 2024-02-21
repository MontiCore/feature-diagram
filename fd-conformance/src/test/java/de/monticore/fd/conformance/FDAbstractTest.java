package de.monticore.fd.conformance;

import com.microsoft.z3.BoolExpr;
import com.microsoft.z3.Context;
import com.microsoft.z3.Model;
import com.microsoft.z3.enumerations.Z3_lbool;
import de.monticore.fd.conformance.fd2smt.SMTFDiagram;
import de.monticore.featurediagram.FeatureDiagramMill;
import de.se_rwth.commons.logging.Log;
import java.util.*;
import org.junit.jupiter.api.BeforeEach;

public abstract class FDAbstractTest {
  @BeforeEach
  public void setup() {
    Log.init();
    FeatureDiagramMill.init();
    FeatureDiagramMill.globalScope();
  }

  public static final String RELATIVE_MODEL_PATH = "src/test/resources/de/monticore/conformance/";

  protected static Context buildContext() {
    Map<String, String> cfg = new HashMap<>();
    cfg.put("model", "true");
    return new Context(cfg);
  }

  protected Set<String> getFeatureConfiguration(SMTFDiagram smtfDiagram, Model model) {

    Set<String> res = new HashSet<>();
    for (String feature : smtfDiagram.getFeatureDiagram().getFeatureDiagram().getAllFeatures()) {
      BoolExpr expr = (BoolExpr) model.eval(smtfDiagram.getFeature(feature), true);
      if (expr.getBoolValue().equals(Z3_lbool.Z3_L_TRUE)) {
        res.add(feature);
      }
    }
    return res;
  }
}
