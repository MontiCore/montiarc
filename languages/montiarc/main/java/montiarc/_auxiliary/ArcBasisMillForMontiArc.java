/* (c) https://github.com/MontiCore/monticore */
package montiarc._auxiliary;

import arcbasis._ast.ASTArcComponentTypeBuilder;
import variablearc._ast.ASTVariableArcComponentTypeBuilder;

public class ArcBasisMillForMontiArc extends ArcBasisMillForMontiArcTOP {

  @Override
  protected ASTArcComponentTypeBuilder _arcComponentTypeBuilder() {
    return new ASTVariableArcComponentTypeBuilder();
  }
}
