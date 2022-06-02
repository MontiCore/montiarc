/* (c) https://github.com/MontiCore/monticore */
package arcautomaton._visitor;

import com.google.common.base.Preconditions;
import de.monticore.scbasis._ast.ASTSCTransition;
import de.monticore.scbasis._visitor.SCBasisVisitor2;
import org.codehaus.commons.nullanalysis.NotNull;

import java.util.ArrayList;
import java.util.List;

public class SCTransitionsCollector implements SCBasisVisitor2 {
  protected final List<ASTSCTransition> transitions;

  public SCTransitionsCollector() {
    this.transitions = new ArrayList<>();
  }

  public List<ASTSCTransition> getTransitions() {
    return transitions;
  }

  @Override
  public void visit(@NotNull ASTSCTransition node) {
    Preconditions.checkNotNull(node);
    this.getTransitions().add(node);
  }
}
