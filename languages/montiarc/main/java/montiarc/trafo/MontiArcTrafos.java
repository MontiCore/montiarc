/* (c) https://github.com/MontiCore/monticore */
package montiarc.trafo;

import com.google.common.base.Preconditions;
import de.monticore.types.check.TypeRelations;
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
      new MASeparateCompInstantiationFromTypeDeclTrafo()
        .andThen(new MAConnectedToNormalCompInstanceTrafo())
        .apply(ast)
    );
  }

  public static MontiArcTrafos afterSymTab() {
    return new MontiArcTrafos(
      new MAAutoConnectTrafo(new TypeRelations())
    );
  }
}
