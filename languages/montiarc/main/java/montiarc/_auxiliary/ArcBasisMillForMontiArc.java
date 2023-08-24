/* (c) https://github.com/MontiCore/monticore */
package montiarc._auxiliary;

import arcbasis._symboltable.ArcBasisScopesGenitorP2;
import arcbasis._symboltable.ComponentTypeSymbolBuilder;
import arcbasis.check.deser.ComposedCompTypeExprDeSer;
import montiarc._symboltable.MontiArcComponentTypeSymbolBuilder;
import montiarc.check.MontiArcSynthesizeComponent;
import montiarc.check.MontiArcTypeCalculator;

public class ArcBasisMillForMontiArc extends ArcBasisMillForMontiArcTOP {

  @Override
  protected ArcBasisScopesGenitorP2 _scopesGenitorP2() {
    return new ArcBasisScopesGenitorP2(new MontiArcSynthesizeComponent(), new MontiArcTypeCalculator());
  }

  @Override
  protected ComposedCompTypeExprDeSer _compTypeExprDeSer() {
    return montiarc.MontiArcMill.compTypeExprDeSer();
  }

  @Override
  protected ComponentTypeSymbolBuilder _componentTypeSymbolBuilder() {
    return new MontiArcComponentTypeSymbolBuilder();
  }
}