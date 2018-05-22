/*
 * Copyright (c) 2015 RWTH Aachen. All rights reserved.
 *
 * http://www.se-rwth.de/
 */
package montiarc._symboltable;

import static com.google.common.base.Preconditions.checkArgument;
import static de.monticore.symboltable.Symbols.sortSymbolsByPosition;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

import de.monticore.symboltable.CommonScopeSpanningSymbol;
import de.monticore.symboltable.ImportStatement;
import de.monticore.symboltable.SymbolKind;
import de.monticore.symboltable.modifiers.AccessModifier;
import de.monticore.symboltable.types.JFieldSymbol;
import de.monticore.symboltable.types.JTypeSymbol;
import de.se_rwth.commons.logging.Log;
import montiarc.MontiArcConstants;
import montiarc.helper.SymbolPrinter;
import montiarc.helper.Timing;

//XXX: https://git.rwth-aachen.de/montiarc/core/issues/49

/**
 * Symbol for component definitions.
 *
 * @author Robert Heim
 */
public class ComponentSymbol extends CommonScopeSpanningSymbol {

  public static final ComponentKind KIND = new ComponentKind();

  private final Map<String, Optional<String>> stereotype = new HashMap<>();

  private boolean isInnerComponent = false;

  private Optional<ComponentSymbolReference> superComponent = Optional.empty();

  private Timing timing = MontiArcConstants.DEFAULT_TIME_PARADIGM;

  private boolean delayed = false;

  // when "this" not actually is a component, but a reference to a component, this optional
  // attribute is set by the symbol-table creator to the referenced component and must be used for
  // implementation.
  private Optional<ComponentSymbol> referencedComponent = Optional.empty();

  private List<ImportStatement> imports;

  public ComponentSymbol(String name) {
    super(name, KIND);
  }

  public ComponentSymbol(String name, SymbolKind kind) {
    super(name, kind);
  }

  /**
   * @return referencedComponent
   */
  public Optional<ComponentSymbol> getReferencedComponent() {
    return this.referencedComponent;
  }

  /**
   * @param referencedComponent the referencedComponent to set
   */
  public void setReferencedComponent(Optional<ComponentSymbol> referencedComponent) {
    this.referencedComponent = referencedComponent;
  }

  /**
   * @param parameterType configuration parameter to add
   */
  public void addConfigParameter(JFieldSymbol parameterType) {
    if (referencedComponent.isPresent())
      referencedComponent.get().addConfigParameter(parameterType);
    else {
      Log.errorIfNull(parameterType);
      checkArgument(parameterType.isParameter(), "Only parameters can be added.");
      getMutableSpannedScope().add(parameterType);
    }
  }

  /**
   * @param target target of the connector to get
   * @return a connector with the given target, absent optional, if it does not exist
   */
  public Optional<ConnectorSymbol> getConnector(String target) {
    // no check for reference required
    for (ConnectorSymbol con : getConnectors()) {
      if (con.getTarget().equals(target)) {
        return Optional.of(con);
      }
    }
    return Optional.empty();
  }

  /**
   * @return connectors of this component
   */
  public Collection<ConnectorSymbol> getConnectors() {
    return referencedComponent.orElse(this)
        .getSpannedScope().<ConnectorSymbol>resolveLocally(ConnectorSymbol.KIND);
  }

  /**
   * @param visibility visibility
   * @return connectors with the given visibility
   */
  public Collection<ConnectorSymbol> getConnectors(AccessModifier visibility) {
    // no check for reference required
    return getConnectors().stream()
        .filter(c -> c.getAccessModifier().includes(visibility))
        .collect(Collectors.toList());
  }

  /**
   * Checks, if this component has a connector with the given receiver name.
   *
   * @param receiver name of the receiver to find a connector for
   * @return true, if this component has a connector with the given receiver name, else false.
   */
  public boolean hasConnector(String receiver) {
    // no check for reference required
    return getConnectors().stream()
        .filter(c -> c.getName().equals(receiver))
        .findAny().isPresent();
  }

