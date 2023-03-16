/* (c) https://github.com/MontiCore/monticore */
package arcbasis._cocos;

import arcbasis._ast.ASTComponentType;
import arcbasis._ast.ASTConnector;
import arcbasis._ast.ASTPortAccess;
import arcbasis._symboltable.ComponentInstanceSymbol;
import arcbasis._symboltable.ComponentTypeSymbol;
import arcbasis._symboltable.PortSymbol;
import com.google.common.base.Preconditions;
import de.se_rwth.commons.logging.Log;
import montiarc.util.ArcError;
import org.codehaus.commons.nullanalysis.NotNull;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * Checks that for every cycle there is at least one component that is strongly causal modulo a port on this cycle.
 */
public class FeedbackLoopStrongCausality implements ArcBasisASTComponentTypeCoCo {

  @Override
  public void check(@NotNull ASTComponentType node) {
    Preconditions.checkNotNull(node);
    Preconditions.checkArgument(node.isPresentSymbol());

    List<ASTConnector> connectors = node.getConnectors();
    List<ASTPortAccess> sources = connectors.stream().map(ASTConnector::getSource).collect(Collectors.toUnmodifiableList());

    // keep the visited list throughout all circle detection to avoid detecting the same circle twice
    ArrayList<String> visited = new ArrayList<>();
    for (ASTPortAccess source : sources) {
      ArrayDeque<String> next = new ArrayDeque<>();
      next.add(getComponentNullSafe(source));

      while (!next.isEmpty()) {
        String current = next.pop();
        for (String neighbourComponent : findNextComponent(node, current)) {
          if (!Objects.equals(neighbourComponent, "")) {
            if (Objects.equals(getComponentNullSafe(source), neighbourComponent)) {
              Log.error(ArcError.FEEDBACK_LOOP_TIMING_NOT_DELAYED.format(node.getName()),
                  source.get_SourcePositionStart(), source.get_SourcePositionEnd());
            } else if (!visited.contains(neighbourComponent)) {
              next.add(neighbourComponent);
            }
          }
        }
        visited.add(current);
      }
    }
  }

  protected static String getComponentNullSafe(ASTPortAccess access) {
    Preconditions.checkNotNull(access);
    return access.isPresentComponent() ? access.getComponent() : "";
  }

  /**
   * Looks at all connectors of {@code componentType} and finds
   * next component starting from {@code component}.
   * Connectors with {@code ignoreTiming} are ignored.
   */
  protected Set<String> findNextComponent(ASTComponentType componentType, String component) {
    Preconditions.checkNotNull(componentType);
    return componentType.getConnectors().stream()
        .filter(c -> isPortNotStronglyCausal(getPortIfPresent(c.getSource(), componentType.getSymbol())))
        .filter(c -> getComponentNullSafe(c.getSource()).equals(component))
        .flatMap(c -> c.getTargetList().stream()
            .filter(t -> isPortNotStronglyCausal(getPortIfPresent(t, componentType.getSymbol())))
            .map(FeedbackLoopStrongCausality::getComponentNullSafe))
        .collect(Collectors.toSet());
  }

  protected boolean isPortNotStronglyCausal(Optional<PortSymbol> portSymbol) {
    return portSymbol.isPresent() && !portSymbol.get().isStronglyCausal();
  }

  protected static Optional<PortSymbol> getPortIfPresent(@NotNull ASTPortAccess astPort,
                                                             @NotNull ComponentTypeSymbol enclComp) {
    Preconditions.checkNotNull(astPort);
    Preconditions.checkNotNull(enclComp);

    if (astPort.isPresentComponent()) {
      Optional<ComponentInstanceSymbol> portOwner = enclComp.getSubComponent(astPort.getComponent());
      if (portOwner.isEmpty() || !portOwner.get().isPresentType()) {
        return Optional.empty();
      }

      return portOwner.get().getType().getTypeInfo().getPort(astPort.getPort());
    } else if (enclComp.getPort(astPort.getPort()).map(PortSymbol::isTypePresent).orElse(false)) {
      return enclComp.getPort(astPort.getPort());
    }
    return Optional.empty();
  }
}
