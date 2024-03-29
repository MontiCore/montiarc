/* (c) https://github.com/MontiCore/monticore */
package arcbasis.check;

import arcbasis.ArcBasisAbstractTest;
import arcbasis.ArcBasisMill;
import arcbasis._symboltable.ArcPortSymbol;
import arcbasis._symboltable.ComponentTypeSymbol;
import arcbasis._symboltable.SymbolService;
import com.google.common.base.Preconditions;
import de.monticore.symbols.basicsymbols.BasicSymbolsMill;
import de.monticore.symbols.basicsymbols._symboltable.VariableSymbol;
import de.monticore.types.check.CompKindExpression;
import de.monticore.types.check.SymTypeExpression;
import de.monticore.types.check.SymTypeExpressionFactory;
import de.monticore.types.check.SymTypePrimitive;
import org.codehaus.commons.nullanalysis.NotNull;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Named;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.stream.Stream;

public class TypeExprOfComponentTest extends ArcBasisAbstractTest {

  protected static Stream<Arguments> compAndOptionallySurrogateProvider() {
    Named<ComponentTypeSymbol> original = Named.of(
      "CompSymbol",
      createComponent("Comp")
    );
    Named<ComponentTypeSymbol> surrogate = Named.of(
      "CompSurrogate",
      createSurrogateFor(original.getPayload())
    );

    return Stream.of(
      Arguments.of(original, original),
      Arguments.of(original, surrogate)
    );
  }

  /**
   * Method under test {@link TypeExprOfComponent#getSuperComponents()}
   * @param symbolWithDefinitions Provide a component type symbol in for which a parent is added in this test.
   * @param symbolVersionForTypeExpr Set this to {@code symbolWithDefinitions}, or to a surrogate pointing to that
   *                                 symbol. This object will be used to create The ComponentTypeExpression.
   */
  @ParameterizedTest
  @MethodSource("compAndOptionallySurrogateProvider")
  public void getParentShouldReturnExpected(@NotNull ComponentTypeSymbol symbolWithDefinitions,
                                            @NotNull ComponentTypeSymbol symbolVersionForTypeExpr) {
    Preconditions.checkNotNull(symbolWithDefinitions);
    Preconditions.checkNotNull(symbolVersionForTypeExpr);

    // Given
    SymbolService.link(ArcBasisMill.globalScope(), symbolWithDefinitions);
    symbolVersionForTypeExpr.setEnclosingScope(ArcBasisMill.globalScope());

    ComponentTypeSymbol parent = ArcBasisMill.componentTypeSymbolBuilder()
      .setName("Parent")
      .setSpannedScope(ArcBasisMill.scope())
      .build();
    TypeExprOfComponent parentTypeExpr = new TypeExprOfComponent(parent);

    symbolWithDefinitions.setSuperComponentsList(Collections.singletonList(parentTypeExpr));
    TypeExprOfComponent compTypeExpr = new TypeExprOfComponent(symbolVersionForTypeExpr);

    // When
    List<CompKindExpression> parentOfTypeExpr = compTypeExpr.getSuperComponents();

    // Then
    Assertions.assertFalse(parentOfTypeExpr.isEmpty(), "Parent not present.");
    Assertions.assertEquals(parentTypeExpr, parentOfTypeExpr.get(0));
  }

  /**
   * Method under test {@link TypeExprOfComponent#getSuperComponents()}
   */
  @Test
  public void getParentShouldReturnOptionalEmpty() {
    // Given
    ComponentTypeSymbol component = ArcBasisMill.componentTypeSymbolBuilder()
      .setName("Comp")
      .setSpannedScope(ArcBasisMill.scope())
      .build();
    TypeExprOfComponent compTypeExpr = new TypeExprOfComponent(component);

    // When
    List<CompKindExpression> parentOfTypeExpr = compTypeExpr.getSuperComponents();

    // Then
    Assertions.assertTrue(parentOfTypeExpr.isEmpty());
  }

