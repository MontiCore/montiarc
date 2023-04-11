/* (c) https://github.com/MontiCore/monticore */
package genericarc._symboltable;

import arcbasis.ArcBasisMill;
import arcbasis._ast.ASTComponentBody;
import arcbasis._ast.ASTComponentType;
import arcbasis._symboltable.ComponentTypeSymbol;
import arcbasis.check.ArcBasisSynthesizeComponent;
import arcbasis.check.ISynthesizeComponent;
import com.google.common.base.Preconditions;
import de.monticore.symbols.basicsymbols._symboltable.TypeSymbol;
import de.monticore.symbols.basicsymbols._symboltable.TypeVarSymbol;
import de.monticore.types.check.SymTypeExpressionFactory;
import de.monticore.types.mcbasictypes._ast.ASTConstantsMCBasicTypes;
import de.monticore.types.mcbasictypes._ast.ASTMCQualifiedType;
import de.monticore.types.mcbasictypes._ast.ASTMCType;
import genericarc.GenericArcAbstractTest;
import genericarc.GenericArcMill;
import genericarc._ast.ASTArcTypeParameter;
import genericarc._ast.ASTGenericComponentHead;
import genericarc._visitor.GenericArcTraverser;
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

import java.util.function.Consumer;
import java.util.stream.Stream;

public class GenericArcScopesGenitorP2Test extends GenericArcAbstractTest {

  protected GenericArcScopesGenitorP2 scopesGenP2;

  protected static Stream<Arguments> visitComponentHeadExpectedExceptionProvider() {
    ASTGenericComponentHead ast1 = GenericArcMill.genericComponentHeadBuilder().build();
    ASTGenericComponentHead ast2 = GenericArcMill.genericComponentHeadBuilder().build();
    ast2.setEnclosingScope(GenericArcMill.scope());

    ASTGenericComponentHead ast3 = GenericArcMill.genericComponentHeadBuilder().build();
    IGenericArcScope spannedBySymbol = GenericArcMill.scope();
    TypeSymbol typeSym = GenericArcMill.typeSymbolBuilder().setName("A")
      .setSpannedScope(spannedBySymbol).build();
    spannedBySymbol.setSpanningSymbol(typeSym);
    ast3.setEnclosingScope(spannedBySymbol);

    Consumer<GenericArcScopesGenitorP2> consumer1 = scopesGenP2 -> {};

    return Stream.of(
      Arguments.of(null, consumer1, NullPointerException.class),
      Arguments.of(ast1, consumer1, NullPointerException.class),
      Arguments.of(ast2, consumer1, IllegalArgumentException.class),
      Arguments.of(ast3, consumer1, IllegalArgumentException.class)
    );
  }

  protected GenericArcScopesGenitorP2 getScopesGenP2() {
    return this.scopesGenP2;
  }

  protected void setUpCompleter() {
    this.scopesGenP2 = GenericArcMill.scopesGenitorP2();
  }

  @BeforeEach
  @Override
  public void setUp() {
    super.setUp();
    this.setUpCompleter();
  }

  /**
   * Method under test {@link GenericArcScopesGenitorP2#setComponentSynthesizer(ISynthesizeComponent)}
   */
  @Test
  public void shouldSetComponentSynthesizer() {
    // Given
    ISynthesizeComponent typeSynth = new ArcBasisSynthesizeComponent();

    // When
    this.getScopesGenP2().setComponentSynthesizer(typeSynth);

    // Then
    Assertions.assertEquals(typeSynth, this.getScopesGenP2().getComponentSynthesizer());
  }

  /**
   * Method under test {@link GenericArcScopesGenitorP2#setComponentSynthesizer(ISynthesizeComponent)}
   */
  @Test
  public void setComponentSynthesizerShouldThrowNullPointerException() {
    // When && Then
    Assertions.assertThrows(NullPointerException.class, () -> this.getScopesGenP2().setComponentSynthesizer(null));
  }

  /**
   * Method under test {@link GenericArcScopesGenitorP2#setTraverser(GenericArcTraverser)}
   */
  @Test
  public void shouldSetTraverser() {
    // Given
    GenericArcTraverser traverser = GenericArcMill.traverser();

    // When
    this.getScopesGenP2().setTraverser(traverser);

    // Then
    Assertions.assertEquals(traverser, this.getScopesGenP2().getTraverser());
  }

  /**
   * Method under test {@link GenericArcScopesGenitorP2#setTraverser(GenericArcTraverser)}
   *
   * @param traverser the traverser to set (null)
   */
  @ParameterizedTest
  @NullSource
  public void setTraverserShouldThrowNullPointerException(@Nullable GenericArcTraverser traverser) {
    // When && Then
    Assertions.assertThrows(NullPointerException.class, () -> this.getScopesGenP2().setTraverser(traverser));
  }

  /**
   * Method under test {@link GenericArcScopesGenitorP2#GenericArcScopesGenitorP2()}
   */
  @Test
  public void shouldConstructClass() {
    // When
    GenericArcScopesGenitorP2 scopesGenP2 = new GenericArcScopesGenitorP2();

    //Then
    Assertions.assertNotNull(scopesGenP2.getComponentSynthesizer());
    Assertions.assertNotNull(scopesGenP2.getTypeCalculator());
  }

