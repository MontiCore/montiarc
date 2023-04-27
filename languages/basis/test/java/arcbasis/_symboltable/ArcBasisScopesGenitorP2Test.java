/* (c) https://github.com/MontiCore/monticore */
package arcbasis._symboltable;

import arcbasis.ArcBasisAbstractTest;
import arcbasis.ArcBasisMill;
import arcbasis._ast.*;
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
import de.se_rwth.commons.logging.Log;
import montiarc.Timing;
import montiarc.util.ArcError;
import org.codehaus.commons.nullanalysis.NotNull;
import org.codehaus.commons.nullanalysis.Nullable;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.params.provider.MethodSource;
import org.junit.jupiter.params.provider.NullSource;
import org.mockito.Mockito;

import java.util.Arrays;
import java.util.Optional;
import java.util.function.Consumer;
import java.util.stream.Stream;

/**
 * Holds tests for {@link ArcBasisScopesGenitorP2}.
 */
public class ArcBasisScopesGenitorP2Test extends ArcBasisAbstractTest {

  protected ArcBasisScopesGenitorP2 scopeGenP2;

  protected ArcBasisScopesGenitorP2 getScopeGenP2() {
    return this.scopeGenP2;
  }

  protected void setUpCompleter() {
    this.scopeGenP2 = ArcBasisMill.scopesGenitorP2();
  }

  @BeforeEach
  @Override
  public void setUp() {
    super.setUp();
    this.setUpCompleter();
  }

  /**
   * Method under test {@link ArcBasisScopesGenitorP2#setTraverser(ArcBasisTraverser)}
   */
  @Test
  public void shouldSetTraverser() {
    // Given
    ArcBasisTraverser traverser = ArcBasisMill.traverser();

    // When
    this.getScopeGenP2().setTraverser(traverser);

    // Then
    Assertions.assertEquals(traverser, this.getScopeGenP2().getTraverser());
  }

  /**
   * Method under test {@link ArcBasisScopesGenitorP2#setTraverser(ArcBasisTraverser)}
   *
   * @param traverser the traverser to set (null)
   */
  @ParameterizedTest
  @NullSource
  public void setTraverserShouldThrowNullPointerException(@Nullable ArcBasisTraverser traverser) {
    // When && Then
    Assertions.assertThrows(NullPointerException.class, () -> this.getScopeGenP2().setTraverser(traverser));
  }

  /**
   * Method under test {@link ArcBasisScopesGenitorP2#visit(ASTComponentType)}
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
    Assertions.assertThrows(expected, () -> this.getScopeGenP2().visit(ast));
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
   * Method under test {@link ArcBasisScopesGenitorP2#visit(ASTComponentType)}
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
    this.getScopeGenP2().visit(ast);

    //Then
    Assertions.assertTrue(this.getScopeGenP2().getCurrentCompInstanceType().isEmpty());
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
    this.getScopeGenP2().visit(ast);

    //Then
    Assertions.assertTrue(this.getScopeGenP2().getCurrentCompInstanceType().isPresent());
    Assertions.assertTrue(this.getScopeGenP2().getCurrentCompInstanceType().get() instanceof TypeExprOfComponent);
    Assertions.assertEquals(symbol, this.getScopeGenP2().getCurrentCompInstanceType().get().getTypeInfo());
  }

  /**
   * Method under test {@link ArcBasisScopesGenitorP2#endVisit(ASTComponentType)}
   *
   * @param ast      the ast to visit
   * @param setup    the setup to execute beforehand
   * @param expected the expected exception
   */
  @ParameterizedTest
  @MethodSource("endVisitComponentTypeExpectedExceptionProvider")
  public void endVisitShouldTrowException(@Nullable ASTComponentType ast,
                                          @NotNull Consumer<ArcBasisScopesGenitorP2> setup,
                                          @NotNull Class<Exception> expected) {
    Preconditions.checkNotNull(setup);
    Preconditions.checkNotNull(expected);

    // Given
    setup.accept(this.getScopeGenP2());

    // When && Then
    Assertions.assertThrows(expected, () -> this.getScopeGenP2().endVisit(ast));
  }

