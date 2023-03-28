/* (c) https://github.com/MontiCore/monticore */
package montiarc._symboltable;

import arcbasis.ArcBasisMill;
import arcbasis._symboltable.ArcBasisScopesGenitor;
import variablearc.VariableArcMill;
import variablearc._symboltable.VariableArcScopesGenitor;

public class MontiArcScopesGenitorDelegator extends MontiArcScopesGenitorDelegatorTOP {

  public MontiArcScopesGenitorDelegator() {
    super();
    traverser.getArcBasisVisitorList().clear();
    traverser.getVariableArcVisitorList().clear();
    this.initArcBasisGenitor();
    this.initVariableArcGenitor();
  }

  protected void initArcBasisGenitor() {
    ArcBasisScopesGenitor genitor = ArcBasisMill.scopesGenitor();
    genitor.setScopeStack(scopeStack);

    traverser.setArcBasisHandler(genitor);
    traverser.add4ArcBasis(genitor);
  }

  protected void initVariableArcGenitor() {
    VariableArcScopesGenitor genitor = VariableArcMill.scopesGenitor();
    genitor.setScopeStack(scopeStack);

    traverser.setVariableArcHandler(genitor);
    traverser.add4VariableArc(genitor);
    traverser.add4ArcBasis(genitor);
  }
}