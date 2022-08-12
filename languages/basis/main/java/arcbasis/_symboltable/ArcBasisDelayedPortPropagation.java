/* (c) https://github.com/MontiCore/monticore */
package arcbasis._symboltable;

import arcbasis._ast.ASTComponentType;
import arcbasis._ast.ASTConnectorTOP;
import arcbasis._ast.ASTPortAccess;
import com.google.common.base.Preconditions;
import org.codehaus.commons.nullanalysis.NotNull;

import java.util.List;
import java.util.Optional;

public class ArcBasisDelayedPortPropagation {

  protected static Optional<ComponentInstanceSymbol> getInstanceIfPresent(@NotNull ASTPortAccess astPort,
                                                                          @NotNull ComponentTypeSymbol enclComp) {
    Preconditions.checkNotNull(astPort);
    Preconditions.checkNotNull(enclComp);

    if (astPort.isPresentComponent()) {
      return enclComp.getSubComponent(astPort.getComponent());
    }
    return Optional.empty();
  }

  /**
   * We consider an instance delayed if all its incoming ports are connected with a delayed port
   *
   * @param instance the instance to look at
   * @param enclComp the enclosing component type of the instance
   * @return if the instance is delayed
   */
  protected static boolean isInstanceDelayed(@NotNull ComponentInstanceSymbol instance,
                                             @NotNull ASTComponentType enclComp) {
    List<PortSymbol> incomingPorts = instance.getType().getTypeInfo().getIncomingPorts();
    if (incomingPorts.isEmpty())
      return false;

    for (PortSymbol port : incomingPorts) {
      if (!isTargetPortDelayed(instance.getName() + "." + port.getName(), enclComp))
        return false;
    }
    return true;
  }

  /**
   * Ports are considered delayed if they are the target of a delayed port or the source subcomponent is delayed
   *
   * @param portAccessName the name of the port in connections (i.e. instanceName.portName)
   * @param enclComp       the enclosing component type of the connections
   * @return if the Port is delayed
   */
  protected static boolean isTargetPortDelayed(@NotNull String portAccessName,
                                               @NotNull ASTComponentType enclComp) {
    Optional<ASTPortAccess> portAccess = enclComp.getConnectorsMatchingTarget(portAccessName).stream().map(ASTConnectorTOP::getSource).findFirst();
    if (portAccess.isEmpty())
      return false;
    Optional<ComponentInstanceSymbol> previousInstance = portAccess.flatMap(pa -> getInstanceIfPresent(pa, enclComp.getSymbol()));
    if (previousInstance.isEmpty())
      return false;

    // Either previous port is already delayed
    if (previousInstance.get().getType().getTypeInfo().getPort(portAccess.get().getPort()).isPresent()) {
      PortSymbol previousPort = previousInstance.get().getType().getTypeInfo().getPort(portAccess.get().getPort()).get();
      if (previousPort.isDelayed())
        return true;
    }

    // Or the whole instance is considered delayed
    return isInstanceDelayed(previousInstance.get(), enclComp);
  }

  public static void complete(ASTComponentType node) {
    Preconditions.checkNotNull(node);

    List<PortSymbol> outgoingPorts = node.getSymbol().getOutgoingPorts().stream().filter(p -> !p.isDelayed()).toList();
    for (PortSymbol port : outgoingPorts) {
      if (isTargetPortDelayed(port.getName(), node)) {
        port.setDelayed();
      }
    }
  }
}
