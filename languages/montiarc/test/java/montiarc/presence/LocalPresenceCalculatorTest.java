/* (c) https://github.com/MontiCore/monticore */
package montiarc.presence;

import de.monticore.symbols.compsymbols._symboltable.ComponentTypeSymbol;
import de.monticore.symbols.compsymbols._symboltable.SubcomponentSymbol;
import montiarc.MontiArcTestBase;
import montiarc._ast.ASTMACompilationUnit;
import org.junit.jupiter.api.Test;
import variablearc.VariableArcMill;
import variablearc._ast.ASTVariableArcComponentType;
import variablearc._symboltable.IVariableArcScope;
import variablearc.presence.LocalPresence;
import variablearc.presence.LocalPresenceCalculator;

import java.util.HashMap;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * The class under test is {@link LocalPresenceCalculator}.
 */
public class LocalPresenceCalculatorTest extends MontiArcTestBase {

  protected static final String MODEL = """
    package a;
    component C {
      feature T, A, B, D;
      varif (A) {
        feature F;
        varif (B) { feature G; }
        constraint (D);
        component Inner inner { feature I; }
      } else {
        feature H;
      }
    }""";

  @Test
  public void shouldConditionElementsOnEnclosingVarIfs() {
    // Given
    ComponentTypeSymbol c = compile(MODEL).getArcComponentType().getSymbol();
    IVariableArcScope scope = (IVariableArcScope) c.getSpannedScope();
    LocalPresence presence = ((ASTVariableArcComponentType) c.getAstNode()).getLocalPresence().orElseThrow();

    // When && Then
    assertThat(presence.of(scope.resolveArcFeatureLocally("T").orElseThrow()).print()).isEqualTo("true");
    assertThat(presence.of(scope.resolveArcFeatureLocally("F").orElseThrow()).print()).isEqualTo("A");
    assertThat(presence.of(scope.resolveArcFeatureLocally("G").orElseThrow()).print()).isEqualTo("(A && B)");
    assertThat(presence.of(scope.resolveArcFeatureLocally("H").orElseThrow()).print()).isEqualTo("!A");
  }

  @Test
  public void shouldConditionConstraints() {
    // Given
    ComponentTypeSymbol c = compile(MODEL).getArcComponentType().getSymbol();
    LocalPresence presence = ((ASTVariableArcComponentType) c.getAstNode()).getLocalPresence().orElseThrow();

    // When && Then
    assertThat(presence.getConstraints().size()).isEqualTo(1);
    assertThat(presence.getConstraints().get(0).condition().print()).isEqualTo("A");
    assertThat(VariableArcMill.prettyPrint(presence.getConstraints().get(0).constraint(), false).trim()).isEqualTo("D");
  }

  @Test
  public void shouldCalculateInnerComponentTypesSeparately() {
    // Given
    ComponentTypeSymbol c = compile(MODEL).getArcComponentType().getSymbol();
    ComponentTypeSymbol inner = c.getSpannedScope().resolveComponentTypeLocally("Inner").orElseThrow();
    LocalPresence innerPresence = ((ASTVariableArcComponentType) inner.getAstNode()).getLocalPresence().orElseThrow();

    // When && Then
    assertThat(((ASTVariableArcComponentType) c.getAstNode()).getLocalPresence().orElseThrow().of(inner).print()).isEqualTo("A");
    assertThat(innerPresence.of(((IVariableArcScope) inner.getSpannedScope()).resolveArcFeatureLocally("I").orElseThrow()).print()).isEqualTo("true");
    assertThat(innerPresence.getConstraints()).isEmpty();
  }

  @Test
  public void shouldConditionInstancesOfInnerComponentTypesOnEnclosingVarIfs() {
    // Given
    ComponentTypeSymbol c = compile(MODEL).getArcComponentType().getSymbol();
    SubcomponentSymbol inner = c.getSpannedScope().resolveSubcomponentLocally("inner").orElseThrow();

    // When && Then
    assertThat(((ASTVariableArcComponentType) c.getAstNode()).getLocalPresence().orElseThrow().of(inner).print()).isEqualTo("A");
  }

  @Test
  public void shouldBeReusable() {
    // Given
    ASTMACompilationUnit ast = compile(MODEL);
    LocalPresence first = ((ASTVariableArcComponentType) ast.getArcComponentType()).getLocalPresence().orElseThrow();

    // When
    new LocalPresenceCalculator().calculate(ast.getArcComponentType());
    LocalPresence second = ((ASTVariableArcComponentType) ast.getArcComponentType()).getLocalPresence().orElseThrow();

    // Then
    assertThat(new HashMap<>(second.getNodes())).isEqualTo(new HashMap<>(first.getNodes()));
    assertThat(second.getConstraints()).isEqualTo(first.getConstraints());
  }

  @Test
  public void shouldConsiderSymbolsWithoutNodePresent() {
    // Given
    ComponentTypeSymbol c = compile(MODEL).getArcComponentType().getSymbol();

    // When && Then
    assertThat(((ASTVariableArcComponentType) c.getAstNode()).getLocalPresence().orElseThrow()
      .of(VariableArcMill.arcFeatureSymbolBuilder().setName("X").build()).print()).isEqualTo("true");
  }
}
