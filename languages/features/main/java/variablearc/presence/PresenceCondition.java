/* (c) https://github.com/MontiCore/monticore */
package variablearc.presence;

import com.google.common.base.Preconditions;
import de.monticore.expressions.expressionsbasis._ast.ASTExpression;
import org.codehaus.commons.nullanalysis.NotNull;
import variablearc.VariableArcMill;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * The conjunction of the (negated) conditions of the varif branches that
 * enclose an element of a component type, in the vocabulary of that type.
 * The empty condition always holds.
 */
public record PresenceCondition(@NotNull List<Literal> literals) {

  private static final PresenceCondition TOP = new PresenceCondition(List.of());

  public PresenceCondition {
    Preconditions.checkNotNull(literals);
    literals = List.copyOf(literals);
  }

  public static PresenceCondition top() {
    return TOP;
  }

  public boolean isTop() {
    return this.literals().isEmpty();
  }

  /**
   * @return this condition additionally requiring the literal
   */
  public PresenceCondition and(@NotNull Literal literal) {
    Preconditions.checkNotNull(literal);
    List<Literal> literals = new ArrayList<>(this.literals());
    literals.add(literal);
    return new PresenceCondition(literals);
  }

  public String print() {
    if (this.isTop()) return "true";
    if (this.literals().size() == 1) return this.literals().get(0).print();
    return this.literals().stream().map(Literal::print).collect(Collectors.joining(" && ", "(", ")"));
  }

  /**
   * @param condition the condition of a varif
   * @param negated   whether the element is in the else branch of the varif
   */
  public record Literal(@NotNull ASTExpression condition, boolean negated) {

    public Literal {
      Preconditions.checkNotNull(condition);
    }

    public String print() {
      return (this.negated() ? "!" : "") + VariableArcMill.prettyPrint(this.condition(), false).trim();
    }

    @Override
    public boolean equals(Object other) {
      return other instanceof Literal that && this.condition() == that.condition() && this.negated() == that.negated();
    }

    @Override
    public int hashCode() {
      return 31 * System.identityHashCode(this.condition()) + Boolean.hashCode(this.negated());
    }
  }
}
