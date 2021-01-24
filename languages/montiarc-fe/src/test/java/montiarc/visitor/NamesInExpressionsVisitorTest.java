/* (c) https://github.com/MontiCore/monticore */
package montiarc.visitor;

import de.monticore.expressions.expressionsbasis._ast.ASTNameExpression;
import montiarc.AbstractTest;
import montiarc._parser.MontiArcParser;
import montiarc._visitor.NamesInExpressionsVisitor;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.io.IOException;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class NamesInExpressionsVisitorTest extends AbstractTest {

  @ParameterizedTest
  @MethodSource("expressionAndVariableNamesProvider")
  public void shouldFindNames(String expression, List<String> names) throws IOException {
    MontiArcParser parser = new MontiArcParser();
    NamesInExpressionsVisitor visitor = new NamesInExpressionsVisitor();
    Objects.requireNonNull(parser.parse_StringExpression(expression).orElse(null)).accept(visitor);
    Map<ASTNameExpression, NamesInExpressionsVisitor.ExpressionKind> foundNodes
      = visitor.getFoundNames();
    List<String> foundNames = foundNodes.keySet().stream()
      .map(ASTNameExpression::getName)
      .collect(Collectors.toList());

    Assertions.assertEquals(foundNames.size(), names.size());
    Assertions.assertTrue(foundNames.containsAll(names));
    Assertions.assertTrue(names.containsAll(foundNames));
  }

  static public Stream<Arguments> expressionAndVariableNamesProvider() {
    return Stream.of(Arguments.of("a = 4 + b", Arrays.asList("a", "b")),
      Arguments.of("c == d", Arrays.asList("c", "d")),
      Arguments.of("5 == 5", new ArrayList<>()));
  }
}