/* (c) https://github.com/MontiCore/monticore */
package montiarc.trafo;

import com.google.common.base.Preconditions;
import comfortablearc.trafo.AutoConnectTrafo;
import de.monticore.types3.ISymTypeRelations;
import montiarc.MontiArcMill;
import montiarc._ast.ASTMACompilationUnit;
import montiarc._visitor.MontiArcTraverser;
import org.codehaus.commons.nullanalysis.NotNull;

import java.util.function.UnaryOperator;

/**
 * Uses {@link AutoConnectTrafo} as transformation on all {@link arcbasis._ast.ASTComponentType} in an
 * {@link ASTMACompilationUnit}.
 */
public class MAAutoConnectTrafo extends AutoConnectTrafo implements UnaryOperator<ASTMACompilationUnit> {

  protected final MontiArcTraverser traverser;

  public MAAutoConnectTrafo(@NotNull ISymTypeRelations tr) {
    super(Preconditions.checkNotNull(tr));

    this.traverser = MontiArcMill.inheritanceTraverser();
    this.traverser.add4ArcBasis(this);
    this.traverser.add4ComfortableArc(this);
  }

  @Override
  public ASTMACompilationUnit apply(@NotNull ASTMACompilationUnit cUnit) {
    Preconditions.checkNotNull(cUnit);

    cUnit.accept(this.traverser);
    return cUnit;
  }
}
