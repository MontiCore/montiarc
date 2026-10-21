/* (c) https://github.com/MontiCore/monticore */
package variablearc.presence;

import com.google.common.base.Preconditions;
import de.monticore.ast.ASTNode;
import de.monticore.expressions.expressionsbasis._ast.ASTExpression;
import de.monticore.symboltable.ISymbol;
import org.codehaus.commons.nullanalysis.NotNull;

import java.util.Collections;
import java.util.IdentityHashMap;
import java.util.List;
import java.util.Map;

/**
 * The presence conditions of the elements of a single component type in terms
 * of its own variation points (enclosing varif conditions), i.e., in the
 * vocabulary of that type, calculated from its AST. References the existing
 * AST nodes; elements that are always present have no entry.
 */
public final class LocalPresence {

  private final Map<ASTNode, PresenceCondition> nodes;
  private final List<ConditionalConstraint> constraints;
  private final List<PresenceCondition> delayedBehaviors;

  /**
   * @param delayedBehaviors the presence conditions of the delayed behavior
   *                         descriptions of the component type
   */
  public LocalPresence(@NotNull Map<ASTNode, PresenceCondition> nodes,
                       @NotNull List<ConditionalConstraint> constraints,
                       @NotNull List<PresenceCondition> delayedBehaviors) {
    Preconditions.checkNotNull(nodes);
    Preconditions.checkNotNull(constraints);
    Preconditions.checkNotNull(delayedBehaviors);
    this.nodes = Collections.unmodifiableMap(new IdentityHashMap<>(nodes));
    this.constraints = List.copyOf(constraints);
    this.delayedBehaviors = List.copyOf(delayedBehaviors);
  }

  public static LocalPresence empty() {
    return new LocalPresence(Map.of(), List.of(), List.of());
  }

  public PresenceCondition of(@NotNull ASTNode node) {
    Preconditions.checkNotNull(node);
    return this.nodes.getOrDefault(node, PresenceCondition.top());
  }

  public PresenceCondition of(@NotNull ISymbol symbol) {
    Preconditions.checkNotNull(symbol);
    return symbol.isPresentAstNode() ? this.of(symbol.getAstNode()) : PresenceCondition.top();
  }

  public Map<ASTNode, PresenceCondition> getNodes() {
    return this.nodes;
  }

  public List<ConditionalConstraint> getConstraints() {
    return this.constraints;
  }

  public List<PresenceCondition> getDelayedBehaviors() {
    return this.delayedBehaviors;
  }

  /**
   * @param condition  the presence condition of the constraint declaration
   * @param constraint the constrained expression
   */
  public record ConditionalConstraint(@NotNull PresenceCondition condition, @NotNull ASTExpression constraint) {

    public ConditionalConstraint {
      Preconditions.checkNotNull(condition);
      Preconditions.checkNotNull(constraint);
    }
  }
}
