package de.montiarcautomaton.generator.codegen.xtend.compinst;

import de.montiarcautomaton.generator.helper.ComponentHelper;
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
}