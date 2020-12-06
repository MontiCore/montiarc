/* (c) https://github.com/MontiCore/monticore */
package montiarc.generator.codegen.xtend.util

import montiarc.generator.helper.ComponentHelper
import arcbasis._symboltable.ComponentTypeSymbol

/**
 * Prints member and getter for component's subcomponents.
 */
class Subcomponents {

  def String print(ComponentTypeSymbol comp) {
    return '''
      «FOR subcomponent : comp.subComponents»
        «var type = ComponentHelper.getSubComponentTypeName(subcomponent)»
        protected «type» «subcomponent.name»;
        
        public «type» getComponent«subcomponent.name.toFirstUpper»() {
          return this.«subcomponent.name»;
        }
      «ENDFOR»
    '''
  }
}
