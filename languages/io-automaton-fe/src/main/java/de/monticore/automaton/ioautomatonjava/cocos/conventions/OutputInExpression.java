package de.monticore.automaton.ioautomatonjava.cocos.conventions;

import java.util.Optional;

import de.monticore.automaton.ioautomaton._symboltable.VariableSymbol;
import de.monticore.automaton.ioautomaton._symboltable.VariableSymbol.Direction;
import de.monticore.java.javadsl._ast.ASTPrimaryExpression;
import de.monticore.java.javadsl._cocos.JavaDSLASTPrimaryExpressionCoCo;
import de.monticore.symboltable.Scope;
import de.se_rwth.commons.logging.Log;

public class OutputInExpression implements JavaDSLASTPrimaryExpressionCoCo {

  @Override
  public void check(ASTPrimaryExpression node) {
    if (node.nameIsPresent()) {
      Scope scope = node.getEnclosingScope().get();
      Optional<VariableSymbol> found = scope.resolve(node.getName().get(), VariableSymbol.KIND);
      if (found.isPresent() && found.get().getDirection() == Direction.Output) {
        Log.error("0xAA1A0 Field " + found.get().getName() + " is an Ouput and not allowed in Expressions.", node.get_SourcePositionStart());
      }
    }
  }
  
}
