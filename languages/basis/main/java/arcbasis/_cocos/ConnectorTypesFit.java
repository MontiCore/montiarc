/* (c) https://github.com/MontiCore/monticore */
package arcbasis._cocos;

import arcbasis._ast.ASTConnector;
import arcbasis._ast.ASTPortAccess;
import arcbasis.context.WithinComponentResolver;
import com.google.common.base.Preconditions;
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

  protected final WithinComponentResolver resolver;

  public ConnectorTypesFit() {
    this(new WithinComponentResolver());
  }

  public ConnectorTypesFit(@NotNull WithinComponentResolver resolver) {
    this.resolver = Preconditions.checkNotNull(resolver);
  }

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
              target.get_SourcePositionStart(),
              target.get_SourcePositionEnd()
            );
          }
        } catch (ResolvedSeveralEntriesForSymbolException e) {
          Log.error(
            ArcError.CONNECTOR_TYPE_MISMATCH.format(
              targetType.print(), sourceType.print()),
            target.get_SourcePositionStart(),
            target.get_SourcePositionEnd()
          );
        }
      } else {
        logInfoThatCoCoIsNotChecked4TargetPort(target);
      }
    }
  }

  /**
   * @return the type of the accessed port as resolved by
   * {@link WithinComponentResolver#typeOf(ASTPortAccess)}
   */
  protected Optional<SymTypeExpression> getTypeOfPortIfPresent(@NotNull ASTPortAccess astPort) {
    Preconditions.checkNotNull(astPort);
    return this.resolver.typeOf(astPort);
  }

  protected static void logInfoThatCoCoIsNotChecked4TargetPort(@NotNull ASTPortAccess targetPort) {
    Preconditions.checkNotNull(targetPort);
    Log.debug(() -> String.format("Will not check CoCo on port '%s' at '%s' a its symbol does not " +
        "seem to exist or the type of the port does not seem to be set.", targetPort.getQName(),
      targetPort.get_SourcePositionStart()), ConnectorDirectionsFit.class.getSimpleName()
    );
  }
}
