/* (c) https://github.com/MontiCore/monticore */
package arcbasis._cocos;

import arcbasis._ast.ASTPort;
import com.google.common.base.Preconditions;
import de.se_rwth.commons.logging.Log;
import montiarc.util.ArcError;
import org.codehaus.commons.nullanalysis.NotNull;

import java.util.Collection;

public class PortNameIsNoReservedKeyword implements ArcBasisASTPortCoCo {

  protected final Collection<String> reservedKeywords;
  protected final String languageName;

  public PortNameIsNoReservedKeyword(@NotNull String languageName, @NotNull Collection<String> reservedKeywords) {
    this.reservedKeywords = Preconditions.checkNotNull(reservedKeywords);
    this.languageName = Preconditions.checkNotNull(languageName);
  }

  @Override
  public void check(@NotNull ASTPort node) {
    Preconditions.checkNotNull(node);
    String portName = node.getName();

    if(reservedKeywords.stream().anyMatch(keyword -> keyword.equals(portName))) {
      Log.error(
        ArcError.RESERVED_KEYWORD_USED.format(portName, languageName),
        node.get_SourcePositionStart(), node.get_SourcePositionEnd()
      );
    }
  }
}
