/* (c) https://github.com/MontiCore/monticore */
package montiarc.trafos;

import montiarc._ast.ASTMACompilationUnit;

public interface MACompilationUnitTransformer {

  void transform(ASTMACompilationUnit node);
}