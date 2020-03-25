/* (c) https://github.com/MontiCore/monticore */
package de.montiarcautomaton.generator.codegen.xtend.behavior;

import de.montiarcautomaton.generator.codegen.xtend.util.Utils
import montiarc._symboltable.ComponentSymbol

/**
 * Abstract class for generating a component's behavior implementation. The concrete behavior implementations must implement the abstract print methods 
 * for compute() and getInitialValues().  
 * 
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
      «Utils.printPackage(comp)»
      
      import «Utils.printPackageWithoutKeyWordAndSemicolon(comp)».«comp.name»Result;
      import «Utils.printPackageWithoutKeyWordAndSemicolon(comp)».«comp.name»Input;
      «FOR _import : comp.imports»
        import «_import.statement»«IF _import.isStar».*«ENDIF»;
      «ENDFOR»
      
      import de.montiarcautomaton.runtimes.timesync.implementation.IComputable;
      
      public class «comp.name»Impl«Utils.printFormalTypeParameters(comp)»
      implements IComputable<«comp.name»Input«Utils.printFormalTypeParameters(comp)», «comp.name»Result«Utils.printFormalTypeParameters(comp)»> {
        
        «««	print members for component variables 
        «Utils.printVariables(comp)»
        
        «««  print members for component's configuration parameters
        «Utils.printConfigParameters(comp)»
        
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
