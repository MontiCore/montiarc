/* (c) https://github.com/MontiCore/monticore */
package montiarc.check;

import arcbasis.check.CompSymTypeExpression;
import arcbasis.check.ISynthesizeCompSymTypeExpression;
import arcbasis.check.SynthCompTypeExprFromMCBasicTypes;
import arcbasis.check.SynthCompTypeResult;
import montiarc.MontiArcMill;
import montiarc._visitor.MontiArcTraverser;

import java.util.Optional;

/**
 * A composed visitor for MontiArc that takes component type expressions represented as
 * {@link de.monticore.types.mcbasictypes._ast.ASTMCType} and creates {@link CompSymTypeExpression}s from them.
 */
public class SynthMontiArcCompTypeExpression implements ISynthesizeCompSymTypeExpression {
  protected MontiArcTraverser traverser;
  protected SynthCompTypeResult resultWrapper;

  public SynthMontiArcCompTypeExpression() {
    init();
  }

  @Override
  public void init() {
    this.traverser = MontiArcMill.traverser();
    this.resultWrapper = new SynthCompTypeResult();

    SynthCompTypeExprFromMCBasicTypes synthFromBasicTypes = new SynthCompTypeExprFromMCBasicTypes(resultWrapper);
    SynthCompTypeExprFromMCSimpleGenericTypes synthFromSimpleGenerics =
      new SynthCompTypeExprFromMCSimpleGenericTypes(resultWrapper);

    traverser.setMCBasicTypesHandler(synthFromBasicTypes);
    traverser.setMCSimpleGenericTypesHandler(synthFromSimpleGenerics);
  }

  @Override
  public MontiArcTraverser getTraverser() {
    return this.traverser;
  }

  @Override
  public Optional<CompSymTypeExpression>  getResult() {
    return resultWrapper.getCurrentResult();
  }
}