  /**
   * Checks, if this component has one or more connectors with the given sender.
   *
   * @param sender name of the sender to find a connector for
   * @return true, if this component has one ore more connectors with the given sender name, else
   * false.
   */
  public boolean hasConnectors(String sender) {
    // no check for reference required
    return getConnectors().stream()
        .filter(c -> c.getSource().equals(sender))
        .findAny().isPresent();
  }

  /**
   * @return innerComponents
   */
  public Collection<ComponentSymbol> getInnerComponents() {
    return referencedComponent.orElse(this).getSpannedScope()
        .<ComponentSymbol>resolveLocally(ComponentSymbol.KIND);
  }

  /**
   * @param name inner component name
   * @return inner component with the given name, empty Optional, if it does not exist
   */
  public Optional<ComponentSymbol> getInnerComponent(String name) {
    // no check for reference required
    return getInnerComponents().stream()
        .filter(c -> c.getName().equals(name))
        .findFirst();
  }

  /**
   * @param visibility visibility
   * @return inner components with the given visibility
   */
  public Collection<ComponentSymbol> getInnerComponents(AccessModifier visibility) {
    // no check for reference require
    return getInnerComponents().stream()
        .filter(s -> s.getAccessModifier().includes(visibility))
        .collect(Collectors.toList());
  }

  /**
   * @return true, if this is an inner component, else false.
   */
  public boolean isInnerComponent() {
    return referencedComponent.orElse(this).isInnerComponent;
  }

  /**
   * Sets, if this is an inner component or not.
   *
   * @param isInnerComponent true, if this is an inner component
   */
  public void setIsInnerComponent(boolean isInnerComponent) {
    referencedComponent.orElse(this).isInnerComponent = isInnerComponent;
  }

  /**
   * @param typeParameter generic type parameter to add
   */
  public void addFormalTypeParameter(JTypeSymbol formalTypeParameter) {
    if (referencedComponent.isPresent()) {
      referencedComponent.get().addFormalTypeParameter(formalTypeParameter);
    }
    else {
      checkArgument(formalTypeParameter.isFormalTypeParameter());
      getMutableSpannedScope().add(formalTypeParameter);
    }
  }

  public List<JTypeSymbol> getFormalTypeParameters() {
    final Collection<JTypeSymbol> resolvedTypes = referencedComponent.orElse(this).getSpannedScope()
        .resolveLocally(JTypeSymbol.KIND);
    return resolvedTypes.stream().filter(JTypeSymbol::isFormalTypeParameter)
        .collect(Collectors.toList());
  }

  public boolean hasFormalTypeParameters() {
    return !getFormalTypeParameters().isEmpty();
  }

  public boolean hasConfigParameters() {
    return !getConfigParameters().isEmpty();
  }

  public boolean hasPorts() {
    return !getPorts().isEmpty();
  }

  /**
   * Ports of this component.
   *
   * @return ports of this component.
   */
  public Collection<PortSymbol> getPorts() {
    return referencedComponent.orElse(this).getSpannedScope()
        .<PortSymbol>resolveLocally(PortSymbol.KIND);
  }

  /**
   * Finds a port with a given name.
   *
   * @param name Name of the searched port
   * @return {@link PortSymbol} with the given name. {@link Optional#empty()} if not found
   */
  public Optional<PortSymbol> getPort(String name) {
    return getPorts().stream()
        .filter(p -> p.getName().equals(name))
        .findFirst();
  }

  /**
   * @return incomingPorts of this component
   */
  public Collection<PortSymbol> getIncomingPorts() {
    return referencedComponent.orElse(this).getSpannedScope()
        .<PortSymbol>resolveLocally(PortSymbol.KIND)
        .stream()
        .filter(p -> p.isIncoming())
        .collect(Collectors.toList());
  }

