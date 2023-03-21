/* (c) https://github.com/MontiCore/monticore */
package arcbasis._cocos;

import arcbasis._ast.ASTComponentInstance;
import com.google.common.base.Preconditions;
import de.se_rwth.commons.logging.Log;
import montiarc.util.ArcError;
import org.codehaus.commons.nullanalysis.NotNull;

import java.util.Collection;

public class SubcomponentNoReservedKeyword implements ArcBasisASTComponentInstanceCoCo {

  protected final Collection<String> reservedKeywords;
  protected final String languageName;

  public SubcomponentNoReservedKeyword(@NotNull String languageName, @NotNull Collection<String> reservedKeywords) {
    this.reservedKeywords = Preconditions.checkNotNull(reservedKeywords);
    this.languageName = Preconditions.checkNotNull(languageName);
  }

  @Override
  public void check(ASTComponentInstance node) {
    Preconditions.checkNotNull(node);
    String instName = node.getName();

    if(reservedKeywords.stream().anyMatch(keyword -> keyword.equals(instName))) {
      Log.error(
        ArcError.RESTRICTED_IDENTIFIER.format(instName),
        node.get_SourcePositionStart(), node.get_SourcePositionEnd()
      );
    }
  }
}
