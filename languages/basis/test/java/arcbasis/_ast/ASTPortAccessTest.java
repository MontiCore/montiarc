/* (c) https://github.com/MontiCore/monticore */
package arcbasis._ast;

import arcbasis.AbstractTest;
import arcbasis.ArcBasisMill;
import arcbasis._symboltable.ComponentInstanceSymbol;
import arcbasis._symboltable.ComponentTypeSymbol;
import arcbasis._symboltable.IArcBasisScope;
import arcbasis._symboltable.PortSymbol;
import arcbasis._visitor.ArcBasisTraverser;
import arcbasis.check.TypeExprOfComponent;
import com.google.common.base.Preconditions;
import de.monticore.types.check.SymTypeExpression;
import de.se_rwth.commons.logging.Log;
import org.codehaus.commons.nullanalysis.NotNull;
import org.codehaus.commons.nullanalysis.Nullable;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.params.provider.MethodSource;
import org.junit.jupiter.params.provider.ValueSource;
import org.mockito.Mockito;

import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Holds the tests for {@link ASTPortAccess}.
 */
public class ASTPortAccessTest extends AbstractTest {

  @ParameterizedTest
  @ValueSource(strings = {"sub1", "sub2"})
  public void shouldReturnSubcomponentSymbol(@NotNull String subcomponent) {
    Preconditions.checkNotNull(subcomponent);

    //Given
    ASTPortAccess portAccess = ArcBasisMill.portAccessBuilder()
      .setPort("").setComponent(subcomponent).build();
    portAccess.setEnclosingScope(this.createTestScope());

    ArcBasisTraverser traverser = ArcBasisMill.traverser();
    traverser.add4ArcBasis(ArcBasisMill.symbolTableCompleter());
    portAccess.accept(traverser);

    //When
    ComponentInstanceSymbol resolvedSubcomponent = portAccess.getComponentSymbol();

    //Then
    Assertions.assertTrue(Log.getFindings().isEmpty());
    Assertions.assertEquals(subcomponent, resolvedSubcomponent.getName());
  }

  @ParameterizedTest
  @ValueSource(strings = {"", "sub3"})
  public void shouldNotFindSubcomponentSymbol(@NotNull String subcomponent) {
    Preconditions.checkNotNull(subcomponent);

    //Given
    ASTPortAccess portAccess = ArcBasisMill.portAccessBuilder()
      .setPort("").setComponent(subcomponent).build();
    portAccess.setEnclosingScope(this.createTestScope());

    ArcBasisTraverser traverser = ArcBasisMill.traverser();
    traverser.add4ArcBasis(ArcBasisMill.symbolTableCompleter());
    portAccess.accept(traverser);

    //When
    ComponentInstanceSymbol resolvedSubcomponent = portAccess.getComponentSymbol();

    //Then
    Assertions.assertTrue(Log.getFindings().isEmpty());
    Assertions.assertNull(resolvedSubcomponent);
  }

  @ParameterizedTest
  @MethodSource("validPortAndSubcomponentProvider")
  public void shouldReturnPortSymbol(@Nullable String subcomponent, @NotNull String port) {
    Preconditions.checkNotNull(port);

    //Given
    ASTPortAccess portAccess = ArcBasisMill.portAccessBuilder()
      .setPort(port).setComponent(subcomponent).build();
    portAccess.setEnclosingScope(this.createTestScope());

    ArcBasisTraverser traverser = ArcBasisMill.traverser();
    traverser.add4ArcBasis(ArcBasisMill.symbolTableCompleter());
    portAccess.accept(traverser);

    //When
    PortSymbol resolvedPort = portAccess.getPortSymbol();

    //Then
    Assertions.assertTrue(Log.getFindings().isEmpty());
    Assertions.assertEquals(port, resolvedPort.getName());
  }

  @SuppressWarnings("unused")
  protected static Stream<Arguments> validPortAndSubcomponentProvider() {
    return Stream.of(
      Arguments.of(null, "i1"),
      Arguments.of(null, "i2"),
      Arguments.of(null, "o1"),
      Arguments.of("sub1", "i1"),
      Arguments.of("sub1", "i3"),
      Arguments.of("sub1", "o2"));
  }

