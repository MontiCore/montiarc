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
    	return new ArrayList<>();
    }
    '''
  }
  
  def private static printReconfigureComposed(ComponentSymbol comp) {
    var helper = new DynamicComponentHelper(comp);
    
    return 
    '''
    @Override
    public List<Port> reconfigure() {
    	List<Port> outgoingPortChanges = new ArrayList<>();
    	
    	«FOR subcomponent : comp.subComponents»
        if (new«subcomponent.name» != null){
        	Boolean interfaceMatches = new InterfaceChecker().checkInterface(this.«subcomponent.name».getInterface() , this.new«subcomponent.name».getInterface());
        	if (interfaceMatches){
        		loman.unregisterLoader(this.«subcomponent.name».getInstanceName());
	  			this.«subcomponent.name» = new«subcomponent.name»;
	  			this.new«subcomponent.name» = null;	
	  		
	  		«FOR connector : helper.getConnectorsForSubComp(comp, subcomponent)»
	  			«IF helper.isIncomingPort(comp, connector, false)»
	  			«helper.getConnectorComponentName(connector, false)».setPort("«helper.getConnectorPortName(connector, false)»",«helper.getConnectorComponentName(connector,true)».getPort("«helper.getConnectorPortName(connector, true)»"));
	  				«ENDIF»
	  				«ENDFOR»
	  		 	outgoingPortChanges.addAll(this.«subcomponent.name».getPorts());
	  		} 
        }
        outgoingPortChanges.addAll(this.«subcomponent.name».reconfigure());
    	 «ENDFOR»
    	
    	return outgoingPortChanges;
    }
    '''
  }
  
  
}