package montiarc.trafos;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import de.monticore.symboltable.MutableScope;
import de.monticore.symboltable.Symbol;
import de.monticore.symboltable.resolving.ResolvingFilter;
import de.monticore.symboltable.types.JTypeSymbol;
import de.monticore.symboltable.types.references.ActualTypeArgument;
import de.monticore.symboltable.types.references.JTypeReference;
import de.se_rwth.commons.logging.Log;
import montiarc._symboltable.ComponentSymbolReference;
import montiarc._symboltable.ConnectorSymbol;
import montiarc._symboltable.PortBuilder;
import montiarc._symboltable.PortSymbol;

/**
 * Created by Michael von Wenckstern on 23.05.2016.
 */
public class ExpandedComponentInstanceBuilder {
  protected Optional<String> name = Optional.empty();
  protected Optional<ComponentSymbolReference> symbolReference = Optional.empty();
  protected List<PortSymbol> ports = new ArrayList<>();
  protected List<ExpandedComponentInstanceSymbol> subComponents = new ArrayList<>();
  protected List<ConnectorSymbol> connectors = new ArrayList<>();
  protected Set<ResolvingFilter> resolvingFilters = new LinkedHashSet<>();
  //             FormalTypeParameter, ActualTypeArgument (is the binding of formal parameters
  protected Map<JTypeSymbol, ActualTypeArgument> actualTypeArguments = new LinkedHashMap<>();

  protected static Map<JTypeSymbol, ActualTypeArgument> createMap(List<JTypeSymbol> keys, List<ActualTypeArgument> values) {
    Map<JTypeSymbol, ActualTypeArgument> ret = new LinkedHashMap<>();
    for (int i = 0; i < keys.size(); i++) {
      ret.put(keys.get(i), values.get(i));
    }
    return ret;
  }

  private static ConnectorSymbol cloneConnector(ConnectorSymbol s) {
    return new ConnectorSymbol(s.getSource(), s.getTarget());
  }

  public static ExpandedComponentInstanceSymbol clone(ExpandedComponentInstanceSymbol inst) {
    return new ExpandedComponentInstanceBuilder().setName(inst.getName())
        .setSymbolReference(inst.getComponentType())
        //.addPorts(inst.getPorts().stream().map(p -> PortBuilder.clone(p)).collect(Collectors.toList()))
        .addPorts(inst.getPorts()) // is cloned in build method
        .addConnectors(inst.getConnectors().stream().map(c -> cloneConnector(c)).collect(Collectors.toList()))
        .addSubComponents(inst.getSubComponents().stream().map(s -> ExpandedComponentInstanceBuilder.clone(s)).collect(Collectors.toList()))
        .build();
  }

  public ExpandedComponentInstanceBuilder addResolvingFilter(ResolvingFilter filter) {
    this.resolvingFilters.add(filter);
    return this;
  }

  public ExpandedComponentInstanceBuilder addResolvingFilters(Set<ResolvingFilter<? extends Symbol>> filters) {
    for (ResolvingFilter filter : filters) {
      this.addResolvingFilter(filter);
    }
    return this;
  }

  public ExpandedComponentInstanceBuilder setName(String name) {
    this.name = Optional.of(name);
    return this;
  }

  public ExpandedComponentInstanceBuilder setSymbolReference(ComponentSymbolReference symbolReference) {
    this.symbolReference = Optional.of(symbolReference);
    return this;
  }

  public ExpandedComponentInstanceBuilder addPort(PortSymbol port) {
    this.ports.add(port);
    return this;
  }

  public ExpandedComponentInstanceBuilder addPorts(PortSymbol... ports) {
    for (PortSymbol p : ports) {
      this.addPort(p);
    }
    return this;
  }

  public ExpandedComponentInstanceBuilder addPorts(Collection<PortSymbol> ports) {
    ports.stream().forEachOrdered(p -> this.addPort(p));
    return this;
  }

