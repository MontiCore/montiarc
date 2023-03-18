/* (c) https://github.com/MontiCore/monticore */
package arcbasis._symboltable;

import arcbasis.AbstractTest;
import arcbasis.ArcBasisMill;
import arcbasis._ast.ASTArcField;
import arcbasis._ast.ASTArcFieldDeclaration;
import arcbasis._ast.ASTArcParameter;
import arcbasis._ast.ASTComponentBody;
import arcbasis._ast.ASTComponentHead;
import arcbasis._ast.ASTComponentInstance;
import arcbasis._ast.ASTComponentInstantiation;
import arcbasis._ast.ASTComponentType;
import arcbasis._ast.ASTPort;
import arcbasis._ast.ASTPortDeclaration;
import arcbasis._visitor.ArcBasisTraverser;
import arcbasis.check.TypeExprOfComponent;
import com.google.common.base.Preconditions;
import de.monticore.expressions.expressionsbasis._ast.ASTExpression;
import de.monticore.symbols.basicsymbols._symboltable.TypeSymbol;
import de.monticore.symbols.basicsymbols._symboltable.VariableSymbol;
import de.monticore.types.check.SymTypeExpressionFactory;
import de.monticore.types.mcbasictypes._ast.ASTConstantsMCBasicTypes;
import de.monticore.types.mcbasictypes._ast.ASTMCQualifiedType;
import de.monticore.types.mcbasictypes._ast.ASTMCType;
import org.codehaus.commons.nullanalysis.NotNull;
import org.codehaus.commons.nullanalysis.Nullable;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.junit.jupiter.params.provider.NullSource;
import org.mockito.Mockito;

import java.util.Optional;
import java.util.function.Consumer;
import java.util.stream.Stream;

/**
 * Holds tests for {@link ArcBasisSymbolTableCompleter}.
 */
public class ArcBasisSymbolTableCompleterTest extends AbstractTest {

  protected ArcBasisSymbolTableCompleter completer;

  protected ArcBasisSymbolTableCompleter getCompleter() {
    return this.completer;
  }

  protected void setUpCompleter() {
    this.completer = ArcBasisMill.symbolTableCompleter();
  }

  @BeforeEach
  @Override
  public void init() {
    super.init();
    this.setUpCompleter();
  }

  /**
   * Method under test {@link ArcBasisSymbolTableCompleter#setTraverser(ArcBasisTraverser)}
   */
  @Test
  public void shouldSetTraverser() {
    // Given
    ArcBasisTraverser traverser = ArcBasisMill.traverser();

    // When
    this.getCompleter().setTraverser(traverser);

    // Then
    Assertions.assertEquals(traverser, this.getCompleter().getTraverser());
  }

  /**
   * Method under test {@link ArcBasisSymbolTableCompleter#setTraverser(ArcBasisTraverser)}
   *
   * @param traverser the traverser to set (null)
   */
  @ParameterizedTest
  @NullSource
  public void setTraverserShouldThrowNullPointerException(@Nullable ArcBasisTraverser traverser) {
    // When && Then
    Assertions.assertThrows(NullPointerException.class, () -> this.getCompleter().setTraverser(traverser));
  }

  /**
   * Method under test {@link ArcBasisSymbolTableCompleter#visit(ASTComponentType)}
   *
   * @param ast      the ast to visit
   * @param expected the expected exception
   */
  @ParameterizedTest
  @MethodSource("visitComponentTypeExpectedExceptionProvider")
  public void visitShouldThrowException(@Nullable ASTComponentType ast,
                                        @NotNull Class<Exception> expected) {
    Preconditions.checkNotNull(expected);

    // When && Then
    Assertions.assertThrows(expected, () -> this.getCompleter().visit(ast));
  }

  protected static Stream<Arguments> visitComponentTypeExpectedExceptionProvider() {
    ASTComponentType ast = ArcBasisMill.componentTypeBuilder().setName("SomeName")
      .setHead(Mockito.mock(ASTComponentHead.class))
      .setBody(Mockito.mock(ASTComponentBody.class))
      .build();
    ast.setSymbolAbsent();
    return Stream.of(
      Arguments.of(null, NullPointerException.class),
      Arguments.of(ast, IllegalArgumentException.class)
    );
  }

