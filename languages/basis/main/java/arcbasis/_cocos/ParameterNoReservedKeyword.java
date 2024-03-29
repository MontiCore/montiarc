/* (c) https://github.com/MontiCore/monticore */
package arcbasis._cocos;

import arcbasis._ast.ASTArcParameter;
import com.google.common.base.Preconditions;
import de.se_rwth.commons.logging.Log;
import montiarc.util.ArcError;
import org.codehaus.commons.nullanalysis.NotNull;

import java.util.Collection;

public class ParameterNoReservedKeyword implements ArcBasisASTArcParameterCoCo {

  protected final Collection<String> reservedKeywords;
  protected final String languageName;

  public ParameterNoReservedKeyword(@NotNull String languageName, @NotNull Collection<String> reservedKeywords) {
    this.reservedKeywords = Preconditions.checkNotNull(reservedKeywords);
    this.languageName = Preconditions.checkNotNull(languageName);
  }

  @Override
  public void check(ASTArcParameter node) {
    Preconditions.checkNotNull(node);
    String paramName = node.getName();

    if(reservedKeywords.stream().anyMatch(keyword -> keyword.equals(paramName))) {
      Log.error(
        ArcError.RESTRICTED_IDENTIFIER.format(paramName),
        node.get_SourcePositionStart(), node.get_SourcePositionEnd()
      );
    }
  }
}
