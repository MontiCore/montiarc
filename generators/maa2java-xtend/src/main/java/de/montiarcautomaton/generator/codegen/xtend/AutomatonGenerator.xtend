/*******************************************************************************
 * Copyright (c) 2012 itemis AG (http://www.itemis.eu) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/
package de.montiarcautomaton.generator.codegen.xtend

import de.montiarcautomaton.generator.helper.AutomatonHelper
import montiarc._ast.ASTAutomaton
import montiarc._ast.ASTAutomatonBehavior
import montiarc._ast.ASTComponent
import montiarc._ast.ASTElement
import montiarc._symboltable.ComponentSymbol
import montiarc._symboltable.StateSymbol

class AutomatonGenerator extends BehaviorGenerator {

  override String hook(ComponentSymbol comp) {
    var ASTAutomaton automaton = null
    for (ASTElement element : (comp.astNode.get as ASTComponent).body.elementList) {
      if(element instanceof ASTAutomatonBehavior) {
        automaton = element.automaton
      }
    }
    return 
    '''
    private State currentState;
    
    
    private static enum State {
      «FOR state : automaton.getStateDeclaration(0).stateList SEPARATOR ','»
      «state.name»
      «ENDFOR»;
    }
    '''
  }


  override String generateCompute(ComponentSymbol comp) {
    var resultName = comp.name + "Result"
    var ASTAutomaton automaton = null
    var AutomatonHelper helper = AutomatonHelper.newInstance
    for (ASTElement element : (comp.astNode.get as ASTComponent).body.elementList) {
      if(element instanceof ASTAutomatonBehavior) {
        automaton = element.automaton
      }
    }
    return 
    '''
      @Override
      public «resultName» compute(«comp.name»Input input) {
        // inputs
        «FOR inPort : comp.incomingPorts»
        final «inPort.typeReference.name» «inPort.name» = input.get«inPort.name.toFirstUpper»();
        «ENDFOR»
        
        final «resultName» result = new «resultName»();
        
        // first current state to reduce stimuli and guard checks
        switch (currentState) {
        «FOR state : automaton.stateDeclarationList.get(0).stateList»
          case «state.name»:
            «FOR transition : helper.getTransitions(state.symbolOpt.get as StateSymbol)»
              // transition: «transition.toString»
              if («helper.getGuard(transition)») {
                //reaction
                «FOR assignment : helper.getReaction(transition)»
                  «IF assignment.isAssignment»
                    «IF assignment.isVariable(assignment.left)»
                      «assignment.left» = «assignment.right»;
                    «ELSE»
                      result.set«assignment.left.toFirstUpper»(«assignment.right»);
                    «ENDIF»
                  «ELSE»
                    «assignment.right»;  
                  «ENDIF»
                «ENDFOR»
                
                //state change
                currentState = State.«transition.target.name»;
                break;
              }
              
              
            «ENDFOR»
        «ENDFOR»
        
        return result;
      }
    '''
  }

  override String generateGetInitialValues(ComponentSymbol comp) {
    var resultName = comp.name + "Result"
    var ASTAutomaton automaton = null
    var AutomatonHelper helper = AutomatonHelper.newInstance
    for (ASTElement element : (comp.astNode.get as ASTComponent).body.elementList) {
      if(element instanceof ASTAutomatonBehavior) {
        automaton = element.automaton
      }
    }
    return 
    '''
      @Override
      public «resultName» getInitialValues() {
        final «resultName» result = new «resultName»();
        
    
        // initial reaction
        «FOR assignment : helper.getInitialReaction(automaton.getInitialStateDeclaration(0).symbolOpt.get as StateSymbol)»
          «IF assignment.isAssignment»
            «IF helper.isPort(assignment.left)»
              result.set«assignment.left.toFirstUpper»(«assignment.right»);
            «ELSE»
              «assignment.left» = «assignment.right»;
            «ENDIF»
          «ENDIF»
        «ENDFOR»
        
        currentState = State.«automaton.initialStateDeclarationList.get(0).name»;
        return result;
      }
    '''
  }
}
