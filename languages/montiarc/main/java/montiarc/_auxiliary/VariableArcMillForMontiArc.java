/* (c) https://github.com/MontiCore/monticore */
package montiarc._auxiliary;

import arcbasis.check.deser.ComposedCompTypeExprDeSer;
import com.microsoft.z3.Context;
import variablearc.evaluation.exp2smt.IDeriveSMTExpr;

public class VariableArcMillForMontiArc extends VariableArcMillForMontiArcTOP {

  @Override
  protected IDeriveSMTExpr _fullConverter(Context context) {
    return montiarc.MontiArcMill.fullConverter(context);
  }
}
