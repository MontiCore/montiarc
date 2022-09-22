/* (c) https://github.com/MontiCore/monticore */
package arcbasis._cocos;

import arcbasis._ast.ASTComponentInstance;
import com.google.common.base.Preconditions;
import de.se_rwth.commons.logging.Log;
import montiarc.util.ArcError;
import org.codehaus.commons.nullanalysis.NotNull;

import java.util.Collection;

public class ComponentInstanceNameIsNoReservedKeyword implements ArcBasisASTComponentInstanceCoCo {

  protected final Collection<String> reservedKeywords;
  protected final String languageName;

  public ComponentInstanceNameIsNoReservedKeyword(@NotNull String languageName, @NotNull Collection<String> reservedKeywords) {
    this.reservedKeywords = Preconditions.checkNotNull(reservedKeywords);
    this.languageName = Preconditions.checkNotNull(languageName);
  }

  @Override
  public void check(ASTComponentInstance node) {
    Preconditions.checkNotNull(node);
    String instName = node.getName();

    if(reservedKeywords.stream().anyMatch(keyword -> keyword.equals(instName))) {
      Log.error(
        ArcError.RESERVED_KEYWORD_USED.format(instName, languageName),
        node.get_SourcePositionStart(), node.get_SourcePositionEnd()
      );
    }
  }
}
