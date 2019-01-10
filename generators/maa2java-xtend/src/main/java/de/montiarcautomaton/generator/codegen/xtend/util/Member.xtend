/*
 * Copyright (c) 2018 RWTH Aachen. All rights reserved.
 * 
 * http://www.se-rwth.de/
 */
package de.montiarcautomaton.generator.codegen.xtend.util

import montiarc._ast.ASTComponent
import montiarc._symboltable.ComponentSymbol
import de.montiarcautomaton.generator.helper.ComponentHelper
import montiarc._ast.ASTPort
import montiarc._ast.ASTVariableDeclaration

/**
 * TODO: Write me!
 * 
 * @author  (last commit) $Author$
 * @version $Revision$,
 *          $Date$
 * @since   TODO: add version number
 * 
 */
class Member {
  def static print(String type, String name, String visibility) {
    return '''
      «visibility» «type» «name»;
    '''
  }

  def static printPorts(ComponentSymbol comp) {
    return '''
    «FOR port : comp.ports»
      «print("Port<" + ComponentHelper.printTypeName((port.astNode.get as ASTPort).type) + ">", port.name, "protected")»
    «ENDFOR»
    '''
  }

  def static printSubcomponents(ComponentSymbol comp) {
    return 
    '''
    «FOR sub : comp.subComponents»
      «print(ComponentHelper.getSubComponentTypeName(sub), sub.name, "private")»
    «ENDFOR»
    '''
  }


  def static printConfigParameters(ComponentSymbol comp) {
    return 
    '''
    «FOR param : (comp.astNode.get as ASTComponent).head.parameterList»
      «Member.print(ComponentHelper.printTypeName(param.type), param.name, "private final")»
    «ENDFOR»
    '''
  }

  def static printVariables(ComponentSymbol comp) {
    return 
    '''
    «FOR variable : comp.variables»
      «print(ComponentHelper.printTypeName((variable.astNode.get as ASTVariableDeclaration).type), variable.name, "protected")»
    «ENDFOR»
    '''
  }

}
