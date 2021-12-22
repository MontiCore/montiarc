/* (c) https://github.com/MontiCore/monticore */
package montiarc._auxiliary;

import de.monticore.types.mcsimplegenerictypes.MCSimpleGenericTypesMill;
import genericarc._symboltable.GenericArcSymbolTableCompleter;
import montiarc.check.MontiArcSynthesizeComponent;
import montiarc.check.MontiArcSynthesizeType;

public class GenericArcMillForMontiArc extends GenericArcMillForMontiArcTOP {

  @Override
  protected GenericArcSymbolTableCompleter _symbolTableCompleter() {
    return new GenericArcSymbolTableCompleter(
      MCSimpleGenericTypesMill.mcSimpleGenericTypesPrettyPrinter(),
      new MontiArcSynthesizeComponent(),
      new MontiArcSynthesizeType()
    );
  }
}
