/* (c) https://github.com/MontiCore/monticore */
package arcbasis._symboltable;

import arcbasis.ArcBasisMill;
import com.google.common.base.Preconditions;
import de.monticore.symbols.basicsymbols._symboltable.TypeSymbol;
import de.monticore.symboltable.ImportStatement;
import de.monticore.types.check.SymTypeExpression;
import de.monticore.types.check.SymTypeExpressionFactory;
import de.monticore.types.check.SymTypeOfGenerics;
import de.se_rwth.commons.logging.Log;
import de.se_rwth.commons.logging.LogStub;
import org.apache.commons.lang3.StringUtils;
import org.codehaus.commons.nullanalysis.NotNull;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Stream;

/**
 * Holds tests for {@link de.monticore.types.check.SymTypeOfGenerics}.
 */
public class SymTypeOfGenericsTest {

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
    TypeSymbol x = ArcBasisMill.typeSymbolBuilder().setName("X")
      .setEnclosingScope(scope).setSpannedScope(ArcBasisMill.scope()).build();
    x.getSpannedScope().add(ArcBasisMill.typeVarSymbolBuilder().setName("T")
      .setEnclosingScope(x.getSpannedScope()).build());
    scope.add(x);

    // Create type symbol a.b.c.Y
    TypeSymbol y = ArcBasisMill.typeSymbolBuilder().setName("Y")
      .setEnclosingScope(scope).setSpannedScope(ArcBasisMill.scope()).build();
    y.getSpannedScope().add(ArcBasisMill.typeVarSymbolBuilder().setName("R")
      .setEnclosingScope(y.getSpannedScope()).build());
    scope.add(y);

    // Create type symbol a.b.c.Z
    TypeSymbol z = ArcBasisMill.typeSymbolBuilder().setName("Z")
      .setEnclosingScope(scope).setSpannedScope(ArcBasisMill.scope()).build();
    z.getSpannedScope().add(ArcBasisMill.typeVarSymbolBuilder().setName("R")
      .setEnclosingScope(z.getSpannedScope()).build());
    z.getSpannedScope().add(ArcBasisMill.typeVarSymbolBuilder().setName("Q")
      .setEnclosingScope(z.getSpannedScope()).build());
    scope.add(z);

    // Create scope d.e.f
    IArcBasisArtifactScope scope2 = ArcBasisMill.artifactScope();
    scope2.setEnclosingScope(ArcBasisMill.globalScope());
    scope2.setImportsList(new ArrayList<>());
    scope2.setPackageName("d.e.f");
    scope2.setName("");


    // Create type symbol d.e.f.V
    TypeSymbol v = ArcBasisMill.typeSymbolBuilder().setName("V")
      .setEnclosingScope(scope2).setSpannedScope(ArcBasisMill.scope()).build();
    scope2.add(v);

    // Create scope g.h.i
    IArcBasisArtifactScope scope3 = ArcBasisMill.artifactScope();
    scope3.setEnclosingScope(ArcBasisMill.globalScope());
    scope3.setImportsList(new ArrayList<>());
    scope3.setPackageName("g.h.i");
    scope3.setName("");

