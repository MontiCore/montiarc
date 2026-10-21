/* (c) https://github.com/MontiCore/monticore */
package variablearc._ast;

import arcbasis._ast.ASTArcComponentType;
import com.google.common.base.Preconditions;
import com.microsoft.z3.BoolExpr;
import de.monticore.ast.ASTNode;
import org.codehaus.commons.nullanalysis.NotNull;
import variablearc.presence.LocalPresence;

import java.util.Collections;
import java.util.IdentityHashMap;
import java.util.Map;
import java.util.Optional;

/**
 * A component type that stores the presence of the nodes of its AST, which
 * is calculated from the AST. The presence refers to the nodes of this AST
 * and is hence not copied by {@link #deepClone()}.
 */
public class ASTVariableArcComponentType extends ASTArcComponentType {

  protected LocalPresence localPresence;
  protected Map<ASTNode, BoolExpr> nodePresence = Map.of();

  protected ASTVariableArcComponentType() {
    super();
  }

  public Optional<LocalPresence> getLocalPresence() {
    return Optional.ofNullable(this.localPresence);
  }

  public void setLocalPresence(@NotNull LocalPresence presence) {
    this.localPresence = Preconditions.checkNotNull(presence);
  }

  /**
   * @return the presence formulas of the nodes that are not always present,
   * in the vocabulary of the component type
   */
  public Map<ASTNode, BoolExpr> getNodePresence() {
    return this.nodePresence;
  }

  public void setNodePresence(@NotNull Map<ASTNode, BoolExpr> presence) {
    Preconditions.checkNotNull(presence);
    this.nodePresence = Collections.unmodifiableMap(new IdentityHashMap<>(presence));
  }
}
