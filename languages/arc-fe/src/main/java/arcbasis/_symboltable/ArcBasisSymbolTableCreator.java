/* (c) https://github.com/MontiCore/monticore */
package arcbasis._symboltable;

import arcbasis.ArcBasisMill;
import arcbasis._ast.*;
import com.google.common.base.Preconditions;
import de.monticore.prettyprint.IndentPrinter;
import de.monticore.types.check.SymTypeExpression;
import de.monticore.types.check.SymTypeExpressionFactory;
import de.monticore.types.mcbasictypes._ast.ASTMCType;
import de.monticore.types.mccollectiontypes._ast.ASTMCGenericType;
import de.monticore.types.prettyprint.MCBasicTypesPrettyPrinter;
import de.monticore.types.typesymbols._symboltable.FieldSymbol;
import de.monticore.types.typesymbols._symboltable.FieldSymbolBuilder;
import de.monticore.types.typesymbols._symboltable.ITypeSymbolsScope;
import de.monticore.types.typesymbols._symboltable.TypeSymbolLoader;
import org.codehaus.commons.nullanalysis.NotNull;
import org.codehaus.commons.nullanalysis.Nullable;

import java.util.Deque;
import java.util.Optional;
import java.util.Stack;

/**
 * Visitor that creates the symbol-table of a component.
 */
public class ArcBasisSymbolTableCreator extends ArcBasisSymbolTableCreatorTOP {

  protected Stack<ComponentTypeSymbol> componentStack = new Stack<>();
  protected MCBasicTypesPrettyPrinter typePrinter;
  protected ASTMCType currentCompInstanceType;
  protected ASTMCType currentFieldType;
  protected ASTMCType currentPortType;
  protected ASTPortDirection currentPortDirection;

  public ArcBasisSymbolTableCreator(@NotNull IArcBasisScope enclosingScope) {
    super(Preconditions.checkNotNull(enclosingScope));
    this.typePrinter = new MCBasicTypesPrettyPrinter(new IndentPrinter());
  }

  public ArcBasisSymbolTableCreator(@NotNull Deque<? extends IArcBasisScope> scopeStack) {
    super(Preconditions.checkNotNull(scopeStack));
    this.typePrinter = new MCBasicTypesPrettyPrinter(new IndentPrinter());
  }

  public ArcBasisSymbolTableCreator(@NotNull IArcBasisScope enclosingScope,
    @NotNull MCBasicTypesPrettyPrinter typePrinter) {
    super(Preconditions.checkNotNull(enclosingScope));
    this.typePrinter = Preconditions.checkNotNull(typePrinter);
  }

  public ArcBasisSymbolTableCreator(@NotNull Deque<? extends IArcBasisScope> scopeStack,
    @NotNull MCBasicTypesPrettyPrinter typePrinter) {
    super(Preconditions.checkNotNull(scopeStack));
    this.typePrinter = Preconditions.checkNotNull(typePrinter);
  }

  protected MCBasicTypesPrettyPrinter getTypePrinter() {
    return this.typePrinter;
  }

  protected void setTypePrinter(@NotNull MCBasicTypesPrettyPrinter typesPrinter) {
    assert typesPrinter != null;
    this.typePrinter = typesPrinter;
  }

  protected String printType(@NotNull ASTMCType type) {
    assert type != null;
    return type.printType(this.getTypePrinter());
  }

  protected SymTypeExpression createTypeExpression(@NotNull ASTMCType type, @NotNull ITypeSymbolsScope scope) {
    assert type != null;
    assert scope != null;
    return SymTypeExpressionFactory.createTypeExpression(this.printType(type), scope);
  }

  protected Stack<ComponentTypeSymbol> getComponentStack() {
    return this.componentStack;
  }

