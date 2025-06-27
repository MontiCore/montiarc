/* (c) https://github.com/MontiCore/monticore */
package arcbasis._cocos;

import arcbasis._ast.ASTConnector;
import arcbasis._ast.ASTPortAccess;
import com.google.common.base.Preconditions;
import de.monticore.symbols.compsymbols._symboltable.ComponentTypeSymbol;
import de.monticore.symbols.compsymbols._symboltable.PortSymbol;
import de.monticore.symboltable.IScopeSpanningSymbol;
import de.monticore.symboltable.resolving.ResolvedSeveralEntriesForSymbolException;
import de.monticore.types.check.SymTypeExpression;
import de.monticore.types.check.SymTypeObscure;
import de.monticore.types3.SymTypeRelations;
import de.se_rwth.commons.logging.Log;
import montiarc.util.ArcError;
import org.codehaus.commons.nullanalysis.NotNull;

import java.util.Optional;

/**
 * This context-condition checks weather the type provided by a connector's
 * source port is compatible to the expected type of the connector's target
 * ports.
 * <p>
 * Implements [Hab16] R8: The target port in a connection has to be compatible
 * to the source port, i.e., the type of the target port is identical or a
 * supertype of the source port type. (p. 66, lst. 3.43)
 */
public class ConnectorTypesFit implements ArcBasisASTConnectorCoCo {

  @Override
  public void check(@NotNull ASTConnector conn) {
    Preconditions.checkNotNull(conn);

    Optional<SymTypeExpression> symTypeOfSource = getTypeOfPortIfPresent(conn.getSource());
    if (symTypeOfSource.isEmpty()) {
      Log.debug(() -> String.format("Skip coco check, cannot resolve source port '%s'", conn.getSource().getQName()),
        this.getClass().getCanonicalName());
      return;
    }

    for (ASTPortAccess target : conn.getTargetList()) {
      Optional<SymTypeExpression> symTypeOfTarget = getTypeOfPortIfPresent(target);
      if (symTypeOfTarget.isPresent()) {
        SymTypeExpression sourceType = symTypeOfSource.get();
        SymTypeExpression targetType = symTypeOfTarget.get();

        // Skip type checking if either source or target is Obscure
        if (sourceType instanceof SymTypeObscure || targetType instanceof SymTypeObscure) {
          Log.debug("Skip type check: One or both port types are 'Obscure'.", this.getClass().getSimpleName());
          continue;
        }

        // Perform type check
        try {
          if (!SymTypeRelations.isCompatible(targetType, sourceType)) {
            Log.error(
              ArcError.CONNECTOR_TYPE_MISMATCH.format(
                targetType.print(), sourceType.print()),
              conn.get_SourcePositionStart());
          }
        } catch (ResolvedSeveralEntriesForSymbolException e) {
          Log.error(
            ArcError.CONNECTOR_TYPE_MISMATCH.format(
              targetType.print(), sourceType.print()),
            conn.get_SourcePositionStart());
        }
      } else {
        logInfoThatCoCoIsNotChecked4TargetPort(target);
      }
    }
  }

  /**
   * When we can find a port symbol that fits the given port access and we when its type has already been set then we
   * return the type. When we can not find the port symbol or when we do not find the component instance of the port
   * access or the type of that instance or if we do not find the type of the port then we return an empty Optional.
   */
  protected Optional<SymTypeExpression> getTypeOfPortIfPresent(@NotNull ASTPortAccess astPort) {
    Preconditions.checkNotNull(astPort);

    if (astPort.isPresentComponent()) {
      if (astPort.isPresentComponentSymbol() && astPort.getComponentSymbol().isTypePresent()) {
        return astPort.getComponentSymbol().getType().getTypeOfPort(astPort.getPort());
      }
    } else if (getEnclosingComponent(astPort).isPresent()) {
      return getEnclosingComponent(astPort).get().getPort(astPort.getPort()).map(PortSymbol::getType);
    } else if (astPort.isPresentPortSymbol() && astPort.getPortSymbol().isTypePresent()) {
      return Optional.ofNullable(astPort.getPortSymbol().getType());
    }
    return Optional.empty();
  }

  protected static void logInfoThatCoCoIsNotChecked4TargetPort(@NotNull ASTPortAccess targetPort) {
    Preconditions.checkNotNull(targetPort);
    Log.debug(() -> String.format("Will not check CoCo on port '%s' at '%s' a its symbol does not " +
        "seem to exist or the type of the port does not seem to be set.", targetPort.getQName(),
      targetPort.get_SourcePositionStart()), ConnectorDirectionsFit.class.getSimpleName()
    );
  }

  /**
   * @return an {@code Optional} of the component type this portAccess belongs to. The {@code Optional} is empty if the access
   * does not belong to a component type.
   */
  protected Optional<ComponentTypeSymbol> getEnclosingComponent(@NotNull ASTPortAccess portAccess) {
    Preconditions.checkNotNull(portAccess);
    if (portAccess.getEnclosingScope() == null) {
      return Optional.empty();
    }
    if (!portAccess.getEnclosingScope().isPresentSpanningSymbol()) {
      return Optional.empty();
    }
    IScopeSpanningSymbol symbol = portAccess.getEnclosingScope().getSpanningSymbol();
    if (symbol instanceof ComponentTypeSymbol) {
      return Optional.of((ComponentTypeSymbol) symbol);
    } else {
      return Optional.empty();
    }
  }
}
