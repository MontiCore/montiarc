/*
 * Copyright (c) 2018 RWTH Aachen. All rights reserved.
 *
 * http://www.se-rwth.de/
 */
package de.montiarcautomaton.generator.codegen.xtend.compinst

import de.montiarcautomaton.generator.helper.ComponentHelper
import montiarc._symboltable.ComponentSymbol
import montiarc._ast.ASTConnector
import de.monticore.types.types._ast.ASTQualifiedName
import montiarc._ast.ASTComponent

/**
 * Class responsible for printing the init() method for both atomic and composed components.
 *
 * @author  Pfeiffer
 * @version $Revision$,
 *          $Date$
 *
 */
class DynamicInit {
  
  /**
   * Delegates to the right printInit method.
   */
  def static print(ComponentSymbol comp) {
    if (comp.isAtomic) {
    	return printInitAtomic(comp)
    } else {
      return printInitComposed(comp)
    }
  }
  
  def private static printInitAtomic(ComponentSymbol comp) {
    return
    '''
			@Override
			public void init() {
			«IF comp.superComponent.present»
				super.init();
			«ENDIF»
			// set up unused input ports
			«FOR portIn : comp.incomingPorts»
				if (this.«portIn.name» == null) {
				  this.«portIn.name» = Port.EMPTY;
				}
			«ENDFOR»
			}
		'''
  }
  
  def private static printInitComposed(ComponentSymbol comp) {
    var helper = new ComponentHelper(comp);
    
    return 
    '''
			@Override
			public void init() {
			List<String> subcomps = new ArrayList<>();
			Map<String, List<String>> interfaces = new HashMap<>();
			Map<String, String> subcompTypes = new HashMap<>();
			«FOR subcomponent : comp.subComponents»
			subcomps.add(instanceName + ".«subcomponent.name»");
			interfaces.put("«subcomponent.name»",«subcomponent.name».getInterface());
			subcompTypes.put("«subcomponent.name»","«ComponentHelper.getSubComponentTypeName(subcomponent)»");
			«ENDFOR»
			
			//Set up and start new thread for the Filesystemloader	
			
			this.loader = new FileSystemLoader(instanceName, storeDir, targetDir, 
				subcomps, interfaces, subcompTypes);
			this.loader.start();
			«IF comp.superComponent.present»
				super.init();
			«ENDIF»
			// set up unused input ports
			«FOR portIn : comp.incomingPorts»
				if (this.«portIn.name» == null) {
				  this.«portIn.name» = Port.EMPTY;
				}
			«ENDFOR»
			
			// connect outputs of children with inputs of children, by giving 
			// the inputs a reference to the sending ports
			«FOR ASTConnector connector : (comp.getAstNode().get() as ASTComponent)
			          .getConnectors()»
			          «FOR ASTQualifiedName target : connector.targetsList»
			            «IF helper.isIncomingPort(comp, connector.source, target, false)»
					«helper.getConnectorComponentName(connector.source, target, false)».setPort("«helper.getConnectorPortName(connector.source, target, false)»",«helper.getConnectorComponentName(connector.source, target,true)».getPort("«helper.getConnectorPortName(connector.source, target, true)»"));
			            «ENDIF»
			          «ENDFOR»
			    «ENDFOR» 
			

			
			// init all subcomponents
			
			«FOR subcomponent : comp.subComponents»
				this.«subcomponent.name».init();
			«ENDFOR»
			}
		'''
  }
  
  
}
