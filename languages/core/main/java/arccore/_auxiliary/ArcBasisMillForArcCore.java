/* (c) https://github.com/MontiCore/monticore */
package arccore._auxiliary;

import arcbasis.check.deser.ComposedCompTypeExprDeSer;
import genericarc.check.GenericArcCompTypeExprDeSer;

public class ArcBasisMillForArcCore extends ArcBasisMillForArcCoreTOP {

  @Override
  protected ComposedCompTypeExprDeSer _compTypeExprDeSer() {
    return new GenericArcCompTypeExprDeSer();
  }
}
