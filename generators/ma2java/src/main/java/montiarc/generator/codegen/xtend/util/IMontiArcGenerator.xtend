/* (c) https://github.com/MontiCore/monticore */
package montiarc.generator.codegen.xtend.util

import arcbasis._symboltable.ComponentTypeSymbol

interface IMontiArcGenerator {
  
   def String generate(ComponentTypeSymbol comp);
   
   def String getArtifactName(ComponentTypeSymbol comp);
  
   def getFileEnding() {
     return "java"
   }
}
