/* (c) https://github.com/MontiCore/monticore */
package arcbasis._symboltable;

import arcbasis._ast.ASTArcArgument;
import arcbasis._ast.ASTArcBehaviorElement;
import com.google.common.base.Preconditions;
import de.monticore.symbols.basicsymbols._symboltable.VariableSymbol;
import de.monticore.symbols.compsymbols._symboltable.PortSymbol;
import de.monticore.symbols.compsymbols._symboltable.SubcomponentSymbol;
import de.monticore.symbols.compsymbols._symboltable.Timing;
import de.monticore.symboltable.modifiers.AccessModifier;
import de.monticore.types.check.CompKindExpression;
import de.monticore.types.check.SymTypeExpression;
import org.codehaus.commons.nullanalysis.NotNull;
import org.codehaus.commons.nullanalysis.Nullable;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

public class ComponentTypeSymbol extends ComponentTypeSymbolTOP {

  protected ComponentTypeSymbol outerComponent;
  protected Map<CompKindExpression, List<ASTArcArgument>> parentConfiguration;

  /**
   * @param name the name of this component type.
   */
  protected ComponentTypeSymbol(String name) {
    super(name);
    this.parameters = new ArrayList<>();
    this.parentConfiguration = new HashMap<>();
  }

  @Override
  public void setSpannedScope(@NotNull IArcBasisScope spannedScope) {
    Preconditions.checkNotNull(spannedScope);
    super.setSpannedScope(spannedScope);
  }

  /**
   * @return a {@code List} of the inner component types of this component type. The {@code List}
   * is empty if this component type has no inner component types.
   */
  public List<ComponentTypeSymbol> getInnerComponents() {
    return this.getSpannedScope().getLocalComponentTypeSymbols();
  }

  /**
   * Returns an {@code Optional} of an {@code ComponentSymbol} of an inner component of this
   * component type with the given type name. The {@code Optional} is empty if the component type
   * has no inner component with the given type name. Throws an {@link IllegalArgumentException}
   * if the given type name is {@code null}.
   *
   * @param name the type name of the inner component type.
   * @return an {@code Optional} of an inner component type with the given type name or an empty
   * {@code Optional}, if such an inner component type does not exist.
   */
  public Optional<ComponentTypeSymbol> getInnerComponent(@NotNull String name) {
    Preconditions.checkNotNull(name);
    return this.getSpannedScope().resolveComponentTypeLocally(name);
  }

  /**
   * Returns a {@code List} of {@code ComponentSymbol} of the inner components of this
   * component type with the given visibility. The {@code List} is empty if this component
   * type has no inner components with the given visibility. Throws an
   * {@link IllegalArgumentException} if the visibility is {@code null}.
   *
   * @param visibility the visibility of the inner components.
   * @return a {@code List} of inner components with the given visibility or an empty {@code
   * List}, if the component type has no inner components with the given visibility.
   */
  public List<ComponentTypeSymbol> getInnerComponents(@NotNull AccessModifier visibility) {
    Preconditions.checkNotNull(visibility);
    return this.getInnerComponents().stream()
      .filter(c -> c.getAccessModifier().includes(visibility))
      .collect(Collectors.toList());
  }

  /**
   * @return {@code true}, if this is an inner component, else {@code false}.
   */
  public boolean isInnerComponent() {
    return outerComponent != null;
  }

  /**
   * @return an {@code Optional} of this component type's outer component, or an empty {@code
   * Optional} if this is not an inner component type.
   */
  public Optional<ComponentTypeSymbol> getOuterComponent() {
    return Optional.ofNullable(outerComponent);
  }

  /**
   * Sets the outer component type that contains this component type and subsequently states
   * whether this is an inner component type or not.
   *
   * @param outerComponent the component type that contains this component type.
   */
  public void setOuterComponent(@Nullable ComponentTypeSymbol outerComponent) {
    Preconditions.checkArgument(!(outerComponent instanceof ComponentTypeSymbolSurrogate));
    this.outerComponent = outerComponent;
  }

  /**
   * @return a {@code List} of the configuration parameters of this component type.
   */
  public List<ASTArcArgument> getParentConfiguration(@NotNull CompKindExpression parent) {
    return this.parentConfiguration.getOrDefault(parent, Collections.emptyList());
  }

  /**
   * Adds the elements of a {@code Collection} of {@code VariableSymbol} of component configuration
   * parameters to the configuration parameters of this component type. Throws an
   * {@link IllegalArgumentException} if the given {@code Collection} is {@code null} or contains an
   * element that is {@code null}.
   *
   * @param expressions the symbols to add.
   * @see this#addParameter(VariableSymbol)
   */
  public void setParentConfigurationExpressions(@NotNull CompKindExpression parent, @NotNull List<ASTArcArgument> expressions) {
    Preconditions.checkNotNull(expressions);
    Preconditions.checkArgument(!expressions.contains(null));
    this.parentConfiguration.put(parent, expressions);
  }

