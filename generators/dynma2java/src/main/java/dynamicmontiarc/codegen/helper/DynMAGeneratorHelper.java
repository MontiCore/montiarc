/* (c) https://github.com/MontiCore/monticore */
package dynamicmontiarc.codegen.helper;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import de.montiarcautomaton.generator.helper.ComponentHelper;
import de.monticore.types.types._ast.ASTQualifiedName;
import dynamicmontiarc._ast.ASTModeAutomaton;
import dynamicmontiarc.helper.DynamicMontiArcHelper;
import montiarc._ast.ASTComponent;
import montiarc._ast.ASTConnector;
import montiarc._ast.MontiArcMill;
import montiarc._symboltable.ComponentSymbol;

/**
 * TODO: Write me!
 *
 * @author  (last commit) $Author$
 * @version $Revision$,
 *          $Date$
 * @since   TODO: add version number
 *
 */
public class DynMAGeneratorHelper extends ComponentHelper{

  
  /**
   * Constructor for dynamicmontiarc.codegen.helper.DynMAGeneratorHelper
   * @param component
   */
  public DynMAGeneratorHelper(ComponentSymbol component) {
    super(component);
  }
  
  
  
  /**
   * Calculates the connectors that are active in the initial mode.
   * Also contains the static connectors.
   * 
   */
  public static List<ASTConnector> getInitialConnectors(ASTComponent compNode){
    if(!DynamicMontiArcHelper.isDynamic(compNode)) {
      return compNode.getConnectors();
    }
    
    ASTModeAutomaton modeAutomaton = DynamicMontiArcHelper.getModeAutomaton(compNode);
    return getAllConnectorsInMode(compNode, modeAutomaton.getInitialModeDeclarationList().get(0).getName());
  }
  
  public static List<ASTConnector> getAllConnectorsInMode(ASTComponent compNode, String modeName){
    List<ASTConnector> result = new ArrayList<>();
    result.addAll(compNode.getConnectors());
    
    if (DynamicMontiArcHelper.isDynamic(compNode)) {
      ASTModeAutomaton modeAutomaton = DynamicMontiArcHelper.getModeAutomaton(compNode);
      result.addAll(modeAutomaton.getConnectorsInMode(modeName));
    }
    
    return result;
  }
  
  /**
   * Returns a list of all connectors existing in mode targetMode but not in
   * mode sourceMode.
   * 
   * @param modeAut Mode automaton containing the modes
   * @param sourceMode Name of the source mode of the transition
   * @param targetMode Name of the target mode of the transition
   * @return
   */
  public static List<ASTConnector> getPositiveDelta (
      ASTModeAutomaton modeAut, 
      String sourceMode, 
      String targetMode) {
    
    List<ASTConnector> result = new ArrayList<>();
    
    List<ASTConnector> previousConnectors = modeAut.getConnectorsInMode(sourceMode);
    List<ASTConnector> nextConnectors = modeAut.getConnectorsInMode(targetMode);
    List<String> prevConnectorStrings = 
        previousConnectors.stream()
        .flatMap(conn -> DynMAGeneratorHelper.printSingleConnectors(conn).stream())
        .collect(Collectors.toList());
    
    for (ASTConnector connector : nextConnectors) {
      for (ASTQualifiedName target : connector.getTargetsList()) {
        String simpleConnectorString = connector.getSource().toString() + " -> " + target.toString();
        if(!prevConnectorStrings.contains(simpleConnectorString)) {
          List<ASTQualifiedName> targetList = new ArrayList<>();
          targetList.add(target);
          
          result.add(
              MontiArcMill.connectorBuilder()
                .setSource(connector.getSource())
                .setTargetsList(targetList)
                .build()
              );
          
        }
      }
    }
    
    return result;
  }
  
  private static List<String> printSingleConnectors(ASTConnector connector) {
    
    List<String> connectors = new ArrayList<>();
    for (ASTQualifiedName targetName : connector.getTargetsList()) {
      connectors.add(connector.getSource().toString() + " -> " + targetName.toString());
    }
    
    return connectors;
  }
}
