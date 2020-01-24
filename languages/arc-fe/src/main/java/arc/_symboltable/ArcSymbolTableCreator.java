/* (c) https://github.com/MontiCore/monticore */
package arc._symboltable;

import arc._ast.*;
import com.google.common.base.Preconditions;
import de.monticore.expressions.expressionsbasis._ast.ASTExpression;
import de.monticore.prettyprint.IndentPrinter;
import de.monticore.types.check.SymTypeExpression;
import de.monticore.types.check.SymTypeExpressionFactory;
import de.monticore.types.mcbasictypes._ast.ASTMCObjectType;
import de.monticore.types.mcbasictypes._ast.ASTMCType;
import de.monticore.types.prettyprint.MCBasicTypesPrettyPrinter;
import de.monticore.types.typesymbols._symboltable.*;
import org.codehaus.commons.nullanalysis.NotNull;
import org.codehaus.commons.nullanalysis.Nullable;

import java.util.*;

/**
 * Visitor that creates the symbol-table of a component.
 */
public class ArcSymbolTableCreator extends ArcSymbolTableCreatorTOP {

  protected Stack<ComponentSymbol> componentStack = new Stack<>();
  protected MCBasicTypesPrettyPrinter typePrinter;
  protected ASTMCType currentParameterType;
  protected ASTMCObjectType currentCompInstanceType;
  protected List<ASTExpression> currentCompInstanceArguments;
  protected ASTMCType currentFieldType;
  protected ASTMCType currentPortType;
  protected String currentPortDirection;

  public ArcSymbolTableCreator(@NotNull IArcScope enclosingScope) {
    super(Preconditions.checkNotNull(enclosingScope));
    this.typePrinter = new MCBasicTypesPrettyPrinter(new IndentPrinter());
  }

  public ArcSymbolTableCreator(@NotNull Deque<? extends IArcScope> scopeStack) {
    super(Preconditions.checkNotNull(scopeStack));
    this.typePrinter = new MCBasicTypesPrettyPrinter(new IndentPrinter());
  }

  public ArcSymbolTableCreator(@NotNull IArcScope enclosingScope,
    @NotNull MCBasicTypesPrettyPrinter typePrinter) {
    super(Preconditions.checkNotNull(enclosingScope));
    this.typePrinter = Preconditions.checkNotNull(typePrinter);
  }

  public ArcSymbolTableCreator(@NotNull Deque<? extends IArcScope> scopeStack,
    @NotNull MCBasicTypesPrettyPrinter typePrinter) {
    super(Preconditions.checkNotNull(scopeStack));
    this.typePrinter = Preconditions.checkNotNull(typePrinter);
  }

  public MCBasicTypesPrettyPrinter getTypePrinter() {
    return this.typePrinter;
  }

  public void setTypePrinter(@NotNull MCBasicTypesPrettyPrinter typesPrinter) {
    Preconditions.checkArgument(typesPrinter != null);
    this.typePrinter = typesPrinter;
  }

  protected Stack<ComponentSymbol> getComponentStack() {
    return this.componentStack;
  }

  protected void setComponentStack(@NotNull Stack<ComponentSymbol> stack) {
    Preconditions.checkArgument(stack != null);
    this.componentStack = stack;
  }

  protected Optional<ComponentSymbol> getCurrentComponent() {
    return Optional.ofNullable(this.getComponentStack().peek());
  }

  protected Optional<ComponentSymbol> removeCurrentComponent() {
    return Optional.ofNullable(this.getComponentStack().pop());
  }

  protected void putOnStack(@Nullable ComponentSymbol symbol) {
    this.getComponentStack().push(symbol);
  }

  protected Optional<ASTMCType> getCurrentParameterType() {
    return Optional.ofNullable(this.currentParameterType);
  }

  protected void setCurrentParameterType(@Nullable ASTMCType currentParameterType) {
    this.currentParameterType = currentParameterType;
  }

  protected Optional<ASTMCType> getCurrentPortType() {
    return Optional.ofNullable(this.currentPortType);
  }

