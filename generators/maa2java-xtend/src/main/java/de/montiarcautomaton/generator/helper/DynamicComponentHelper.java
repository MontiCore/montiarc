package de.montiarcautomaton.generator.helper;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;

import de.monticore.types.types._ast.ASTQualifiedName;
import montiarc._ast.ASTComponent;
import montiarc._ast.ASTConnector;
import montiarc._symboltable.ComponentInstanceSymbol;
import montiarc._symboltable.ComponentSymbol;
import montiarc._symboltable.ComponentSymbolReference;

/**
 * Helper class used in the template to generate target code of atomic or
 * composed components.
 *
 */
public class DynamicComponentHelper extends ComponentHelper{

 
  public DynamicComponentHelper(ComponentSymbol component) {
		super(component);
	}

/**
   * Print the type of the specified dynamic subcomponent.
   *
   * @param instance The instance of which the type should be printed
   * @return The printed subcomponent type
   */
  public static String getSubComponentTypeName(ComponentInstanceSymbol instance) {
    String result = "";
    final ComponentSymbolReference componentTypeReference = instance.getComponentType();
    result += componentTypeReference.getFullName();
    if (componentTypeReference.hasActualTypeArguments()) {
      result += printTypeArguments(componentTypeReference.getActualTypeArguments());
    }
    String sourceWord = ".";
    String targetWord = ".Dynamic";
    StringBuilder strb=new StringBuilder(result);    
    int index=strb.lastIndexOf(sourceWord);    
    strb.replace(index,sourceWord.length()+index,targetWord);    
    return strb.toString();
  }

/**
 * Returns all connectors that start/end in a specified subcomponent
 * @param comp
 * @param subComp
 * @return
 */
public Collection<ASTConnector> getConnectorsForSubComp(ComponentSymbol comp, ComponentInstanceSymbol subComp){
	Collection<ASTConnector> subCompConnectors = new ArrayList<>();
	Collection<ASTConnector> connectors = ((ASTComponent)comp.getAstNode().get()).getConnectors();
	for (ASTConnector connectorSymbol : connectors) {
		for (ASTQualifiedName target : connectorSymbol.getTargetsList()) {
		if ( subComp.getName().equals( getConnectorComponentName(connectorSymbol.getSource(),target, true))){
		subCompConnectors.add(connectorSymbol);
		}
		}
	}
	return subCompConnectors;
}
}
