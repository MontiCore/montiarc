package de.monticore.automaton.ioautomatonjava._symboltable;

import java.util.Deque;

import de.monticore.java.javadsl._ast.ASTIdentifierAndTypeArgument;
import de.monticore.java.javadsl._ast.ASTPrimaryExpression;
import de.monticore.java.symboltable.JavaSymbolTableCreator;
import de.monticore.symboltable.MutableScope;
import de.monticore.symboltable.ResolverConfiguration;

public class JavaSymbolTableCreatorFix extends JavaSymbolTableCreator {

  public JavaSymbolTableCreatorFix(ResolverConfiguration resolverConfig, MutableScope enclosingScope) {
    super(resolverConfig, enclosingScope);
  }
  
  public JavaSymbolTableCreatorFix(ResolverConfiguration resolverConfig, Deque<MutableScope> scopeStack) {
    super(resolverConfig, scopeStack);
  }
  
  @Override
  public void visit(ASTPrimaryExpression node) {
    super.visit(node);
    node.setEnclosingScope(currentScope().get());
  }
  
  @Override
  public void visit(ASTIdentifierAndTypeArgument node) {
    super.visit(node);
    node.setEnclosingScope(currentScope().get());
  }
}