  protected void setCurrentPortType(@Nullable ASTMCType currentPortType) {
    this.currentPortType = currentPortType;
  }

  protected Optional<String> getCurrentPortDirection() {
    return Optional.ofNullable(this.currentPortDirection);
  }

  protected void setCurrentPortDirection(@Nullable String currentPortDirection) {
    this.currentPortDirection = currentPortDirection;
  }

  protected Optional<ASTMCType> getCurrentFieldType() {
    return Optional.ofNullable(this.currentFieldType);
  }

  protected void setCurrentFieldType(@Nullable ASTMCType currentFieldType) {
    this.currentFieldType = currentFieldType;
  }

  protected Optional<ASTMCObjectType> getCurrentCompInstanceType() {
    return Optional.ofNullable((this.currentCompInstanceType));
  }

  protected void setCurrentCompInstanceType(@Nullable ASTMCObjectType currentCompInstanceType) {
    this.currentCompInstanceType = currentCompInstanceType;
  }

  protected Optional<List<ASTExpression>> getCurrentCompInstanceArguments() {
    return Optional.ofNullable(this.currentCompInstanceArguments);
  }

  protected void setCurrentCompInstanceArguments(
    @Nullable List<ASTExpression> currentCompInstanceArguments) {
    this.currentCompInstanceArguments = currentCompInstanceArguments;
  }

  @Override
  public void addToScope(@NotNull ComponentSymbol symbol) {
    Preconditions.checkArgument(symbol != null);
    Preconditions.checkState(this.getCurrentScope().isPresent());
    this.getCurrentScope().get().add(symbol);
  }

  @Override
  protected ComponentSymbol create_Component(@NotNull ASTComponent ast) {
    ComponentSymbolBuilder builder = ArcSymTabMill.componentSymbolBuilder();
    builder.setName(ast.getName());
    IArcScope scope = this.createScope(false);
    builder.setSpannedScope(scope);
    return builder.build();
  }

  @Override
  protected void initialize_Component(@NotNull ComponentSymbol symbol, @NotNull ASTComponent ast) {
    if (!componentStack.isEmpty()) {
      symbol.setOuterComponent(componentStack.peek());
    }
  }

  @Override
  public void addToScopeAndLinkWithNode(@NotNull ComponentSymbol symbol,
    @NotNull ASTComponent ast) {
    this.addToScope(symbol);
    this.putOnStack(symbol.getSpannedScope());
    this.setLinkBetweenSymbolAndNode(symbol, ast);
  }

  @Override
  public void visit(@NotNull ASTComponent node) {
    Preconditions.checkArgument(node != null);
    Preconditions.checkState(this.getCurrentScope().isPresent());
    ComponentSymbol symbol = this.create_Component(node);
    this.initialize_Component(symbol, node);
    this.addToScopeAndLinkWithNode(symbol, node);
    componentStack.push(symbol);
  }

  @Override
  public void endVisit(@NotNull ASTComponent node) {
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
    Preconditions.checkState(!this.getComponentStack().isEmpty());
    Preconditions.checkState(this.getCurrentComponent().isPresent());
    if (node.isPresentParentComponent()) {
      ComponentSymbolLoader parentLoader = this.create_ComponentLoader(node.getParentComponent());
      parentLoader.setEnclosingScope(this.getCurrentComponent().get().getEnclosingScope());
      this.getCurrentComponent().get().setParent(parentLoader);
    }
  }

  @Override
  public void visit(@NotNull ASTArcParameterDeclaration node) {
    Preconditions.checkArgument(node != null);
    Preconditions.checkState(this.getCurrentScope().isPresent());
    node.setEnclosingScope(this.getCurrentScope().get());
    this.setCurrentParameterType(node.getType());
  }

  @Override
  public void endVisit(@NotNull ASTArcParameterDeclaration node) {
    Preconditions.checkArgument(node != null);
    Preconditions.checkState(this.getCurrentParameterType().isPresent());
    Preconditions.checkState(this.getCurrentParameterType().get().equals(node.getType()));
    this.setCurrentParameterType(null);
  }