  /**
   * Method under test {@link ArcBasisSymbolTableCompleter#visit(ASTComponentType)}
   */
  @Test
  public void shouldVisitComponentType() {
    // Given
    ASTComponentType ast = ArcBasisMill.componentTypeBuilder().setName("SomeName")
      .setHead(Mockito.mock(ASTComponentHead.class))
      .setBody(Mockito.mock(ASTComponentBody.class))
      .build();
    ComponentTypeSymbol symbol = ArcBasisMill.componentTypeSymbolBuilder().setName("SomeName")
      .setSpannedScope(ArcBasisMill.scope()).build();
    ast.setSymbol(symbol);

    // When
    this.getCompleter().visit(ast);

    //Then
    Assertions.assertTrue(this.getCompleter().getCurrentCompInstanceType().isEmpty());
  }

  @Test
  public void shouldVisitComponentTypeAndSetCurrentCompInstanceType() {
    // Given
    ASTComponentType ast = ArcBasisMill.componentTypeBuilder().setName("SomeName")
      .setHead(Mockito.mock(ASTComponentHead.class))
      .setBody(Mockito.mock(ASTComponentBody.class))
      .addInstance("someInst")
      .build();
    ComponentTypeSymbol symbol = ArcBasisMill.componentTypeSymbolBuilder().setName("SomeName")
      .setSpannedScope(ArcBasisMill.scope()).build();
    ast.setSymbol(symbol);

    // When
    this.getCompleter().visit(ast);

    //Then
    Assertions.assertTrue(this.getCompleter().getCurrentCompInstanceType().isPresent());
    Assertions.assertTrue(this.getCompleter().getCurrentCompInstanceType().get() instanceof TypeExprOfComponent);
    Assertions.assertEquals(symbol, this.getCompleter().getCurrentCompInstanceType().get().getTypeInfo());
  }

  /**
   * Method under test {@link ArcBasisSymbolTableCompleter#endVisit(ASTComponentType)}
   *
   * @param ast      the ast to visit
   * @param setup    the setup to execute beforehand
   * @param expected the expected exception
   */
  @ParameterizedTest
  @MethodSource("endVisitComponentTypeExpectedExceptionProvider")
  public void endVisitShouldTrowException(@Nullable ASTComponentType ast,
                                          @NotNull Consumer<ArcBasisSymbolTableCompleter> setup,
                                          @NotNull Class<Exception> expected) {
    Preconditions.checkNotNull(setup);
    Preconditions.checkNotNull(expected);

    // Given
    setup.accept(this.getCompleter());

    // When && Then
    Assertions.assertThrows(expected, () -> this.getCompleter().endVisit(ast));
  }

  public static Stream<Arguments> endVisitComponentTypeExpectedExceptionProvider() {
    ASTComponentType ast1 = ArcBasisMill.componentTypeBuilder().setName("SomeName")
      .setHead(Mockito.mock(ASTComponentHead.class))
      .setBody(Mockito.mock(ASTComponentBody.class))
      .build();
    ast1.setSymbolAbsent();

    Consumer<ArcBasisSymbolTableCompleter> consumer1 = completer -> {};

    return Stream.of(
      Arguments.of(null, consumer1, NullPointerException.class),
      Arguments.of(ast1, consumer1, IllegalArgumentException.class)
    );
  }

  /**
   * Method under test {@link ArcBasisSymbolTableCompleter#endVisit(ASTComponentType)}
   */
  @Test
  public void shouldEndVisitComponentType() {
    // Given
    ASTComponentType ast = ArcBasisMill.componentTypeBuilder()
      .setName("SomeName")
      .setHead(Mockito.mock(ASTComponentHead.class))
      .setBody(Mockito.mock(ASTComponentBody.class))
      .build();
    ast.setSymbol(ArcBasisMill.componentTypeSymbolBuilder()
      .setName("Comp")
      .setSpannedScope(Mockito.mock(IArcBasisScope.class))
      .build());
    ast.getSymbol().setAstNode(ast);

    this.getCompleter().setCurrentCompInstanceType(new TypeExprOfComponent(ast.getSymbol()));

    // When
    this.getCompleter().endVisit(ast);

    // Then
    Assertions.assertFalse(this.getCompleter().getCurrentCompInstanceType().isPresent());
  }

