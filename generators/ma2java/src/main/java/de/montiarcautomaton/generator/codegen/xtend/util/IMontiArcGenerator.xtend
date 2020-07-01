/*
 * Copyright (c) 2019 RWTH Aachen. All rights reserved.
 *
 * http://www.se-rwth.de/
 */
package de.montiarcautomaton.generator.codegen.xtend.util

import arcbasis._symboltable.ComponentTypeSymbol

interface IMontiArcGenerator {
  
   def String generate(ComponentTypeSymbol comp);
   
   def String getArtifactName(ComponentTypeSymbol comp);
  
   def getFileEnding() {
     return "java"
   }
}