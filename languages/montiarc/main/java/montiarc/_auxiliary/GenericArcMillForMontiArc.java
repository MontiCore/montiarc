/* (c) https://github.com/MontiCore/monticore */
package montiarc._auxiliary;

import de.monticore.types.check.FullCompKindExprDeSer;
import genericarc._symboltable.GenericArcScopesGenitorP2;
import montiarc.check.MontiArcSynthesizeComponent;

public class GenericArcMillForMontiArc extends GenericArcMillForMontiArcTOP {

  @Override
  protected GenericArcScopesGenitorP2 _scopesGenitorP2() {
    return new GenericArcScopesGenitorP2(new MontiArcSynthesizeComponent());
  }

  @Override
  protected FullCompKindExprDeSer _compTypeExprDeSer() {
    return montiarc.MontiArcMill.compTypeExprDeSer();
  }
}