  /**
   * Method under test {@link ArcBasisSymbolTableCompleter#visit(ASTComponentHead)}
   * <p>
   * If the component has no parent then the symbol should not be modified.
   */
  @Test
  public void shouldVisitComponentHeadWithoutParent() {
    // Given
    ComponentTypeSymbol symbol = ArcBasisMill.componentTypeSymbolBuilder()
      .setName("SomeName")
      .setSpannedScope(ArcBasisMill.scope())
      .build();

    ASTComponentHead ast = ArcBasisMill.componentHeadBuilder().build();
    ast.setEnclosingScope(symbol.getSpannedScope());
    ast.setParentAbsent();

    // When
    this.getCompleter().visit(ast);

    // Then
    Assertions.assertFalse(ast.isPresentParent());
    Assertions.assertFalse(symbol.isPresentParent());
  }

  /**
   * Method under test {@link ArcBasisSymbolTableCompleter#visit(ASTComponentHead)}
   */
  @Test
  public void shouldVisitComponentHead() {
    // Given
    String parentCompName = "ParentComp";
    ComponentTypeSymbol parentComp = ArcBasisMill.componentTypeSymbolBuilder()
      .setName(parentCompName)
      .setSpannedScope(Mockito.mock(IArcBasisScope.class))
      .build();

    String childCompName = "ChildComp";
    ComponentTypeSymbol symChildComp = ArcBasisMill.componentTypeSymbolBuilder()
      .setName(childCompName)
      .setSpannedScope(ArcBasisMill.scope())
      .build();

    ASTComponentHead childCompHead = ArcBasisMill.componentHeadBuilder()
      .setParent(createQualifiedType(parentCompName))
      .build();

    ASTComponentType astChildComp = ArcBasisMill.componentTypeBuilder()
      .setName(childCompName)
      .setBody(Mockito.mock(ASTComponentBody.class))
      .setHead(childCompHead)
      .build();

    ArcBasisMill.globalScope().add(parentComp);
    ArcBasisMill.globalScope().add(symChildComp);
    parentComp.getSpannedScope().setEnclosingScope(ArcBasisMill.globalScope());
    symChildComp.getSpannedScope().setEnclosingScope(ArcBasisMill.globalScope());

    symChildComp.setAstNode(astChildComp);
    astChildComp.setSymbol(symChildComp);
    astChildComp.setEnclosingScope(ArcBasisMill.globalScope());
    astChildComp.setSpannedScope(symChildComp.getSpannedScope());
    childCompHead.setEnclosingScope(symChildComp.getSpannedScope());
    childCompHead.getParent().setEnclosingScope(symChildComp.getSpannedScope());
    ((ASTMCQualifiedType) childCompHead.getParent()).getMCQualifiedName().setEnclosingScope(symChildComp.getSpannedScope());

    // When
    getCompleter().visit(childCompHead);

    // Then
    Assertions.assertEquals(parentComp, symChildComp.getParent().getTypeInfo());
  }

  /**
   * Method under test {@link ArcBasisSymbolTableCompleter#visit(ASTComponentHead)}
   *
   * @param ast      the ast to visit
   * @param setup    the setup to execute beforehand
   * @param expected the expected exception
   */
  @ParameterizedTest
  @MethodSource("visitComponentHeadExpectedExceptionProvider")
  public void visitShouldTrowException(@Nullable ASTComponentHead ast,
                                       @NotNull Consumer<ArcBasisSymbolTableCompleter> setup,
                                       @NotNull Class<Exception> expected) {
    Preconditions.checkNotNull(setup);
    Preconditions.checkNotNull(expected);

    // Given
    setup.accept(this.getCompleter());

    // When && Then
    Assertions.assertThrows(expected, () -> this.getCompleter().visit(ast));
  }

