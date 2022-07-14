/* (c) https://github.com/MontiCore/monticore */
package montiarc._symboltable;

import de.monticore.class2mc.OOClass2MCResolver;
import de.monticore.symbols.basicsymbols._symboltable.TypeSymbol;
import de.monticore.symbols.oosymbols._symboltable.OOTypeSymbol;
import montiarc.AbstractTest;
import montiarc.MontiArcMill;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;

import java.util.Optional;

/**
 * Holds tests that check whether types adapted via the {@link de.monticore.class2mc.OOClass2MCResolver} can be resolved
 */
public class Class2MCResolvingTest extends AbstractTest {

  @BeforeEach
  @Override
  public void init() {
    MontiArcMill.globalScope().clear();
    MontiArcMill.init();
  }

  @Test
  public void shouldFindAdaptedTypes() {
    // Given
    IMontiArcScope scope = MontiArcMill.scope();
    MontiArcMill.globalScope().addAdaptedTypeSymbolResolver(new OOClass2MCResolver());
    MontiArcMill.globalScope().addSubScope(scope);
    String type = "java.util.Date";

    // When
    Optional<TypeSymbol> symbol = scope.resolveType(type);

    // Then
    Assertions.assertTrue(symbol.isPresent());
  }

  @Test
  public void shouldNotFindUnadaptedTypes() {
    // Given
    IMontiArcScope scope = MontiArcMill.scope();
    MontiArcMill.globalScope().addSubScope(scope);
    String type = "java.util.Date";

    // When
    Optional<TypeSymbol> symbol = scope.resolveType(type);

    // Then
    Assertions.assertFalse(symbol.isPresent());
  }

  @Test
  public void shouldFindAdaptedOOTypes() {
    // Given
    IMontiArcScope scope = MontiArcMill.scope();
    MontiArcMill.globalScope().addAdaptedOOTypeSymbolResolver(new OOClass2MCResolver());
    MontiArcMill.globalScope().addSubScope(scope);
    String type = "java.util.Date";

    // When
    Optional<OOTypeSymbol> symbol = scope.resolveOOType(type);

    // Then
    Assertions.assertTrue(symbol.isPresent());
  }

  @Disabled("Currently unsupported, either a bug or by design. Update after MC discussion")
  @Test
  public void shouldFindTypeFromAdaptedOOTypes() {
    // Given
    IMontiArcScope scope = MontiArcMill.scope();
    MontiArcMill.globalScope().addAdaptedOOTypeSymbolResolver(new OOClass2MCResolver());
    MontiArcMill.globalScope().addSubScope(scope);
    String type = "java.util.Date";

    // When
    Optional<TypeSymbol> symbol = scope.resolveType(type);

    // Then
    Assertions.assertTrue(symbol.isPresent());
  }

  @Test
  public void shouldFindTypeWithBothAdapter() {
    // Given
    IMontiArcScope scope = MontiArcMill.scope();
    MontiArcMill.globalScope().addAdaptedTypeSymbolResolver(new OOClass2MCResolver());
    MontiArcMill.globalScope().addAdaptedOOTypeSymbolResolver(new OOClass2MCResolver());
    MontiArcMill.globalScope().addSubScope(scope);
    String type = "java.util.Date";

    // When
    Optional<TypeSymbol> symbol = scope.resolveType(type);

    // Then
    Assertions.assertTrue(symbol.isPresent());
  }

  @Test
  public void shouldFindOOTypeWithBothAdapter() {
    // Given
    IMontiArcScope scope = MontiArcMill.scope();
    MontiArcMill.globalScope().addAdaptedTypeSymbolResolver(new OOClass2MCResolver());
    MontiArcMill.globalScope().addAdaptedOOTypeSymbolResolver(new OOClass2MCResolver());
    MontiArcMill.globalScope().addSubScope(scope);
    String type = "java.util.Date";

    // When
    Optional<OOTypeSymbol> symbol = scope.resolveOOType(type);

    // Then
    Assertions.assertTrue(symbol.isPresent());
  }
}