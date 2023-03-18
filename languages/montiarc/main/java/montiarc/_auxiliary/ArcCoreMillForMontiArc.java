/* (c) https://github.com/MontiCore/monticore */
package montiarc._auxiliary;

import arcbasis.check.deser.ComposedCompTypeExprDeSer;

public class ArcCoreMillForMontiArc extends ArcCoreMillForMontiArcTOP {

  @Override
  protected ComposedCompTypeExprDeSer _compTypeExprDeSer() {
    return montiarc.MontiArcMill.compTypeExprDeSer();
  }
}
