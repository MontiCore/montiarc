/*
 * Copyright (c) 2018 RWTH Aachen. All rights reserved.
 * 
 * http://www.se-rwth.de/
 */
package de.montiarcautomaton.generator.codegen.xtend.behavior;

import de.montiarcautomaton.generator.codegen.xtend.util.Utils
import montiarc._ast.ASTComponent
import montiarc._symboltable.ComponentSymbol

/**
 * Abstract class for generating a component's behavior implementation. The concrete behavior implementations must implement the abstract print methods 
 * for compute() and getInitialValues().  
 * 
 * @author  Pfeiffer
 * @version $Revision$,
 *          $Date$
 */
abstract class ABehaviorGenerator {

  /**
   * Implementing this method is mandatory.
   * 
   * @return the implementation of the compute() method
   */
  def String printCompute(ComponentSymbol comp);

  /**
   * Implementing this method is mandatory.
   * 
   * @return the implementation of the getInitialValues() method
   */
  def String printGetInitialValues(ComponentSymbol comp);

  /**
   * This method can be used to add additional code to the implementation class without.   
   */
  def String hook(ComponentSymbol comp);

  /**
   * Entry point for generating a component's implementation.
   * 
   */
  def String generate(ComponentSymbol comp) {
    return '''
      package «comp.packageName»;
      
      import «comp.packageName».«comp.name»Result;
      import «comp.packageName».«comp.name»Input;
      «FOR _import : comp.imports»
        import «_import.statement»«IF _import.isStar».*«ENDIF»;
      «ENDFOR»
      
      import de.montiarcautomaton.runtimes.timesync.implementation.IComputable;
      
      public class «comp.name»Impl«Utils.printFormalTypeParameters(comp)»
      implements IComputable<«comp.name»Input«Utils.printFormalTypeParameters(comp)», «comp.name»Result«Utils.printFormalTypeParameters(comp)»> {
        
         «««	print members for component variables 
    		«FOR compVar : comp.variables»
         «Utils.printVariables(comp)»
        «ENDFOR» 
        
        «««  print members for component's configuration parameters
    		«FOR param : (comp.astNode.get as ASTComponent).head.parameterList»
          «Utils.printConfigParameters(comp)»
        «ENDFOR»
        
        «hook(comp)»
        
        «printConstructor(comp)»
        
        «printGetInitialValues(comp)»
        
        «printCompute(comp)»
         
      }
    '''

  }

  def String printConstructor(ComponentSymbol comp) {
    return '''
      public «comp.name»Impl(«Utils.printConfiurationParametersAsList(comp)») {
        «FOR param : comp.configParameters»
          this.«param.name» = «param.name»; 
        «ENDFOR»
      }
    '''

  }

}
