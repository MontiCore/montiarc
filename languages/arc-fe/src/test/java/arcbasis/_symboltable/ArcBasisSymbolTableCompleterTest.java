/* (c) https://github.com/MontiCore/monticore */
package arcbasis._symboltable;

import arcbasis.AbstractTest;
import arcbasis.ArcBasisMill;
import arcbasis._ast.ASTComponentBody;
import arcbasis._ast.ASTComponentHead;
import arcbasis._ast.ASTComponentType;
import arcbasis._visitor.ArcBasisTraverser;
import com.google.common.base.Preconditions;
import de.monticore.types.mcbasictypes.MCBasicTypesMill;
import de.monticore.types.mcbasictypes._ast.ASTConstantsMCBasicTypes;
import de.monticore.types.mcbasictypes._ast.ASTMCType;
import de.monticore.types.prettyprint.MCBasicTypesFullPrettyPrinter;
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

import java.util.Arrays;
import java.util.Stack;
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
   * Method under test {@link ArcBasisSymbolTableCompleter#setTypePrinter(MCBasicTypesFullPrettyPrinter)}
   */
  @Test
  public void shouldSetTypePrinter() {
    // Given
    MCBasicTypesFullPrettyPrinter typesPrinter = MCBasicTypesMill.mcBasicTypesPrettyPrinter();

    // When
    this.getCompleter().setTypePrinter(typesPrinter);

    // Then
    Assertions.assertEquals(typesPrinter, this.getCompleter().getTypePrinter());
  }

  /**
   * Method under test {@link ArcBasisSymbolTableCompleter#setTypePrinter(MCBasicTypesFullPrettyPrinter)}
   */
  @Test
  public void setTypePrinterShouldThrowNullPointerException() {
    // When && Then
    Assertions.assertThrows(NullPointerException.class, () -> this.getCompleter().setTypePrinter(null));
  }

  /**
   * Method under test {@link ArcBasisSymbolTableCompleter#printType(ASTMCType)}
   *
   * @param ast      the type to print
   * @param expected the expected print result
   */
  @ParameterizedTest
  @MethodSource("typeAndExpectedStringRepresentationProvider")
  public void shouldPrintType(@NotNull ASTMCType ast, @NotNull String expected) {
    Preconditions.checkNotNull(ast);
    Preconditions.checkNotNull(expected);

    // Given
    this.getCompleter().setTypePrinter(MCBasicTypesMill.mcBasicTypesPrettyPrinter());

    // When && Then
    Assertions.assertEquals(expected, this.getCompleter().printType(ast));
  }

  protected static Stream<Arguments> typeAndExpectedStringRepresentationProvider() {
    return Stream.of(
      Arguments.of(MCBasicTypesMill.mCPrimitiveTypeBuilder()
        .setPrimitive(ASTConstantsMCBasicTypes.BOOLEAN).build(), "boolean"),
      Arguments.of(MCBasicTypesMill.mCQualifiedTypeBuilder()
        .setMCQualifiedName(MCBasicTypesMill.mCQualifiedNameBuilder()
          .setPartsList(Arrays.asList("foo", "bar", "Type"))
          .build())
        .build(), "foo.bar.Type")
    );
  }

  /**
   * Method under test {@link ArcBasisSymbolTableCompleter#printType(ASTMCType)}
   */
  @Test
  public void printTypeShouldThrowNullPointerException() {
    // When && Then
    Assertions.assertThrows(NullPointerException.class, () -> this.getCompleter().printType(null));
  }

  /**
   * Method under test {@link ArcBasisSymbolTableCompleter#setComponentStack(Stack)}
   */
  @Test
  public void shouldSetComponentStack() {
    // Given
    Stack<ComponentTypeSymbol> stack = new Stack<>();

    // When
    this.getCompleter().setComponentStack(stack);

    // Then
    Assertions.assertEquals(stack, this.getCompleter().getComponentStack());
  }

  /**
   * Method under test {@link ArcBasisSymbolTableCompleter#setComponentStack(Stack)}
   *
   * @param stack    the stack to set
   * @param expected the expected exception
   */
  @ParameterizedTest
  @MethodSource("componentStackAndExpectedExceptionProvider")
  public void setComponentStackShouldThrowException(@Nullable Stack<ComponentTypeSymbol> stack,
                                                    @NotNull Class<Exception> expected) {
    Preconditions.checkNotNull(expected);

    // When && Then
    Assertions.assertThrows(expected, () -> this.getCompleter().setComponentStack(stack));
  }

  protected static Stream<Arguments> componentStackAndExpectedExceptionProvider() {
    Stack<ComponentTypeSymbol> stack = new Stack<>();
    stack.add(null);
    return Stream.of(
      Arguments.of(null, NullPointerException.class),
      Arguments.of(stack, IllegalArgumentException.class)
    );
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
   * Method under test {@link ArcBasisSymbolTableCompleter#ArcBasisSymbolTableCompleter()}
   */
  @Test
  public void shouldConstructClass() {
    // When
    ArcBasisSymbolTableCompleter completer = new ArcBasisSymbolTableCompleter();

    //Then
    Assertions.assertNotNull(completer.getTypePrinter());
    Assertions.assertNotNull(completer.getComponentStack());
  }

  /**
   * Method under test {@link ArcBasisSymbolTableCompleter#ArcBasisSymbolTableCompleter(MCBasicTypesFullPrettyPrinter)}
   *
   * @param printer the first constructor parameter
   */
  @ParameterizedTest
  @MethodSource("typesPrinterProvider")
  public void shouldConstructClass(@NotNull MCBasicTypesFullPrettyPrinter printer) {
    Preconditions.checkNotNull(printer);

    // When
    ArcBasisSymbolTableCompleter completer = new ArcBasisSymbolTableCompleter(printer);

    // Then
    Assertions.assertEquals(printer, completer.getTypePrinter());
    Assertions.assertNotNull(completer.getComponentStack());
  }

  protected static Stream<MCBasicTypesFullPrettyPrinter> typesPrinterProvider() {
    return Stream.of(MCBasicTypesMill.mcBasicTypesPrettyPrinter());
  }

  /**
   * Method under test {@link ArcBasisSymbolTableCompleter#ArcBasisSymbolTableCompleter(MCBasicTypesFullPrettyPrinter)}
   *
   * @param printer the first constructor parameter (null)
   */
  @ParameterizedTest
  @NullSource
  public void constructorShouldThrowNullPointerException(@Nullable MCBasicTypesFullPrettyPrinter printer) {
    // When && Then
    Assertions.assertThrows(NullPointerException.class, () -> new ArcBasisSymbolTableCompleter(printer));
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
    Preconditions.checkState(this.getCompleter().getComponentStack().isEmpty());

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
    Assertions.assertFalse(this.getCompleter().getComponentStack().isEmpty());
    Assertions.assertEquals(1, this.getCompleter().getComponentStack().size());
    Assertions.assertTrue(this.getCompleter().getCurrentComponent().isPresent());
    Assertions.assertEquals(ast.getSymbol(), this.getCompleter().getCurrentComponent().get());
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
    ASTComponentType ast2 = ArcBasisMill.componentTypeBuilder().setName("SomeName")
      .setHead(Mockito.mock(ASTComponentHead.class))
      .setBody(Mockito.mock(ASTComponentBody.class))
      .build();
    ast2.setSymbol(ArcBasisMill.componentTypeSymbolBuilder().setName("SomeName")
      .setSpannedScope(ArcBasisMill.scope()).build());
    ast2.getSymbol().setAstNode(ast2);

    Consumer<ArcBasisSymbolTableCompleter> consumer1 = completer -> {};
    Consumer<ArcBasisSymbolTableCompleter> consumer2 = completer -> completer.putOnStack(null);
    Consumer<ArcBasisSymbolTableCompleter> consumer3 = completer -> {
      ComponentTypeSymbol symbol = Mockito.mock(ComponentTypeSymbol.class);
      completer.putOnStack(symbol);
      Mockito.when(symbol.isPresentAstNode()).thenReturn(false);
    };
    Consumer<ArcBasisSymbolTableCompleter> consumer4 = completer -> {
      ComponentTypeSymbol symbol = Mockito.mock(ComponentTypeSymbol.class);
      symbol.setAstNode(Mockito.mock(ASTComponentType.class));
      completer.putOnStack(symbol);
    };
    Consumer<ArcBasisSymbolTableCompleter> consumer5 = completer -> {
      ComponentTypeSymbol symbol = Mockito.mock(ComponentTypeSymbol.class);
      symbol.setAstNode(ast2);
      completer.putOnStack(symbol);
    };
    return Stream.of(
      Arguments.of(null, consumer1, NullPointerException.class),
      Arguments.of(ast1, consumer1, IllegalArgumentException.class),
      Arguments.of(ast2, consumer1, IllegalStateException.class),
      Arguments.of(ast2, consumer2, IllegalStateException.class),
      Arguments.of(ast2, consumer3, IllegalStateException.class),
      Arguments.of(ast2, consumer4, IllegalStateException.class),
      Arguments.of(ast2, consumer5, IllegalStateException.class)
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

    this.getCompleter().putOnStack(ast.getSymbol());

    // When
    this.getCompleter().endVisit(ast);

    // Then
    Assertions.assertTrue(this.getCompleter().getComponentStack().isEmpty());
  }

  /**
   * Method under test {@link ArcBasisSymbolTableCompleter#visit(ASTComponentHead)}
   * <p>
   * If the component has no parent then the symbol should not be modified.
   */
  @Test
  public void shouldVisitComponentHeadWithoutParent() {
    // Given
    ASTComponentHead ast = ArcBasisMill.componentHeadBuilder().build();
    ast.setEnclosingScope(ArcBasisMill.scope());
    ast.setParentAbsent();

    ComponentTypeSymbol symbol = ArcBasisMill.componentTypeSymbolBuilder()
      .setName("SomeName")
      .setSpannedScope(ArcBasisMill.scope())
      .build();
    this.getCompleter().putOnStack(symbol);

    // When
    this.getCompleter().visit(ast);

    // Then
    Assertions.assertFalse(ast.isPresentParent());
    Assertions.assertFalse(symbol.isPresentParentComponent());
    Assertions.assertFalse(this.getCompleter().getComponentStack().isEmpty());
    Assertions.assertEquals(1, this.getCompleter().getComponentStack().size());
    Assertions.assertTrue(this.getCompleter().getCurrentComponent().isPresent());
    Assertions.assertEquals(symbol, this.getCompleter().getCurrentComponent().get());
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
      .setSpannedScope(Mockito.mock(IArcBasisScope.class))
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

    symChildComp.setAstNode(astChildComp);
    astChildComp.setSymbol(symChildComp);
    astChildComp.setEnclosingScope(ArcBasisMill.globalScope());
    childCompHead.setEnclosingScope(ArcBasisMill.globalScope());

    getCompleter().putOnStack(symChildComp);

    // When
    getCompleter().visit(childCompHead);

    // Then
    Assertions.assertEquals(parentComp, symChildComp.getParent().getTypeInfo());
    Assertions.assertEquals(1, this.getCompleter().getComponentStack().size());
    Assertions.assertTrue(this.getCompleter().getCurrentComponent().isPresent());
    Assertions.assertEquals(symChildComp, this.getCompleter().getCurrentComponent().get());
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

    Consumer<ArcBasisSymbolTableCompleter> consumer1 = completer -> {};
    Consumer<ArcBasisSymbolTableCompleter> consumer2 = completer -> completer.putOnStack(null);

    return Stream.of(
      Arguments.of(null, consumer1, NullPointerException.class),
      Arguments.of(ast1, consumer1, NullPointerException.class),
      Arguments.of(ast2, consumer1, IllegalStateException.class),
      Arguments.of(ast2, consumer2, IllegalStateException.class)
    );
  }
}