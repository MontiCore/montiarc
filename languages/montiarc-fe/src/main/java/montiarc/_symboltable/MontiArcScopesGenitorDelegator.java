/* (c) https://github.com/MontiCore/monticore */
package montiarc._symboltable;

import arcbasis.ArcBasisMill;
import arcbasis._symboltable.ArcBasisScopesGenitor;

public class MontiArcScopesGenitorDelegator extends MontiArcScopesGenitorDelegatorTOP {

  public MontiArcScopesGenitorDelegator() {
    super();
    traverser.getArcBasisVisitorList().clear();
    ArcBasisScopesGenitor arcBasisScopesGenitor = ArcBasisMill.scopesGenitor();
    arcBasisScopesGenitor.setScopeStack(scopeStack);
    traverser.add4ArcBasis(arcBasisScopesGenitor);
    traverser.setArcBasisHandler(arcBasisScopesGenitor);
  }
}