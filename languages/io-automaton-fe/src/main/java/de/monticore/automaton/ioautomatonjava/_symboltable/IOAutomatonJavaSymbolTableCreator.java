package de.monticore.automaton.ioautomatonjava._symboltable;

import static java.util.Objects.requireNonNull;

import java.util.ArrayList;
import java.util.Deque;
import java.util.List;
import java.util.Optional;

import de.monticore.ast.ASTNode;
import de.monticore.automaton.ioautomaton._ast.ASTAlternative;
import de.monticore.automaton.ioautomaton._ast.ASTAutomaton;
import de.monticore.automaton.ioautomaton._ast.ASTAutomatonContent;
import de.monticore.automaton.ioautomaton._ast.ASTIOAssignment;
import de.monticore.automaton.ioautomaton._ast.ASTInitialStateDeclaration;
import de.monticore.automaton.ioautomaton._ast.ASTInputDeclaration;
import de.monticore.automaton.ioautomaton._ast.ASTOutputDeclaration;
import de.monticore.automaton.ioautomaton._ast.ASTState;
import de.monticore.automaton.ioautomaton._ast.ASTTransition;
import de.monticore.automaton.ioautomaton._ast.ASTVariable;
import de.monticore.automaton.ioautomaton._ast.ASTVariableDeclaration;
import de.monticore.automaton.ioautomaton._symboltable.AutomatonSymbol;
import de.monticore.automaton.ioautomaton._symboltable.IOAutomatonSymbolTableCreator;
import de.monticore.automaton.ioautomaton._symboltable.StateSymbol;
import de.monticore.automaton.ioautomaton._symboltable.StateSymbolReference;
import de.monticore.automaton.ioautomaton._symboltable.TransitionSymbol;
import de.monticore.automaton.ioautomaton._symboltable.VariableSymbol;
import de.monticore.automaton.ioautomaton._symboltable.VariableSymbol.Direction;
import de.monticore.automaton.ioautomaton._visitor.CommonIOAutomatonDelegatorVisitor;
import de.monticore.automaton.ioautomaton._visitor.IOAutomatonDelegatorVisitor;
import de.monticore.automaton.ioautomaton._visitor.IOAutomatonVisitor;
import de.monticore.automaton.ioautomatonjava._ast.ASTIOACompilationUnit;
import de.monticore.automaton.ioautomatonjava._visitor.CommonIOAutomatonJavaDelegatorVisitor;
import de.monticore.automaton.ioautomatonjava._visitor.IOAutomatonJavaDelegatorVisitor;
import de.monticore.automaton.ioautomatonjava._visitor.IOAutomatonJavaVisitor;
import de.monticore.common.common._ast.ASTStereoValue;
import de.monticore.java.javadsl._ast.ASTExpression;
import de.monticore.java.javadsl._ast.ASTPrimaryExpression;
import de.monticore.java.symboltable.JavaSymbolTableCreator;
import de.monticore.literals.literals._ast.ASTBooleanLiteral;
import de.monticore.literals.literals._ast.ASTLiteral;
import de.monticore.symboltable.ArtifactScope;
import de.monticore.symboltable.ImportStatement;
import de.monticore.symboltable.MutableScope;
import de.monticore.symboltable.ResolverConfiguration;
import de.monticore.symboltable.Scope;
import de.monticore.symboltable.Symbols;
import de.monticore.symboltable.types.JTypeSymbol;
import de.monticore.symboltable.types.references.CommonJTypeReference;
import de.monticore.symboltable.types.references.JTypeReference;
import de.monticore.types.JTypeSymbolsHelper;
import de.monticore.types.TypesHelper;
import de.monticore.types.TypesPrinter;
import de.monticore.types.types._ast.ASTImportStatement;
import de.monticore.types.types._ast.ASTType;
import de.se_rwth.commons.Names;
import de.se_rwth.commons.logging.Log;

public class IOAutomatonJavaSymbolTableCreator extends de.monticore.symboltable.CommonSymbolTableCreator implements IOAutomatonJavaVisitor {
  
  private List<ImportStatement> currentImports = new ArrayList<>();
  
  // TODO doc
  private final IOAutomatonJavaDelegatorVisitor visitor = new CommonIOAutomatonJavaDelegatorVisitor();
  
