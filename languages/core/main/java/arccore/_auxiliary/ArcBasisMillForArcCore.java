/* (c) https://github.com/MontiCore/monticore */
package arccore._auxiliary;

import de.monticore.types.check.FullCompKindExprDeSer;
import genericarc.check.GenericArcCompTypeExprDeSer;

public class ArcBasisMillForArcCore extends ArcBasisMillForArcCoreTOP {

  @Override
  protected FullCompKindExprDeSer _compTypeExprDeSer() {
    return new GenericArcCompTypeExprDeSer();
  }
}
