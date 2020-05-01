/* (c) https://github.com/MontiCore/monticore */
package dynamicmontiarc._ast;

import montiarc._ast.ASTConnector;

import java.util.*;
import java.util.stream.Collectors;

/**
 * TODO
 *
 */
public class ASTModeAutomaton extends ASTModeAutomatonTOP {

  public ASTModeAutomaton(
      Optional<String> name,
      List<ASTModeDeclaration> modeDeclarations,
      List<ASTInitialModeDeclaration> initialModes,
      List<ASTModeTransition> modeTransitions) {
    super(name, modeDeclarations, initialModes, modeTransitions);
  }

  public ASTModeAutomaton() {
    super();
  }

  /**
   * Returns names of all modes used in the component
   *
   * @return A set containing the names of all modes in the component.
   */
  public Set<String> getModeNames() {

    return this.getModeDeclarationList().stream()
        .map(ASTModeDeclaration::getModeList)
        .flatMap(Collection::stream)
        .collect(Collectors.toSet());
  }


  public Set<ASTModeDeclaration> getModeDeclarationsByName(String mode){
    return this.getModeDeclarationList().stream()
        .filter(e -> e.getModeList().contains(mode))
        .collect(Collectors.toSet());
  }

  public List<ASTConnector> getConnectorsInMode(String mode){
    List<ASTConnector> connectors = new ArrayList<>();
    for (ASTModeDeclaration modeDeclaration :
        getModeDeclarationsByName(mode)) {
      connectors.addAll(modeDeclaration.getConnectorList());
    }
    return connectors;
  }
  
  
  public List<String> getActiveSubcomponentsInMode(String modeName) {
    Set<ASTModeDeclaration> modeDecls = this.getModeDeclarationsByName(modeName);
    
    ArrayList<String> result = new ArrayList<String>();
    for (ASTModeDeclaration declaration : modeDecls) {
      for(ASTUseStatement useStatement : declaration.getUseStatementList()) {
        result.addAll(useStatement.getNameList());
      }
    }
    return result;
  }
}
