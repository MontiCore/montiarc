/* (c) https://github.com/MontiCore/monticore */
package montiarc._auxiliary;

import arcbasis._symboltable.ArcBasisSymbolTableCompleter;
import arcbasis._symboltable.ComponentTypeSymbolBuilder;
import arcbasis.check.deser.ComposedCompTypeExprDeSer;
import montiarc.check.MontiArcSynthesizeComponent;
import montiarc.check.MontiArcTypeCalculator;
import variablearc._symboltable.VariableArcComponentTypeSymbolBuilder;

public class ArcBasisMillForMontiArc extends ArcBasisMillForMontiArcTOP {

  @Override
  protected ArcBasisSymbolTableCompleter _symbolTableCompleter() {
    return new ArcBasisSymbolTableCompleter(new MontiArcSynthesizeComponent(), new MontiArcTypeCalculator());
  }

  @Override
  protected ComposedCompTypeExprDeSer _compTypeExprDeSer() {
    return montiarc.MontiArcMill.compTypeExprDeSer();
  }

  @Override
  protected ComponentTypeSymbolBuilder _componentTypeSymbolBuilder() {
    return new VariableArcComponentTypeSymbolBuilder();
  }
}