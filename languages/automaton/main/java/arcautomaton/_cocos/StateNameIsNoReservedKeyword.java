/* (c) https://github.com/MontiCore/monticore */
package arcautomaton._cocos;

import com.google.common.base.Preconditions;
import de.monticore.scbasis._ast.ASTSCState;
import de.monticore.scbasis._cocos.SCBasisASTSCStateCoCo;
import de.se_rwth.commons.logging.Log;
import montiarc.util.ArcError;
import org.codehaus.commons.nullanalysis.NotNull;

import java.util.Collection;

public class StateNameIsNoReservedKeyword implements SCBasisASTSCStateCoCo {

  protected final Collection<String> reservedKeywords;
  protected final String languageName;

  public StateNameIsNoReservedKeyword(@NotNull String languageName, @NotNull Collection<String> reservedKeywords) {
    this.reservedKeywords = Preconditions.checkNotNull(reservedKeywords);
    this.languageName = Preconditions.checkNotNull(languageName);
  }

  @Override
  public void check(ASTSCState node) {
    Preconditions.checkNotNull(node);
    String stateName = node.getName();

    if(reservedKeywords.stream().anyMatch(keyword -> keyword.equals(stateName))) {
      Log.error(
        ArcError.RESTRICTED_IDENTIFIER.format(stateName),
        node.get_SourcePositionStart(), node.get_SourcePositionEnd()
      );
    }
  }
}
