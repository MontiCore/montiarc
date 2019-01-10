/*
 * Copyright (c) 2019 RWTH Aachen. All rights reserved.
 *
 * http://www.se-rwth.de/
 */
package de.montiarcautomaton.generator.codegen.xtend.atomic.behavior.automaton

import montiarc._ast.ASTAutomaton
import montiarc._symboltable.ComponentSymbol

/**
 * TODO: Write me!
 *
 * @author  (last commit) $Author$
 * @version $Revision$,
 *          $Date$
 * @since   TODO: add version number
 *
 */
class StateEnum {
  def static String print(ASTAutomaton automaton, ComponentSymbol comp) {
    return '''
    private static enum «comp.name»State {
    «FOR state : automaton.getStateDeclaration(0).stateList SEPARATOR ','»
      «state.name»
    «ENDFOR»;
    }
    '''
    
  }
}