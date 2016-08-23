package de.monticore.lang.montiarc.montiarcautomaton._symboltable;

import java.util.Deque;

import de.monticore.lang.montiarc.montiarcbehavior._ast.ASTBehaviorImplementation;
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
  
  private String name;
  
  @Override
  public void visit(ASTBehaviorImplementation node) {
    name = node.getName();
  }
  
  @Override
  public void endVisit(ASTBehaviorImplementation node) {
    name = null;
    setEnclosingScopeOfNodes(node);
  }
  
  public String getName() {
    return name;
  }
}
