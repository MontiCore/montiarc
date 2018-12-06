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
import de.montiarcautomaton.generator.helper.ComponentHelper

class AutomatonGenerator extends BehaviorGenerator {

  override String hook(ComponentSymbol comp) {
    var compHelper = new ComponentHelper(comp)
    var ASTAutomaton automaton = null
    for (ASTElement element : (comp.astNode.get as ASTComponent).body.elementList) {
      if(element instanceof ASTAutomatonBehavior) {
        automaton = element.automaton
      }
    }
    return 
    '''
    private «comp.name»State «compHelper.currentStateName»;
    
    
    private static enum «comp.name»State {
      «FOR state : automaton.getStateDeclaration(0).stateList SEPARATOR ','»
      «state.name»
      «ENDFOR»;
    }
    '''
  }


  override String generateCompute(ComponentSymbol comp) {
    var resultName = comp.name + "Result"
    var ASTAutomaton automaton = null
    var AutomatonHelper helper = new AutomatonHelper(comp)
    var ComponentHelper compHelper = new ComponentHelper(comp)
    for (ASTElement element : (comp.astNode.get as ASTComponent).body.elementList) {
      if(element instanceof ASTAutomatonBehavior) {
        automaton = element.automaton
      }
    }
    return 
    '''
    @Override
    public «comp.name»Result
          «IF helper.isGeneric»
            «FOR generic : helper.genericParameters SEPARATOR ','»
              «generic»
            «ENDFOR»
          «ENDIF»
          compute(«comp.name»Input
          «IF helper.isGeneric»
            «FOR generic : helper.genericParameters SEPARATOR ','»
              «generic»
            «ENDFOR»
          «ENDIF» «helper.inputName») {
        // inputs
        «FOR inPort : comp.incomingPorts»
        final «helper.printPortType(inPort)» «inPort.name» = «helper.inputName».get«inPort.name.toFirstUpper»();
        «ENDFOR»
        
        final «resultName» «helper.resultName» = new «resultName»();
        
        // first current state to reduce stimuli and guard checks
        switch («compHelper.currentStateName») {
        «FOR state : automaton.stateDeclarationList.get(0).stateList»
          case «state.name»:
            «FOR transition : helper.getTransitions(state.symbolOpt.get as StateSymbol)»
              // transition: «transition.toString»
              if («IF transition.guardAST.isPresent»«helper.getGuard(transition)»«ELSE» true «ENDIF») {
                //reaction
                «FOR assignment : helper.getReaction(transition)»
                  «IF assignment.isAssignment»
                    «IF assignment.isVariable(assignment.left)»
                      «assignment.left» = «assignment.right»;
                    «ELSE»
                      «helper.resultName».set«assignment.left.toFirstUpper»(«assignment.right»);
                    «ENDIF»
                  «ELSE»
                    «assignment.right»;  
                  «ENDIF»
                «ENDFOR»
                
                //state change
                «compHelper.currentStateName» = «comp.name»State.«transition.target.name»;
                break;
              }
              
              
            «ENDFOR»
        «ENDFOR»
        }
        return result;
      }
    '''
  }

  override String generateGetInitialValues(ComponentSymbol comp) {
    var resultName = comp.name + "Result"
    var ASTAutomaton automaton = null
    var ComponentHelper compHelper = new ComponentHelper(comp)
    var AutomatonHelper helper = new AutomatonHelper(comp)
    for (ASTElement element : (comp.astNode.get as ASTComponent).body.elementList) {
      if(element instanceof ASTAutomatonBehavior) {
        automaton = element.automaton
      }
    }
    return 
    '''
      @Override
      public «resultName» 
            «IF helper.isGeneric»
              «FOR generic : helper.genericParameters SEPARATOR ','»
                «generic»
              «ENDFOR»
            «ENDIF» 
      getInitialValues() {
        final «resultName» «helper.resultName» = new «resultName»();
        
    
        // initial reaction
        «FOR assignment : helper.getInitialReaction(helper.initialState)»
          «IF assignment.isAssignment»
            «IF helper.isPort(assignment.left)»
              «helper.resultName».set«assignment.left.toFirstUpper»(«assignment.right»);
            «ELSE»
              «assignment.left» = «assignment.right»;
            «ENDIF»
          «ENDIF»
        «ENDFOR»
        
        «compHelper.currentStateName» = «comp.name»State.«automaton.initialStateDeclarationList.get(0).name»;
        return «helper.resultName»;
      }
    '''
  }
  
}