  /**
   * @param name port name
   * @return incoming port with the given name, empty optional, if it does not exist
   */
  public Optional<PortSymbol> getIncomingPort(String name) {
    // no check for reference required
    return getIncomingPorts().stream()
        .filter(p -> p.getName().equals(name))
        .findFirst();
  }

  /**
   * @param visibility
   * @return incoming ports with the given visibility
   */
  public Collection<PortSymbol> getIncomingPorts(AccessModifier visibility) {
    // no check for reference required
    return getIncomingPorts().stream()
        .filter(s -> s.getAccessModifier().includes(visibility))
        .collect(Collectors.toList());
  }

  /**
   * @return outgoingPorts of this component
   */
  public Collection<PortSymbol> getOutgoingPorts() {
    return referencedComponent.orElse(this).getSpannedScope()
        .<PortSymbol>resolveLocally(PortSymbol.KIND)
        .stream()
        .filter(p -> p.isOutgoing())
        .collect(Collectors.toList());
  }

  /**
   * Returns a list of all incoming ports that also contains ports from a super component.
   *
   * @param loader        used to load full version of super component (if needed)
   * @param deserializers used to load full version of super component (if needed)
   * @return list of all incoming ports.
   */
  public List<PortSymbol> getAllIncomingPorts() {
    return referencedComponent.orElse(this).getAllPorts(true);
  }

  /**
   * @param name port name
   * @return outgoing port with the given name, empty optional, if it does not exist
   */
  public Optional<PortSymbol> getOutgoingPort(String name) {
    // no check for reference required
    return getOutgoingPorts().stream()
        .filter(p -> p.getName().equals(name))
        .findFirst();
  }

  /**
   * @param visibility visibility
   * @return outgoing ports with the given visibility
   */
  public Collection<PortSymbol> getOutgogingPorts(AccessModifier visibility) {
    // no check for reference required
    return getOutgoingPorts().stream()
        .filter(s -> s.getAccessModifier().includes(visibility))
        .collect(Collectors.toList());
  }

  /**
   * Returns a list of all outgoing ports that also contains ports from a super component.
   *
   * @param loader        used to load full version of super component (if needed)
   * @param deserializers used to load full version of super component (if needed)
   * @return list of all outgoing ports.
   */
  public List<PortSymbol> getAllOutgoingPorts() {
    return referencedComponent.orElse(this).getAllPorts(false);
  }

  protected List<PortSymbol> getAllPorts() {
    List<PortSymbol> result = new ArrayList<PortSymbol>();

    // own ports
    result.addAll(getPorts());

    // ports from super components
    Optional<ComponentSymbolReference> superCompOpt = getSuperComponent();
    if (superCompOpt.isPresent()) {
      for (PortSymbol superPort : superCompOpt.get().getAllPorts()) {
        boolean alreadyAdded = false;
        for (PortSymbol pToAdd : result) {
          if (pToAdd.getName().equals(superPort.getName())) {
            alreadyAdded = true;
            break;
          }
        }
        if (!alreadyAdded) {
          result.add(superPort);
        }
      }
    }
    return result;
  }

  private List<PortSymbol> getAllPorts(boolean isIncoming) {
    return getAllPorts().stream().filter(p -> p.isIncoming() == isIncoming)
        .collect(Collectors.toList());
  }

  public Collection<VariableSymbol> getVariables() {
    return referencedComponent.orElse(this).getSpannedScope()
        .<VariableSymbol>resolveLocally(VariableSymbol.KIND);
  }

  public Optional<VariableSymbol> getVariable(String name) {
    return getVariables().stream().filter(v -> v.getName().equals(name)).findFirst();
  }

  /**
   * @return super component of this component, empty optional, if it does not have a super
   * component
   */
  public Optional<ComponentSymbolReference> getSuperComponent() {
    if (referencedComponent.isPresent()) {
      return referencedComponent.get().getSuperComponent();
    }
    else {
      return superComponent;
    }
  }

  /**
   * @param superComponent the super component to set
   */
  public void setSuperComponent(Optional<ComponentSymbolReference> superComponent) {
    referencedComponent.orElse(this).superComponent = superComponent;
  }

