/* (c) https://github.com/MontiCore/monticore */
package montiarc.check;

import arcbasis.check.CompTypeExpression;
import arcbasis.check.ISynthesizeComponent;
import arcbasis.check.SynthesizeComponentFromMCBasicTypes;
import arcbasis.check.SynthCompTypeResult;
import montiarc.MontiArcMill;
import montiarc._visitor.MontiArcTraverser;
import variablearc.check.SynthesizeVariableComponentFromMCBasicTypes;

import java.util.Optional;

/**
 * A composed visitor for MontiArc that takes component type expressions represented as
 * {@link de.monticore.types.mcbasictypes._ast.ASTMCType} and creates {@link CompTypeExpression}s from them.
 */
public class MontiArcSynthesizeComponent implements ISynthesizeComponent {
  protected MontiArcTraverser traverser;
  protected SynthCompTypeResult resultWrapper;

  public MontiArcSynthesizeComponent() {
    init();
  }

  @Override
  public void init() {
    this.traverser = MontiArcMill.traverser();
    this.resultWrapper = new SynthCompTypeResult();

    SynthesizeComponentFromMCBasicTypes synthFromBasicTypes = new SynthesizeVariableComponentFromMCBasicTypes(resultWrapper);
    SynthesizeComponentFromMCSimpleGenericTypes synthFromSimpleGenerics =
      new SynthesizeComponentFromMCSimpleGenericTypes(resultWrapper);

    traverser.setMCBasicTypesHandler(synthFromBasicTypes);
    traverser.setMCSimpleGenericTypesHandler(synthFromSimpleGenerics);
  }

  @Override
  public MontiArcTraverser getTraverser() {
    return this.traverser;
  }

  @Override
  public Optional<CompTypeExpression>  getResult() {
    return resultWrapper.getResult();
  }
}
