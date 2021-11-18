/* (c) https://github.com/MontiCore/monticore */
package montiarc.generator.util;

import arcbasis._symboltable.ComponentInstanceSymbol;
import arcbasis._symboltable.ComponentTypeSymbol;
import arcbasis._symboltable.PortSymbol;
import arcbasis.check.CompSymTypeExpression;
import com.google.common.base.Preconditions;
import de.monticore.symbols.basicsymbols._symboltable.VariableSymbol;
import de.monticore.symboltable.ISymbol;
import de.monticore.types.check.SymTypeExpression;
import montiarc.MontiArcMill;
import org.codehaus.commons.nullanalysis.NotNull;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.mockito.ArgumentMatchers;
import org.mockito.Mockito;

import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.stream.Stream;

/**
 * Holds test for {@link Identifier}
 */
public class IdentifierTest {

  /**
   * Method under test {@link Identifier#nameEquals(ISymbol, String)}
   */
  @ParameterizedTest
  @MethodSource("symbolAndIdentifierProvider")
  public void shouldCorrectlyDetectIdentifier(@NotNull ISymbol symbol, @NotNull String id, boolean equals) {
    Preconditions.checkNotNull(symbol);
    Preconditions.checkNotNull(id);

    // Given
    Identifier identifier = Mockito.mock(Identifier.class);
    Mockito.when(identifier.nameEquals(Mockito.any(), Mockito.any())).thenCallRealMethod();

    // When && Then
    Assertions.assertEquals(equals, identifier.nameEquals(symbol, id));
  }

  protected static Stream<Arguments> symbolAndIdentifierProvider() {
    ISymbol symbol1 = Mockito.mock(ISymbol.class);
    Mockito.when(symbol1.getName()).thenReturn("a");
    ISymbol symbol2 = Mockito.mock(ISymbol.class);
    Mockito.when(symbol2.getName()).thenReturn("");
    ISymbol symbol3 = Mockito.mock(ISymbol.class);
    Mockito.when(symbol3.getName()).thenReturn("a.b");
    return Stream.of(
      Arguments.of(symbol1, "a", true),
      Arguments.of(symbol1, "b", false),
      Arguments.of(symbol2, "", true),
      Arguments.of(symbol2, " ", false),
      Arguments.of(symbol2, "a", false),
      Arguments.of(symbol3, "a.b", true),
      Arguments.of(symbol3, "a", false),
      Arguments.of(symbol3, "b", false),
      Arguments.of(symbol3, ".", false));
  }

  /**
   * Method under test {@link Identifier#containsIdentifier(Collection, String)}
   */
  @ParameterizedTest
  @MethodSource("symbolsAndIdentifierProvider")
  public void shouldCorrectlyDetectIdentifier(@NotNull Collection<ISymbol> symbols,
                                              @NotNull String id, boolean equals) {
    Preconditions.checkNotNull(symbols);
    Preconditions.checkNotNull(id);

    // Given
    Identifier identifier = Mockito.mock(Identifier.class);
    Mockito.when(identifier.containsIdentifier(ArgumentMatchers.<Collection<ISymbol>>any(), Mockito.any())).thenCallRealMethod();
    Mockito.when(identifier.nameEquals(Mockito.any(), Mockito.any())).thenCallRealMethod();

    // When && Then
    Assertions.assertEquals(equals, identifier.containsIdentifier(symbols, id));
  }

  protected static Stream<Arguments> symbolsAndIdentifierProvider() {
    ISymbol symbol1 = Mockito.mock(ISymbol.class);
    Mockito.when(symbol1.getName()).thenReturn("a");
    ISymbol symbol2 = Mockito.mock(ISymbol.class);
    Mockito.when(symbol2.getName()).thenReturn("b");
    ISymbol symbol3 = Mockito.mock(ISymbol.class);
    Mockito.when(symbol3.getName()).thenReturn("c");
    return Stream.of(
      Arguments.of(Collections.singletonList(symbol1), "a", true),
      Arguments.of(Collections.singletonList(symbol1), "b", false),
      Arguments.of(Arrays.asList(symbol1, symbol2, symbol3), "a", true),
      Arguments.of(Arrays.asList(symbol1, symbol2, symbol3), "b", true),
      Arguments.of(Arrays.asList(symbol1, symbol2, symbol3), "c", true),
      Arguments.of(Arrays.asList(symbol1, symbol2, symbol3), "d", false));
  }

