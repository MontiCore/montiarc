/* (c) https://github.com/MontiCore/monticore */
package comfortablearc.trafos;

import comfortablearc._ast.ASTComfortableArcNode;

public interface ComfortableArcNodeTransformer {

  void transform(ASTComfortableArcNode node);
}