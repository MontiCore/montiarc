/* (c) https://github.com/MontiCore/monticore */
package montiarc;

import com.microsoft.z3.Context;
import de.monticore.types.check.FullCompKindExprDeSer;
import montiarc._symboltable.MontiArcScopesGenitorP2;
import montiarc._symboltable.MontiArcScopesGenitorP2Delegator;
import montiarc._symboltable.MontiArcScopesGenitorP3Delegator;
import montiarc.check.MontiArcCompTypeExprDeSer;
import montiarc.evaluation.MontiArcDeriveSMTExpr;
import variablearc.evaluation.exp2smt.IDeriveSMTExpr;

public class MontiArcMill extends MontiArcMillTOP {

  protected static MontiArcMill millMontiArcScopesGenitorP2;

  protected static MontiArcMill millMontiArcScopesGenitorP2Delegator;

  protected static MontiArcMill millMontiArcScopesGenitorP3Delegator;

  protected static MontiArcMill millMontiArcFullConverter;

  protected static MontiArcMill millMontiArcCompTypeExprDeSer;

  public static MontiArcScopesGenitorP2 scopesGenitorP2() {
    if (millMontiArcScopesGenitorP2 == null) {
      millMontiArcScopesGenitorP2 = getMill();
    }
    return millMontiArcScopesGenitorP2._scopesGenitorP2();
  }

  protected MontiArcScopesGenitorP2 _scopesGenitorP2() {
    return new MontiArcScopesGenitorP2();
  }

  public static MontiArcScopesGenitorP2Delegator scopesGenitorP2Delegator() {
    if (millMontiArcScopesGenitorP2Delegator == null) {
      millMontiArcScopesGenitorP2Delegator = getMill();
    }
    return millMontiArcScopesGenitorP2Delegator._scopesGenitorP2Delegator();
  }

  protected MontiArcScopesGenitorP2Delegator _scopesGenitorP2Delegator() {
    return new MontiArcScopesGenitorP2Delegator();
  }

  public static MontiArcScopesGenitorP3Delegator scopesGenitorP3Delegator() {
    if (millMontiArcScopesGenitorP3Delegator == null) {
      millMontiArcScopesGenitorP3Delegator = getMill();
    }
    return millMontiArcScopesGenitorP3Delegator._scopesGenitorP3Delegator();
  }

  protected MontiArcScopesGenitorP3Delegator _scopesGenitorP3Delegator() {
    return new MontiArcScopesGenitorP3Delegator();
  }

  public static IDeriveSMTExpr fullConverter(Context context) {
    if (millMontiArcFullConverter == null) {
      millMontiArcFullConverter = getMill();
    }
    return millMontiArcFullConverter._fullConverter(context);
  }

  protected IDeriveSMTExpr _fullConverter(Context context) {
    return new MontiArcDeriveSMTExpr(context);
  }

  public static FullCompKindExprDeSer compTypeExprDeSer() {
    if (millMontiArcCompTypeExprDeSer == null) {
      millMontiArcCompTypeExprDeSer = getMill();
    }
    return millMontiArcCompTypeExprDeSer._compTypeExprDeSer();
  }

  protected FullCompKindExprDeSer _compTypeExprDeSer() {
    return new MontiArcCompTypeExprDeSer();
  }

  public static void initMe(MontiArcMill a) {
    MontiArcMillTOP.initMe(a);
    millMontiArcScopesGenitorP2 = a;
    millMontiArcScopesGenitorP2Delegator = a;
    millMontiArcScopesGenitorP3Delegator = a;
    millMontiArcFullConverter = a;
  }

  public static void reset() {
    MontiArcMillTOP.reset();
    millMontiArcScopesGenitorP2 = null;
    millMontiArcScopesGenitorP2Delegator = null;
    millMontiArcScopesGenitorP3Delegator = null;
    millMontiArcFullConverter = null;
  }
}