/* (c) https://github.com/MontiCore/monticore */
package variablearc._auxiliary;

import arcbasis._ast.ASTArcComponentTypeBuilder;
import variablearc._ast.ASTVariableArcComponentTypeBuilder;

public class ArcBasisMillForVariableArc extends ArcBasisMillForVariableArcTOP {

  @Override
  protected ASTArcComponentTypeBuilder _arcComponentTypeBuilder() {
    return new ASTVariableArcComponentTypeBuilder();
  }
}