  public ExpandedComponentInstanceBuilder addActualTypeArgument(JTypeSymbol formalTypeParameter, ActualTypeArgument typeArgument) {
    this.actualTypeArguments.put(formalTypeParameter, typeArgument);
    return this;
  }

  public ExpandedComponentInstanceBuilder addActualTypeArguments(List<JTypeSymbol> formalTypeParameters, List<ActualTypeArgument> actualTypeArguments) {
    if (formalTypeParameters.size() != actualTypeArguments.size()) {
      Log.debug("instance has not as many actual type arguments as component definition has formal type parameters. No mapping is possible. Function does nothing.",
          ExpandedComponentInstanceBuilder.class.toString());
    }
    else {
      for (int i = 0; i < formalTypeParameters.size(); i++) {
        this.addActualTypeArgument(formalTypeParameters.get(i), actualTypeArguments.get(i));
      }
    }
    return this;
  }

  public ExpandedComponentInstanceBuilder addPortsIfNameDoesNotExists(Collection<PortSymbol> ports) {
    List<String> existingPortNames = this.ports.stream().map(p -> p.getName())
        .collect(Collectors.toList());
    this.addPorts(ports.stream().filter(p ->
        !existingPortNames.contains(p.getName()))
        .collect(Collectors.toList()));
    return this;
  }

  /**
   * adds ports if they do not exist and replace generics of ports
   */
  public ExpandedComponentInstanceBuilder addPortsIfNameDoesNotExists(Collection<PortSymbol> ports, List<JTypeSymbol> formalTypeParameters, List<ActualTypeArgument> actualTypeArguments) {
    List<PortSymbol> pList = ports.stream().collect(Collectors.toList());
    createMap(formalTypeParameters, actualTypeArguments).forEach((k, v) ->
        ports.stream().filter(p -> p.getTypeReference().getReferencedSymbol().getName().equals(k.getName()))
            .forEachOrdered(p -> {
              PortSymbol pCloned = PortBuilder.clone(p);
              pCloned.setTypeReference((JTypeReference<? extends JTypeSymbol>) v.getType());
              Collections.replaceAll(pList, p, pCloned);
            })
    );
    this.addPortsIfNameDoesNotExists(pList);
    return this;
  }

  public ExpandedComponentInstanceBuilder addSubComponent(ExpandedComponentInstanceSymbol subCmp) {
    this.subComponents.add(subCmp);
    return this;
  }

  public ExpandedComponentInstanceBuilder addSubComponentIfNameDoesNotExists(ExpandedComponentInstanceSymbol subCmp) {
    List<String> existingSubComponentNames = this.subComponents.stream().map(s -> s.getName())
        .collect(Collectors.toList());
    if (!existingSubComponentNames.contains(subCmp.getName())) {
      this.addSubComponent(subCmp);
    }
    return this;
  }

  public ExpandedComponentInstanceBuilder addSubComponents(ExpandedComponentInstanceSymbol... subCmps) {
    for (ExpandedComponentInstanceSymbol s : subCmps) {
      this.addSubComponent(s);
    }
    return this;
  }

  public ExpandedComponentInstanceBuilder addSubComponents(Collection<ExpandedComponentInstanceSymbol> subCmps) {
    subCmps.stream().forEachOrdered(s -> this.addSubComponent(s));
    return this;
  }

  public ExpandedComponentInstanceBuilder addSubComponentsIfNameDoesNotExists(Collection<ExpandedComponentInstanceSymbol> subCmps) {
    List<String> existingSubComponentNames = this.subComponents.stream().map(s -> s.getName())
        .collect(Collectors.toList());
    this.addSubComponents(subCmps.stream().filter(s ->
        !existingSubComponentNames.contains(s.getName()))
        .collect(Collectors.toList()));
    return this;
  }

  public ExpandedComponentInstanceBuilder addConnector(ConnectorSymbol connector) {
    this.connectors.add(connector);
    return this;
  }

