/* (c) https://github.com/MontiCore/monticore */
package arcbasis._cocos;

import arcbasis._ast.ASTComponentType;
import arcbasis._ast.ASTConnector;
import arcbasis._ast.ASTPortAccess;
import com.google.common.base.Preconditions;
import de.monticore.symbols.compsymbols._symboltable.SubcomponentSymbol;
import de.se_rwth.commons.logging.Log;
import montiarc.util.ArcError;
import org.codehaus.commons.nullanalysis.NotNull;

import java.util.HashSet;
import java.util.Set;
import java.util.Stack;

/**
 * Checks that for every cycle there is at least one component that is strongly causal modulo a port on this cycle.
 */
public class FeedbackStrongCausality implements ArcBasisASTComponentTypeCoCo {

  @Override
  public void check(@NotNull ASTComponentType node) {
    Preconditions.checkNotNull(node);
    Preconditions.checkArgument(node.isPresentSymbol());

    Set<SubcomponentSymbol> visited = new HashSet<>();

    for (SubcomponentSymbol vertex : node.getSymbol().getSubcomponents()) {
      if (!visited.contains(vertex)) {
        this.check(node, vertex, new Stack<>(), visited);
      }
    }
  }

  protected void check(@NotNull ASTComponentType graph,
                       @NotNull SubcomponentSymbol next,
                       @NotNull Stack<SubcomponentSymbol> path,
                       @NotNull Set<SubcomponentSymbol> visited) {
    Preconditions.checkNotNull(graph);
    Preconditions.checkNotNull(next);
    Preconditions.checkNotNull(path);
    Preconditions.checkNotNull(visited);

    path.push(next);
    for (ASTConnector connector : graph.getConnectorsMatchingSource(next)) {
      this.check(graph, connector, path, visited);
    }
    visited.add(path.pop());
  }

  protected void check(@NotNull ASTComponentType graph,
                       @NotNull ASTConnector next,
                       @NotNull Stack<SubcomponentSymbol> path,
                       @NotNull Set<SubcomponentSymbol> visited) {
    Preconditions.checkNotNull(graph);
    Preconditions.checkNotNull(next);
    Preconditions.checkNotNull(path);
    Preconditions.checkNotNull(visited);

    if (next.getSource().isPresentPortSymbol() && !next.getSource().getPortSymbol().getStronglyCausal()) {
      for (ASTPortAccess target : next.getTargetList()) {
        this.check(graph, target, path, visited);
      }
    }
  }

  protected void check(@NotNull ASTComponentType graph,
                       @NotNull ASTPortAccess next,
                       @NotNull Stack<SubcomponentSymbol> path,
                       @NotNull Set<SubcomponentSymbol> visited) {
    Preconditions.checkNotNull(graph);
    Preconditions.checkNotNull(next);
    Preconditions.checkNotNull(path);
    Preconditions.checkNotNull(visited);

    if (!next.isPresentComponent() || !next.isPresentComponentSymbol()) {
      return;
    }

    if (path.contains(next.getComponentSymbol())) {
      Log.error(ArcError.FEEDBACK_CAUSALITY.toString(),
        next.get_SourcePositionStart(), next.get_SourcePositionEnd()
      );
    } else {
      this.check(graph, next.getComponentSymbol(), path, visited);
    }
  }
}