  @ParameterizedTest
  @MethodSource("invalidPortAndSubcomponentProvider")
  public void shouldNotFindPortSymbol(@Nullable String subcomponent, @NotNull String port) {
    Preconditions.checkNotNull(port);

    //Given
    ASTPortAccess portAccess = ArcBasisMill.portAccessBuilder()
      .setPort(port).setComponent(subcomponent).build();
    portAccess.setEnclosingScope(this.createTestScope());

    ArcBasisTraverser traverser = ArcBasisMill.traverser();
    traverser.add4ArcBasis(ArcBasisMill.symbolTableCompleter());
    portAccess.accept(traverser);

    //When
    PortSymbol resolvedPort = portAccess.getPortSymbol();

    //Then
    Assertions.assertTrue(Log.getFindings().isEmpty());
    Assertions.assertNull(resolvedPort);
  }

  protected static Stream<Arguments> invalidPortAndSubcomponentProvider() {
    return Stream.of(
      Arguments.of("", ""),
      Arguments.of("", "undefPort"),
      Arguments.of("undefComp", "undefPort"),
      Arguments.of("undefComp", ""),
      Arguments.of("", "i1"),
      Arguments.of("", "i2"),
      Arguments.of("", "o1"),
      Arguments.of("", "i3"),
      Arguments.of("", "o2"),
      Arguments.of(null, "i3"),
      Arguments.of(null, "o2"),
      Arguments.of("sub1", "i2"),
      Arguments.of("sub1", "o1"),
      Arguments.of("sub2", ""),
      Arguments.of("sub2", "i2"),
      Arguments.of("sub2", "o1"));
  }

  protected IArcBasisScope createTestScope() {
    IArcBasisScope scope1 = ArcBasisMill.scope();
    IArcBasisScope scope2 = ArcBasisMill.scope();
    IArcBasisScope scope3 = ArcBasisMill.scope();
    ComponentTypeSymbol compA = ArcBasisMill.componentTypeSymbolBuilder()
      .setName("CompA").setSpannedScope(scope1).build();
    ComponentTypeSymbol compB = ArcBasisMill.componentTypeSymbolBuilder().setName("CompB")
      .setSpannedScope(scope2).build();
    ComponentTypeSymbol compC = ArcBasisMill.componentTypeSymbolBuilder().setName("CompC")
      .setSpannedScope(scope3).build();
    ArcBasisMill.globalScope().add(compA);
    ArcBasisMill.globalScope().add(compB);
    ArcBasisMill.globalScope().add(compC);

    PortSymbol port1 = ArcBasisMill.portSymbolBuilder()
      .setName("i1").setIncoming(true).setType(Mockito.mock(SymTypeExpression.class)).build();
    scope1.add(port1);
    port1.setEnclosingScope(scope1);
    PortSymbol port2 = ArcBasisMill.portSymbolBuilder()
      .setName("i2").setIncoming(true).setType(Mockito.mock(SymTypeExpression.class)).build();
    scope1.add(port2);
    port2.setEnclosingScope(scope1);
    PortSymbol port3 = ArcBasisMill.portSymbolBuilder()
      .setName("o1").setIncoming(false).setType(Mockito.mock(SymTypeExpression.class)).build();
    scope1.add(port3);
    port3.setEnclosingScope(scope1);

    PortSymbol port4 = ArcBasisMill.portSymbolBuilder()
      .setName("i1").setIncoming(true).setType(Mockito.mock(SymTypeExpression.class)).build();
    scope2.add(port4);
    port4.setEnclosingScope(scope2);
    PortSymbol port5 = ArcBasisMill.portSymbolBuilder()
      .setName("i3").setIncoming(true).setType(Mockito.mock(SymTypeExpression.class)).build();
    scope2.add(port5);
    port5.setEnclosingScope(scope2);
    PortSymbol port6 = ArcBasisMill.portSymbolBuilder()
      .setName("o2").setIncoming(false).setType(Mockito.mock(SymTypeExpression.class)).build();
    scope2.add(port6);
    port6.setEnclosingScope(scope2);

    ComponentInstanceSymbol sub1 = ArcBasisMill.componentInstanceSymbolBuilder()
      .setName("sub1").setType(new TypeExprOfComponent(compB)).build();
    scope1.add(sub1);
    sub1.setEnclosingScope(scope1);
    ComponentInstanceSymbol sub2 = ArcBasisMill.componentInstanceSymbolBuilder()
      .setName("sub2").setEnclosingScope(scope1).setType(new TypeExprOfComponent(compC)).build();
    scope1.add(sub2);
    sub2.setEnclosingScope(scope1);
    return scope1;
  }