  protected void setComponentStack(@NotNull Stack<ComponentTypeSymbol> stack) {
    Preconditions.checkArgument(stack != null);
    this.componentStack = stack;
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

  protected Optional<ASTMCType> getCurrentPortType() {
    return Optional.ofNullable(this.currentPortType);
  }

  protected void setCurrentPortType(@Nullable ASTMCType currentPortType) {
    this.currentPortType = currentPortType;
  }

  protected Optional<ASTPortDirection> getCurrentPortDirection() {
    return Optional.ofNullable(this.currentPortDirection);
  }

  protected void setCurrentPortDirection(@Nullable ASTPortDirection currentPortDirection) {
    this.currentPortDirection = currentPortDirection;
  }

  protected Optional<ASTMCType> getCurrentFieldType() {
    return Optional.ofNullable(this.currentFieldType);
  }

  protected void setCurrentFieldType(@Nullable ASTMCType currentFieldType) {
    this.currentFieldType = currentFieldType;
  }

  protected Optional<ASTMCType> getCurrentCompInstanceType() {
    return Optional.ofNullable((this.currentCompInstanceType));
  }

  protected void setCurrentCompInstanceType(@Nullable ASTMCType currentCompInstanceType) {
    this.currentCompInstanceType = currentCompInstanceType;
  }

  @Override
  public void addToScope(@NotNull ComponentTypeSymbol symbol) {
    Preconditions.checkArgument(symbol != null);
    Preconditions.checkState(this.getCurrentScope().isPresent());
    this.getCurrentScope().get().add(symbol);
  }

  @Override
  protected ComponentTypeSymbol create_ComponentType(@NotNull ASTComponentType ast) {
    ComponentTypeSymbolBuilder builder = ArcBasisMill.componentTypeSymbolBuilder();
    builder.setName(ast.getName());
    IArcBasisScope scope = this.createScope(false);
    builder.setSpannedScope(scope);
    return builder.build();
  }

  @Override
  protected void initialize_ComponentType(@NotNull ComponentTypeSymbol symbol, @NotNull ASTComponentType ast) {
    if (!componentStack.isEmpty()) {
      symbol.setOuterComponent(componentStack.peek());
    }
  }

  @Override
  public void addToScopeAndLinkWithNode(@NotNull ComponentTypeSymbol symbol,
    @NotNull ASTComponentType ast) {
    this.addToScope(symbol);
    this.putOnStack(symbol.getSpannedScope());
    this.setLinkBetweenSymbolAndNode(symbol, ast);
  }

  @Override
  public void visit(@NotNull ASTComponentType node) {
    Preconditions.checkArgument(node != null);
    Preconditions.checkState(this.getCurrentScope().isPresent());
    ComponentTypeSymbol symbol = this.create_ComponentType(node);
    this.initialize_ComponentType(symbol, node);
    this.addToScopeAndLinkWithNode(symbol, node);
    componentStack.push(symbol);
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
    if (node.isPresentParent()) {
      ComponentTypeSymbolLoader parentLoader =
        this.create_ComponentLoader(node.getParent());
      parentLoader.setEnclosingScope(this.getCurrentComponent().get().getEnclosingScope());
      this.getCurrentComponent().get().setParent(parentLoader);
    }
  }

  /**
   * @deprecated No need to create type loaders, use SymTypeExpressionFactory to directly create type ecpressions
   */
  @Deprecated
  protected TypeSymbolLoader create_TypeLoader(@NotNull ASTMCType type) {
    assert (this.getCurrentScope().isPresent());
    return new TypeSymbolLoader(this.printType(type),
      this.getCurrentScope().get());
  }

  @Override
  protected FieldSymbol create_ArcParameter(@NotNull ASTArcParameter ast) {
    assert (this.getCurrentScope().isPresent());
    FieldSymbolBuilder builder = ArcBasisMill.fieldSymbolBuilder();
    builder.setName(ast.getName());
    builder.setType(this.createTypeExpression(ast.getMCType(), this.getCurrentScope().get()));
    return builder.build();
  }

  @Override
  public void visit(@NotNull ASTArcParameter node) {
    Preconditions.checkArgument(node != null);
    Preconditions.checkState(this.getCurrentScope().isPresent());
    Preconditions.checkState(this.getCurrentComponent().isPresent());
    FieldSymbol symbol = this.create_ArcParameter(node);
    this.initialize_ArcParameter(symbol, node);
    this.addToScopeAndLinkWithNode(symbol, node);
    this.getCurrentComponent().get().addParameter(symbol);
  }

  @Override
  public void visit(@NotNull ASTPortDeclaration node) {
    Preconditions.checkArgument(node != null);
    Preconditions.checkState(getCurrentScope().isPresent());
    node.setEnclosingScope(getCurrentScope().get());
    this.setCurrentPortType(node.getMCType());
    this.setCurrentPortDirection(node.getPortDirection());
  }

  @Override
  public void endVisit(@NotNull ASTPortDeclaration node) {
    Preconditions.checkArgument(node != null);
    Preconditions.checkState(this.getCurrentPortType().isPresent());
    Preconditions.checkState(this.getCurrentPortType().get().equals(node.getMCType()));
    Preconditions.checkState(this.getCurrentPortDirection().isPresent());
    Preconditions.checkState(this.getCurrentPortDirection().get().equals(node.getPortDirection()));
    this.setCurrentPortType(null);
    this.setCurrentPortDirection(null);
  }

  @Override
  protected PortSymbol create_Port(@NotNull ASTPort ast) {
    assert (this.getCurrentPortType().isPresent());
    assert (this.getCurrentPortDirection().isPresent());
    assert (this.getCurrentScope().isPresent());
    PortSymbolBuilder builder = ArcBasisMill.portSymbolBuilder();
    builder.setName(ast.getName());
    builder.setType(this.createTypeExpression(this.getCurrentPortType().get(), getCurrentScope().get()));
    builder.setDirection(this.getCurrentPortDirection().get());
    return builder.build();
  }

  @Override
  public void visit(@NotNull ASTPort node) {
    Preconditions.checkArgument(node != null);
    Preconditions.checkState(getCurrentScope().isPresent());
    Preconditions.checkState(getCurrentPortType().isPresent());
    Preconditions.checkState(getCurrentPortDirection().isPresent());
    PortSymbol symbol = this.create_Port(node);
    this.initialize_Port(symbol, node);
    this.addToScopeAndLinkWithNode(symbol, node);
  }

  @Override
  public void visit(@NotNull ASTArcFieldDeclaration node) {
    Preconditions.checkArgument(node != null);
    Preconditions.checkState(this.getCurrentScope().isPresent());
    node.setEnclosingScope(this.getCurrentScope().get());
    this.setCurrentFieldType(node.getMCType());
  }

  @Override
  public void endVisit(@NotNull ASTArcFieldDeclaration node) {
    Preconditions.checkArgument(node != null);
    Preconditions.checkState(this.getCurrentFieldType().isPresent());
    Preconditions.checkState(this.getCurrentFieldType().get().equals(node.getMCType()));
    this.setCurrentFieldType(null);
  }

  @Override
  protected FieldSymbol create_ArcField(@NotNull ASTArcField ast) {
    assert (this.getCurrentFieldType().isPresent());
    assert (this.getCurrentScope().isPresent());
    FieldSymbolBuilder builder = ArcBasisMill.fieldSymbolBuilder();
    builder.setName(ast.getName());
    builder.setType(this.createTypeExpression(this.getCurrentFieldType().get(), this.getCurrentScope().get()));
    return builder.build();
  }

  @Override
  public void visit(@NotNull ASTArcField node) {
    Preconditions.checkArgument(node != null);
    Preconditions.checkState(this.getCurrentScope().isPresent());
    Preconditions.checkState(this.getCurrentFieldType().isPresent());
    FieldSymbol symbol = this.create_ArcField(node);
    this.initialize_ArcField(symbol, node);
    this.addToScopeAndLinkWithNode(symbol, node);
  }

  @Override
  public void visit(@NotNull ASTComponentInstantiation node) {
    Preconditions.checkArgument(node != null);
    Preconditions.checkState(this.getCurrentScope().isPresent());
    node.setEnclosingScope(this.getCurrentScope().get());
    this.setCurrentCompInstanceType(node.getMCType());
  }

  @Override
  public void endVisit(@NotNull ASTComponentInstantiation node) {
    Preconditions.checkArgument(node != null);
    Preconditions.checkState(this.getCurrentCompInstanceType().isPresent());
    Preconditions.checkState(this.getCurrentCompInstanceType().get().equals(node.getMCType()));
    this.setCurrentCompInstanceType(null);
  }

  protected ComponentTypeSymbolLoader create_ComponentLoader(@NotNull ASTMCType type) {
    if (type instanceof ASTMCGenericType){
      return ArcBasisMill.componentTypeSymbolLoaderBuilder().setName(((ASTMCGenericType)type).printWithoutTypeArguments()).build();
    }
    return ArcBasisMill.componentTypeSymbolLoaderBuilder().setName(this.printType(type)).build();
  }

  @Override
  protected ComponentInstanceSymbol create_ComponentInstance(@NotNull ASTComponentInstance ast) {
    ComponentInstanceSymbolBuilder builder = ArcBasisMill.componentInstanceSymbolBuilder();
    Preconditions.checkArgument(ast != null);
    Preconditions.checkState(this.getCurrentCompInstanceType().isPresent());
    Preconditions.checkState(this.getCurrentScope().isPresent());
    ComponentTypeSymbolLoader typeLoader =
      this.create_ComponentLoader(this.getCurrentCompInstanceType().get());
    typeLoader.setEnclosingScope(this.getCurrentScope().get());
    builder.setName(ast.getName()).setType(typeLoader);
    return builder.build();
  }

  @Override
  protected void initialize_ComponentInstance(@NotNull ComponentInstanceSymbol symbol,
    @NotNull ASTComponentInstance ast) {
    if (ast.isPresentArcArguments()) {
      symbol.addArguments(ast.getArcArguments().getExpressionList());
    }
  }

  @Override
  public void visit(@NotNull ASTComponentInstance node) {
    Preconditions.checkArgument(node != null);
    Preconditions.checkState(this.getCurrentScope().isPresent());
    Preconditions.checkState(this.getCurrentCompInstanceType().isPresent());
    ComponentInstanceSymbol symbol = create_ComponentInstance(node);
    this.initialize_ComponentInstance(symbol, node);
    this.addToScopeAndLinkWithNode(symbol, node);
  }
}