/* (c) https://github.com/MontiCore/monticore */
package arcbasis._cocos;

import arcbasis._ast.ASTComponentType;
import arcbasis._ast.ASTConnector;
import arcbasis._ast.ASTPortAccess;
import arcbasis._symboltable.ComponentInstanceSymbol;
import arcbasis._symboltable.ComponentTypeSymbol;
import arcbasis._symboltable.PortSymbol;
import com.google.common.base.Preconditions;
import de.monticore.symboltable.resolving.ResolvedSeveralEntriesForSymbolException;
import de.monticore.types.check.ITypeRelations;
import de.monticore.types.check.SymTypeExpression;
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
public class ConnectorTypesFit implements ArcBasisASTComponentTypeCoCo {

  protected final ITypeRelations tr;

  public ConnectorTypesFit(@NotNull ITypeRelations tr) {
    this.tr = Preconditions.checkNotNull(tr);
  }

  @Override
  public void check(@NotNull ASTComponentType node) {
    Preconditions.checkNotNull(node);
    Preconditions.checkArgument(node.isPresentSymbol());

    for (ASTConnector conn : node.getConnectors()) {
      this.check(conn, node.getSymbol());
    }
  }

  protected void check(@NotNull ASTConnector conn, @NotNull ComponentTypeSymbol enclComponent) {
    Optional<SymTypeExpression> symTypeOfSource = getTypeOfPortIfPresent(conn.getSource(), enclComponent);
    if (symTypeOfSource.isEmpty()) {
      Log.debug(String.format("Skip coco check, cannot resolve source port '%s'", conn.getSource().getQName()), this.getClass().getCanonicalName());
      return;
    }

    for (ASTPortAccess target : conn.getTargetList()) {
      Optional<SymTypeExpression> symTypeOfTarget = getTypeOfPortIfPresent(target, enclComponent);
      if (symTypeOfTarget.isPresent()) {
        // Perform type check
        try {
          if (!tr.compatible(symTypeOfTarget.get(), symTypeOfSource.get())) {
            Log.error(
              ArcError.CONNECTOR_TYPE_MISMATCH.format(
                symTypeOfSource.get().print(), symTypeOfTarget.get().print(),
                enclComponent.getFullName()),
              conn.get_SourcePositionStart());
          }
        } catch (ResolvedSeveralEntriesForSymbolException e) {
          Log.error(
            ArcError.CONNECTOR_TYPE_MISMATCH.format(
              symTypeOfSource.get().print(), symTypeOfTarget.get().print(),
              enclComponent.getFullName()),
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
  protected static Optional<SymTypeExpression> getTypeOfPortIfPresent(@NotNull ASTPortAccess astPort,
                                                                      @NotNull ComponentTypeSymbol enclComp) {
    Preconditions.checkNotNull(astPort);
    Preconditions.checkNotNull(enclComp);

    if (astPort.isPresentComponent()) {
      Optional<ComponentInstanceSymbol> portOwner = enclComp.getSubComponent(astPort.getComponent());
      if (portOwner.isEmpty() || !portOwner.get().isPresentType()) {
        return Optional.empty();
      }

      return portOwner.get().getType().getTypeExprOfPort(astPort.getPort());
    } else if (enclComp.getPort(astPort.getPort(), true).map(PortSymbol::isTypePresent).orElse(false)) {
      return enclComp.getPort(astPort.getPort(), true).map(PortSymbol::getType);
    }
    return Optional.empty();
  }

  protected static void logInfoThatCoCoIsNotChecked4TargetPort(@NotNull ASTPortAccess targetPort) {
    Preconditions.checkNotNull(targetPort);
    Log.debug(String.format("Will not check CoCo on port '%s' at '%s' a its symbol does not " +
        "seem to exist or the type of the port does not seem to be set.", targetPort.getQName(),
      targetPort.get_SourcePositionStart()), ConnectorDirectionsFit.class.getSimpleName()
    );
  }
}
