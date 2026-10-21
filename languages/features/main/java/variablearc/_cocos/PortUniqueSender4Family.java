/* (c) https://github.com/MontiCore/monticore */
package variablearc._cocos;

import arcbasis._ast.ASTArcComponentType;
import arcbasis._ast.ASTConnector;
import arcbasis._ast.ASTPortAccess;
import arcbasis._cocos.ArcBasisASTArcComponentTypeCoCo;
import arcbasis.context.WithinComponentResolver;
import com.google.common.base.Preconditions;
import com.microsoft.z3.BoolExpr;
import de.monticore.symbols.compsymbols._symboltable.ComponentTypeSymbol;
import de.se_rwth.commons.logging.Log;
import montiarc.util.ArcError;
import org.codehaus.commons.nullanalysis.NotNull;
import variablearc._ast.VariableArcElements;
import variablearc.presence.PresenceResolver;
import variablearc.smt.FormulaSolver;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Checks that no port is the target of multiple connectors in any
 * configuration. A target is reported if it may be present together with a
 * preceding connector to the same port, including connectors in variation
 * points.
 */
public class PortUniqueSender4Family implements ArcBasisASTArcComponentTypeCoCo {

  protected final WithinComponentResolver withinComponentResolver;
  protected final PresenceResolver presenceResolver;

  public PortUniqueSender4Family(@NotNull WithinComponentResolver withinComponentResolver,
                                 @NotNull PresenceResolver presenceResolver) {
    this.withinComponentResolver = Preconditions.checkNotNull(withinComponentResolver);
    this.presenceResolver = Preconditions.checkNotNull(presenceResolver);
  }

  @Override
  public void check(@NotNull ASTArcComponentType node) {
    Preconditions.checkNotNull(node);
    if (!node.isPresentSymbol()) return;

    ComponentTypeSymbol component = node.getSymbol();
    List<ASTConnector> connectors = VariableArcElements.of(node, ASTConnector.class);
    Map<String, List<BoolExpr>> preceding = new HashMap<>();

    FormulaSolver solver = this.presenceResolver.solverFor(component);
    for (ASTConnector connector : connectors) {
      BoolExpr connectorPresence = this.presenceResolver.presenceOf(component, connector);
      for (ASTPortAccess target : connector.getTargetList()) {
        List<BoolExpr> senders = preceding.computeIfAbsent(target.getQName(), name -> new ArrayList<>());
        if (!senders.isEmpty()) {
          this.report(solver, solver.and(connectorPresence, solver.or(senders)), target);
        }
        senders.add(connectorPresence);
      }
    }
  }

  protected void report(@NotNull FormulaSolver solver, @NotNull BoolExpr violation, @NotNull ASTPortAccess target) {
    String message = ArcError.PORT_MULTIPLE_SENDER.format(target.getQName());
    switch (solver.check(violation)) {
      case SATISFIABLE -> Log.error(message, target.get_SourcePositionStart(), target.get_SourcePositionEnd());
      case UNKNOWN -> Log.warn(message, target.get_SourcePositionStart(), target.get_SourcePositionEnd());
      case UNSATISFIABLE -> { }
    }
  }
}