  public void setParentConfigurationMap(@NotNull Map<CompKindExpression, List<ASTArcArgument>> parentConfiguration){
    Preconditions.checkNotNull(parentConfiguration);
    this.parentConfiguration = parentConfiguration;
  }

  /**
   * Adds the given configuration parameter to the configuration parameters of this component
   * type. Throws an {@link IllegalArgumentException} if the given symbol is {@code null}.
   * Warning!!: The VariableSymbol, with which this method is called, must already be part of the spanned scope of this
   * component!
   *
   * @param parameter the symbol to add.
   */
  public void addParameter(@NotNull VariableSymbol parameter) {
    Preconditions.checkNotNull(parameter);
    Preconditions.checkArgument(this.getSpannedScope().getLocalVariableSymbols().contains(parameter));
    this.parameters.add(parameter);
  }

  /**
   * Adds the elements of a {@code Collection} of {@code VariableSymbol} of component configuration
   * parameters to the configuration parameters of this component type. Throws an
   * {@link IllegalArgumentException} if the given {@code Collection} is {@code null} or contains an
   * element that is {@code null}.
   *
   * @param parameters the symbols to add.
   * @see this#addParameter(VariableSymbol)
   */
  public void addParameters(@NotNull Collection<VariableSymbol> parameters) {
    Preconditions.checkNotNull(parameters);
    Preconditions.checkArgument(!parameters.contains(null));
    for (VariableSymbol parameter : parameters) {
      this.addParameter(parameter);
    }
  }

  public boolean hasParameters() {
    return !this.getParametersList().isEmpty();
  }

  /**
   * @return a {@code List} of the fields of this component type.
   */
  public List<VariableSymbol> getFields() {
    return this.getSpannedScope().getLocalVariableSymbols().stream()
      .filter(f -> !(f instanceof Port2VariableAdapter))
      .collect(Collectors.toList());
  }

  /**
   * Searches the fields of this component type for a field with the given name. Returns an {@code
   * Optional} of a field of this component type with the given name, or an empty {@code Optional}
   * if no such field exists. Throws an {@link IllegalArgumentException} if the given name is
   * {@code null}.
   *
   * @param name the name of the field.
   * @return an {@code Optional} of a field of this component type with the given name, or an
   * empty {@code Optional} if no such field exists.
   */
  public Optional<VariableSymbol> getFields(@NotNull String name) {
    Preconditions.checkNotNull(name);
    return this.getFields().stream().filter(field -> field.getName().equals(name)).findFirst();
  }

  /**
   * Returns the {@code List} of the ports of this component type. Does not include inherited ports.
   *
   * @return a {@code List} of the ports of this component type.
   */
  public List<ArcPortSymbol> getArcPorts() {
    return this.getSpannedScope().getLocalArcPortSymbols();
  }

  @Override
  public List<PortSymbol> getPortsList() {
    return (List<PortSymbol>) (List<? extends PortSymbol>) getArcPorts();
  }

  /**
   * Searches the ports of this component type for a port with the given name. Does not consider
   * inherited ports. Returns an empty {@code Optional} if no such port exist. Throws an
   * {@link IllegalArgumentException} if the given name is {@code null}.
   *
   * @param name the name of the port.
   * @return an {@code Optional} of a port of this component type with the given name or an empty
   * {@code Optional} if no such port exists.
   */
  public Optional<ArcPortSymbol> getArcPort(@NotNull String name) {
    Preconditions.checkNotNull(name);
    return this.getSpannedScope().resolveArcPortLocallyMany(
      false, name, de.monticore.symboltable.modifiers.AccessModifier.ALL_INCLUSION, x -> true
    ).stream().findFirst();
  }

  /**
   * Searches the ports of this component type for a port with the given name. Search range can be
   * extended to inherited ports with the boolean parameter. Returns an empty {@code Optional} if
   * not such port exists. Throws an {@link IllegalArgumentException} if the given name is {@code
   * null}.
   *
   * @param name the name of the port.
   * @param searchInherited true, if to consider inherited ports in the search.
   * @return an {@code Optional} of a port with the given name, or an empty {@code Optional}, if no
   * such port exists.
   */
  public Optional<ArcPortSymbol> getArcPort(@NotNull String name, boolean searchInherited) {
    Preconditions.checkNotNull(name);
    Collection<ArcPortSymbol> portsToConsider
      = searchInherited ? this.getAllArcPorts() : this.getArcPorts();
    return portsToConsider.stream().filter(p -> p.getName().equals(name)).findFirst();
  }