  /**
   * @param symbolWithDefinitions Provide a component type symbol in for which a port is added in this test.
   * @param symbolVersionForTypeExpr Set this to {@code symbolWithDefinitions}, or to a surrogate pointing to that
   *                                 symbol. This object will be used to create The ComponentTypeExpression.
   */
  @ParameterizedTest
  @MethodSource("compAndOptionallySurrogateProvider")
  public void shouldGetTypeExprOfPort(@NotNull ComponentTypeSymbol symbolWithDefinitions,
                                      @NotNull ComponentTypeSymbol symbolVersionForTypeExpr) {
    Preconditions.checkNotNull(symbolWithDefinitions);
    Preconditions.checkNotNull(symbolVersionForTypeExpr);

    // Given
    SymbolService.link(ArcBasisMill.globalScope(), symbolWithDefinitions);
    symbolVersionForTypeExpr.setEnclosingScope(ArcBasisMill.globalScope());

    String portName = "port";
    ArcPortSymbol port = ArcBasisMill.arcPortSymbolBuilder()
      .setName(portName)
      .setType(SymTypeExpressionFactory.createPrimitive(BasicSymbolsMill.INT))
      .setIncoming(true)
      .build();
    symbolWithDefinitions.getSpannedScope().add(port);

    TypeExprOfComponent compTypeExpr = new TypeExprOfComponent(symbolVersionForTypeExpr);

    // When
    Optional<SymTypeExpression> portsType = compTypeExpr.getTypeOfPort(portName);

    // Then
    Assertions.assertTrue(portsType.isPresent(), "Port not present");
    Assertions.assertInstanceOf(SymTypePrimitive.class, portsType.get());
    Assertions.assertEquals(BasicSymbolsMill.INT, portsType.get().print());
  }

  @Test
  public void shouldGetTypeExprOfInheritedPort() {
    // Given
    ComponentTypeSymbol parent = ArcBasisMill.componentTypeSymbolBuilder()
      .setName("Parent")
      .setSpannedScope(ArcBasisMill.scope())
      .build();
    String portName = "port";
    ArcPortSymbol port = ArcBasisMill.arcPortSymbolBuilder()
      .setName(portName)
      .setType(SymTypeExpressionFactory.createPrimitive(BasicSymbolsMill.INT))
      .setIncoming(true)
      .build();
    parent.getSpannedScope().add(port);

    ComponentTypeSymbol component = ArcBasisMill.componentTypeSymbolBuilder()
      .setName("Comp")
      .setSuperComponentsList(Collections.singletonList(new TypeExprOfComponent(parent)))
      .setSpannedScope(ArcBasisMill.scope())
      .build();

    TypeExprOfComponent compTypeExpr = new TypeExprOfComponent(component);

    // When
    Optional<SymTypeExpression> portsType = compTypeExpr.getTypeOfPort(portName);

    // Then
    Assertions.assertTrue(portsType.isPresent());
    Assertions.assertTrue(portsType.get() instanceof SymTypePrimitive);
    Assertions.assertEquals(BasicSymbolsMill.INT, portsType.get().print());
  }

  /**
   * @param symbolWithDefinitions Provide a component type symbol in for which a parameter is added in this test.
   * @param symbolVersionForTypeExpr Set this to {@code symbolWithDefinitions}, or to a surrogate pointing to that
   *                                 symbol. This object will be used to create The ComponentTypeExpression.
   */
  @ParameterizedTest
  @MethodSource("compAndOptionallySurrogateProvider")
  public void shouldGetTypeExprOfParameter(@NotNull ComponentTypeSymbol symbolWithDefinitions,
                                           @NotNull ComponentTypeSymbol symbolVersionForTypeExpr) {
    Preconditions.checkNotNull(symbolWithDefinitions);
    Preconditions.checkNotNull(symbolVersionForTypeExpr);

    // Given
    SymbolService.link(ArcBasisMill.globalScope(), symbolWithDefinitions);
    symbolVersionForTypeExpr.setEnclosingScope(ArcBasisMill.globalScope());

    String paramName = "para";
    VariableSymbol param = ArcBasisMill.variableSymbolBuilder()
      .setName(paramName)
      .setType(SymTypeExpressionFactory.createPrimitive(BasicSymbolsMill.INT))
      .build();
    symbolWithDefinitions.getSpannedScope().add(param);
    symbolWithDefinitions.addParameter(param);

    TypeExprOfComponent compTypeExpr = new TypeExprOfComponent(symbolVersionForTypeExpr);

    // When
    Optional<SymTypeExpression> paramType = compTypeExpr.getTypeOfParameter(paramName);

    // Then
    Assertions.assertTrue(paramType.isPresent(), "Param not present");
    Assertions.assertInstanceOf(SymTypePrimitive.class, paramType.get());
    Assertions.assertEquals(BasicSymbolsMill.INT, paramType.get().print());
  }

  /**
   * Beware that the created symbol is not enclosed by any scope yet.
   */
  protected static ComponentTypeSymbol createComponent(@NotNull String compName) {
    Preconditions.checkNotNull(compName);

    ComponentTypeSymbol symbol = ArcBasisMill.componentTypeSymbolBuilder()
      .setName(compName)
      .setSpannedScope(ArcBasisMill.scope())
      .build();

    return symbol;
  }

  /**
   * Beware that the created surrogate is not enclosed by any scope yet.
   */
  protected static ComponentTypeSymbol createSurrogateFor(@NotNull ComponentTypeSymbol original) {
    Preconditions.checkNotNull(original);

    return ArcBasisMill
      .componentTypeSymbolSurrogateBuilder()
      .setName(original.getFullName())
      .build();
  }
}
