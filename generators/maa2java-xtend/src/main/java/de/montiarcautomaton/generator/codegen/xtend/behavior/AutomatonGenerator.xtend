/*******************************************************************************
 * Copyright (c) 2012 itemis AG (http://www.itemis.eu) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/
package de.montiarcautomaton.generator.codegen.xtend.behavior

import de.montiarcautomaton.generator.codegen.xtend.util.Identifier
import de.montiarcautomaton.generator.codegen.xtend.util.Utils
import de.montiarcautomaton.generator.helper.ComponentHelper
import de.montiarcautomaton.generator.visitor.CDAttributeGetterTransformationVisitor
import de.monticore.java.prettyprint.JavaDSLPrettyPrinter
import de.monticore.mcexpressions._ast.ASTExpression
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
import montiarc._ast.ASTValueList
import montiarc._symboltable.ComponentSymbol
import montiarc._symboltable.StateSymbol
import montiarc._symboltable.TransitionSymbol
import montiarc._symboltable.VariableSymbol

/**
 * Prints the automaton behavior of a component.
 * 
 * @author  Pfeiffer
 * @version $Revision$,
 *          $Date$
 */
class AutomatonGenerator extends ABehaviorGenerator {

  var Collection<StateSymbol> states

  var Collection<VariableSymbol> variables

  var Collection<TransitionSymbol> transitions

  var ComponentSymbol comp;

  new(ComponentSymbol component) {
    this.comp = comp
    this.states = new ArrayList;
    this.transitions = new ArrayList;
    this.variables = new ArrayList;

    component.getSpannedScope().getSubScopes().stream().forEach(
      scope |
        scope.<StateSymbol>resolveLocally(StateSymbol.KIND).forEach(state|this.states.add(state))
    );
    component.getSpannedScope().getSubScopes().stream().forEach(
      scope |
        scope.<TransitionSymbol>resolveLocally(TransitionSymbol.KIND).forEach(transition|
          this.transitions.add(transition)
        )
    );
    // variables can only be defined in the component's body unlike in JavaP
    component.getSpannedScope().<VariableSymbol>resolveLocally(VariableSymbol.KIND).forEach(
      variable |
        this.variables.add(variable)
    );
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
			    
«««			  Generate implementation of automaton
			    // first current state to reduce stimuli and guard checks
			    switch («Identifier.currentStateName») {
			    «FOR state : automaton.stateDeclarationList.get(0).stateList»
			    	case «state.name»:
			    	  «FOR transition : transitions.stream.filter(s | s.source.name == state.name).collect(Collectors.toList)»
			    	  	// transition: «transition.toString»
			    	  	if («IF transition.guardAST.isPresent»«printExpression(transition.guardAST.get.guardExpression.expression)»«ELSE» true «ENDIF») {
			    	  	  //reaction
			    	  	  «IF transition.reactionAST.present»
			    	  	  	«FOR assignment : transition.reactionAST.get.getIOAssignmentList»
			    	  	  		«IF assignment.isAssignment»
			    	  	  			«IF isVariable(assignment.name, assignment)»
			    	  	  				«assignment.name» = «printRightHandSide(assignment)»;
			    	  	  			«ELSE»
			    	  	  				«Identifier.resultName».set«assignment.name.toFirstUpper»(«printRightHandSide(assignment)»);
			    	  	  			«ENDIF»
			    	  	  		«ELSE»
			    	  	  			«printRightHandSide(assignment)»;  
			    	  	  		«ENDIF»
			    	  	  	«ENDFOR»
			    	  	  «ENDIF»
			    	  	  
			    	  	  //state change
			    	  	  «Identifier.currentStateName» = «comp.name»State.«transition.target.name»;
			    	  	  break;
			    	  	}
			    	  	
			    	  	
			    	  «ENDFOR»
			    «ENDFOR»
			    }
			    return result;
			  }
		'''
  }

  override String printGetInitialValues(ComponentSymbol comp) {
    var resultName = comp.name + "Result"
    var ASTAutomaton automaton = null
    for (ASTElement element : (comp.astNode.get as ASTComponent).body.elementList) {
      if (element instanceof ASTAutomatonBehavior) {
        automaton = element.automaton
      }
    }
    return '''
			@Override
			public «resultName»«Utils.printFormalTypeParameters(comp)»
			getInitialValues() {
			  final «resultName»«Utils.printFormalTypeParameters(comp)» «Identifier.resultName» = new «resultName»«Utils.printFormalTypeParameters(comp)»();
			  
			  // initial reaction
			  «var StateSymbol initialState = states.stream.filter(state | state.isInitial).findFirst.get»
			  «IF initialState.initialReactionAST.isPresent»
			  	«FOR assignment : initialState.initialReactionAST.get.getIOAssignmentList»
			  		«IF assignment.isAssignment»
			  			«IF comp.getPort(assignment.name).isPresent»
			  				«Identifier.resultName».set«assignment.name.toFirstUpper»(«printRightHandSide(assignment)»);
			  			«ELSE»
			  				«assignment.name» = «printRightHandSide(assignment)»;
			  			«ENDIF»
			  		«ELSE»
			  			«printRightHandSide(assignment)»;  
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
			«FOR state : automaton.getStateDeclaration(0).stateList SEPARATOR ','»
				«state.name»
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

  /**
   * Returns the right side of an assignment/comparison. ValueLists &
   * Alternatives are not supported.
   * 
   * @return
   */
  def private String printRightHandSide(ASTIOAssignment assignment) {
    if (assignment.isPresentAlternative()) {
      throw new RuntimeException("Alternatives not supported.");
    } else {
      var ASTValueList vl = assignment.getValueList();
      if (vl.isPresentValuation()) {
        return printExpression(vl.getValuation().getExpression(), assignment.isAssignment);
      } else {
        throw new RuntimeException("ValueLists not supported.");
      }
    }
  }

  /**
   * Prints the java expression of the given AST expression node.
   * 
   * @param expr
   * @return
   */
  def private String printExpression(ASTExpression expr, boolean isAssignment) {
    var IndentPrinter printer = new IndentPrinter();
    var JavaDSLPrettyPrinter prettyPrinter = new JavaDSLPrettyPrinter(printer);
    if (isAssignment) {
      prettyPrinter = new CDAttributeGetterTransformationVisitor(printer);
    }
    expr.accept(prettyPrinter);
    return printer.getContent();
  }

  def private String printExpression(ASTExpression expr) {
    return printExpression(expr, true);
  }

}
