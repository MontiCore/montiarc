/*
 * Copyright (c) 2019 RWTH Aachen. All rights reserved.
 * 
 * http://www.se-rwth.de/
 */
package de.montiarcautomaton.generator.codegen.xtend.util

import de.montiarcautomaton.generator.helper.ComponentHelper
import de.monticore.types.types._ast.ASTTypeVariableDeclaration
import java.util.ArrayList
import java.util.List
import montiarc._ast.ASTComponent
import montiarc._ast.ASTVariableDeclaration
import montiarc._symboltable.ComponentSymbol

/**
 * This class contains several methods commonly used by generator classes.
 * 
 * @author  Pfeiffer
 * @version $Revision$,
 *          $Date$
 * 
 */
class Utils {

  /**
   * Prints the component's configuration parameters as a comma separated list.
   */
  def static printConfiurationParametersAsList(ComponentSymbol comp) {
    var helper = new ComponentHelper(comp)
    return '''
      «FOR param : comp.configParameters SEPARATOR ','» «helper.printFqnTypeName(comp.astNode.get as ASTComponent, param.type)» «param.name» «ENDFOR»
    '''
  }

  /**
   * Prints the component's imports
   */
  def static printImports(ComponentSymbol comp) {
    return '''
      «FOR _import : comp.imports»
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
  def static printConfigParameters(ComponentSymbol comp) {
    return '''
      «FOR param : (comp.astNode.get as ASTComponent).head.parameterList»
        «printMember(ComponentHelper.printTypeName(param.type), param.name, "private final")»
      «ENDFOR»
    '''
  }

  /**
   * Prints members for variables
   */
  def static printVariables(ComponentSymbol comp) {
    return '''
      «FOR variable : comp.variables»
        «printMember(ComponentHelper.printTypeName((variable.astNode.get as ASTVariableDeclaration).type), variable.name, "protected")»
      «ENDFOR»
    '''
  }
  
  /**
   * Prints formal parameters of a component.
   */
  def static printFormalTypeParameters(ComponentSymbol comp) {
    return '''
      «IF (comp.astNode.get as ASTComponent).head.isPresentGenericTypeParameters»
        <
          «FOR generic : getGenericParameters(comp) SEPARATOR ','»
            «generic»
          «ENDFOR»
        >
      «ENDIF»
    '''
  }

  def private static List<String> getGenericParameters(ComponentSymbol comp) {
    var componentNode = comp.astNode.get as ASTComponent
    var List<String> output = new ArrayList
    if (componentNode.getHead().isPresentGenericTypeParameters()) {
      var List<ASTTypeVariableDeclaration> parameterList = componentNode.getHead().getGenericTypeParameters().
        getTypeVariableDeclarationList()
      for (ASTTypeVariableDeclaration variableDeclaration : parameterList) {
        output.add(variableDeclaration.getName())
      }
    }
    return output;
  }
  
  /**
   * Print the package declaration for generated component classes.
   * Uses recursive determination of the package name to accomodate for components
   * with at least two levels of inner component. These require changing the package name
   * to avoid name clashes between the generated packages and the outermost component.
   */
  def static String printPackage(ComponentSymbol comp) {
  	return '''

  	package «comp.packageName»;
  	'''
  }
  
  /**
   * Helper function used to determine package names.
   */
  def static String printPackageWithoutKeyWordAndSemicolon(ComponentSymbol comp){
  	return '''
  	«comp.packageName»
  	'''
  }
  
  def static String printSuperClassFQ(ComponentSymbol comp){
  	var String packageName = printPackageWithoutKeyWordAndSemicolon(comp.superComponent.get.referencedSymbol);
  	if(packageName.equals("")){
  		return '''«comp.superComponent.get.name»'''
  	} else {
  		return '''«packageName».«comp.superComponent.get.name»'''
  	}
  }

}
