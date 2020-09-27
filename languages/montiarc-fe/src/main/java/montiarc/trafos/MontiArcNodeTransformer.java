/* (c) https://github.com/MontiCore/monticore */
package montiarc.trafos;

import montiarc._ast.ASTMontiArcNode;

public interface MontiArcNodeTransformer {

  void transform(ASTMontiArcNode node);
}