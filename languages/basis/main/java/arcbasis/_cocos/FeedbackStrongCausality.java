/* (c) https://github.com/MontiCore/monticore */
package arcbasis._cocos;

import arcbasis._ast.ASTComponentType;
import arcbasis._ast.ASTConnector;
import arcbasis._ast.ASTPortAccess;
import arcbasis._symboltable.ComponentInstanceSymbol;
import com.google.common.base.Preconditions;
import de.se_rwth.commons.logging.Log;
import montiarc.util.ArcError;
import org.codehaus.commons.nullanalysis.NotNull;

import java.util.Stack;

/**
 * Checks that for every cycle there is at least one component that is strongly causal modulo a port on this cycle.
 */
public class FeedbackStrongCausality implements ArcBasisASTComponentTypeCoCo {

  @Override
  public void check(@NotNull ASTComponentType node) {
    Preconditions.checkNotNull(node);
    Preconditions.checkArgument(node.isPresentSymbol());

    for (ComponentInstanceSymbol root : node.getSpannedScope().getLocalComponentInstanceSymbols()) {
      this.check(node, root, new Stack<>());
    }
  }

  protected void check(@NotNull ASTComponentType graph,
                       @NotNull ComponentInstanceSymbol next,
                       @NotNull Stack<ComponentInstanceSymbol> path) {
    Preconditions.checkNotNull(graph);
    Preconditions.checkNotNull(next);
    Preconditions.checkNotNull(path);

    path.push(next);
    for (ASTConnector connector : graph.getConnectorsMatchingSource(next)) {
      this.check(graph, connector, path);
    }
    path.pop();
  }

  protected void check(@NotNull ASTComponentType graph,
                       @NotNull ASTConnector next,
                       @NotNull Stack<ComponentInstanceSymbol> path) {
    Preconditions.checkNotNull(graph);
    Preconditions.checkNotNull(next);
    Preconditions.checkNotNull(path);

    if (next.getSource().isPresentPortSymbol() && !next.getSource().getPortSymbol().isStronglyCausal()) {
      for (ASTPortAccess target : next.getTargetList()) {
        this.check(graph, target, path);
      }
    }
  }

  protected void check(@NotNull ASTComponentType graph,
                       @NotNull ASTPortAccess next,
                       @NotNull Stack<ComponentInstanceSymbol> path) {
    Preconditions.checkNotNull(graph);
    Preconditions.checkNotNull(next);
    Preconditions.checkNotNull(path);

    if (!next.isPresentComponent() || !next.isPresentComponentSymbol()) {
      return;
    }

    if (path.contains(next.getComponentSymbol())) {
      Log.error(ArcError.FEEDBACK_CAUSALITY.toString(),
        next.get_SourcePositionStart(), next.get_SourcePositionEnd()
      );
    } else {
      this.check(graph, next.getComponentSymbol(), path);
    }
  }
}
