/*******************************************************************************
 * Copyright (c) 2012 itemis AG (http://www.itemis.eu) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/
package de.montiarcautomaton.generator.codegen.xtend.atomic.behavior.automaton

import de.montiarcautomaton.generator.codegen.xtend.atomic.behavior.BehaviorGenerator
import de.montiarcautomaton.generator.codegen.xtend.util.Generics
import de.montiarcautomaton.generator.helper.AutomatonHelper
import de.montiarcautomaton.generator.helper.ComponentHelper
import montiarc._ast.ASTAutomaton
import montiarc._ast.ASTAutomatonBehavior
import montiarc._ast.ASTComponent
import montiarc._ast.ASTElement
import montiarc._symboltable.ComponentSymbol
import montiarc._symboltable.StateSymbol
import montiarc._symboltable.VariableSymbol
import de.monticore.mcexpressions._ast.ASTExpression
import de.monticore.prettyprint.IndentPrinter
import de.montiarcautomaton.generator.visitor.CDAttributeGetterTransformationVisitor
import de.monticore.java.prettyprint.JavaDSLPrettyPrinter
import java.util.Optional
import montiarc._ast.ASTValueList
import montiarc._ast.ASTIOAssignment

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


  override String printCompute(ComponentSymbol comp) {
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
    public «resultName»«Generics.print(comp)»
          compute(«comp.name»Input«Generics.print(comp)» «helper.inputName») {
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
                    «IF isVariable(assignment.name, assignment)»
                      «assignment.name» = «printRightHandSide(assignment)»;
                    «ELSE»
                      «helper.resultName».set«assignment.name.toFirstUpper»(«printRightHandSide(assignment)»);
                    «ENDIF»
                  «ELSE»
                    «printRightHandSide(assignment)»;  
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

  override String printGetInitialValues(ComponentSymbol comp) {
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
      public «resultName»«Generics.print(comp)»
      getInitialValues() {
        final «resultName» «helper.resultName» = new «resultName»();
        
        // initial reaction
        «FOR assignment : helper.getInitialReaction(helper.initialState)»
          «IF assignment.isAssignment»
            «IF helper.isPort(assignment.name)»
              «helper.resultName».set«assignment.name.toFirstUpper»(«printRightHandSide(assignment)»);
            «ELSE»
              «assignment.name» = «printRightHandSide(assignment)»;
            «ENDIF»
          «ENDIF»
        «ENDFOR»
        
        «compHelper.currentStateName» = «comp.name»State.«automaton.initialStateDeclarationList.get(0).name»;
        return «helper.resultName»;
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
    var Optional<VariableSymbol> symbol = assignment.getEnclosingScopeOpt().get()
        .<VariableSymbol> resolve(name, VariableSymbol.KIND);
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
    }
    else {
      var ASTValueList vl = assignment.getValueList();
      if (vl.isPresentValuation()) {
        return printExpression(vl.getValuation().getExpression(), assignment);
      }
      else {
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
  def private String printExpression(ASTExpression expr, ASTIOAssignment assignment) {
    var IndentPrinter printer = new IndentPrinter();
    var JavaDSLPrettyPrinter prettyPrinter = new JavaDSLPrettyPrinter(printer);
    if (assignment.isAssignment) {
      prettyPrinter = new CDAttributeGetterTransformationVisitor(printer);
    }
    expr.accept(prettyPrinter);
    return printer.getContent();
  }
  
}
