/* (c) https://github.com/MontiCore/monticore */
package montiarc._auxiliary;

import arcbasis._visitor.IFullPrettyPrinter;
import genericarc._symboltable.GenericArcSymbolTableCompleter;
import montiarc.check.MontiArcSynthesizeComponent;
import montiarc.check.MontiArcTypeCalculator;

public class GenericArcMillForMontiArc extends GenericArcMillForMontiArcTOP {

  @Override
  protected IFullPrettyPrinter _fullPrettyPrinter() {
    return montiarc.MontiArcMill.fullPrettyPrinter();
  }

  @Override
  protected GenericArcSymbolTableCompleter _symbolTableCompleter() {
    return new GenericArcSymbolTableCompleter(
      montiarc.MontiArcMill.fullPrettyPrinter(),
      new MontiArcSynthesizeComponent(),
      new MontiArcTypeCalculator()
    );
  }
}
