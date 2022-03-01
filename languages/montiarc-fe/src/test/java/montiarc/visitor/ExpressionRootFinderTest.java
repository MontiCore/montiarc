/* (c) https://github.com/MontiCore/monticore */
package montiarc.visitor;

import arcautomaton._visitor.ExpressionRootFinder;
import com.google.common.base.Preconditions;
import de.monticore.ast.ASTNode;
import de.monticore.expressions.assignmentexpressions._ast.ASTAssignmentExpression;
import de.monticore.expressions.assignmentexpressions._ast.ASTDecSuffixExpression;
import de.monticore.expressions.assignmentexpressions._ast.ASTIncSuffixExpression;
import de.monticore.expressions.commonexpressions._ast.ASTBracketExpression;
import de.monticore.expressions.commonexpressions._ast.ASTEqualsExpression;
import de.monticore.expressions.commonexpressions._ast.ASTPlusExpression;
import de.monticore.expressions.expressionsbasis._ast.ASTNameExpression;
import de.monticore.expressions.expressionsbasis._visitor.ExpressionsBasisTraverser;
import montiarc.MontiArcMill;
import montiarc._parser.MontiArcParser;
import org.codehaus.commons.nullanalysis.NotNull;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.io.IOException;
import java.util.HashSet;
import java.util.Optional;
import java.util.Set;
import java.util.function.Consumer;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class ExpressionRootFinderTest {

  private static final MontiArcParser PARSER = new MontiArcParser();

  @BeforeAll
  public static void init(){
    MontiArcMill.init();
  }

  @ParameterizedTest
  @MethodSource(value = "provideTestCases")
  public void testFinder(ASTNode node, Set<Class<?>> expected){
    // given
    ExpressionRootFinder rootFinder = new ExpressionRootFinder();
    ExpressionsBasisTraverser traverser = MontiArcMill.inheritanceTraverser();
    traverser.add4ExpressionsBasis(rootFinder);

    // when
    node.accept(traverser);
    Set<Class<?>> actual = rootFinder.getExpressionRoots().stream().map(Object::getClass).collect(Collectors.toSet());

    // then
    Assertions.assertEquals(expected, actual);
  }

  public static Stream<Arguments> provideTestCases() throws IOException {
    return Stream.of(
        new Argument(PARSER.parse_StringNameExpression("a"))
            .is(ASTNameExpression.class),
        new Argument(PARSER.parse_StringArguments("(a + (6 - 5), (z* 6 + b++), u, f = 4+2*4)"))
            .is(ASTNameExpression.class)
            .is(ASTPlusExpression.class)
            .is(ASTBracketExpression.class)
            .is(ASTAssignmentExpression.class),
        new Argument(PARSER.parse_StringSCTransition("A -> B [x==y] / {x++; y--;};"))
            .is(ASTEqualsExpression.class)
            .is(ASTIncSuffixExpression.class)
            .is(ASTDecSuffixExpression.class),
        new Argument(PARSER.parse_StringArcStatechart("automaton {initial state A; A -> A;}"))
        );
  }

  private static class Argument implements Arguments {
    final Object[] arguments;
    final Set<Class<?>> set = new HashSet<>();

    private Argument(@NotNull Optional<? extends ASTNode> node) {
      Preconditions.checkNotNull(node);
      Preconditions.checkArgument(node.isPresent());
      arguments = new Object[]{node.get(), set};
    }

    private Argument is(@NotNull Class<?> type){
      Preconditions.checkNotNull(type);
      set.add(type);
      return this;
    }

    @Override
    public Object[] get() {
      return arguments;
    }
  }

}