  public Optional<SymTypeExpression> getTypeOfPort(@NotNull String portName) {
    Preconditions.checkNotNull(portName);
    // We first look if the requested port is part of our definition.
    // If not, we ask our parent if they have such a port.
    boolean portDefinedByUs = getArcPort(portName, false).isPresent();

    if (portDefinedByUs) {
      return getArcPort(portName, false)
        .filter(ArcPortSymbol::isTypePresent)
        .map(ArcPortSymbol::getType);
    } else if (!getSuperComponentsList().isEmpty()) {
      // We do not have this port. Now we look if our parents have such a port.
      return this.getSuperComponentsList().stream().map(parent -> parent.getTypeOfPort(portName)).filter(Optional::isPresent).map(Optional::get).findAny();
    } else {
      return Optional.empty();
    }
  }

  /**
   * Returns the incoming ports of this component type. Does not include inherited ports.
   *
   * @return a {@code List} of incoming ports of this component type.
   */
  public List<ArcPortSymbol> getIncomingArcPorts() {
    return this.getArcPorts(true);
  }

  /**
   * Returns the incoming ports of this component type that have the given visibility. Does not
   * included inherited ports. Throws an {@link IllegalArgumentException} if the given visibility
   * is {@code null}.
   *
   * @param visibility the visibility of the ports.
   * @return a {@code List} of the incoming ports of this component type that have the given
   * visibility.
   */
  public List<ArcPortSymbol> getIncomingArcPorts(@NotNull AccessModifier visibility) {
    Preconditions.checkNotNull(visibility);
    return this.getIncomingArcPorts().stream().filter(p -> p.getAccessModifier().includes(visibility))
      .collect(Collectors.toList());
  }

  /**
   * Searches the ports of this component type for an incoming port with the given name. Does not
   * consider inherited ports. Returns an empty {@code Optional} if no such port exist. Throws an
   * {@link IllegalArgumentException} if the given name is {@code null}.
   *
   * @param name the name of the port.
   * @return an {@code Optional} of an incoming port with the given name or an empty {@code
   * Optional} if no such port exists.
   */
  public Optional<ArcPortSymbol> getIncomingArcPort(@NotNull String name) {
    Preconditions.checkNotNull(name);
    return this.getIncomingArcPort(name, false);
  }

  /**
   * Searches the ports of this component type for an incoming port with the given name. Does
   * consider inherited ports as stated by the given {@code boolean} value. Throws an
   * {@link IllegalArgumentException} if the given name is {@code null}.
   *
   * @param name the name of the port.
   * @param searchInherited if to consider inherited ports.
   * @return an {@code Optional} of an incoming port with the given name or an empty {@code
   * Optional} if no such port exists in the specified search range.
   */
  public Optional<ArcPortSymbol> getIncomingArcPort(@NotNull String name, boolean searchInherited) {
    Preconditions.checkNotNull(name);
    Collection<ArcPortSymbol> portsToConsider
      = searchInherited ? this.getAllIncomingArcPorts() : this.getIncomingArcPorts();
    return portsToConsider.stream().filter(p -> p.getName().equals(name)).findFirst();
  }

  /**
   * Returns the outgoing ports of this component type. Does not include inherited ports.
   *
   * @return a {@code List} of the outgoing ports of this component type.
   */
  public List<ArcPortSymbol> getOutgoingArcPorts() {
    return this.getArcPorts(false);
  }

  /**
   * Returns the outgoing ports of this component type that have the given visibility. Does not
   * included inherited ports. Throws an {@link IllegalArgumentException} if the given visibility
   * is {@code null}.
   *
   * @param visibility the visibility of the ports.
   * @return a {@code List} of the outgoing ports of this component type that have the given
   * visibility.
   */
  public List<ArcPortSymbol> getOutgoingArcPorts(@NotNull AccessModifier visibility) {
    Preconditions.checkNotNull(visibility);
    return this.getOutgoingArcPorts().stream().filter(p -> p.getAccessModifier().includes(visibility))
      .collect(Collectors.toList());
  }

  /**
   * Searches the ports of this component type for an outgoing port with the given name. Does not
   * consider inherited ports. Returns an empty {@code Optional} if no such port exist. Throws an
   * {@link IllegalArgumentException} if the given name is {@code null}.
   *
   * @param name the name of the port.
   * @return an {@code Optional} of an outgoing port with the given name or an empty {@code
   * Optional} if no such port exists.
   */
  public Optional<ArcPortSymbol> getOutgoingArcPort(@NotNull String name) {
    Preconditions.checkNotNull(name);
    return this.getOutgoingArcPort(name, false);
  }

