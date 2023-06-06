/* (c) https://github.com/MontiCore/monticore */
package arcautomaton._symboltable;

import arcautomaton._ast.ASTMsgEvent;
import arcautomaton._visitor.ArcAutomatonVisitor2;
import com.google.common.base.Preconditions;
import de.monticore.scevents._symboltable.SCEventDefSymbol;
import de.se_rwth.commons.logging.Log;
import montiarc.util.ArcAutomataError;
import org.codehaus.commons.nullanalysis.NotNull;

import java.util.Optional;

public class ArcAutomatonScopesGenitorP2 implements ArcAutomatonVisitor2 {
  
  @Override
  public void visit(@NotNull ASTMsgEvent node) {
    Preconditions.checkNotNull(node);
    Preconditions.checkNotNull(node.getEnclosingScope());
    
    Optional<SCEventDefSymbol> optEventSym = node.getEnclosingScope()
        .resolveSCEventDefMany(node.getName()).stream().findFirst();
    
    if(optEventSym.isPresent()) {
      node.setEventSymbol(optEventSym.get());
    } else {
      Log.error(ArcAutomataError.MSG_EVENT_WITHOUT_SYMBOL.format(), node.get_SourcePositionStart());
    }
  }
}