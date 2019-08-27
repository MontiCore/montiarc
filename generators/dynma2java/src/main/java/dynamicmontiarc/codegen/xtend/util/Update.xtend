/* (c) https://github.com/MontiCore/monticore */
package dynamicmontiarc.codegen.xtend.util

import dynamicmontiarc._ast.ASTModeAutomaton
import dynamicmontiarc._ast.ASTUseStatement
import dynamicmontiarc.helper.DynamicMontiArcHelper
import java.util.ArrayList
import java.util.List
import montiarc._ast.ASTComponent
import montiarc._symboltable.ComponentSymbol

/**
 * Prints the update() method for both atomic and composed components.
 */
class Update {
  
  /**
   * Delegates to the right print method.
   */
  def static print(ComponentSymbol comp) {
    if (comp.isDecomposed) {
    	val compNode = comp.astNode.get as ASTComponent
    	if(DynamicMontiArcHelper.isDynamic(compNode)){
			return printUpdateComposedDynamic(comp, DynamicMontiArcHelper.getModeAutomaton(compNode))
    	} else {
    		return printUpdateComposed(comp)
    	}
    } else {
    	return printUpdateAtomic(comp)
    }
  }
  
  def private static printUpdateComposed(ComponentSymbol comp){
    return 
    '''
			@Override
			public void update() {
			  // update subcomponent instances
			  «IF comp.superComponent.present»
			  	super.update();
			  «ENDIF»
			  «FOR subcomponent : comp.subComponents»
			  	this.«subcomponent.name».update();
			  «ENDFOR»
			}
		'''
  } 
  
  def private static printUpdateComposedDynamic(ComponentSymbol comp, ASTModeAutomaton modeAutomaton){
    return 
    '''
	  @Override
	  public void update() {
	    «IF comp.superComponent.present»
		  super.update();
		«ENDIF»
		// update all subcompoent instances that are active in the current mode
	    «FOR modeName : modeAutomaton.getModeNames()»
		  if(this.currentMode.equals(«comp.name»Mode.«modeName»)) {
		  «FOR subcomponent : modeAutomaton.getActiveSubcomponentsInMode(modeName)»
		    this.«subcomponent».update();
		  «ENDFOR»
		  }
		«ENDFOR»
		}
	'''
  }
  
  def private static printUpdateAtomic(ComponentSymbol comp){
    return 
    '''
	@Override
	public void update() {
	  «IF comp.superComponent.present»
		super.update();
	  «ENDIF»
			
	  // update computed value for next computation cycle in all outgoing ports
	  «FOR portOut : comp.outgoingPorts»
		this.«portOut.name».update();
	  «ENDFOR»
	}
	'''
  }
}