  public static Stream<Arguments> endVisitComponentTypeExpectedExceptionProvider() {
    ASTComponentType ast1 = ArcBasisMill.componentTypeBuilder().setName("SomeName")
      .setHead(Mockito.mock(ASTComponentHead.class))
      .setBody(Mockito.mock(ASTComponentBody.class))
      .build();
    ast1.setSymbolAbsent();

    Consumer<ArcBasisScopesGenitorP2> consumer1 = scopesGen -> {
    };

    return Stream.of(
      Arguments.of(null, consumer1, NullPointerException.class),
      Arguments.of(ast1, consumer1, IllegalArgumentException.class)
    );
  }

  /**
   * Method under test {@link ArcBasisScopesGenitorP2#endVisit(ASTComponentType)}
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

    this.getScopeGenP2().setCurrentCompInstanceType(new TypeExprOfComponent(ast.getSymbol()));

    // When
    this.getScopeGenP2().endVisit(ast);

    // Then
    Assertions.assertFalse(this.getScopeGenP2().getCurrentCompInstanceType().isPresent());
  }

  /**
   * If the component has no parent declaration, visiting the component head
   * should not change the state of the ast node or the state of the symbol.
   * <p>
   * Method under test {@link ArcBasisScopesGenitorP2#visit(ASTComponentHead)}
   */
  @Test
  public void testVisitComponentHead1() {
    // Given
    ComponentTypeSymbol symbol = ArcBasisMill.componentTypeSymbolBuilder()
      .setName("SomeName")
      .setSpannedScope(ArcBasisMill.scope())
      .build();

    ASTComponentHead ast = ArcBasisMill.componentHeadBuilder().build();
    ast.setEnclosingScope(symbol.getSpannedScope());
    ast.setParentAbsent();

    // When
    this.getScopeGenP2().visit(ast);

    // Then
    Assertions.assertAll(
      () -> Assertions.assertFalse(ast.isPresentParent()),
      () -> Assertions.assertFalse(symbol.isPresentParent())
    );
  }

  /**
   * If the component has a parent declaration and the parent is uniquely
   * resolvable, visiting the component head should link the component
   * symbol and the ast node with the symbol representing the parent.
   * <p>
   * Method under test {@link ArcBasisScopesGenitorP2#visit(ASTComponentHead)}
   */
  @Test
  public void testVisitComponentHead2() {
    // Given
    String parentCompName = "ParentComp";
    ComponentTypeSymbol parent = ArcBasisMill.componentTypeSymbolBuilder()
      .setName(parentCompName)
      .setSpannedScope(Mockito.mock(IArcBasisScope.class))
      .build();

    String childCompName = "ChildComp";
    ComponentTypeSymbol child = ArcBasisMill.componentTypeSymbolBuilder()
      .setName(childCompName)
      .setSpannedScope(ArcBasisMill.scope())
      .build();

    ASTComponentHead head = ArcBasisMill.componentHeadBuilder()
      .setParent(createQualifiedType(parentCompName))
      .build();

    ASTComponentType comp = ArcBasisMill.componentTypeBuilder()
      .setName(childCompName)
      .setBody(Mockito.mock(ASTComponentBody.class))
      .setHead(head)
      .build();

    ArcBasisMill.globalScope().add(parent);
    ArcBasisMill.globalScope().add(child);
    parent.getSpannedScope().setEnclosingScope(ArcBasisMill.globalScope());
    child.getSpannedScope().setEnclosingScope(ArcBasisMill.globalScope());

    child.setAstNode(comp);
    comp.setSymbol(child);
    comp.setEnclosingScope(ArcBasisMill.globalScope());
    comp.setSpannedScope(child.getSpannedScope());
    head.setEnclosingScope(child.getSpannedScope());
    head.getParent().setEnclosingScope(child.getSpannedScope());
    ((ASTMCQualifiedType) head.getParent()).getMCQualifiedName().setEnclosingScope(child.getSpannedScope());

    // When
    getScopeGenP2().visit(head);

    // Then
    Assertions.assertAll(
      () -> Assertions.assertTrue(child.isPresentParent()),
      () -> Assertions.assertTrue(head.getParent().getDefiningSymbol().isPresent())
    );
    Assertions.assertAll(
      () -> Assertions.assertEquals(parent, child.getParent().getTypeInfo()),
      () -> Assertions.assertEquals(parent, head.getParent().getDefiningSymbol().get())
    );
  }

