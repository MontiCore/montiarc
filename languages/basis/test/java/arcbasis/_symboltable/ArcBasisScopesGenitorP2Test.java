/* (c) https://github.com/MontiCore/monticore */
package arcbasis._symboltable;

import arcbasis.ArcBasisMill;
import arcbasis.ArcBasisTestBase;
import arcbasis._ast.ASTArcArgument;
import arcbasis._ast.ASTArcComponentType;
import arcbasis._ast.ASTArcFieldDeclaration;
import arcbasis._ast.ASTArcParameter;
import arcbasis._ast.ASTArcParent;
import arcbasis._ast.ASTComponentBody;
import arcbasis._ast.ASTComponentHead;
import arcbasis._ast.ASTComponentInstance;
import arcbasis._ast.ASTComponentInstantiation;
import arcbasis._ast.ASTPortDeclaration;
import arcbasis._visitor.ArcBasisTraverser;
import arcbasis.check.TypeExprOfComponent;
import com.google.common.base.Preconditions;
import de.monticore.expressions.expressionsbasis._ast.ASTExpression;
import de.monticore.symbols.basicsymbols._symboltable.TypeSymbol;
import de.monticore.symbols.basicsymbols._symboltable.TypeVarSymbol;
import de.monticore.symbols.basicsymbols._symboltable.VariableSymbol;
import de.monticore.symbols.compsymbols._symboltable.SubcomponentSymbol;
import de.monticore.symbols.compsymbols._symboltable.Timing;
import de.monticore.types.check.SymTypeExpression;
import de.monticore.types.check.SymTypeExpressionFactory;
import de.monticore.types.mcbasictypes._ast.ASTConstantsMCBasicTypes;
import de.monticore.types.mcbasictypes._ast.ASTMCQualifiedName;
import de.monticore.types.mcbasictypes._ast.ASTMCQualifiedType;
import de.monticore.types.mcbasictypes._ast.ASTMCType;
import de.monticore.types.typeparameters._ast.ASTTypeParameter;
import de.se_rwth.commons.logging.Log;
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
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.function.Consumer;
import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Holds tests for {@link ArcBasisScopesGenitorP2}.
 */
public class ArcBasisScopesGenitorP2Test extends ArcBasisTestBase {

  protected ArcBasisScopesGenitorP2 scopeGenP2;

  protected ArcBasisScopesGenitorP2 getScopeGenP2() {
    return this.scopeGenP2;
  }

  protected void setUpCompleter() {
    this.scopeGenP2 = ArcBasisMill.scopesGenitorP2();
  }

