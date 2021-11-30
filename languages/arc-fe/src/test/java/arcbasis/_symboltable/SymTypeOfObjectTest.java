/* (c) https://github.com/MontiCore/monticore */
package arcbasis._symboltable;

import arcbasis.ArcBasisMill;
import com.google.common.base.Preconditions;
import de.monticore.symbols.basicsymbols._symboltable.TypeSymbol;
import de.monticore.symboltable.ImportStatement;
import de.monticore.types.check.SymTypeExpression;
import de.monticore.types.check.SymTypeExpressionFactory;
import de.monticore.types.check.SymTypeOfObject;
import de.se_rwth.commons.logging.Log;
import de.se_rwth.commons.logging.LogStub;
import org.codehaus.commons.nullanalysis.NotNull;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.stream.Stream;

/**
 * Holds tests for {@link SymTypeOfObject}.
 */
public class SymTypeOfObjectTest {

  @BeforeAll
  public static void init() {
    ArcBasisMill.globalScope().clear();
    ArcBasisMill.reset();
    ArcBasisMill.init();
    LogStub.init();
    Log.enableFailQuick(false);
    setUp();
  }

  public static void setUp() {
    // Create scope a.b.c
    IArcBasisArtifactScope scope = ArcBasisMill.artifactScope();
    scope.setEnclosingScope(ArcBasisMill.globalScope());
    scope.setImportsList(new ArrayList<>());
    scope.setPackageName("a.b.c");
    scope.setName("");

    // Create type symbol a.b.c.X
    TypeSymbol symbol = ArcBasisMill.typeSymbolBuilder().setName("X")
      .setEnclosingScope(scope).setSpannedScope(ArcBasisMill.scope()).build();
    scope.add(symbol);
  }

  /**
   * Method under test {@link SymTypeOfObject#print()}.
   */
  @ParameterizedTest
  @MethodSource("symTypeExprAndExpectedNameProvider")
  public void shouldPrintName(@NotNull SymTypeExpression symType, @NotNull String expected) {
    Preconditions.checkNotNull(symType);
    Preconditions.checkNotNull(expected);
    Preconditions.checkArgument(!expected.isEmpty());

    // When
    String actual = symType.print();

    // Then
    Assertions.assertEquals(expected, actual);
  }

  protected static Stream<Arguments> symTypeExprAndExpectedNameProvider() {
    List<SymTypeExpression> symTypes = createSymTypeExpressions();

    return Stream.of(
      Arguments.of(symTypes.get(0), "X"),
      Arguments.of(symTypes.get(1), "X"),
      Arguments.of(symTypes.get(2), "a.b.c.X"),
      Arguments.of(symTypes.get(3), "Y"),
      Arguments.of(symTypes.get(4), "a.b.c.Y")
    );
  }

  /**
   * Method under test {@link SymTypeOfObject#printFullName()}.
   */
  @ParameterizedTest
  @MethodSource("symTypeExprAndExpectedFullNameProvider")
  public void shouldPrintFullName(@NotNull SymTypeExpression symType, @NotNull String expected) {
    Preconditions.checkNotNull(symType);
    Preconditions.checkNotNull(expected);
    Preconditions.checkArgument(!expected.isEmpty());

    // When
    String actual = symType.printFullName();

    // Then
    Assertions.assertEquals(expected, actual);
  }

  protected static Stream<Arguments> symTypeExprAndExpectedFullNameProvider() {
    List<SymTypeExpression> symTypes = createSymTypeExpressions();

    return Stream.of(
      Arguments.of(symTypes.get(0), "a.b.c.X"),
      Arguments.of(symTypes.get(1), "a.b.c.X"),
      Arguments.of(symTypes.get(2), "a.b.c.X"),
      Arguments.of(symTypes.get(3), "Y"),
      Arguments.of(symTypes.get(4), "a.b.c.Y")
    );
  }

  protected static List<SymTypeExpression> createSymTypeExpressions() {
    Preconditions.checkState(ArcBasisMill.globalScope().resolveType("a.b.c.X").isPresent());
    // Sym type where the type symbol was already resolved beforehand
    SymTypeExpression x = SymTypeExpressionFactory.createTypeObject(ArcBasisMill.globalScope()
      .resolveType("a.b.c.X").get());

    // Sym type where the type symbol is resolved via an import statement
    IArcBasisArtifactScope scope = ArcBasisMill.artifactScope();
    scope.setEnclosingScope(ArcBasisMill.globalScope());
    scope.setPackageName("somePackage");
    scope.setName("");
    scope.setImportsList(Collections.singletonList(new ImportStatement("a.b.c.X", false)));
    SymTypeExpression x2 = SymTypeExpressionFactory.createTypeObject("X", scope);

    // Sym type that states the type symbol's fully qualified name
    IArcBasisArtifactScope scope2 = ArcBasisMill.artifactScope();
    scope2.setEnclosingScope(ArcBasisMill.globalScope());
    scope2.setPackageName("anotherPackage");
    scope2.setName("");
    SymTypeExpression x3 = SymTypeExpressionFactory.createTypeObject("a.b.c.X", scope2);

    // Sym type where the type symbol cannot be resolved
    SymTypeExpression y = SymTypeExpressionFactory.createTypeObject("Y", scope2);
    SymTypeExpression y2 = SymTypeExpressionFactory.createTypeObject("a.b.c.Y", scope2);

    return Arrays.asList(x, x2, x3, y, y2);
  }
}