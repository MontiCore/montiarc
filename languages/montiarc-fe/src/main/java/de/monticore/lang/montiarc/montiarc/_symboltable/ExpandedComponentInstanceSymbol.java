package de.monticore.lang.montiarc.montiarc._symboltable;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import de.monticore.lang.montiarc.helper.SymbolPrinter;
import de.monticore.lang.montiarc.tagging._symboltable.TaggingScopeSpanningSymbol;
import de.monticore.symboltable.types.references.ActualTypeArgument;

/**
 * @author Michael von Wenckstern
 *         Created by Michael von Wenckstern on 23.05.2016.
 *         The aim of this class is to have real component instances<br>
 *         component A {
 *         component B<Integer> b1;
 *         component B<Double> b2;
 *         }
 *         component B<T> {
 *         component C<T> c1, c2;
 *         }
 *         component C<T> {
 *         ports in T a,
 *         in T b,
 *         out T c;
 *         }
 *         after expanding the component's definitions we will get the following instance:
 *         component instance A {
 *         component instance b1 {
 *         component instance c1 {
 *         ports in Integer a,
 *         ports in Integer b,
 *         ports out Integer c;
 *         }
 *         component instance c2 {
 *         ports in Integer a,
 *         ports in Integer b,
 *         ports out Integer c;
 *         }
 *         }
 *         component instance b2 {
 *         component instance c1 {
 *         ports in Double a,
 *         ports in Double b,
 *         ports out Double c;
 *         }
 *         component instance c2 {
 *         ports in Double a,
 *         ports in Double b,
 *         ports out Double c;
 *         }
 *         }
 *         }
 *         These instances are important for:
 *         * executing the simulation order later on (to be compatible with industry, Simulink:
 *         they only care about the atomic elements and give them an execution order.
 *         In this example these would be the four elements: A.b1.c1, A.b1.c2, A.b2.c1, A.b2.c2
 *         *  for tagging instances differently (e.g. A.b1.c1 may be deployed on another processor
 *         than A.b2.c1)
 *         *  different C%C analysis techniques, e.g. Control-Flow-Analysis (need to differentiate
 *         between instances)
 *         This class is the basic class for instances so that you can resolve them using the
 *         standard symbol table mechanism
 */
public class ExpandedComponentInstanceSymbol
    extends TaggingScopeSpanningSymbol {

  public static final ExpandedComponentInstanceKind KIND = new ExpandedComponentInstanceKind();

  protected ComponentSymbolReference type;
  protected List<ActualTypeArgument> actualTypeArguments = new ArrayList<>();

  /**
   * use {@link #builder()}
   */
  protected ExpandedComponentInstanceSymbol(String name, ComponentSymbolReference type) {
    super(name, KIND);
    this.type = type;
  }

  public static ExpandedComponentInstanceBuilder builder() {
    return new ExpandedComponentInstanceBuilder();
  }

  /**
   * this is only needed as temp variable to derive generics
   */
  @Deprecated
  public List<ActualTypeArgument> getActualTypeArguments() {
    return actualTypeArguments;
  }

  public void setActualTypeArguments(List<ActualTypeArgument> actualTypeArguments) {
    this.actualTypeArguments = actualTypeArguments;
  }

  public ComponentSymbolReference getComponentType() {
    return type;
  }

  public boolean hasPorts() {
    return !getPorts().isEmpty();
  }

  /**
   * ExpandedComponentInstanceSymbol::getPorts() may return different
   * results than ComponentSymbol::getPorts()
   * "MontiArc provides a structural inheritance mechanism that allows to define a component as
   * an extension of another component type (see requirement LRQ1.1.1). The new type inherits the
   * interface as well as the architectural configuration from the supercomponent. Thus, all ports,
   * inner component type definitions, subcomponents, and connectors are inherited." (p. 42, Ph.D. AH)
   */
  public Collection<PortSymbol> getPorts() {
    return getSpannedScope().<PortSymbol>resolveLocally(PortSymbol.KIND);
  }

  public Optional<PortSymbol> getPort(String name) {
    return getSpannedScope().resolveLocally(name, PortSymbol.KIND);
  }

  public Collection<PortSymbol> getIncomingPorts() {
    return getPorts().stream().filter(PortSymbol::isIncoming).collect(Collectors.toList());
  }

  public Collection<PortSymbol> getOutgoingPorts() {
    return getPorts().stream().filter(PortSymbol::isOutgoing).collect(Collectors.toList());
  }

  /**
   * ExpandedComponentInstanceSymbol::getSubComponents() may return different
   * results than the union of ComponentSymbol::getSubComponents() and
   * ComponentSymbol::getInnerComponents.
   * "MontiArc provides a structural inheritance mechanism that allows to define a component as
   * an extension of another component type (see requirement LRQ1.1.1). The new type inherits the
   * interface as well as the architectural configuration from the supercomponent. Thus, all ports,
   * inner component type definitions, subcomponents, and connectors are inherited." (p. 42, Ph.D. AH)
   */
  public Collection<ExpandedComponentInstanceSymbol> getSubComponents() {
    return getSpannedScope().<ExpandedComponentInstanceSymbol>resolveLocally(ExpandedComponentInstanceSymbol.KIND);
  }

  public Optional<ExpandedComponentInstanceSymbol> getSubComponent(String name) {
    return getSpannedScope().<ExpandedComponentInstanceSymbol>resolveLocally(name, ExpandedComponentInstanceSymbol.KIND);
  }

  /**
   * ExpandedComponentInstanceSymbol::getPorts() may return different
   * results than ComponentSymbol::getPorts()
   * "MontiArc provides a structural inheritance mechanism that allows to define a component as
   * an extension of another component type (see requirement LRQ1.1.1). The new type inherits the
   * interface as well as the architectural configuration from the supercomponent. Thus, all ports,
   * inner component type definitions, subcomponents, and connectors are inherited." (p. 42, Ph.D. AH)
   */
  public Collection<ConnectorSymbol> getConnectors() {
    return getSpannedScope().<ConnectorSymbol>resolveLocally(ConnectorSymbol.KIND);
  }

  @Override
  public String toString() {
    return SymbolPrinter.printExpandedComponentInstance(this);
  }
}
