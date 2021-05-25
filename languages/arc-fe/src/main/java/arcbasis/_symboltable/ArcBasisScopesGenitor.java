/* (c) https://github.com/MontiCore/monticore */
package arcbasis._symboltable;

import arcbasis.ArcBasisMill;
import arcbasis._ast.*;
import arcbasis.check.FullSynthesizeSymTypeFromMCBasicTypes;
import com.google.common.base.Preconditions;
import de.monticore.symbols.basicsymbols._symboltable.VariableSymbol;
import de.monticore.symbols.basicsymbols._symboltable.VariableSymbolBuilder;
import de.monticore.types.check.ISynthesize;
import de.monticore.types.check.SymTypeExpression;
import de.monticore.types.check.SymTypeExpressionFactory;
import de.monticore.types.mcbasictypes.MCBasicTypesMill;
import de.monticore.types.mcbasictypes._ast.ASTMCType;
import de.monticore.types.mccollectiontypes._ast.ASTMCGenericType;
import de.monticore.types.prettyprint.MCBasicTypesFullPrettyPrinter;
import org.codehaus.commons.nullanalysis.NotNull;
import org.codehaus.commons.nullanalysis.Nullable;

import java.util.ArrayList;
import java.util.EmptyStackException;
import java.util.Optional;
import java.util.Stack;
import java.util.function.BiFunction;

public class ArcBasisScopesGenitor extends ArcBasisScopesGenitorTOP {

  protected Stack<ComponentTypeSymbol> componentStack = new Stack<>();
  protected MCBasicTypesFullPrettyPrinter typePrinter = MCBasicTypesMill.mcBasicTypesPrettyPrinter();
  protected BiFunction<ASTMCType, IArcBasisScope, SymTypeExpression> expressionCreator;
  protected ISynthesize typeSynthesizer;
  protected ASTMCType currentCompInstanceType;
  protected Stack<IArcBasisScope> enclosingScope4InstancesStack = new Stack();
  protected ASTMCType currentFieldType;
  protected ASTMCType currentPortType;
  protected ASTPortDirection currentPortDirection;

  public ArcBasisScopesGenitor() {
    this.typeSynthesizer = new FullSynthesizeSymTypeFromMCBasicTypes();
  }

  /**
   * allows updating the internal printer used to print types
   * @param typesPrinter new printer
   */
  public void setTypePrinter(@NotNull MCBasicTypesFullPrettyPrinter typesPrinter) {
    assert typesPrinter != null;
    this.typePrinter = typesPrinter;
  }

  /**
   * @param type type of an model element instance
   * @return a string representation of this element type that can be used for building expressions, etc.
   */
  protected String printType(@NotNull ASTMCType type) {
    assert type != null;
    return type.printType(typePrinter);
  }

  /**
   * transforms this type to an expression and maybe adds it to the scope
   * @param type type to transform
   * @return null- void- or object-expression
   */
  protected SymTypeExpression createTypeExpression(@NotNull ASTMCType type) {
    assert type != null && this.getCurrentScope().isPresent();
    if(expressionCreator==null){
      updateConverter(mapWith(typePrinter));
    }
    return expressionCreator.apply(type, getCurrentScope().get());
  }

