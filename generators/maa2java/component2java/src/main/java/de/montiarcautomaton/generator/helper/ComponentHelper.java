package de.montiarcautomaton.generator.helper;

import java.util.Collection;
import java.util.Optional;
import java.util.stream.Collectors;

import de.monticore.lang.montiarc.montiarc._symboltable.ComponentInstanceSymbol;
import de.monticore.lang.montiarc.montiarc._symboltable.ComponentSymbol;
import de.monticore.lang.montiarc.montiarc._symboltable.ConnectorSymbol;
import de.monticore.lang.montiarc.montiarc._symboltable.PortSymbol;
import de.monticore.symboltable.types.JFieldSymbol;
import de.monticore.symboltable.types.JTypeSymbol;
import de.monticore.symboltable.types.references.JTypeReference;
import de.monticore.types.TypesHelper;

/**
 * Helper class used in the template to generate target code of atomic or
 * composed components.
 * 
 * @author Gerrit Leonhardt
 */
public class ComponentHelper {
  public static String DEPLOY_STEREOTYPE = "deploy";
  
  private final ComponentSymbol component;
  
  public ComponentHelper(ComponentSymbol component) {
    this.component = component;
  }
  
  public String getPortTypeName(PortSymbol port) {
    return printFqnTypeName(port.getTypeReference());
  }
  
  public String getParamTypeName(JFieldSymbol param) {
    return printFqnTypeName(param.getType());
  }
  
  public Collection<String> getParamValues(ComponentInstanceSymbol param) {
    return param.getConfigArguments().stream().map(symbol -> symbol.getValue()).collect(Collectors.toList());
  }
  
  public String getSubComponentTypeName(ComponentInstanceSymbol instance) {
    return instance.getComponentType().getName();
  }
  
  /**
   * Returns the component name of a connection.
   * 
   * @param conn the connection
   * @param isSource <tt>true</tt> for siurce component, else <tt>false>tt>
   * @return
   */
  public String getConnectorComponentName(ConnectorSymbol conn, boolean isSource) {
    final String name;
    if (isSource) {
      name = conn.getSource();
    }
    else {
      name = conn.getTarget();
    }
    return name.split("\\.")[0];
  }
  
  /**
   * Returns the port name of a connection.
   * 
   * @param conn the connection
   * @param isSource <tt>true</tt> for siurce component, else <tt>false>tt>
   * @return
   */
  public String getConnectorPortName(ConnectorSymbol conn, boolean isSource) {
    final String name;
    if (isSource) {
      name = conn.getSource();
    }
    else {
      name = conn.getTarget();
    }
    return name.split("\\.")[1];
  }
  
  /**
   * Returns <tt>true</tt> if the component is deployable.
   * 
   * @return
   */
  public boolean isDeploy() {
    if (component.getStereotype().containsKey(DEPLOY_STEREOTYPE)) {
      if (!component.getConfigParameters().isEmpty()) {
        throw new RuntimeException("Config parameters are not allowed for a depolyable component.");
      }
      return true;
    }
    return false;
  }
  
  /**
   * Prints the type of the reference including dimensions.
   * 
   * @param ref
   * @return
   */
  protected String printFqnTypeName(JTypeReference<? extends JTypeSymbol> ref) {
    String name = ref.getName();
    Optional<JTypeSymbol> sym = ref.getEnclosingScope().<JTypeSymbol>resolve(ref.getName(), JTypeSymbol.KIND);
    if(sym.isPresent()){
      name = sym.get().getFullName();
    }
    for (int i = 0; i < ref.getDimension(); ++i) {
      name += "[]";
    }
    return name;
  }
}
