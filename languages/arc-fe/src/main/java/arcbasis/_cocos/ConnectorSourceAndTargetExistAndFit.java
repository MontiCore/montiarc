/* (c) https://github.com/MontiCore/monticore */
package arcbasis._cocos;

import arcbasis._ast.ASTComponentType;
import arcbasis._symboltable.ComponentInstanceSymbol;
import arcbasis._symboltable.ComponentTypeSymbol;
import arcbasis._symboltable.PortSymbol;
import arcbasis.util.ArcError;
import com.google.common.base.Preconditions;
import de.monticore.symboltable.resolving.ResolvedSeveralEntriesForSymbolException;
import de.monticore.types.check.SymTypeExpression;
import de.monticore.types.check.TypeCheck;
import de.se_rwth.commons.logging.Log;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

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
  public void check(ASTComponentType node) {
    Preconditions.checkArgument(node != null);
    Preconditions.checkArgument(node.isPresentSymbol(), "ASTComponent node '%s' has no symbol. "
      + "Did you forget to run the SymbolTableCreator before checking cocos?", node.getName());
    ComponentTypeSymbol component = node.getSymbol();
    node.getConnectors().forEach(connector -> {
      final Optional<PortSymbol> sourcePort;
      final List<Optional<PortSymbol>> targetPorts = new ArrayList<>();
      try {
        if (!connector.getSource().isPresentComponent()) {
          sourcePort = component.getPort(connector.getSource().getPort(), true);
        } else {
          Optional<ComponentInstanceSymbol> componentInstanceSymbol = component
            .getSubComponent(connector.getSource().getComponent());
          if (componentInstanceSymbol.isPresent()) {
            if (componentInstanceSymbol.get().getType().isSymbolLoaded()) {
              sourcePort = componentInstanceSymbol.get().getTypeInfo()
                .getPort(connector.getSource().getPort(), true);
            } else {
              sourcePort = Optional.empty();
            }
          } else {
            sourcePort = Optional.empty();
          }
        }
        if (!sourcePort.isPresent()) {
          Log.error(
            String.format(ArcError.SOURCE_PORT_NOT_EXISTS.toString(), connector.getSourceName(),
              component.getFullName()),
            connector.get_SourcePositionStart());
        }

        connector.streamTargets().forEach(target -> {
          Optional<PortSymbol> targetPort;
          if (!target.isPresentComponent()) {
            targetPort = component.getPort(target.getPort(), true);
          } else {
            Optional<ComponentInstanceSymbol> componentInstanceSymbol = component
              .getSubComponent(target.getComponent());
            if (componentInstanceSymbol.isPresent()) {
              if (componentInstanceSymbol.get().getType().isSymbolLoaded()) {
                targetPort = componentInstanceSymbol.get().getTypeInfo()
                  .getPort(target.getPort(), true);
              } else {
                targetPort = Optional.empty();
              }
            } else {
              targetPort = Optional.empty();
            }
          }
          if (!targetPort.isPresent()) {
            Log.error(
              String.format(ArcError.TARGET_PORT_NOT_EXISTS.toString(), target.getQName(),
                component.getFullName()), connector.get_SourcePositionStart());
          }
          targetPorts.add(targetPort);
        });
      }
      catch (ResolvedSeveralEntriesForSymbolException e) {
        return;
      }

      if (!sourcePort.isPresent()) {
        return;
      }
      PortSymbol source = sourcePort.get();
      for (Optional<PortSymbol> targetPort : targetPorts) {

        if (!targetPort.isPresent()) {
          return;
        }
        PortSymbol target = targetPort.get();
        SymTypeExpression sourceType = source.getType();
        SymTypeExpression targetType = target.getType();

        if (!TypeCheck.compatible(sourceType, targetType)) {
          Log.error(
            String.format(ArcError.SOURCE_AND_TARGET_TYPE_MISMATCH.toString(),
              source.getType().print(), target.getType().print(), component.getFullName()),
            connector.get_SourcePositionStart());
        }
      }
    });
  }
}