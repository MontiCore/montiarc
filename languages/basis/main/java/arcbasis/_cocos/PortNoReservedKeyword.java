/* (c) https://github.com/MontiCore/monticore */
package arcbasis._cocos;

import arcbasis._ast.ASTPort;
import com.google.common.base.Preconditions;
import de.se_rwth.commons.logging.Log;
import montiarc.util.ArcError;
import org.codehaus.commons.nullanalysis.NotNull;

import java.util.Collection;

public class PortNoReservedKeyword implements ArcBasisASTPortCoCo {

  protected final Collection<String> keywords;
  protected final String language;

  public PortNoReservedKeyword(@NotNull String language, @NotNull Collection<String> keywords) {
    this.keywords = Preconditions.checkNotNull(keywords);
    this.language = Preconditions.checkNotNull(language);
  }

  @Override
  public void check(@NotNull ASTPort node) {
    Preconditions.checkNotNull(node);
    String portName = node.getName();

    if (keywords.stream().anyMatch(keyword -> keyword.equals(portName))) {
      Log.error(ArcError.RESTRICTED_IDENTIFIER.format(portName),
        node.get_SourcePositionStart(), node.get_SourcePositionEnd()
      );
    }
  }
}
