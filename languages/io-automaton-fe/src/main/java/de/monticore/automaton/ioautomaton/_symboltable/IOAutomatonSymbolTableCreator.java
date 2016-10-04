package de.monticore.automaton.ioautomaton._symboltable;

import java.util.ArrayList;
import java.util.Deque;
import java.util.List;

import de.monticore.automaton.ioautomaton._ast.ASTAutomaton;
import de.monticore.automaton.ioautomaton._ast.ASTAutomatonBody;
import de.monticore.automaton.ioautomaton._ast.ASTIOAssignment;
import de.monticore.automaton.ioautomaton._ast.ASTInitialStateDeclaration;
import de.monticore.automaton.ioautomaton._ast.ASTInputDeclaration;
import de.monticore.automaton.ioautomaton._ast.ASTOutputDeclaration;
import de.monticore.automaton.ioautomaton._ast.ASTState;
import de.monticore.automaton.ioautomaton._ast.ASTTransition;
import de.monticore.automaton.ioautomaton._ast.ASTValuationExt;
import de.monticore.automaton.ioautomaton._ast.ASTVariable;
import de.monticore.automaton.ioautomaton._ast.ASTVariableDeclaration;
import de.monticore.automaton.ioautomaton._symboltable.VariableSymbol.Direction;
import de.monticore.automaton.ioautomaton._visitor.CommonIOAutomatonDelegatorVisitor;
import de.monticore.automaton.ioautomaton._visitor.IOAutomatonDelegatorVisitor;
import de.monticore.automaton.ioautomaton._visitor.IOAutomatonVisitor;
import de.monticore.automaton.ioautomatonjava._symboltable.AssignmentNameCompleter;
import de.monticore.common.common._ast.ASTStereoValue;
import de.monticore.java.symboltable.JavaTypeSymbolReference;
import de.monticore.symboltable.ImportStatement;
import de.monticore.symboltable.MutableScope;
import de.monticore.symboltable.ResolverConfiguration;
import de.monticore.symboltable.Scope;
import de.monticore.symboltable.types.JTypeSymbol;
import de.monticore.symboltable.types.references.JTypeReference;
import de.monticore.types.JTypeSymbolsHelper;
import de.monticore.types.TypesHelper;
import de.monticore.types.TypesPrinter;
import de.monticore.types.types._ast.ASTType;
import de.se_rwth.commons.logging.Log;

public class IOAutomatonSymbolTableCreator extends de.monticore.symboltable.CommonSymbolTableCreator implements IOAutomatonVisitor {
  
  private List<ImportStatement> currentImports = new ArrayList<>();
  
  // TODO doc
  private final IOAutomatonDelegatorVisitor visitor = new CommonIOAutomatonDelegatorVisitor();
  
  public IOAutomatonSymbolTableCreator(final ResolverConfiguration resolverConfig, final MutableScope enclosingScope) {
    super(resolverConfig, enclosingScope);
    initSuperSTC(resolverConfig);
  }
  
  public IOAutomatonSymbolTableCreator(final ResolverConfiguration resolverConfig, final Deque<MutableScope> scopeStack) {
    super(resolverConfig, scopeStack);
    initSuperSTC(resolverConfig);
  }
  
  private void initSuperSTC(ResolverConfiguration resolverConfig) {
    // TODO doc
//    visitor.set_de_monticore_automaton_ioautomaton__visitor_IOAutomatonVisitor(IOAutomatonSymbolTableCreator(resolverConfig, scopeStack));
//    visitor.set_de_monticore_java_javadsl__visitor_JavaDSLVisitor(new JavaSymbolTableCreatorFix(resolverConfig, scopeStack));
//    visitor.set_de_monticore_automaton_ioautomatonjava__visitor_IOAutomatonJavaVisitor(this);
//    visitor.set_de_monticore_automaton_ioautomaton__visitor_IOAutomatonVisitor(this);
//    visitor.set_de_monticore_common_common__visitor_CommonVisitor(new CommonSymbolTableCreator(resolverConfig, scopeStack));
  }
  
  /**
   * Creates the symbol table starting from the <code>rootNode</code> and
   * returns the first scope that was created.
   *
   * @param rootNode the root node
   * @return the first scope that was created
   */
  public Scope createFromAST(de.monticore.automaton.ioautomaton._ast.ASTAutomaton rootNode) {
    Log.errorIfNull(rootNode, "0xA7004_904 Error by creating of the IOAutomatonSymbolTableCreatorTOP symbol table: top ast node is null");
    rootNode.accept(realThis);
    return getFirstCreatedScope();
  }
  
