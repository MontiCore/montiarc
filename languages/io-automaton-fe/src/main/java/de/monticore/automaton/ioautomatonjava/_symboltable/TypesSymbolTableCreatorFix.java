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
import de.monticore.types.types._ast.ASTArrayType;
import de.monticore.types.types._ast.ASTComplexArrayType;
import de.monticore.types.types._ast.ASTComplexReferenceType;
import de.monticore.types.types._ast.ASTImportStatement;
import de.monticore.types.types._ast.ASTQualifiedName;
import de.monticore.types.types._ast.ASTReferenceType;
import de.monticore.types.types._ast.ASTSimpleReferenceType;
import de.monticore.types.types._ast.ASTTypeArgument;
import de.monticore.types.types._ast.ASTTypeArguments;
import de.monticore.types.types._ast.ASTTypeParameters;
import de.monticore.types.types._ast.ASTTypesNode;
import de.monticore.types.types._ast.ASTVoidType;
import de.monticore.types.types._ast.ASTWildcardType;
import de.monticore.types.types._visitor.CommonTypesDelegatorVisitor;
import de.monticore.types.types._visitor.TypesDelegatorVisitor;
import de.monticore.types.types._visitor.TypesVisitor;

public class TypesSymbolTableCreatorFix extends de.monticore.symboltable.CommonSymbolTableCreator implements TypesVisitor {
  
  // TODO doc
  private final TypesDelegatorVisitor visitor = new CommonTypesDelegatorVisitor();
  
  public TypesSymbolTableCreatorFix(ResolverConfiguration resolverConfig, MutableScope enclosingScope) {
    super(resolverConfig, enclosingScope);
  }
  
  public TypesSymbolTableCreatorFix(ResolverConfiguration resolverConfig, Deque<MutableScope> scopeStack) {
    super(resolverConfig, scopeStack);
  }
  
  public Scope createFromAST(ASTTypesNode rootNode) {
    requireNonNull(rootNode);
    rootNode.accept(realThis);
    return getFirstCreatedScope();
  }
  
  private TypesVisitor realThis = this;
  
  public TypesVisitor getRealThis() {
    return realThis;
  }
  
  @Override
  public void setRealThis(TypesVisitor realThis) {
    if (this.realThis != realThis) {
      this.realThis = realThis;
      visitor.setRealThis(realThis);
    }
  }
  
  @Override
  public void visit(ASTArrayType node) {
    node.setEnclosingScope(currentScope().get());
  }
 
  @Override
  public void visit(ASTComplexArrayType node) {
    node.setEnclosingScope(currentScope().get());
  }
  
  @Override
  public void visit(ASTReferenceType node) {
    node.setEnclosingScope(currentScope().get());
  }
  
  @Override
  public void visit(ASTSimpleReferenceType node) {
    node.setEnclosingScope(currentScope().get());
  }
  
  @Override
  public void visit(ASTWildcardType node) {
    node.setEnclosingScope(currentScope().get());
  }
  
  @Override
  public void visit(ASTTypeArgument node) {
    node.setEnclosingScope(currentScope().get());
  }
  
  @Override
  public void visit(ASTVoidType node) {
    node.setEnclosingScope(currentScope().get());
  }
  
  @Override
  public void visit(ASTTypeParameters node) {
    node.setEnclosingScope(currentScope().get());
  }
  
  @Override
  public void visit(ASTTypeArguments node) {
    node.setEnclosingScope(currentScope().get());
  }
  
  @Override
  public void visit(ASTComplexReferenceType node) {
    node.setEnclosingScope(currentScope().get());
  }
  
  @Override
  public void visit(ASTQualifiedName node) {
    node.setEnclosingScope(currentScope().get());
  }
}
