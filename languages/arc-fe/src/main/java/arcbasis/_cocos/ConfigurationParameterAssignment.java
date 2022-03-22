/* (c) https://github.com/MontiCore/monticore */
package arcbasis._cocos;

import arcbasis._ast.ASTArcParameter;
import arcbasis._ast.ASTComponentInstance;
import arcbasis.check.ArcBasisTypeCalculator;
import arcbasis.check.CompTypeExpression;
import arcbasis.check.IArcTypeCalculator;
import arcbasis.util.ArcError;
import com.google.common.base.Preconditions;
import de.monticore.expressions.expressionsbasis._ast.ASTExpression;
import de.monticore.symbols.basicsymbols._symboltable.VariableSymbol;
import de.monticore.symboltable.ISymbol;
import de.monticore.types.check.IDerive;
import de.monticore.types.check.SymTypeExpression;
import de.monticore.types.check.TypeCheck;
import de.monticore.types.check.TypeCheckResult;
import de.se_rwth.commons.SourcePosition;
import de.se_rwth.commons.logging.Log;
import org.codehaus.commons.nullanalysis.NotNull;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Checks coco R10 of [Hab16]: If a configurable component is instantiated as a subcomponent, all configuration
 * parameters have to be assigned. This coco is extended by coco MR1 of [Wor16]: Arguments of configuration parameters
 * with default values may be omitted during subcomponent declaration.
 * <p>
 * In short: * [binding]: When a component type is instantiated, all it's configuration parameters without specified
 * default values must be bound with values or expressions. When default values are overwritten, then in order of their
 * appearance in the components signature. * [typecheck]: The configuration parameters must be assignable from the
 * specified value bindings.
 * <p>
 * This coco must be checked AFTER symbol table creation! This coco can be reused by grammars that extend ArcBasis and
 * introduce new expressions by calling a constructor with a configured {@link TypeCheck} or an {@link IDerive} to use.
 */
public class ConfigurationParameterAssignment implements ArcBasisASTComponentInstanceCoCo {

  /**
   * Used to extract the type to which instantiation arguments evaluate to.
   */
  protected final IArcTypeCalculator typeCalculator;

  /**
   * Creates this coco with an {@link ArcBasisTypeCalculator}.
   *
   * @see #ConfigurationParameterAssignment(IArcTypeCalculator)
   */
  public ConfigurationParameterAssignment() {
    this(new ArcBasisTypeCalculator());
  }

  /**
   * Creates this coco with a custom {@link IArcTypeCalculator} to extract the types to which instantiation arguments evaluate to.
   */
  public ConfigurationParameterAssignment(@NotNull IArcTypeCalculator typeCalculator) {
    this.typeCalculator = Preconditions.checkNotNull(typeCalculator);
  }

  /**
   * If astInst is linked to a symbol then this method checks that the arguments of
   * {@code astInst.getSymbol().getArguments()} are equal to {@code astInst.getArguments().getExpressionList(}
   */
  protected static void assertConsistentArguments(@NotNull ASTComponentInstance astInst) {
    Preconditions.checkNotNull(astInst);
    if (astInst.isPresentSymbol()) {

      Preconditions.checkArgument(
        astInst.getSymbol().getArguments().isEmpty()
          == (!astInst.isPresentArguments() || astInst.getArguments().getExpressionList().isEmpty())
      );

      if (!astInst.getSymbol().getArguments().isEmpty()) {
        Preconditions.checkArgument(astInst.getSymbol().getArguments().size()
          == astInst.getArguments().getExpressionList().size()
        );
      }

    }
  }

  protected IArcTypeCalculator getTypeCalculator() {
    return this.typeCalculator;
  }

  @Override
  public void check(@NotNull ASTComponentInstance astInst) {
    Preconditions.checkNotNull(astInst);
    Preconditions.checkArgument(astInst.isPresentSymbol(), "Could not perform coco check '%s'. Perhaps you missed the " +
      "symbol table creation.", this.getClass().getSimpleName());

    // Check CoCos
    checkInstantiationArgsAreNotTooMany(astInst);
    checkInstantiationArgsBindAllMandatoryParams(astInst);
    checkInstantiationArgsHaveCorrectTypes(astInst);
  }