  /**
   * If the component has a parent declaration but the parent is not
   * resolvable, visiting the component head should report a missing
   * component symbol.
   * <p>
   * Method under test {@link ArcBasisScopesGenitorP2#visit(ASTComponentHead)}
   */
  @Test
  public void testVisitComponentHead3() {
    // Given
    String childCompName = "ChildComp";
    ComponentTypeSymbol child = ArcBasisMill.componentTypeSymbolBuilder()
      .setName(childCompName)
      .setSpannedScope(ArcBasisMill.scope())
      .build();

    ASTComponentHead head = ArcBasisMill.componentHeadBuilder()
      .setParent(createQualifiedType("ParentComp"))
      .build();

    ASTComponentType comp = ArcBasisMill.componentTypeBuilder()
      .setName(childCompName)
      .setBody(Mockito.mock(ASTComponentBody.class))
      .setHead(head)
      .build();

    ArcBasisMill.globalScope().add(child);
    child.getSpannedScope().setEnclosingScope(ArcBasisMill.globalScope());

    child.setAstNode(comp);
    comp.setSymbol(child);
    comp.setEnclosingScope(ArcBasisMill.globalScope());
    comp.setSpannedScope(child.getSpannedScope());
    head.setEnclosingScope(child.getSpannedScope());
    head.getParent().setEnclosingScope(child.getSpannedScope());
    ((ASTMCQualifiedType) head.getParent()).getMCQualifiedName().setEnclosingScope(child.getSpannedScope());

    // When
    getScopeGenP2().visit(head);

    // Then
    Assertions.assertAll(
      () -> Assertions.assertFalse(child.isPresentParent()),
      () -> Assertions.assertFalse(head.getParent().getDefiningSymbol().isPresent()),
      () -> Assertions.assertEquals(1, Log.getErrorCount()),
      () -> Assertions.assertEquals(ArcError.MISSING_COMPONENT.getErrorCode(), Log.getFindings().get(0).getMsg().substring(0, 7))
    );
  }

  /**
   * If the component has a parent declaration but the parent is ambiguously
   * referenced, visiting the component head should report an ambiguous symbol
   * reference.
   * <p>
   * Method under test {@link ArcBasisScopesGenitorP2#visit(ASTComponentHead)}
   */
  @Test
  public void testVisitComponentHead4() {
    // Given
    String parentCompName = "ParentComp";
    ComponentTypeSymbol ref1 = ArcBasisMill.componentTypeSymbolBuilder()
      .setName(parentCompName)
      .setSpannedScope(Mockito.mock(IArcBasisScope.class))
      .build();
    ComponentTypeSymbol ref2 = ArcBasisMill.componentTypeSymbolBuilder()
      .setName(parentCompName)
      .setSpannedScope(Mockito.mock(IArcBasisScope.class))
      .build();

    String childCompName = "ChildComp";
    ComponentTypeSymbol child = ArcBasisMill.componentTypeSymbolBuilder()
      .setName(childCompName)
      .setSpannedScope(ArcBasisMill.scope())
      .build();

    ASTComponentHead head = ArcBasisMill.componentHeadBuilder()
      .setParent(createQualifiedType(parentCompName))
      .build();

    ASTComponentType comp = ArcBasisMill.componentTypeBuilder()
      .setName(childCompName)
      .setBody(Mockito.mock(ASTComponentBody.class))
      .setHead(head)
      .build();

    ArcBasisMill.globalScope().add(ref1);
    ArcBasisMill.globalScope().add(ref2);
    ArcBasisMill.globalScope().add(child);
    ref1.getSpannedScope().setEnclosingScope(ArcBasisMill.globalScope());
    ref2.getSpannedScope().setEnclosingScope(ArcBasisMill.globalScope());
    child.getSpannedScope().setEnclosingScope(ArcBasisMill.globalScope());

    child.setAstNode(comp);
    comp.setSymbol(child);
    comp.setEnclosingScope(ArcBasisMill.globalScope());
    comp.setSpannedScope(child.getSpannedScope());
    head.setEnclosingScope(child.getSpannedScope());
    head.getParent().setEnclosingScope(child.getSpannedScope());
    ((ASTMCQualifiedType) head.getParent()).getMCQualifiedName().setEnclosingScope(child.getSpannedScope());

    // When
    getScopeGenP2().visit(head);

    // Then
    Assertions.assertAll(
      () -> Assertions.assertEquals(1, Log.getErrorCount())
    );
  }