  public IOAutomatonJavaSymbolTableCreator(final ResolverConfiguration resolverConfig, final MutableScope enclosingScope) {
    super(resolverConfig, enclosingScope);
    initSuperSTC(resolverConfig);
  }
  
  public IOAutomatonJavaSymbolTableCreator(final ResolverConfiguration resolverConfig, final Deque<MutableScope> scopeStack) {
    super(resolverConfig, scopeStack);
    initSuperSTC(resolverConfig);
  }
  
  private void initSuperSTC(ResolverConfiguration resolverConfig) {
    // TODO doc
//    visitor.set_de_monticore_automaton_ioautomaton__visitor_IOAutomatonVisitor(IOAutomatonSymbolTableCreator(resolverConfig, scopeStack));
    visitor.set_de_monticore_java_javadsl__visitor_JavaDSLVisitor(new JavaSymbolTableCreatorFix(resolverConfig, scopeStack));
    visitor.set_de_monticore_automaton_ioautomaton__visitor_IOAutomatonVisitor(new IOAutomatonSymbolTableCreator(resolverConfig, scopeStack));
    visitor.set_de_monticore_automaton_ioautomatonjava__visitor_IOAutomatonJavaVisitor(this);
    visitor.set_de_monticore_literals_literals__visitor_LiteralsVisitor(new LiteralsSymbolTableCreatorFix(resolverConfig, scopeStack));
    visitor.set_de_monticore_types_types__visitor_TypesVisitor(new TypesSymbolTableCreatorFix(resolverConfig, scopeStack));
//    visitor.set_de_monticore_common_common__visitor_CommonVisitor(new CommonSymbolTableCreator(resolverConfig, scopeStack));
  }
  
  /**
   * Creates the symbol table starting from the <code>rootNode</code> and
   * returns the first scope that was created.
   *
   * @param rootNode the root node
   * @return the first scope that was created
   */
  public Scope createFromAST(de.monticore.automaton.ioautomatonjava._ast.ASTIOACompilationUnit rootNode) {
    Log.errorIfNull(rootNode, "0xA7004_904 Error by creating of the IOAutomatonSymbolTableCreatorTOP symbol table: top ast node is null");
    rootNode.accept(realThis);
    return getFirstCreatedScope();
  }
  
  @Override
  public MutableScope getFirstCreatedScope() {
    return super.getFirstCreatedScope();
  }
  
  private IOAutomatonJavaVisitor realThis = visitor;
  
  public IOAutomatonJavaVisitor getRealThis() {
    return realThis;
  }
  
  @Override
  public void setRealThis(IOAutomatonJavaVisitor realThis) {
    if (this.realThis != realThis) {
      this.realThis = realThis;
      visitor.setRealThis(realThis);
    }
  }
  
  @Override
  public void visit(ASTIOACompilationUnit node) {
    Log.debug("Building Symboltable for Automaton: " + node.getAutomaton().getName(), IOAutomatonJavaSymbolTableCreator.class.getSimpleName());
    String compilationUnitPackage = Names.getQualifiedName(node.getPackage());
    
    // imports
    List<ImportStatement> imports = new ArrayList<>();
    for (ASTImportStatement astImportStatement : node.getImportStatements()) {
      String qualifiedImport = Names.getQualifiedName(astImportStatement.getImportList());
      ImportStatement importStatement = new ImportStatement(qualifiedImport, astImportStatement.isStar());
      imports.add(importStatement);
    }
    // add default java imports
//    JavaHelper.addJavaDefaultImports(imports); //not needed here because java default types are already imported in JavaDSL
    imports.add(new ImportStatement("java.lang.String", true)); // string is missing 
    ArtifactScope artifactScope = new ArtifactScope(Optional.of(currentScope().get()), compilationUnitPackage, imports);
    this.currentImports = imports;
    putOnStack(artifactScope); // introduces new scope
  }
  
  @Override
  public void endVisit(ASTIOACompilationUnit node) {
    removeCurrentScope();
  }
  

  
//  @Override
//  public void visit(ASTPrimaryExpression node) {
//    node.setEnclosingScope(currentScope().get());
//  }
//  
//  @Override
//  public void visit(ASTBooleanLiteral node) {
//    node.setEnclosingScope(currentScope().get());
//  }
  
  
}
