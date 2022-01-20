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
    ArcBasisScopesGenitor arcBasisScopesGenitor = ArcBasisMill.scopesGenitor();
    arcBasisScopesGenitor.setScopeStack(scopeStack);
    traverser.add4ArcBasis(arcBasisScopesGenitor);
    traverser.setArcBasisHandler(arcBasisScopesGenitor);

    traverser.getVariableArcVisitorList().clear();
    VariableArcScopesGenitor variableArcScopesGenitor = VariableArcMill.scopesGenitor();
    variableArcScopesGenitor.setScopeStack(scopeStack);
    traverser.add4VariableArc(variableArcScopesGenitor);
    traverser.add4ArcBasis(variableArcScopesGenitor);
    traverser.setVariableArcHandler(variableArcScopesGenitor);
  }
}