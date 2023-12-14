/* (c) https://github.com/MontiCore/monticore */
package arcbasis._cocos;

import arcbasis._ast.ASTArcArgument;
import arcbasis._ast.ASTArcParameter;
import arcbasis._ast.ASTComponentInstance;
import arcbasis.check.CompTypeExpression;
import arcbasis.check.IArcTypeCalculator;
import com.google.common.base.Preconditions;
import de.monticore.expressions.expressionsbasis._ast.ASTExpression;
import de.monticore.symbols.basicsymbols._symboltable.VariableSymbol;
import de.monticore.symbols.compsymbols._symboltable.ComponentSymbol;
import de.monticore.symbols.compsymbols._symboltable.SubcomponentSymbol;
import de.monticore.types.check.SymTypeExpression;
import de.monticore.types3.SymTypeRelations;
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
 */
public class ConfigurationParameterAssignment implements ArcBasisASTComponentInstanceCoCo {

  protected final IArcTypeCalculator tc;

  public ConfigurationParameterAssignment(@NotNull IArcTypeCalculator tc) {
    Preconditions.checkNotNull(tc);
    this.tc = tc;
  }

  @Override
  public void check(@NotNull ASTComponentInstance node) {
    Preconditions.checkNotNull(node);
    Preconditions.checkArgument(node.isPresentSymbol());

    if (!node.getSymbol().isTypePresent() || !(node.getSymbol().getType() instanceof CompTypeExpression)) {
      Log.debug("Skip coco check, the subcomponent's type is missing.", this.getClass().getCanonicalName());
      return;
    }

    this.checkArgumentNotTooMany(node);

    if (this.checkKeywordsMustBeParameters(node) & this.checkKeywordArgsLast(node)) {
      this.checkArgumentsBindAllMandatoryParameters(node);
      if (this.checkArgValuesUnique(node) & this.checkKeywordArgsUnique(node)) {
        this.checkInstantiationArgsHaveCorrectTypes(node);
      }
    }
  }

