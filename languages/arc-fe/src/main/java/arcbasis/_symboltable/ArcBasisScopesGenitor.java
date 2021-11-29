/* (c) https://github.com/MontiCore/monticore */
package arcbasis._symboltable;

import arcbasis.ArcBasisMill;
import arcbasis._ast.*;
import com.google.common.base.Preconditions;
import de.monticore.symbols.basicsymbols._symboltable.VariableSymbol;
import de.monticore.symbols.basicsymbols._symboltable.VariableSymbolBuilder;
import de.monticore.types.check.SymTypeExpression;
import de.monticore.types.check.SymTypeExpressionFactory;
import de.monticore.types.mcbasictypes._ast.ASTMCType;
import de.monticore.types.prettyprint.MCBasicTypesFullPrettyPrinter;
import org.codehaus.commons.nullanalysis.NotNull;
import org.codehaus.commons.nullanalysis.Nullable;

import java.util.ArrayList;
import java.util.EmptyStackException;
import java.util.Optional;
import java.util.Stack;
import java.util.function.BiFunction;

public class ArcBasisScopesGenitor extends ArcBasisScopesGenitorTOP {

  protected Stack<ComponentTypeSymbol> componentStack;
  protected Stack<IArcBasisScope> enclosingScope4InstancesStack;
  protected ASTPortDirection currentPortDirection;

  public ArcBasisScopesGenitor() {
    super();
    this.componentStack = new Stack<>();
    this.enclosingScope4InstancesStack = new Stack<>();
  }

  protected Stack<ComponentTypeSymbol> getComponentStack() {
    return this.componentStack;
  }

  protected Optional<ComponentTypeSymbol> getCurrentComponent() {
    return Optional.ofNullable(this.getComponentStack().peek());
  }

  protected Optional<ComponentTypeSymbol> removeCurrentComponent() {
    return Optional.ofNullable(this.getComponentStack().pop());
  }

  protected void putOnStack(@Nullable ComponentTypeSymbol symbol) {
    this.getComponentStack().push(symbol);
  }

  protected Optional<ASTPortDirection> getCurrentPortDirection() {
    return Optional.ofNullable(this.currentPortDirection);
  }

  protected void setCurrentPortDirection(@Nullable ASTPortDirection currentPortDirection) {
    this.currentPortDirection = currentPortDirection;
  }

  protected Stack<IArcBasisScope> getEnclosingScope4InstancesStack() {
    return this.enclosingScope4InstancesStack;
  }

  protected Optional<IArcBasisScope> getCurrentEnclosingScope4Instances() {
    try {
      return Optional.ofNullable(this.getEnclosingScope4InstancesStack().peek());
    } catch (EmptyStackException e) {
      return Optional.empty();
    }
  }

  protected Optional<IArcBasisScope> removeCurrentEnclosingScope4Instances() {
    try {
      return Optional.ofNullable(this.getEnclosingScope4InstancesStack().pop());
    } catch (EmptyStackException e) {
      return Optional.empty();
    }
  }

  protected void pushCurrentEnclosingScope4Instances(@Nullable IArcBasisScope scope) {
    Preconditions.checkArgument(scope != null);
    this.getEnclosingScope4InstancesStack().push(scope);
  }

  @Override
  public IArcBasisArtifactScope createFromAST(@NotNull ASTArcElement rootNode)  {
    Preconditions.checkNotNull(rootNode);
    IArcBasisArtifactScope artifactScope = arcbasis.ArcBasisMill.artifactScope();
    artifactScope.setPackageName("");
    artifactScope.setImportsList(new ArrayList<>());
    this.putOnStack(artifactScope);
    rootNode.accept(getTraverser());
    assert this.getCurrentScope().isPresent();
    assert this.getCurrentScope().get().equals(artifactScope);
    this.removeCurrentScope();
    return artifactScope;
  }

  public void addToScope(@NotNull ComponentTypeSymbol symbol) {
    Preconditions.checkArgument(symbol != null);
    Preconditions.checkState(this.getCurrentScope().isPresent());
    this.getCurrentScope().get().add(symbol);
  }

  protected ComponentTypeSymbolBuilder create_ComponentType(@NotNull ASTComponentType ast) {
    ComponentTypeSymbolBuilder builder = ArcBasisMill.componentTypeSymbolBuilder();
    builder.setName(ast.getName());
    IArcBasisScope scope = this.createScope(false);
    builder.setSpannedScope(scope);
    return builder;
  }

