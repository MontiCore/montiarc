/* (c) https://github.com/MontiCore/monticore */
package arcbasis._cocos;

import arcbasis._ast.ASTArcArgument;
import arcbasis._ast.ASTArcParameter;
import arcbasis._ast.ASTComponentInstance;
import arcbasis._ast.ASTComponentType;
import arcbasis._symboltable.ComponentTypeSymbol;
import arcbasis.check.CompTypeExpression;
import arcbasis.check.IArcTypeCalculator;
import com.google.common.base.Preconditions;
import de.monticore.expressions.expressionsbasis._ast.ASTExpression;
import de.monticore.symbols.basicsymbols._symboltable.VariableSymbol;
import de.monticore.symbols.compsymbols._symboltable.ComponentSymbol;
import de.monticore.types.check.CompKindExpression;
import de.monticore.types.check.SymTypeExpression;
import de.monticore.types3.SymTypeRelations;
import de.se_rwth.commons.SourcePosition;
import de.se_rwth.commons.logging.Log;
import montiarc.util.ArcError;
import org.codehaus.commons.nullanalysis.NotNull;

import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
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
 * The CoCo can also be applied to super and refinement component configurations.
 * <p>
 */
public class ConfigurationParameterAssignment implements ArcBasisASTComponentInstanceCoCo, ArcBasisASTComponentTypeCoCo {

  protected final IArcTypeCalculator tc;

  public ConfigurationParameterAssignment(@NotNull IArcTypeCalculator tc) {
    Preconditions.checkNotNull(tc);
    this.tc = tc;
  }

  @Override
  public void check(@NotNull ASTComponentType node) {
    Preconditions.checkNotNull(node);
    Preconditions.checkArgument(node.isPresentSymbol());

    ComponentTypeSymbol component = node.getSymbol();

    for (CompKindExpression parent : component.getSuperComponentsList()) {
      if (!(parent instanceof CompTypeExpression)) continue;
      check((CompTypeExpression) parent, node.get_SourcePositionStart(), node.get_SourcePositionEnd());
    }
    for (CompKindExpression refines : component.getRefinementsList()) {
      if (!(refines instanceof CompTypeExpression)) continue;
      check((CompTypeExpression) refines, node.get_SourcePositionStart(), node.get_SourcePositionEnd());
    }
  }

  @Override
  public void check(@NotNull ASTComponentInstance node) {
    Preconditions.checkNotNull(node);
    Preconditions.checkArgument(node.isPresentSymbol());

    if (!node.getSymbol().isTypePresent() || !(node.getSymbol().getType() instanceof CompTypeExpression)) {
      Log.debug("Skip coco check, the subcomponent's type is missing.", this.getClass().getCanonicalName());
      return;
    }

    check((CompTypeExpression) node.getSymbol().getType(), node.get_SourcePositionStart(), node.get_SourcePositionEnd());
  }

  protected void check(@NotNull CompTypeExpression componentExpression, @NotNull SourcePosition sourcePositionStart, @NotNull SourcePosition sourcePositionEnd) {
    Preconditions.checkNotNull(componentExpression);
    Preconditions.checkNotNull(sourcePositionStart);
    Preconditions.checkNotNull(sourcePositionEnd);

    this.checkArgumentNotTooMany(componentExpression);

    if (this.checkKeywordsMustBeParameters(componentExpression) & this.checkKeywordArgsLast(componentExpression)) {
      this.checkArgumentsBindAllMandatoryParameters(componentExpression, sourcePositionStart, sourcePositionEnd);
      if (this.checkArgValuesUnique(componentExpression) & this.checkKeywordArgsUnique(componentExpression)) {
        this.checkInstantiationArgsHaveCorrectTypes(componentExpression);
      }
    }
  }

  /**
   * Checks that the number of arguments provided to the subcomponent's
   * instantiation is at most the number of the constructor's parameters.
   *
   * @param componentExpression the component expression to check
   */
  protected void checkArgumentNotTooMany(@NotNull CompTypeExpression componentExpression) {
    Preconditions.checkNotNull(componentExpression);

    List<ASTArcArgument> arguments = componentExpression.getArcArguments();
    List<VariableSymbol> parameters = componentExpression.getTypeInfo().getParameters();

    if (arguments.size() > parameters.size()) {
      ASTArcArgument firstIllegalArg = componentExpression.getArcArguments().get(parameters.size());
      ASTArcArgument lastIllegalArg = componentExpression.getArcArguments().get(arguments.size() - 1);

      Log.error(ArcError.TOO_MANY_ARGUMENTS.format(parameters.size(), arguments.size()),
        firstIllegalArg.get_SourcePositionStart(),
        lastIllegalArg.get_SourcePositionEnd()
      );
    }
  }

