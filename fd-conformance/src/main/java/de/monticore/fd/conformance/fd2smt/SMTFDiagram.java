/* (c) https://github.com/MontiCore/monticore */

package de.monticore.fd.conformance.fd2smt;

import com.microsoft.z3.*;
import de.monticore.fd.conformance.FDExpression2smt;
import de.monticore.featurediagram._ast.*;
import de.monticore.ocl2smt.ocl2smt.expr2smt.expr2z3.Z3ExprAdapter;
import de.monticore.ocl2smt.ocl2smt.expr2smt.expr2z3.Z3ExprFactory;
import de.monticore.ocl2smt.ocl2smt.expr2smt.expr2z3.Z3TypeFactory;

import java.util.*;
import java.util.function.BiFunction;
import java.util.function.Function;
import java.util.stream.Collectors;

// FIXME: 13.07.2023 allow Name mapping

public class SMTFDiagram {
  private final ASTFDCompilationUnit fd;
  private final Context ctx;
  private final Map<ASTFDElement, BoolExpr> constraints = new HashMap<>();
  private final Map<String, BoolExpr> features = new HashMap<>();
  private final FDExpression2smt expression2smt;

  /***
   * convert a Feature Diagram to SMT.
   * FD: feature-diagram { A -> C & B } ;
   * *
   * 1-declare a boolExpr for each A feature with the interpretation that the bool expression will be evaluated to "true"
   * if the feature is present in the configuration
   * ** (declare-const A ()  BooSort)
   * *
   * 3-transform the rule and constraints as OCL constraints
   * ** A implies B and C
   *
   * @param fd the feature diagram to convert.
   * @param ctx context where the declaration will be make.
   */
  public SMTFDiagram(ASTFDCompilationUnit fd, Context ctx, Function<String, String> ident) {
    this.fd = fd;
    this.ctx = ctx;

    for (String feature : fd.getFeatureDiagram().getAllFeatures()) {
      BoolExpr constr = ctx.mkBoolConst(ident.apply(feature));
      features.put(feature, constr);
    }
    Z3TypeFactory typeFactory = new Z3TypeFactory(ctx);
    Z3ExprFactory eFactory = new Z3ExprFactory(typeFactory,ctx);
    expression2smt = new FDExpression2smt(eFactory,typeFactory,ctx, features);

    // convert a feature element to boolean constraints
    fd.getFeatureDiagram().getFDElementList().forEach(elem -> constraints.put(elem, convert(elem)));
  }

  protected BoolExpr convert(ASTFDElement element) {
    BoolExpr res = null;
    if (element instanceof ASTFeatureTreeRule) {
      res = convert((ASTFeatureTreeRule) element);
    } else if (element instanceof ASTFeatureConstraint) {
      res = expression2smt.convertBoolExpr(((ASTFeatureConstraint) element).getConstraint());
    }
    return res;
  }

  /***
   * convert a feature rule to smt.
   * eg:
   * FD: A -> B & C & D?
   * SMT:  (implies A (and B  C))
   *       (implies B A) and (implies C A) and (implies D A)
   *  @param rule the rule to convert.
   * @return the conversion as boolExpr.
   */
  protected BoolExpr convert(ASTFeatureTreeRule rule) {
    Set<BoolExpr> res = new HashSet<>();
    String parent = rule.getName();

    // rule like A -> B? have no influence
    if (!collectNonOptionalGroupParts(rule.getFeatureGroup()).isEmpty()) {
      res.add(ctx.mkImplies(features.get(parent), convert(rule.getFeatureGroup())));
    }

    // make sure child is present iff parent is present
    for (String child : collectParts(rule.getFeatureGroup())) {
      res.add(ctx.mkImplies(features.get(child), features.get(parent)));
    }
    return ctx.mkAnd(res.toArray(new BoolExpr[0]));
  }

  protected BoolExpr convert(ASTFeatureGroup group) {
    BoolExpr res = null;
    if (group instanceof ASTAndGroup) {
      res = convert((ASTAndGroup) group);
    } else if (group instanceof ASTOrGroup) {
      res = convert((ASTOrGroup) group);
    } else if (group instanceof ASTXorGroup) {
      res = convert((ASTXorGroup) group);
    }
    return res;
  }

  /***
   * transform a andGroup to smt.
   *FD: A & B
   *SMT: (and A B)
   * @param andGroup the andGroup to convert.
   * @return conversion as boolExpr.
   */
  protected BoolExpr convert(ASTAndGroup andGroup) {
    return convert(andGroup, ctx::mkAnd);
  }

  /***
   * transform a xorGroup to smt.
   *FD: A | B
   *SMT:  (or A B)
   * @param orGroup the orGroup to convert.
   * @return conversion as boolExpr.
   */
  protected BoolExpr convert(ASTOrGroup orGroup) {
    return convert(orGroup, ctx::mkOr);
  }

  /***
   * transform a xorGroup to smt.
   *FD: A ^ B
   *SMT: (xor A B)
   * @param xorGroup the xorGroup to convert.
   * @return conversion as boolExpr.
   */
  protected BoolExpr convert(ASTXorGroup xorGroup) {
    return convert(xorGroup, ctx::mkXor);
  }

  protected BoolExpr convert(
      ASTFeatureGroup group, BiFunction<BoolExpr, BoolExpr, BoolExpr> operation) {
    List<String> parts = collectNonOptionalGroupParts(group);

    BoolExpr res = getFeature(parts.get(0));
    for (int i = 1; i < parts.size(); i++) {
      res = operation.apply(res, features.get(parts.get(i)));
    }
    return res;
  }

  protected List<String> collectNonOptionalGroupParts(ASTFeatureGroup group) {
    return group.getGroupPartList().stream()
        .filter(part -> !part.isOptional())
        .map(ASTGroupPart::getName)
        .collect(Collectors.toList());
  }

  protected List<String> collectParts(ASTFeatureGroup group) {
    return group.getGroupPartList().stream()
        .map(ASTGroupPart::getName)
        .collect(Collectors.toList());
  }

  public BoolExpr getFDConstraint() {
    return ctx.mkAnd(constraints.values().toArray(new BoolExpr[0]));
  }

  public ASTFDCompilationUnit getFeatureDiagram() {
    return fd;
  }

  public Map<String, BoolExpr> getAllFeatures() {
    return features;
  }

  public BoolExpr getFeature(String feature) {
    return features.getOrDefault(feature, null);
  }
}
