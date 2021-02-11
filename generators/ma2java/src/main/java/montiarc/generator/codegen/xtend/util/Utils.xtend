/* (c) https://github.com/MontiCore/monticore */
package montiarc.generator.codegen.xtend.util

import arcbasis._symboltable.ComponentTypeSymbol
import montiarc.generator.helper.ComponentHelper

/**
 * This class contains several methods commonly used by generator classes.
 */
class Utils {

  /**
   * Prints the component's configuration parameters as a comma separated list.
   */
  def static printConfiurationParametersAsList(ComponentTypeSymbol comp) {
    return '''
      «FOR param : comp.getParameters SEPARATOR ','» «ComponentHelper.print(param.getType)» «param.name» «ENDFOR»
    '''
  }

    /**
     * Prints the component's imports
     */
    def static printImports(ComponentTypeSymbol comp) {
      return '''
        «FOR _import : ComponentHelper.getImports(comp)»
          import «_import.statement»«IF _import.isStar».*«ENDIF»;
        «ENDFOR»
        «FOR inner : comp.innerComponents»
          import «printPackageWithoutKeyWordAndSemicolon(inner) + "." + inner.name»;
        «ENDFOR»
      '''
    }

  /**
   * Prints a member of given visibility name and type
   */
  def static printMember(String type, String name, String visibility) {
    return '''
      «visibility» «type» «name»;
    '''
  }
  
  /**
   * Prints members for configuration parameters.
   */
  def static printConfigParameters(ComponentTypeSymbol comp) {
    return '''
      «FOR param : comp.getParameters»
        «printMember(ComponentHelper.print(param.getType), param.name, "private final")»
      «ENDFOR»
    '''
  }

  /**
   * Prints members for variables
   */
  def static printVariables(ComponentTypeSymbol comp) {
    return '''
      «FOR variable : comp.getFields»
        «IF !comp.getParameters.contains(variable)»
          «printMember(ComponentHelper.print(variable.getType), variable.name, "protected")»
        «ENDIF»
      «ENDFOR»
    '''
  }
  
  /**
   * Prints formal parameters of a component.
   */
  def static printFormalTypeParameters(ComponentTypeSymbol comp) {
    return '''
      «IF comp.hasTypeParameter»
        <
          «FOR generic : comp.getTypeParameters SEPARATOR ','»
            «generic.getName»
          «ENDFOR»
        >
      «ENDIF»
    '''
  }
  
  /**
   * Print the package declaration for generated component classes.
   * Uses recursive determination of the package name to accomodate for components
   * with at least two levels of inner component. These require changing the package name
   * to avoid name clashes between the generated packages and the outermost component.
   */
  def static String printPackage(ComponentTypeSymbol comp) {
  	return '''
  	«IF comp.isInnerComponent»
  	package «printPackageWithoutKeyWordAndSemicolon(comp.getOuterComponent.get) + "." + comp
  	.getOuterComponent.get.name + "gen"»;
	«ELSE»
  	package «comp.packageName»;
	«ENDIF»
  	'''
  }
  
  /**
   * Helper function used to determine package names.
   */
  def static String printPackageWithoutKeyWordAndSemicolon(ComponentTypeSymbol comp){
  	return '''
  	«IF comp.isInnerComponent»
  	«printPackageWithoutKeyWordAndSemicolon(comp.getOuterComponent.get) + "." + comp
  	.getOuterComponent.get.name + "gen"»
	«ELSE»
  	«comp.packageName»
	«ENDIF»
  	'''
  }
  
  def static String printSuperClassFQ(ComponentTypeSymbol comp){
  	var String packageName = printPackageWithoutKeyWordAndSemicolon(comp.getParent);
  	if(packageName.equals("")){
  		return '''«comp.getParent.getName»'''
  	} else {
  		return '''«packageName».«comp.getParent.getName»'''
  	}
  }
}