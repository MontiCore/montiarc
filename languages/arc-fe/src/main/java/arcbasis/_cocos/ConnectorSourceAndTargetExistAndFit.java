/* (c) https://github.com/MontiCore/monticore */
package arcbasis._cocos;

import arcbasis._ast.ASTComponentType;
import arcbasis._ast.ASTConnector;
import arcbasis._ast.ASTPortAccess;
import arcbasis._symboltable.ComponentInstanceSymbol;
import arcbasis._symboltable.ComponentTypeSymbol;
import arcbasis._symboltable.PortSymbol;
import arcbasis._visitor.ArcBasisFullPrettyPrinter;
import arcbasis.check.ArcTypeCheck;
import arcbasis.check.CompTypeExpression;
import arcbasis.util.ArcError;
import com.google.common.base.Preconditions;
import de.monticore.symboltable.resolving.ResolvedSeveralEntriesForSymbolException;
import de.monticore.types.check.SymTypeExpression;
import de.se_rwth.commons.logging.Log;
import org.codehaus.commons.nullanalysis.NotNull;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.function.Supplier;

/**
 * Checks whether source and target type of connected ports match. It also
 * checks whether the source and target ports of the connectors actually exist.
 *
 * @implements [Hab16] CO3: Unqualified sources or targets in connectors either
 * refer to a port or a subcomponent in the same namespace. (p.61 Lst. 3.35)
 * @implements [Hab16] R5: The first part of a qualified connector’s source
 * respectively target must correspond to a subcomponent declared in the current
 * component definition. (p.64 Lst. 3.40)
 * @implements [Hab16] R6: The second part of a qualified connector’s source
 * respectively target must correspond to a port name of the referenced
 * subcomponent determined by the first part. (p.64, Lst. 3.41)
 * @implements [Hab16] R7: The source port of a simple connector must exist in
 * the subcomponents type. (p.65 Lst. 3.42)
 * @implements [Hab16] R8: The target port in a connection has to be compatible
 * to the source port, i.e., the type of the target port is identical or a
 * supertype of the source port type. (p. 66, lst. 3.43)
 */
public class ConnectorSourceAndTargetExistAndFit implements ArcBasisASTComponentTypeCoCo {

  @Override
  public void check(@NotNull ASTComponentType node) {
    Preconditions.checkArgument(node != null);
    Preconditions.checkArgument(node.isPresentSymbol(), "ASTComponent node '%s' has no symbol. "
      + "Did you forget to run the SymbolTableCreator before checking cocos?", node.getName());
    ComponentTypeSymbol component = node.getSymbol();
    node.getConnectors().forEach(connector -> {
      final Optional<PortSymbol> sourcePort;
      final List<Optional<PortSymbol>> targetPorts = new ArrayList<>();
      try {
        sourcePort = getPortSymbol(connector.getSource(), component, ()->printConnector(connector), true);
        connector.streamTarget().map(target -> getPortSymbol(target, component, ()->printConnector(connector), false)).forEach(targetPorts::add);
      }
      catch (ResolvedSeveralEntriesForSymbolException e) {
        // none of this coco's business
        return;
      }

      // finally check the types of the (present) port symbols
      sourcePort.ifPresent(source ->
        targetPorts.stream().filter(Optional::isPresent).map(Optional::get).forEach(target -> {

          SymTypeExpression sourceType = source.getType();
          SymTypeExpression targetType = target.getType();

          if (!ArcTypeCheck.compatible(sourceType, targetType)) {
            Log.error(
              ArcError.SOURCE_AND_TARGET_TYPE_MISMATCH.format(
                source.getType().print(), target.getType().print(),
                printConnector(connector),
                component.getFullName()),
              connector.get_SourcePositionStart());
          }
        })
      );
    });
  }

  /**
   * @return a nice string that can be used to enhance error messages
   */
  private static String printConnector(ASTConnector connector){
    return new ArcBasisFullPrettyPrinter().prettyprint(connector)
        .replaceAll("[;\n]", "");
  }

  /**
   * Retrieves the symbol of a port and logs an error if that is not possible.
   * Also calls {@link #checkDirection(PortSymbol, ASTPortAccess, boolean, Supplier)}
   * to ensure the port is not connected backwards
   * @param portAccess the port for which the symbol should be found
   * @param component the component that contains the port
   * @param isSource identifies whether the port is the source or the target of an connector
   * @param connector string representation of the connector to use in error messages
   * @return symbol of the port, may be {@link Optional#empty()}, if {@link Log#enableFailQuick(boolean)} is disabled
   */
  private static Optional<PortSymbol> getPortSymbol(ASTPortAccess portAccess, ComponentTypeSymbol component, Supplier<String> connector, boolean isSource){
    Optional<PortSymbol> portSymbol;
    // is the port is a port of the surrounding component?
    if (!portAccess.isPresentComponent()) {
      portSymbol = component.getPort(portAccess.getPort(), true);
    }
    else {
      // is the port the port of a sub-component?
      portSymbol = component.getSubComponent(portAccess.getComponent())
        .map(ComponentInstanceSymbol::getType)
        .map(CompTypeExpression::getTypeInfo)
        .flatMap(compType -> compType.getPort(portAccess.getPort(), true));
    }
    // some checks with the resulting port-optional
    if (!portSymbol.isPresent()) {
      Log.error(
          String.format((isSource?ArcError.SOURCE_PORT_NOT_EXISTS:ArcError.TARGET_PORT_NOT_EXISTS).toString(),
              portAccess.getQName(),
              connector.get(),
              component.getFullName()),
          portAccess.get_SourcePositionStart());
    } else {
      checkDirection(portSymbol.get(), portAccess, isSource, connector);
    }
    return portSymbol;
  }

  /**
   * Checks if the direction of a port is valid.
   * Incoming ports may usually be connected as target whereas outgoing ports are usually sources of connectors.
   * If the port, however, does not belong to a sub-component but is connected to the surrounding component,
   * it has to be connected the other way around
   * @param symbol port-object that provides it's direction
   * @param port the access node of that same port
   * @param isSource false if this port is a target port
   * @param connector string representation of the connector under test
   */
  protected static void checkDirection(PortSymbol symbol, ASTPortAccess port, boolean isSource, Supplier<String> connector){
    if(symbol.isIncoming() == port.isPresentComponent() == isSource) {
      Log.error(ArcError.PORT_DIRECTION_MISMATCH.format(
          symbol.isIncoming()?"input":"output",
          port.getQName(),
          isSource?"source":"target",
          connector.get(),
          port.isPresentComponent()?"the port of a subcomponent":"a forwarding port"
        ), port.get_SourcePositionStart());
    }
  }
}