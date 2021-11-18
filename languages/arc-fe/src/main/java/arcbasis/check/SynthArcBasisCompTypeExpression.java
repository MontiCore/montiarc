/* (c) https://github.com/MontiCore/monticore */
package arcbasis.check;

import arcbasis.ArcBasisMill;
import arcbasis._visitor.ArcBasisTraverser;

import java.util.Optional;

/**
 * A composed visitor for ArcBasis that takes component type expressions represented as
 * {@link de.monticore.types.mcbasictypes._ast.ASTMCType} and creates {@link CompTypeExpression}s from them.
 */
public class SynthArcBasisCompTypeExpression implements ISynthesizeCompTypeExpression {

  protected ArcBasisTraverser traverser;
  protected SynthCompTypeResult resultWrapper;

  public SynthArcBasisCompTypeExpression() {
    init();
  }

  @Override
  public void init() {
    this.traverser = ArcBasisMill.traverser();
    this.resultWrapper = new SynthCompTypeResult();

    SynthCompTypeExprFromMCBasicTypes synthFromBasicTypes = new SynthCompTypeExprFromMCBasicTypes(resultWrapper);
    traverser.setMCBasicTypesHandler(synthFromBasicTypes);
  }

  @Override
  public ArcBasisTraverser getTraverser() {
    return traverser;
  }

  @Override
  public Optional<CompTypeExpression> getResult() {
    return resultWrapper.getCurrentResult();
  }
}
