/* (c) https://github.com/MontiCore/monticore */
package montiarc.trafo;

import com.google.common.base.Preconditions;
import montiarc._ast.ASTMACompilationUnit;
import org.codehaus.commons.nullanalysis.NotNull;

import java.util.function.UnaryOperator;

public class MontiArcTrafos {

  private final UnaryOperator<ASTMACompilationUnit> trafos;

  public MontiArcTrafos(@NotNull UnaryOperator<ASTMACompilationUnit> trafos) {
    this.trafos = Preconditions.checkNotNull(trafos);
  }

  public void applyAll(@NotNull ASTMACompilationUnit ast) {
    Preconditions.checkNotNull(ast);
    trafos.apply(ast);
  }

  public static MontiArcTrafos afterParsing() {
    return new MontiArcTrafos(ast ->
      new MAEnforceBlocksInVarIfTrafo()
        .andThen(new MASeparateCompInstantiationFromTypeDeclTrafo())
        .andThen(new MAConnectedToNormalCompInstanceTrafo())
        .andThen(new MAReplaceAbsentTriggersByTicks())
        .apply(ast)
    );
  }

  public static MontiArcTrafos afterSymTab() {
    return new MontiArcTrafos(
      new MAAutoConnectTrafo()
    );
  }
}
