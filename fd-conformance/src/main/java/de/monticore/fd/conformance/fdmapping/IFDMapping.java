/* (c) https://github.com/MontiCore/monticore */

package de.monticore.fd.conformance.fdmapping;

import com.microsoft.z3.BoolExpr;
import com.microsoft.z3.Context;
import de.monticore.fd.conformance.fd2smt.SMTFDiagram;
import java.util.List;

public interface IFDMapping {
  List<BoolExpr> map(SMTFDiagram ref, SMTFDiagram con, Context ctx);
}
