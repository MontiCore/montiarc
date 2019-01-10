/*
 * Copyright (c) 2019 RWTH Aachen. All rights reserved.
 * 
 * http://www.se-rwth.de/
 */
package de.montiarcautomaton.generator.codegen.xtend.util

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
class Subcomponents {

  def static String print(ComponentSymbol comp) {
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
