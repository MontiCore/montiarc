/* (c) https://github.com/MontiCore/monticore */
package arc._symboltable;

import com.google.common.base.Preconditions;
import de.monticore.symboltable.modifiers.AccessModifier;
import de.monticore.types.typesymbols._symboltable.FieldSymbol;
import de.monticore.types.typesymbols._symboltable.TypeSymbol;
import org.codehaus.commons.nullanalysis.Nullable;
import org.codehaus.commons.nullanalysis.NotNull;

import java.util.*;
import java.util.stream.Collectors;

public class ComponentSymbol extends ComponentSymbolTOP {

  protected ComponentSymbol outerComponent;
  protected ComponentSymbolLoader parentComponent;
  protected List<FieldSymbol> parameters;
  protected List<TypeSymbol> typeParameters;

  /**
   * @param name the name of this component type.
   */
  protected ComponentSymbol(String name) {
    super(name);
    this.parameters = new ArrayList<>();
    this.typeParameters = new ArrayList<>();
  }

  @Override
  public void setSpannedScope(@NotNull IArcScope spannedScope) {
    Preconditions.checkArgument(spannedScope != null);
    super.setSpannedScope(spannedScope);
  }

  /**
   * @return a {@code List} of the inner component types of this component type. The {@code List}
   * is empty if this component type has no inner component types.
   */
  public List<ComponentSymbol> getInnerComponents() {
    return this.getSpannedScope().getLocalComponentSymbols();
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
  public Optional<ComponentSymbol> getInnerComponent(@NotNull String name) {
    Preconditions.checkArgument(name != null);
    return this.getSpannedScope().resolveComponentLocally(name);
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
  public List<ComponentSymbol> getInnerComponents(@NotNull AccessModifier visibility) {
    Preconditions.checkArgument(visibility != null);
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
  public Optional<ComponentSymbol> getOuterComponent() {
    return Optional.ofNullable(outerComponent);
  }

  /**
   * Sets the outer component type that contains this component type and subsequently states
   * whether this is an inner component type or not.
   *
   * @param outerComponent the component type that contains this component type.
   */
  public void setOuterComponent(@Nullable ComponentSymbol outerComponent) {
    this.outerComponent = outerComponent;
  }

  /**
   * @return an {@code Optional} of this component's parent component, or an empty {@code Optional},
   * if this component type has no parent component type.
   */
  public Optional<ComponentSymbolLoader> getParentComponent() {
    return Optional.ofNullable(this.parentComponent);
  }

  /**
   * @param parentComponent this component type's parent component type.
   */
  public void setParentComponent(@Nullable ComponentSymbolLoader parentComponent) {
    this.parentComponent = parentComponent;
  }

  /**
   * @return a {@code List} of the configuration parameters of this component type.
   */
  public List<FieldSymbol> getParameters() {
    return this.parameters;
  }

  /**
   * Adds the given configuration parameter to the configuration parameters of this component
   * type. Throws an {@link IllegalArgumentException} if the given symbol is {@code null}.
   *
   * @param parameter the symbol to add.
   */
  public void addParameter(@NotNull FieldSymbol parameter) {
    Preconditions.checkArgument(parameter != null);
    this.parameters.add(parameter);
    this.getSpannedScope().add(parameter);
  }

  /**
   * Adds the elements of a {@code Collection} of {@code FieldSymbol} of component configuration
   * parameters to the configuration parameters of this component type. Throws an
   * {@link IllegalArgumentException} if the given {@code Collection} is {@code null} or contains an
   * element that is {@code null}.
   *
   * @param parameters the symbols to add.
   * @see this#addParameter(FieldSymbol)
   */
  public void addParameters(@NotNull Collection<FieldSymbol> parameters) {
    Preconditions.checkArgument(parameters != null);
    Preconditions.checkArgument(!parameters.contains(null));
    for (FieldSymbol parameter : parameters) {
      this.addParameter(parameter);
    }
  }

  public boolean hasParameters() {
    return !this.getParameters().isEmpty();
  }

  /**
   * @return a {@code List} of the type parameters of this component type.
   */
  public List<TypeSymbol> getTypeParameters() {
    return this.typeParameters;
  }

  /**
   * Adds the given {@code ArcTypeParameterSymbol} of a component type parameter to the
   * type parameters of this component type. Throws an {@link IllegalArgumentException}
   * if the given symbol is {@code null}.
   *
   * @param typeParameter the symbol to add.
   */
  public void addTypeParameter(@NotNull TypeSymbol typeParameter) {
    Preconditions.checkArgument(typeParameter != null);
    this.typeParameters.add(typeParameter);
    this.getSpannedScope().add(typeParameter);
  }

  /**
   * Adds the elements of a {@code Collection} of {@code ArcTypeParameterSymbol} of component type
   * parameters to the type parameters of this component type. Throws an
   * {@link IllegalArgumentException} if the given {@code Collection} is {@code null} or contains an
   * element that is {@code null}.
   *
   * @param typeParameters the symbols to add.
   */
  public void addTypeParameters(@NotNull Collection<TypeSymbol> typeParameters) {
    Preconditions.checkArgument(typeParameters != null);
    Preconditions.checkArgument(!typeParameters.contains(null));
    for (TypeSymbol typeParameter : typeParameters) {
      this.addTypeParameter(typeParameter);
    }
  }

  public boolean hasTypeParameter() {
    return !this.getTypeParameters().isEmpty();
  }

  /**
   * @return a {@code List} of the fields of this component type.
   */
  public List<FieldSymbol> getFields() {
    return this.getSpannedScope().getLocalFieldSymbols();
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
  public Optional<FieldSymbol> getFields(@NotNull String name) {
    Preconditions.checkArgument(name != null);
    return this.getFields().stream().filter(field -> field.getName().equals(name)).findFirst();
  }

  /**
   * Returns the {@code List} of the ports of this component type. Does not include inherited ports.
   *
   * @return a {@code List} of the ports of this component type.
   */
  public List<PortSymbol> getPorts() {
    return this.getSpannedScope().getLocalPortSymbols();
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
  public Optional<PortSymbol> getPort(@NotNull String name) {
    Preconditions.checkArgument(name != null);
    return this.getSpannedScope().resolvePortLocally(name);
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
  public Optional<PortSymbol> getPort(@NotNull String name, boolean searchInherited) {
    Preconditions.checkArgument(name != null);
    Collection<PortSymbol> portsToConsider
      = searchInherited ? this.getAllPorts() : this.getPorts();
    return portsToConsider.stream().filter(p -> p.getName().equals(name)).findFirst();
  }

  /**
   * Returns the incoming ports of this component type. Does not include inherited ports.
   *
   * @return a {@code List} of incoming ports of this component type.
   */
  public List<PortSymbol> getIncomingPorts() {
    return this.getPorts(true);
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
  public List<PortSymbol> getIncomingPorts(@NotNull AccessModifier visibility) {
    Preconditions.checkArgument(visibility != null);
    return this.getIncomingPorts().stream().filter(p -> p.getAccessModifier().includes(visibility))
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
  public Optional<PortSymbol> getIncomingPort(@NotNull String name) {
    Preconditions.checkArgument(name != null);
    return this.getIncomingPort(name, false);
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
  public Optional<PortSymbol> getIncomingPort(@NotNull String name, boolean searchInherited) {
    Preconditions.checkArgument(name != null);
    Collection<PortSymbol> portsToConsider
      = searchInherited ? this.getAllIncomingPorts() : this.getIncomingPorts();
    return portsToConsider.stream().filter(p -> p.getName().equals(name)).findFirst();
  }

  /**
   * Returns the outgoing ports of this component type. Does not include inherited ports.
   *
   * @return a {@code List} of the outgoing ports of this component type.
   */
  public List<PortSymbol> getOutgoingPorts() {
    return this.getPorts(false);
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
  public List<PortSymbol> getOutgoingPorts(@NotNull AccessModifier visibility) {
    Preconditions.checkArgument(visibility != null);
    return this.getOutgoingPorts().stream().filter(p -> p.getAccessModifier().includes(visibility))
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
  public Optional<PortSymbol> getOutgoingPort(@NotNull String name) {
    Preconditions.checkArgument(name != null);
    return this.getOutgoingPort(name, false);
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
  public Optional<PortSymbol> getOutgoingPort(@NotNull String name, boolean searchInherited) {
    Preconditions.checkArgument(name != null);
    Collection<PortSymbol> portsToConsider
      = searchInherited ? this.getAllOutgoingPorts() : this.getOutgoingPorts();
    return portsToConsider.stream().filter(p -> p.getName().equals(name)).findFirst();
  }

  /**
   * Returns the ports of this component type that have the given direction. Does not included
   * ports inherited from parent component types.
   *
   * @param isIncoming the direction of the ports.
   * @return a {@code List} of ports of this component type that have the given direction.
   */
  protected List<PortSymbol> getPorts(boolean isIncoming) {
    return this.getPorts().stream().filter(p -> p.isIncoming() == isIncoming)
      .collect(Collectors.toList());
  }

  /**
   * Return all ports of this component type, including inherited port from all parent
   * components types. NameSpaceHiding is considered and therefore hidden ports are not returned.
   *
   * @return a {@code List} of all ports of this component type.
   */
  public List<PortSymbol> getAllPorts() {
    List<PortSymbol> result = getPorts();
    if (getParentComponent().isPresent()) {
      List<PortSymbol> inheritedPorts = new ArrayList<>();
      for (PortSymbol port : this.getParentComponent().get().getLoadedSymbol().getAllPorts()) {
        if (result.stream().anyMatch(p -> p.getName().equals(port.getName()))) {
          inheritedPorts.add(port);
        }
      }
      result.addAll(inheritedPorts);
    }
    return result;
  }

  /**
   * Returns all incoming ports of this component type, including inherited ports from all parent
   * component types. NameSpaceHiding is considered and therefore hidden ports are not returned.
   *
   * @return a {@code List} of all incoming ports of this component type.
   */
  public List<PortSymbol> getAllIncomingPorts() {
    return this.getAllPorts(true);
  }

  /**
   * Returns all outgoing ports of this component type, including inherited ports from all parent
   * component types. NameSpaceHiding is considered and therefore hidden ports are not returned.
   *
   * @return a {@code List} of all outgoing ports of this component type.
   */
  public List<PortSymbol> getAllOutgoingPorts() {
    return this.getAllPorts(false);
  }

  /**
   * Returns all ports of this component type that have the given direction, including inherited
   * ports from all parent components types. NameSpaceHiding is considered and therefore hidden
   * ports are not returned.
   *
   * @param isIncoming the direction of the ports.
   * @return a {@code List} of all ports of this component type that have the given direction.
   */
  protected List<PortSymbol> getAllPorts(boolean isIncoming) {
    return this.getAllPorts().stream().filter(p -> p.isIncoming() == isIncoming)
      .collect(Collectors.toList());
  }

  public boolean hasPorts() {
    return !this.getPorts().isEmpty();
  }

  /**
   * @return a {@code List} of the subcomponent instances of this component type.
   */
  public List<ComponentInstanceSymbol> getSubComponents() {
    return this.getSpannedScope().getLocalComponentInstanceSymbols();
  }

  /**
   * Returns the subcomponent instances of this component type that have the given visibility.
   *
   * @param visibility the visibility of the subcomponent instances
   * @return a {@code List} of the subcomponent instances of this component type that have the
   * given visibility.
   */
  public List<ComponentInstanceSymbol> getSubComponents(AccessModifier visibility) {
    return this.getSubComponents().stream().filter(c -> c.getAccessModifier().includes(visibility))
      .collect(Collectors.toList());
  }

  /**
   * @param name the name of the subcomponent instance.
   * @return an {@code Optional} of a subcomponent instance of this component type that has the
   * given name or an empty {@code Optional} if such a subcomponent instance does not exist.
   */
  public Optional<ComponentInstanceSymbol> getSubComponent(@NotNull String name) {
    Preconditions.checkArgument(name != null);
    return this.getSubComponents().stream().filter(c -> c.getName().equals(name)).findFirst();
  }

  public boolean isDecomposed() {
    return !this.getSubComponents().isEmpty();
  }

  public boolean isAtomic() {
    return this.getSubComponents().isEmpty();
  }
}