/*
 * Copyright (c) 2018 RWTH Aachen. All rights reserved.
 * 
 * http://www.se-rwth.de/
 */
package de.montiarcautomaton.generator.codegen.xtend.util

import de.montiarcautomaton.generator.helper.ComponentHelper
import montiarc._ast.ASTComponent
import montiarc._symboltable.ComponentSymbol
import java.util.List
import de.monticore.types.types._ast.ASTTypeVariableDeclaration
import java.util.ArrayList

/**
 * TODO: Write me!
 * 
 * @author  (last commit) $Author$
 * @version $Revision$,
 *          $Date$
 * @since   TODO: add version number
 * 
 */
class Generics {
  def static print(ComponentSymbol comp) {
    return '''
      «IF (comp.astNode.get as ASTComponent).head.isPresentGenericTypeParameters»
        <
          «FOR generic : getGenericParameters(comp) SEPARATOR ','»
            «generic»
          «ENDFOR»
        >
      «ENDIF»
    '''
  }

  def private static List<String> getGenericParameters(ComponentSymbol comp) {
    var componentNode = comp.astNode.get as ASTComponent
    var List<String> output = new ArrayList
    if (componentNode.getHead().isPresentGenericTypeParameters()) {
      var List<ASTTypeVariableDeclaration> parameterList = componentNode.getHead().getGenericTypeParameters().
        getTypeVariableDeclarationList()
      for (ASTTypeVariableDeclaration variableDeclaration : parameterList) {
        output.add(variableDeclaration.getName())
      }
    }
    return output;
  }

}
