/* (c) https://github.com/MontiCore/monticore */
package montiarc._auxiliary;

import arcbasis._visitor.IFullPrettyPrinter;
import arcbasis.check.deser.ComposedCompTypeExprDeSer;

public class ArcCoreMillForMontiArc extends ArcCoreMillForMontiArcTOP {

  @Override
  protected IFullPrettyPrinter _fullPrettyPrinter() {
    return montiarc.MontiArcMill.fullPrettyPrinter();
  }

  @Override
  protected ComposedCompTypeExprDeSer _millCompTypeExprDeSer() {
    return montiarc.MontiArcMill.millCompTypeExprDeSer();
  }
}
