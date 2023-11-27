/* (c) https://github.com/MontiCore/monticore */
package montiarc.trafo;

import arcautomaton.ReplaceAbsentTriggersByTicks;
import com.google.common.base.Preconditions;
import montiarc.MontiArcMill;
import montiarc._ast.ASTMACompilationUnit;
import montiarc._visitor.MontiArcTraverser;
import org.codehaus.commons.nullanalysis.NotNull;

import java.util.function.UnaryOperator;

public class MAReplaceAbsentTriggersByTicks implements UnaryOperator<ASTMACompilationUnit> {

  private final MontiArcTraverser traverser;

  public MAReplaceAbsentTriggersByTicks() {
    this.traverser = MontiArcMill.inheritanceTraverser();

    ReplaceAbsentTriggersByTicks trafo = new ReplaceAbsentTriggersByTicks();
    this.traverser.add4SCTransitions4Code(trafo);
    this.traverser.setArcAutomatonHandler(trafo);
  }
  @Override
  public ASTMACompilationUnit apply(@NotNull ASTMACompilationUnit cUnit) {
    Preconditions.checkNotNull(cUnit);

    traverser.handle(cUnit);
    return cUnit;
  }
}