  /**
   * if this is an inner component, this method sets the outer component of this one
   * @param symbol component to integrate into the structure
   */
  protected void setOuter(@NotNull ComponentTypeSymbol symbol) {
    if (!componentStack.isEmpty()) {
      symbol.setOuterComponent(componentStack.peek());
    }
  }

  @Override
  public void visit(@NotNull ASTComponentType node) {
    Preconditions.checkArgument(node != null);
    Preconditions.checkState(this.getCurrentScope().isPresent());
    ComponentTypeSymbol symbol = this.create_ComponentType(node).build();
    this.setOuter(symbol);
    node.setSymbol(symbol);
    node.setEnclosingScope(this.getCurrentScope().get());
    node.setSpannedScope(symbol.getSpannedScope());
    symbol.getSpannedScope().setAstNode(node);
    symbol.setAstNode(node);
    symbol.setEnclosingScope(this.getCurrentScope().get());
    this.getCurrentScope().get().add(symbol);
    this.putOnStack(symbol.getSpannedScope());
    this.putOnStack(symbol);
  }

  @Override
  public void endVisit(@NotNull ASTComponentType node) {
    Preconditions.checkArgument(node != null);
    Preconditions.checkState(this.getCurrentComponent().isPresent());
    Preconditions.checkState(this.getCurrentScope().isPresent());
    Preconditions.checkState(this.getCurrentComponent().get().isPresentAstNode());
    Preconditions.checkState(this.getCurrentComponent().get().getAstNode().equals(node));
    this.removeCurrentComponent();
    this.removeCurrentScope();
  }

  @Override
  public void visit(@NotNull ASTComponentHead node) {
    Preconditions.checkArgument(node != null);
    Preconditions.checkState(this.getCurrentScope().isPresent());
    Preconditions.checkState(!this.getComponentStack().isEmpty());
    Preconditions.checkState(this.getCurrentComponent().isPresent());
    node.setEnclosingScope(getCurrentScope().get());
  }

  @Override
  public void visit(@NotNull ASTComponentBody node) {
    Preconditions.checkArgument(node != null);
    Preconditions.checkState(this.getCurrentScope().isPresent());
    node.setEnclosingScope(this.getCurrentScope().get());
    this.pushCurrentEnclosingScope4Instances(this.getCurrentScope().get());
  }

  @Override
  public void endVisit(@NotNull ASTComponentBody node) {
    Preconditions.checkArgument(node != null);
    Preconditions.checkState(this.getCurrentEnclosingScope4Instances().isPresent());
    this.removeCurrentEnclosingScope4Instances();
  }

  protected VariableSymbolBuilder create_ArcParameter(@NotNull ASTArcParameter ast) {
    assert (this.getCurrentScope().isPresent());
    VariableSymbolBuilder builder = ArcBasisMill.variableSymbolBuilder();
    builder.setName(ast.getName());
    return builder;
  }

  @Override
  public void visit(@NotNull ASTArcParameter node) {
    Preconditions.checkArgument(node != null);
    Preconditions.checkState(this.getCurrentScope().isPresent());
    Preconditions.checkState(this.getCurrentComponent().isPresent());
    VariableSymbol symbol = this.create_ArcParameter(node).build();
    node.setSymbol(symbol);
    node.setEnclosingScope(this.getCurrentScope().get());
    symbol.setAstNode(node);
    symbol.setEnclosingScope(this.getCurrentScope().get());
    this.getCurrentScope().get().add(symbol);
    this.getCurrentComponent().get().addParameter(symbol);
  }

  @Override
  public void visit(@NotNull ASTPortDeclaration node) {
    Preconditions.checkArgument(node != null);
    Preconditions.checkState(getCurrentScope().isPresent());
    node.setEnclosingScope(getCurrentScope().get());
    this.setCurrentPortDirection(node.getPortDirection());
  }

  @Override
  public void endVisit(@NotNull ASTPortDeclaration node) {
    Preconditions.checkArgument(node != null);
    Preconditions.checkState(this.getCurrentPortDirection().isPresent());
    Preconditions.checkState(this.getCurrentPortDirection().get().equals(node.getPortDirection()));
    this.setCurrentPortDirection(null);
  }

