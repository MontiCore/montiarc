/*
 * Copyright (c) 2018 RWTH Aachen. All rights reserved.
 *
 * http://www.se-rwth.de/
 */
package de.montiarcautomaton.generator.codegen.xtend.compinst

import de.montiarcautomaton.generator.helper.ComponentHelper
import montiarc._symboltable.ComponentSymbol


class Reconfigure {
  
  /**
   * Delegates to the right printInit method.
   */
  def static print(ComponentSymbol comp) {
    if (comp.isAtomic) {
    	return printReconfigureAtomic(comp)
    } else {
      return printReconfigureComposed(comp)
    }
  }
  
  def private static printReconfigureAtomic(ComponentSymbol comp) {
    return	
    '''
    @Override
    public List<Port> reconfigure() {
    	return null;
    }
    '''
  }
  
  def private static printReconfigureComposed(ComponentSymbol comp) {
    var helper = new ComponentHelper(comp);
    
    return 
    '''
    @Override
    public List<Port> reconfigure() {
    	List<Port> outgoingPortChanges = new ArrayList<>();
    	
    	«FOR subcomponent : comp.subComponents»
        if (new«subcomponent.name» != null){
        	loman.unregisterLoader(this.«subcomponent.name».getInstanceName());
	  		this.«subcomponent.name» = new«subcomponent.name»;
	  		this.new«subcomponent.name» = null;	
        }
    	 «ENDFOR»
    	
    	return outgoingPortChanges;
    }
    '''
  }
  
  
}