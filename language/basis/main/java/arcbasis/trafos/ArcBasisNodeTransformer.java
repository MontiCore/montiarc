/* (c) https://github.com/MontiCore/monticore */
package arcbasis.trafos;

import arcbasis._ast.ASTArcBasisNode;

public interface ArcBasisNodeTransformer {

  void transform(ASTArcBasisNode node);
}