  @Override
  public MutableScope getFirstCreatedScope() {
    return super.getFirstCreatedScope();
  }
  
 private IOAutomatonVisitor realThis = this;
  
  @Override
  public IOAutomatonVisitor getRealThis() {
    return realThis;
  }
  
  @Override
  public void setRealThis(IOAutomatonVisitor realThis) {
    if (this.realThis != realThis) {
      this.realThis = realThis;
      visitor.setRealThis(realThis);
    }
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
  public void visit(ASTAutomatonBody node) {
    // needed here, else setEnclosingScopeOfNodes will not work
    node.setEnclosingScope(currentScope().get());
  }
  
  @Override
  public void endVisit(ASTAutomatonBody node) {
    setEnclosingScopeOfNodes(node);
    // automaton core loaded & all enclosing scopes set, so we can reconstruct the missing assignment names
    node.accept(new AssignmentNameCompleter(currentScope().get()));
  }
  
  @Override
  public void visit(ASTVariable node) {
    MutableScope scope = currentScope().get();
    scope.<VariableSymbol> resolveMany(node.getName(), VariableSymbol.KIND).forEach(c -> c.setInitializationAST(node.getValuation()));
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
      scope.<StateSymbol> resolveMany(name, StateSymbol.KIND).forEach(c -> {c.setInitial(true); c.setInitialReactionAST(node.getBlock());});
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
    
    transition.setGuardAST(node.getGuard());
    transition.setReactionAST(node.getReaction());
    transition.setStimulusAST(node.getStimulus());
    
    addToScopeAndLinkWithNode(transition, node); // introduces new scope
  }
  
  @Override
  public void endVisit(ASTTransition node) {
    removeCurrentScope();
  }
  
  @Override
  public void visit(ASTVariableDeclaration node) {
    JTypeReference<? extends JTypeSymbol> typeRef = getTyperef(node.getType());
    for (ASTVariable astVar : node.getVariables()) {
      VariableSymbol varSymbol = new VariableSymbol(astVar.getName());
      varSymbol.setTypeReference(typeRef);
      varSymbol.setDirection(Direction.Variable);
      addToScopeAndLinkWithNode(varSymbol, astVar);
    }
  }
  
  @Override
  public void visit(ASTInputDeclaration node) {
    JTypeReference<? extends JTypeSymbol> type = getTyperef(node.getType());
    for (ASTVariable astVar : node.getVariables()) {
      VariableSymbol varSymbol = new VariableSymbol(astVar.getName());
      varSymbol.setTypeReference(type);
      varSymbol.setDirection(Direction.Input);
      addToScopeAndLinkWithNode(varSymbol, astVar);
    }
  }
  
  @Override
  public void visit(ASTOutputDeclaration node) {
    JTypeReference<? extends JTypeSymbol> type = getTyperef(node.getType());
    for (ASTVariable astVar : node.getVariables()) {
      VariableSymbol varSymbol = new VariableSymbol(astVar.getName());
      varSymbol.setTypeReference(type);
      varSymbol.setDirection(Direction.Output);
      addToScopeAndLinkWithNode(varSymbol, astVar);
    }
  }
  
  @Override
  public void visit(ASTIOAssignment node) {
    node.setEnclosingScope(currentScope().get());
  }

  @Override
  public void visit(ASTValuationExt node) {
    node.setEnclosingScope(currentScope().get());
  }
  
  private JavaTypeSymbolReference getTyperef(ASTType astType) {
    String typeName = TypesPrinter.printTypeWithoutTypeArgumentsAndDimension(astType);
//    JTypeReference<JTypeSymbol> typeRef = new CommonJTypeReference<JTypeSymbol>(typeName, JTypeSymbol.KIND, currentScope().get());
    JavaTypeSymbolReference typeRef = new JavaTypeSymbolReference(typeName, currentScope().get(), 0);
    typeRef.setDimension(TypesHelper.getArrayDimensionIfArrayOrZero(astType));
    
    addTypeArgumentsToTypeSymbol(typeRef, astType);
    
    return typeRef;
  }
  
  private void addTypeArgumentsToTypeSymbol(JTypeReference<? extends JTypeSymbol> typeRef, ASTType astType) {
    JTypeSymbolsHelper.addTypeArgumentsToTypeSymbol(typeRef, astType, currentScope().get(), new JTypeSymbolsHelper.CommonJTypeReferenceFactory());
  }
}
