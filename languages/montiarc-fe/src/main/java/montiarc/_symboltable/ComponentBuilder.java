package montiarc._symboltable;

import java.util.Collection;

import de.monticore.symboltable.MutableScope;
import de.monticore.symboltable.types.JFieldSymbol;
import de.monticore.symboltable.types.JTypeSymbol;
import de.se_rwth.commons.logging.Log;
import montiarc.helper.SymbolPrinter;

//XXX: https://git.rwth-aachen.de/montiarc/core/issues/48

/**
 * Created by Michael von Wenckstern on 13.06.2016.
 *
 * @author Michael von Wenckstern
 *         This class allows to modify {@see ComponentSymbol},
 *         if you do so the symbol table may not be consistent.
 *         Especially you need to call {@see MontiArcExpandedComponentInstanceSymbolCreator#createInstances}
 *         TODO static methods should call a protected doMethod() to allow extending this class
 *         TODO the builder should also be used to create a new ComponentSymbol with a build() method
 */
public class ComponentBuilder {
  protected static ComponentBuilder instance = null;

  protected static ComponentBuilder getInstance() {
    if (instance == null) {
      instance = new ComponentBuilder();
    }
    return instance;
  }

  public ComponentBuilder() {
  }

  ////////////////////////// ports //////////////////////////////////////////////

  public static ComponentBuilder addPort(ComponentSymbol cs, PortSymbol ps) {
    ((MutableScope) cs.getSpannedScope()).add(ps);
    return getInstance();
  }

  public static ComponentBuilder addPorts(ComponentSymbol cs, PortSymbol... ps) {
    for (PortSymbol p : ps) {
      addPort(cs, p);
    }
    return getInstance();
  }

  public static ComponentBuilder addPorts(ComponentSymbol cs, Collection<PortSymbol> ps) {
    ps.stream().forEachOrdered(p -> addPort(cs, p));
    return getInstance();
  }

  public static ComponentBuilder removePort(ComponentSymbol cs, PortSymbol ps) {
    ((MutableScope) cs.getSpannedScope()).remove(ps);
    return getInstance();
  }

  public static ComponentBuilder removePorts(ComponentSymbol cs, PortSymbol... ps) {
    for (PortSymbol p : ps) {
      removePort(cs, p);
    }
    return getInstance();
  }

  public static ComponentBuilder removePorts(ComponentSymbol cs, Collection<PortSymbol> ps) {
    ps.stream().forEachOrdered(p -> removePort(cs, p));
    return getInstance();
  }

  ////////////////////////// connectors //////////////////////////////////////////////

  public static ComponentBuilder addConnector(ComponentSymbol cs, ConnectorSymbol con) {
    ((MutableScope) cs.getSpannedScope()).add(con);
    return getInstance();
  }

  public static ComponentBuilder addConnectors(ComponentSymbol cs, ConnectorSymbol... con) {
    for (ConnectorSymbol c : con) {
      addConnector(cs, c);
    }
    return getInstance();
  }

  public static ComponentBuilder addConnectors(ComponentSymbol cs, Collection<ConnectorSymbol> con) {
    con.stream().forEachOrdered(c -> addConnector(cs, c));
    return getInstance();
  }

  public static ComponentBuilder removeConnector(ComponentSymbol cs, ConnectorSymbol con) {
    ((MutableScope) cs.getSpannedScope()).remove(con);
    return getInstance();
  }

  public static ComponentBuilder removeConnectors(ComponentSymbol cs, ConnectorSymbol... con) {
    for (ConnectorSymbol c : con) {
      removeConnector(cs, c);
    }
    return getInstance();
  }

  public static ComponentBuilder removeConnectors(ComponentSymbol cs, Collection<ConnectorSymbol> con) {
    con.stream().forEachOrdered(c -> removeConnector(cs, c));
    return getInstance();
  }

  ////////////////////////// inner components //////////////////////////////////////////////

  public static ComponentBuilder addInnerComponent(ComponentSymbol cs, ComponentSymbol innerComponent) {
    ((MutableScope) cs.getSpannedScope()).add(innerComponent);
    return getInstance();
  }

  public static ComponentBuilder addInnerComponents(ComponentSymbol cs, ComponentSymbol... innerComponent) {
    for (ComponentSymbol c : innerComponent) {
      addInnerComponent(cs, c);
    }
    return getInstance();
  }

  public static ComponentBuilder addInnerComponents(ComponentSymbol cs, Collection<ComponentSymbol> innerComponent) {
    innerComponent.stream().forEachOrdered(c -> addInnerComponent(cs, c));
    return getInstance();
  }

  public static ComponentBuilder removeInnerComponent(ComponentSymbol cs, ComponentSymbol innerComponent) {
    ((MutableScope) cs.getSpannedScope()).remove(innerComponent);
    return getInstance();
  }

  public static ComponentBuilder removeInnerComponents(ComponentSymbol cs, ComponentSymbol... innerComponent) {
    for (ComponentSymbol c : innerComponent) {
      removeInnerComponent(cs, c);
    }
    return getInstance();
  }