  protected static Stream<Arguments> visitComponentHeadExpectedExceptionProvider() {
    ASTComponentHead ast1 = ArcBasisMill.componentHeadBuilder().build();
    ASTComponentHead ast2 = ArcBasisMill.componentHeadBuilder().build();
    ast2.setEnclosingScope(ArcBasisMill.scope());

    ASTComponentHead ast3 = ArcBasisMill.componentHeadBuilder().build();
    IArcBasisScope spannedBySymbol = ArcBasisMill.scope();
    TypeSymbol typeSym = ArcBasisMill.typeSymbolBuilder().setName("A")
      .setSpannedScope(spannedBySymbol).build();
    spannedBySymbol.setSpanningSymbol(typeSym);
    ast3.setEnclosingScope(spannedBySymbol);

    Consumer<ArcBasisSymbolTableCompleter> consumer1 = completer -> {};

    return Stream.of(
      Arguments.of(null, consumer1, NullPointerException.class),
      Arguments.of(ast1, consumer1, NullPointerException.class),
      Arguments.of(ast2, consumer1, IllegalArgumentException.class),
      Arguments.of(ast3, consumer1, IllegalArgumentException.class)
    );
  }

  @Test
  public void shouldVisitArcParameter() {
    // Given
    ASTMCType type = ArcBasisMill.mCPrimitiveTypeBuilder().setPrimitive(ASTConstantsMCBasicTypes.BYTE).build();
    type.setEnclosingScope(ArcBasisMill.globalScope());
    VariableSymbol symParam = ArcBasisMill.variableSymbolBuilder().setName("par").build();
    ASTArcParameter astParam = arcbasis.ArcBasisMill.arcParameterBuilder().setName("par")
      .setMCType(type).build();
    astParam.setSymbol(symParam);
    symParam.setAstNode(astParam);
    astParam.setEnclosingScope(ArcBasisMill.globalScope());
    symParam.setEnclosingScope(ArcBasisMill.globalScope());

    // When
    getCompleter().visit(astParam);

    // Then
    Assertions.assertNotNull(symParam.getType());
    Assertions.assertTrue(SymTypeExpressionFactory.createPrimitive("byte").deepEquals(symParam.getType()));
  }

  @Test
  public void shouldVisitPortDeclaration() {
    // Given
    ASTMCType type = createQualifiedType("some.Type");
    ASTPortDeclaration astPortDecl = arcbasis.ArcBasisMill.portDeclarationBuilder()
      .setMCType(type)
      .setIncoming(true)
      .addPort("mock").build();

    // When
    this.getCompleter().visit(astPortDecl);

    // Then
    Assertions.assertTrue(this.getCompleter().getCurrentPortType().isPresent());
    Assertions.assertEquals(type, this.getCompleter().getCurrentPortType().get());

  }

  @Test
  public void shouldEndVisitPortDeclaration() {
    // When
    ASTMCType type = createQualifiedType("some.Type");
    ASTPortDeclaration astPortDecl = arcbasis.ArcBasisMill.portDeclarationBuilder()
      .setMCType(type)
      .setIncoming(true)
      .addPort("mock").build();
    this.getCompleter().setCurrentPortType(type);

    // When
    this.getCompleter().endVisit(astPortDecl);

    // Then
    Assertions.assertFalse(this.getCompleter().getCurrentPortType().isPresent());
  }

  @Test
  public void shouldVisitPort() {
    // Given
    ASTMCType type = ArcBasisMill.mCPrimitiveTypeBuilder().setPrimitive(ASTConstantsMCBasicTypes.BYTE).build();
    type.setEnclosingScope(ArcBasisMill.globalScope());
    PortSymbol symParam = ArcBasisMill.portSymbolBuilder()
      .setName("po")
      .buildWithoutType();
    ASTPort astPort = ArcBasisMill.portBuilder().setName("po").build();
    astPort.setSymbol(symParam);
    symParam.setAstNode(astPort);
    astPort.setEnclosingScope(ArcBasisMill.globalScope());
    symParam.setEnclosingScope(ArcBasisMill.globalScope());

    this.getCompleter().setCurrentPortType(type);

    // When
    getCompleter().visit(astPort);

    // Then
    Assertions.assertDoesNotThrow(symParam::getType);
    Assertions.assertTrue(SymTypeExpressionFactory.createPrimitive("byte").deepEquals(symParam.getType()));
  }

