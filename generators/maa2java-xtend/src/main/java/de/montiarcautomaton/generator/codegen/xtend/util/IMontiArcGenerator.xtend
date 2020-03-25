/*
 * Copyright (c) 2019 RWTH Aachen. All rights reserved.
 *
 * http://www.se-rwth.de/
 */
package de.montiarcautomaton.generator.codegen.xtend.util

import montiarc._symboltable.ComponentSymbol

/**
 * TODO: Write me!
 *
 *          $Date$
 *
 */
interface IMontiArcGenerator {
  
   def String generate(ComponentSymbol comp);
   
   def String getArtifactName(ComponentSymbol comp);
  
   def getFileEnding() {
     return "java"
   }
}
