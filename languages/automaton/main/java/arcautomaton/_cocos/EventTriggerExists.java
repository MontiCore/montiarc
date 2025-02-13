/* (c) https://github.com/MontiCore/monticore */
package arcautomaton._cocos;

import arcautomaton._ast.ASTMsgEvent;
import com.google.common.base.Preconditions;
import de.monticore.scevents._symboltable.SCEventDefSymbol;
import de.monticore.symbols.basicsymbols._symboltable.VariableSymbol;
import de.monticore.symboltable.modifiers.AccessModifier;
import de.se_rwth.commons.logging.Log;
import montiarc.util.ArcAutomataError;
import org.codehaus.commons.nullanalysis.NotNull;

import java.util.Optional;
import java.util.function.Predicate;

/**
 * Checks that the transition event triggers exist
 */
public class EventTriggerExists implements ArcAutomatonASTMsgEventCoCo {

  @Override
  public void check(@NotNull ASTMsgEvent node) {
    Preconditions.checkNotNull(node);
    Preconditions.checkNotNull(node.getEnclosingScope());

    Optional<SCEventDefSymbol> optEventSym = node.getEnclosingScope()
      .resolveSCEventDefMany(node.getName(), getSymbolPredicate()).stream().findFirst();

    if(optEventSym.isEmpty()) {
      Log.error(ArcAutomataError.MSG_EVENT_WITHOUT_SYMBOL.format(), node.get_SourcePositionStart(), node.get_SourcePositionEnd());
    }
  }

  protected Predicate<SCEventDefSymbol> getSymbolPredicate() {
    return v -> true;
  }
}
