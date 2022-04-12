/* (c) https://github.com/MontiCore/monticore */
package basicmodeautomata._symboltable;

import arcbasis._ast.ASTComponentBody;
import arcbasis._ast.ASTComponentType;
import basicmodeautomata.BasicModeAutomataMill;
import basicmodeautomata._ast.ASTModeAutomaton;
import basicmodeautomata._ast.ASTModeDeclaration;
import com.google.common.base.Preconditions;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Stream;

public class BasicModeAutomataScopesGenitor extends BasicModeAutomataScopesGenitorTOP {

  @Override
  public void endVisit(ASTModeAutomaton automaton) {
    Preconditions.checkNotNull(automaton);
    Preconditions.checkNotNull(automaton.getEnclosingScope());
    if(automaton.getEnclosingScope().getAstNode() instanceof ASTModeDeclaration){
      return;  // this is handled by a coco, which produces a nicer error-message
    }
    Preconditions.checkArgument(automaton.getEnclosingScope().getAstNode() instanceof ASTComponentType,
        "Cannot handle automata that are not owned by component-types");
    ASTComponentType component = (ASTComponentType) automaton.getEnclosingScope().getAstNode();
    Preconditions.checkArgument(component.isPresentSymbol());

    computeModes(component).forEach(automaton.getEnclosingScope()::add);
  }

  /**
   * Creates mode-symbols, each of which contains its relevant bodies.
   * The created modes do not have an enclosing scope, which has to be set externally.
   * @param component a component that contains a mode-automaton
   * @return a stream with all modes of the component or an empty stream if the component is not dynamic
   */
  protected Stream<ModeSymbol> computeModes(ASTComponentType component) {
    Map<String, List<ASTModeDeclaration>> map = new HashMap<>();

    BasicModeAutomataMill.getModeTool().streamDeclarations(Preconditions.checkNotNull(component)).forEach(declaration ->
        declaration.getNameList().forEach(name ->
            map.computeIfAbsent(name, n -> new ArrayList<>()).add(declaration)
        )
    );

    return map.keySet().stream().map(modeName -> {
      ModeSymbolBuilder builder = new ModeSymbolBuilder();
      builder.setDeclarationList(map.get(modeName));
      builder.setName(modeName);
      builder.setPackageName(component.getSymbol().getFullName());
      return builder.build();
    });
  }
}