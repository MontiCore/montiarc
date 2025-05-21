/* (c) https://github.com/MontiCore/monticore */
package arcbasis.check;

import arcbasis.ArcBasisMill;
import arcbasis._visitor.ArcBasisTraverser;
import de.monticore.types.check.CompKindCheckResult;
import de.monticore.types.check.CompKindExpression;
import de.monticore.types.check.FullSynthesizeFromMCSimpleGenericTypes;
import de.monticore.types.check.ISynthesizeComponent;
import de.monticore.types.check.SynthesizeCompKindFromMCBasicTypes;
import de.monticore.types.check.SynthesizeCompKindFromMCSimpleGenericTypes;
import de.monticore.types.mcbasictypes._visitor.MCBasicTypesTraverser;
import de.monticore.types.mcsimplegenerictypes.MCSimpleGenericTypesMill;
import de.monticore.types.mcsimplegenerictypes._visitor.MCSimpleGenericTypesTraverser;

import java.util.Optional;

/**
 * A composed visitor for ArcBasis that takes component type expressions represented as
 * {@link de.monticore.types.mcbasictypes._ast.ASTMCType} and creates {@link CompTypeExpression}s from them.
 */
public class ArcBasisSynthesizeComponent implements ISynthesizeComponent {

  protected MCSimpleGenericTypesTraverser traverser;

  protected CompKindCheckResult resultWrapper;

  @Override
  public void init() {
    this.traverser = MCSimpleGenericTypesMill.traverser();
    this.resultWrapper = new CompKindCheckResult();
    SynthesizeComponentFromMCBasicTypes synFromBasic = new SynthesizeComponentFromMCBasicTypes(resultWrapper);
    SynthesizeComponentFromMCSimpleGenericTypes synFromSimple = new SynthesizeComponentFromMCSimpleGenericTypes(resultWrapper);

    traverser.setMCSimpleGenericTypesHandler(synFromSimple);
    traverser.setMCBasicTypesHandler(synFromBasic);
  }

  @Override
  public MCBasicTypesTraverser getTraverser() {
    return traverser;
  }

  @Override
  public Optional<CompKindExpression> getResult() {
    return resultWrapper.getResult();
  }
}