  @Test
  public void shouldVisitFieldDeclaration() {
    // Given
    ASTMCType type = createQualifiedType("some.Type");
    ASTArcFieldDeclaration astFieldDecl = arcbasis.ArcBasisMill.arcFieldDeclarationBuilder()
      .setMCType(type)
      .addArcField("mock", Mockito.mock(ASTExpression.class))
      .build();

    // When
    this.getCompleter().visit(astFieldDecl);

    // Then
    Assertions.assertTrue(this.getCompleter().getCurrentFieldType().isPresent());
    Assertions.assertEquals(type, this.getCompleter().getCurrentFieldType().get());
  }

  @Test
  public void shouldEndVisitFieldDeclaration() {
    // Given
    ASTMCType type = createQualifiedType("some.Type");
    ASTArcFieldDeclaration astFieldDecl = arcbasis.ArcBasisMill.arcFieldDeclarationBuilder()
      .setMCType(type)
      .addArcField("mock", Mockito.mock(ASTExpression.class))
      .build();
    this.getCompleter().setCurrentFieldType(type);

    // When
    this.getCompleter().endVisit(astFieldDecl);

    Assertions.assertFalse(this.getCompleter().getCurrentFieldType().isPresent());
  }

  @Test
  public void shouldVisitArcField() {
    // Given
    ASTMCType type = ArcBasisMill.mCPrimitiveTypeBuilder().setPrimitive(ASTConstantsMCBasicTypes.BYTE).build();
    type.setEnclosingScope(ArcBasisMill.globalScope());
    VariableSymbol symField = ArcBasisMill.variableSymbolBuilder()
      .setName("po")
      .build();
    ASTArcField astField = ArcBasisMill.arcFieldBuilder().setName("po").uncheckedBuild();
    astField.setSymbol(symField);
    symField.setAstNode(astField);
    astField.setEnclosingScope(ArcBasisMill.globalScope());
    symField.setEnclosingScope(ArcBasisMill.globalScope());

    this.getCompleter().setCurrentFieldType(type);

    // When
    getCompleter().visit(astField);

    // Then
    Assertions.assertNotNull(symField.getType());
    Assertions.assertTrue(SymTypeExpressionFactory.createPrimitive("byte").deepEquals(symField.getType()));
  }

  @Test
  public void shouldVisitComponentInstantiation() {
    // Given
    ASTComponentInstantiation instantiation = provideComponentInstantiation();
    Optional<ComponentTypeSymbol> compTypeSym = ArcBasisMill.globalScope().resolveComponentType("Comp");
    if(compTypeSym.isEmpty()) {
      throw new IllegalStateException("We expect the component type 'Comp' to be added to the global scope by the " +
        "provider of the ASTComponentInstantiation.");
    }

    // When
    getCompleter().visit(instantiation);

    // Then
    Assertions.assertTrue(getCompleter().getCurrentCompInstanceType().isPresent());
    Assertions.assertTrue(getCompleter().getCurrentCompInstanceType().get() instanceof TypeExprOfComponent);
    Assertions.assertEquals(compTypeSym.get(), getCompleter().getCurrentCompInstanceType().get().getTypeInfo());
  }

  @Test
  public void shouldEndVisitComponentInstantiation() {
    // Given
    ASTComponentInstantiation instantiation = provideComponentInstantiation();
    Optional<ComponentTypeSymbol> compTypeSym = ArcBasisMill.globalScope().resolveComponentType("Comp");
    if(compTypeSym.isEmpty()) {
      throw new IllegalStateException("We expect the component type 'Comp' to be added to the global scope by the " +
        "provider of the ASTComponentInstantiation.");
    }
    getCompleter().setCurrentCompInstanceType(new TypeExprOfComponent(compTypeSym.get()));

    // When
    getCompleter().endVisit(instantiation);

    // Then
    Assertions.assertFalse(getCompleter().getCurrentCompInstanceType().isPresent());
  }

