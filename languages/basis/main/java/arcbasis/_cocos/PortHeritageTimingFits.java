/* (c) https://github.com/MontiCore/monticore */
package arcbasis._cocos;

import arcbasis._ast.ASTArcComponentType;
import arcbasis.context.WithinComponentResolver;
import com.google.common.base.Preconditions;
import de.monticore.symbols.compsymbols._symboltable.ComponentTypeSymbol;
import de.monticore.symbols.compsymbols._symboltable.PortSymbol;
import de.monticore.symbols.compsymbols._symboltable.Timing;
import de.se_rwth.commons.logging.Log;
import montiarc.util.ArcError;
import org.codehaus.commons.nullanalysis.NotNull;

import java.util.Optional;

public class PortHeritageTimingFits implements ArcBasisASTArcComponentTypeCoCo {

  protected final WithinComponentResolver resolver;

  public PortHeritageTimingFits() {
    this(new WithinComponentResolver());
  }

  public PortHeritageTimingFits(@NotNull WithinComponentResolver resolver) {
    this.resolver = Preconditions.checkNotNull(resolver);
  }

  @Override
  public void check(@NotNull ASTArcComponentType node) {
    Preconditions.checkNotNull(node);
    Preconditions.checkArgument(node.isPresentSymbol());

    ComponentTypeSymbol componentSymbol = node.getSymbol();

    // Iterate over all ports in the current component
    for (PortSymbol port : componentSymbol.getPorts()) {
      // Check if the port overrides a port from the parent component
      Optional<PortSymbol> inheritedPort = getInheritedPort(componentSymbol, port.getName());
      if (inheritedPort.isPresent()) {
        PortSymbol parentPort = inheritedPort.get();

        // Check timing compatibility
        if (port.isOutgoing() && parentPort.isOutgoing()) {
          // Outgoing ports: sync -> non-sync is forbidden
          if (parentPort.getTiming().matches(Timing.TIMED_SYNC) && !port.getTiming().matches(Timing.TIMED_SYNC)) {
            Log.error(ArcError.INVALID_PORT_TIMING_OVERRIDE.format(
                port.getName(), "sync", "non-sync"),
              port.getSourcePosition());
          }
        } else if (port.isIncoming() && parentPort.isIncoming()) {
          // Incoming ports: non-sync -> sync is forbidden
          if (!parentPort.getTiming().matches(Timing.TIMED_SYNC) && port.getTiming().matches(Timing.TIMED_SYNC)) {
            Log.error(ArcError.INVALID_PORT_TIMING_OVERRIDE.format(
                port.getName(), "non-sync", "sync"),
              port.getSourcePosition());
          }
        }
      }
    }
  }

  protected Optional<PortSymbol> getInheritedPort(ComponentTypeSymbol componentSymbol, String portName) {
    Preconditions.checkNotNull(componentSymbol);
    Preconditions.checkNotNull(portName);

    return componentSymbol.getSuperComponentsList().stream()
      .flatMap(parent -> this.resolver.resolveMany(parent.getTypeInfo(), portName, PortSymbol.class).stream())
      .findFirst();
  }
}