  /**
   * Checks that enough arguments are provided to bind all mandatory parameters
   * of the component's constructor.
   *
   * @param componentExpression the component expression to check
   */
  protected void checkArgumentsBindAllMandatoryParameters(@NotNull CompTypeExpression componentExpression, @NotNull SourcePosition sourcePositionStart, @NotNull SourcePosition sourcePositionEnd) {
    Preconditions.checkNotNull(componentExpression);
    Preconditions.checkNotNull(sourcePositionStart);
    Preconditions.checkNotNull(sourcePositionEnd);

    if (!componentExpression.getTypeInfo().isPresentAstNode()) {
      Log.debug("Skip coco check, we currently do not support this check for library components", this.getClass().getCanonicalName());
      return;
    }

    List<ASTArcArgument> arguments = componentExpression.getArcArguments();
    List<VariableSymbol> parameters = componentExpression.getTypeInfo().getParameters();

    List<String> paramNames = parameters.stream()
      .map(VariableSymbol::getName).collect(Collectors.toList());
    Map<String, Integer> paramIndices = IntStream.range(0, paramNames.size()).boxed()
      .collect(Collectors.toMap(paramNames::get, Function.identity()));

    List<ASTArcArgument> keywordArgs = arguments.stream()
      .filter(ASTArcArgument::isPresentName)
      .collect(Collectors.toList());

    long mandatoryParamsAmount = parameters.stream()
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

    if (mandatoryParamsAmount + defaultAssignedByKey > arguments.size()) {
      Log.error(ArcError.TOO_FEW_ARGUMENTS.format(mandatoryParamsAmount, arguments.size()),
        sourcePositionStart,
        sourcePositionEnd
      );
    }
  }

  /**
   * Checks that all arguments of the subcomponent's instantiation uphold
   * the type signature of the component's constructor.
   *
   * @param componentExpression the component expression to check
   */
  protected void checkInstantiationArgsHaveCorrectTypes(@NotNull CompTypeExpression componentExpression) {
    Preconditions.checkNotNull(componentExpression);

    List<ASTArcArgument> arguments = componentExpression.getArcArguments();
    List<VariableSymbol> parameters = componentExpression.getTypeInfo().getParameters();

    List<String> paramNames = parameters.stream().map(VariableSymbol::getName).collect(Collectors.toList());
    Map<String, Integer> paramIndices = IntStream.range(0, paramNames.size()).boxed()
      .collect(Collectors.toMap(paramNames::get, Function.identity()));

    List<SymTypeExpression> argTypes = arguments.stream()
      .map(ASTArcArgument::getExpression)
      .map(this.tc::typeOf)
      .collect(Collectors.toList());

    List<SymTypeExpression> paramTypes = componentExpression.getParameterTypes();

    for (int i = 0; i < Math.min(argTypes.size(), paramTypes.size()); i++) {
      if (arguments.get(i).isPresentName()) {
        // check keyword argument
        String key = arguments.get(i).getName();

        int paramIndex = paramIndices.get(key);
        if (!paramTypes.get(paramIndex).isObscureType()
          && !argTypes.get(i).isObscureType()
          && !SymTypeRelations.isCompatible(paramTypes.get(paramIndex), argTypes.get(i))) {
          // check non-keyword argument
          ASTExpression argument = arguments.get(i).getExpression();

          Log.error(ArcError.COMP_ARG_TYPE_MISMATCH.format(
              paramTypes.get(i).print(), argTypes.get(i).print()
            ),
            argument.get_SourcePositionStart(),
            argument.get_SourcePositionEnd()
          );
        }
      } else {
        // the non-keyword argument's type is available
        if (!paramTypes.get(i).isObscureType()
          && !argTypes.get(i).isObscureType()
          && !SymTypeRelations.isCompatible(paramTypes.get(i), argTypes.get(i))) {
          ASTExpression argument = arguments.get(i).getExpression();

          Log.error(ArcError.COMP_ARG_TYPE_MISMATCH.format(
              paramTypes.get(i).print(), argTypes.get(i).print()
            ),
            argument.get_SourcePositionStart(),
            argument.get_SourcePositionEnd());
        }
      }
    }
  }

