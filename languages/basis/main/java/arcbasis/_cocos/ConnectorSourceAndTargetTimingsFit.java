/* (c) https://github.com/MontiCore/monticore */
package arcbasis._cocos;

import arcbasis._ast.ASTComponentType;
import arcbasis._ast.ASTConnector;
import arcbasis._ast.ASTPortAccess;
import arcbasis._symboltable.ComponentInstanceSymbol;
import arcbasis._symboltable.ComponentTypeSymbol;
import arcbasis._symboltable.PortSymbol;
import arcbasis._visitor.ArcBasisFullPrettyPrinter;
import arcbasis.timing.Timing;
import com.google.common.base.Preconditions;
import de.se_rwth.commons.logging.Log;
import montiarc.util.ArcError;
import org.codehaus.commons.nullanalysis.NotNull;

import java.util.Optional;

/**
 * Checks whether source and target timing of connected ports match.
 * <p>
 * untimed -> untimed matches
 * instant -> instant matches
 * delayed -> instant matches
 * sync -> sync matches
 * causalsync -> causalsync matches
 */
public class ConnectorSourceAndTargetTimingsFit implements ArcBasisASTComponentTypeCoCo {

  /**
   * When we can find a port symbol that fits the given port access and we when its type has already been set then we
   * return the type. When we can not find the port symbol or when we do not find the component instance of the port
   * access or the type of that instance or if we do not find the type of the port then we return an empty Optional.
   */
  protected static Optional<Timing> getTimingOfPortIfPresent(@NotNull ASTPortAccess astPort,
                                                             @NotNull ComponentTypeSymbol enclComp) {
    Preconditions.checkNotNull(astPort);
    Preconditions.checkNotNull(enclComp);

    if (astPort.isPresentComponent()) {
      Optional<ComponentInstanceSymbol> portOwner = enclComp.getSubComponent(astPort.getComponent());
      if (portOwner.isEmpty() || !portOwner.get().isPresentType()) {
        return Optional.empty();
      }

      return portOwner.get().getType().getTypeInfo().getPort(astPort.getPort()).map(PortSymbol::getTiming);
    } else if (enclComp.getPort(astPort.getPort()).map(PortSymbol::isTypePresent).orElse(false)) {
      return enclComp.getPort(astPort.getPort()).map(PortSymbol::getTiming);
    }
    return Optional.empty();
  }

  protected static void logInfoThatCoCoIsNotChecked4Connection(@NotNull ASTConnector connector) {
    Preconditions.checkNotNull(connector);
    Log.debug(String.format("Will not check CoCo on connector '%s' at '%s' as its source port does not " +
                "seem to be resolvable to a port symbol or the type of the port does not seem to be set.",
            printConnector(connector), connector.get_SourcePositionStart()),
        ConnectorSourceAndTargetTimingsFit.class.getSimpleName()
    );
  }

  protected static void logInfoThatCoCoIsNotChecked4TargetPort(@NotNull ASTPortAccess targetPort) {
    Preconditions.checkNotNull(targetPort);
    Log.debug(String.format("Will not check CoCo on port '%s' at '%s' a its symbol does not " +
            "seem to exist or the type of the port does not seem to be set.", targetPort.getQName(),
        targetPort.get_SourcePositionStart()), ConnectorSourceAndTargetTimingsFit.class.getSimpleName()
    );
  }

  /**
   * @return a nice string that can be used to enhance error messages
   */
  private static String printConnector(ASTConnector connector) {
    return new ArcBasisFullPrettyPrinter().prettyprint(connector)
        .replaceAll("[;\n]", "");
  }

  protected static boolean timingMatches(Timing timing, Timing other) {
    switch (timing) {
      case UNTIMED: return other.equals(Timing.UNTIMED);
      case INSTANT: case DELAYED: return other.equals(Timing.INSTANT) || other.equals(Timing.DELAYED);
      case SYNC: case CAUSALSYNC: default: return other.equals(Timing.SYNC) || other.equals(Timing.CAUSALSYNC);
    }
  }

  @Override
  public void check(@NotNull ASTComponentType node) {
    Preconditions.checkNotNull(node);
    Preconditions.checkArgument(node.isPresentSymbol(), "ASTComponent node '%s' at '%s' has no symbol. Thus can not " +
            "check CoCo '%s'. Did you forget to run the scopes genitor and symbol table completer before checking the " +
            "coco?",
        node.getName(), node.get_SourcePositionStart(), this.getClass().getSimpleName());
    ComponentTypeSymbol enclComponent = node.getSymbol();

    for (ASTConnector conn : node.getConnectors()) {
      Optional<Timing> timingOfSource = getTimingOfPortIfPresent(conn.getSource(), enclComponent);

      if (timingOfSource.isEmpty()) {
        logInfoThatCoCoIsNotChecked4Connection(conn);
        continue;
      }

      for (ASTPortAccess target : conn.getTargetList()) {
        Optional<Timing> timingOfTarget = getTimingOfPortIfPresent(target, enclComponent);
        if (timingOfTarget.isPresent()) {
          // Perform timing check
          if (!timingMatches(timingOfSource.get(), timingOfTarget.get())) {
            Log.error(ArcError.SOURCE_AND_TARGET_TIMING_MISMATCH.format(
                    timingOfSource.get().getName(), timingOfTarget.get().getName(),
                    printConnector(conn),
                    enclComponent.getFullName()),
                conn.get_SourcePositionStart(), conn.get_SourcePositionEnd());
          }
        } else {
          logInfoThatCoCoIsNotChecked4TargetPort(target);
        }
      }
    }
  }
}
