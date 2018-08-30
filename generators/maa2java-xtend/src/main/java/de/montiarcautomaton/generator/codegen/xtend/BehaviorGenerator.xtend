/*
 * Copyright (c) 2018 RWTH Aachen. All rights reserved.
 * 
 * http://www.se-rwth.de/
 */
package de.montiarcautomaton.generator.codegen.xtend

import de.montiarcautomaton.generator.helper.ComponentHelper
import montiarc._symboltable.ComponentSymbol

/**
 * TODO: Write me!
 * 
 * @author  (last commit) $Author$
 * @version $Revision$,
 *          $Date$
 * @since   TODO: add version number
 * 
 */
abstract class BehaviorGenerator {

  def String generateCompute(ComponentSymbol comp);
  
  def String generateGetInitialValues(ComponentSymbol comp);

  def String hook(ComponentSymbol comp);

  def String generate(ComponentSymbol comp) {
    var ComponentHelper helper = new ComponentHelper(comp)
    return '''
      package «comp.packageName»;
      
      import «comp.packageName».«comp.name»Result;
      import «comp.packageName».«comp.name»Input;
      «FOR _import : comp.imports»
        import «_import.statement»«IF _import.isStar».*«ENDIF»
      «ENDFOR»
      
      import de.montiarcautomaton.runtimes.timesync.implementation.IComputable;
      
      public class «comp.name»Impl
      «IF helper.isGeneric»
        «FOR generic : helper.genericParameters SEPARATOR ','»
          «generic»
        «ENDFOR»
      «ENDIF» 
      implements IComputable<«comp.name»Input, «comp.name»Result> {
        
        //component variables
        «FOR compVar : comp.variables»
          private «compVar.typeReference.name» «compVar.name»;
        «ENDFOR» 
        
        // config parameters
        «FOR param : comp.configParameters»
          private final «param.type.name» «param.name»; 
        «ENDFOR»
        
        
        «hook(comp)»
        
        «generateConstructor(comp)»
        
        «generateGetInitialValues(comp)»
        
        «generateCompute(comp)»
        
      
      
    '''

  }

  def String generateConstructor(ComponentSymbol comp) {
    return '''
      public «comp.name»(«FOR param : comp.configParameters SEPARATOR ','» «param.type.name» «param.name» «ENDFOR») {
        «FOR param : comp.configParameters»
        this.«param.name» = «param.name»; 
        «ENDFOR»
      }
    '''

  }


}
