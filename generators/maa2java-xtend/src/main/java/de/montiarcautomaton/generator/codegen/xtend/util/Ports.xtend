/*
 * Copyright (c) 2019 RWTH Aachen. All rights reserved.
 *
 * http://www.se-rwth.de/
 */
package de.montiarcautomaton.generator.codegen.xtend.util

import de.montiarcautomaton.generator.helper.ComponentHelper
import java.util.Collection
import montiarc._ast.ASTPort
import montiarc._symboltable.PortSymbol

/**
 * TODO: Write me!
 *
 * @author  (last commit) $Author$
 * @version $Revision$,
 *          $Date$
 * @since   TODO: add version number
 *
 */
class Ports {
  
  def static String print(Collection<PortSymbol> ports) {
    
    return 
    '''
    «FOR port : ports»
    «var type = ComponentHelper.getRealPortTypeString(port.component.get, port)»
    «var name = port.name»
    
    protected Port<«type»> «name»;
    
    public Port<«type»> getPort«name.toFirstUpper»() {
          return this.«name»;
    }
    
    public void setPort«name.toFirstUpper»(Port<«type»> «name») {
          this.«name» = «name»;
    }
    
    «ENDFOR»
    '''
  } 
}