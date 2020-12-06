/* (c) https://github.com/MontiCore/monticore */
package montiarc.generator.codegen.xtend.util

import montiarc.generator.helper.ComponentHelper
import arcbasis._symboltable.ComponentTypeSymbol
import arcbasis._ast.ASTConnector
import arcbasis._ast.ASTPortAccess

/**
 * Prints the setup for both atomic and composed components.
 */
class Setup {

  /**
   * Delegates to the right print method.
   */
  def print(ComponentTypeSymbol comp) {
    if (comp.isAtomic) {
      return printSetupAtomic(comp)
    } else {
      return printSetupComposed(comp)
    }
  }

  def protected printSetupAtomic(ComponentTypeSymbol comp) {
    return '''
      @Override
      public void setUp() {
      «IF comp.isPresentParentComponent»
        super.setUp();
      «ENDIF»
      
      
      // set up output ports
      «FOR portOut : comp.outgoingPorts»
        this.«portOut.name» = new Port<«portOut.getType.print»>();
      «ENDFOR»
      
      this.initialize();
      
      }
    '''
  }

  def protected printSetupComposed(ComponentTypeSymbol comp) {
    var helper = new ComponentHelper(comp)
    return '''
        @Override
        public void setUp() {
        «IF comp.isPresentParentComponent»
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
        this.«portOut.name» = new Port<«portOut.getType.print()»>();
      «ENDFOR»
      

      
        // propagate children's output ports to own output ports
    «FOR ASTConnector connector : comp.getAstNode().getConnectors()»
          «FOR ASTPortAccess target : connector.getTargetList»
            «IF !helper.isIncomingPort(comp,connector.source, target, false)»
              «helper.getConnectorComponentName(connector.source, target, false)».setPort«helper
              .getConnectorPortName(connector.source, target,false).toFirstUpper»(«helper.getConnectorComponentName(connector.source, target, true)».getPort«helper.getConnectorPortName(connector.source, target, true).toFirstUpper»());
            «ENDIF»
          «ENDFOR»
        «ENDFOR»
        
        }
      '''
  }

}