  /**
   * Method under test {@link ArcBasisScopesGenitorP2#visit(ASTComponentHead)}
   *
   * @param ast      the ast to visit
   * @param setup    the setup to execute beforehand
   * @param expected the expected exception
   */
  @ParameterizedTest
  @MethodSource("visitComponentHeadExpectedExceptionProvider")
  public void testVisitComponentHead5(@Nullable ASTComponentHead ast,
                                      @NotNull Consumer<ArcBasisScopesGenitorP2> setup,
                                      @NotNull Class<Exception> expected) {
    Preconditions.checkNotNull(setup);
    Preconditions.checkNotNull(expected);

    // Given
    setup.accept(this.getScopeGenP2());

    // When && Then
    Assertions.assertThrows(expected, () -> this.getScopeGenP2().visit(ast));
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

    Consumer<ArcBasisScopesGenitorP2> consumer1 = scopesGenP2 -> {
    };

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
    getScopeGenP2().visit(astParam);

    // Then
    Assertions.assertNotNull(symParam.getType());
    Assertions.assertTrue(SymTypeExpressionFactory.createPrimitive("byte").deepEquals(symParam.getType()));
  }

  /**
   * If the scopes genitor p2 visits a port declaration with a single
   * port and resolvable type, then it should update the port's symbol
   * with its type, direction, timing, and delay.
   * The method under test is {@link ArcBasisScopesGenitorP2#visit(ASTPortDeclaration).
   */
  @ParameterizedTest
  @CsvSource(value = {
    "0, false, null",
    "0, true, null",
    "0, false, true",
    "0, false, null",
    "1, false, null",
    "1, true, null",
    "1, false, true",
    "1, false, null",
    "2, false, null",
    "2, true, null",
    "2, false, true",
    "2, false, null"
  })
  public void testVisitPortDeclaration1(@Nullable short tid, boolean i, Boolean d) {
    // Given
    Timing t = tid == 0 ? Timing.TIMED_SYNC : tid == 1 ? Timing.TIMED : Timing.UNTIMED;
    ASTPortDeclaration ast = arcbasis.ArcBasisMill.portDeclarationBuilder()
      .setMCType(ArcBasisMill.mCQualifiedTypeBuilder()
        .setMCQualifiedName(ArcBasisMill.mCQualifiedNameBuilder()
          .addParts("a")
          .addParts("b")
          .addParts("X")
          .build())
        .build())
      .setIncoming(i)
      .setStereotype(ArcBasisMill.stereotypeBuilder()
        .addValues(ArcBasisMill.stereoValueBuilder()
          .setName(t.getName())
          .setContent("")
          .build())
        .build())
      .addPort("p")
      .build();
    if (d) {
      ast.getStereotype().addValues(ArcBasisMill.stereoValueBuilder()
        .setName(ASTPortDeclaration.DELAY)
        .setContent("")
        .build()
      );
    }

    IArcBasisArtifactScope as = ArcBasisMill.artifactScope();
    as.add(ArcBasisMill.typeSymbolBuilder()
      .setPackageName("a.b").setName("X")
      .setEnclosingScope(as)
      .setSpannedScope(ArcBasisMill.scope())
      .build());
    ArcBasisMill.globalScope().addSubScope(as);

    ast.accept(ArcBasisMill.scopesGenitorDelegator().traverser);

    // When
    this.getScopeGenP2().visit(ast);

    // Then
    Assertions.assertAll(
      () -> Assertions.assertEquals("a.b.X", ast.getPort(0).getSymbol().getType().printFullName()),
      () -> Assertions.assertEquals(t, ast.getPort(0).getSymbol().getTiming()),
      () -> Assertions.assertEquals(i, ast.getPort(0).getSymbol().isIncoming()),
      () -> Assertions.assertEquals(d, ast.getPort(0).getSymbol().getDelayed()),
      () -> Assertions.assertEquals(0, Log.getFindingsCount(), Log.getFindings().toString())
    );
  }

