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
 * Checks whether the directions of ports are consistent with their roles in connectors. Source port of connectors can
 * be outgoing ports of subcomponents or incoming ports of enclosing component types. Target port of connectors
 * can be incoming ports of subcomponent instances or outgoing ports of the enclosing component types.
 */
public class ConnectorSourceAndTargetDirectionsFit implements ArcBasisASTComponentTypeCoCo {
  @Override
  public void check(@NotNull ASTComponentType node) {
    Preconditions.checkNotNull(node);
    Preconditions.checkArgument(node.isPresentSymbol(), "ASTComponent node '%s' at '%s' has no symbol. Thus can not " +
        "check CoCo '%s'. Did you forget to run the scopes genitor and symbol table completer before checking the coco?",
      node.getName(), node.get_SourcePositionStart(), this.getClass().getSimpleName());
    ComponentTypeSymbol enclComponent = node.getSymbol();

    for(ASTConnector conn : node.getConnectors()) {
      if(conn.getSource().isPresentPortSymbol()) {
        checkDirectionFitsForSourcePort(conn.getSource(), conn.getSource().getPortSymbol(), conn);
      } else {
        // None of our business. The presence of symbols for ports in connectors is checked by another coco.
        logInfoThatCoCoIsNotChecked(conn.getSource());
      }

      for(ASTPortAccess astTarget : conn.getTargetList()) {
        if(astTarget.isPresentPortSymbol()) {
          checkDirectionFitsTargetPort(astTarget, astTarget.getPortSymbol(), conn);
        } else {
          // None of our business. The presence of symbols for ports in connectors is checked by another coco.
          logInfoThatCoCoIsNotChecked(astTarget);
        }
      }
    }
  }

  /**
   * Check whether the ast port access can be used as a source port in a connector. If this is not the case an error is
   * logged. An ast port access can be a source port in a connector if a) it is the port access on a subcomponent and
   * the port is an outgoing port of that subcomponent, or b) it is the port access on the port of the enclosing
   * component type and the type of the port is incoming.
   * @param astPort The port access to check
   * @param portSym The port symbol corresponding with the port accessed in {@code astPort}
   * @param connectorOfPortAccess The connector in which the given port access serves as a source port (we need this for
   *                              printing the error message).
   */
  protected static void checkDirectionFitsForSourcePort(@NotNull ASTPortAccess astPort,
                                                        @NotNull PortSymbol portSym,
                                                        @NotNull ASTConnector connectorOfPortAccess) {
    Preconditions.checkNotNull(astPort);
    Preconditions.checkNotNull(portSym);
    Preconditions.checkArgument(connectorOfPortAccess.getSource().equals(astPort));

    if((astPort.isPresentComponent() && portSym.isIncoming())
      || (!astPort.isPresentComponent() && portSym.isOutgoing())) {
      Log.error(ArcError.PORT_DIRECTION_MISMATCH.format(
          astPort.getQName(), "source", printConnector(connectorOfPortAccess),
          astPort.isPresentComponent() ? "the port of a subcomponent" : "a forwarding port"
        ),
        astPort.get_SourcePositionStart()
      );
    }
  }

  /**
   * Check whether the ast port access can be used as a target port in a connector. If this is not the case an error is
   * logged. An ast port access can be a target port in a connector if a) it is the port access on a subcomponent and
   * the port is an incoming port of that subcomponent, or b) it is the port access on the port of the enclosing
   * component type and the type of the port is outgoing.
   * @param astPort The port access to check
   * @param portSym The port symbol corresponding with the port accessed in {@code astPort}
   * @param connectorOfPortAccess The connector in which the given port access serves as a target port (we need this for
   *                              printing the error message).
   */
  protected static void checkDirectionFitsTargetPort(@NotNull ASTPortAccess astPort,
                                                     @NotNull PortSymbol portSym,
                                                     @NotNull ASTConnector connectorOfPortAccess) {
    Preconditions.checkNotNull(astPort);
    Preconditions.checkNotNull(portSym);
    Preconditions.checkArgument(connectorOfPortAccess.getTargetList().stream().anyMatch( tgt -> tgt.equals(astPort)));

    if((astPort.isPresentComponent() && portSym.isOutgoing())
      || (!astPort.isPresentComponent() && portSym.isIncoming())) {
      Log.error(ArcError.PORT_DIRECTION_MISMATCH.format(
          astPort.getQName(), "target", printConnector(connectorOfPortAccess),
          astPort.isPresentComponent() ? "the port of a subcomponent" : "a forwarding port"
        ),
        astPort.get_SourcePositionStart()
      );
    }
  }

  /**
   * @return a nice string that can be used to enhance error messages
   */
  private static String printConnector(ASTConnector connector){
    return new ArcBasisFullPrettyPrinter().prettyprint(connector)
      .replaceAll("[;\n]", "");
  }

  protected static void logInfoThatCoCoIsNotChecked(@NotNull ASTPortAccess portAccess) {
    Preconditions.checkNotNull(portAccess);
    Log.debug(String.format("Will not check CoCo on port access '%s' at '%s' as the port to access does not " +
        "seem to exist", portAccess.getQName(), portAccess.get_SourcePositionStart()),
      ConnectorSourceAndTargetDirectionsFit.class.getSimpleName());
  }
}