    // Create type symbol g.h.i.W
    TypeSymbol w = ArcBasisMill.typeSymbolBuilder().setName("W")
      .setEnclosingScope(scope3).setSpannedScope(ArcBasisMill.scope()).build();
    scope3.add(w);
  }

  /**
   * Method under test {@link SymTypeOfGenerics#print()}.
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
    Assertions.assertEquals(StringUtils.deleteWhitespace(expected), StringUtils.deleteWhitespace(actual));
  }

  protected static Stream<Arguments> symTypeExprAndExpectedNameProvider() {
    List<SymTypeExpression> symTypes = createSymTypeExpressions();

    return Stream.of(
      Arguments.of(symTypes.get(0), "X<T>"),
      Arguments.of(symTypes.get(1), "Y<V>"),
      Arguments.of(symTypes.get(2), "Z<V, W>"),
      Arguments.of(symTypes.get(3), "X<Y<V>>"),
      Arguments.of(symTypes.get(4), "X<>"),
      Arguments.of(symTypes.get(5), "Y<V>"),
      Arguments.of(symTypes.get(6), "Z<V, W>"),
      Arguments.of(symTypes.get(7), "X<Y<V>>"),
      Arguments.of(symTypes.get(8), "X<>"),
      Arguments.of(symTypes.get(9), "Y<V>"),
      Arguments.of(symTypes.get(10), "Z<V, W>"),
      Arguments.of(symTypes.get(11), "X<Y<V>>")
    );
  }

  /**
   * Method under test {@link SymTypeOfGenerics#printFullName()}.
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
    Assertions.assertEquals(StringUtils.deleteWhitespace(expected), StringUtils.deleteWhitespace(actual));
  }

  protected static Stream<Arguments> symTypeExprAndExpectedFullNameProvider() {
    List<SymTypeExpression> symTypes = createSymTypeExpressions();

    return Stream.of(
      Arguments.of(symTypes.get(0), "a.b.c.X<a.b.c.X.T>"),
      Arguments.of(symTypes.get(1), "a.b.c.Y<d.e.f.V>"),
      Arguments.of(symTypes.get(2), "a.b.c.Z<d.e.f.V, g.h.i.W>"),
      Arguments.of(symTypes.get(3), "a.b.c.X<a.b.c.Y<d.e.f.V>>"),
      Arguments.of(symTypes.get(4), "a.b.c.X<>"),
      Arguments.of(symTypes.get(5), "a.b.c.Y<d.e.f.V>"),
      Arguments.of(symTypes.get(6), "a.b.c.Z<d.e.f.V, g.h.i.W>"),
      Arguments.of(symTypes.get(7), "a.b.c.X<a.b.c.Y<d.e.f.V>>"),
      Arguments.of(symTypes.get(8), "a.b.c.X<>"),
      Arguments.of(symTypes.get(9), "a.b.c.Y<d.e.f.V>"),
      Arguments.of(symTypes.get(10), "a.b.c.Z<d.e.f.V, g.h.i.W>"),
      Arguments.of(symTypes.get(11), "a.b.c.X<a.b.c.Y<d.e.f.V>>")
    );
  }

  protected static List<SymTypeExpression> createSymTypeExpressions() {
    Preconditions.checkState(ArcBasisMill.globalScope().resolveType("d.e.f.V").isPresent());
    Preconditions.checkState(ArcBasisMill.globalScope().resolveType("g.h.i.W").isPresent());
    Preconditions.checkState(ArcBasisMill.globalScope().resolveType("a.b.c.X").isPresent());
    Preconditions.checkState(ArcBasisMill.globalScope().resolveType("a.b.c.Y").isPresent());
    Preconditions.checkState(ArcBasisMill.globalScope().resolveType("a.b.c.Z").isPresent());

    // Sym type without arguments where the type symbol was already resolved beforehand
    SymTypeExpression x = SymTypeExpressionFactory.createGenericsDeclaredType(ArcBasisMill.globalScope()
      .resolveType("a.b.c.X").get());

    // Sym type with argument where the type symbol was already resolved beforehand
    SymTypeExpression y = SymTypeExpressionFactory.createGenerics(ArcBasisMill.globalScope()
        .resolveType("a.b.c.Y").get(),
      SymTypeExpressionFactory.createTypeObject(ArcBasisMill.globalScope()
        .resolveType("d.e.f.V").get())
    );

    // Sym the with two arguments where the type symbol was already resolved beforehand
    SymTypeExpression z = SymTypeExpressionFactory.createGenerics(ArcBasisMill.globalScope()
        .resolveType("a.b.c.Z").get(),
      SymTypeExpressionFactory.createTypeObject(ArcBasisMill.globalScope()
        .resolveType("d.e.f.V").get()),
      SymTypeExpressionFactory.createTypeObject(ArcBasisMill.globalScope()
        .resolveType("g.h.i.W").get())
    );

    // Sym type X<Y<V>> with nested generics where the type symbol was already resolved beforehand
    SymTypeExpression xy = SymTypeExpressionFactory.createGenerics(ArcBasisMill.globalScope()
        .resolveType("a.b.c.X").get(),
      SymTypeExpressionFactory.createGenerics(ArcBasisMill.globalScope()
          .resolveType("a.b.c.Y").get(),
        SymTypeExpressionFactory.createTypeObject(ArcBasisMill.globalScope()
          .resolveType("d.e.f.V").get())
      )
    );

    // Scope with import statements
    IArcBasisArtifactScope scope = ArcBasisMill.artifactScope();
    scope.setEnclosingScope(ArcBasisMill.globalScope());
    scope.setPackageName("somePackage");
    scope.setName("");
    scope.setImportsList(Arrays.asList(
      new ImportStatement("d.e.f.V", false),
      new ImportStatement("g.h.i.W", false),
      new ImportStatement("a.b.c.X", false),
      new ImportStatement("a.b.c.Y", false),
      new ImportStatement("a.b.c.Z", false)
    ));

    // Sym type without arguments where the type symbol is resolved via an import statement
    SymTypeExpression x2 = SymTypeExpressionFactory.createGenerics(scope.resolveType("X").orElseThrow(), List.of());

    // Sym type with argument where the type symbol is resolved via an import statement
    SymTypeExpression y2 = SymTypeExpressionFactory.createGenerics( scope.resolveType("Y").orElseThrow(),
      SymTypeExpressionFactory.createTypeObject(scope.resolveType("V").orElseThrow())
    );

    // Sym type with two arguments where the type symbol is resolved via an import statement
    SymTypeExpression z2 = SymTypeExpressionFactory.createGenerics(scope.resolveType("Z").orElseThrow(),
      SymTypeExpressionFactory.createTypeObject(scope.resolveType("V").orElseThrow()),
      SymTypeExpressionFactory.createTypeObject(scope.resolveType("W").orElseThrow())
    );

    // Sym type X<Y<V>> with nested generics where the type symbol is resolved via an import statement
    SymTypeExpression xy2 = SymTypeExpressionFactory.createGenerics(scope.resolveType("X").orElseThrow(),
      SymTypeExpressionFactory.createGenerics(scope.resolveType("Y").orElseThrow(),
        SymTypeExpressionFactory.createTypeObject(scope.resolveType("V").orElseThrow())
      )
    );

    // Scope without import statements
    IArcBasisArtifactScope scope2 = ArcBasisMill.artifactScope();
    scope2.setEnclosingScope(ArcBasisMill.globalScope());
    scope2.setPackageName("anotherPackage");
    scope2.setName("");

    // Sym type without type arguments that states the type symbol's fully qualified name
    SymTypeExpression x3 = SymTypeExpressionFactory.createGenerics(scope2.resolveType("a.b.c.X").orElseThrow(), List.of());

    // Sym type with type argument that states the type symbol's fully qualified name
    SymTypeExpression y3 = SymTypeExpressionFactory.createGenerics(scope2.resolveType("a.b.c.Y").orElseThrow(),
      SymTypeExpressionFactory.createTypeObject(scope2.resolveType("d.e.f.V").orElseThrow())
    );

    // Sym type with two type arguments that states the type symbol's fully qualified name
    SymTypeExpression z3 = SymTypeExpressionFactory.createGenerics(scope2.resolveType("a.b.c.Z").orElseThrow(),
      SymTypeExpressionFactory.createTypeObject(scope2.resolveType("d.e.f.V").orElseThrow()),
      SymTypeExpressionFactory.createTypeObject(scope2.resolveType("g.h.i.W").orElseThrow())
    );

    // Sym type X<Y<V>> with nested generics that state the type symbol's fully qualified name
    SymTypeExpression xy3 = SymTypeExpressionFactory.createGenerics(scope.resolveType("a.b.c.X").orElseThrow(),
      SymTypeExpressionFactory.createGenerics(scope.resolveType("a.b.c.Y").orElseThrow(),
        SymTypeExpressionFactory.createTypeObject(scope.resolveType("d.e.f.V").orElseThrow())
      )
    );

    return Arrays.asList(x, y, z, xy, x2, y2, z2, xy2, x3, y3, z3, xy3);
  }
}