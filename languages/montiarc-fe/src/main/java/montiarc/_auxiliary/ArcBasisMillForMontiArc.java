/* (c) https://github.com/MontiCore/monticore */
package montiarc._auxiliary;

import arcbasis._symboltable.ArcBasisScopesGenitor;
import arcbasis._symboltable.ArcBasisSymbolTableCompleter;
import de.monticore.types.mcsimplegenerictypes.MCSimpleGenericTypesMill;
import montiarc.check.MontiArcSynthesizeComponent;

public class ArcBasisMillForMontiArc extends ArcBasisMillForMontiArcTOP {

  @Override
  protected ArcBasisScopesGenitor _scopesGenitor() {
    return new ArcBasisScopesGenitor(MCSimpleGenericTypesMill.mcSimpleGenericTypesPrettyPrinter());
  }

  @Override
  protected ArcBasisSymbolTableCompleter _symbolTableCompleter() {
    return new ArcBasisSymbolTableCompleter(MCSimpleGenericTypesMill.mcSimpleGenericTypesPrettyPrinter(),
      new MontiArcSynthesizeComponent());
  }
}