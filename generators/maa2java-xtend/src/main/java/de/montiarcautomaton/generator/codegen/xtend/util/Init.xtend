/*
 * Copyright (c) 2018 RWTH Aachen. All rights reserved.
 *
 * http://www.se-rwth.de/
 */
package de.montiarcautomaton.generator.codegen.xtend.util

import de.montiarcautomaton.generator.helper.ComponentHelper
import montiarc._symboltable.ComponentSymbol

/**
 * Class responsible for printing the init() method for both atomic and composed components.
 *
 * @author  Pfeiffer
 * @version $Revision$,
 *          $Date$
 *
 */
class Init {
  
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
    «FOR connector : comp.connectors»
      «IF helper.isIncomingPort(comp, connector, false)»
        «helper.getConnectorComponentName(connector, false)».setPort«helper.getConnectorPortName(connector, false).toFirstUpper»(«helper.getConnectorComponentName(connector,true)».getPort«helper.getConnectorPortName(connector, true).toFirstUpper»());
      «ENDIF»
    «ENDFOR» 
    
    // init all subcomponents
    
    «FOR subcomponent : comp.subComponents»
      this.«subcomponent.name».init();
    «ENDFOR»
    }
    '''
  }
  
  
}