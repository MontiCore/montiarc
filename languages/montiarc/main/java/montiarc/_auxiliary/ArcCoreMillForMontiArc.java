/* (c) https://github.com/MontiCore/monticore */
package montiarc._auxiliary;


import de.monticore.types.check.FullCompKindExprDeSer;

public class ArcCoreMillForMontiArc extends ArcCoreMillForMontiArcTOP {

  @Override
  protected FullCompKindExprDeSer _compTypeExprDeSer() {
    return montiarc.MontiArcMill.compTypeExprDeSer();
  }
}
