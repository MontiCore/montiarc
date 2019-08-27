/* (c) https://github.com/MontiCore/monticore */
package dynamicmontiarc.codegen.xtend.util

import de.montiarcautomaton.generator.helper.ComponentHelper
import dynamicmontiarc.helper.DynamicMontiArcHelper
import montiarc._ast.ASTComponent
import montiarc._ast.ASTConnector
import montiarc._symboltable.ComponentSymbol
import dynamicmontiarc.codegen.helper.DynMAGeneratorHelper

/**
 * TODO: Write me!
 *
 * @author  (last commit) Mutert
 *
 */
class Init{
	
	
  /**
   * Prints the init method for composed components, dynamic and static ones.
   * 
   */
  def static printInitComposed(ComponentSymbol comp) {
    var helper = new ComponentHelper(comp);
    var node = comp.astNode.get as ASTComponent;
    var isDynamic = DynamicMontiArcHelper.isDynamic(node);
    var modeautomaton = DynamicMontiArcHelper.getModeAutomaton(node)
    
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
      «FOR ASTConnector connector : DynMAGeneratorHelper.getInitialConnectors(node)»
          «FOR target : connector.getTargetsList()»
            «IF helper.isIncomingPort(comp, connector.source, target, false)»
              «helper.getConnectorComponentName(connector.source, target, false)».setPort«helper.getConnectorPortName(connector.source, target, false).toFirstUpper»(«helper.getConnectorComponentName(connector.source, target,true)».getPort«helper.getConnectorPortName(connector.source, target, true).toFirstUpper»());
            «ENDIF»
          «ENDFOR»
      «ENDFOR» 
    
      // Initialize all embedded components
    
      «FOR subcomponent : comp.subComponents»
      this.«subcomponent.name».init();
      «ENDFOR»
      
      «IF isDynamic»
      // Set the initial mode of the mode automaton
      this.currentMode = «comp.name»Mode.«modeautomaton.initialModeDeclarationList.get(0).getName()»;
      «ENDIF»
    }
    '''
  }
  
  
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
	
}