  /**
   * If the scopes genitor p2 visits a port declaration with multiple
   * ports and resolvable type, then it should update the ports' symbols
   * with their type, direction, timing, and delay.
   * The method under test is {@link ArcBasisScopesGenitorP2#visit(ASTPortDeclaration).
   */
  @ParameterizedTest
  @CsvSource(value = {
    "0, false, null",
    "0, true, null",
    "0, false, true",
    "0, false, null",
    "1, false, null",
    "1, true, null",
    "1, false, true",
    "1, false, null",
    "2, false, null",
    "2, true, null",
    "2, false, true",
    "2, false, null"
  })
  public void testVisitPortDeclaration2(@Nullable short tid, boolean i, Boolean d) {
    // Given
    Timing t = tid == 0 ? Timing.TIMED_SYNC : tid == 1 ? Timing.TIMED : Timing.UNTIMED;
    ASTPortDeclaration ast = arcbasis.ArcBasisMill.portDeclarationBuilder()
      .setMCType(ArcBasisMill.mCQualifiedTypeBuilder()
        .setMCQualifiedName(ArcBasisMill.mCQualifiedNameBuilder()
          .addParts("a")
          .addParts("b")
          .addParts("Y")
          .build())
        .build())
      .setIncoming(i)
      .setStereotype(ArcBasisMill.stereotypeBuilder()
        .addValues(ArcBasisMill.stereoValueBuilder()
          .setName(t.getName())
          .setContent("")
          .build())
        .build())
      .addPort("p1")
      .addPort("p2")
      .build();
    if (d) {
      ast.getStereotype().addValues(ArcBasisMill.stereoValueBuilder()
        .setName(ASTPortDeclaration.DELAY)
        .setContent("")
        .build()
      );
    }

    IArcBasisArtifactScope as = ArcBasisMill.artifactScope();
    as.add(ArcBasisMill.typeSymbolBuilder()
      .setPackageName("a.b").setName("Y")
      .setEnclosingScope(as)
      .setSpannedScope(ArcBasisMill.scope())
      .build());
    ArcBasisMill.globalScope().addSubScope(as);

    ast.accept(ArcBasisMill.scopesGenitorDelegator().traverser);

    // When
    this.getScopeGenP2().visit(ast);

    // Then
    Assertions.assertAll(
      () -> Assertions.assertEquals("a.b.Y", ast.getPort(0).getSymbol().getType().printFullName()),
      () -> Assertions.assertEquals("a.b.Y", ast.getPort(1).getSymbol().getType().printFullName()),
      () -> Assertions.assertEquals(t, ast.getPort(0).getSymbol().getTiming()),
      () -> Assertions.assertEquals(t, ast.getPort(1).getSymbol().getTiming()),
      () -> Assertions.assertEquals(i, ast.getPort(0).getSymbol().isIncoming()),
      () -> Assertions.assertEquals(i, ast.getPort(1).getSymbol().isIncoming()),
      () -> Assertions.assertEquals(d, ast.getPort(0).getSymbol().isDelayed()),
      () -> Assertions.assertEquals(d, ast.getPort(1).getSymbol().isDelayed()),
      () -> Assertions.assertEquals(0, Log.getFindingsCount(), Log.getFindings().toString())
    );
  }

