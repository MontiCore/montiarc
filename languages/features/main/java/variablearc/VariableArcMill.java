/* (c) https://github.com/MontiCore/monticore */
package variablearc;

import com.microsoft.z3.Context;
import variablearc.presence.PresenceFormulaCalculator;
import variablearc.smt.VariableArcDeriveSMTExpr;
import variablearc.smt.exp2smt.IDeriveSMTExpr;

public class VariableArcMill extends VariableArcMillTOP {

  protected static VariableArcMill millVariableArcFullConverter;

  protected static VariableArcMill millVariableArcCompTypeExprDeSer;

  protected static VariableArcMill millVariableArcPresenceFormulaCalculator;

  public static IDeriveSMTExpr fullConverter(Context context) {
    if (millVariableArcFullConverter == null) {
      millVariableArcFullConverter = getMill();
    }
    return millVariableArcFullConverter._fullConverter(context);
  }

  public static PresenceFormulaCalculator presenceFormulaCalculator() {
    if (millVariableArcPresenceFormulaCalculator == null) {
      millVariableArcPresenceFormulaCalculator = getMill();
    }
    return millVariableArcPresenceFormulaCalculator._presenceFormulaCalculator();
  }

  public static void initMe(VariableArcMill a) {
    VariableArcMillTOP.initMe(a);
    millVariableArcFullConverter = a;
    millVariableArcCompTypeExprDeSer = a;
    millVariableArcPresenceFormulaCalculator = a;
  }

  public static void reset() {
    if (mill != null && mill.variableArcGlobalScope != null) {
      mill.variableArcGlobalScope.closePresenceContext();
    }
    VariableArcMillTOP.reset();
    millVariableArcFullConverter = null;
    millVariableArcCompTypeExprDeSer = null;
    millVariableArcPresenceFormulaCalculator = null;
  }

  protected IDeriveSMTExpr _fullConverter(Context context) {
    return new VariableArcDeriveSMTExpr(context);
  }

  protected PresenceFormulaCalculator _presenceFormulaCalculator() {
    return new PresenceFormulaCalculator();
  }
}
