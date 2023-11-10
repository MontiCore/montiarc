/* (c) https://github.com/MontiCore/monticore */
package variablearc;

import com.microsoft.z3.Context;
import variablearc.evaluation.VariableArcDeriveSMTExpr;
import variablearc.evaluation.exp2smt.IDeriveSMTExpr;

public class VariableArcMill extends VariableArcMillTOP {

  protected static VariableArcMill millVariableArcFullConverter;

  protected static VariableArcMill millVariableArcCompTypeExprDeSer;

  public static IDeriveSMTExpr fullConverter(Context context) {
    if (millVariableArcFullConverter == null) {
      millVariableArcFullConverter = getMill();
    }
    return millVariableArcFullConverter._fullConverter(context);
  }

  public static void initMe(VariableArcMill a) {
    VariableArcMillTOP.initMe(a);
    millVariableArcFullConverter = a;
    millVariableArcCompTypeExprDeSer = a;
  }

  public static void reset() {
    VariableArcMillTOP.reset();
    millVariableArcFullConverter = null;
    millVariableArcCompTypeExprDeSer = null;
  }

  protected IDeriveSMTExpr _fullConverter(Context context) {
    return new VariableArcDeriveSMTExpr(context);
  }
}