  /**
   * If the scopes genitor p2 visits a port declaration with a single
   * port but non-resolvable type, then it should update the ports' symbols
   * with their direction, timing, and delay and log an error for the
   * missing type.
   * The method under test is {@link ArcBasisScopesGenitorP2#visit(ASTPortDeclaration).
   */
  @ParameterizedTest
  @CsvSource(value = {
    "0, false, null",
    "0, true, null",
    "0, false, true",
    "0, false, null",
    "1, false, null",
    "1, true, null",
    "1, false, true",
    "1, false, null",
    "2, false, null",
    "2, true, null",
    "2, false, true",
    "2, false, null"
  })
  public void testVisitPortDeclaration3(@Nullable short tid, boolean i, Boolean d) {
    // Given
    Timing t = tid == 0 ? Timing.TIMED_SYNC : tid == 1 ? Timing.TIMED : Timing.UNTIMED;
    ASTPortDeclaration ast = arcbasis.ArcBasisMill.portDeclarationBuilder()
      .setMCType(ArcBasisMill.mCQualifiedTypeBuilder()
        .setMCQualifiedName(ArcBasisMill.mCQualifiedNameBuilder()
          .addParts("a")
          .addParts("b")
          .addParts("Z")
          .build())
        .build())
      .setIncoming(i)
      .setStereotype(ArcBasisMill.stereotypeBuilder()
        .addValues(ArcBasisMill.stereoValueBuilder()
          .setName(t.getName())
          .setContent("")
          .build())
        .build())
      .addPort("p")
      .build();
    if (d) {
      ast.getStereotype().addValues(ArcBasisMill.stereoValueBuilder()
        .setName(ASTPortDeclaration.DELAY)
        .setContent("")
        .build()
      );
    }

    ast.accept(ArcBasisMill.scopesGenitorDelegator().traverser);

    // When
    this.getScopeGenP2().visit(ast);

    // Then
    Assertions.assertAll(
      () -> Assertions.assertTrue(ast.getPort(0).getSymbol().getType().isObscureType()),
      () -> Assertions.assertEquals(t, ast.getPort(0).getSymbol().getTiming()),
      () -> Assertions.assertEquals(i, ast.getPort(0).getSymbol().isIncoming()),
      () -> Assertions.assertEquals(d, ast.getPort(0).getSymbol().isDelayed()),
      () -> Assertions.assertEquals(1, Log.getFindingsCount(), Log.getFindings().toString())
    );
  }

  /**
   * If the scopes genitor p2 visits a field declaration with a single
   * field and resolvable type, then it should update the field's symbol
   * with its type.
   * The method under test is {@link ArcBasisScopesGenitorP2#visit(ASTArcFieldDeclaration).
   */
  @Test
  public void testVisitArcFieldDeclaration1() {
    // Given
    ASTArcFieldDeclaration ast = ArcBasisMill.arcFieldDeclarationBuilder()
      .setMCType(ArcBasisMill.mCQualifiedTypeBuilder()
        .setMCQualifiedName(ArcBasisMill.mCQualifiedNameBuilder()
          .addParts("a")
          .addParts("b")
          .addParts("X")
          .build())
        .build())
      .addArcField(ArcBasisMill.arcFieldBuilder()
        .setName("v")
        .setInitial(Mockito.mock(ASTExpression.class))
        .build())
      .build();

    IArcBasisArtifactScope as = ArcBasisMill.artifactScope();
    as.add(ArcBasisMill.typeSymbolBuilder()
      .setPackageName("a.b").setName("X")
      .setEnclosingScope(as)
      .setSpannedScope(ArcBasisMill.scope())
      .build());
    ArcBasisMill.globalScope().addSubScope(as);

    ast.accept(ArcBasisMill.scopesGenitorDelegator().traverser);

    // When
    this.getScopeGenP2().visit(ast);

    // Then
    Assertions.assertAll(
      () -> Assertions.assertEquals("a.b.X", ast.getArcField(0).getSymbol().getType().printFullName()),
      () -> Assertions.assertEquals(0, Log.getFindingsCount(), Log.getFindings().toString())
    );
  }

