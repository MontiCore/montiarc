/* (c) https://github.com/MontiCore/monticore */
package arcbasis._cocos;

import arcbasis._ast.ASTComponentType;
import arcbasis._ast.ASTConnector;
import arcbasis._symboltable.ArcPortSymbol;
import arcbasis._symboltable.ComponentInstanceSymbol;
import com.google.common.base.Preconditions;
import de.se_rwth.commons.logging.Log;
import montiarc.util.ArcError;
import org.codehaus.commons.nullanalysis.NotNull;

import java.util.Collection;
import java.util.stream.Collectors;

/**
 * CV6: All ports of subcomponents should be used in at least one connector.<br>
 * DIFFERENCE to CV5: CV5 checks that in and out ports are connected
 * <em>within</em> the (non-atomic) component itself while CV6 checks that a
 * subcomponent is connected in its <em>outer context</em> (i.e. the outer
 * component).
 * <p>
 * Implements [Hab16] CV6: All ports of subcomponents should be used in at
 * least one connector. (p.72 Lst. 3.53)
 */
public class SubPortsConnected implements ArcBasisASTComponentTypeCoCo {

  @Override
  public void check(@NotNull ASTComponentType node) {
    Preconditions.checkNotNull(node);
    Preconditions.checkArgument(node.isPresentSymbol());

    if (node.getSymbol().isAtomic()) {
      return;
    }

    final Collection<String> targets = node.getConnectors().stream()
      .map(ASTConnector::getTargetsNames)
      .flatMap(Collection::stream)
      .collect(Collectors.toList());
    final Collection<String> sources = node.getConnectors().stream()
      .map(ASTConnector::getSourceName)
      .collect(Collectors.toList());

    for (ComponentInstanceSymbol subSymbol : node.getSymbol().getSubComponents()) {
      if (!subSymbol.isPresentType()) continue;

      // --------- INCOMING PORTS ----------
      Collection<String> subInputPorts = this.getNames(subSymbol.getType().getTypeInfo().getAllIncomingPorts());
      subInputPorts = subInputPorts.stream()
        .map(s -> subSymbol.getName() + "." + s)
        .collect(Collectors.toList());
      subInputPorts.removeAll(targets);
      for (String port : subInputPorts) {
        if (!sources.contains(port)) {
          Log.error(ArcError.IN_PORT_NOT_CONNECTED.format(port), subSymbol.getSourcePosition());
        }
      }

      // --------- OUTGOING PORTS ----------
      Collection<String> subOutputPorts = this.getNames(subSymbol.getType().getTypeInfo().getAllOutgoingPorts());
      subOutputPorts = subOutputPorts.stream()
        .map(s -> subSymbol.getName() + "." + s)
        .collect(Collectors.toList());
      subOutputPorts.removeAll(sources);
      for (String port : subOutputPorts) {
        if (!targets.contains(port)) {
          Log.warn(ArcError.OUT_PORT_NOT_CONNECTED.format(port), subSymbol.getSourcePosition());
        }
      }
    }
  }

  protected Collection<String> getNames(Collection<ArcPortSymbol> ports) {
    return ports.stream().map(ArcPortSymbol::getName).collect(Collectors.toList());
  }
}