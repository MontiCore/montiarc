/* (c) https://github.com/MontiCore/monticore */
package dynamicmontiarc._symboltable;

import de.monticore.symboltable.MutableScope;
import de.monticore.symboltable.ResolvingConfiguration;
import dynamicmontiarc._ast.ASTModeDeclaration;
import dynamicmontiarc._ast.ASTModeAutomaton;
import dynamicmontiarc._ast.ASTModeTransition;
import dynamicmontiarc._visitor.DynamicMontiArcVisitor;
import montiarc._symboltable.MontiArcSymbolTableCreator;

import java.util.Deque;

/**
 * Symboltable creator for the Dynamic MontiArc language.
 *
 */
public class DynamicMontiArcSymbolTableCreator extends MontiArcSymbolTableCreator implements DynamicMontiArcVisitor {

  private DynamicMontiArcVisitor realThis = this;

  public DynamicMontiArcSymbolTableCreator(ResolvingConfiguration resolverConfig, MutableScope enclosingScope) {
    super(resolverConfig, enclosingScope);
  }

  public DynamicMontiArcSymbolTableCreator(ResolvingConfiguration resolverConfig, Deque<MutableScope> scopeStack) {
    super(resolverConfig, scopeStack);
  }

  @Override
  public void setRealThis(DynamicMontiArcVisitor realThis) {
    this.realThis = realThis;
  }

  @Override
  public DynamicMontiArcVisitor getRealThis() {
    return realThis;
  }

  @Override
  public void visit(ASTModeDeclaration node){
    node.setEnclosingScope(scopeStack.peek());
  }

  @Override
  public void visit(ASTModeAutomaton node){
    createScope(node);
  }

  @Override
  public void endVisit(ASTModeAutomaton node) {
    removeCurrentScope();
  }

  @Override
  public void visit(ASTModeTransition node){
    final String symbolName = node.getSource() + " -M-> " + node.getTarget();

    final String sourceName = node.getSource();
    final String targetName = node.getTarget();

    final ModeTransitionSymbol transitionSymbol =
        new ModeTransitionSymbol(symbolName);

    addToScopeAndLinkWithNode(transitionSymbol, node);
  }

  @Override
  public void endVisit(ASTModeTransition node){
    setEnclosingScopeOfNodes(node);
    removeCurrentScope();
  }
}
