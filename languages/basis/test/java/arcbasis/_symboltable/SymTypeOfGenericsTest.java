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
      Arguments.of(symTypes.get(8), "a.b.c.X<>"),
      Arguments.of(symTypes.get(9), "a.b.c.Y<d.e.f.V>"),
      Arguments.of(symTypes.get(10), "a.b.c.Z<d.e.f.V, g.h.i.W>"),
      Arguments.of(symTypes.get(11), "a.b.c.X<a.b.c.Y<d.e.f.V>>"),
      Arguments.of(symTypes.get(12), "M<>"),
      Arguments.of(symTypes.get(13), "a.b.c.M<>"),
      Arguments.of(symTypes.get(14), "M<N>"),
      Arguments.of(symTypes.get(15), "a.b.c.M<d.e.f.N>"),
      Arguments.of(symTypes.get(16), "M<N, O>"),
      Arguments.of(symTypes.get(17), "a.b.c.M<d.e.f.N, g.h.i.O>")
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
      Arguments.of(symTypes.get(11), "a.b.c.X<a.b.c.Y<d.e.f.V>>"),
      Arguments.of(symTypes.get(12), "M<>"),
      Arguments.of(symTypes.get(13), "a.b.c.M<>"),
      Arguments.of(symTypes.get(14), "M<N>"),
      Arguments.of(symTypes.get(15), "a.b.c.M<d.e.f.N>"),
      Arguments.of(symTypes.get(16), "M<N, O>"),
      Arguments.of(symTypes.get(17), "a.b.c.M<d.e.f.N, g.h.i.O>")
    );
  }

  protected static List<SymTypeExpression> createSymTypeExpressions() {
    Preconditions.checkState(ArcBasisMill.globalScope().resolveType("d.e.f.V").isPresent());
    Preconditions.checkState(ArcBasisMill.globalScope().resolveType("g.h.i.W").isPresent());
    Preconditions.checkState(ArcBasisMill.globalScope().resolveType("a.b.c.X").isPresent());
    Preconditions.checkState(ArcBasisMill.globalScope().resolveType("a.b.c.Y").isPresent());
    Preconditions.checkState(ArcBasisMill.globalScope().resolveType("a.b.c.Z").isPresent());

    // Sym type without arguments where the type symbol was already resolved beforehand
    SymTypeExpression x = SymTypeExpressionFactory.createGenerics(ArcBasisMill.globalScope()
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
    SymTypeExpression x2 = SymTypeExpressionFactory.createGenerics("X", scope);

    // Sym type with argument where the type symbol is resolved via an import statement
    SymTypeExpression y2 = SymTypeExpressionFactory.createGenerics("Y", scope,
      SymTypeExpressionFactory.createTypeObject("V", scope)
    );

    // Sym type with two arguments where the type symbol is resolved via an import statement
    SymTypeExpression z2 = SymTypeExpressionFactory.createGenerics("Z", scope,
      SymTypeExpressionFactory.createTypeObject("V", scope),
      SymTypeExpressionFactory.createTypeObject("W", scope)
    );

    // Sym type X<Y<V>> with nested generics where the type symbol is resolved via an import statement
    SymTypeExpression xy2 = SymTypeExpressionFactory.createGenerics("X", scope,
      SymTypeExpressionFactory.createGenerics("Y", scope,
        SymTypeExpressionFactory.createTypeObject("V", scope)
      )
    );

    // Scope without import statements
    IArcBasisArtifactScope scope2 = ArcBasisMill.artifactScope();
    scope2.setEnclosingScope(ArcBasisMill.globalScope());
    scope2.setPackageName("anotherPackage");
    scope2.setName("");

    // Sym type without type arguments that states the type symbol's fully qualified name
    SymTypeExpression x3 = SymTypeExpressionFactory.createGenerics("a.b.c.X", scope2);

    // Sym type with type argument that states the type symbol's fully qualified name
    SymTypeExpression y3 = SymTypeExpressionFactory.createGenerics("a.b.c.Y", scope2,
      SymTypeExpressionFactory.createTypeObject("d.e.f.V", scope2)
    );

    // Sym type with two type arguments that states the type symbol's fully qualified name
    SymTypeExpression z3 = SymTypeExpressionFactory.createGenerics("a.b.c.Z", scope2,
      SymTypeExpressionFactory.createTypeObject("d.e.f.V", scope2),
      SymTypeExpressionFactory.createTypeObject("g.h.i.W", scope2)
    );

    // Sym type X<Y<V>> with nested generics that state the type symbol's fully qualified name
    SymTypeExpression xy3 = SymTypeExpressionFactory.createGenerics("a.b.c.X", scope,
      SymTypeExpressionFactory.createGenerics("a.b.c.Y", scope,
        SymTypeExpressionFactory.createTypeObject("d.e.f.V", scope)
      )
    );

    // Sym type without type arguments where the type symbol cannot be resolved
    SymTypeExpression m = SymTypeExpressionFactory.createGenerics("M", scope2);
    SymTypeExpression m2 = SymTypeExpressionFactory.createGenerics("a.b.c.M", scope2);

    // Sym type with type argument where the type symbol cannot be resolved
    SymTypeExpression mn = SymTypeExpressionFactory.createGenerics("M", scope2,
      SymTypeExpressionFactory.createTypeObject("N", scope2)
    );
    SymTypeExpression mn2 = SymTypeExpressionFactory.createGenerics("a.b.c.M", scope2,
      SymTypeExpressionFactory.createTypeObject("d.e.f.N", scope2)
    );

    // Sym type with two type arguments where the type symbol cannot be resolved
    SymTypeExpression mno = SymTypeExpressionFactory.createGenerics("M", scope2,
      SymTypeExpressionFactory.createTypeObject("N", scope2),
      SymTypeExpressionFactory.createTypeObject("O", scope2)
    );
    SymTypeExpression mno2 = SymTypeExpressionFactory.createGenerics("a.b.c.M", scope2,
      SymTypeExpressionFactory.createTypeObject("d.e.f.N", scope2),
      SymTypeExpressionFactory.createTypeObject("g.h.i.O", scope2)
    );

    return Arrays.asList(x, y, z, xy, x2, y2, z2, xy2, x3, y3, z3, xy3, m, m2, mn, mn2, mno, mno2);
  }
}