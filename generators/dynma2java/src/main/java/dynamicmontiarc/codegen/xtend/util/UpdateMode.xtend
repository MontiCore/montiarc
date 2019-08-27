/* (c) https://github.com/MontiCore/monticore */
package dynamicmontiarc.codegen.xtend.util

import montiarc._symboltable.ComponentSymbol
import dynamicmontiarc._ast.ASTModeAutomaton
import dynamicmontiarc.codegen.helper.DynMAGeneratorHelper
import de.montiarcautomaton.generator.helper.ComponentHelper
import java.util.HashMap
import java.util.List
import java.util.ArrayList
import dynamicmontiarc._ast.ASTModeTransition
import montiarc.visitor.NamesInExpressionsDelegatorVisitor
import java.util.stream.Collectors
import de.monticore.mcexpressions._ast.ASTNameExpression

/**
 * TODO: Write me!
 *
 * @author  (last commit)  Mutert
 *
 */
class UpdateMode {

  static def printUpdateMode(ComponentSymbol comp, ASTModeAutomaton automaton){
	
	var helper = new ComponentHelper(comp)
	var portNullChecks = new HashMap<ASTModeTransition, List<String>>()
	var variableNullChecks = new HashMap<ASTModeTransition, List<String>>()
	var ev = new NamesInExpressionsDelegatorVisitor();
    
    for (transition : automaton.modeTransitionList) {
    	ev.foundNames.clear()
		var list = new ArrayList<String>()
		if(transition.guardOpt.isPresent) {
			transition.guard.guardExpression.expression.accept(ev)
			var foundNames = ev.foundNames.keySet
				.stream.map([n | n.getName()])
				.collect(Collectors.toList())
			var portList = new ArrayList<String>
			var varList = new ArrayList<String>
			for(name : foundNames) {
				if(comp.getPort(name, true).isPresent && comp.getPort(name, true).get.isIncoming){
					portList.add(name)
				} else if(comp.getVariable(name).isPresent){
					varList.add(name)
				}
			}
			portNullChecks.put(transition, portList)
			variableNullChecks.put(transition, varList)
		}
	}
	
  return '''
	  private void updateMode() {
	  	
	  	if(this.currentMode == null) {
	  		return;
	  	}
	  	
	  «FOR incomingPort : comp.allIncomingPorts»
	    final «ComponentHelper.getRealPortTypeString(comp, incomingPort)» «incomingPort.name» = this.«incomingPort.name».getCurrentValue();	  
	  «ENDFOR»
	  	
	  «FOR modeTransition : automaton.modeTransitionList»
	  	if(this.currentMode.equals(«comp.name»Mode.«modeTransition.source»)
«««		  	If the guard is present we have to print it as an additional condition
   	  «IF modeTransition.guardOpt.isPresent»
		  	 «FOR portName : portNullChecks.get(modeTransition)»
			&& «portName» != null && «portName» != null
			«ENDFOR»
  			«FOR varName : variableNullChecks.get(modeTransition)»
  			&& «varName» != null
  			«ENDFOR»
	  		&& «helper.printExpression(modeTransition.guardOpt.get.guardExpression.expression)»
	  	  «ENDIF»
	  	) {
	  	
	  	  «IF modeTransition.reactionOpt.isPresent»
	  	    // Reaction of the mode transition
	  	    «FOR assignment : modeTransition.reaction.getIOAssignmentList»
	  	      «assignment.name» = «helper.printRightHandSide(assignment)»;
	  	    «ENDFOR»
	  	  «ENDIF»
	  
	  	  // Handle switching of the mode
	  
	  	  «FOR newConnector : DynMAGeneratorHelper.getPositiveDelta(automaton, modeTransition.source, modeTransition.target)»
«««	  	  Connector from embedded component to the embedding component
		    «IF newConnector.source.sizeParts == 1»
		      // Connector(s) from input port(s) to input port(s) of embedded component(s)
		      «newConnector.getTargets(0).getPart(0)».setPort«newConnector.getTargets(0).getPart(1).toFirstUpper»(this.getPort«newConnector.source.toString.toFirstUpper»());
		    «ELSEIF newConnector.getTargets(0).sizeParts == 1»
		      // Connector(s) from embedded components output port(s) to output port(s) of the component
		      «newConnector.getTargets(0).toString» = «newConnector.source.partList.get(0)».getPort«newConnector.source.getPart(1).toFirstUpper»();
			«ELSE»
			  // Connector(s) from embedded components output port(s) to input port(s) of embedded component(s)
			  «newConnector.getTargets(0).getPart(0)».setPort«newConnector.getTargets(0).getPart(1).toFirstUpper»(«newConnector.source.getPart(0)».getPort«newConnector.source.getPart(1).toFirstUpper»());
		    «ENDIF»
«««		  Connector from an embedded component to another embedded component

	  	  «ENDFOR»
	  
	  	  // Set current mode
	  	  this.currentMode = «comp.name»Mode.«modeTransition.target»;
	  	  return;
	    }
	  «ENDFOR»
	  	
	  	
	  }
  '''
  }
}