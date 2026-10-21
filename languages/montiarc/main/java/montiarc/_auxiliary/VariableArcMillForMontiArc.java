/* (c) https://github.com/MontiCore/monticore */
package montiarc._auxiliary;

import com.microsoft.z3.Context;
import variablearc.presence.PresenceFormulaCalculator;
import variablearc.smt.exp2smt.IDeriveSMTExpr;

public class VariableArcMillForMontiArc extends VariableArcMillForMontiArcTOP {

  @Override
  protected IDeriveSMTExpr _fullConverter(Context context) {
    return montiarc.MontiArcMill.fullConverter(context);
  }

  @Override
  protected PresenceFormulaCalculator _presenceFormulaCalculator() {
    return montiarc.MontiArcMill.presenceFormulaCalculator();
  }
}
