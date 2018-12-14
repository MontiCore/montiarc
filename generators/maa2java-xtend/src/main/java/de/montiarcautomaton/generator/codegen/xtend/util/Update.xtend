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
class Update {
    def static printUpdate(ComponentSymbol comp) {
    return 
    '''
    @Override
      public void update() {
        // update subcomponent instances
        «IF comp.superComponent.present»
          super.update();
        «ENDIF»
        «FOR subcomponent : comp.subComponents»
          this.«subcomponent.name».update();
        «ENDFOR»
      }
    '''
  }
}