/* (c) https://github.com/MontiCore/monticore */
package arcbasis._cocos;

import arcbasis._ast.ASTArcField;
import com.google.common.base.Preconditions;
import de.se_rwth.commons.logging.Log;
import montiarc.util.ArcError;
import org.codehaus.commons.nullanalysis.NotNull;

import java.util.Collection;

public class FieldNameIsNoReservedKeyword implements ArcBasisASTArcFieldCoCo {

  protected final Collection<String> reservedKeywords;
  protected final String languageName;

  public FieldNameIsNoReservedKeyword(@NotNull String languageName, @NotNull Collection<String> reservedKeywords) {
    this.reservedKeywords = Preconditions.checkNotNull(reservedKeywords);
    this.languageName = Preconditions.checkNotNull(languageName);
  }

  @Override
  public void check(ASTArcField node) {
    Preconditions.checkNotNull(node);
    String fieldName = node.getName();

    if(reservedKeywords.stream().anyMatch(keyword -> keyword.equals(fieldName))) {
      Log.error(
        ArcError.RESTRICTED_IDENTIFIER.format(fieldName),
        node.get_SourcePositionStart(), node.get_SourcePositionEnd()
      );
    }
  }
}