  @ParameterizedTest
  @CsvSource(value = {
    "c, p, c, p",
    "c, p1, c, p2",
    "null, p, null, p",
    "null, p1, null, p2"
  }, nullValues = {"null"})
  public void shouldMatchComponent(@Nullable String c1, @NotNull String p1,
                                   @Nullable String c2, @NotNull String p2) {
    Preconditions.checkNotNull(p1);
    Preconditions.checkNotNull(p2);

    // Given
    ASTPortAccess ref1 = ArcBasisMill.portAccessBuilder()
      .setComponent(c1).setPort(p1).build();
    ASTPortAccess ref2 = ArcBasisMill.portAccessBuilder()
      .setComponent(c2).setPort(p2).build();

    // When && Then
    assertThat(ref1.matchesComponent(ref2)).isTrue();
  }

  @ParameterizedTest
  @CsvSource(value = {
    "c1, p, c2, p",
    "null, p, c, p",
    "c, p, null, p"
  }, nullValues = {"null"})
  public void shouldNotMatchComponent(@Nullable String c1, @NotNull String p1,
                                      @Nullable String c2, @NotNull String p2) {
    Preconditions.checkNotNull(p1);
    Preconditions.checkNotNull(p2);

    // Given
    ASTPortAccess ref1 = ArcBasisMill.portAccessBuilder()
      .setComponent(c1).setPort(p1).build();
    ASTPortAccess ref2 = ArcBasisMill.portAccessBuilder()
      .setComponent(c2).setPort(p2).build();

    // When && Then
    assertThat(ref1.matchesComponent(ref2)).isFalse();
  }

  @ParameterizedTest
  @CsvSource(value = {
    "c, p, c, p",
    "null, p, null, p",
  }, nullValues = {"null"})
  public void shouldMatch(@Nullable String c1, @NotNull String p1,
                          @Nullable String c2, @NotNull String p2) {
    Preconditions.checkNotNull(p1);
    Preconditions.checkNotNull(p2);

    // Given
    ASTPortAccess ref1 = ArcBasisMill.portAccessBuilder()
      .setComponent(c1).setPort(p1).build();
    ASTPortAccess ref2 = ArcBasisMill.portAccessBuilder()
      .setComponent(c2).setPort(p2).build();

    // When && Then
    assertThat(ref1.matches(ref2)).isTrue();
  }


  @ParameterizedTest
  @CsvSource(value = {
    "c, p1, c, p2",
    "c1, p, c2, p",
    "c1, p1, c2, p2",
    "null, p, c, p",
    "c, p, null, p"
  }, nullValues = {"null"})
  public void shouldNotMatch(@Nullable String c1, @NotNull String p1,
                             @Nullable String c2, @NotNull String p2) {
    Preconditions.checkNotNull(p1);
    Preconditions.checkNotNull(p2);

    // Given
    ASTPortAccess ref1 = ArcBasisMill.portAccessBuilder()
      .setComponent(c1).setPort(p1).build();
    ASTPortAccess ref2 = ArcBasisMill.portAccessBuilder()
      .setComponent(c2).setPort(p2).build();

    // When && Then
    assertThat(ref1.matches(ref2)).isFalse();
  }
}