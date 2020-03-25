/* (c) https://github.com/MontiCore/monticore */
package de.montiarcautomaton.generator.codegen.xtend.util

import de.montiarcautomaton.generator.helper.ComponentHelper
import montiarc._ast.ASTPort
import montiarc._symboltable.ComponentSymbol
import de.monticore.types.types._ast.ASTQualifiedName
import montiarc._ast.ASTConnector
import montiarc._ast.ASTComponent

/**
 * Prints the setup for both atomic and composed components.
 * 
 *          $Date$
 * 
 */
class Setup {

  /**
   * Delegates to the right print method.
   */
  def print(ComponentSymbol comp) {
    if (comp.isAtomic) {
      return printSetupAtomic(comp)
    } else {
      return printSetupComposed(comp)
    }
  }

  def protected printSetupAtomic(ComponentSymbol comp) {
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

  def protected printSetupComposed(ComponentSymbol comp) {
    var helper = new ComponentHelper(comp)
    return '''
        @Override
        public void setUp() {
        «IF comp.superComponent.present»
        super.setUp();
        «ENDIF»
        
        «FOR subcomponent : comp.subComponents»
        this.«subcomponent.name» = new «ComponentHelper.getSubComponentTypeName(subcomponent)»(
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
    «FOR ASTConnector connector : (comp.getAstNode().get() as ASTComponent)
          .getConnectors()»
          «FOR ASTQualifiedName target : connector.targetsList»
            «IF !helper.isIncomingPort(comp,connector.source, target, false)»
              «helper.getConnectorComponentName(connector.source, target,false)».setPort«helper.getConnectorPortName(connector.source, target,false).toFirstUpper»(«helper.getConnectorComponentName(connector.source, target, true)».getPort«helper.getConnectorPortName(connector.source, target, true).toFirstUpper»());
            «ENDIF»
          «ENDFOR»
        «ENDFOR»
        
        }
      '''
  }

}
