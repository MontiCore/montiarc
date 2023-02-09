/* (c) https://github.com/MontiCore/monticore */
package montiarc._auxiliary;

import arcbasis._visitor.IFullPrettyPrinter;
import arcbasis.check.deser.ComposedCompTypeExprDeSer;
import com.microsoft.z3.Context;
import variablearc.evaluation.exp2smt.IDeriveSMTExpr;

public class VariableArcMillForMontiArc extends VariableArcMillForMontiArcTOP {

  @Override
  protected IFullPrettyPrinter _fullPrettyPrinter() {
    return montiarc.MontiArcMill.fullPrettyPrinter();
  }

  @Override
  protected IDeriveSMTExpr _fullConverter(Context context) {
    return montiarc.MontiArcMill.fullConverter(context);
  }

  @Override
  protected ComposedCompTypeExprDeSer _compTypeExprDeSer() {
    return montiarc.MontiArcMill.compTypeExprDeSer();
  }
}