  /**
   * Checks that the number of arguments provided to the subcomponent's
   * instantiation is at most the number of the constructor's parameters.
   *
   * @param node the subcomponent to check
   */
  protected void checkArgumentNotTooMany(@NotNull ASTComponentInstance node) {
    Preconditions.checkNotNull(node);
    Preconditions.checkArgument(node.isPresentSymbol());
    Preconditions.checkArgument(node.getSymbol().isTypePresent());
    Preconditions.checkNotNull(node.getSymbol().getType().getTypeInfo());

    SubcomponentSymbol subcomponent = node.getSymbol();
    CompTypeExpression componentExpression = (CompTypeExpression) subcomponent.getType();

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
   * @param node the subcomponent to check
   */
  protected void checkArgumentsBindAllMandatoryParameters(@NotNull ASTComponentInstance node) {
    Preconditions.checkNotNull(node);
    Preconditions.checkArgument(node.isPresentSymbol());
    Preconditions.checkArgument(node.getSymbol().isTypePresent());
    Preconditions.checkNotNull(node.getSymbol().getType().getTypeInfo());

    if (!node.getSymbol().getType().getTypeInfo().isPresentAstNode()) {
      Log.debug("Skip coco check, we currently do not support this check for library components", this.getClass().getCanonicalName());
      return;
    }

    SubcomponentSymbol subcomponent = node.getSymbol();
    CompTypeExpression componentExpression = (CompTypeExpression) subcomponent.getType();

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
        node.get_SourcePositionStart(),
        node.get_SourcePositionEnd()
      );
    }
  }

  /**
   * Checks that all arguments of the subcomponent's instantiation uphold
   * the type signature of the component's constructor.
   *
   * @param node the subcomponent to check
   */
  protected void checkInstantiationArgsHaveCorrectTypes(@NotNull ASTComponentInstance node) {
    Preconditions.checkNotNull(node);
    Preconditions.checkArgument(node.isPresentSymbol());
    Preconditions.checkArgument(node.getSymbol().isTypePresent());
    Preconditions.checkNotNull(node.getSymbol().getType().getTypeInfo());

    SubcomponentSymbol subcomponent = node.getSymbol();
    CompTypeExpression componentExpression = (CompTypeExpression) subcomponent.getType();

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
        if (!SymTypeRelations.isCompatible(paramTypes.get(paramIndex), argTypes.get(i))) {
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
        if (!SymTypeRelations.isCompatible(paramTypes.get(i), argTypes.get(i))) {
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
   * @param node the subcomponent to check
   */
  protected boolean checkKeywordArgsUnique(@NotNull ASTComponentInstance node) {
    Preconditions.checkNotNull(node);
    Preconditions.checkArgument(node.isPresentSymbol());
    Preconditions.checkArgument(node.getSymbol().isTypePresent());
    Preconditions.checkNotNull(node.getSymbol().getType().getTypeInfo());

    SubcomponentSymbol subcomponent = node.getSymbol();
    CompTypeExpression componentExpression = (CompTypeExpression) subcomponent.getType();
    List<ASTArcArgument> arguments = componentExpression.getArcArguments();

    Set<String> keyArguments = new HashSet<>();
    int keywordCounter;

    boolean isUnique = true;
    for (ASTArcArgument argument : arguments) {
      if (argument.isPresentName()) {
        keywordCounter = keyArguments.size();
        keyArguments.add(argument.getName());
        if (keywordCounter == keyArguments.size()) {
          Log.error(ArcError.KEY_NOT_UNIQUE.format(
              argument.getName(), subcomponent.getName()
            ),
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
   * @param node the subcomponent to check
   */
  protected boolean checkArgValuesUnique(@NotNull ASTComponentInstance node) {
    Preconditions.checkNotNull(node);
    Preconditions.checkArgument(node.isPresentSymbol());
    Preconditions.checkArgument(node.getSymbol().isTypePresent());
    Preconditions.checkNotNull(node.getSymbol().getType().getTypeInfo());

    SubcomponentSymbol subcomponent = node.getSymbol();
    CompTypeExpression componentExpression = (CompTypeExpression) subcomponent.getType();
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
   * @param node the subcomponent to check
   */
  protected boolean checkKeywordArgsLast(@NotNull ASTComponentInstance node) {
    Preconditions.checkNotNull(node);
    Preconditions.checkArgument(node.isPresentSymbol());
    Preconditions.checkArgument(node.getSymbol().isTypePresent());
    Preconditions.checkNotNull(node.getSymbol().getType().getTypeInfo());

    SubcomponentSymbol subcomponent = node.getSymbol();
    CompTypeExpression componentExpression = (CompTypeExpression) subcomponent.getType();

    boolean keywordAssignmentPresent = false;
    boolean rightArgumentOrder = true;

    List<ASTArcArgument> instantiationArgs = componentExpression.getArcArguments();

    for (ASTArcArgument argument : instantiationArgs) {
      if (argument.isPresentName()) {
        keywordAssignmentPresent = true;
      } else if (keywordAssignmentPresent) {
        Log.error(ArcError.COMP_ARG_VALUE_AFTER_KEY.format(subcomponent.getName()),
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
   * @param node the subcomponent to check
   */
  protected boolean checkKeywordsMustBeParameters(@NotNull ASTComponentInstance node) {
    Preconditions.checkNotNull(node);
    Preconditions.checkArgument(node.isPresentSymbol());
    Preconditions.checkArgument(node.getSymbol().isTypePresent());
    Preconditions.checkNotNull(node.getSymbol().getType().getTypeInfo());

    SubcomponentSymbol subcomponent = node.getSymbol();
    CompTypeExpression componentExpression = (CompTypeExpression) subcomponent.getType();
    ComponentSymbol component = componentExpression.getTypeInfo();

    boolean keysAreParams = true;

    for (ASTArcArgument argument : componentExpression.getArcArguments()) {
      if (argument.isPresentName() && component.getParameter(argument.getName()).isEmpty()) {
        Log.error(ArcError.COMP_ARG_KEY_INVALID.format(node.getName()),
          argument.get_SourcePositionStart(),
          argument.get_SourcePositionEnd()
        );
        keysAreParams = false;
      }

    }
    return keysAreParams;
  }
}
