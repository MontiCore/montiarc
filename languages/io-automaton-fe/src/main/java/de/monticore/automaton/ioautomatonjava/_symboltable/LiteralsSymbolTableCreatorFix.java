package de.monticore.automaton.ioautomatonjava._symboltable;

import static java.util.Objects.requireNonNull;

import java.util.Deque;

import de.monticore.ast.ASTNode;
import de.monticore.automaton.ioautomatonjava._visitor.IOAutomatonJavaVisitor;
import de.monticore.java.javadsl._ast.ASTJavaDSLNode;
import de.monticore.java.javadsl._visitor.CommonJavaDSLDelegatorVisitor;
import de.monticore.java.javadsl._visitor.JavaDSLDelegatorVisitor;
import de.monticore.java.javadsl._visitor.JavaDSLVisitor;
import de.monticore.java.symboltable.JavaSymbolTableCreator;
import de.monticore.lexicals.lexicals._ast.ASTLexicalsNode;
import de.monticore.literals.literals._ast.ASTBooleanLiteral;
import de.monticore.literals.literals._ast.ASTCharLiteral;
import de.monticore.literals.literals._ast.ASTDoubleLiteral;
import de.monticore.literals.literals._ast.ASTFloatLiteral;
import de.monticore.literals.literals._ast.ASTIntLiteral;
import de.monticore.literals.literals._ast.ASTLiteral;
import de.monticore.literals.literals._ast.ASTLiteralsNode;
import de.monticore.literals.literals._ast.ASTLongLiteral;
import de.monticore.literals.literals._ast.ASTNullLiteral;
import de.monticore.literals.literals._ast.ASTNumericLiteral;
import de.monticore.literals.literals._ast.ASTSignedDoubleLiteral;
import de.monticore.literals.literals._ast.ASTSignedFloatLiteral;
import de.monticore.literals.literals._ast.ASTSignedIntLiteral;
import de.monticore.literals.literals._ast.ASTSignedLiteral;
import de.monticore.literals.literals._ast.ASTSignedLongLiteral;
import de.monticore.literals.literals._ast.ASTSignedNumericLiteral;
import de.monticore.literals.literals._ast.ASTStringLiteral;
import de.monticore.literals.literals._visitor.CommonLiteralsDelegatorVisitor;
import de.monticore.literals.literals._visitor.LiteralsDelegatorVisitor;
import de.monticore.literals.literals._visitor.LiteralsVisitor;
import de.monticore.symboltable.CommonScope;
import de.monticore.symboltable.MutableScope;
import de.monticore.symboltable.ResolverConfiguration;
import de.monticore.symboltable.Scope;

public class LiteralsSymbolTableCreatorFix extends de.monticore.symboltable.CommonSymbolTableCreator implements LiteralsVisitor {
  
  // TODO doc
  private final LiteralsDelegatorVisitor visitor = new CommonLiteralsDelegatorVisitor();
  
  public LiteralsSymbolTableCreatorFix(ResolverConfiguration resolverConfig, MutableScope enclosingScope) {
    super(resolverConfig, enclosingScope);
  }
  
  public LiteralsSymbolTableCreatorFix(ResolverConfiguration resolverConfig, Deque<MutableScope> scopeStack) {
    super(resolverConfig, scopeStack);
  }
  
  public Scope createFromAST(ASTLiteralsNode rootNode) {
    requireNonNull(rootNode);
    rootNode.accept(realThis);
    return getFirstCreatedScope();
  }
  
  private LiteralsVisitor realThis = this;
  
  public LiteralsVisitor getRealThis() {
    return realThis;
  }
  
  @Override
  public void setRealThis(LiteralsVisitor realThis) {
    if (this.realThis != realThis) {
      this.realThis = realThis;
      visitor.setRealThis(realThis);
    }
  }
  
  @Override
  public void visit(ASTBooleanLiteral node) {
    node.setEnclosingScope(currentScope().get());
  }
  
  @Override
  public void visit(ASTIntLiteral node) {
    node.setEnclosingScope(currentScope().get());
  }
  
  @Override
  public void visit(ASTCharLiteral node) {
    node.setEnclosingScope(currentScope().get());
  }
  
  @Override
  public void visit(ASTDoubleLiteral node) {
    node.setEnclosingScope(currentScope().get());
  }
  
  @Override
  public void visit(ASTFloatLiteral node) {
    node.setEnclosingScope(currentScope().get());
  }
  
  @Override
  public void visit(ASTLexicalsNode node) {
    node.setEnclosingScope(currentScope().get());
  }
  
  @Override
  public void visit(ASTLongLiteral node) {
    node.setEnclosingScope(currentScope().get());
  }
  
  @Override
  public void visit(ASTSignedDoubleLiteral node) {
    node.setEnclosingScope(currentScope().get());
  }
  
  @Override
  public void visit(ASTNumericLiteral node) {
    node.setEnclosingScope(currentScope().get());
  }
  
  @Override
  public void visit(ASTSignedFloatLiteral node) {
    node.setEnclosingScope(currentScope().get());
  }
  
  @Override
  public void visit(ASTSignedIntLiteral node) {
    node.setEnclosingScope(currentScope().get());
  }
  
  @Override
  public void visit(ASTSignedLiteral node) {
    node.setEnclosingScope(currentScope().get());
  }
  
  @Override
  public void visit(ASTSignedLongLiteral node) {
    node.setEnclosingScope(currentScope().get());
  }
  
  @Override
  public void visit(ASTStringLiteral node) {
    node.setEnclosingScope(currentScope().get());
  }
  
  @Override
  public void visit(ASTSignedNumericLiteral node) {
    node.setEnclosingScope(currentScope().get());
  }
  
  @Override
  public void visit(ASTNullLiteral node) {
    node.setEnclosingScope(currentScope().get());
  }
  
  @Override
  public void visit(ASTNode node) {
    node.setEnclosingScope(currentScope().get());
  }
  
  @Override
  public void visit(ASTLiteralsNode node) {
    node.setEnclosingScope(currentScope().get());
  }
  
  @Override
  public void visit(ASTLiteral node) {
    node.setEnclosingScope(currentScope().get());
  }
}
