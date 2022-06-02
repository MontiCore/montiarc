/* (c) https://github.com/MontiCore/monticore */
package arcbasis.trafos;

import arcbasis._ast.ASTComponentType;

public interface ComponentTypeTransformer {

  void transform(ASTComponentType node);
}