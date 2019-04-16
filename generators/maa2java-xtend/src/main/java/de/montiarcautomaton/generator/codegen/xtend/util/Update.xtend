/*
 * Copyright (c) 2018 RWTH Aachen. All rights reserved.
 *
 * http://www.se-rwth.de/
 */
package de.montiarcautomaton.generator.codegen.xtend.util

import montiarc._symboltable.ComponentSymbol

/**
 * Prints the update() method for both atomic and composed components.
 *
 * @author  Pfeiffer
 * @version $Revision$,
 *          $Date$
 *
 */
class Update {
  
  /**
   * Delegates to the right print method.
   */
  def static print(ComponentSymbol comp) {
    if (comp.isDecomposed) {
    	return printUpdateComposed(comp)
    } else {
    	return printUpdateAtomic(comp)
    }
  }
  
  
  def private static printUpdateComposed(ComponentSymbol comp){
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
  
  def private static printUpdateAtomic(ComponentSymbol comp){
    return 
    '''
    @Override
    public void update() {
      «IF comp.superComponent.present»
      super.update();
      «ENDIF»
    
      // update computed value for next computation cycle in all outgoing ports
      «FOR portOut : comp.outgoingPorts»
      this.«portOut.name».update();
      «ENDFOR»
    }
    '''
  }
}