  protected TypeSymbolLoader create_TypeLoader(@NotNull ASTMCType type) {
    assert (this.getCurrentScope().isPresent());
    return new TypeSymbolLoader(type.printType(this.getTypePrinter()),
      this.getCurrentScope().get());
  }

  @Override
  protected FieldSymbol create_ArcParameter(@NotNull ASTArcParameter ast) {
    assert (this.getCurrentParameterType().isPresent());
    assert (this.getCurrentScope().isPresent());
    FieldSymbolBuilder builder = ArcSymTabMill.fieldSymbolBuilder();
    builder.setName(ast.getName());
    TypeSymbolLoader typeLoader = this.create_TypeLoader(this.getCurrentParameterType().get());
    typeLoader.setEnclosingScope(this.getCurrentScope().get());
    SymTypeExpression typeExpression = SymTypeExpressionFactory.createTypeObject(typeLoader);
    builder.setType(typeExpression);
    return builder.build();
  }

  @Override
  public void visit(@NotNull ASTArcParameter node) {
    Preconditions.checkArgument(node != null);
    Preconditions.checkState(this.getCurrentScope().isPresent());
    Preconditions.checkState(this.getCurrentParameterType().isPresent());
    Preconditions.checkState(this.getCurrentComponent().isPresent());
    FieldSymbol symbol = this.create_ArcParameter(node);
    this.initialize_ArcParameter(symbol, node);
    this.addToScopeAndLinkWithNode(symbol, node);
    this.getCurrentComponent().get().addParameter(symbol);
  }

  @Override
  protected TypeVarSymbol create_ArcTypeParameter(@NotNull ASTArcTypeParameter ast) {
    assert (this.getCurrentScope().isPresent());
    TypeVarSymbolBuilder builder = ArcSymTabMill.typeVarSymbolBuilder();
    builder.setName(ast.getName());
    List<SymTypeExpression> bounds = new ArrayList<>();
    for (ASTMCType type : ast.getUpperBoundList()) {
      TypeSymbolLoader typeLoader = create_TypeLoader(type);
      typeLoader.setEnclosingScope(this.getCurrentScope().get());
      bounds.add(SymTypeExpressionFactory.createTypeObject(typeLoader));
    }
    builder.setUpperBoundList(bounds);
    return builder.build();
  }

  @Override
  public void visit(@NotNull ASTArcTypeParameter node) {
    Preconditions.checkArgument(node != null);
    Preconditions.checkState(this.getCurrentScope().isPresent());
    Preconditions.checkState(this.getCurrentComponent().isPresent());
    TypeVarSymbol symbol = this.create_ArcTypeParameter(node);
    this.initialize_ArcTypeParameter(symbol, node);
    this.addToScopeAndLinkWithNode(symbol, node);
    this.getCurrentComponent().get().addTypeParameter(symbol);
  }

  @Override
  public void visit(@NotNull ASTPortDeclaration node) {
    Preconditions.checkArgument(node != null);
    Preconditions.checkState(getCurrentScope().isPresent());
    node.setEnclosingScope(getCurrentScope().get());
    this.setCurrentPortType(node.getType());
    this.setCurrentPortDirection(node.getDirection());
  }

  @Override
  public void endVisit(@NotNull ASTPortDeclaration node) {
    Preconditions.checkArgument(node != null);
    Preconditions.checkState(this.getCurrentPortType().isPresent());
    Preconditions.checkState(this.getCurrentPortType().get().equals(node.getType()));
    Preconditions.checkState(this.getCurrentPortDirection().isPresent());
    Preconditions.checkState(this.getCurrentPortDirection().get().equals(node.getDirection()));
    this.setCurrentPortType(null);
    this.setCurrentPortDirection(null);
  }

