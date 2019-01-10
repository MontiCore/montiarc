/*
 * Copyright (c) 2018 RWTH Aachen. All rights reserved.
 * 
 * http://www.se-rwth.de/
 */
package de.montiarcautomaton.generator.codegen.xtend.atomic.behavior;

import de.montiarcautomaton.generator.codegen.xtend.util.ConfigurationParameters
import de.montiarcautomaton.generator.codegen.xtend.util.Member
import de.montiarcautomaton.generator.codegen.xtend.util.TypeParameters
import de.montiarcautomaton.generator.helper.ComponentHelper
import montiarc._ast.ASTComponent
import montiarc._ast.ASTVariableDeclaration
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
      
      public class «comp.name»Impl«TypeParameters.printFormalTypeParameters(comp)»
      implements IComputable<«comp.name»Input«TypeParameters.printFormalTypeParameters(comp)», «comp.name»Result«TypeParameters.printFormalTypeParameters(comp)»> {
        
      //component variables
      «FOR compVar : comp.variables»
        «Member.print(ComponentHelper.printTypeName((compVar.astNode.get as ASTVariableDeclaration).type), compVar.name, "private")»
      «ENDFOR» 
      
      // config parameters
      «FOR param : (comp.astNode.get as ASTComponent).head.parameterList»
        «Member.print(ComponentHelper.printTypeName(param.type), param.name, "private final")»
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
