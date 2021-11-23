/* (c) https://github.com/MontiCore/monticore */
package arcbasis.check;

import arcbasis.ArcBasisMill;
import arcbasis._visitor.ArcBasisTraverser;
import de.monticore.types.check.ISynthesize;
import de.monticore.types.check.SymTypeExpression;
import de.monticore.types.check.SynthesizeSymTypeFromMCBasicTypes;
import de.monticore.types.check.TypeCheckResult;
import de.monticore.types.mcbasictypes._visitor.MCBasicTypesTraverser;

import java.util.Optional;

public class ArcBasisSynthesizeType implements ISynthesize {
  protected ArcBasisTraverser traverser;
  protected TypeCheckResult typeCheckResult;
  
  public ArcBasisSynthesizeType() {
    init();
  }
  
  @Override
  public Optional<SymTypeExpression> getResult() {
    return Optional.empty();
  }
  
  @Override
  public void init() {
    traverser = ArcBasisMill.traverser();
    typeCheckResult = new TypeCheckResult();
    SynthesizeSymTypeFromMCBasicTypes mCBasicTypesVisitor = new SynthesizeSymTypeFromMCBasicTypes();
    mCBasicTypesVisitor.setTypeCheckResult(typeCheckResult);
    traverser.add4MCBasicTypes(mCBasicTypesVisitor);
    traverser.setMCBasicTypesHandler(mCBasicTypesVisitor);
  }
  
  @Override
  public MCBasicTypesTraverser getTraverser() {
    return traverser;
  }
}