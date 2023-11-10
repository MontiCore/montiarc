/* (c) https://github.com/MontiCore/monticore */
package arcbasis._symboltable;

import arcbasis.ArcBasisMill;
import arcbasis._ast.ASTArcElement;
import arcbasis._ast.ASTArcField;
import arcbasis._ast.ASTArcFieldDeclaration;
import arcbasis._ast.ASTArcParameter;
import arcbasis._ast.ASTComponentHead;
import arcbasis._ast.ASTComponentInstance;
import arcbasis._ast.ASTComponentInstantiation;
import arcbasis._ast.ASTComponentType;
import arcbasis._ast.ASTArcPort;
import arcbasis._ast.ASTPortDeclaration;
import arcbasis._ast.ASTPortDirection;
import com.google.common.base.Preconditions;
import de.monticore.symbols.basicsymbols._symboltable.VariableSymbol;
import de.monticore.symbols.basicsymbols._symboltable.VariableSymbolBuilder;
import de.monticore.symbols.compsymbols._symboltable.SubcomponentSymbol;
import de.monticore.symbols.compsymbols._symboltable.SubcomponentSymbolBuilder;
import de.monticore.symboltable.modifiers.BasicAccessModifier;
import org.codehaus.commons.nullanalysis.NotNull;
import org.codehaus.commons.nullanalysis.Nullable;

import java.util.ArrayList;
import java.util.Optional;
import java.util.Stack;

public class ArcBasisScopesGenitor extends ArcBasisScopesGenitorTOP {

  protected Stack<ComponentTypeSymbol> componentStack;
  protected ASTPortDirection currentPortDirection;

  public ArcBasisScopesGenitor() {
    super();
    this.componentStack = new Stack<>();
  }

  protected Stack<ComponentTypeSymbol> getComponentStack() {
    return this.componentStack;
  }

  protected Optional<ComponentTypeSymbol> getCurrentComponent() {
    return Optional.ofNullable(this.getComponentStack().peek());
  }