  /**
   * Method under test {@link Identifier#containsIdentifier(ComponentTypeSymbol, String)}
   */
  @ParameterizedTest
  @MethodSource("componentAndIdentifierProvider")
  public void shouldCorrectlyDetectIdentifier(@NotNull ComponentTypeSymbol component,
                                              @NotNull String id, boolean equals) {
    Preconditions.checkNotNull(component);
    Preconditions.checkNotNull(id);

    // Given
    Identifier identifier = Mockito.mock(Identifier.class);
    Mockito.when(identifier.containsIdentifier(Mockito.any(ComponentTypeSymbol.class), Mockito.any())).thenCallRealMethod();
    Mockito.when(identifier.containsIdentifier(ArgumentMatchers.<Collection<ISymbol>>any(), Mockito.any())).thenCallRealMethod();
    Mockito.when(identifier.nameEquals(Mockito.any(), Mockito.any())).thenCallRealMethod();

    // When && Then
    Assertions.assertEquals(equals, identifier.containsIdentifier(component, id));
  }

  protected static Stream<Arguments> componentAndIdentifierProvider() {
    ComponentTypeSymbol component = MontiArcMill.componentTypeSymbolBuilder()
      .setName("Comp").setSpannedScope(MontiArcMill.scope()).build();
    PortSymbol port = MontiArcMill.portSymbolBuilder()
      .setName("a").setIncoming(true).setType(Mockito.mock(SymTypeExpression.class)).build();
    VariableSymbol parameter = MontiArcMill.variableSymbolBuilder()
      .setName("b").setType(Mockito.mock(SymTypeExpression.class)).build();
    VariableSymbol field = MontiArcMill.fieldSymbolBuilder()
      .setName("c").setType(Mockito.mock(SymTypeExpression.class)).build();
    ComponentInstanceSymbol subcomponent = MontiArcMill.componentInstanceSymbolBuilder()
      .setName("d").setType(Mockito.mock(CompSymTypeExpression.class)).build();
    component.getSpannedScope().add(port);
    component.getSpannedScope().add(parameter);
    component.addParameter(parameter);
    component.getSpannedScope().add(field);
    component.getSpannedScope().add(subcomponent);
    return Stream.of(
      Arguments.of(component, "a", true),
      Arguments.of(component, "b", true),
      Arguments.of(component, "c", true),
      Arguments.of(component, "d", true),
      Arguments.of(component, "e", false));
  }

  /**
   * Method under test {@link Identifier#updateNameMapping(ComponentTypeSymbol)}
   */
  @Test
  public void shouldUpdateResultNameOnly() {
    // Given
    ComponentTypeSymbol component = MontiArcMill.componentTypeSymbolBuilder()
      .setName("Comp").setSpannedScope(MontiArcMill.scope()).build();
    VariableSymbol field = MontiArcMill.fieldSymbolBuilder()
      .setName(Identifier.RESULT_NAME).setType(Mockito.mock(SymTypeExpression.class)).build();
    component.getSpannedScope().add(field);

    // When
    Identifier identifier = Identifier.getNewIdentifier(component);

    // Then
    Assertions.assertEquals(Identifier.PREFIX + Identifier.RESULT_NAME, identifier.getResultName());
    Assertions.assertEquals(Identifier.INPUT_NAME, identifier.getInputName());
    Assertions.assertEquals(Identifier.BEHAVIOR_IMPL_NAME, identifier.getBehaviorImplName());
    Assertions.assertEquals(Identifier.CURRENT_STATE_NAME, identifier.getCurrentStateName());
  }

  /**
   * Method under test {@link Identifier#updateNameMapping(ComponentTypeSymbol)}
   */
  @ParameterizedTest
  @MethodSource("componentAndExpectedNamesProvider")
  public void shouldUpdateExpectedNamesOnly(@NotNull ComponentTypeSymbol component,
                                            @NotNull String expectedResultName,
                                            @NotNull String expectedInputName,
                                            @NotNull String expectedBehaviorImplName,
                                            @NotNull String expectedCurrentStateName) {
    Preconditions.checkNotNull(component);
    Preconditions.checkNotNull(expectedResultName);
    Preconditions.checkNotNull(expectedInputName);
    Preconditions.checkNotNull(expectedBehaviorImplName);
    Preconditions.checkNotNull(expectedCurrentStateName);

    // Given
    Identifier identifier = new Identifier(Mockito.mock(ComponentTypeSymbol.class));

    // When
    identifier.updateNameMapping(component);

    // Then
    Assertions.assertEquals(expectedResultName, identifier.getResultName());
    Assertions.assertEquals(expectedInputName, identifier.getInputName());
    Assertions.assertEquals(expectedBehaviorImplName, identifier.getBehaviorImplName());
    Assertions.assertEquals(expectedCurrentStateName, identifier.getCurrentStateName());
  }