  protected PortSymbolBuilder create_Port(@NotNull ASTPort ast) {
    Preconditions.checkState(this.getCurrentPortDirection().isPresent());
    Preconditions.checkState(this.getCurrentScope().isPresent());
    PortSymbolBuilder builder = ArcBasisMill.portSymbolBuilder();
    builder.setName(ast.getName());
    builder.setDirection(this.getCurrentPortDirection().get());
    return builder;
  }

  @Override
  public void visit(@NotNull ASTPort node) {
    Preconditions.checkArgument(node != null);
    Preconditions.checkState(getCurrentScope().isPresent());
    Preconditions.checkState(getCurrentPortDirection().isPresent());
    PortSymbol symbol = this.create_Port(node).buildWithoutType();
    node.setSymbol(symbol);
    node.setEnclosingScope(this.getCurrentScope().get());
    symbol.setAstNode(node);
    symbol.setEnclosingScope(this.getCurrentScope().get());
    this.getCurrentScope().get().add(symbol);
  }

  @Override
  public void visit(@NotNull ASTArcFieldDeclaration node) {
    Preconditions.checkArgument(node != null);
    Preconditions.checkState(this.getCurrentScope().isPresent());
    node.setEnclosingScope(this.getCurrentScope().get());
  }

  protected VariableSymbolBuilder create_ArcField(@NotNull ASTArcField ast) {
    Preconditions.checkState(this.getCurrentScope().isPresent());
    VariableSymbolBuilder builder = ArcBasisMill.variableSymbolBuilder();
    builder.setName(ast.getName());
    return builder;
  }

  @Override
  public void visit(@NotNull ASTArcField node) {
    Preconditions.checkArgument(node != null);
    Preconditions.checkState(this.getCurrentScope().isPresent());
    VariableSymbol symbol = this.create_ArcField(node).build();
    node.setSymbol(symbol);
    node.setEnclosingScope(this.getCurrentScope().get());
    symbol.setAstNode(node);
    symbol.setEnclosingScope(this.getCurrentScope().get());
    this.getCurrentScope().get().add(symbol);
  }

  @Override
  public void visit(@NotNull ASTComponentInstantiation node) {
    Preconditions.checkArgument(node != null);
    Preconditions.checkState(this.getCurrentScope().isPresent());
    node.setEnclosingScope(this.getCurrentScope().get());
  }

  protected ComponentInstanceSymbolBuilder create_ComponentInstance(@NotNull ASTComponentInstance ast) {
    ComponentInstanceSymbolBuilder builder = ArcBasisMill.componentInstanceSymbolBuilder();
    Preconditions.checkArgument(ast != null);
    Preconditions.checkState(this.getCurrentScope().isPresent());
    ast.setEnclosingScope(this.getCurrentScope().get());
    builder.setName(ast.getName());
    return builder;
  }

  @Override
  public void visit(@NotNull ASTComponentInstance node) {
    Preconditions.checkArgument(node != null);
    Preconditions.checkState(this.getCurrentEnclosingScope4Instances().isPresent());

    ComponentInstanceSymbol symbol = create_ComponentInstance(node).build();
    node.setSymbol(symbol);
    node.setEnclosingScope(getCurrentEnclosingScope4Instances().get());
    symbol.setAstNode(node);
    symbol.setEnclosingScope(getCurrentEnclosingScope4Instances().get());
    getCurrentEnclosingScope4Instances().get().add(symbol);
  }

  @Override
  public void endVisit(ASTComponentInstance ast){
    Preconditions.checkNotNull(ast);
    Preconditions.checkState(ast.isPresentSymbol());
    if (ast.isPresentArguments()) {
      ast.getSymbol().addArguments(ast.getArguments().getExpressionList());
    }
  }

  /**
   * the scope is the enclosing scope of the expression to create
   * @param printer printer that can be used to print a type
   * @return a mapper that can create a symTypeExpression from a MontiCore-Type
   */
  public static BiFunction<ASTMCType, IArcBasisScope, SymTypeExpression> mapWith(MCBasicTypesFullPrettyPrinter printer){
    Preconditions.checkNotNull(printer);
    return (type, scope) -> SymTypeExpressionFactory.createTypeExpression(
        Preconditions.checkNotNull(type).printType(printer),
        Preconditions.checkNotNull(scope));
  }
}