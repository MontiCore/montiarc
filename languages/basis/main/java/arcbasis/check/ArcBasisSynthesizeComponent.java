/* (c) https://github.com/MontiCore/monticore */
package arcbasis.check;

import arcbasis.ArcBasisMill;
import arcbasis._visitor.ArcBasisTraverser;
import de.monticore.types.check.CompKindExpression;

import java.util.Optional;

/**
 * A composed visitor for ArcBasis that takes component type expressions represented as
 * {@link de.monticore.types.mcbasictypes._ast.ASTMCType} and creates {@link CompTypeExpression}s from them.
 */
public class ArcBasisSynthesizeComponent implements ISynthesizeComponent {

  protected ArcBasisTraverser traverser;
  protected SynthCompTypeResult resultWrapper;

  public ArcBasisSynthesizeComponent() {
    init();
  }

  @Override
  public void init() {
    this.traverser = ArcBasisMill.traverser();
    this.resultWrapper = new SynthCompTypeResult();

    SynthesizeComponentFromMCBasicTypes synthFromBasicTypes = new SynthesizeComponentFromMCBasicTypes(resultWrapper);
    traverser.setMCBasicTypesHandler(synthFromBasicTypes);
  }

  @Override
  public ArcBasisTraverser getTraverser() {
    return traverser;
  }

  @Override
  public Optional<CompKindExpression> getResult() {
    return resultWrapper.getResult();
  }
}
