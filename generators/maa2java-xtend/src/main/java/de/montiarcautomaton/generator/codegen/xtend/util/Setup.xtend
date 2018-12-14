/*
 * Copyright (c) 2018 RWTH Aachen. All rights reserved.
 *
 * http://www.se-rwth.de/
 */
package de.montiarcautomaton.generator.codegen.xtend.util

import de.montiarcautomaton.generator.helper.ComponentHelper
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
class Setup {
   def static printSetup(ComponentSymbol comp) {
    var helper = new ComponentHelper(comp);
    
    return 
    '''
    @Override
      public void setUp() {
      «IF comp.superComponent.present»
        super.setUp();
      «ENDIF»
      // instantiate all subcomponents
      «FOR subcomponent : comp.subComponents»
        this.«subcomponent.name» = new «helper.getSubComponentTypeName(subcomponent)»(
        «FOR param : helper.getParamValues(subcomponent) SEPARATOR ','»
          «param»
        «ENDFOR»);
        
      «ENDFOR»
    
        //set up all sub components  
        «FOR subcomponent : comp.subComponents»
      this.«subcomponent.name».setUp();
        «ENDFOR»
        
        // set up output ports
        «FOR portOut : comp.outgoingPorts»
      this.«portOut.name» = new Port<«helper.printPortType(portOut)»>();
        «ENDFOR»
        
        // propagate children's output ports to own output ports
        «FOR connector : comp.connectors»
      «IF !helper.isIncomingPort(comp,connector, false)»
        «helper.getConnectorComponentName(connector,false)».setPort«helper.getConnectorPortName(connector,false).toFirstUpper»(«helper.getConnectorComponentName(connector, true)».getPort«helper.getConnectorPortName(connector, true).toFirstUpper»());
      «ENDIF»
        «ENDFOR»
        
      }
    '''
  }
}