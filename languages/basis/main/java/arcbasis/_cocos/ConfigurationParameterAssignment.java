/* (c) https://github.com/MontiCore/monticore */
package arcbasis._cocos;

import arcbasis._ast.ASTArcArgument;
import arcbasis._ast.ASTArcParameter;
import arcbasis._ast.ASTComponentInstance;
import arcbasis.check.ArcBasisTypeCalculator;
import arcbasis.check.CompTypeExpression;
import arcbasis.check.IArcTypeCalculator;
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
import montiarc.util.ArcError;
import org.codehaus.commons.nullanalysis.NotNull;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

/**
 * Checks coco R10 of [Hab16]: If a configurable component is instantiated as a subcomponent, all configuration
 * parameters have to be assigned. This coco is extended by coco MR1 of [Wor16]: Arguments of configuration parameters
 * with default values may be omitted during subcomponent declaration.
 * <p>
 * In short: * [binding]: When a component type is instantiated, all it's configuration parameters without specified
 * default values must be bound with values or expressions. When default values are overwritten, then in order of their
 * appearance in the component's signature. The parameter binding can be both position based and keyword based. We
 * assure that after a keyword based assignment, no position based assignment is allowed.
 * * [typecheck]: The configuration parameters must be assignable from the
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
        astInst.getSymbol().getArcArguments().isEmpty()
          == (!astInst.isPresentArcArguments() || astInst.getArcArguments().getArcArgumentList().isEmpty())
      );

      if (!astInst.getSymbol().getArcArguments().isEmpty()) {
        Preconditions.checkArgument(astInst.getSymbol().getArcArguments().size()
          == astInst.getArcArguments().getArcArgumentList().size()
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
    if(checkKeywordsMustBeParameters(astInst) &
      checkKeywordArgsLast(astInst)) {
      checkInstantiationArgsHaveCorrectTypes(astInst);
      checkInstantiationArgsBindAllMandatoryParams(astInst);
    }
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
    if (!instance.getSymbol().isPresentType()) {
      Log.debug("Could not perform coco check '" + this.getClass().getSimpleName() + "', due to missing type.",
          this.getClass().getSimpleName());
      return;
    }
    Preconditions.checkNotNull(instance.getSymbol().getType().getTypeInfo());
    assertConsistentArguments(instance);

    List<ASTArcArgument> instantiationArgs = instance.getSymbol().getArcArguments();
    CompTypeExpression toInstantiate = instance.getSymbol().getType();
    List<VariableSymbol> paramsOfCompType = toInstantiate.getTypeInfo().getParameters();

    if (instantiationArgs.size() > paramsOfCompType.size()) {
      ASTArcArgument firstIllegalArg = instantiationArgs.get(paramsOfCompType.size());
      ASTArcArgument lastIllegalArg = instantiationArgs.get(instantiationArgs.size() - 1);

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
    if (!instance.getSymbol().isPresentType()) {
      Log.debug("Could not perform coco check '" + this.getClass().getSimpleName() + "', due to missing type.",
          this.getClass().getSimpleName());
      return;
    }
    assertConsistentArguments(instance);

    List<ASTArcArgument> instantiationArgs = instance.getSymbol().getArcArguments();
    CompTypeExpression toInstantiate = instance.getSymbol().getType();
    List<VariableSymbol> paramsOfCompType = toInstantiate.getTypeInfo().getParameters();

    List<String> paramNames = paramsOfCompType.stream()
      .map(VariableSymbol::getName).collect(Collectors.toList());
    Map<String,Integer> paramIndices = IntStream.range(0, paramNames.size()).boxed()
      .collect(Collectors.toMap(paramNames::get, Function.identity()));

    List<ASTArcArgument> keywordArgs = instantiationArgs.stream()
      .filter(ASTArcArgument::isPresentName)
      .collect(Collectors.toList());

    long mandatoryParamsAmount = paramsOfCompType.stream()
      .map(VariableSymbol::getAstNode)
      .map(ASTArcParameter.class::cast)
      .filter(param -> !param.isPresentDefault())
      .count();

    int defaultAssignedByKey = 0;

    for (ASTArcArgument keywordArg : keywordArgs) {
      String argumentKey = keywordArg.getName();
      int paramIndex = paramIndices.get(argumentKey);
      if (paramIndex >= mandatoryParamsAmount) {
        defaultAssignedByKey++;
      }
    }

    if (mandatoryParamsAmount + defaultAssignedByKey > instantiationArgs.size()) {
      SourcePosition lastArgumentPos = !instantiationArgs.isEmpty() ?
        instantiationArgs.get(instantiationArgs.size() - 1).get_SourcePositionEnd() :
        instance.get_SourcePositionEnd();

      Log.error(ArcError.TOO_FEW_INSTANTIATION_ARGUMENTS.format(
        instantiationArgs.size(), instance.getName(), toInstantiate.printName(), mandatoryParamsAmount
      ), lastArgumentPos);
    }
  }

  /**
   * Checks that all instantiation arguments have the correct types. This CoCo only applies
   *
   * @param instance The AST node of the component instance whose instantiation arguments should be checked.
   */
  protected void checkInstantiationArgsHaveCorrectTypes(@NotNull ASTComponentInstance instance) {
    Preconditions.checkNotNull(instance);
    Preconditions.checkState(instance.isPresentSymbol());
    if (!instance.getSymbol().isPresentType()) {
      Log.debug("Could not perform coco check '" + this.getClass().getSimpleName() + "', due to missing type.",
          this.getClass().getSimpleName());
      return;
    }
    Preconditions.checkNotNull(instance.getSymbol().getType().getTypeInfo());
    assertConsistentArguments(instance);

    List<ASTArcArgument> instantiationArgs = instance.getSymbol().getArcArguments();
    List<String> paramNames = instance.getSymbol().getType().getTypeInfo().getParameters()
            .stream().map(VariableSymbol::getName).collect(Collectors.toList());
    Map<String,Integer> paramIndices = IntStream.range(0, paramNames.size()).boxed()
            .collect(Collectors.toMap(paramNames::get, Function.identity()));

    long posArgsAmount = instantiationArgs.stream()
      .filter(Predicate.not(ASTArcArgument::isPresentName))
      .count();

    List<TypeCheckResult> instArgs = instantiationArgs.stream()
      .map(ASTArcArgument::getExpression)
      .map(expr -> this.getTypeCalculator().deriveType(expr))
      .collect(Collectors.toList());

    CompTypeExpression toInstantiate = instance.getSymbol().getType();

    List<Optional<SymTypeExpression>> paramSignatureOfCompType = toInstantiate.getTypeInfo()
      .getParameters().stream()
      .map(ISymbol::getName)
      .map(toInstantiate::getTypeExprOfParameter)
      .collect(Collectors.toList());

    for (int i = 0; i < Math.min(instArgs.size(), paramSignatureOfCompType.size()); i++) {
      String correspondingParamName = toInstantiate.getTypeInfo().getParameters().get(i).getName();
      if (!instArgs.get(i).isPresentResult()) {
        Log.info(String.format("Checking coco '%s' is skipped for instantiation argument No. '%d', as the type of " +
              "the argument expression could not be calculated. Position: '%s'.",
            this.getClass().getSimpleName(), i + 1, instance.getArcArguments().getArcArgument(i).get_SourcePositionStart()),
          "CoCos");
      } else if (instArgs.get(i).isType()) {
        ASTExpression typeRefExpr = instance.getArcArguments().getArcArgument(i).getExpression();

        Log.error(ArcError.CONFIG_PARAM_BINDING_IS_TYPE_REF.format(
          instArgs.get(i).getResult().print(), correspondingParamName, i + 1,
          toInstantiate.getTypeInfo().getName(), instance.getName()
        ), typeRefExpr.get_SourcePositionStart(), typeRefExpr.get_SourcePositionEnd());
      } else if (instantiationArgs.get(i).isPresentName()) {
        String argumentKey = instantiationArgs.get(i).getName();
        int paramIndex = paramIndices.get(argumentKey);
        if (paramIndex<posArgsAmount){
          Log.error(ArcError.MULTIPLE_VALUES_FOR_ARGUMENT.format(instance.getName(), argumentKey),
            instantiationArgs.get(i).get_SourcePositionStart(), instantiationArgs.get(i).get_SourcePositionEnd());
        }
        if (paramSignatureOfCompType.get(paramIndex).isEmpty() || !TypeCheck.compatible(paramSignatureOfCompType.get(paramIndex).get(), instArgs.get(i).getResult())) {
          ASTExpression incompatibleArgument = instance.getArcArguments().getArcArgument(i).getExpression();

          Log.error(ArcError.INSTANTIATION_ARGUMENT_TYPE_MISMATCH.format(
            i + 1, instance.getName(), instArgs.get(i).getResult().print(),
            paramSignatureOfCompType.get(i).map(SymTypeExpression::print).orElse("UNKNOWN"), correspondingParamName,
            toInstantiate.printName()
          ), incompatibleArgument.get_SourcePositionStart(), incompatibleArgument.get_SourcePositionEnd());
        }
      } else {
        if (paramSignatureOfCompType.get(i).isEmpty() || !TypeCheck.compatible(paramSignatureOfCompType.get(i).get(), instArgs.get(i).getResult())) {
          ASTExpression incompatibleArgument = instance.getArcArguments().getArcArgument(i).getExpression();

          Log.error(ArcError.INSTANTIATION_ARGUMENT_TYPE_MISMATCH.format(
            i + 1, instance.getName(), instArgs.get(i).getResult().print(),
            paramSignatureOfCompType.get(i).map(SymTypeExpression::print).orElse("UNKNOWN"), correspondingParamName,
            toInstantiate.printName()
          ), incompatibleArgument.get_SourcePositionStart(), incompatibleArgument.get_SourcePositionEnd());
        }
      }
    }
  }

  /**
   * Checks that keyword based instantiation arguments of a component instance come after positional arguments.
   *
   * @param instance The AST node of the component instance whose instantiation arguments should be checked.
   */
  protected boolean checkKeywordArgsLast(@NotNull ASTComponentInstance instance) {
    Preconditions.checkNotNull(instance);

    boolean keywordAssignmentPresent=false;
    boolean rightArgumentOrder=true;

    List<ASTArcArgument> instantiationArgs = instance.getSymbol().getArcArguments();

    for (ASTArcArgument argument : instantiationArgs) {
      if (argument.isPresentName()){
        keywordAssignmentPresent=true;
      }else{
        if(keywordAssignmentPresent) {
          Log.error(ArcError.POSITIONAL_ASSIGNMENT_AFTER_KEYWORD_ASSIGNMENT.format(
                          instance.getName()), argument.get_SourcePositionStart(),
                  argument.get_SourcePositionEnd());
          rightArgumentOrder=false;
        }
      }
    }
    return rightArgumentOrder;
  }

  /**
   * Checks that keyword instantiations only use keywords referring to parameters of the component instance type
   *
   * @param instance The AST node of the component instance whose instantiation arguments should be checked.
   */
  protected boolean checkKeywordsMustBeParameters(@NotNull ASTComponentInstance instance){
    Preconditions.checkNotNull(instance);
    Preconditions.checkArgument(instance.isPresentSymbol());
    boolean keysAreParams = true;
    if (!instance.getSymbol().isPresentType()) {
      Log.debug("Could not perform coco check '" + this.getClass().getSimpleName() + "', due to missing type.",
              this.getClass().getSimpleName());
      return keysAreParams;
    }

    List<ASTArcArgument> keywordArgs =  instance.getSymbol().getArcArguments().stream()
      .filter(ASTArcArgument::isPresentName)
      .collect(Collectors.toList());

    for (ASTArcArgument argument : keywordArgs){
      String paramName = argument.getName();
      if (instance.getSymbol().getType().getTypeInfo().getParameters().stream()
        .noneMatch(typeParam -> typeParam.getName().equals(paramName))){
        Log.error(ArcError.ASSIGNMENT_KEYWORD_NOT_A_PARAMETER.format(
          instance.getName()), argument.get_SourcePositionStart(),
          argument.get_SourcePositionEnd()
        );
        keysAreParams = false;
      }

    }
    return keysAreParams;
  }
}
