/* (c) https://github.com/MontiCore/monticore */
package montiarc._symboltable;

import arcbasis.ArcBasisMill;
import arcbasis._symboltable.ArcBasisScopesGenitor;
import de.monticore.prettyprint.IndentPrinter;
import de.monticore.types.prettyprint.MCSimpleGenericTypesFullPrettyPrinter;

public class MontiArcScopesGenitorDelegator extends MontiArcScopesGenitorDelegatorTOP {

  public MontiArcScopesGenitorDelegator() {
    super();
    traverser.getArcBasisVisitorList().clear();
    ArcBasisScopesGenitor arcBasisScopesGenitor = ArcBasisMill.scopesGenitor();
    arcBasisScopesGenitor.setScopeStack(scopeStack);
    arcBasisScopesGenitor.setTypePrinter(new MCSimpleGenericTypesFullPrettyPrinter(new IndentPrinter()));
    traverser.add4ArcBasis(arcBasisScopesGenitor);
    traverser.setArcBasisHandler(arcBasisScopesGenitor);
  }
}