  public ExpandedComponentInstanceBuilder addConnectors(ConnectorSymbol... connectors) {
    for (ConnectorSymbol c : connectors) {
      this.addConnector(c);
    }
    return this;
  }

  public ExpandedComponentInstanceBuilder addConnectors(Collection<ConnectorSymbol> connectors) {
    connectors.stream().forEachOrdered(c -> this.addConnector(c));
    return this;
  }

  protected void exchangeGenerics(ExpandedComponentInstanceSymbol inst,
      Map<JTypeSymbol, ActualTypeArgument> mapTypeArguments) {
    // TODO work with full names, but then you got the problem with generics.GenericInstance.Generic.T != generics.SuperGenericComparableComp2.T
    // because when delegating the name of the referenced type must be created

    mapTypeArguments.forEach((k, v) -> {
      // 1) replace port generics
      inst.getPorts().stream()
          //          .filter(p -> p.getTypeReference().getReferencedSymbol().getFullName().equals(k.getFullName()))
          .filter(p -> p.getTypeReference().getReferencedSymbol().getName().equals(k.getName()))
          .forEachOrdered(p -> p.setTypeReference((JTypeReference<? extends JTypeSymbol>) v.getType()));

      // 2) propagate component instance definition generics
      inst.getSubComponents().stream()
          // now update the actual type reference definitions by replacing them according to the hash map
          .forEachOrdered(s -> s.setActualTypeArguments(
              s.getActualTypeArguments().stream()
                  // replace this filtered type argument with the value we want to replace
                  //                  .map(a -> a.getType().getReferencedSymbol().getFullName().equals(k.getFullName()) ? v : a)
                  .map(a -> a.getType().getReferencedSymbol().getName().equals(k.getName()) ? v : a)
                  .collect(Collectors.toList())
          ));
    });

    // delegate generic exchanges through inner component hierarchy
    inst.getSubComponents().stream()
        .forEachOrdered(s -> exchangeGenerics(s, createMap(s.getComponentType().getFormalTypeParameters(),
            s.getActualTypeArguments())));
  }

  public ExpandedComponentInstanceSymbol build() {
    if (name.isPresent() && symbolReference.isPresent()) {
      ExpandedComponentInstanceSymbol sym =
          new ExpandedComponentInstanceSymbol(this.name.get(),
              this.symbolReference.get());

      //TODO add checks that port names and subcomponent names are unique
      final MutableScope scope = (MutableScope) sym.getSpannedScope();
      resolvingFilters.stream().forEachOrdered(f -> scope.addResolver(f));

      ports.stream().forEachOrdered(p -> scope.add(PortBuilder.clone(p))); // must be cloned since we change it if it has generics
      connectors.stream().forEachOrdered(c -> scope.add(cloneConnector(c)));
      subComponents.stream().forEachOrdered(s -> scope.add(s));

      sym.setActualTypeArguments(actualTypeArguments.values().stream().collect(Collectors.toList()));
      exchangeGenerics(sym, actualTypeArguments);
      return sym;
    }
    Log.error("not all parameters have been set before to build the expanded component instance symbol");
    throw new Error("not all parameters have been set before to build the expanded component instance symbol");
  }

  public ExpandedComponentInstanceBuilder addConnectorIfNameDoesNotExists(ConnectorSymbol connector) {
    List<String> existingConnectorSources = this.connectors.stream().map(c -> c.getSource())
        .collect(Collectors.toList());
    List<String> existingConnectorTargets = this.connectors.stream().map(c -> c.getTarget())
        .collect(Collectors.toList());
    if(!existingConnectorSources.contains(connector.getSource()) && !existingConnectorTargets.contains(connector.getTarget())) {
      this.addConnector(connector);
    }
    return this;
  }

  public ExpandedComponentInstanceBuilder addConnectorsIfNameDoesNotExists(Collection<ConnectorSymbol> connectors) {
    connectors.stream().forEach(this::addConnectorIfNameDoesNotExists);
    return this;
  }
}
