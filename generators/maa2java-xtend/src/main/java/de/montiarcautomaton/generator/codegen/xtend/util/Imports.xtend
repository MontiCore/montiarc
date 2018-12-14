/*
 * Copyright (c) 2018 RWTH Aachen. All rights reserved.
 *
 * http://www.se-rwth.de/
 */
package de.montiarcautomaton.generator.codegen.xtend.util

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
class Imports {
    def static printImports(ComponentSymbol comp) {
    return 
    '''
    «FOR _import : comp.imports»
      import «_import.statement»«IF _import.isStar».*«ENDIF»;
    «ENDFOR»
    '''
  }
  
}