/* (c) https://github.com/MontiCore/monticore */
package arccompute._ast;

import arcbasis._symboltable.IArcBasisScope;
import arccompute.ArcComputeMill;
import arccompute._symboltable.IArcComputeScope;
import de.monticore.statements.mcstatementsbasis._ast.ASTMCBlockStatement;
import de.monticore.statements.mcstatementsbasis._symboltable.IMCStatementsBasisScope;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.mockito.Mockito;

import java.util.stream.Stream;

/**
 * Holds tests for the handwritten methods of {@link ASTArcCompute}.
 */
public class ASTArcComputeTest {

  /**
   * Method under test {@link ASTArcCompute#ASTArcCompute(ASTMCBlockStatement)}.
   */
  @Test
  public void constructorWithBlockStatementShouldBuildCorrectly() {
    //Given
    final ASTMCBlockStatement blockStatement = Mockito.mock(ASTMCBlockStatement.class);

    //When
    final ASTArcCompute compute = new ASTArcCompute(blockStatement);

    //Then
    Assertions.assertEquals(blockStatement, compute.getMCBlockStatement());
  }

  /**
   * Method under test {@link ASTArcCompute#ASTArcCompute(ASTMCBlockStatement)}.
   */
  @Test
  public void constructedWithBlockStatementShouldDetectIllegalArgument() {
    //When && Then
    Assertions.assertThrows(NullPointerException.class, () -> new ASTArcCompute(null));
  }

  /**
   * Method under test {@link ASTArcCompute#setEnclosingScope(IArcComputeScope)}.
   */
  @ParameterizedTest
  @MethodSource("validEnclosingScopeProvider")
  public void setterShouldSetCorrectScope(final IArcComputeScope scope) {
    //Given
    final ASTArcCompute compute = createCompute();

    //When
    compute.setEnclosingScope(scope);

    //Then
    Assertions.assertEquals(scope, compute.getEnclosingScope());
  }

  /**
   * Method under test {@link ASTArcCompute#setEnclosingScope(IArcBasisScope)}.
   */
  @ParameterizedTest
  @MethodSource("validEnclosingScopeProvider")
  public void setterShouldSetCorrectScope(final IArcBasisScope scope) {
    //Given
    final ASTArcCompute compute = createCompute();

    //When
    compute.setEnclosingScope(scope);

    //Then
    Assertions.assertEquals(scope, compute.getEnclosingScope());
  }

  /**
   * Method under test {@link ASTArcCompute#setEnclosingScope(IMCStatementsBasisScope)}.
   */
  @ParameterizedTest
  @MethodSource("validEnclosingScopeProvider")
  public void setterShouldSetCorrectScope(final IMCStatementsBasisScope scope) {
    //Given
    final ASTArcCompute compute = createCompute();

    //When
    compute.setEnclosingScope(scope);

    //Then
    Assertions.assertEquals(scope, compute.getEnclosingScope());
  }

  public static Stream<Arguments> validEnclosingScopeProvider() {
    return Stream.of(Arguments.of(Mockito.mock(IArcComputeScope.class)));
  }

  /**
   * Method under test {@link ASTArcCompute#setEnclosingScope(IArcComputeScope)}.
   */
  @Test
  public void setterShouldSetNullScope() {
    //Given
    final ASTArcCompute compute = createCompute();

    //When
    compute.setEnclosingScope(null);

    //Then
    Assertions.assertEquals(null, compute.getEnclosingScope());
  }

  /**
   * Method under test {@link ASTArcCompute#setEnclosingScope(IArcBasisScope)}.
   */
  @Test
  public void setterWithBehaviorBasisScopeShouldDetectIllegalArgument() {
    //Given
    final IArcBasisScope scope = Mockito.mock(IArcBasisScope.class);
    final ASTArcCompute compute = createCompute();

    //When && Then
    Assertions.assertThrows(IllegalArgumentException.class, () -> compute.setEnclosingScope(scope));
  }

  /**
   * Method under test {@link ASTArcCompute#setEnclosingScope(IMCStatementsBasisScope)}.
   */
  @Test
  public void setterWithStatementsBasisScopeShouldDetectIllegalArgument() {
    //Given
    final IMCStatementsBasisScope scope = Mockito.mock(IMCStatementsBasisScope.class);
    final ASTArcCompute compute = createCompute();

    //When && Then
    Assertions.assertThrows(IllegalArgumentException.class, () -> compute.setEnclosingScope(scope));
  }

  protected ASTArcCompute createCompute() {
    return ArcComputeMill.arcComputeBuilder().setMCBlockStatement(Mockito.mock(ASTMCBlockStatement.class)).build();
  }
}