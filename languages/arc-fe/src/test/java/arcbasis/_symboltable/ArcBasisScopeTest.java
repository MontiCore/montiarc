/* (c) https://github.com/MontiCore/monticore */
package arcbasis._symboltable;

import arcbasis.AbstractTest;
import arcbasis.ArcBasisMill;
import de.monticore.symbols.basicsymbols._symboltable.TypeSymbol;
import de.monticore.symbols.oosymbols._symboltable.OOTypeSymbol;
import org.codehaus.commons.nullanalysis.NotNull;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.Optional;
import java.util.stream.Stream;

public class ArcBasisScopeTest extends AbstractTest {

  protected IArcBasisScope scope;

  protected static Stream<Arguments> validScopeAndTypeAndResolutionNameProvider() {
    return Stream.of(Arguments.of("", "A", "A"), Arguments.of("x", "A", "A"), Arguments.of("x", "A", "x.A"),
      Arguments.of("x", "A", "B.x.A"), Arguments.of("", "x.A", "x.A"), Arguments.of("y", "x.A", "x.A"),
      Arguments.of("y", "x.A", "y.x.A"), Arguments.of("y", "x.A", "B.y.x.A"));
  }

  protected static Stream<Arguments> invalidScopeAndTypeAndResolutionNameProvider() {
    return Stream.of(Arguments.of("", "A", ""), Arguments.of("", "", "A"), Arguments.of("", "", "B"),
      Arguments.of("", "A", "B"), Arguments.of("", "x.A", "A"), Arguments.of("x", "A", "B.A"));
  }

  protected IArcBasisScope getScope() {
    return this.scope;
  }

  @BeforeEach
  public void setUp() {
    this.scope = ArcBasisMill.scope();
    IArcBasisArtifactScope aScope = ArcBasisMill.artifactScope();
    aScope.setName("B");
    aScope.addSubScope(this.getScope());
    ArcBasisMill.globalScope().addSubScope(aScope);
  }

  @ParameterizedTest
  @MethodSource("validScopeAndTypeAndResolutionNameProvider")
  public void shouldResolveType(@NotNull String scopeName, @NotNull String typeName, @NotNull String resolutionName) {
    //Given
    this.getScope().setName(scopeName);
    this.getScope().add(ArcBasisMill.typeSymbolBuilder()
      .setName(typeName).setSpannedScope(ArcBasisMill.scope()).build());

    //When
    Optional<TypeSymbol> symbol = this.getScope().resolveType(resolutionName);

    //Then
    Assertions.assertTrue(symbol.isPresent());
  }

  @ParameterizedTest
  @MethodSource("invalidScopeAndTypeAndResolutionNameProvider")
  public void shouldNotResolveType(@NotNull String scopeName, @NotNull String typeName,
                                   @NotNull String resolutionName) {
    //Given
    this.getScope().setName(scopeName);
    this.getScope().add(ArcBasisMill.typeSymbolBuilder()
      .setName(typeName).setSpannedScope(ArcBasisMill.scope()).build());

    //When
    Optional<OOTypeSymbol> symbol = this.getScope().resolveOOType(resolutionName);

    //Then
    Assertions.assertFalse(symbol.isPresent());
  }

  @ParameterizedTest
  @MethodSource("validScopeAndTypeAndResolutionNameProvider")
  public void shouldResolveOOType(@NotNull String scopeName, @NotNull String typeName, @NotNull String resolutionName) {
    //Given
    this.getScope().setName(scopeName);
    this.getScope().add(ArcBasisMill.oOTypeSymbolBuilder()
      .setName(typeName).setSpannedScope(ArcBasisMill.scope()).build());

    //When
    Optional<OOTypeSymbol> symbol = this.getScope().resolveOOType(resolutionName);

    //Then
    Assertions.assertTrue(symbol.isPresent());
  }

  @ParameterizedTest
  @MethodSource("invalidScopeAndTypeAndResolutionNameProvider")
  public void shouldNotResolveOOType(@NotNull String scopeName, @NotNull String typeName,
                                     @NotNull String resolutionName) {
    //Given
    this.getScope().setName(scopeName);
    this.getScope().add(ArcBasisMill.oOTypeSymbolBuilder()
      .setName(typeName).setSpannedScope(ArcBasisMill.scope()).build());

    //When
    Optional<OOTypeSymbol> symbol = this.getScope().resolveOOType(resolutionName);

    //Then
    Assertions.assertFalse(symbol.isPresent());
  }

  @ParameterizedTest
  @MethodSource("validScopeAndTypeAndResolutionNameProvider")
  public void shouldResolveComponentType(@NotNull String scopeName, @NotNull String typeName,
                                         @NotNull String resolutionName) {
    //Given
    this.getScope().setName(scopeName);
    this.getScope().add(ArcBasisMill.componentTypeSymbolBuilder()
      .setName(typeName).setSpannedScope(ArcBasisMill.scope()).build());

    //When
    Optional<ComponentTypeSymbol> symbol = this.getScope().resolveComponentType(resolutionName);

    //Then
    Assertions.assertTrue(symbol.isPresent());
  }

  @ParameterizedTest
  @MethodSource("invalidScopeAndTypeAndResolutionNameProvider")
  public void shouldNotResolveComponentType(@NotNull String scopeName, @NotNull String typeName,
                                            @NotNull String resolutionName) {
    //Given
    this.getScope().setName(scopeName);
    this.getScope().add(ArcBasisMill.componentTypeSymbolBuilder()
      .setName(typeName).setSpannedScope(ArcBasisMill.scope()).build());

    //When
    Optional<ComponentTypeSymbol> symbol = this.getScope().resolveComponentType(resolutionName);

    //Then
    Assertions.assertFalse(symbol.isPresent());
  }
}