  public static ComponentBuilder removeInnerComponents(ComponentSymbol cs, Collection<ComponentSymbol> innerComponent) {
    innerComponent.stream().forEachOrdered(c -> removeInnerComponent(cs, c));
    return getInstance();
  }

  ////////////////////////// formal type parameters //////////////////////////////////////////////

  public static ComponentBuilder addFormalTypeParameter(ComponentSymbol cs, JTypeSymbol formalTypeParameter) {
    if (!formalTypeParameter.isFormalTypeParameter()) {
      Log.error(String.format("%s is not a formal type parameter. JTypeSymbol#isFormalTypeParameter() is false.",
          SymbolPrinter.printFormalTypeParameters(formalTypeParameter)));
    }
    ((MutableScope) cs.getSpannedScope()).add(formalTypeParameter);
    return getInstance();
  }

  public static ComponentBuilder addFormalTypeParameters(ComponentSymbol cs, JTypeSymbol... formalTypeParameter) {
    for (JTypeSymbol f : formalTypeParameter) {
      addFormalTypeParameter(cs, f);
    }
    return getInstance();
  }

  public static ComponentBuilder addFormalTypeParameters(ComponentSymbol cs, Collection<JTypeSymbol> formalTypeParameter) {
    formalTypeParameter.stream().forEachOrdered(f -> addFormalTypeParameter(cs, f));
    return getInstance();
  }

  public static ComponentBuilder removeFormalTypeParameter(ComponentSymbol cs, JTypeSymbol formalTypeParameter) {
    ((MutableScope) cs.getSpannedScope()).remove(formalTypeParameter);
    return getInstance();
  }

  public static ComponentBuilder removeFormalTypeParameters(ComponentSymbol cs, JTypeSymbol... formalTypeParameter) {
    for (JTypeSymbol f : formalTypeParameter) {
      removeFormalTypeParameter(cs, f);
    }
    return getInstance();
  }

  public static ComponentBuilder removeFormalTypeParameters(ComponentSymbol cs, Collection<JTypeSymbol> formalTypeParameter) {
    formalTypeParameter.stream().forEachOrdered(f -> removeFormalTypeParameter(cs, f));
    return getInstance();
  }

  ////////////////////////// config parameters //////////////////////////////////////////////

  public static ComponentBuilder addConfigParameter(ComponentSymbol cs, JFieldSymbol configParameter) {
    ((MutableScope) cs.getSpannedScope()).add(configParameter);
    return getInstance();
  }

  public static ComponentBuilder addConfigParameters(ComponentSymbol cs, JFieldSymbol... configParameter) {
    for (JFieldSymbol c : configParameter) {
      addConfigParameter(cs, c);
    }
    return getInstance();
  }

  public static ComponentBuilder addConfigParameters(ComponentSymbol cs, Collection<JFieldSymbol> configParameter) {
    configParameter.stream().forEachOrdered(c -> addConfigParameter(cs, c));
    return getInstance();
  }

  public static ComponentBuilder removeConfigParameter(ComponentSymbol cs, JFieldSymbol configParameter) {
    ((MutableScope) cs.getSpannedScope()).remove(configParameter);
    return getInstance();
  }

  public static ComponentBuilder removeConfigParameters(ComponentSymbol cs, JFieldSymbol... configParameter) {
    for (JFieldSymbol c : configParameter) {
      removeConfigParameter(cs, c);
    }
    return getInstance();
  }

  public static ComponentBuilder removeConfigParameters(ComponentSymbol cs, Collection<JFieldSymbol> configParameter) {
    configParameter.stream().forEachOrdered(c -> removeConfigParameter(cs, c));
    return getInstance();
  }

  ////////////////////////// sub components //////////////////////////////////////////////

  public static ComponentBuilder addSubComponent(ComponentSymbol cs, ComponentInstanceSymbol subComponent) {
    ((MutableScope) cs.getSpannedScope()).add(subComponent);
    return getInstance();
  }

  public static ComponentBuilder addSubComponents(ComponentSymbol cs, ComponentInstanceSymbol... subComponent) {
    for (ComponentInstanceSymbol s : subComponent) {
      addSubComponent(cs, s);
    }
    return getInstance();
  }

  public static ComponentBuilder addSubComponents(ComponentSymbol cs, Collection<ComponentInstanceSymbol> subComponent) {
    subComponent.stream().forEachOrdered(s -> addSubComponent(cs, s));
    return getInstance();
  }

  public static ComponentBuilder removeSubComponent(ComponentSymbol cs, ComponentInstanceSymbol subComponent) {
    ((MutableScope) cs.getSpannedScope()).remove(subComponent);
    return getInstance();
  }

  public static ComponentBuilder removeSubComponents(ComponentSymbol cs, ComponentInstanceSymbol... subComponent) {
    for (ComponentInstanceSymbol s : subComponent) {
      removeSubComponent(cs, s);
    }
    return getInstance();
  }

  public static ComponentBuilder removeSubComponents(ComponentSymbol cs, Collection<ComponentInstanceSymbol> subComponent) {
    subComponent.stream().forEachOrdered(s -> removeSubComponent(cs, s));
    return getInstance();
  }

}
