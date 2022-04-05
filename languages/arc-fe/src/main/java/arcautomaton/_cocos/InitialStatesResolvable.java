/* (c) https://github.com/MontiCore/monticore */
package arcautomaton._cocos;

import arcautomaton._ast.ASTInitialOutputDeclaration;
import arcbasis.util.ArcError;
import com.google.common.base.Preconditions;
import de.se_rwth.commons.logging.Log;
import org.codehaus.commons.nullanalysis.NotNull;

/**
 * Checks whether states marked as initial (by {@link arcautomaton._ast.ASTInitialOutputDeclaration}s) refer to an
 * existing state of that automaton.
 */
public class InitialStatesResolvable implements ArcAutomatonASTInitialOutputDeclarationCoCo {

  @Override
  public void check(@NotNull ASTInitialOutputDeclaration node) {
    Preconditions.checkNotNull(node);
    if(!node.isPresentNameDefinition()){
      Log.error(ArcError.INITIAL_STATE_REFERENCE_MISSING.format(node.getName()),
        node.get_SourcePositionStart(), node.get_SourcePositionEnd()
      );
    }
  }
}
