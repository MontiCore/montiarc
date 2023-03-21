/* (c) https://github.com/MontiCore/monticore */
package arcbasis._cocos;

import arcbasis._ast.ASTComponentType;
import com.google.common.base.Preconditions;
import de.se_rwth.commons.logging.Log;
import montiarc.util.ArcError;
import org.codehaus.commons.nullanalysis.NotNull;

import java.util.Collection;

public class ComponentNoReservedKeyword implements ArcBasisASTComponentTypeCoCo {

  protected final Collection<String> reservedKeywords;
  protected final String languageName;

  public ComponentNoReservedKeyword(@NotNull String languageName, @NotNull Collection<String> reservedKeywords) {
    this.reservedKeywords = Preconditions.checkNotNull(reservedKeywords);
    this.languageName = Preconditions.checkNotNull(languageName);
  }

  @Override
  public void check(ASTComponentType node) {
    Preconditions.checkNotNull(node);
    String compName = node.getName();

    if(reservedKeywords.stream().anyMatch(keyword -> keyword.equals(compName))) {
      Log.error(
        ArcError.RESTRICTED_IDENTIFIER.format(compName),
        node.get_SourcePositionStart(), node.get_SourcePositionEnd()
      );
    }
  }
}
