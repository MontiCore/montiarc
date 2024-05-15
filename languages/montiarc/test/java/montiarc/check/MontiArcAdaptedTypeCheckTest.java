/* (c) https://github.com/MontiCore/monticore */
package montiarc.check;

import arcbasis._symboltable.IArcBasisScope;
import arcbasis._symboltable.TransitiveScopeSetter;
import com.google.common.base.Preconditions;
import de.monticore.class2mc.OOClass2MCResolver;
import de.monticore.expressions.expressionsbasis._ast.ASTExpression;
import de.se_rwth.commons.logging.Log;
import montiarc.MontiArcAbstractTest;
import montiarc.MontiArcMill;
import org.assertj.core.api.Assertions;
import org.codehaus.commons.nullanalysis.NotNull;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.junit.jupiter.params.provider.ValueSource;

import java.io.IOException;
import java.util.stream.Stream;

/**
 * This class provides tests for validating the correctness of type checking of
 * adapted MontiArc symbols.
 * <p>
 * The class under test is {@link MontiArcTypeCalculator}.
 */
public class MontiArcAdaptedTypeCheckTest extends MontiArcAbstractTest {

  /**
   * The enclosing scope of the symbols of the test setup
   */
  protected IArcBasisScope scope;

  @BeforeAll
  public static void log() {
    Log.enableFailQuick(false);
  }

  @BeforeEach
  @Override
  public void setUp() {
    super.setUp();
    MontiArcMill.globalScope().addAdaptedTypeSymbolResolver(new OOClass2MCResolver());
    MontiArcMill.globalScope().addAdaptedOOTypeSymbolResolver(new OOClass2MCResolver());
    this.initSymbols();
  }

  protected void initSymbols() {
    compile("component Super<T> {" +
      "port in T pGenInh; " +
      "}");
    scope = compile("component A<K extends java.lang.Integer>(int p1) extends Super<K> {" +
      "feature f; " +
      "port out int pOut; " +
      "int field = 1; " +
      "component B(int pSub) { " +
      "component C sub { feature fSubSub; } " +
      "int fieldSub = 1; " +
      "feature fSub; " +
      "port in int pIn; " +
      "} " +
      "B sub(p1); " +
      "}").getComponentType().getSpannedScope();
  }

  @ParameterizedTest
  @ValueSource(strings = {
    "pOut == 1", // Port
    "p1 == 1", // Parameter
    "field == 1", // Field
    "f", // Feature
    "sub", // Subcomponent
    "sub.fSub", // Sub-feature
    "sub.pIn == 1", // Sub-port
    "field = pGenInh" // Inherited generic port
  })
  public void testValidExpression(@NotNull String expr) throws IOException {
    Preconditions.checkNotNull(expr);
    Preconditions.checkArgument(!expr.isBlank());

    MontiArcTypeCalculator tc = new MontiArcTypeCalculator();
    TransitiveScopeSetter scopeSetter = new TransitiveScopeSetter();

    // Given
    ASTExpression ast = MontiArcMill.parser()
      .parse_StringExpression(expr)
      .orElseThrow();
    scopeSetter.setScope(ast, this.scope);

    // When
    tc.deriveType(ast).getResult();

    // Then
    Assertions.assertThat(Log.getFindings())
      .as("Expression " + expr + " should be valid, there shouldn't be any findings.")
      .isEmpty();
  }

  @ParameterizedTest
  @MethodSource("invalidExpression")
  public void testInvalidExpression(@NotNull String expr,
                                    @NotNull String... errors) throws IOException {
    Preconditions.checkNotNull(expr);
    Preconditions.checkNotNull(errors);
    Preconditions.checkArgument(!expr.isBlank());
    Preconditions.checkArgument(errors.length > 0);

    MontiArcTypeCalculator tc = new MontiArcTypeCalculator();
    TransitiveScopeSetter scopeSetter = new TransitiveScopeSetter();

    // Given
    ASTExpression ast = MontiArcMill.parser()
      .parse_StringExpression(expr)
      .orElseThrow();
    scopeSetter.setScope(ast, this.scope);

    // When
    tc.deriveType(ast).getResult();

    // Then
    Assertions.assertThat(Log.getFindings())
      .as("Expression " + expr + " should be invalid, there should be findings.")
      .isNotEmpty();
    Assertions.assertThat(this.collectErrorCodes(Log.getFindings()))
      .as("Expression " + expr + " is invalid, the findings should contain exactly the expected error.")
      .containsExactly(errors);
  }

  protected static Stream<Arguments> invalidExpression() {
    return Stream.of(
      Arguments.of("B", new String[]{"0xFD118"}), // Component Type
      //Arguments.of("sub.fieldSub == 1", new String[]{"0xF737F"}), // Sub-field
      //Arguments.of("sub.pSub == 1", new String[]{"0xF737F"}), // Sub-parameter
      Arguments.of("sub.sub", new String[]{"0xF737F"}), // Sub-instance
      Arguments.of("sub.C", new String[]{"0xF737F"}), // Sub-Component Type
      Arguments.of("sub.sub.fSubSub", new String[]{"0xF735F", "0xF737F"}) // Sub-sub-feature
    );
  }
}
