/*
 * Copyright (c) 2018 RWTH Aachen. All rights reserved.
 *
 * http://www.se-rwth.de/
 */
package de.montiarcautomaton.generator.codegen.xtend.compinst

import de.montiarcautomaton.generator.helper.ComponentHelper
import montiarc._symboltable.ComponentSymbol
import montiarc._ast.ASTConnector
import montiarc._ast.ASTComponent
import de.monticore.types.types._ast.ASTQualifiedName
import de.montiarcautomaton.generator.helper.DynamicComponentHelper

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
    	return new ArrayList<Port>();
    }
    '''
  }
  
  def private static printReconfigureComposed(ComponentSymbol comp) {
    var helper = new DynamicComponentHelper(comp);
    
    return 
    '''
    @Override
    public List<Port> reconfigure() {
    	List<Port> outgoingPortChanges = new ArrayList<Port>();
    	
    	«FOR subcomponent : comp.subComponents»
        if (new«subcomponent.name» != null){
        	Boolean interfaceMatches = new InterfaceChecker().checkInterface(this.«subcomponent.name».getInterface() , this.new«subcomponent.name».getInterface());
        	if (interfaceMatches){
        		loman.unregisterLoader(this.«subcomponent.name».getInstanceName());
	  			this.«subcomponent.name» = new«subcomponent.name»;
	  			this.new«subcomponent.name» = null;	
	  		
	  		«FOR ASTConnector connector : (comp.getAstNode().get() as ASTComponent)
	  				  				          .getConnectors()»
	  		«FOR ASTQualifiedName target : connector.targetsList»
	  			«IF helper.isIncomingPort(comp, connector.source, target, false)»
	  			«helper.getConnectorComponentName(connector.source, target, false)».setPort("«helper.getConnectorPortName(connector.source, target, false)»",«helper.getConnectorComponentName(connector.source, target,true)».getPort("«helper.getConnectorPortName(connector.source, target, true)»"));
	  				«ENDIF»
	  				«ENDFOR»
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