/* (c) https://github.com/MontiCore/monticore */
package arcbasis._cocos;

import arcbasis._ast.ASTArcComponentType;
import arcbasis._ast.ASTComponentInstance;
import com.google.common.base.Preconditions;
import de.monticore.ast.ASTNode;
import de.monticore.expressions.expressionsbasis._ast.ASTExpression;
import de.monticore.symbols.basicsymbols._symboltable.VariableSymbol;
import de.monticore.symbols.compsymbols._ast.ASTSubcomponentArgument;
import de.monticore.symbols.compsymbols._symboltable.ComponentTypeSymbol;
import de.monticore.types.check.CompKindExpression;
import de.monticore.types.check.SymTypeExpression;
import de.monticore.types3.SymTypeRelations;
import de.monticore.types3.TypeCheck3;
import de.se_rwth.commons.SourcePosition;
import de.se_rwth.commons.logging.Log;
import montiarc.util.ArcError;
import org.codehaus.commons.nullanalysis.NotNull;

import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Optional;
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
 * This CoCo is also applied to refinement declarations, and super component configurations.
 * <p>
 */
public class ConfigurationParameterAssignment
  implements ArcBasisASTComponentInstanceCoCo, ArcBasisASTArcComponentTypeCoCo {

  public ConfigurationParameterAssignment() { }

  @Override
  public void check(@NotNull ASTArcComponentType node) {
    Preconditions.checkNotNull(node);
    Preconditions.checkArgument(node.isPresentSymbol());

    ComponentTypeSymbol component = node.getSymbol();

    for (CompKindExpression parent : component.getSuperComponentsList()) {
      Optional<SourcePosition> srcStart = parent.getSourceNode().map(ASTNode::get_SourcePositionStart);
      Optional<SourcePosition> srcEnd = parent.getSourceNode().map(ASTNode::get_SourcePositionEnd);

      check(
        parent,
        srcStart.isPresent() ? srcStart : getSrcStart(node.getHead().getArcParentList()),
        srcEnd.isPresent() ? srcEnd : getSrcEnd(node.getHead().getArcParentList())
      );
    }

    for (CompKindExpression refines : component.getRefinementsList()) {
      Optional<SourcePosition> srcStart = refines.getSourceNode().map(ASTNode::get_SourcePositionStart);
      Optional<SourcePosition> srcEnd = refines.getSourceNode().map(ASTNode::get_SourcePositionEnd);

      check(
        refines,
        srcStart.isPresent() ? srcStart : getSrcStart(node.getHead().getSpecList()),
        srcEnd.isPresent() ? srcEnd : getSrcEnd(node.getHead().getSpecList())
      );
    }
  }

  @Override
  public void check(@NotNull ASTComponentInstance node) {
    Preconditions.checkNotNull(node);
    Preconditions.checkArgument(node.isPresentSymbol());

    if (!node.getSymbol().isTypePresent()) {
      Log.debug(() -> "Skip coco check, the subcomponent's type is missing.", this.getClass().getCanonicalName());
      return;
    }

    check(
      node.getSymbol().getType(),
      Optional.of(node.get_SourcePositionStart()),
      Optional.of(node.get_SourcePositionEnd())
    );
  }

  protected void check(@NotNull CompKindExpression componentExpression,
                       @NotNull Optional<SourcePosition> sourcePositionStart,
                       @NotNull Optional<SourcePosition> sourcePositionEnd) {
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
  protected void checkArgumentNotTooMany(@NotNull CompKindExpression componentExpression) {
    Preconditions.checkNotNull(componentExpression);

    List<ASTSubcomponentArgument> arguments = componentExpression.getArguments();
    List<VariableSymbol> parameters = componentExpression.getTypeInfo().getParameterList();

    if (arguments.size() > parameters.size()) {
      ASTSubcomponentArgument firstIllegalArg = componentExpression.getArguments().get(parameters.size());
      ASTSubcomponentArgument lastIllegalArg = componentExpression.getArguments().get(arguments.size() - 1);

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
  protected void checkArgumentsBindAllMandatoryParameters(@NotNull CompKindExpression componentExpression,
                                                          @NotNull Optional<SourcePosition> srcStart,
                                                          @NotNull Optional<SourcePosition> srcEnd) {
    Preconditions.checkNotNull(componentExpression);
    Preconditions.checkNotNull(srcStart);
    Preconditions.checkNotNull(srcEnd);

    List<ASTSubcomponentArgument> arguments = componentExpression.getArguments();
    List<VariableSymbol> parameters = componentExpression.getTypeInfo().getParameterList();

    List<String> paramNames = parameters.stream()
      .map(VariableSymbol::getName).toList();
    Map<String, Integer> paramIndices = IntStream.range(0, paramNames.size()).boxed()
      .collect(Collectors.toMap(paramNames::get, Function.identity()));

    List<ASTSubcomponentArgument> keywordArgs = arguments.stream()
      .filter(ASTSubcomponentArgument::isPresentName)
      .toList();

    int mandatoryParamsAmount = parameters.size() - componentExpression.getTypeInfo().getNumOptParams();
    int defaultAssignedByKey = 0;

    for (ASTSubcomponentArgument keywordArg : keywordArgs) {
      String argumentKey = keywordArg.getName();
      int paramIndex = paramIndices.get(argumentKey);
      if (paramIndex >= mandatoryParamsAmount) {
        defaultAssignedByKey++;
      }
    }

    if (mandatoryParamsAmount + defaultAssignedByKey > arguments.size()) {
      String errorMsg = ArcError.TOO_FEW_ARGUMENTS.format(mandatoryParamsAmount, arguments.size());

      if (srcStart.isPresent() && srcEnd.isPresent()) {
        Log.error(errorMsg, srcStart.get(), srcEnd.get());
      } else {
        Log.error(errorMsg);
      }
    }
  }

  /**
   * Checks that all arguments of the subcomponent's instantiation uphold
   * the type signature of the component's constructor.
   *
   * @param componentExpression the component expression to check
   */
  protected void checkInstantiationArgsHaveCorrectTypes(@NotNull CompKindExpression componentExpression) {
    Preconditions.checkNotNull(componentExpression);

    List<ASTSubcomponentArgument> arguments = componentExpression.getArguments();
    List<VariableSymbol> parameters = componentExpression.getTypeInfo().getParameterList();

    List<String> paramNames = parameters.stream().map(VariableSymbol::getName).toList();
    Map<String, Integer> paramIndices = IntStream.range(0, paramNames.size()).boxed()
      .collect(Collectors.toMap(paramNames::get, Function.identity()));

    List<ASTExpression> exprs = arguments.stream()
      .map(ASTSubcomponentArgument::getExpression)
      .toList();

    List<Optional<SymTypeExpression>> paramTypes = componentExpression.getParameterTypes();

    for (int i = 0; i < Math.min(exprs.size(), paramTypes.size()); i++) {

      Optional<SymTypeExpression> paramType = arguments.get(i).isPresentName() ?
        // get keyword parameter
        paramTypes.get(paramIndices.get(arguments.get(i).getName())) :
        // else get non-keyword parameter
        paramTypes.get(i);

      SymTypeExpression argType = paramType.isPresent() ? TypeCheck3.typeOf(exprs.get(i), paramType.get()) : TypeCheck3.typeOf(exprs.get(i));

      if (paramType.isEmpty() || !paramType.get().isObscureType()
        && !argType.isObscureType()
        && !SymTypeRelations.isCompatible(paramType.get(), argType)) {

        Log.error(ArcError.COMP_ARG_TYPE_MISMATCH.format(
            paramType.map(SymTypeExpression::print).orElse(""), argType.print()
          ),
          exprs.get(i).get_SourcePositionStart(),
          exprs.get(i).get_SourcePositionEnd());
      }
    }
  }

  /**
   * Checks that keyword parameter is used only once in the subcomponent's instantiation
   *
   * @param componentExpression the component expression to check
   */
  protected boolean checkKeywordArgsUnique(@NotNull CompKindExpression componentExpression) {
    Preconditions.checkNotNull(componentExpression);

    List<ASTSubcomponentArgument> arguments = componentExpression.getArguments();

    Set<String> keyArguments = new HashSet<>();
    int keywordCounter;

    boolean isUnique = true;
    for (ASTSubcomponentArgument argument : arguments) {
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
  protected boolean checkArgValuesUnique(@NotNull CompKindExpression componentExpression) {
    Preconditions.checkNotNull(componentExpression);

    List<ASTSubcomponentArgument> arguments = componentExpression.getArguments();

    List<String> paramNames = componentExpression.getTypeInfo().getParameterList()
      .stream().map(VariableSymbol::getName).toList();
    Map<String, Integer> paramIndices = IntStream.range(0, paramNames.size()).boxed()
      .collect(Collectors.toMap(paramNames::get, Function.identity()));

    long posArgsAmount = arguments.stream()
      .filter(Predicate.not(ASTSubcomponentArgument::isPresentName))
      .count();

    boolean isUnique = true;
    for (ASTSubcomponentArgument argument : arguments) {
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
  protected boolean checkKeywordArgsLast(@NotNull CompKindExpression componentExpression) {
    Preconditions.checkNotNull(componentExpression);

    boolean keywordAssignmentPresent = false;
    boolean rightArgumentOrder = true;

    List<ASTSubcomponentArgument> instantiationArgs = componentExpression.getArguments();

    for (ASTSubcomponentArgument argument : instantiationArgs) {
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
  protected boolean checkKeywordsMustBeParameters(@NotNull CompKindExpression componentExpression) {
    Preconditions.checkNotNull(componentExpression);
    ComponentTypeSymbol component = componentExpression.getTypeInfo();

    boolean keysAreParams = true;

    for (ASTSubcomponentArgument argument : componentExpression.getArguments()) {
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

  /**
   * If the list is non-empty, returns the start source position of the first element
   */
  protected static Optional<SourcePosition> getSrcStart(@NotNull List<? extends ASTNode> nodes) {
    Preconditions.checkNotNull(nodes);
    if (nodes.isEmpty()) {
      return Optional.empty();
    } else {
      return Optional.of(nodes.get(0).get_SourcePositionStart());
    }
  }

  /**
   * If the list is non-empty, returns the end source position of the last element
   */
  protected static Optional<SourcePosition> getSrcEnd(@NotNull List<? extends ASTNode> nodes) {
    Preconditions.checkNotNull(nodes);
    if (nodes.isEmpty()) {
      return Optional.empty();
    } else {
      return Optional.of(nodes.get(nodes.size() - 1).get_SourcePositionEnd());
    }
  }
}
