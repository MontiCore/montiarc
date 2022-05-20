/* (c) https://github.com/MontiCore/monticore */
package montiarc._symboltable;

import arcbasis.ArcBasisMill;
import arcbasis._symboltable.ArcBasisScopesGenitor;
import openmodeautomata._symboltable.OpenModeAutomataScopesGenitor;
import variablearc.VariableArcMill;
import variablearc._symboltable.VariableArcScopesGenitor;

public class MontiArcScopesGenitorDelegator extends MontiArcScopesGenitorDelegatorTOP {

  public MontiArcScopesGenitorDelegator() {
    super();
    this.resetVisitorLists();
    this.initArcBasisGenitor();
    this.initVariableArcGenitor();
  }

  protected void resetVisitorLists() {
    // Visitor lists are not reset indiviudally in the init|Lang|Gentior methods, as different genitors add themselves
    // as visitors for different sublanguages. E.g., both the ArcBasisGenitor and VariableArcGenitor are added as
    // visitors for ArcBasis.
    // Handlers on the other side are reset in the corresponding methods. This is possible, as there can only be one
    // handler and the genitors are not competing for it.
    traverser.getArcBasisVisitorList().clear();
    traverser.getVariableArcVisitorList().clear();
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

  protected void initOpenModeAutomataGenitor() {
    // the mill does not provide the genitor
    OpenModeAutomataScopesGenitor genitor = new OpenModeAutomataScopesGenitor();
    // the genitor does not need to be visited around
    genitor.run();
  }
}