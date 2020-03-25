/* (c) https://github.com/MontiCore/monticore */
package de.montiarcautomaton.generator.codegen.xtend.behavior

import de.montiarcautomaton.generator.codegen.xtend.util.Identifier
import de.montiarcautomaton.generator.codegen.xtend.util.Utils
import de.montiarcautomaton.generator.helper.ComponentHelper
import de.montiarcautomaton.generator.visitor.CDAttributeGetterTransformationVisitor
import de.monticore.java.prettyprint.JavaDSLPrettyPrinter
import de.monticore.mcexpressions._ast.ASTExpression
import de.monticore.mcexpressions._ast.ASTNameExpression
import de.monticore.prettyprint.IndentPrinter
import java.util.ArrayList
import java.util.Collection
import java.util.Optional
import java.util.stream.Collectors
import montiarc._ast.ASTAutomaton
import montiarc._ast.ASTAutomatonBehavior
import montiarc._ast.ASTComponent
import montiarc._ast.ASTElement
import montiarc._ast.ASTIOAssignment
import montiarc._symboltable.ComponentSymbol
import montiarc._symboltable.PortSymbol
import montiarc._symboltable.StateSymbol
import montiarc._symboltable.TransitionSymbol
import montiarc._symboltable.VariableSymbol
import montiarc.visitor.NamesInExpressionsDelegatorVisitor
import de.montiarcautomaton.generator.visitor.NamesInExpressionVisitor
import de.montiarcautomaton.generator.visitor.NoDataUsageVisitor
import java.util.Set

/**
 * Prints the automaton behavior of a component.
 * 
 *          $Date$
 */
class AutomatonGenerator extends ABehaviorGenerator {

  def Collection<StateSymbol> getStates(ComponentSymbol comp) {
    return comp.getSpannedScope().getSubScopes().stream().flatMap(
      scope |
        scope.<StateSymbol>resolveLocally(StateSymbol.KIND).stream
    ).collect(Collectors.toList);
  }
  
  def Collection<TransitionSymbol> getTransitions(ComponentSymbol comp) {
    return comp.getSpannedScope().getSubScopes().stream().flatMap(
      scope |
        scope.<TransitionSymbol>resolveLocally(TransitionSymbol.KIND).stream
    ).collect(Collectors.toList);
  }
  

  /**
   * Adds a enum for alls states of the automtaton and the attribute currentState for storing 
   * the current state of the automaton.
   */
  override String hook(ComponentSymbol comp) {
    var ASTAutomaton automaton = null
    for (ASTElement element : (comp.astNode.get as ASTComponent).body.elementList) {
      if (element instanceof ASTAutomatonBehavior) {
        automaton = element.automaton
      }
    }
    return '''
			«Utils.printMember(comp.name + "State", Identifier.currentStateName, "private")»
			
			«printStateEnum(automaton, comp)»
		'''
  }

  /**
   * Prints the compute implementation of automaton behavior.
   */
  override String printCompute(ComponentSymbol comp) {
    var resultName = comp.name + "Result"
    var ASTAutomaton automaton = null
    for (ASTElement element : (comp.astNode.get as ASTComponent).body.elementList) {
      if (element instanceof ASTAutomatonBehavior) {
        automaton = element.automaton
      }
    }
    var helper = new ComponentHelper(comp)
    
    return '''
			@Override
			public «resultName»«Utils.printFormalTypeParameters(comp)»
			      compute(«comp.name»Input«Utils.printFormalTypeParameters(comp)» «Identifier.inputName») {
			    
«««			  Lists all ingoing ports and stores the values of the passed parameter input.
			    // inputs
			    «FOR inPort : comp.allIncomingPorts»
			    	final «ComponentHelper.getRealPortTypeString(comp, inPort)» «inPort.name» = «Identifier.inputName».get«inPort.name.toFirstUpper»();
			    «ENDFOR»
			    
«««			  Initialize result
			    final «resultName»«Utils.printFormalTypeParameters(comp)» «Identifier.resultName» = new «resultName»«Utils.printFormalTypeParameters(comp)»();
			    
«««			  Generate implementation of automaton:
«««			  switch-case statement for every state name
			    switch («Identifier.currentStateName») {
			    «FOR stateDeclaration : automaton.getStateDeclarationList()»
			    «FOR state : stateDeclaration.stateList»
			    	case «state.name»:
			    	  «FOR transition : getTransitions(comp).stream.filter(s | s.source.name == state.name).collect(Collectors.toList)»
			    	  	// transition: «transition.toString»
«««			    	  if statement for each guard of a transition from this state
			    	  	if («IF transition.guardAST.isPresent»«printNullChecks(comp, transition.guardAST.get.guardExpression.expression)» («helper.printExpression(transition.guardAST.get.guardExpression.expression)»)«ELSE» true «ENDIF») {
			    	  	  //reaction
«««			    	  	if true execute reaction of transition
			    	  	  «IF transition.reactionAST.present»
			    	  	  	«FOR assignment : transition.reactionAST.get.getIOAssignmentList»
			    	  	  		«IF assignment.isAssignment»
			    	  	  			«IF isVariable(assignment.name, assignment)»
			    	  	  				«assignment.name» = «helper.printRightHandSide(assignment)»;
			    	  	  			«ELSE»
			    	  	  				«Identifier.resultName».set«assignment.name.toFirstUpper»(«helper.printRightHandSide(assignment)»);
			    	  	  			«ENDIF»
			    	  	  		«ELSE»
			    	  	  			«helper.printRightHandSide(assignment)»;
			    	  	  		«ENDIF»
			    	  	  	«ENDFOR»
			    	  	  «ENDIF»

«««			    	  	and change state to target state of transition
			    	  	  «Identifier.currentStateName» = «comp.name»State.«transition.target.name»;
			    	  	  break;
			    	  	}


			    	  «ENDFOR»
			    «ENDFOR»
			    «ENDFOR»
			    }
			    return result;
			  }
		'''
  }
  
