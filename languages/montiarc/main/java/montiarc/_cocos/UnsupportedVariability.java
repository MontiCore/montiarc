/* (c) https://github.com/MontiCore/monticore */
package montiarc._cocos;

import com.google.common.base.Preconditions;
import de.se_rwth.commons.logging.Log;
import montiarc.util.ArcError;
import org.codehaus.commons.nullanalysis.NotNull;
import variablearc._ast.ASTArcVarIf;
import variablearc._cocos.VariableArcASTArcVarIfCoCo;

public class UnsupportedVariability implements VariableArcASTArcVarIfCoCo {
  @Override
  public void check(@NotNull ASTArcVarIf ast) {
    Preconditions.checkNotNull(ast);
    Log.error(
      ArcError.UNSUPPORTED_MODEL_ELEMENT.format("variability"),
      ast.get_SourcePositionStart(),
      ast.get_SourcePositionEnd()
    );
  }
}
