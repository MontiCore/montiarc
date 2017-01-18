package de.monticore.automaton.ioautomaton._symboltable;

import java.util.Optional;

import de.monticore.automaton.ioautomaton._ast.ASTValuationExt;
import de.monticore.automaton.ioautomatonjava._ast.ASTValuation;
import de.monticore.symboltable.types.JTypeSymbol;
import de.monticore.symboltable.types.references.JTypeReference;

public class VariableSymbol extends VariableSymbolTOP {
  public enum Direction {
    Variable,
    Input,
    Output
  }

  private JTypeReference<? extends JTypeSymbol> typeReference;
  private Direction direction;
  private Optional<ASTValuationExt> initialization;
  
  public VariableSymbol(String name) {
    super(name);
    direction = Direction.Variable;
  }
  
  public void setInitializationAST(Optional<ASTValuationExt> valuation) {
    initialization = valuation;
  }
  
  public Optional<ASTValuationExt> getInitializationAST() {
    return initialization;
  }
  
  public JTypeReference<? extends JTypeSymbol> getTypeReference() {
    return this.typeReference;
  }

  public void setTypeReference(JTypeReference<? extends JTypeSymbol> typeReference) {
    this.typeReference = typeReference;
  }
  
  public void setDirection(Direction direction) {
    this.direction = direction;
  }
  
  public Direction getDirection() {
    return direction;
  }
  
//  @Override
//  public String toString() {
//    return direction.name() + " " + typeReference.getName() + " " + getName();
//  }
}
