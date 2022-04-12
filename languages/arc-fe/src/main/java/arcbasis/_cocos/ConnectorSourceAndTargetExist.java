/* (c) https://github.com/MontiCore/monticore */
package arcbasis._cocos;

import arcbasis._ast.ASTComponentType;
import arcbasis._ast.ASTConnector;
import arcbasis._ast.ASTPortAccess;
import arcbasis._symboltable.ComponentInstanceSymbol;
import arcbasis._symboltable.ComponentTypeSymbol;
import arcbasis._symboltable.PortSymbol;
import arcbasis._visitor.ArcBasisFullPrettyPrinter;
import arcbasis.util.ArcError;
import com.google.common.base.Preconditions;
import de.se_rwth.commons.logging.Log;
import org.codehaus.commons.nullanalysis.NotNull;

import java.util.Optional;

/**
 * Checks whether source and target of connected ports exist.
 *
 * @implements [Hab16] CO3: Unqualified sources or targets in connectors either
 * refer to a port or a subcomponent in the same namespace. (p.61 Lst. 3.35).
 * However, we currently do not support unqualified sources and targets that represent subcomponents.
 * @implements [Hab16] R5: The first part of a qualified connector’s source
 * respectively target must correspond to a subcomponent declared in the current
 * component definition. (p.64 Lst. 3.40)
 * @implements [Hab16] R6: The second part of a qualified connector’s source
 * respectively target must correspond to a port name of the referenced
 * subcomponent determined by the first part. (p.64, Lst. 3.41)
 * @implements [Hab16] R7: The source port of a simple connector must exist in
 * the subcomponents type. (p.65 Lst. 3.42)
 */
public class ConnectorSourceAndTargetExist implements ArcBasisASTComponentTypeCoCo {
  @Override
  public void check(@NotNull ASTComponentType node) {
    Preconditions.checkNotNull(node);
    Preconditions.checkArgument(node.isPresentSymbol(), "ASTComponent node '%s' at '%s' has no symbol. Thus can not " +
      "check CoCo '%s'. Did you forget to run the scopes genitor and symbol table completer before checking the coco?",
      node.getName(), node.get_SourcePositionStart(), this.getClass().getSimpleName());
    ComponentTypeSymbol component = node.getSymbol();

    for(ASTConnector conn : node.getConnectors()) {
      checkPortPresence(conn.getSource(), component, conn);
      conn.forEachTarget(tgt -> checkPortPresence(tgt, component, conn));

    }
  }

  /**
   * Checks that the port access is backed by resolvable symbols. Else logs errors. If the port access is qualified, it
   * is checked that the subcomponent that the port access refers to exists and that the type of that subcomponent has
   * a port with a name equal to the one given by the port access. If we have a port without qualification we check
   * that the component type enclosing the port access has a corresponding port symbol (with equal name).
   * @param port The port access for which to test whether there is a port symbol which the port access refers to.
   * @param enclComp The component type symbol in whose enclosing scope the port is defined (the ast node of the
   *                 component type symbol is an ancestor of the port access in the AST).
   * @param connector The connector of which the port access is part of.
   */
  protected static void checkPortPresence(@NotNull ASTPortAccess port,
                                          @NotNull ComponentTypeSymbol enclComp,
                                          @NotNull ASTConnector connector) {
    Preconditions.checkNotNull(port);
    Preconditions.checkNotNull(enclComp);
    Preconditions.checkNotNull(connector);
    Preconditions.checkArgument(connector.getSource().equals(port)
      || connector.getTargetList().stream().anyMatch( tgt -> tgt.equals(port))
    );

    if(port.isPresentComponent()) {
      // first check the existence of the subcomponent that owns the port
      Optional<ComponentInstanceSymbol> sub = enclComp.getSubComponent(port.getComponent());
      if(!sub.isPresent()) {
        logMissingComponent(port, connector);
      } else {
        // now also check whether the subcomponent has a port with the given name
        Preconditions.checkState(sub.get().isPresentType(), "CoCo '%s' can only be run after symbol table " +
            "completion, but we detected that the component type for a component instance has not been set yet.",
          ConnectorSourceAndTargetExist.class.getSimpleName());

        Optional<PortSymbol> portSym = sub.get().getType().getTypeInfo().getPort(port.getPort(), true);
        if(!portSym.isPresent()) {
          logMissingPort(port, connector, enclComp);
        }
      }

    // v checking the port existence for ports of the enclosing component type.
    } else {
      Optional<PortSymbol> portSym = enclComp.getPort(port.getPort(), true);
      if(!portSym.isPresent()) {
        logMissingPort(port, connector, enclComp);
      }
    }
  }

  /**
   * Prints an error message stating that the component instance that is the owner of the given port does not exist.
   * @param connectorOfPort The connector of which the port is part (we need this for printing the error message).
   *                        If the connector does not contain the given port an exception is raised.
   */
  protected static void logMissingComponent(@NotNull ASTPortAccess port,
                                            @NotNull ASTConnector connectorOfPort) {
    Preconditions.checkNotNull(port);
    Preconditions.checkNotNull(connectorOfPort);
    Preconditions.checkArgument(port.isPresentComponent());
    Preconditions.checkArgument(connectorOfPort.getSource().equals(port)
      || connectorOfPort.getTargetList().stream().anyMatch( tgt -> tgt.equals(port))
    );

    boolean isSourcePort = port.equals(connectorOfPort.getSource());
    Log.error(String.format(isSourcePort ?
          ArcError.SOURCE_PORT_COMPONENT_MISSING.toString()
          : ArcError.TARGET_PORT_COMPONENT_MISSING.toString(),
        port.getComponent(), printConnector(connectorOfPort)),
      port.get_SourcePositionStart()
    );
  }

  /**
   * Prints an error message stating that the given port does not exist.
   * @param connectorOfPort The connector of which the port is part (we need this for printing the error message).
   *                        If the connector does not contain the given port an exception is raised.
   * @param enclComp The component symbol of in whose scope the given port access lays (we need this for printing the
   *                 error message). If the given port access does not lay in the component symbols scope an exception
   *                 is raised.
   */
  protected static void logMissingPort(@NotNull ASTPortAccess port,
                                       @NotNull ASTConnector connectorOfPort,
                                       @NotNull ComponentTypeSymbol enclComp) {
    Preconditions.checkNotNull(port);
    Preconditions.checkNotNull(connectorOfPort);
    Preconditions.checkNotNull(enclComp);
    Preconditions.checkArgument(connectorOfPort.getSource().equals(port)
      || connectorOfPort.getTargetList().stream().anyMatch( tgt -> tgt.equals(port))
    );

    boolean isSourcePort = port.equals(connectorOfPort.getSource());
    Log.error(String.format((isSourcePort ?
          ArcError.SOURCE_PORT_NOT_EXISTS
          : ArcError.TARGET_PORT_NOT_EXISTS).toString(),
        port.getQName(), printConnector(connectorOfPort), enclComp.getFullName()),
      port.get_SourcePositionStart()
    );
  }

  /**
   * @return a nice string that can be used to enhance error messages
   */
  private static String printConnector(ASTConnector connector){
    return new ArcBasisFullPrettyPrinter().prettyprint(connector)
      .replaceAll("[;\n]", "");
  }
}
