/* (c) https://github.com/MontiCore/monticore */
package basicmodeautomata._symboltable;

import arcbasis._ast.ASTComponentBody;
import arcbasis._ast.ASTComponentType;
import arcbasis._ast.ASTConnector;
import arcbasis._symboltable.ComponentInstanceSymbol;
import arcbasis._symboltable.ComponentTypeSymbol;
import arcbasis._symboltable.IArcBasisScope;
import arcbasis._symboltable.IArcBasisScopeTOP;
import arcbasis._symboltable.PortSymbol;
import basicmodeautomata.BasicModeAutomataMill;
import basicmodeautomata._ast.ASTInitialModeDeclarationTOP;
import basicmodeautomata._ast.ASTModeAutomaton;
import basicmodeautomata._ast.ASTModeDeclaration;
import com.google.common.base.Preconditions;
import de.monticore.scbasis._symboltable.SCStateSymbol;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class ModeSymbol extends SCStateSymbol {
  
  protected ASTComponentBody body;
  protected List<ASTModeDeclaration> declarations;

  public ModeSymbol(String name) {
    super(name);
  }

  public void setBody(ASTComponentBody body) {
    this.body = body;
  }

  public void setDeclarations(List<ASTModeDeclaration> declarationList) {
    this.declarations = declarationList;
  }

  /**
   * @return true, if this mode is marked initial in one of the components automata
   */
  public boolean isInitial(ComponentTypeSymbol component){
    Preconditions.checkNotNull(component);
    return BasicModeAutomataMill.getModeTool()
        .streamAutomata(component.getAstNode())
        .map(ASTModeAutomaton::getInitialMode)
        .map(ASTInitialModeDeclarationTOP::getMode)
        .anyMatch(getName()::equals);
  }

  /**
   * @return the dynamic elements that are active in this mode, additionally to the static ones
   */
  public ASTComponentBody getBody() {
    return body;
  }

  public List<ASTModeDeclaration> getDeclarations(){
    return declarations;
  }

  public Stream<ASTModeDeclaration> streamDeclarations(){
    return getDeclarations().stream();
  }

  /**
   * all subcomponents that are active in a mode, additionally to or including the ones that are permanently (static) active in the component
   * @param component if <code>null</code>, only dynamic subcomponents are returned, otherwise the subcomponents of the component are included
   * @return a list of subcomponents
   */
  public List<ComponentInstanceSymbol> getSubComponents(ComponentTypeSymbol component){
    return Stream.concat(
            Stream.of(component).filter(Objects::nonNull).map(ComponentTypeSymbol::getSubComponents),
            streamDeclarations()
                .map(ASTModeDeclaration::getSpannedScope)
                .map(IArcBasisScope::getLocalComponentInstanceSymbols)
        ).collect(ArrayList::new, ArrayList::addAll, ArrayList::addAll);
  }

  /**
   * @param component if given, this returns a 150%-solution, if <code>null</code> a delta
   * @return ports that are active in this mode
   */
  public List<PortSymbol> getPorts(ComponentTypeSymbol component){
    return Stream.concat(
        Stream.of(component).filter(Objects::nonNull).map(ComponentTypeSymbol::getPorts),
        streamDeclarations().map(ASTModeDeclaration::getSpannedScope).map(IArcBasisScopeTOP::getLocalPortSymbols)
      )
        .flatMap(Collection::stream)
        .collect(Collectors.toList());
  }

  /**
   * @param component the component whose static connectors should be added to the listing, or <code>null</code> if a delta should be returned
   * @return all connectors this mode adds to the component dynamically, possibly including the static ones
   */
  public List<ASTConnector> getConnectors(ComponentTypeSymbol component){
    return Stream.concat(
            Stream.of(component).filter(Objects::nonNull).map(ComponentTypeSymbol::getAstNode).map(ASTComponentType::getBody),
            Stream.of(getBody())
        )
        .flatMap(ASTComponentBody::streamArcElements)
        .filter(e -> e instanceof ASTConnector)
        .map(e -> (ASTConnector) e)
        .collect(Collectors.toList());
  }
}
