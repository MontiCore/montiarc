/* (c) https://github.com/MontiCore/monticore */
package arcbasis._cocos;

import arcbasis._ast.ASTConnector;
import arcbasis._ast.ASTPortAccess;
import arcbasis._symboltable.ArcPortSymbol;
import com.google.common.base.Preconditions;
import de.monticore.symbols.compsymbols._symboltable.SubcomponentSymbol;
import de.se_rwth.commons.logging.Log;
import montiarc.util.ArcError;
import org.codehaus.commons.nullanalysis.NotNull;

import java.util.Optional;

/**
 * Checks whether source and target of connected ports exist.
 * <p>
 * Implements [Hab16] CO3: Unqualified sources or targets in connectors either
 * refer to a port or a subcomponent in the same namespace. (p.61 Lst. 3.35).
 * However, we currently do not support unqualified sources and targets that represent subcomponents.
 * Implements [Hab16] R5: The first part of a qualified connector’s source
 * respectively target must correspond to a subcomponent declared in the current
 * component definition. (p.64 Lst. 3.40)
 * Implements [Hab16] R6: The second part of a qualified connector’s source
 * respectively target must correspond to a port name of the referenced
 * subcomponent determined by the first part. (p.64, Lst. 3.41)
 * Implements [Hab16] R7: The source port of a simple connector must exist in
 * the subcomponents type. (p.65 Lst. 3.42)
 */
public class ConnectorPortsExist implements ArcBasisASTConnectorCoCo {

  @Override
  public void check(@NotNull ASTConnector node) {
    Preconditions.checkNotNull(node);

    checkPortPresence(node.getSource(), node);
    node.forEachTarget(tgt -> checkPortPresence(tgt, node));
  }

  /**
   * Checks that the port access is backed by resolvable symbols. Else logs errors. If the port access is qualified, it
   * is checked that the subcomponent that the port access refers to exists and that the type of that subcomponent has
   * a port with a name equal to the one given by the port access. If we have a port without qualification we check
   * that the component type enclosing the port access has a corresponding port symbol (with equal name).
   *
   * @param port      The port access for which to test whether there is a port symbol which the port access refers to.
   * @param connector The connector of which the port access is part of.
   */
  protected static void checkPortPresence(@NotNull ASTPortAccess port,
                                          @NotNull ASTConnector connector) {
    Preconditions.checkNotNull(port);
    Preconditions.checkNotNull(connector);
    Preconditions.checkArgument(connector.getSource().equals(port)
      || connector.getTargetList().stream().anyMatch(tgt -> tgt.equals(port))
    );

    if (port.isPresentComponent()) {
      // first check the existence of the subcomponent that owns the port
      Optional<SubcomponentSymbol> sub = Optional.ofNullable(port.getComponentSymbol());
      if (sub.isEmpty()) {
        Log.error(ArcError.MISSING_SUBCOMPONENT.format(port.getComponent()),
          port.get_SourcePositionStart(), port.get_SourcePositionEnd());
      } else {
        // now also check whether the subcomponent has a port with the given name
        if (!sub.get().isTypePresent()) { // ignore missing type as this is handled by other cocos
          return;
        }
        Optional<ArcPortSymbol> portSym = Optional.ofNullable(port.getPortSymbol());
        if (portSym.isEmpty()) {
          Log.error(ArcError.MISSING_PORT.format(port.getQName()),
            port.get_SourcePositionStart(), port.get_SourcePositionEnd()
          );
        }
      }

      // v checking the port existence for ports of the enclosing component type.
    } else {
      Optional<ArcPortSymbol> portSym = Optional.ofNullable(port.getPortSymbol());
      if (portSym.isEmpty()) {
        Log.error(ArcError.MISSING_PORT.format(port.getQName()),
          port.get_SourcePositionStart(), port.get_SourcePositionEnd()
        );
      }
    }
  }
}