  /**
   * If the scopes genitor p2 visits a field declaration with multiple
   * fields and resolvable type, then it should update the fields' symbols
   * with its type.
   * The method under test is {@link ArcBasisScopesGenitorP2#visit(ASTArcFieldDeclaration).
   */
  @Test
  public void testVisitArcFieldDeclaration2() {
    // Given
    ASTArcFieldDeclaration ast = arcbasis.ArcBasisMill.arcFieldDeclarationBuilder()
      .setMCType(ArcBasisMill.mCQualifiedTypeBuilder()
        .setMCQualifiedName(ArcBasisMill.mCQualifiedNameBuilder()
          .addParts("a")
          .addParts("b")
          .addParts("Y")
          .build())
        .build())
      .addArcField(ArcBasisMill.arcFieldBuilder()
        .setName("v1")
        .setInitial(Mockito.mock(ASTExpression.class))
        .build())
      .addArcField(ArcBasisMill.arcFieldBuilder()
        .setName("v2")
        .setInitial(Mockito.mock(ASTExpression.class))
        .build())
      .build();

    IArcBasisArtifactScope as = ArcBasisMill.artifactScope();
    as.add(ArcBasisMill.typeSymbolBuilder()
      .setPackageName("a.b").setName("Y")
      .setEnclosingScope(as)
      .setSpannedScope(ArcBasisMill.scope())
      .build());
    ArcBasisMill.globalScope().addSubScope(as);

    ast.accept(ArcBasisMill.scopesGenitorDelegator().traverser);

    // When
    this.getScopeGenP2().visit(ast);

    // Then
    Assertions.assertAll(
      () -> Assertions.assertEquals("a.b.Y", ast.getArcField(0).getSymbol().getType().printFullName()),
      () -> Assertions.assertEquals("a.b.Y", ast.getArcField(1).getSymbol().getType().printFullName()),
      () -> Assertions.assertEquals(0, Log.getFindingsCount(), Log.getFindings().toString())
    );
  }

  /**
   * If the scopes genitor p2 visits a field declaration with a single
   * field and resolvable type, then it should update the field's symbol
   * with its type.
   * The method under test is {@link ArcBasisScopesGenitorP2#visit(ASTArcFieldDeclaration).
   */
  @Test
  public void testVisitArcFieldDeclaration3() {
    // Given
    ASTArcFieldDeclaration ast = arcbasis.ArcBasisMill.arcFieldDeclarationBuilder()
      .setMCType(ArcBasisMill.mCQualifiedTypeBuilder()
        .setMCQualifiedName(ArcBasisMill.mCQualifiedNameBuilder()
          .addParts("a")
          .addParts("b")
          .addParts("Z")
          .build())
        .build())
      .addArcField(ArcBasisMill.arcFieldBuilder()
        .setName("v")
        .setInitial(Mockito.mock(ASTExpression.class))
        .build())
      .build();

    ast.accept(ArcBasisMill.scopesGenitorDelegator().traverser);

    // When
    this.getScopeGenP2().visit(ast);

    // Then
    Assertions.assertAll(
      () -> Assertions.assertTrue(ast.getArcField(0).getSymbol().getType().isObscureType()),
      () -> Assertions.assertEquals(1, Log.getFindingsCount(), Log.getFindings().toString())
    );
  }