  /**
   * Checks that there are not more instantiation arguments provided than there are configuration parameters in the
   * component type that should be instantiated.
   *
   * @param instance The AST node of the component instance whose instantiation arguments should be checked.
   */
  protected void checkInstantiationArgsAreNotTooMany(@NotNull ASTComponentInstance instance) {
    Preconditions.checkNotNull(instance);
    Preconditions.checkArgument(instance.isPresentSymbol());
    Preconditions.checkArgument(instance.getSymbol().isPresentType());
    Preconditions.checkNotNull(instance.getSymbol().getType().getTypeInfo());
    assertConsistentArguments(instance);

    List<ASTExpression> instantiationArgs = instance.getSymbol().getArguments();
    CompTypeExpression toInstantiate = instance.getSymbol().getType();
    List<VariableSymbol> paramsOfCompType = toInstantiate.getTypeInfo().getParameters();

    if (instantiationArgs.size() > paramsOfCompType.size()) {
      ASTExpression firstIllegalArg = instantiationArgs.get(paramsOfCompType.size());
      ASTExpression lastIllegalArg = instantiationArgs.get(instantiationArgs.size() - 1);

      Log.error(ArcError.TOO_MANY_INSTANTIATION_ARGUMENTS.format(
        instantiationArgs.size(), instance.getName(), toInstantiate.printName(), paramsOfCompType.size()
      ), firstIllegalArg.get_SourcePositionStart(), lastIllegalArg.get_SourcePositionEnd());
    }
  }

  /**
   * Checks that there are enough instantiation arguments provided to bind all mandatory configuration parameters of the
   * component type that should be instantiated.
   *
   * @param instance The AST node of the component instance whose instantiation arguments should be checked.
   */
  protected void checkInstantiationArgsBindAllMandatoryParams(@NotNull ASTComponentInstance instance) {
    Preconditions.checkNotNull(instance);
    Preconditions.checkState(instance.isPresentSymbol());
    Preconditions.checkArgument(instance.getSymbol().isPresentType());
    assertConsistentArguments(instance);

    List<ASTExpression> instantiationArgs = instance.getSymbol().getArguments();
    CompTypeExpression toInstantiate = instance.getSymbol().getType();
    List<VariableSymbol> paramsOfCompType = toInstantiate.getTypeInfo().getParameters();

    long mandatoryParamsAmount = paramsOfCompType.stream()
      .map(VariableSymbol::getAstNode)
      .map(ASTArcParameter.class::cast)
      .filter(param -> !param.isPresentDefault())
      .count();

    if (mandatoryParamsAmount > instantiationArgs.size()) {
      SourcePosition lastArgumentPos = !instantiationArgs.isEmpty() ?
        instantiationArgs.get(instantiationArgs.size() - 1).get_SourcePositionEnd() :
        instance.get_SourcePositionEnd();

      Log.error(ArcError.TOO_FEW_INSTANTIATION_ARGUMENTS.format(
        instantiationArgs.size(), instance.getName(), toInstantiate.printName(), mandatoryParamsAmount
      ), lastArgumentPos);
    }
  }

  protected void checkInstantiationArgsHaveCorrectTypes(@NotNull ASTComponentInstance instance) {
    Preconditions.checkNotNull(instance);
    Preconditions.checkState(instance.isPresentSymbol());
    Preconditions.checkArgument(instance.getSymbol().isPresentType());
    Preconditions.checkNotNull(instance.getSymbol().getType().getTypeInfo());
    assertConsistentArguments(instance);

    CompTypeExpression toInstantiate = instance.getSymbol().getType();
    List<TypeCheckResult> instantiationArgs = instance.getSymbol().getArguments().stream()
      .map(expr -> this.getTypeCalculator().deriveType(expr))
      .collect(Collectors.toList());

    List<SymTypeExpression> paramSignatureOfCompType = toInstantiate.getTypeInfo()
      .getParameters().stream()
      .map(ISymbol::getName)
      .map(toInstantiate::getTypeExprOfParameter)
      .map(paramType -> paramType.orElseThrow(IllegalStateException::new))
      .collect(Collectors.toList());

    for (int i = 0; i < Math.min(instantiationArgs.size(), paramSignatureOfCompType.size()); i++) {
      if (instantiationArgs.get(i).isPresentCurrentResult()
        && !TypeCheck.compatible(paramSignatureOfCompType.get(i), instantiationArgs.get(i).getCurrentResult())) {

        ASTExpression incompatibleArgument = instance.getArguments().getExpression(i);
        String correspondingParamName = toInstantiate.getTypeInfo().getParameters().get(i).getName();

        Log.error(ArcError.INSTANTIATION_ARGUMENT_TYPE_MISMATCH.format(
          i + 1, instance.getName(), instantiationArgs.get(i).getCurrentResult().print(),
          paramSignatureOfCompType.get(i).print(), correspondingParamName,
          toInstantiate.printName()
        ), incompatibleArgument.get_SourcePositionStart(), incompatibleArgument.get_SourcePositionEnd());
      }
      if (!instantiationArgs.get(i).isPresentCurrentResult()) {
        Log.debug(String.format("Checking coco '%s' is skipped for instantiation argument No. '%d', as the type of " +
              "the argument expression could not be calculated. Position: '%s'.",
            this.getClass().getSimpleName(), i + 1, instance.getArguments().getExpression(i).get_SourcePositionStart()),
          "CoCos");
      }
    }
  }
}
