/* (c) https://github.com/MontiCore/monticore */
package arcautomaton._ast;

import de.monticore.scbasis._ast.ASTSCState;
import de.monticore.scbasis._ast.ASTSCTransition;

import java.util.stream.Stream;

public class ASTArcStatechart extends ASTArcStatechartTOP {

  /**
   * @return all transitions that occur in this statechart, probably in the order in which they are given
   */
  public Stream<ASTSCTransition> streamTransitions() {
    return streamSCStatechartElements()
      .filter(e -> e instanceof ASTSCTransition)
      .map(t -> (ASTSCTransition) t);
  }

  /**
   * @return all nodes that define init-actions of this statechart
   */
  public Stream<ASTInitialOutputDeclaration> streamInitialOutput() {
    return streamSCStatechartElements()
      .filter(e -> e instanceof ASTInitialOutputDeclaration)
      .map(o -> (ASTInitialOutputDeclaration) o);
  }

  /**
   * @return all states that are contained in this statechart
   */
  public Stream<ASTSCState> streamStates() {
    return streamSCStatechartElements()
      .filter(e -> e instanceof ASTSCState)
      .map(s -> (ASTSCState) s);
  }

  /**
   * @return a stream containing all initial states of this statechart, regardless of how they are defined as initial
   */
  public Stream<ASTSCState> streamInitialStates() {
    return Stream.concat(
      streamStates().filter(s -> s.getSCModifier().isInitial()),
      streamInitialOutput().filter(ASTInitialOutputDeclaration::isPresentNameDefinition)
        .map(ASTInitialOutputDeclaration::getNameDefinition))
      .distinct();
  }
}