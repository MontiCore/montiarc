/* (c) https://github.com/MontiCore/monticore */
package basicmodeautomata.util;

import arcbasis._ast.ASTComponentBody;
import arcbasis._ast.ASTComponentBodyBuilder;
import arcbasis._ast.ASTComponentType;
import basicmodeautomata._ast.ASTModeAutomaton;
import basicmodeautomata._ast.ASTModeDeclaration;
import basicmodeautomata._symboltable.ModeSymbol;
import com.google.common.base.Preconditions;
import de.monticore.scbasis._symboltable.ISCBasisScope;

import java.util.*;
import java.util.stream.Stream;

/**
 * Helper-class that contains utility-methods for
 * finding mode-automaton-related elements of arc-basis-ast-nodes
 */
public class ComponentModeTool  {

  /**
   * finds mode-elements in a component.
   * The returned Stream does not contain <code>null</code>-elements
   * @param component component type
   * @return all definition fragments that contain dynamic structure elements
   */
  public Stream<ASTModeDeclaration> streamDeclarations(ASTComponentType component){
    Preconditions.checkNotNull(component);
    return component.getBody().streamArcElements()
        .filter(x -> x instanceof ASTModeDeclaration)
        .map(x -> (ASTModeDeclaration) x);
  }

  /**
   * finds mode-elements in a component.
   * The returned Stream does not contain <code>null</code>-elements
   * @param component component type
   * @return all mode-automata of that component
   */
  public Stream<ASTModeAutomaton> streamAutomata(ASTComponentType component){
    Preconditions.checkNotNull(component);
    return component.getBody().streamArcElements()
        .filter(x -> x instanceof ASTModeAutomaton)
        .map(x -> (ASTModeAutomaton) x);
  }

  /**
   * finds mode-elements in a component.
   * The returned Stream does not contain <code>null</code>-elements
   * @param component component type
   * @return all modes' names
   */
  public Stream<ASTModeAutomaton> streamModeNames(ASTComponentType component){
    Preconditions.checkNotNull(component);
    return component.getBody().streamArcElements()
        .filter(x -> x instanceof ASTModeAutomaton)
        .map(x -> (ASTModeAutomaton) x);
  }

  /**
   * @return all modes that exist in the component's mode automata (/automaton)
   */
  public Stream<ModeSymbol> streamModes(ASTComponentType component){
    return streamAutomata(component)
        .limit(1)
        .map(ASTModeAutomaton::getEnclosingScope)
        .map(ISCBasisScope::getLocalSCStateSymbols)
        .flatMap(Collection::stream)
        .filter(state -> state instanceof ModeSymbol)
        .map(state -> (ModeSymbol) state);
  }

  /**
   * joins component bodies together
   * @param list a list with some component bodies
   * @return a new AST node, that contains all elements that are also contained in the given nodes
   */
  public ASTComponentBody mergeBodies(List<ASTComponentBody> list) {
    Preconditions.checkNotNull(list);
    return list.stream().collect(ASTComponentBodyBuilder::new,
        (builder, body) ->
            builder.addAll_PostComments(body.get_PostCommentList())
                .addAll_PreComments(body.get_PreCommentList())
                .addAllArcElements(body.getArcElementList()),
        (builder, other) ->
            builder.addAll_PostComments(other.get_PostCommentList())
                .addAll_PreComments(other.get_PreCommentList())
                .addAllArcElements(other.getArcElementList())
    ).build();
  }
}