  /**
   * Method under test {@link Identifier#getNewIdentifier(ComponentTypeSymbol)}
   */
  @ParameterizedTest
  @MethodSource("componentAndExpectedNamesProvider")
  public void shouldConstructExpectedIdentifier(@NotNull ComponentTypeSymbol component,
                                                @NotNull String expectedResultName,
                                                @NotNull String expectedInputName,
                                                @NotNull String expectedBehaviorImplName,
                                                @NotNull String expectedCurrentStateName) {
    Preconditions.checkNotNull(component);
    Preconditions.checkNotNull(expectedResultName);
    Preconditions.checkNotNull(expectedInputName);
    Preconditions.checkNotNull(expectedBehaviorImplName);
    Preconditions.checkNotNull(expectedCurrentStateName);

    // When
    Identifier identifier = Identifier.getNewIdentifier(component);

    // Then
    Assertions.assertEquals(expectedResultName, identifier.getResultName());
    Assertions.assertEquals(expectedInputName, identifier.getInputName());
    Assertions.assertEquals(expectedBehaviorImplName, identifier.getBehaviorImplName());
    Assertions.assertEquals(expectedCurrentStateName, identifier.getCurrentStateName());
  }

  protected static Stream<Arguments> componentAndExpectedNamesProvider() {
    ComponentTypeSymbol component1 = MontiArcMill.componentTypeSymbolBuilder()
      .setName("Comp").setSpannedScope(MontiArcMill.scope()).build();
    ComponentTypeSymbol component2 = MontiArcMill.componentTypeSymbolBuilder()
      .setName("Comp").setSpannedScope(MontiArcMill.scope()).build();
    ComponentTypeSymbol component3 = MontiArcMill.componentTypeSymbolBuilder()
      .setName("Comp").setSpannedScope(MontiArcMill.scope()).build();
    ComponentTypeSymbol component4 = MontiArcMill.componentTypeSymbolBuilder()
      .setName("Comp").setSpannedScope(MontiArcMill.scope()).build();
    VariableSymbol field1 = MontiArcMill.fieldSymbolBuilder()
      .setName(Identifier.RESULT_NAME).setType(Mockito.mock(SymTypeExpression.class)).build();
    VariableSymbol field2 = MontiArcMill.fieldSymbolBuilder()
      .setName(Identifier.INPUT_NAME).setType(Mockito.mock(SymTypeExpression.class)).build();
    VariableSymbol field3 = MontiArcMill.fieldSymbolBuilder()
      .setName(Identifier.BEHAVIOR_IMPL_NAME).setType(Mockito.mock(SymTypeExpression.class)).build();
    VariableSymbol field4 = MontiArcMill.fieldSymbolBuilder()
      .setName(Identifier.CURRENT_STATE_NAME).setType(Mockito.mock(SymTypeExpression.class)).build();
    component1.getSpannedScope().add(field1);
    component2.getSpannedScope().add(field2);
    component3.getSpannedScope().add(field3);
    component4.getSpannedScope().add(field4);
    return Stream.of(
      Arguments.of(component1, Identifier.PREFIX + Identifier.RESULT_NAME, Identifier.INPUT_NAME,
        Identifier.BEHAVIOR_IMPL_NAME, Identifier.CURRENT_STATE_NAME),
      Arguments.of(component2, Identifier.RESULT_NAME, Identifier.PREFIX + Identifier.INPUT_NAME,
        Identifier.BEHAVIOR_IMPL_NAME, Identifier.CURRENT_STATE_NAME),
      Arguments.of(component3, Identifier.RESULT_NAME, Identifier.INPUT_NAME,
        Identifier.PREFIX + Identifier.BEHAVIOR_IMPL_NAME, Identifier.CURRENT_STATE_NAME),
      Arguments.of(component4, Identifier.RESULT_NAME, Identifier.INPUT_NAME,
        Identifier.BEHAVIOR_IMPL_NAME, Identifier.PREFIX + Identifier.CURRENT_STATE_NAME));
  }
}