/* (c) https://github.com/MontiCore/monticore */
package comfortablearc.trafo;

import arcbasis._ast.ASTComponentType;
import arcbasis._visitor.ArcBasisVisitor2;
import comfortablearc._ast.ASTArcAutoConnect;
import comfortablearc._visitor.ComfortableArcVisitor2;
import org.codehaus.commons.nullanalysis.NotNull;

public interface IAutoConnectTrafo extends ComfortableArcVisitor2, ArcBasisVisitor2 {

  void apply(@NotNull ASTArcAutoConnect node);

  default void visit(@NotNull ASTArcAutoConnect node) {
    apply(node);
  }

  @Override
  void visit(@NotNull ASTComponentType node);

}
