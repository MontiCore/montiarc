/* (c) https://github.com/MontiCore/monticore */
package arcprepost._cocos;

import arcprepost._ast.ASTArcPrePost;
import com.google.common.base.Preconditions;
import de.monticore.symbols.compsymbols._symboltable.PortSymbol;
import de.se_rwth.commons.logging.Log;
import montiarc.util.PrePostError;
import org.codehaus.commons.nullanalysis.NotNull;

import java.util.List;

public class TriggerPortExists implements ArcPrePostASTArcPrePostCoCo {

  @Override
  public void check(@NotNull ASTArcPrePost node) {
    Preconditions.checkNotNull(node);
    Preconditions.checkNotNull(node.getEnclosingScope());

    if (node.isPresentTrigger()) {
      String trigger = node.getTrigger();
      List<PortSymbol> ports = node.getEnclosingScope().resolvePortMany(trigger);

      if (ports.size() >= 2) {
        Log.debug(() ->
            "Trigger Port existence check skipped. Multiple ports with the relevant name have been found. An error should have already been logged.",
          node.get_SourcePositionStart(),
          node.get_SourcePositionEnd(),
          this.getClass().getSimpleName()
        );
        return;
      }

      if (ports.isEmpty()) {
        Log.error(PrePostError.TRIGGER_MISSING_PORT.format(trigger),
          node.get_SourcePositionStart(),
          node.get_SourcePositionEnd()
        );
      } else if (!ports.getFirst().isIncoming()) {
        Log.error(PrePostError.TRIGGER_NOT_AN_IN_PORT.format(trigger),
          node.get_SourcePositionStart(),
          node.get_SourcePositionEnd()
        );
      }
    }
  }
}
