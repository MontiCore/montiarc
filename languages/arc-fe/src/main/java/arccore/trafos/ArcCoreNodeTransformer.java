/* (c) https://github.com/MontiCore/monticore */
package arccore.trafos;

import arccore._ast.ASTArcCoreNode;

public interface ArcCoreNodeTransformer {

  void transform(ASTArcCoreNode node);
}