  def String printNullChecks(ComponentSymbol symbol, ASTExpression expression) {
    var StringBuilder builder = new StringBuilder();
    var NamesInExpressionVisitor visitor = new NamesInExpressionVisitor();
    expression.accept(visitor);
    var NoDataUsageVisitor nodataVisitor = new NoDataUsageVisitor();
    expression.accept(nodataVisitor);
    val Set<String> portsComparedToNoData = nodataVisitor.getPortsComparedToNoData;
            
    for(String name : visitor.foundNames) {
      if(symbol.spannedScope.resolve(name, VariableSymbol.KIND).present || symbol.spannedScope.resolve(name, PortSymbol.KIND).present) {
        if(!portsComparedToNoData.contains(name)) {        
          builder.append(" " + name + "!=null &&");
        }
      }
      
    }
    return builder.toString;
  }

  override String printGetInitialValues(ComponentSymbol comp) {
    var resultName = comp.name + "Result"
    var ASTAutomaton automaton = null
    for (ASTElement element : (comp.astNode.get as ASTComponent).body.elementList) {
      if (element instanceof ASTAutomatonBehavior) {
        automaton = element.automaton
      }
    }
    var ComponentHelper helper = new ComponentHelper(comp)
    
    return '''
			@Override
			public «resultName»«Utils.printFormalTypeParameters(comp)»
			getInitialValues() {
«««			initialize initial result
			  final «resultName»«Utils.printFormalTypeParameters(comp)» «Identifier.resultName» = new «resultName»«Utils.printFormalTypeParameters(comp)»();
			  
			  // initial reaction
			  «var StateSymbol initialState = getStates(comp).stream.filter(state | state.isInitial).findFirst.get»
«««			if an initial reaction is present
			  «IF initialState.initialReactionAST.isPresent»
			  	«FOR assignment : initialState.initialReactionAST.get.getIOAssignmentList»
«««			  	set initial result			  	
			  		«IF assignment.isAssignment»
			  			«IF comp.getPort(assignment.name).isPresent»
			  				«Identifier.resultName».set«assignment.name.toFirstUpper»(«helper.printRightHandSide(assignment)»);
			  			«ELSE»
			  				«assignment.name» = «helper.printRightHandSide(assignment)»;
			  			«ENDIF»
			  		«ELSE»
			  			«helper.printRightHandSide(assignment)»;  
			  		«ENDIF»
			  	«ENDFOR»
			  «ENDIF»
			  
			  «Identifier.currentStateName» = «comp.name»State.«automaton.initialStateDeclarationList.get(0).name»;
			  return «Identifier.resultName»;
			}
		'''
  }

  /**
   * Prints a enum with all states of the automaton.
   */
  def private String printStateEnum(ASTAutomaton automaton, ComponentSymbol comp) {
    return '''
			private static enum «comp.name»State {
			«FOR stateDeclaration : automaton.getStateDeclarationList() SEPARATOR ','»
        «FOR state : stateDeclaration.stateList SEPARATOR ','»
          «state.name»
        «ENDFOR»
      «ENDFOR»;
			}
		'''
  }

  /**
   * Returns <tt>true</tt> if the given name is a variable name.
   * 
   * @param name
   * @return
   */
  def private boolean isVariable(String name, ASTIOAssignment assignment) {
    var Optional<VariableSymbol> symbol = assignment.getEnclosingScopeOpt().get().<VariableSymbol>resolve(name,
      VariableSymbol.KIND);
    if (symbol.isPresent()) {
      return true;
    }
    return false;
  }
}
