/* (c) https://github.com/MontiCore/monticore */
package genericarc.trafos;

import genericarc._ast.ASTGenericArcNode;

public interface GenericArcNodeTransformer {

  void transform(ASTGenericArcNode node);
}