  @BeforeEach
  public void setUp() {
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
   * If the component has no parent declaration, visiting the component head
   * should not change the state of the ast node or the state of the symbol.
   * <p>
   * Method under test {@link ArcBasisScopesGenitorP2#visit(ASTComponentHead)}
   */
  @Test
  public void testVisitComponentHead1() {
    // Given
    ArcComponentTypeSymbol symbol = ArcBasisMill.arcComponentTypeSymbolBuilder()
      .setName("SomeName")
      .setSpannedScope(ArcBasisMill.scope())
      .build();

    ASTComponentHead ast = ArcBasisMill.componentHeadBuilder().build();
    ast.setEnclosingScope(symbol.getSpannedScope());

    // When
    this.getScopeGenP2().visit(ast);

    // Then
    Assertions.assertAll(
      () -> Assertions.assertTrue(ast.isEmptyArcParents()),
      () -> Assertions.assertTrue(symbol.isEmptySuperComponents())
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
    ArcComponentTypeSymbol parent = ArcBasisMill.arcComponentTypeSymbolBuilder()
      .setName(parentCompName)
      .setSpannedScope(Mockito.mock(IArcBasisScope.class))
      .build();

    String childCompName = "ChildComp";
    ArcComponentTypeSymbol child = ArcBasisMill.arcComponentTypeSymbolBuilder()
      .setName(childCompName)
      .setSpannedScope(ArcBasisMill.scope())
      .build();

    ASTComponentHead head = ArcBasisMill.componentHeadBuilder()
      .addArcParent(ArcBasisMill.arcParentBuilder()
        .setType(ArcBasisMill.mCQualifiedTypeBuilder()
          .setMCQualifiedName(ArcBasisMill.mCQualifiedNameBuilder()
            .addParts(parentCompName)
            .build())
          .build())
        .build())
      .build();

    ASTArcComponentType comp = ArcBasisMill.arcComponentTypeBuilder()
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
    head.getArcParent(0).getType().setEnclosingScope(child.getSpannedScope());
    ((ASTMCQualifiedType) head.getArcParent(0).getType()).getMCQualifiedName().setEnclosingScope(child.getSpannedScope());

    // When
    getScopeGenP2().visit(head);

    // Then
    Assertions.assertAll(
      () -> Assertions.assertFalse(child.isEmptySuperComponents()),
      () -> Assertions.assertEquals(1, child.getSuperComponentsList().size()),
      () -> Assertions.assertTrue(head.getArcParent(0).getType().getDefiningSymbol().isPresent())
    );
    Assertions.assertAll(
      () -> Assertions.assertEquals(parent, child.getSuperComponents(0).getTypeInfo()),
      () -> Assertions.assertEquals(parent, head.getArcParent(0).getType().getDefiningSymbol().get())
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
    ArcComponentTypeSymbol child = ArcBasisMill.arcComponentTypeSymbolBuilder()
      .setName(childCompName)
      .setSpannedScope(ArcBasisMill.scope())
      .build();

    ASTComponentHead head = ArcBasisMill.componentHeadBuilder()
      .addArcParent(ArcBasisMill.arcParentBuilder()
        .setType(ArcBasisMill.mCQualifiedTypeBuilder()
          .setMCQualifiedName(ArcBasisMill.mCQualifiedNameBuilder()
            .addParts("ParentComp")
            .build())
          .build())
        .build())
      .build();

    ASTArcComponentType comp = ArcBasisMill.arcComponentTypeBuilder()
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
    head.getArcParent(0).getType().setEnclosingScope(child.getSpannedScope());
    ((ASTMCQualifiedType) head.getArcParent(0).getType()).getMCQualifiedName().setEnclosingScope(child.getSpannedScope());

    // When
    getScopeGenP2().visit(head);

    // Then
    Assertions.assertAll(
      () -> Assertions.assertTrue(child.isEmptySuperComponents()),
      () -> Assertions.assertFalse(head.getArcParent(0).getType().getDefiningSymbol().isPresent()),
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
    ArcComponentTypeSymbol ref1 = ArcBasisMill.arcComponentTypeSymbolBuilder()
      .setName(parentCompName)
      .setSpannedScope(Mockito.mock(IArcBasisScope.class))
      .build();
    ArcComponentTypeSymbol ref2 = ArcBasisMill.arcComponentTypeSymbolBuilder()
      .setName(parentCompName)
      .setSpannedScope(Mockito.mock(IArcBasisScope.class))
      .build();

    String childCompName = "ChildComp";
    ArcComponentTypeSymbol child = ArcBasisMill.arcComponentTypeSymbolBuilder()
      .setName(childCompName)
      .setSpannedScope(ArcBasisMill.scope())
      .build();

    ASTComponentHead head = ArcBasisMill.componentHeadBuilder()
      .addArcParent(ArcBasisMill.arcParentBuilder()
        .setType(ArcBasisMill.mCQualifiedTypeBuilder()
          .setMCQualifiedName(ArcBasisMill.mCQualifiedNameBuilder()
            .addParts(parentCompName)
            .build())
          .build())
        .build())
      .build();

    ASTArcComponentType comp = ArcBasisMill.arcComponentTypeBuilder()
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
    head.getArcParent(0).getType().setEnclosingScope(child.getSpannedScope());
    ((ASTMCQualifiedType) head.getArcParent(0).getType()).getMCQualifiedName().setEnclosingScope(child.getSpannedScope());

    // When
    getScopeGenP2().visit(head);

    // Then
    assertThat(getLoggedErrorCodes())
      .containsExactlyInAnyOrder(getErrorCodes(ArcError.AMBIGUOUS_REFERENCE));
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

  /**
   * If a refined component is uniquely resolvable, visiting it should link
   * the symbol of the refining component to the refined component via a component type
   * expression. The type expression has bound arguments if the ast reference has such.
   * <p>
   * Method under test {@link ArcBasisScopesGenitorP2#visit(ASTComponentHead)}
   */
  @Test
  void testVisitComponentHead6() {
    // Given
    String abstractionName = "Abstraction";
    ArcComponentTypeSymbol abstraction = ArcBasisMill.arcComponentTypeSymbolBuilder()
      .setName(abstractionName)
      .setSpannedScope(ArcBasisMill.scope())
      .build();
    ArcBasisMill.globalScope().add(abstraction);

    VariableSymbol param = ArcBasisMill.variableSymbolBuilder()
      .setName("param")
      .setType(Mockito.mock(SymTypeExpression.class))
      .setEnclosingScope(ArcBasisMill.globalScope())
      .build();
    abstraction.getSpannedScope().add(param);
    abstraction.addParameter(param);

    String concretizationName = "Concretization";
    ArcComponentTypeSymbol concretizationSym = ArcBasisMill.arcComponentTypeSymbolBuilder()
      .setName(concretizationName)
      .setSpannedScope(ArcBasisMill.scope())
      .build();
    ArcBasisMill.globalScope().add(concretizationSym);

    IArcBasisScope compScope = ArcBasisMill.scope();
    concretizationSym.setSpannedScope(compScope);
    compScope.setEnclosingScope(ArcBasisMill.globalScope());

    ASTExpression argExpr = Mockito.mock(ASTExpression.class);
    ASTMCQualifiedName astTypeName = ArcBasisMill.mCQualifiedNameBuilder()
      .addParts(abstractionName)
      .build();
    astTypeName.setEnclosingScope(compScope);
    ASTMCType astType = ArcBasisMill.mCQualifiedTypeBuilder()
      .setMCQualifiedName(astTypeName)
      .build();
    astType.setEnclosingScope(compScope);
    ASTArcParent ref = ArcBasisMill.arcParentBuilder()
      .setArcArgumentsList(Collections.singletonList(
        ArcBasisMill.arcArgumentBuilder().setExpression(argExpr).build())
      )
      .setType(astType)
      .uncheckedBuild();
    ref.setEnclosingScope(compScope);

    ASTComponentHead concretizationAst = ArcBasisMill.componentHeadBuilder()
        .addSpec(ref)
        .build();
    concretizationAst.setEnclosingScope(compScope);

    // When
    getScopeGenP2().visit(concretizationAst);

    // Then
    Assertions.assertEquals(1, concretizationSym.getRefinementsList().size());
    Assertions.assertAll(
      () -> Assertions.assertNotNull(concretizationSym.getRefinements(0)),
      () -> Assertions.assertEquals(abstraction, concretizationSym.getRefinements(0).getTypeInfo()),
      () -> assertThat(concretizationSym.getRefinements(0).getArguments())
              .containsExactly(argExpr)
    );
  }

  /**
   * If a refined component is not resolvable, visiting should report a missing
   * component symbol.
   * <p>
   * Method under test {@link ArcBasisScopesGenitorP2#visit(ASTComponentHead)}
   */
  @Test
  void testVisitComponentHead7() {
    // Given
    String concretizationName = "Concretization";
    ArcComponentTypeSymbol concretizationSym = ArcBasisMill.arcComponentTypeSymbolBuilder()
      .setName(concretizationName)
      .setSpannedScope(ArcBasisMill.scope())
      .build();
    ArcBasisMill.globalScope().add(concretizationSym);

    IArcBasisScope compScope = ArcBasisMill.scope();
    concretizationSym.setSpannedScope(compScope);
    compScope.setEnclosingScope(ArcBasisMill.globalScope());

    ASTMCQualifiedName astTypeName = ArcBasisMill.mCQualifiedNameBuilder()
      .addParts("NotExistent")
      .build();
    astTypeName.setEnclosingScope(compScope);
    ASTMCType astType = ArcBasisMill.mCQualifiedTypeBuilder()
      .setMCQualifiedName(astTypeName)
      .build();
    astType.setEnclosingScope(compScope);
    ASTArcParent ref = ArcBasisMill.arcParentBuilder()
      .setType(astType)
      .uncheckedBuild();
    ref.setEnclosingScope(compScope);

    ASTComponentHead headAst = ArcBasisMill.componentHeadBuilder()
      .addSpec(ref)
      .build();
    headAst.setEnclosingScope(compScope);

    // When
    getScopeGenP2().visit(headAst);

    // Then
    Assertions.assertAll(
      () -> Assertions.assertEquals(0, concretizationSym.getRefinementsList().size()),
      () -> assertThat(getLoggedErrorCodes())
              .containsExactly(ArcError.MISSING_COMPONENT.getErrorCode())
    );
  }

  /**
   * If the refined component is ambiguously referenced, visiting should report
   * an ambiguous symbol reference.
   * <p>
   * Method under test {@link ArcBasisScopesGenitorP2#visit(ASTComponentHead)}
   */
  @Test
  void testVisitComponentHead8() {
    // Given
    String abstractionName = "Abstraction";
    ArcComponentTypeSymbol abstraction1 = ArcBasisMill.arcComponentTypeSymbolBuilder()
      .setName(abstractionName)
      .setSpannedScope(Mockito.mock(IArcBasisScope.class))
      .build();
    ArcBasisMill.globalScope().add(abstraction1);

    ArcComponentTypeSymbol abstraction2 = ArcBasisMill.arcComponentTypeSymbolBuilder()
      .setName(abstractionName)
      .setSpannedScope(Mockito.mock(IArcBasisScope.class))
      .build();
    ArcBasisMill.globalScope().add(abstraction2);

    String concretizationName = "Concretization";
    ArcComponentTypeSymbol concretizationSym = ArcBasisMill.arcComponentTypeSymbolBuilder()
      .setName(concretizationName)
      .setSpannedScope(ArcBasisMill.scope())
      .build();
    ArcBasisMill.globalScope().add(concretizationSym);

    IArcBasisScope compScope = ArcBasisMill.scope();
    concretizationSym.setSpannedScope(compScope);
    compScope.setEnclosingScope(ArcBasisMill.globalScope());

    ASTMCQualifiedName astTypeName = ArcBasisMill.mCQualifiedNameBuilder()
      .addParts(abstractionName)
      .build();
    astTypeName.setEnclosingScope(compScope);
    ASTMCType astType = ArcBasisMill.mCQualifiedTypeBuilder()
      .setMCQualifiedName(astTypeName)
      .build();
    astType.setEnclosingScope(ArcBasisMill.globalScope());
    ASTArcParent ref = ArcBasisMill.arcParentBuilder()
      .setType(astType)
      .uncheckedBuild();
    ref.setEnclosingScope(compScope);

    ASTComponentHead headAst = ArcBasisMill.componentHeadBuilder()
      .addSpec(ref)
      .build();
    headAst.setEnclosingScope(compScope);

    // When
    getScopeGenP2().visit(headAst);

    // Then
    Assertions.assertAll(
      () -> Assertions.assertEquals(1, concretizationSym.getRefinementsList().size()),
      () -> assertThat(getLoggedErrorCodes())
              .containsExactlyInAnyOrder(getErrorCodes(ArcError.AMBIGUOUS_REFERENCE))
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
    "0, false",
    "0, true",
    "1, false",
    "1, true",
    "2, false",
    "2, true",
  })
  public void testVisitPortDeclaration1(@Nullable short tid, boolean i) {
    // Given
    Timing t = tid == 0 ? Timing.TIMED_SYNC : Timing.TIMED;
    ASTPortDeclaration ast = arcbasis.ArcBasisMill.portDeclarationBuilder()
      .setMCType(ArcBasisMill.mCQualifiedTypeBuilder()
        .setMCQualifiedName(ArcBasisMill.mCQualifiedNameBuilder()
          .addParts("a")
          .addParts("b")
          .addParts("X")
          .build())
        .build())
      .setIncoming(i)
      .setSync(t.equals(Timing.TIMED_SYNC))
      .addPort("p")
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
      () -> Assertions.assertEquals("a.b.X", ast.getArcPort(0).getSymbol().getType().printFullName()),
      () -> Assertions.assertEquals(t, ast.getArcPort(0).getSymbol().getTiming()),
      () -> Assertions.assertEquals(i, ast.getArcPort(0).getSymbol().isIncoming()),
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
    "0, false",
    "0, true",
    "1, false",
    "1, true",
    "2, false",
    "2, true",
  })
  public void testVisitPortDeclaration2(@Nullable short tid, boolean i) {
    // Given
    Timing t = tid == 0 ? Timing.TIMED_SYNC : Timing.TIMED;
    ASTPortDeclaration ast = arcbasis.ArcBasisMill.portDeclarationBuilder()
      .setMCType(ArcBasisMill.mCQualifiedTypeBuilder()
        .setMCQualifiedName(ArcBasisMill.mCQualifiedNameBuilder()
          .addParts("a")
          .addParts("b")
          .addParts("Y")
          .build())
        .build())
      .setIncoming(i)
      .setSync(t.equals(Timing.TIMED_SYNC))
      .addPort("p1")
      .addPort("p2")
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
      () -> Assertions.assertEquals("a.b.Y", ast.getArcPort(0).getSymbol().getType().printFullName()),
      () -> Assertions.assertEquals("a.b.Y", ast.getArcPort(1).getSymbol().getType().printFullName()),
      () -> Assertions.assertEquals(t, ast.getArcPort(0).getSymbol().getTiming()),
      () -> Assertions.assertEquals(t, ast.getArcPort(1).getSymbol().getTiming()),
      () -> Assertions.assertEquals(i, ast.getArcPort(0).getSymbol().isIncoming()),
      () -> Assertions.assertEquals(i, ast.getArcPort(1).getSymbol().isIncoming()),
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
    "0, false",
    "0, true",
    "1, false",
    "1, true",
    "2, false",
    "2, true",
  })
  public void testVisitPortDeclaration3(@Nullable short tid, boolean i) {
    // Given
    Timing t = tid == 0 ? Timing.TIMED_SYNC : Timing.TIMED;
    ASTPortDeclaration ast = arcbasis.ArcBasisMill.portDeclarationBuilder()
      .setMCType(ArcBasisMill.mCQualifiedTypeBuilder()
        .setMCQualifiedName(ArcBasisMill.mCQualifiedNameBuilder()
          .addParts("a")
          .addParts("b")
          .addParts("Z")
          .build())
        .build())
      .setIncoming(i)
      .setSync(t.equals(Timing.TIMED_SYNC))
      .addPort("p")
      .build();

    ast.accept(ArcBasisMill.scopesGenitorDelegator().traverser);

    // When
    this.getScopeGenP2().visit(ast);

    // Then
    Assertions.assertAll(
      () -> Assertions.assertTrue(ast.getArcPort(0).getSymbol().getType().isObscureType()),
      () -> Assertions.assertEquals(t, ast.getArcPort(0).getSymbol().getTiming()),
      () -> Assertions.assertEquals(i, ast.getArcPort(0).getSymbol().isIncoming()),
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
    ArcComponentTypeSymbol comp = ArcBasisMill.globalScope().resolveArcComponentType("Comp").orElseThrow();

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
    Optional<ArcComponentTypeSymbol> compTypeSym = ArcBasisMill.globalScope().resolveArcComponentType("Comp");
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
    SubcomponentSymbol symInstance = ArcBasisMill.subcomponentSymbolBuilder()
      .setName("inst").build();

    astInstance.setSymbol(symInstance);
    symInstance.setAstNode(astInstance);

    ArcComponentTypeSymbol compType = ArcBasisMill.arcComponentTypeSymbolBuilder()
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
    Assertions.assertEquals(3, astInstance.getSymbol().getType().getArguments().size());
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
  public void shouldHandleComponentInstantiationByCompletingSubcomponentSymbols() {
    // Given
    ASTComponentInstantiation astInstantiation = provideComponentInstantiation();
    Preconditions.checkState(astInstantiation.getComponentInstanceList().size() > 0);
    Optional<ArcComponentTypeSymbol> compTypeSym = ArcBasisMill.globalScope().resolveArcComponentType("Comp");
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
    ArcComponentTypeSymbol compTypeSym = ArcBasisMill.arcComponentTypeSymbolBuilder()
      .setName(compName)
      .setSpannedScope(ArcBasisMill.scope())
      .build();

    ASTMCType astCompType = ArcBasisMill.mCQualifiedTypeBuilder()
      .setMCQualifiedName(ArcBasisMill.mCQualifiedNameBuilder()
        .addParts(compName)
        .build())
      .build();
    ASTComponentInstantiation instantiation = ArcBasisMill.componentInstantiationBuilder()
      .setMCType(astCompType)
      .addInstance("inst1")
      .addInstance("inst2")
      .build();

    ArcBasisMill.globalScope().add(compTypeSym);
    astCompType.setEnclosingScope(ArcBasisMill.globalScope());
    instantiation.setEnclosingScope(ArcBasisMill.globalScope());

    for (ASTComponentInstance astInst : instantiation.getComponentInstanceList()) {
      SubcomponentSymbol instSym = ArcBasisMill.subcomponentSymbolBuilder()
        .setName(astInst.getName())
        .build();
      instSym.setAstNode(astInst);
      astInst.setSymbol(instSym);
      astInst.setEnclosingScope(ArcBasisMill.globalScope());
      ArcBasisMill.globalScope().add(instSym);
    }

    return instantiation;
  }

  /**
   * Method under test {@link ArcBasisScopesGenitorP2#visit(SubcomponentSymbol)}
   */
  @Test
  public void shouldVisitSubcomponentSymbol() {
    // Given
    IArcBasisScope scope = ArcBasisMill.scope();
    VariableSymbol parameter1 = ArcBasisMill.variableSymbolBuilder()
      .setName("a").setEnclosingScope(scope).build();
    scope.add(parameter1);
    VariableSymbol parameter2 = ArcBasisMill.variableSymbolBuilder()
      .setName("b").setEnclosingScope(scope).build();
    scope.add(parameter2);
    ArcComponentTypeSymbol component = ArcBasisMill.arcComponentTypeSymbolBuilder()
      .setParameterList(Arrays.asList(parameter1, parameter2)) // List.of produces an
      .setName("C")
      .setSpannedScope(scope)
      .build();
    List<ASTArcArgument> bindings = Arrays.asList(Mockito.mock(ASTArcArgument.class), Mockito.mock(ASTArcArgument.class));
    TypeExprOfComponent typeExpr = new TypeExprOfComponent(component);
    typeExpr.addArcArguments(bindings);

    SubcomponentSymbol symbol = ArcBasisMill.subcomponentSymbolBuilder().setType(typeExpr).setName("c1").build();

    List<ASTArcArgument> bindingsBeforeCompletion = ((TypeExprOfComponent) symbol.getType()).getParamArcBindingsAsList();
    // When
    getScopeGenP2().visit(symbol);

    // Then
    Assertions.assertNotEquals(((TypeExprOfComponent) symbol.getType()).getParamArcBindingsAsList(),bindingsBeforeCompletion);
    Assertions.assertEquals(typeExpr.getTypeInfo(), symbol.getType()
      .getTypeInfo());
    Assertions.assertIterableEquals(bindings, (((TypeExprOfComponent) symbol.getType()).getParamArcBindingsAsList()));
  }

  @Test
  public void shouldVisitTypeParameter() {
    // Given
    ASTTypeParameter astTypeParam = ArcBasisMill.typeParameterBuilder()
      .setName("T")
      .build();
    TypeVarSymbol typeParamSym = ArcBasisMill.typeVarSymbolBuilder()
      .setName("T")
      .setSpannedScope(ArcBasisMill.scope())
      .build();
    astTypeParam.setSymbol(typeParamSym);
    typeParamSym.setAstNode(astTypeParam);

    ASTMCType upperDouble = ArcBasisMill.mCPrimitiveTypeBuilder().setPrimitive(ASTConstantsMCBasicTypes.DOUBLE).build();
    upperDouble.setEnclosingScope(ArcBasisMill.globalScope());
    ASTMCType upperBool = ArcBasisMill.mCPrimitiveTypeBuilder().setPrimitive(ASTConstantsMCBasicTypes.BOOLEAN).build();
    upperBool.setEnclosingScope(ArcBasisMill.globalScope());

    ASTTypeParameter astTypeParamWithBounds = ArcBasisMill.typeParameterBuilder()
      .setName("U")
      .addMCType(upperDouble)
      .addMCType(upperBool)
      .build();
    TypeVarSymbol typeParamWithBoundSym = ArcBasisMill.typeVarSymbolBuilder()
      .setName("U")
      .setSpannedScope(ArcBasisMill.scope())
      .build();
    astTypeParamWithBounds.setSymbol(typeParamWithBoundSym);
    typeParamWithBoundSym.setAstNode(astTypeParamWithBounds);

    // When
    this.getScopeGenP2().visit(astTypeParam);
    this.getScopeGenP2().visit(astTypeParamWithBounds);

    // Then
    Assertions.assertEquals(0, typeParamSym.getSuperTypesList().size());
    Assertions.assertEquals(2, typeParamWithBoundSym.getSuperTypesList().size());
    Assertions.assertTrue(SymTypeExpressionFactory.createPrimitive("double").deepEquals(
      typeParamWithBoundSym.getSuperTypes(0)));
    Assertions.assertTrue(SymTypeExpressionFactory.createPrimitive("boolean").deepEquals(
      typeParamWithBoundSym.getSuperTypes(1)));
  }

  @Test
  public void visitShouldThrowException() {
    // Given
    ASTTypeParameter param = ArcBasisMill.typeParameterBuilder().setName("A").build();

    // When & Then
    Assertions.assertAll(
      () -> Assertions.assertThrows(NullPointerException.class, () -> this.getScopeGenP2().visit((ASTTypeParameter) null)),
      () -> Assertions.assertThrows(IllegalArgumentException.class, () -> this.getScopeGenP2().visit(param))
    );
  }
}