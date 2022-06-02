/* (c) https://github.com/MontiCore/monticore */
package montiarc._auxiliary;

import arcbasis._symboltable.ArcBasisSymbolTableCompleter;
import de.monticore.types.mcsimplegenerictypes.MCSimpleGenericTypesMill;
import montiarc.check.MontiArcSynthesizeComponent;
import montiarc.check.MontiArcTypeCalculator;

public class ArcBasisMillForMontiArc extends ArcBasisMillForMontiArcTOP {

  @Override
  protected ArcBasisSymbolTableCompleter _symbolTableCompleter() {
    return new ArcBasisSymbolTableCompleter(MCSimpleGenericTypesMill.mcSimpleGenericTypesPrettyPrinter(),
      new MontiArcSynthesizeComponent(), new MontiArcTypeCalculator());
  }
}