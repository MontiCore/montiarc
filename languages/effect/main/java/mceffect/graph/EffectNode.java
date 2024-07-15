/* (c) https://github.com/MontiCore/monticore */
package mceffect.graph;

import arcbasis._ast.ASTComponentType;
import de.monticore.symbols.compsymbols._symboltable.PortSymbol;
import de.monticore.symbols.compsymbols._symboltable.SubcomponentSymbol;
import de.monticore.symboltable.ISymbol;
import java.util.HashMap;
import java.util.Map;

public class EffectNode {

  public static Map<PortSymbol, Map<ISymbol, EffectNode>> instances = new HashMap<>();
  protected PortSymbol port;
  protected ISymbol component;

  private EffectNode(PortSymbol value, ISymbol component) {
    this.component = component;
    this.port = value;
  }

  public static EffectNode getInstance(PortSymbol symbol, ISymbol component) {
    if (instances.containsKey(symbol) && instances.get(symbol).containsKey(component)) {
      return instances.get(symbol).get(component);
    } else {
      EffectNode n = new EffectNode(symbol, component);
      instances.put(symbol, Map.of(component, n));
      return n;
    }
  }

  public PortSymbol getPort() {
    return port;
  }

  @Override
  public String toString() {
    String componentName = "";

    if (component instanceof SubcomponentSymbol) {
      componentName = component.getName() + ".";
    }
    return componentName + port.getName();
  }
}
