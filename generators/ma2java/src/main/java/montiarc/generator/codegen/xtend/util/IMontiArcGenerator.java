/* (c) https://github.com/MontiCore/monticore */
package montiarc.generator.codegen.xtend.util;

import arcbasis._symboltable.ComponentTypeSymbol;


public interface IMontiArcGenerator {
  String generate(final ComponentTypeSymbol comp);
  
  String getArtifactName(final ComponentTypeSymbol comp);
  
  default String getFileEnding() {
    return "java";
  }
}