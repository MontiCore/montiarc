/* (c) https://github.com/MontiCore/monticore */
package genericarc._symboltable;

import arcbasis._ast.ASTComponentBody;
import arcbasis._ast.ASTComponentType;
import arcbasis._symboltable.ComponentTypeSymbol;
import com.google.common.base.Preconditions;
import de.monticore.symbols.basicsymbols._symboltable.TypeSymbol;
import de.monticore.types.mcbasictypes.MCBasicTypesMill;
import de.monticore.types.mcbasictypes._ast.ASTMCQualifiedType;
import de.monticore.types.prettyprint.MCBasicTypesFullPrettyPrinter;
import genericarc.AbstractTest;
import genericarc.GenericArcMill;
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

public class GenericArcSymbolTableCompleterTest extends AbstractTest {

  protected GenericArcSymbolTableCompleter completer;

  protected GenericArcSymbolTableCompleter getCompleter() {
    return this.completer;
  }

  protected void setUpCompleter() {
    this.completer = GenericArcMill.symbolTableCompleter();
  }

  @BeforeEach
  @Override
  public void init() {
    super.init();
    this.setUpCompleter();
  }


  /**
   * Method under test {@link GenericArcSymbolTableCompleter#setTypePrinter(MCBasicTypesFullPrettyPrinter)}
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
   * Method under test {@link GenericArcSymbolTableCompleter#setTypePrinter(MCBasicTypesFullPrettyPrinter)}
   */
  @Test
  public void setTypePrinterShouldThrowNullPointerException() {
    // When && Then
    Assertions.assertThrows(NullPointerException.class, () -> this.getCompleter().setTypePrinter(null));
  }

  /**
   * Method under test {@link GenericArcSymbolTableCompleter#setTraverser(GenericArcTraverser)}
   */
  @Test
  public void shouldSetTraverser() {
    // Given
    GenericArcTraverser traverser = GenericArcMill.traverser();

    // When
    this.getCompleter().setTraverser(traverser);

    // Then
    Assertions.assertEquals(traverser, this.getCompleter().getTraverser());
  }

  /**
   * Method under test {@link GenericArcSymbolTableCompleter#setTraverser(GenericArcTraverser)}
   *
   * @param traverser the traverser to set (null)
   */
  @ParameterizedTest
  @NullSource
  public void setTraverserShouldThrowNullPointerException(@Nullable GenericArcTraverser traverser) {
    // When && Then
    Assertions.assertThrows(NullPointerException.class, () -> this.getCompleter().setTraverser(traverser));
  }

  /**
   * Method under test {@link GenericArcSymbolTableCompleter#GenericArcSymbolTableCompleter()}
   */
  @Test
  public void shouldConstructClass() {
    // When
    GenericArcSymbolTableCompleter completer = new GenericArcSymbolTableCompleter();

    //Then
    Assertions.assertNotNull(completer.getTypePrinter());
  }

  /**
   * Method under test {@link GenericArcSymbolTableCompleter#GenericArcSymbolTableCompleter(MCBasicTypesFullPrettyPrinter)}
   *
   * @param printer the first constructor parameter
   */
  @ParameterizedTest
  @MethodSource("typesPrinterProvider")
  public void shouldConstructClass(@NotNull MCBasicTypesFullPrettyPrinter printer) {
    Preconditions.checkNotNull(printer);

    // When
    GenericArcSymbolTableCompleter completer = new GenericArcSymbolTableCompleter(printer);

    // Then
    Assertions.assertEquals(printer, completer.getTypePrinter());
  }

  protected static Stream<MCBasicTypesFullPrettyPrinter> typesPrinterProvider() {
    return Stream.of(MCBasicTypesMill.mcBasicTypesPrettyPrinter());
  }

  /**
   * Method under test {@link GenericArcSymbolTableCompleter#GenericArcSymbolTableCompleter(MCBasicTypesFullPrettyPrinter)}
   *
   * @param printer the first constructor parameter (null)
   */
  @ParameterizedTest
  @NullSource
  public void constructorShouldThrowNullPointerException(@Nullable MCBasicTypesFullPrettyPrinter printer) {
    // When && Then
    Assertions.assertThrows(NullPointerException.class, () -> new GenericArcSymbolTableCompleter(printer));
  }

  /**
   * Method under test {@link GenericArcSymbolTableCompleter#visit(ASTGenericComponentHead)}
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
    this.getCompleter().visit(ast);

    // Then
    Assertions.assertFalse(ast.isPresentParent());
    Assertions.assertFalse(symbol.isPresentParentComponent());
  }

  /**
   * Method under test {@link GenericArcSymbolTableCompleter#visit(ASTGenericComponentHead)}
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
    getCompleter().visit(childCompHead);

    // Then
    Assertions.assertEquals(parentComp, symChildComp.getParent().getTypeInfo());
  }

  /**
   * Method under test {@link GenericArcSymbolTableCompleter#visit(ASTGenericComponentHead)}
   *
   * @param ast      the ast to visit
   * @param setup    the setup to execute beforehand
   * @param expected the expected exception
   */
  @ParameterizedTest
  @MethodSource("visitComponentHeadExpectedExceptionProvider")
  public void visitShouldTrowException(@Nullable ASTGenericComponentHead ast,
                                       @NotNull Consumer<GenericArcSymbolTableCompleter> setup,
                                       @NotNull Class<Exception> expected) {
    Preconditions.checkNotNull(setup);
    Preconditions.checkNotNull(expected);

    // Given
    setup.accept(this.getCompleter());

    // When && Then
    Assertions.assertThrows(expected, () -> this.getCompleter().visit(ast));
  }

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

    Consumer<GenericArcSymbolTableCompleter> consumer1 = completer -> {};

    return Stream.of(
      Arguments.of(null, consumer1, NullPointerException.class),
      Arguments.of(ast1, consumer1, NullPointerException.class),
      Arguments.of(ast2, consumer1, IllegalArgumentException.class),
      Arguments.of(ast3, consumer1, IllegalArgumentException.class)
    );
  }
}
