/*
 * Copyright (c) 2018 RWTH Aachen. All rights reserved.
 * 
 * http://www.se-rwth.de/
 */
package de.montiarcautomaton.generator.codegen.xtend.atomic.behavior;

import de.montiarcautomaton.generator.codegen.xtend.util.ConfigurationParameters
import de.montiarcautomaton.generator.codegen.xtend.util.Generics
import de.montiarcautomaton.generator.codegen.xtend.util.Member
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

  def String printCompute(ComponentSymbol comp);

  def String printGetInitialValues(ComponentSymbol comp);

  def String hook(ComponentSymbol comp);

  def String generate(ComponentSymbol comp) {
    var ComponentHelper helper = new ComponentHelper(comp)
    return '''
      package «comp.packageName»;
      
      import «comp.packageName».«comp.name»Result;
      import «comp.packageName».«comp.name»Input;
      «FOR _import : comp.imports»
        import «_import.statement»«IF _import.isStar».*«ENDIF»;
      «ENDFOR»
      
      import de.montiarcautomaton.runtimes.timesync.implementation.IComputable;
      
      public class «comp.name»Impl«Generics.printGenerics(comp)»
      implements IComputable<«comp.name»Input, «comp.name»Result> {
        
      //component variables
      «FOR compVar : comp.variables»
        «Member.printMember(helper.printVariableTypeName(compVar), compVar.name, "private")»
      «ENDFOR» 
      
      // config parameters
      «FOR param : comp.configParameters»
        «Member.printMember(helper.printParamTypeName(param), param.name, "private final")»
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
      public «comp.name»Impl(«ConfigurationParameters.print(comp)») {
        «FOR param : comp.configParameters»
          this.«param.name» = «param.name»; 
        «ENDFOR»
      }
    '''

  }

}
