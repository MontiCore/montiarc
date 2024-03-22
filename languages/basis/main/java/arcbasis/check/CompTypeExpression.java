/* (c) https://github.com/MontiCore/monticore */
package arcbasis.check;

import arcbasis._ast.ASTArcArgument;
import arcbasis._symboltable.ComponentTypeSymbol;
import com.google.common.base.Preconditions;
import de.monticore.expressions.expressionsbasis._ast.ASTExpression;
import de.monticore.symbols.basicsymbols._symboltable.VariableSymbol;
import de.monticore.symbols.compsymbols._symboltable.ComponentSymbol;
import de.monticore.types.check.CompKindExpression;
import de.monticore.types.check.SymTypeExpression;
import org.codehaus.commons.nullanalysis.NotNull;

import java.util.*;
import java.util.stream.Collectors;

/**
 * Represents all sorts of component types that component instances can have or that can be parents of other component
 * types. E.g., a {@code CompTypeExpression} can represent a generic component type whose type arguments are filled:
 * {@code MyComp<Integer>}. This is not representable by Symbols alone, as generic component types only have unspecific
 * type parameters ({@code MyComp<T>}.
 */
public abstract class CompTypeExpression extends CompKindExpression {

  protected LinkedHashMap<VariableSymbol, ASTArcArgument> parameterBindings;
  protected List<ASTArcArgument> arguments;

  /**
   * @return a {@code List} of the configuration arguments of this component.
   */
  public List<ASTArcArgument> getArcArguments() {
    return this.arguments;
  }

  /**
   * @param argument the configuration argument to add to this component.
   */
  public void addArcArgument(@NotNull ASTArcArgument argument) {
    Preconditions.checkNotNull(argument);
    this.arguments.add(argument);
  }

  /**
   * @param arguments the configuration arguments to add to this component.
   * @see this#addArcArgument(ASTArcArgument)
   */
  public void addArcArguments(@NotNull List<ASTArcArgument> arguments) {
    Preconditions.checkNotNull(arguments);
    Preconditions.checkArgument(!arguments.contains(null));
    for (ASTArcArgument argument : arguments) {
      this.addArcArgument(argument);
    }
  }

  public Optional<ASTArcArgument> getParamArcBindingFor(@NotNull VariableSymbol var) {
    Preconditions.checkNotNull(var);
    return Optional.ofNullable(this.getParamArcBindings().get(var));
  }

  public Map<VariableSymbol, ASTArcArgument> getParamArcBindings() {
    Preconditions.checkNotNull(parameterBindings);
    return Collections.unmodifiableMap(parameterBindings);
  }

  public List<ASTArcArgument> getParamArcBindingsAsList() {
    Preconditions.checkNotNull(parameterBindings);
    // We know LinkedHashMaps are ordered and thus .values represents the order of the arguments
    return new ArrayList<>(this.getParamArcBindings().values());
  }

  @Override
  public List<ASTExpression> getArguments() {
    return getArcArguments().stream().map(ASTArcArgument::getExpression).collect(Collectors.toList());
  }

  @Override
  public Optional<ASTExpression> getParamBindingFor(VariableSymbol var) {
    return getParamArcBindingFor(var).map(ASTArcArgument::getExpression);
  }

  @Override
  public List<ASTExpression> getParamBindingsAsList() {
    return getParamArcBindingsAsList().stream().map(ASTArcArgument::getExpression).collect(Collectors.toList());
  }

