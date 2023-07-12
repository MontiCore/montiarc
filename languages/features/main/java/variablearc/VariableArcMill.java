/* (c) https://github.com/MontiCore/monticore */
package variablearc;

import arcbasis.check.deser.ArcBasisCompTypeExprDeSer;
import arcbasis.check.deser.ComposedCompTypeExprDeSer;
import com.microsoft.z3.Context;
import variablearc._symboltable.VariableArcScopesGenitorP2;
import variablearc._symboltable.VariableArcScopesGenitorP2Delegator;
import variablearc.evaluation.VariableArcDeriveSMTExpr;
import variablearc.evaluation.exp2smt.IDeriveSMTExpr;

public class VariableArcMill extends VariableArcMillTOP {

  protected static VariableArcMill millVariableArcScopesGenitorP2;

  protected static VariableArcMill millVariableArcScopesGenitorP2Delegator;

  protected static VariableArcMill millVariableArcFullConverter;

  protected static VariableArcMill millVariableArcCompTypeExprDeSer;

  public static VariableArcScopesGenitorP2 scopesGenitorP2() {
    if (millVariableArcScopesGenitorP2 == null) {
      millVariableArcScopesGenitorP2 = getMill();
    }
    return millVariableArcScopesGenitorP2._scopesGenitorP2();
  }

  public static VariableArcScopesGenitorP2Delegator scopesGenitorP2Delegator() {
    if (millVariableArcScopesGenitorP2Delegator == null) {
      millVariableArcScopesGenitorP2Delegator = getMill();
    }
    return millVariableArcScopesGenitorP2Delegator._scopesGenitorP2Delegator();
  }

  public static IDeriveSMTExpr fullConverter(Context context) {
    if (millVariableArcFullConverter == null) {
      millVariableArcFullConverter = getMill();
    }
    return millVariableArcFullConverter._fullConverter(context);
  }

  public static void initMe(VariableArcMill a) {
    VariableArcMillTOP.initMe(a);
    millVariableArcScopesGenitorP2 = a;
    millVariableArcScopesGenitorP2Delegator = a;
    millVariableArcFullConverter = a;
    millVariableArcCompTypeExprDeSer = a;
  }

  public static void reset() {
    VariableArcMillTOP.reset();
    millVariableArcScopesGenitorP2 = null;
    millVariableArcScopesGenitorP2Delegator = null;
    millVariableArcFullConverter = null;
    millVariableArcCompTypeExprDeSer = null;
  }

  protected VariableArcScopesGenitorP2 _scopesGenitorP2() {
    return new VariableArcScopesGenitorP2();
  }

  protected VariableArcScopesGenitorP2Delegator _scopesGenitorP2Delegator() {
    return new VariableArcScopesGenitorP2Delegator();
  }

  protected IDeriveSMTExpr _fullConverter(Context context) {
    return new VariableArcDeriveSMTExpr(context);
  }
}