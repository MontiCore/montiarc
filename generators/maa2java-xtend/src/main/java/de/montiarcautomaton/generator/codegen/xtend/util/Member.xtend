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
«««    «FOR »for(port : comp.ports) {
«««         print("Port<" + ComponentHelper.printTypeName((port.astNode.get as ASTPort).type) + ">", port.name, "protected")
«««    }
    
    '''
  }

  def static printSubcomponents(ComponentSymbol comp) {
    for (sub : comp.subComponents) {
      print(sub.componentType.fullName + TypeParameters.printTypeArguments(sub.componentType.actualTypeArguments), sub.name, "private")
    }
  }


  def static printConfigParameters(ComponentSymbol comp) {
    for(param : (comp.astNode.get as ASTComponent).head.parameterList) {
      Member.print(ComponentHelper.printTypeName(param.type), param.name, "private final")
    }
  }

  def static printVariables(ComponentSymbol comp) {
    for(variable : comp.variables) {
      print(ComponentHelper.printTypeName((variable.astNode.get as ASTVariableDeclaration).type), variable.name, "protected")
    }
  }

}
