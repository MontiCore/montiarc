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
import de.monticore.automaton.ioautomaton._symboltable.StateSymbol;
import de.monticore.automaton.ioautomaton._symboltable.StateSymbolReference;
import de.monticore.automaton.ioautomaton._symboltable.TransitionSymbol;
import de.monticore.automaton.ioautomaton._symboltable.VariableSymbol;
import de.monticore.automaton.ioautomaton._symboltable.VariableSymbol.Direction;
import de.monticore.automaton.ioautomaton._visitor.CommonIOAutomatonDelegatorVisitor;
import de.monticore.automaton.ioautomaton._visitor.IOAutomatonDelegatorVisitor;
import de.monticore.automaton.ioautomaton._visitor.IOAutomatonVisitor;
import de.monticore.automaton.ioautomatonjava._ast.ASTIOACompilationUnit;
import de.monticore.automaton.ioautomatonjava._visitor.IOAutomatonJavaVisitor;
import de.monticore.common.common._ast.ASTStereoValue;
import de.monticore.java.javadsl._ast.ASTExpression;
import de.monticore.java.javadsl._ast.ASTPrimaryExpression;
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
  private final IOAutomatonDelegatorVisitor visitor = new CommonIOAutomatonDelegatorVisitor();
  
  public IOAutomatonJavaSymbolTableCreator(final ResolverConfiguration resolverConfig, final MutableScope enclosingScope) {
    super(resolverConfig, enclosingScope);
    initSuperSTC();
  }
  
  public IOAutomatonJavaSymbolTableCreator(final ResolverConfiguration resolverConfig, final Deque<MutableScope> scopeStack) {
    super(resolverConfig, scopeStack);
    initSuperSTC();
  }
  
  private void initSuperSTC() {
    // TODO doc
    // visitor.set_de_monticore_automaton_ioautomaton__visitor_IOAutomatonVisitor(de.monticore.automaton.ioautomaton._visitor.IOAutomatonSymbolTableCreator(resolverConfig,
    // scopeStack));
    // visitor.set_de_monticore_common_common__visitor_CommonVisitor(de.monticore.common.common._visitor.CommonSymbolTableCreator(resolverConfig,
    // scopeStack));
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
    rootNode.accept(this);
    return getFirstCreatedScope();
  }
  
  private IOAutomatonJavaVisitor realThis = this;
  
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
    JavaHelper.addJavaDefaultImports(imports);
    ArtifactScope artifactScope = new ArtifactScope(Optional.of(currentScope().get()), compilationUnitPackage, imports);
    this.currentImports = imports;
    putOnStack(artifactScope); // introduces new scope
  }
  
  @Override
  public void endVisit(ASTIOACompilationUnit node) {
    removeCurrentScope();
  }
  
  @Override
  public void visit(ASTAutomaton node) {
    AutomatonSymbol automaton = new AutomatonSymbol(node.getName());
    automaton.setImports(currentImports);
    if (node.getStereotype().isPresent()) {
      for (ASTStereoValue value : node.getStereotype().get().getValues()) {
        automaton.addStereoValue(value.getName());
      }
    }
    addToScopeAndLinkWithNode(automaton, node); // introduces new scope
  }
  
  @Override
  public void endVisit(ASTAutomaton node) {
    removeCurrentScope();
  }
  
  @Override
  public void visit(ASTVariable node) {
  }
  
  @Override
  public void visit(ASTState node) {
    StateSymbol state = new StateSymbol(node.getName());
    if (node.getStereotype().isPresent()) {
      for (ASTStereoValue value : node.getStereotype().get().getValues()) {
        state.addStereoValue(value.getName());
      }
    }
    addToScopeAndLinkWithNode(state, node);
  }
  
  @Override
  public void visit(ASTInitialStateDeclaration node) {
    MutableScope scope = currentScope().get();
    for (String name : node.getNames()) {
      scope.<StateSymbol> resolve(name, StateSymbol.KIND).ifPresent(c -> c.setInitial(true));
    }
  }
  
  @Override
  public void visit(ASTTransition node) {
    // get target name, if there is no get source name (loop to itself)
    String targetName = node.getTarget().orElse(node.getSource());
    
    StateSymbolReference source = new StateSymbolReference(node.getSource(), currentScope().get());
    StateSymbolReference target = new StateSymbolReference(targetName, currentScope().get());
    
    TransitionSymbol transition = new TransitionSymbol(node.getSource() + " -> " + targetName);
    transition.setSource(source);
    transition.setTarget(target);
    
    transition.setGuard(node.getGuard().isPresent());
    transition.setReaction(node.getReaction().isPresent());
    transition.setStimulus(node.getStimulus().isPresent());
    
    addToScopeAndLinkWithNode(transition, node); // introduces new scope
  }
  
  @Override
  public void endVisit(ASTTransition node) {
    removeCurrentScope();
  }
  
  @Override
  public void visit(ASTVariableDeclaration node) {
    JTypeReference<JTypeSymbol> typeRef = getTyperef(node.getType());
    for (ASTVariable astVar : node.getVariables()) {
      VariableSymbol varSymbol = new VariableSymbol(astVar.getName());
      varSymbol.setTypeReference(typeRef);
      varSymbol.setDirection(Direction.Variable);
      addToScopeAndLinkWithNode(varSymbol, astVar);
    }
  }
  
  @Override
  public void visit(ASTInputDeclaration node) {
    JTypeReference<JTypeSymbol> type = getTyperef(node.getType());
    for (ASTVariable astVar : node.getVariables()) {
      VariableSymbol varSymbol = new VariableSymbol(astVar.getName());
      varSymbol.setTypeReference(type);
      varSymbol.setDirection(Direction.Input);
      addToScopeAndLinkWithNode(varSymbol, astVar);
    }
  }
  
  @Override
  public void visit(ASTOutputDeclaration node) {
    JTypeReference<JTypeSymbol> type = getTyperef(node.getType());
    for (ASTVariable astVar : node.getVariables()) {
      VariableSymbol varSymbol = new VariableSymbol(astVar.getName());
      varSymbol.setTypeReference(type);
      varSymbol.setDirection(Direction.Output);
      addToScopeAndLinkWithNode(varSymbol, astVar);
    }
  }
  
  @Override
  public void visit(ASTPrimaryExpression node) {
    node.setEnclosingScope(currentScope().get());
  }
  
  @Override
  public void visit(ASTBooleanLiteral node) {
    node.setEnclosingScope(currentScope().get());
  }
  
  @Override
  public void visit(ASTIOAssignment node) {
    node.setEnclosingScope(currentScope().get());
  }
  
  private JTypeReference<JTypeSymbol> getTyperef(ASTType astType) {
    String typeName = TypesPrinter.printTypeWithoutTypeArgumentsAndDimension(astType);
    JTypeReference<JTypeSymbol> typeRef = new CommonJTypeReference<JTypeSymbol>(typeName, JTypeSymbol.KIND, currentScope().get());
    typeRef.setDimension(TypesHelper.getArrayDimensionIfArrayOrZero(astType));
    
    addTypeArgumentsToTypeSymbol(typeRef, astType);
    
    return typeRef;
  }
  
  private void addTypeArgumentsToTypeSymbol(JTypeReference<? extends JTypeSymbol> typeRef, ASTType astType) {
    JTypeSymbolsHelper.addTypeArgumentsToTypeSymbol(typeRef, astType, currentScope().get(), new JTypeSymbolsHelper.CommonJTypeReferenceFactory());
  }
}
