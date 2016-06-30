/*
 * Copyright (c) 2015 RWTH Aachen. All rights reserved.
 *
 * http://www.se-rwth.de/
 */

package de.monticore.lang.montiarc.montiarc._symboltable;

import javax.annotation.Nullable;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

import de.monticore.lang.montiarc.helper.SymbolPrinter;
import de.monticore.lang.montiarc.tagging._symboltable.TaggingSymbol;
import de.monticore.symboltable.types.JTypeSymbol;
import de.monticore.symboltable.types.references.JTypeReference;

/**
 * Symboltable entry for ports.
 *
 * @author Arne Haber, Robert Heim
 */
public class PortSymbol extends TaggingSymbol {
  public static final PortKind KIND = PortKind.INSTANCE;

  private final Map<String, Optional<String>> stereotype = new HashMap<>();

  /**
   * Maps direction incoming to true.
   */
  public static final boolean INCOMING = true;

  /**
   * Flags, if this port is incoming.
   */
  private boolean incoming;

  private JTypeReference<? extends JTypeSymbol> typeReference;

  /**
   * use {@link #builder()}
   */
  protected PortSymbol(String name) {
    super(name, KIND);
  }

  public static PortBuilder builder() {
    return new PortBuilder();
  }

  /**
   * @param isIncoming incoming = true, outgoing = false
   */
  public void setDirection(boolean isIncoming) {
    incoming = isIncoming;
  }

  /**
   * @return true, if this is an incoming port, else false.
   */
  public boolean isIncoming() {
    return incoming;
  }

  /**
   * @return true, if this is an outgoing port, else false.
   */
  public boolean isOutgoing() {
    return !isIncoming();
  }

  /**
   * @return typeReference reference to the type from this port
   */
  public JTypeReference<? extends JTypeSymbol> getTypeReference() {
    return this.typeReference;
  }

  /**
   * @param typeReference reference to the type from this port
   */
  public void setTypeReference(JTypeReference<? extends JTypeSymbol> typeReference) {
    this.typeReference = typeReference;
  }

  /**
   * returns the component which defines the connector
   * this is independent from the component to which the source and target ports
   * belong to
   *
   * @return is optional, b/c a connector can belong to a component symbol or to
   * an expanded component instance symbol
   */
  public Optional<ComponentSymbol> getComponent() {
    if (!this.getEnclosingScope().getSpanningSymbol().isPresent()) {
      return Optional.empty();
    }
    if (!(this.getEnclosingScope().getSpanningSymbol().get() instanceof ComponentSymbol)) {
      return Optional.empty();
    }
    return Optional.of((ComponentSymbol) this.getEnclosingScope().getSpanningSymbol().get());
  }

  /**
   * returns the expanded component instance which defines the connector
   * this is independent from the component to which the source and target ports
   * belong to
   *
   * @return is optional, b/c a connector can belong to a component symbol or to
   * an expanded component instance symbol
   */
  public Optional<ExpandedComponentInstanceSymbol> getComponentInstance() {
    if (!this.getEnclosingScope().getSpanningSymbol().isPresent()) {
      return Optional.empty();
    }
    if (!(this.getEnclosingScope().getSpanningSymbol().get() instanceof ExpandedComponentInstanceSymbol)) {
      return Optional.empty();
    }
    return Optional.of((ExpandedComponentInstanceSymbol) this.getEnclosingScope().getSpanningSymbol().get());
  }

  /**
   * Adds the stereotype key=value to this entry's map of stereotypes
   *
   * @param key      the stereotype's key
   * @param optional the stereotype's value
   */
  public void addStereotype(String key, Optional<String> optional) {
    stereotype.put(key, optional);
  }

  /**
   * Adds the stereotype key=value to this entry's map of stereotypes
   *
   * @param key   the stereotype's key
   * @param value the stereotype's value
   */
  public void addStereotype(String key, @Nullable String value) {
    if (value != null && value.isEmpty()) {
      value = null;
    }
    stereotype.put(key, Optional.ofNullable(value));
  }

  /**
   * @return map representing the stereotype of this component
   */
  public Map<String, Optional<String>> getStereotype() {
    return stereotype;
  }

  @Override
  public String toString() {
    return SymbolPrinter.printPort(this);
  }
}