  /**
   * Checks that keyword parameter is used only once in the subcomponent's instantiation
   *
   * @param componentExpression the component expression to check
   */
  protected boolean checkKeywordArgsUnique(@NotNull CompTypeExpression componentExpression) {
    Preconditions.checkNotNull(componentExpression);

    List<ASTArcArgument> arguments = componentExpression.getArcArguments();

    Set<String> keyArguments = new HashSet<>();
    int keywordCounter;

    boolean isUnique = true;
    for (ASTArcArgument argument : arguments) {
      if (argument.isPresentName()) {
        keywordCounter = keyArguments.size();
        keyArguments.add(argument.getName());
        if (keywordCounter == keyArguments.size()) {
          Log.error(ArcError.KEY_NOT_UNIQUE.format(argument.getName()),
            argument.get_SourcePositionStart(),
            argument.get_SourcePositionEnd()
          );
          isUnique = false;
        }
      }
    }

    return isUnique;
  }

  /**
   * Checks that keyword-based parent arguments do not overwrite position based parameter values.
   *
   * @param componentExpression the component expression to check
   */
  protected boolean checkArgValuesUnique(@NotNull CompTypeExpression componentExpression) {
    Preconditions.checkNotNull(componentExpression);

    List<ASTArcArgument> arguments = componentExpression.getArcArguments();

    List<String> paramNames = componentExpression.getTypeInfo().getParameters()
      .stream().map(VariableSymbol::getName).collect(Collectors.toList());
    Map<String, Integer> paramIndices = IntStream.range(0, paramNames.size()).boxed()
      .collect(Collectors.toMap(paramNames::get, Function.identity()));

    long posArgsAmount = arguments.stream()
      .filter(Predicate.not(ASTArcArgument::isPresentName))
      .count();

    boolean isUnique = true;
    for (ASTArcArgument argument : arguments) {
      if (argument.isPresentName()) {
        String key = argument.getName();
        int paramIndex = paramIndices.get(key);
        if (paramIndex < posArgsAmount) {
          Log.error(ArcError.COMP_ARG_MULTIPLE_VALUES.format(key),
            argument.get_SourcePositionStart(),
            argument.get_SourcePositionEnd()
          );
        }
      }

    }

    return isUnique;
  }

  /**
   * Checks that keyword arguments of the subcomponent's instantiation come
   * after positional arguments.
   *
   * @param componentExpression the component expression to check
   */
  protected boolean checkKeywordArgsLast(@NotNull CompTypeExpression componentExpression) {
    Preconditions.checkNotNull(componentExpression);

    boolean keywordAssignmentPresent = false;
    boolean rightArgumentOrder = true;

    List<ASTArcArgument> instantiationArgs = componentExpression.getArcArguments();

    for (ASTArcArgument argument : instantiationArgs) {
      if (argument.isPresentName()) {
        keywordAssignmentPresent = true;
      } else if (keywordAssignmentPresent) {
        Log.error(ArcError.COMP_ARG_VALUE_AFTER_KEY.format(),
          argument.get_SourcePositionStart(),
          argument.get_SourcePositionEnd()
        );
        rightArgumentOrder = false;
      }
    }
    return rightArgumentOrder;
  }

  /**
   * Checks that all keys of keyword arguments of the subcomponent's
   * instantiation are parameters of the component's constructor.
   *
   * @param componentExpression the component expression to check
   */
  protected boolean checkKeywordsMustBeParameters(@NotNull CompTypeExpression componentExpression) {
    Preconditions.checkNotNull(componentExpression);
    ComponentSymbol component = componentExpression.getTypeInfo();

    boolean keysAreParams = true;

    for (ASTArcArgument argument : componentExpression.getArcArguments()) {
      if (argument.isPresentName() && component.getParameter(argument.getName()).isEmpty()) {
        Log.error(ArcError.COMP_ARG_KEY_INVALID.format(argument.getName()),
          argument.get_SourcePositionStart(),
          argument.get_SourcePositionEnd()
        );
        keysAreParams = false;
      }

    }
    return keysAreParams;
  }
}
