/* (c) https://github.com/MontiCore/monticore */
package montiarc._symboltable;

import com.google.common.base.Preconditions;
import com.google.common.collect.Lists;
import de.monticore.expressions.expressionsbasis._ast.ASTNameExpression;
import de.monticore.expressions.expressionsbasis._visitor.ExpressionsBasisVisitor2;
import de.monticore.symbols.basicsymbols._symboltable.VariableSymbol;
import de.se_rwth.commons.logging.Log;
import montiarc.MontiArcAbstractTest;
import montiarc.MontiArcMill;
import montiarc.MontiArcTool;
import montiarc._ast.ASTMACompilationUnit;
import montiarc._ast.ASTMontiArcNode;
import montiarc._visitor.MontiArcTraverser;
import org.codehaus.commons.nullanalysis.NotNull;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.function.Executable;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import java.nio.file.Paths;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;

/**
 * Tests whether symbols are created for variables declared by
 * {@link de.monticore.statements.mcvardeclarationstatements._ast.ASTLocalVariableDeclaration}
 */
public class LocalVarDeclarationTest extends MontiArcAbstractTest {

  protected static final String TEST_MODEL_PATH = "symboltable/localvardeclarations/";

  @ParameterizedTest
  @ValueSource(strings = {"UsesLocalVarCorrectly.arc"})
  void shouldCreateResolvableLocalVariableSymbols(@NotNull String model) {
    Preconditions.checkNotNull(model);

    // Given
    List<String> localVarNames = Lists.newArrayList("a", "b", "c");
    MontiArcTool tool = new MontiArcTool();
    tool.initializeClass2MC();

    // When
    ASTMACompilationUnit ast = tool.parse(Paths.get(RELATIVE_MODEL_PATH, TEST_MODEL_PATH, model))
      .orElseThrow(() -> new IllegalStateException(Log.getFindings().toString()));
    tool.createSymbolTable(ast);
    tool.completeSymbolTable(ast);
    tool.runDefaultCoCos(ast);

    // Then
    // Check that all NameExpressions representing local variables resolve to a variable symbol
    Executable[] localVarReferencesResolvableTests =
      NameExpressionCollector.collectNamesIn(ast)
        .stream()
        .filter(expr -> localVarNames.contains(expr.getName()))
        .map(ref -> getVariableResolvabilityTest(ref, true))
        .toArray(Executable[]::new);

    Assertions.assertAll("All references to local variables in NameExpressions should resolve to a variable symbol",
      localVarReferencesResolvableTests
    );
  }

  @ParameterizedTest
  @ValueSource(strings = {"UsesLocalVarOutOfScope.arc"})
  void shouldCreateUnresolvableLocalVariableReferences(@NotNull String model) {
    Preconditions.checkNotNull(model);

    // Given
    List<String> localVarNames = Lists.newArrayList("localBlock1", "localBlock2", "innerLocalBlock1, innerLocalBlock2");
    MontiArcTool tool = new MontiArcTool();
    tool.initializeClass2MC();

    // When
    ASTMACompilationUnit ast = tool.parse(Paths.get(RELATIVE_MODEL_PATH, TEST_MODEL_PATH, model))
      .orElseThrow(() -> new IllegalStateException(Log.getFindings().toString()));
    tool.createSymbolTable(ast);
    tool.completeSymbolTable(ast);
    tool.runDefaultCoCos(ast);

    // Then
    // Check that all NameExpressions representing local variables resolve to a variable symbol
    Executable[] localVarReferencesResolvableTests =
      NameExpressionCollector.collectNamesIn(ast)
        .stream()
        .filter(expr -> localVarNames.contains(expr.getName()))
        .map(ref -> getVariableResolvabilityTest(ref, false))
        .toArray(Executable[]::new);

    Assertions.assertAll("No reference to local variables in NameExpressions should resolve to a variable symbol",
      localVarReferencesResolvableTests
    );
  }

  /**
   * Creates an Assertion test, that test whether or not the given {@link ASTNameExpression} resolves to a
   * {@link VariableSymbol} via the enclosing scope of the name expression.
   * If {@code shouldResolve} is set, then the test succeeds when the symbol could be resolved and fails else.
   * If {@code shouldResolve} is false, then the test fails when the symbol could be resolved and fails else.
   */
  static Executable getVariableResolvabilityTest(@NotNull ASTNameExpression expr, boolean shouldResolve) {
    Preconditions.checkArgument(expr.getEnclosingScope() != null);
    Preconditions.checkArgument(expr.getEnclosingScope() instanceof IMontiArcScope);

    IMontiArcScope scope = (IMontiArcScope) expr.getEnclosingScope();
    Optional<VariableSymbol> optSym = scope.resolveVariable(expr.getName());

    if(shouldResolve) {
      return () -> Assertions.assertTrue( optSym.isPresent(),
        "ASTExpression " + expr.getName() + " at " + expr.get_SourcePositionStart() + " should have been resolved " +
          "to a variable symbol, but was not. ");
    } else {
      return () -> Assertions.assertFalse( optSym.isPresent(),
        "ASTExpression " + expr.getName() + " at " + expr.get_SourcePositionStart() + " was resolved to a variable " +
          "symbol which should not have been possible.");
    }
  }

  static class NameExpressionCollector implements ExpressionsBasisVisitor2 {
    private final Collection<ASTNameExpression> foundNameExpressions = new HashSet<>();

    private NameExpressionCollector() {}

    private Collection<ASTNameExpression> getFoundNameExpressions() {
      return foundNameExpressions;
    }

    @Override
    public void visit(@NotNull ASTNameExpression expr) {
      Preconditions.checkNotNull(expr);
      foundNameExpressions.add(expr);
    }

    static Collection<ASTNameExpression> collectNamesIn(@NotNull ASTMontiArcNode node) {
      Preconditions.checkNotNull(node);

      NameExpressionCollector collector = new NameExpressionCollector();
      MontiArcTraverser traverser = MontiArcMill.traverser();
      traverser.add4ExpressionsBasis(collector);

      node.accept(traverser);
      return collector.getFoundNameExpressions();
    }
  }
}
