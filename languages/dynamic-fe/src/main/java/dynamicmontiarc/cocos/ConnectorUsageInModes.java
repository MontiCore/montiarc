/* (c) https://github.com/MontiCore/monticore */
package dynamicmontiarc.cocos;

import de.monticore.types.types._ast.ASTQualifiedName;
import de.se_rwth.commons.logging.Log;
import dynamicmontiarc._ast.ASTInitialModeDeclaration;
import dynamicmontiarc._ast.ASTModeDeclaration;
import dynamicmontiarc._ast.ASTModeAutomaton;
import dynamicmontiarc._cocos.DynamicMontiArcASTModeAutomatonCoCo;
import dynamicmontiarc.helper.DynamicMontiArcHelper;
import montiarc._ast.*;
import montiarc._cocos.MontiArcASTComponentCoCo;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

/**
 */
public class ConnectorUsageInModes implements MontiArcASTComponentCoCo {


  @Override
  public void check(ASTComponent node) {
    if (!DynamicMontiArcHelper.isDynamic(node)) {
      return;
    }

    List<ASTPort> ports = node.getPorts();
    List<ASTPort> outgoingPorts = new ArrayList<>();
    for (ASTPort port : ports) {
      if (!port.isIncoming()) {
        outgoingPorts.add(port);
      }
    }

    ASTModeAutomaton automaton = DynamicMontiArcHelper.getModeAutomaton(node);
    Set<String> modeNames = automaton.getModeNames();

    for (String modeName : modeNames) {
      List<String> subComponentPorts = new ArrayList<>();
      List<ASTConnector> connectors = automaton.getConnectorsInMode(modeName);
      for (ASTPort port : outgoingPorts) {
        List<ASTConnector> connectorsForPort = new ArrayList<>();
        for (ASTConnector connector : connectors) {
          for (ASTQualifiedName qualifiedName : connector.getTargetsList()) {
            if (qualifiedName.toString().equals(port.getName(0))) {
              connectorsForPort.add(connector);

            }
          }
        }
        if (connectorsForPort.size() > 1) {
          for (ASTConnector connector : connectorsForPort) {
            Log.error("0xMA213 An outgoing port is connected more than once " +
                            "in mode " + modeName + ".",
                    connector.get_SourcePositionStart());
          }
        }
      }
      for (ASTConnector connector : connectors) {
        for (ASTQualifiedName qualifiedName : connector.getTargetsList()) {
          if (qualifiedName.toString().contains(".")) {
            if (subComponentPorts.contains(qualifiedName.toString())) {
              Log.error("0xMA214 An incoming port of a subcomponentis connected more than once " +
                              "in mode " + modeName + ".",
                      connector.get_SourcePositionStart());
            } else {
              subComponentPorts.add(qualifiedName.toString());
            }
          }
        }
      }
    }
  }
}
