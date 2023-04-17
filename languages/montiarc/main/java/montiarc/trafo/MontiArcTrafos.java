/* (c) https://github.com/MontiCore/monticore */
package montiarc.trafo;

import com.google.common.base.Preconditions;
import comfortablearc.trafo.AutoConnectTrafo;
import comfortablearc.trafo.IAutoConnectTrafo;
import de.monticore.types.check.TypeRelations;
import montiarc.MontiArcMill;
import montiarc._ast.ASTMACompilationUnit;
import montiarc._visitor.MontiArcTraverser;
import org.codehaus.commons.nullanalysis.NotNull;

public class MontiArcTrafos {

  MontiArcTraverser traverser;

  MontiArcTrafos() {
    this.traverser = MontiArcMill.traverser();
  }

  public static MontiArcTrafos afterSymTab() {
    MontiArcTrafos trafos = new MontiArcTrafos();
    trafos.addTrafo(new AutoConnectTrafo(new TypeRelations()));

    return trafos;
  }

  public void addTrafo(@NotNull IAutoConnectTrafo trafo) {
    Preconditions.checkNotNull(trafo);
    this.traverser.add4ArcBasis(trafo);
    this.traverser.add4ComfortableArc(trafo);
  }

  public void applyAll(@NotNull ASTMACompilationUnit ast) {
    Preconditions.checkNotNull(ast);
    ast.accept(this.traverser);
  }
}