  @Override
  protected PortSymbol create_Port(@NotNull ASTPort ast) {
    assert (this.getCurrentPortType().isPresent());
    assert (this.getCurrentPortDirection().isPresent());
    assert (this.getCurrentScope().isPresent());
    PortSymbolBuilder builder = ArcSymTabMill.portSymbolBuilder();
    builder.setName(ast.getName());
    TypeSymbolLoader typeLoader = this.create_TypeLoader(this.getCurrentPortType().get());
    typeLoader.setEnclosingScope(this.getCurrentScope().get());
    SymTypeExpression typeExpression = SymTypeExpressionFactory
      .createTypeObject(typeLoader);
    builder.setType(typeExpression);
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
    this.setCurrentFieldType(node.getType());
  }

  @Override
  public void endVisit(@NotNull ASTArcFieldDeclaration node) {
    Preconditions.checkArgument(node != null);
    Preconditions.checkState(this.getCurrentFieldType().isPresent());
    Preconditions.checkState(this.getCurrentFieldType().get().equals(node.getType()));
    this.setCurrentFieldType(null);
  }

  @Override
  protected FieldSymbol create_ArcField(@NotNull ASTArcField ast) {
    assert (this.getCurrentFieldType().isPresent());
    assert (this.getCurrentScope().isPresent());
    FieldSymbolBuilder builder = ArcSymTabMill.fieldSymbolBuilder();
    builder.setName(ast.getName());
    TypeSymbolLoader typeLoader = this.create_TypeLoader(this.getCurrentFieldType().get());
    typeLoader.setEnclosingScope(this.getCurrentScope().get());
    SymTypeExpression typeExpression = SymTypeExpressionFactory
      .createTypeObject(typeLoader);
    builder.setType(typeExpression);
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
    this.setCurrentCompInstanceType(node.getType());
    this.setCurrentCompInstanceArguments(new ArrayList<>(node.getArgumentList()));
  }

  @Override
  public void endVisit(@NotNull ASTComponentInstantiation node) {
    Preconditions.checkArgument(node != null);
    Preconditions.checkState(this.getCurrentCompInstanceType().isPresent());
    Preconditions.checkState(this.getCurrentCompInstanceType().get().equals(node.getType()));
    Preconditions.checkState(this.getCurrentCompInstanceArguments().isPresent());
    this.setCurrentCompInstanceType(null);
    this.setCurrentCompInstanceArguments(null);
  }

  protected ComponentSymbolLoader create_ComponentLoader(@NotNull ASTMCObjectType type) {
    return ArcSymTabMill.componentSymbolLoaderBuilder()
      .setName(type.printType(this.getTypePrinter())).build();
  }

  @Override
  protected ComponentInstanceSymbol create_ComponentInstance(@NotNull ASTComponentInstance ast) {
    ComponentInstanceSymbolBuilder builder = ArcSymTabMill.componentInstanceSymbolBuilder();
    Preconditions.checkArgument(ast != null);
    Preconditions.checkState(this.getCurrentCompInstanceType().isPresent());
    Preconditions.checkState(this.getCurrentScope().isPresent());
    ComponentSymbolLoader typeLoader =
      this.create_ComponentLoader(this.getCurrentCompInstanceType().get());
    typeLoader.setEnclosingScope(this.getCurrentScope().get());
    builder.setName(ast.getName()).setType(typeLoader);
    return builder.build();
  }

  @Override
  protected void initialize_ComponentInstance(@NotNull ComponentInstanceSymbol symbol,
    @NotNull ASTComponentInstance ast) {
    assert (this.getCurrentCompInstanceArguments().isPresent());
    symbol.addArguments(this.getCurrentCompInstanceArguments().get());
  }

  @Override
  public void visit(@NotNull ASTComponentInstance node) {
    Preconditions.checkArgument(node != null);
    Preconditions.checkState(this.getCurrentScope().isPresent());
    Preconditions.checkState(this.getCurrentCompInstanceType().isPresent());
    Preconditions.checkState(this.getCurrentCompInstanceArguments().isPresent());
    ComponentInstanceSymbol symbol = create_ComponentInstance(node);
    this.initialize_ComponentInstance(symbol, node);
    this.addToScopeAndLinkWithNode(symbol, node);
  }
}