  @Test
  public void shouldVisitComponentInstance() {
    // Given
    ASTComponentInstance astInstance = ArcBasisMill.componentInstanceBuilder()
      .setName("inst").build();
    ComponentInstanceSymbol symInstance = ArcBasisMill.componentInstanceSymbolBuilder()
      .setName("inst").build();

    astInstance.setSymbol(symInstance);
    symInstance.setAstNode(astInstance);

    ComponentTypeSymbol compType = ArcBasisMill.componentTypeSymbolBuilder()
      .setName("CompType")
      .setSpannedScope(ArcBasisMill.scope())
      .build();
    getCompleter().setCurrentCompInstanceType(new TypeExprOfComponent(compType));

    // When
    getCompleter().visit(astInstance);

    // Then
    Assertions.assertDoesNotThrow(astInstance.getSymbol()::getType);
    Assertions.assertTrue(astInstance.getSymbol().getType() instanceof TypeExprOfComponent);
    Assertions.assertEquals(compType, astInstance.getSymbol().getType().getTypeInfo());
  }

  @Test
  public void shouldHandleComponentInstantiationByCompletingComponentInstanceSymbols() {
    // Given
    ASTComponentInstantiation astInstantiation = provideComponentInstantiation();
    Preconditions.checkState(astInstantiation.getComponentInstanceList().size() > 0);
    Optional<ComponentTypeSymbol> compTypeSym = ArcBasisMill.globalScope().resolveComponentType("Comp");
    if(compTypeSym.isEmpty()) {
      throw new IllegalStateException("We expect the component type 'Comp' to be added to the global scope by the " +
        "provider of the ASTComponentInstantiation.");
    }
    // Attach a traverser to the completer, so that he can perfrom the whole handling process
    ArcBasisTraverser traverser = ArcBasisMill.traverser();
    traverser.setArcBasisHandler(getCompleter());
    traverser.add4ArcBasis(getCompleter());

    // When
    getCompleter().handle(astInstantiation);

    // Then
    for (ASTComponentInstance astInst : astInstantiation.getComponentInstanceList()) {
      Assertions.assertDoesNotThrow(astInst.getSymbol()::getType);
      Assertions.assertTrue(astInst.getSymbol().getType() instanceof TypeExprOfComponent);
      Assertions.assertEquals(compTypeSym.get(), astInst.getSymbol().getType().getTypeInfo());
    }
  }

  /**
   * Adds a component type symbol "Comp" to the global scope and creates an ASTComponentInstantiation
   * {@code Comp inst1, inst2;} where all AST elements are enclosed by the global scope. Also adds symbols for the
   * instances (inst1 and inst2) to the global scope. The types of the instances are not set yet though.
   */
  protected ASTComponentInstantiation provideComponentInstantiation() {
    String compName = "Comp";
    ComponentTypeSymbol compTypeSym = ArcBasisMill.componentTypeSymbolBuilder()
      .setName(compName)
      .setSpannedScope(ArcBasisMill.scope())
      .build();

    ASTMCType astCompType = createQualifiedType(compName);
    ASTComponentInstantiation instantiation = ArcBasisMill.componentInstantiationBuilder()
      .setMCType(astCompType)
      .addInstance("inst1")
      .addInstance("inst2")
      .build();

    ArcBasisMill.globalScope().add(compTypeSym);
    astCompType.setEnclosingScope(ArcBasisMill.globalScope());
    instantiation.setEnclosingScope(ArcBasisMill.globalScope());

    for(ASTComponentInstance astInst : instantiation.getComponentInstanceList()) {
      ComponentInstanceSymbol instSym = ArcBasisMill.componentInstanceSymbolBuilder()
        .setName(astInst.getName())
        .build();
      instSym.setAstNode(astInst);
      astInst.setSymbol(instSym);
      astInst.setEnclosingScope(ArcBasisMill.globalScope());
      ArcBasisMill.globalScope().add(instSym);
    }

    return instantiation;
  }
}