/* (c) https://github.com/MontiCore/monticore */
package montiarc._auxiliary;

import arcbasis._symboltable.ArcBasisSymbolTableCompleter;
import arcbasis._visitor.IFullPrettyPrinter;
import de.monticore.types.mcsimplegenerictypes.MCSimpleGenericTypesMill;
import montiarc.check.MontiArcSynthesizeComponent;
import montiarc.check.MontiArcTypeCalculator;

public class ArcBasisMillForMontiArc extends ArcBasisMillForMontiArcTOP {

  @Override
  protected IFullPrettyPrinter _fullPrettyPrinter() {
    return montiarc.MontiArcMill.fullPrettyPrinter();
  }

  @Override
  protected ArcBasisSymbolTableCompleter _symbolTableCompleter() {
    return new ArcBasisSymbolTableCompleter(montiarc.MontiArcMill.fullPrettyPrinter(),
      new MontiArcSynthesizeComponent(), new MontiArcTypeCalculator());
  }
}