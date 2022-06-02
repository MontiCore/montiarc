/* (c) https://github.com/MontiCore/monticore */
package montiarc.visitor;

import de.monticore.expressions.expressionsbasis._ast.ASTNameExpression;
import montiarc.AbstractTest;
import montiarc.MontiArcMill;
import montiarc._parser.MontiArcParser;
import montiarc._visitor.MontiArcTraverser;
import arcautomaton._visitor.NamesInExpressionsVisitor;
import org.assertj.core.util.Preconditions;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.io.IOException;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static arcautomaton._visitor.NamesInExpressionsVisitor.*;

public class NamesInExpressionsVisitorTest extends AbstractTest {

  @ParameterizedTest
  @MethodSource("expressionAndVariableNamesProvider")
  public void shouldFindNames(String expression, List<String> names, List<VarAccessKind> kinds) throws IOException {
    MontiArcParser parser = MontiArcMill.parser();
    NamesInExpressionsVisitor visitor = new NamesInExpressionsVisitor();
    MontiArcTraverser traverser = MontiArcMill.traverser();
    visitor.registerTo(traverser);
    Objects.requireNonNull(parser.parse_StringExpression(expression).orElse(null)).accept(traverser);
    Map<ASTNameExpression, VarAccessKind> map
      = visitor.getFoundNames();
    List<ASTNameExpression> found = sort(map.keySet(), names);

    Assertions.assertEquals(names, found.stream().map(ASTNameExpression::getName).collect(Collectors.toList()));
    Assertions.assertEquals(kinds, found.stream().map(map::get).collect(Collectors.toList()), names.toString());
  }

  static public Stream<Arguments> expressionAndVariableNamesProvider() {
    return Stream.of(
        new Tuple("a = 4 + b")
            .is("a").is(VarAccessKind.OVERWRITE)
            .is("b").is(VarAccessKind.DEFAULT_READ)
            .build(),
        new Tuple("c == d")
            .is("c").is(VarAccessKind.DEFAULT_READ)
            .is("d").is(VarAccessKind.DEFAULT_READ)
            .build(),
        new Tuple("e *= 5")
            .is("e").is(VarAccessKind.UPDATE)
            .build(),
        new Tuple("5 == 5")
            .build(),
        new Tuple("t <= !(r)")
            .is("t").is(VarAccessKind.DEFAULT_READ)
            .is("r").is(VarAccessKind.DEFAULT_READ)
            .build(),
        new Tuple("z = x++")
            .is("z").is(VarAccessKind.OVERWRITE)
            .is("x").is(VarAccessKind.UPDATE)
            .build(),
        new Tuple("++f % g")
            .is("g").is(VarAccessKind.DEFAULT_READ)
            .is("f").is(VarAccessKind.UPDATE)
            .build()
    );
  }

  // *************
  // just infrastructure for simpler testing
  // ****

  /**
   * costly method create a list from a set in which the elements are ordered like in the given reference
   * @param toSort set without order
   * @param reference list that purports an order for the given data
   * @return list that contains all given elements in the given order, other elements are just appended to the back
   */
  public static List<ASTNameExpression> sort(Set<ASTNameExpression> toSort, List<String> reference){
    Preconditions.checkNotNull(toSort);
    Preconditions.checkNotNull(reference);

    Set<ASTNameExpression> set = new HashSet<>(toSort);
    List<ASTNameExpression> sorted = new ArrayList<>();
    for(String name : reference) {
      set.removeIf(ex -> {
        if(ex.getName().equals(name)){
          sorted.add(ex);
          return true;
        }
        return false;
      });
    }
    sorted.addAll(set);
    return sorted;
  }

  /**
   * allows simple inline creation of an argument parameter
   */
  public static class Tuple {
    private final String expression;
    private final List<String> names = new ArrayList<>();
    private final List<VarAccessKind> kinds = new ArrayList<>();

    public Tuple(String expression) {
      this.expression = Preconditions.checkNotNull(expression);
    }

    public Tuple is(String name){
      names.add(Preconditions.checkNotNull(name));
      return this;
    }

    public Tuple is(VarAccessKind kind){
      kinds.add(Preconditions.checkNotNull(kind));
      return this;
    }

    public Arguments build(){
      return Arguments.of(expression, names, kinds);
    }
  }
}