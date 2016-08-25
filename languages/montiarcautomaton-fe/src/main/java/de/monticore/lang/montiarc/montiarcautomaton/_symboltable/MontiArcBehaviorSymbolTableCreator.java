package de.monticore.lang.montiarc.montiarcautomaton._symboltable;

import java.util.Deque;

import de.monticore.automaton.ioautomaton._symboltable.AutomatonSymbol;
import de.monticore.lang.montiarc.montiarcbehavior._ast.ASTBehaviorImplementation;
import de.monticore.lang.montiarc.montiarcbehavior._ast.ASTMontiArcBehaviorNode;
import de.monticore.lang.montiarc.montiarcbehavior._visitor.CommonMontiArcBehaviorDelegatorVisitor;
import de.monticore.lang.montiarc.montiarcbehavior._visitor.MontiArcBehaviorVisitor;
import de.monticore.symboltable.MutableScope;
import de.monticore.symboltable.ResolverConfiguration;

public class MontiArcBehaviorSymbolTableCreator extends de.monticore.symboltable.CommonSymbolTableCreator implements MontiArcBehaviorVisitor {
  private final MontiArcBehaviorVisitor visitor = new CommonMontiArcBehaviorDelegatorVisitor();
  
  public MontiArcBehaviorSymbolTableCreator(ResolverConfiguration resolverConfig, Deque<MutableScope> scopeStack) {
    super(resolverConfig, scopeStack);
  }
  
  private MontiArcBehaviorVisitor realThis = this;
  
  @Override
  public MontiArcBehaviorVisitor getRealThis() {
    return realThis;
  }
  
  @Override
  public void setRealThis(MontiArcBehaviorVisitor realThis) {
    if (this.realThis != realThis) {
      this.realThis = realThis;
      visitor.setRealThis(realThis);
    }
  }
  
  private String automatonName;
  
  @Override
  public void visit(ASTBehaviorImplementation node) {
    automatonName = node.getName();
  }
  
  @Override
  public void endVisit(ASTBehaviorImplementation node) {
    automatonName = null;
    setEnclosingScopeOfNodes(node);
  }
  
  @Override
  public void visit(ASTMontiArcBehaviorNode node) {
    AutomatonSymbol automaton = new AutomatonSymbol(automatonName);
    addToScopeAndLinkWithNode(automaton, node); // introduces new scope
  }
  
  @Override
  public void endVisit(ASTMontiArcBehaviorNode node) {    
    removeCurrentScope();
  }
}
