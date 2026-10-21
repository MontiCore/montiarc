/* (c) https://github.com/MontiCore/monticore */
package montiarc._cocos;

import com.google.common.base.Preconditions;
import de.se_rwth.commons.logging.Log;
import montiarc.util.ArcError;
import org.codehaus.commons.nullanalysis.NotNull;
import variablearc._ast.ASTArcConstraintDeclaration;
import variablearc._ast.ASTArcFeatureDeclaration;
import variablearc._ast.ASTArcVarIf;
import variablearc._cocos.VariableArcASTArcConstraintDeclarationCoCo;
import variablearc._cocos.VariableArcASTArcFeatureDeclarationCoCo;
import variablearc._cocos.VariableArcASTArcVarIfCoCo;

/**
 * Rejects features, variation points, and constraints if the analysis of
 * variable components is disabled, as they cannot be checked without it.
 * Registered as a varif CoCo, it is a feature and constraint declaration CoCo,
 * too, as all are visited by the same VariableArc visitor.
 */
public class UnsupportedVariability implements VariableArcASTArcVarIfCoCo, VariableArcASTArcFeatureDeclarationCoCo,
  VariableArcASTArcConstraintDeclarationCoCo {

  @Override
  public void check(@NotNull ASTArcVarIf ast) {
    Preconditions.checkNotNull(ast);
    Log.error(
      ArcError.VARIABILITY_NOT_ANALYZED.format("varif"),
      ast.get_SourcePositionStart(),
      ast.getThen().get_SourcePositionEnd()
    );
  }

  @Override
  public void check(@NotNull ASTArcFeatureDeclaration ast) {
    Preconditions.checkNotNull(ast);
    Log.error(
      ArcError.VARIABILITY_NOT_ANALYZED.format("feature"),
      ast.get_SourcePositionStart(),
      ast.get_SourcePositionEnd()
    );
  }

  @Override
  public void check(@NotNull ASTArcConstraintDeclaration ast) {
    Preconditions.checkNotNull(ast);
    Log.error(
      ArcError.VARIABILITY_NOT_ANALYZED.format("constraint"),
      ast.get_SourcePositionStart(),
      ast.get_SourcePositionEnd()
    );
  }
}