  protected void removeCurrentComponent() {
    this.getComponentStack().pop();
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

  @Override
  public IArcBasisArtifactScope createFromAST(@NotNull ASTArcElement rootNode) {
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
    Preconditions.checkNotNull(symbol);
    Preconditions.checkState(this.getCurrentScope().isPresent());
    this.getCurrentScope().get().add(symbol);
  }

  protected ComponentTypeSymbolBuilder create_ComponentType(@NotNull ASTComponentType ast) {
    ComponentTypeSymbolBuilder builder = ArcBasisMill.componentTypeSymbolBuilder();
    builder.setName(ast.getName());
    IArcBasisScope scope = this.createScope(true);
    builder.setSpannedScope(scope);
    return builder;
  }

  /**
   * if this is an inner component, this method sets the outer component of this one
   *
   * @param symbol component to integrate into the structure
   */
  protected void setOuter(@NotNull ComponentTypeSymbol symbol) {
    if (!componentStack.isEmpty()) {
      symbol.setOuterComponent(componentStack.peek());
    }
  }

  @Override
  public void visit(@NotNull ASTComponentType node) {
    Preconditions.checkNotNull(node);
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
    Preconditions.checkNotNull(node);
    Preconditions.checkState(this.getCurrentComponent().isPresent());
    Preconditions.checkState(this.getCurrentScope().isPresent());
    Preconditions.checkState(this.getCurrentComponent().get().isPresentAstNode());
    Preconditions.checkState(this.getCurrentComponent().get().getAstNode().equals(node));
    this.removeCurrentComponent();
    this.removeCurrentScope();
  }

  @Override
  public void visit(@NotNull ASTComponentHead node) {
    Preconditions.checkNotNull(node);
    Preconditions.checkState(this.getCurrentScope().isPresent());
    Preconditions.checkState(!this.getComponentStack().isEmpty());
    Preconditions.checkState(this.getCurrentComponent().isPresent());
    node.setEnclosingScope(getCurrentScope().get());
  }

  protected VariableSymbolBuilder create_ArcParameter(@NotNull ASTArcParameter ast) {
    assert (this.getCurrentScope().isPresent());
    VariableSymbolBuilder builder = ArcBasisMill.variableSymbolBuilder();
    builder.setAccessModifier(BasicAccessModifier.PROTECTED);
    builder.setName(ast.getName());
    return builder;
  }

  @Override
  public void visit(@NotNull ASTArcParameter node) {
    Preconditions.checkNotNull(node);
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
    Preconditions.checkNotNull(node);
    Preconditions.checkState(getCurrentScope().isPresent());
    node.setEnclosingScope(getCurrentScope().get());
    this.setCurrentPortDirection(node.getPortDirection());
  }

  @Override
  public void endVisit(@NotNull ASTPortDeclaration node) {
    Preconditions.checkNotNull(node);
    Preconditions.checkState(this.getCurrentPortDirection().isPresent());
    Preconditions.checkState(this.getCurrentPortDirection().get().equals(node.getPortDirection()));
    this.setCurrentPortDirection(null);
  }

  protected ArcPortSymbolBuilder create_Port(@NotNull ASTArcPort ast) {
    Preconditions.checkState(this.getCurrentPortDirection().isPresent());
    Preconditions.checkState(this.getCurrentScope().isPresent());
    ArcPortSymbolBuilder builder = ArcBasisMill.arcPortSymbolBuilder();
    builder.setName(ast.getName());
    builder.setIncoming(this.getCurrentPortDirection().get().isIn());
    builder.setOutgoing(this.getCurrentPortDirection().get().isOut());
    return builder;
  }

  @Override
  public void visit(@NotNull ASTArcPort node) {
    Preconditions.checkNotNull(node);
    Preconditions.checkState(getCurrentScope().isPresent());
    Preconditions.checkState(getCurrentPortDirection().isPresent());

    ArcPortSymbol port = this.create_Port(node).buildWithoutType();
    node.setSymbol(port);
    port.setAstNode(node);
    node.setEnclosingScope(this.getCurrentScope().get());
    SymbolService.link(this.getCurrentScope().get(), port);
  }

  @Override
  public void visit(@NotNull ASTArcFieldDeclaration node) {
    Preconditions.checkNotNull(node);
    Preconditions.checkState(this.getCurrentScope().isPresent());
    node.setEnclosingScope(this.getCurrentScope().get());
  }

  protected VariableSymbolBuilder create_ArcField(@NotNull ASTArcField ast) {
    Preconditions.checkNotNull(ast);
    VariableSymbolBuilder builder = ArcBasisMill.variableSymbolBuilder();
    builder.setAccessModifier(BasicAccessModifier.PRIVATE);
    builder.setName(ast.getName());
    return builder;
  }

  @Override
  public void visit(@NotNull ASTArcField node) {
    Preconditions.checkNotNull(node);
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
    Preconditions.checkNotNull(node);
    Preconditions.checkState(this.getCurrentScope().isPresent());
    node.setEnclosingScope(this.getCurrentScope().get());
  }

  protected SubcomponentSymbolBuilder create_ComponentInstance(@NotNull ASTComponentInstance ast) {
    SubcomponentSymbolBuilder builder = ArcBasisMill.subcomponentSymbolBuilder();
    Preconditions.checkNotNull(ast);
    builder.setName(ast.getName());
    return builder;
  }

  @Override
  public void visit(@NotNull ASTComponentInstance node) {
    Preconditions.checkNotNull(node);
    Preconditions.checkState(this.getCurrentScope().isPresent());

    SubcomponentSymbol symbol = create_ComponentInstance(node).build();
    symbol.setAstNode(node);
    node.setSymbol(symbol);
    node.setEnclosingScope(this.getCurrentScope().get());
    this.getCurrentScope().get().add(symbol);
  }
}