  /**
   * sets a new mapper object used to create sym tab expressions
   * @param newBuilder new lambda
   * @return this object, for daisy chaining
   */
  public ArcBasisScopesGenitor updateConverter(BiFunction<ASTMCType, IArcBasisScope, SymTypeExpression> newBuilder){
    expressionCreator = newBuilder;
    return this;
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

  /**
   * Creates an ASTMCType that represents the  given component type
   * @param comp the component type for which the ASMCType should be created
   * @return the given component type, represented as MCType
   */
  protected ASTMCType mcTypeFromCompType(@NotNull ComponentTypeSymbol comp) {
    Preconditions.checkArgument(comp != null);
    Preconditions.checkArgument(comp.getEnclosingScope() != null);

    ASTMCType type = ArcBasisMill
      .mCQualifiedTypeBuilder()
      .setMCQualifiedName(
        MCBasicTypesMill
          .mCQualifiedNameBuilder()
          .addParts(comp.getFullName())
          .build()
      )
      .build();

    type.setEnclosingScope(comp.getEnclosingScope());

    return type;
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
    this.setCurrentCompInstanceType(this.mcTypeFromCompType(symbol));
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
    this.setCurrentCompInstanceType(null);
  }

  @Override
  public void visit(@NotNull ASTComponentHead node) {
    Preconditions.checkArgument(node != null);
    Preconditions.checkState(this.getCurrentScope().isPresent());
    Preconditions.checkState(!this.getComponentStack().isEmpty());
    Preconditions.checkState(this.getCurrentComponent().isPresent());
    node.setEnclosingScope(getCurrentScope().get());
    if (node.isPresentParent()) {
      this.getCurrentComponent().get().setParent(this.delegateToParent(node));
    }
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

  /**
   * creates a surrogate of the component this component extends
   * @param node a component head defining an extends-relation
   * @return the parent: the extended component
   */
  protected ComponentTypeSymbol delegateToParent(@NotNull ASTComponentHead node) {
    Preconditions.checkArgument(node != null);
    Preconditions.checkArgument(node.isPresentParent());
    return this.create_ComponentSurrogate(node.getParent());
  }

  /**
   * todo: override the simple types genitor to do this
   */
  public void visit(ASTMCType node) {
    Preconditions.checkState(this.getCurrentScope().isPresent());
    node.setEnclosingScope(this.getCurrentScope().get());
  }

  protected VariableSymbolBuilder create_ArcParameter(@NotNull ASTArcParameter ast) {
    assert (this.getCurrentScope().isPresent());
    VariableSymbolBuilder builder = ArcBasisMill.variableSymbolBuilder();
    builder.setName(ast.getName());
    builder.setType(this.createTypeExpression(ast.getMCType()));
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

  protected PortSymbolBuilder create_Port(@NotNull ASTPort ast) {
    assert (this.getCurrentPortType().isPresent());
    assert (this.getCurrentPortDirection().isPresent());
    assert (this.getCurrentScope().isPresent());
    PortSymbolBuilder builder = ArcBasisMill.portSymbolBuilder();
    builder.setName(ast.getName());
    builder.setType(this.createTypeExpression(this.getCurrentPortType().get()));
    builder.setDirection(this.getCurrentPortDirection().get());
    return builder;
  }

  @Override
  public void visit(@NotNull ASTPort node) {
    Preconditions.checkArgument(node != null);
    Preconditions.checkState(getCurrentScope().isPresent());
    Preconditions.checkState(getCurrentPortType().isPresent());
    Preconditions.checkState(getCurrentPortDirection().isPresent());
    PortSymbol symbol = this.create_Port(node).build();
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
    this.setCurrentFieldType(node.getMCType());
  }

  @Override
  public void endVisit(@NotNull ASTArcFieldDeclaration node) {
    Preconditions.checkArgument(node != null);
    Preconditions.checkState(this.getCurrentFieldType().isPresent());
    Preconditions.checkState(this.getCurrentFieldType().get().equals(node.getMCType()));
    this.setCurrentFieldType(null);
  }

  protected VariableSymbolBuilder create_ArcField(@NotNull ASTArcField ast) {
    assert (this.getCurrentFieldType().isPresent());
    assert (this.getCurrentScope().isPresent());
    VariableSymbolBuilder builder = ArcBasisMill.variableSymbolBuilder();
    builder.setName(ast.getName());
    builder.setType(this.createTypeExpression(this.getCurrentFieldType().get()));
    return builder;
  }

  @Override
  public void visit(@NotNull ASTArcField node) {
    Preconditions.checkArgument(node != null);
    Preconditions.checkState(this.getCurrentScope().isPresent());
    Preconditions.checkState(this.getCurrentFieldType().isPresent());
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
    this.setCurrentCompInstanceType(node.getMCType());
  }

  @Override
  public void endVisit(@NotNull ASTComponentInstantiation node) {
    Preconditions.checkArgument(node != null);
    Preconditions.checkState(this.getCurrentCompInstanceType().isPresent());
    Preconditions.checkState(this.getCurrentCompInstanceType().get().equals(node.getMCType()));
    this.setCurrentCompInstanceType(null);
  }

  /**
   * todo: Generic components belong to generic arc
   * builds a component surrogate and adds it to the current enclosing scope
   * @param type component type
   */
  protected ComponentTypeSymbolSurrogate create_ComponentSurrogate(@NotNull ASTMCType type) {
    Preconditions.checkState(this.getCurrentScope().isPresent());
    if (type instanceof ASTMCGenericType){
      return ArcBasisMill.componentTypeSymbolSurrogateBuilder().setName(((ASTMCGenericType)type).printWithoutTypeArguments()).setEnclosingScope(this.getCurrentScope().get()).build();
    }
    return ArcBasisMill.componentTypeSymbolSurrogateBuilder().setName(this.printType(type)).setEnclosingScope(this.getCurrentScope().get()).build();
  }

  protected ComponentInstanceSymbolBuilder create_ComponentInstance(@NotNull ASTComponentInstance ast) {
    ComponentInstanceSymbolBuilder builder = ArcBasisMill.componentInstanceSymbolBuilder();
    Preconditions.checkArgument(ast != null);
    Preconditions.checkState(this.getCurrentCompInstanceType().isPresent());
    Preconditions.checkState(this.getCurrentScope().isPresent());
    ast.setEnclosingScope(this.getCurrentScope().get());
    builder.setName(ast.getName()).setType(this.delegateToType(ast));
    builder.fetchParametersFrom(createTypeExpression(this.getCurrentCompInstanceType().get()));
    return builder;
  }

  /**
   * @param node not used, but may not be null
   * @return component surrogate of the type of the current instance
   */
  protected ComponentTypeSymbol delegateToType(@NotNull ASTComponentInstance node) {
    Preconditions.checkArgument(node != null);
    Preconditions.checkState(this.getCurrentCompInstanceType().isPresent());
    return this.create_ComponentSurrogate(this.getCurrentCompInstanceType().get());
  }

  @Override
  public void visit(@NotNull ASTComponentInstance node) {
    Preconditions.checkArgument(node != null);
    Preconditions.checkState(this.getCurrentCompInstanceType().isPresent());
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