  public void bindParams() {
    List<ASTArcArgument> parameterArguments = this.getArcArguments();

    int firstKeywordArgument = 0;
    LinkedHashMap<String, ASTArcArgument> keywordExpressionMap = new LinkedHashMap<>();
    LinkedHashMap<VariableSymbol, ASTArcArgument> parameterBindings = new LinkedHashMap<>();
    super.parameterBindings = new LinkedHashMap<>();
    // We know LinkedHashMaps are ordered by insertion time. As we rely on the fact that the ordering of the
    // arguments is consistent with the ordering in the map, the following iteration ensures it:
    for (int i = 0; i < this.getTypeInfo().getParameters().size(); i++) {
      if (i < parameterArguments.size()) // Deal with wrong number of parameters through cocos
        if (!parameterArguments.get(i).isPresentName()) {
          parameterBindings.put(this.getTypeInfo().getParameters().get(i), parameterArguments.get(i));
          super.parameterBindings.put(this.getTypeInfo().getParameters().get(i), parameterArguments.get(i).getExpression());
          firstKeywordArgument++;
        } else {
          keywordExpressionMap.put(parameterArguments.get(i).getName(), parameterArguments.get(i));
        }
    }

    // iterate over keyword-based arguments (CoCo assures that no position-based argument occurs
    // after the first keyword-based argument)
    for (int j = firstKeywordArgument; j < this.getTypeInfo().getParameters().size(); j++) {
      if (keywordExpressionMap.containsKey(this.getTypeInfo().getParameters().get(j).getName()) &&
        !parameterBindings.containsKey(this.getTypeInfo().getParameters().get(j))) {
        parameterBindings.put(this.getTypeInfo().getParameters().get(j),
          keywordExpressionMap.get(this.getTypeInfo().getParameters().get(j).getName()));
        super.parameterBindings.put(this.getTypeInfo().getParameters().get(j),
          keywordExpressionMap.get(this.getTypeInfo().getParameters().get(j).getName()).getExpression());
      }
    }

    this.parameterBindings = parameterBindings;
  }

  protected CompTypeExpression(@NotNull ComponentTypeSymbol compTypeSymbol) {
    super(Preconditions.checkNotNull(compTypeSymbol));
    this.arguments = new ArrayList<>();
    this.parameterBindings = new LinkedHashMap<>();
  }

  public ComponentTypeSymbol getTypeInfo() {
    return (ComponentTypeSymbol) this.component;
  }

  public abstract String printName();

  public abstract String printFullName();

  /**
   * Returns the SymTypeExpression of the type of the port specified by {@code portName}. If the port's type depends on
   * type parameters which are assigned by this CompTypeExpression, they are resolved in the returned
   * SymTypeExpression. E.g., let assume this component's type expression is {@code Comp<Person>} and Comp is defined by
   * {@code Comp<T>}, having a port of type {@code T}. Then, as the type argument for {@code T} is {@code Person}, the
   * SymTypeExpression returned by this method will be {@code Person} for that port.
   *
   * @param portName The name of the port for whom the type is requested.
   * @return The {@code SymTypeExpressions} of the port's type enclosed in an {@code Optional}. An empty {@code
   * Optional} if the component has no such port.
   */
  @Override
  public abstract Optional<SymTypeExpression> getTypeOfPort(@NotNull String portName);

  /**
   * Returns the SymTypeExpression of the type of the parameter specified by {@code parameterName}. If the parameter's
   * type depends on type parameters which are assigned by this CompTypeExpression, they are resolved in the returned
   * SymTypeExpression. E.g., let assume this component's type expression is {@code Comp<Person>} and Comp is defined by
   * {@code Comp<T>}, having a parameter of type {@code T}. Then, as the type argument for {@code T} is {@code Person},
   * the SymTypeExpression returned by this method will be {@code Person} for that parameter.
   *
   * @param name The name of the parameter for whom the type is requested.
   * @return The {@code SymTypeExpressions} of the parameter's type enclosed in an {@code Optional}. An empty {@code
   * Optional} if the component has no such parameter.
   */
  @Override
  public abstract Optional<SymTypeExpression> getTypeOfParameter(@NotNull String name);

  public abstract List<SymTypeExpression> getParameterTypes();

  public abstract List<SymTypeExpression> getTypeBindingsAsList();

  public CompTypeExpression deepClone() {
    return deepClone(getTypeInfo());
  }

  public abstract CompTypeExpression deepClone(@NotNull ComponentTypeSymbol compTypeSymbol);

  public CompKindExpression deepClone(@NotNull ComponentSymbol componentSymbol) {
    return deepClone((ComponentTypeSymbol) componentSymbol);
  }

  public abstract boolean deepEquals(@NotNull CompTypeExpression compSymType);

  @Override
  public boolean deepEquals(@NotNull CompKindExpression compKindExpression) {
    if (!(compKindExpression instanceof CompTypeExpression)) return false;
    return deepEquals((CompTypeExpression) compKindExpression);
  }
}
