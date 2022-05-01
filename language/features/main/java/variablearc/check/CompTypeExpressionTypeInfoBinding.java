/* (c) https://github.com/MontiCore/monticore */
package variablearc.check;

import arcbasis._symboltable.IArcBasisScope;
import arcbasis._symboltable.PortSymbol;
import arcbasis.check.CompTypeExpression;
import com.google.common.base.Preconditions;
import org.codehaus.commons.nullanalysis.NotNull;
import variablearc._symboltable.IVariableArcScope;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Represents a mapping between {@code CompTypeExpression} and {@code ComponentTypeSymbol}. Instead of directly
 * accessing {@code CompTypeExpression.getTypeInfo()} this should be used to access methods of the {@code ComponentTypeSymbol}.
 * It allows for filtering out e.g. {@code PortSymbol}s with the {@code CompTypeExpression} bindings.
 */
public abstract class CompTypeExpressionTypeInfoBinding {

  /**
   * Returns the {@code List} of the ports of this component type filtered by variability. Does not include inherited ports.
   *
   * @param compTypeExpression the compTypeExpression used for variability.
   * @return a {@code List} of the ports of this component type.
   */
  public static List<PortSymbol> getPorts(@NotNull CompTypeExpression compTypeExpression) {
    IArcBasisScope scope = compTypeExpression.getTypeInfo().getSpannedScope();
    if (compTypeExpression instanceof TypeExprOfVariableComponent && scope instanceof IVariableArcScope) {
      return ((IVariableArcScope) scope).getLocalPortSymbols(((TypeExprOfVariableComponent) compTypeExpression).getVariationPoints());
    }
    return compTypeExpression.getTypeInfo().getPorts();
  }

  /**
   * Searches the ports of this component type for multiple ports with the given name filtered by variability. Search range can be
   * extended to inherited ports with the boolean parameter. Returns an empty {@code List} if
   * no such ports exists. Throws an {@link IllegalArgumentException} if the given name is {@code
   * null}.
   *
   * @param compTypeExpression the compTypeExpression used for variability.
   * @param name            the name of the port.
   * @param searchInherited true, if to consider inherited ports in the search.
   * @return an {@code List} of ports with the given name, or an empty {@code List}, if no
   * such ports exists.
   */
  public static List<PortSymbol> getPorts(@NotNull CompTypeExpression compTypeExpression, @NotNull String name, boolean searchInherited) {
    Preconditions.checkArgument(name != null);
    Collection<PortSymbol> portsToConsider = searchInherited ? getAllPorts(compTypeExpression) : getPorts(compTypeExpression);
    return portsToConsider.stream().filter(p -> p.getName().equals(name)).collect(Collectors.toList());
  }

  /**
   * Returns the ports filtered by variability of this component type that have the given direction. Does not include
   * ports inherited from parent component types.
   *
   * @param compTypeExpression the compTypeExpression used for variability.
   * @param isIncoming the direction of the ports.
   * @return a {@code List} of ports of this component type that have the given direction.
   */
  protected static List<PortSymbol> getPorts(@NotNull CompTypeExpression compTypeExpression, boolean isIncoming) {
    return getPorts(compTypeExpression).stream().filter(p -> p.isIncoming() == isIncoming).collect(Collectors.toList());
  }

  /**
   * Return all ports of this component type, including inherited port from all parent
   * components types. NameSpaceHiding is considered and therefore hidden ports are not returned.
   *
   * @param compTypeExpression the compTypeExpression used for variability.
   * @return a {@code List} of all ports of this component type.
   */
  public static List<PortSymbol> getAllPorts(@NotNull CompTypeExpression compTypeExpression) {
    List<PortSymbol> result = new ArrayList<>(getPorts(compTypeExpression));
    if (compTypeExpression.getParentTypeExpr().isPresent()) {
      List<PortSymbol> inheritedPorts = new ArrayList<>();
      for (PortSymbol port : getAllPorts(compTypeExpression.getParentTypeExpr().get())) {
        if (result.stream().noneMatch(p -> p.getName().equals(port.getName()))) {
          inheritedPorts.add(port);
        }
      }
      result.addAll(inheritedPorts);
    }
    return result;
  }

  /**
   * Returns all ports of this component type that have the given direction, including inherited
   * ports from all parent components types. NameSpaceHiding is considered and therefore hidden
   * ports are not returned.
   *
   * @param compTypeExpression the compTypeExpression used for variability.
   * @param isIncoming the direction of the ports.
   * @return a {@code List} of all ports of this component type that have the given direction.
   */
  protected static List<PortSymbol> getAllPorts(@NotNull CompTypeExpression compTypeExpression, boolean isIncoming) {
    return getAllPorts(compTypeExpression).stream().filter(p -> p.isIncoming() == isIncoming).collect(Collectors.toList());
  }
}
