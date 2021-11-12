/* (c) https://github.com/MontiCore/monticore */
package arcbasis.check;

import arcbasis._symboltable.ComponentTypeSymbol;
import com.google.common.base.Preconditions;
import de.monticore.types.check.SymTypeExpression;
import org.codehaus.commons.nullanalysis.NotNull;

import java.util.Optional;

/**
 * Represents all sorts of component types that component instances can have or that can be parents of other component
 * types. E.g., a {@code CompSymTypeExpression} can represent a generic component type whose type arguments are filled:
 * {@code MyComp<Integer>}. This is not representable by Symbols alone, as generic component types only have unspecific
 * type parameters ({@code MyComp<T>}.
 */
public abstract class CompSymTypeExpression {

  protected final ComponentTypeSymbol compTypeSymbol;

  protected CompSymTypeExpression(@NotNull ComponentTypeSymbol compTypeSymbol) {
    Preconditions.checkNotNull(compTypeSymbol);
    this.compTypeSymbol = compTypeSymbol;
  }

  public ComponentTypeSymbol getTypeInfo() {
    return this.compTypeSymbol;
  }

  public abstract String printName();

  public abstract String printFullName();

  /**
   * @return The CompSymTypeExpression that represents this component's parent. E.g., if this component's type
   * expression is {@code Comp<Person>} and the definition of Comp is {@code Comp<T> extends Parent<List<T>>}, then the
   * returned CompSymTypeExpression represents {@code Parent<List<Person>>}. The returned CompSymTypeExpression is
   * enclosed in an {@code Optional}. The Optional is empty {@code Optional} if the component has no parent component.
   */
  public abstract Optional<CompSymTypeExpression> getParentTypeExpr();

  /**
   * Returns the SymTypeExpression of the type of the port specified by {@code portName}. If the port's type depends on
   * type parameters which are assigned by this CompSymTypeExpression, they are resolved in the returned
   * SymTypeExpression. E.g., let assume this component's type expression is {@code Comp<Person>} and Comp is defined by
   * {@code Comp<T>}, having a port of type {@code T}. Then, as the type argument for {@code T} is {@code Person}, the
   * SymTypeExpression returned by this method will be {@code Person} for that port.
   *
   * @param portName The name of the port for whom the type is requested.
   * @return The {@code SymTypeExpressions} of the port's type enclosed in an {@code Optional}. An empty {@code
   * Optional} if the component has no such port.
   */
  public abstract Optional<SymTypeExpression> getTypeExprOfPort(@NotNull String portName);

  /**
   * Returns the SymTypeExpression of the type of the parameter specified by {@code parameterName}. If the parameter's
   * type depends on type parameters which are assigned by this CompSymTypeExpression, they are resolved in the returned
   * SymTypeExpression. E.g., let assume this component's type expression is {@code Comp<Person>} and Comp is defined by
   * {@code Comp<T>}, having a parameter of type {@code T}. Then, as the type argument for {@code T} is {@code Person},
   * the SymTypeExpression returned by this method will be {@code Person} for that parameter.
   *
   * @param parameterName The name of the parameter for whom the type is requested.
   * @return The {@code SymTypeExpressions} of the parameter's type enclosed in an {@code Optional}. An empty {@code
   * Optional} if the component has no such parameter.
   */
  public abstract Optional<SymTypeExpression> getTypeExprOfParameter(@NotNull String parameterName);

  public abstract CompSymTypeExpression deepClone();

  public abstract boolean deepEquals(@NotNull CompSymTypeExpression compSymType);
}