  /**
   * @return subComponents
   */
  public Collection<ComponentInstanceSymbol> getSubComponents() {
    return referencedComponent.orElse(this).getSpannedScope()
        .resolveLocally(ComponentInstanceSymbol.KIND);
  }

  /**
   * @param name subcomponent instance name
   * @return subcomponent with the given name, empty optional, if it does not exist
   */
  public Optional<ComponentInstanceSymbol> getSubComponent(String name) {
    // no check for reference required
    return getSubComponents().stream()
        .filter(p -> p.getName().equals(name))
        .findFirst();
  }

  /**
   * @param visibility visibility
   * @return subcomponents with the given visibility
   */
  public Collection<ComponentInstanceSymbol> getSubComponents(AccessModifier visibility) {
    // no check for reference required
    return getSubComponents().stream()
        .filter(s -> s.getAccessModifier().includes(visibility))
        .collect(Collectors.toList());
  }

  /**
   * @return configParameters
   */
  public List<JFieldSymbol> getConfigParameters() {
    if (referencedComponent.isPresent()) {
      return referencedComponent.get().getConfigParameters();
    }
    else {
      final Collection<JFieldSymbol> resolvedAttributes = getSpannedScope()
          .resolveLocally(JFieldSymbol.KIND);
      final List<JFieldSymbol> parameters = sortSymbolsByPosition(resolvedAttributes.stream()
          .filter(JFieldSymbol::isParameter).collect(Collectors.toList()));
      return parameters;
    }
  }

  /**
   * @return List of configuration parameters that are to be set during instantiation with the given
   * visibility
   */
  public Collection<JFieldSymbol> getConfigParameters(AccessModifier visibility) {
    // no need to check for reference, as getParameres() does so.
    return getConfigParameters().stream()
        .filter(s -> s.getAccessModifier().includes(visibility))
        .collect(Collectors.toList());
  }

  /**
   * Sets, if the component has a delay.
   *
   * @param delayed true, if the component has a delay, else false.
   */
  public void setDelayed(boolean delayed) {
    referencedComponent.orElse(this).delayed = delayed;
  }

  /**
   * @return true, if the component has a delay, else false.
   */
  public boolean hasDelay() {
    return referencedComponent.orElse(this).delayed;
  }

  /**
   * Adds the stereotype key=value to this entry's map of stereotypes
   *
   * @param key      the stereotype's key
   * @param optional the stereotype's value
   */
  public void addStereotype(String key, Optional<String> optional) {
    referencedComponent.orElse(this).stereotype.put(key, optional);
  }

  /**
   * @return map representing the stereotype of this component
   */
  public Map<String, Optional<String>> getStereotype() {
    return referencedComponent.orElse(this).stereotype;
  }

  /**
   * @return the timing
   */
  public Timing getBehaviorKind() {
    return referencedComponent.orElse(this).timing;
  }

  /**
   * @param timing the timing to set
   */
  public void setBehaviorKind(Timing behaviorKind) {
    referencedComponent.orElse(this).timing = behaviorKind;
    if (behaviorKind.isDelaying()) {
      referencedComponent.orElse(this).setDelayed(true);
    }
  }

  public boolean isDecomposed() {
    return !isAtomic();
  }

  public boolean isAtomic() {
    return getSubComponents().isEmpty();
  }

  @Override
  public String toString() {
    return SymbolPrinter.printComponent(this);
  }

  /**
   * TODO reuse ArtifactScope? see TODO in {@link #setImports(List)}
   *
   * @return imports
   */
  public List<ImportStatement> getImports() {
    return this.imports;
  }

  /**
   * TODO could we get these somehow from the ArtifactScope? there the imports are private, but we
   * want (some?) imports to be printed in a generated java file, when e.g. aggregated with Java and
   * other Java-types are referenced.
   *
   * @param imports
   */
  public void setImports(List<ImportStatement> imports) {
    this.imports = imports;
  }

}