  /**
   * Searches the ports of this component type for an outgoing port with the given name. Does
   * consider inherited ports as stated by the given {@code boolean} value. Throws an
   * {@link IllegalArgumentException} if the given name is {@code null}.
   *
   * @param name the name of the port.
   * @param searchInherited if to consider inherited ports.
   * @return an {@code Optional} of an outgoing port with the given name or an empty {@code
   * Optional} if no such port exists in the specified search range.
   */
  public Optional<ArcPortSymbol> getOutgoingArcPort(@NotNull String name, boolean searchInherited) {
    Preconditions.checkNotNull(name);
    Collection<ArcPortSymbol> portsToConsider
      = searchInherited ? this.getAllOutgoingArcPorts() : this.getOutgoingArcPorts();
    return portsToConsider.stream().filter(p -> p.getName().equals(name)).findFirst();
  }

  /**
   * Returns the ports of this component type that have the given direction. Does not included
   * ports inherited from parent component types.
   *
   * @param isIncoming the direction of the ports.
   * @return a {@code List} of ports of this component type that have the given direction.
   */
  protected List<ArcPortSymbol> getArcPorts(boolean isIncoming) {
    return this.getArcPorts().stream().filter(p -> p.isIncoming() == isIncoming)
      .collect(Collectors.toList());
  }

  /**
   * Return all ports of this component type, including inherited port from all parent
   * components types. NameSpaceHiding is considered and therefore hidden ports are not returned.
   *
   * @return a {@code List} of all ports of this component type.
   */
  public List<ArcPortSymbol> getAllArcPorts() {
    return this.getAllArcPorts(new HashSet<>());
  }

  protected List<ArcPortSymbol> getAllArcPorts(@NotNull Collection<ComponentTypeSymbol> visited) {
    Preconditions.checkNotNull(visited);
    visited.add(this);

    List<ArcPortSymbol> result = new ArrayList<>(getArcPorts());
    for (CompKindExpression parent : getSuperComponentsList()) {
      if (!visited.contains(parent.getTypeInfo())) {
        List<ArcPortSymbol> inheritedPorts = new ArrayList<>();
        for (ArcPortSymbol port : ((ComponentTypeSymbol) parent.getTypeInfo()).getAllArcPorts(visited)) {
          if (result.stream().noneMatch(p -> p.getName().equals(port.getName()))) {
            inheritedPorts.add(port);
          }
        }
        result.addAll(inheritedPorts);
      }
    }

    return result;
  }

  /**
   * Returns all incoming ports of this component type, including inherited ports from all parent
   * component types. NameSpaceHiding is considered and therefore hidden ports are not returned.
   *
   * @return a {@code List} of all incoming ports of this component type.
   */
  public List<ArcPortSymbol> getAllIncomingArcPorts() {
    return this.getAllArcPorts(true);
  }

  /**
   * Returns all outgoing ports of this component type, including inherited ports from all parent
   * component types. NameSpaceHiding is considered and therefore hidden ports are not returned.
   *
   * @return a {@code List} of all outgoing ports of this component type.
   */
  public List<ArcPortSymbol> getAllOutgoingArcPorts() {
    return this.getAllArcPorts(false);
  }

  /**
   * Returns all ports of this component type that have the given direction, including inherited
   * ports from all parent components types. NameSpaceHiding is considered and therefore hidden
   * ports are not returned.
   *
   * @param isIncoming the direction of the ports.
   * @return a {@code List} of all ports of this component type that have the given direction.
   */
  protected List<ArcPortSymbol> getAllArcPorts(boolean isIncoming) {
    return this.getAllArcPorts().stream().filter(p -> p.isIncoming() == isIncoming)
      .collect(Collectors.toList());
  }

  public boolean hasPorts() {
    return !this.getArcPorts().isEmpty();
  }

  /**
   * Returns the subcomponent instances of this component type that have the given visibility.
   *
   * @param visibility the visibility of the subcomponent instances
   * @return a {@code List} of the subcomponent instances of this component type that have the
   * given visibility.
   */
  public List<SubcomponentSymbol> getSubcomponents(AccessModifier visibility) {
    return this.getSubcomponents().stream().filter(c -> c.getAccessModifier().includes(visibility))
      .collect(Collectors.toList());
  }

  protected Timing timing;

  public Optional<Timing> getTiming() {
    if (this.timing != null) {
      return Optional.of(this.timing);
    } else if (this.isAtomic() && this.getBehavior().isPresent()) {
      this.timing = this.getBehavior().get().getTiming();
      return Optional.of(this.timing);
    } else {
      return Optional.empty();
    }
  }

  public boolean isStronglyCausal() {
    return false;
  }

  protected Optional<ASTArcBehaviorElement> getBehavior() {
    if (this.isPresentAstNode()) {
      return this.getAstNode().getBody().streamArcElements()
        .filter(e -> e instanceof ASTArcBehaviorElement)
        .map(c -> (ASTArcBehaviorElement) c).findFirst();
    } else {
      return Optional.empty();
    }
  }
}
