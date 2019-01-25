/*
 * Copyright (c) 2018 RWTH Aachen. All rights reserved.
 * 
 * http://www.se-rwth.de/
 */
package de.montiarcautomaton.generator.codegen.xtend.compinst

import montiarc._symboltable.ComponentSymbol
import de.montiarcautomaton.generator.helper.ComponentHelper
import montiarc._ast.ASTPort

/**
 * Prints the setup for both atomic and composed components.
 * 
 * @author  Pfeiffer
 * @version $Revision$,
 *          $Date$
 * 
 */
class DynamicSetup {

  /**
   * Delegates to the right print method.
   */
  def static print(ComponentSymbol comp) {
    if (comp.isAtomic) {
      return printSetupAtomic(comp)
    } else {
      return printSetupComposed(comp)
    }
  }

  def private static printSetupAtomic(ComponentSymbol comp) {
    return '''
			@Override
			public void setUp() {
			«IF comp.superComponent.present»
				super.setUp();
			«ENDIF»
			
			
			// set up output ports
			«FOR portOut : comp.outgoingPorts»
				this.«portOut.name» = new Port<«ComponentHelper.printTypeName((portOut.astNode.get as ASTPort).type)»>();
			«ENDFOR»
			
			this.initialize();
			
			}
		'''
  }

  def private static printSetupComposed(ComponentSymbol comp) {
    var helper = new ComponentHelper(comp)
    return '''
			  @Override
			  public void setUp() {
			  «IF comp.superComponent.present»
				super.setUp();
			  «ENDIF»
			  
			  //Cast subcomponents to generic interface so they can be replaced
			  «FOR subcomponent : comp.subComponents»
				this.«subcomponent.name» = (IDynamicComponent) new «DynamicComponentHelper.getSubComponentTypeName(subcomponent)»(
				«FOR param : helper.getParamValues(subcomponent) SEPARATOR ','»
					«param»
				«ENDFOR»
				);
			  «ENDFOR»
			
			  //set up all sub components  
			  «FOR subcomponent : comp.subComponents»
			  	this.«subcomponent.name».setUp();
			  «ENDFOR»
			
			
			
			// set up output ports
			«FOR portOut : comp.outgoingPorts»
				this.«portOut.name» = new Port<«ComponentHelper.printTypeName((portOut.astNode.get as ASTPort).type)»>();
			«ENDFOR»
			
			
			
			  // propagate children's output ports to own output ports
			  «FOR connector : comp.connectors»
			  	«IF !helper.isIncomingPort(comp,connector, false)»
					«helper.getConnectorComponentName(connector, false)».setPort("«helper.getConnectorPortName(connector, false)»",«helper.getConnectorComponentName(connector,true)».getPort("«helper.getConnectorPortName(connector, true)»"));
			  	«ENDIF»
			  «ENDFOR»
			  
			  }
		'''
  }

}