  /**
   * Method under test {@link GenericArcScopesGenitorP2#visit(ASTGenericComponentHead)}
   * <p>
   * If the component has no parent then the symbol should not be modified.
   */
  @Test
  public void shouldVisitComponentHeadWithoutParent() {
    // Given
    ComponentTypeSymbol symbol = GenericArcMill.componentTypeSymbolBuilder()
      .setName("SomeName")
      .setSpannedScope(GenericArcMill.scope())
      .build();

    ASTGenericComponentHead ast = GenericArcMill.genericComponentHeadBuilder().build();
    ast.setEnclosingScope(symbol.getSpannedScope());
    ast.setParentAbsent();

    // When
    this.getScopesGenP2().visit(ast);

    // Then
    Assertions.assertFalse(ast.isPresentParent());
    Assertions.assertFalse(symbol.isPresentParent());
  }

  /**
   * Method under test {@link GenericArcScopesGenitorP2#visit(ASTGenericComponentHead)}
   */
  @Test
  public void shouldVisitComponentHead() {
    // Given
    String parentCompName = "ParentComp";
    ComponentTypeSymbol parentComp = GenericArcMill.componentTypeSymbolBuilder()
      .setName(parentCompName)
      .setSpannedScope(Mockito.mock(IGenericArcScope.class))
      .build();

    String childCompName = "ChildComp";
    ComponentTypeSymbol symChildComp = GenericArcMill.componentTypeSymbolBuilder()
      .setName(childCompName)
      .setSpannedScope(GenericArcMill.scope())
      .build();

    ASTGenericComponentHead childCompHead = GenericArcMill.genericComponentHeadBuilder()
      .setParent(createQualifiedType(parentCompName))
      .build();

    ASTComponentType astChildComp = GenericArcMill.componentTypeBuilder()
      .setName(childCompName)
      .setBody(Mockito.mock(ASTComponentBody.class))
      .setHead(childCompHead)
      .build();

    GenericArcMill.globalScope().add(parentComp);
    GenericArcMill.globalScope().add(symChildComp);
    parentComp.getSpannedScope().setEnclosingScope(GenericArcMill.globalScope());
    symChildComp.getSpannedScope().setEnclosingScope(GenericArcMill.globalScope());

    symChildComp.setAstNode(astChildComp);
    astChildComp.setSymbol(symChildComp);
    astChildComp.setEnclosingScope(GenericArcMill.globalScope());
    astChildComp.setSpannedScope(symChildComp.getSpannedScope());
    childCompHead.setEnclosingScope(symChildComp.getSpannedScope());
    childCompHead.getParent().setEnclosingScope(symChildComp.getSpannedScope());
    ((ASTMCQualifiedType) childCompHead.getParent()).getMCQualifiedName().setEnclosingScope(symChildComp.getSpannedScope());

    // When
    getScopesGenP2().visit(childCompHead);

    // Then
    Assertions.assertEquals(parentComp, symChildComp.getParent().getTypeInfo());
  }

  /**
   * Method under test {@link GenericArcScopesGenitorP2#visit(ASTGenericComponentHead)}
   *
   * @param ast      the ast to visit
   * @param setup    the setup to execute beforehand
   * @param expected the expected exception
   */
  @ParameterizedTest
  @MethodSource("visitComponentHeadExpectedExceptionProvider")
  public void visitShouldTrowException(@Nullable ASTGenericComponentHead ast,
                                       @NotNull Consumer<GenericArcScopesGenitorP2> setup,
                                       @NotNull Class<Exception> expected) {
    Preconditions.checkNotNull(setup);
    Preconditions.checkNotNull(expected);

    // Given
    setup.accept(this.getScopesGenP2());

    // When && Then
    Assertions.assertThrows(expected, () -> this.getScopesGenP2().visit(ast));
  }

  @Test
  public void shouldVisitArcTypeParameter() {
    // Given
    ASTArcTypeParameter astTypeParam = GenericArcMill.arcTypeParameterBuilder()
      .setName("T")
      .build();
    TypeVarSymbol typeParamSym = GenericArcMill.typeVarSymbolBuilder()
      .setName("T")
      .setSpannedScope(GenericArcMill.scope())
      .build();
    astTypeParam.setSymbol(typeParamSym);
    typeParamSym.setAstNode(astTypeParam);

    ASTMCType upperDouble = ArcBasisMill.mCPrimitiveTypeBuilder().setPrimitive(ASTConstantsMCBasicTypes.DOUBLE).build();
    upperDouble.setEnclosingScope(ArcBasisMill.globalScope());
    ASTMCType upperBool = ArcBasisMill.mCPrimitiveTypeBuilder().setPrimitive(ASTConstantsMCBasicTypes.BOOLEAN).build();
    upperBool.setEnclosingScope(ArcBasisMill.globalScope());

    ASTArcTypeParameter astTypeParamWithBounds = GenericArcMill.arcTypeParameterBuilder()
      .setName("U")
      .addUpperBound(upperDouble)
      .addUpperBound(upperBool)
      .build();
    TypeVarSymbol typeParamWithBoundSym = GenericArcMill.typeVarSymbolBuilder()
      .setName("U")
      .setSpannedScope(GenericArcMill.scope())
      .build();
    astTypeParamWithBounds.setSymbol(typeParamWithBoundSym);
    typeParamWithBoundSym.setAstNode(astTypeParamWithBounds);

    // When
    this.getScopesGenP2().visit(astTypeParam);
    this.getScopesGenP2().visit(astTypeParamWithBounds);

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
    ASTArcTypeParameter param = GenericArcMill.arcTypeParameterBuilder().setName("A").build();

    // When & Then
    Assertions.assertAll(
      () -> Assertions.assertThrows(NullPointerException.class, () -> this.getScopesGenP2().visit((ASTArcTypeParameter) null)),
      () -> Assertions.assertThrows(IllegalArgumentException.class, () -> this.getScopesGenP2().visit(param))
    );
  }
}