  /**
   * Method under test {@link ArcBasisScopesGenitorP2#visit(ASTComponentInstantiation)
   */
  @Test
  public void shouldVisitComponentInstantiation() {
    // Given
    ASTComponentInstantiation ast = provideComponentInstantiation();
    ComponentTypeSymbol comp = ArcBasisMill.globalScope().resolveComponentType("Comp").orElseThrow();

    // When
    getScopeGenP2().visit(ast);

    // Then
    Assertions.assertTrue(getScopeGenP2().getCurrentCompInstanceType().isPresent());
    Assertions.assertEquals(comp, getScopeGenP2().getCurrentCompInstanceType().get().getTypeInfo());
    Assertions.assertTrue(ast.getMCType().getDefiningSymbol().isPresent());
    Assertions.assertEquals(ast.getMCType().getDefiningSymbol().get(), comp);
  }

  @Test
  public void shouldEndVisitComponentInstantiation() {
    // Given
    ASTComponentInstantiation instantiation = provideComponentInstantiation();
    Optional<ComponentTypeSymbol> compTypeSym = ArcBasisMill.globalScope().resolveComponentType("Comp");
    if (compTypeSym.isEmpty()) {
      throw new IllegalStateException("We expect the component type 'Comp' to be added to the global scope by the " +
        "provider of the ASTComponentInstantiation.");
    }
    getScopeGenP2().setCurrentCompInstanceType(new TypeExprOfComponent(compTypeSym.get()));

    // When
    getScopeGenP2().endVisit(instantiation);

    // Then
    Assertions.assertFalse(getScopeGenP2().getCurrentCompInstanceType().isPresent());
  }

  @Test
  public void shouldVisitComponentInstance() {
    // Given
    ASTComponentInstance astInstance = ArcBasisMill.componentInstanceBuilder()
      .setName("inst").setArcArguments(arcbasis.ArcBasisMill.arcArgumentsBuilder().setArcArgumentsList(
        Arrays.asList(this.argumentMockValues(3))).build()
      ).build();
    ComponentInstanceSymbol symInstance = ArcBasisMill.componentInstanceSymbolBuilder()
      .setName("inst").build();

    astInstance.setSymbol(symInstance);
    symInstance.setAstNode(astInstance);

    ComponentTypeSymbol compType = ArcBasisMill.componentTypeSymbolBuilder()
      .setName("CompType")
      .setSpannedScope(ArcBasisMill.scope())
      .build();
    getScopeGenP2().setCurrentCompInstanceType(new TypeExprOfComponent(compType));

    // When
    getScopeGenP2().visit(astInstance);

    // Then
    Assertions.assertDoesNotThrow(astInstance.getSymbol()::getType);
    Assertions.assertTrue(astInstance.getSymbol().getType() instanceof TypeExprOfComponent);
    Assertions.assertEquals(compType, astInstance.getSymbol().getType().getTypeInfo());
    Assertions.assertEquals(3, astInstance.getSymbol().getType().getArcArguments().size());
  }

  protected ASTArcArgument[] argumentMockValues(int length) {
    ASTArcArgument[] values = new ASTArcArgument[length];
    for (int i = 0; i < length; i++) {
      values[i] = Mockito.mock(ASTArcArgument.class);
      values[i].setExpression(Mockito.mock(ASTExpression.class));
    }
    return values;
  }
  @Test
  public void shouldHandleComponentInstantiationByCompletingComponentInstanceSymbols() {
    // Given
    ASTComponentInstantiation astInstantiation = provideComponentInstantiation();
    Preconditions.checkState(astInstantiation.getComponentInstanceList().size() > 0);
    Optional<ComponentTypeSymbol> compTypeSym = ArcBasisMill.globalScope().resolveComponentType("Comp");
    if (compTypeSym.isEmpty()) {
      throw new IllegalStateException("We expect the component type 'Comp' to be added to the global scope by the " +
        "provider of the ASTComponentInstantiation.");
    }
    ArcBasisTraverser traverser = ArcBasisMill.traverser();
    traverser.setArcBasisHandler(getScopeGenP2());
    traverser.add4ArcBasis(getScopeGenP2());

    // When
    getScopeGenP2().handle(astInstantiation);

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

    for (ASTComponentInstance astInst : instantiation.getComponentInstanceList()) {
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