/* (c) https://github.com/MontiCore/monticore */
package arcautomaton._visitor;

import com.google.common.base.Preconditions;
import de.monticore.scbasis._ast.ASTSCState;
import de.monticore.scbasis._visitor.SCBasisVisitor2;
import org.codehaus.commons.nullanalysis.NotNull;

import java.util.Stack;

public class SCSuperStateResolver implements SCBasisVisitor2 {
  protected final ASTSCState state;
  protected final Stack<ASTSCState> superStates;
  protected ASTSCState result;

  public SCSuperStateResolver(@NotNull ASTSCState state) {
    Preconditions.checkNotNull(state);
    this.state = state;
    this.superStates = new Stack<>();
  }

  protected ASTSCState getState() {
    return this.state;
  }

  protected Stack<ASTSCState> getSuperStates() {
    return this.superStates;
  }

  public ASTSCState getResult() {
    return result;
  }

  public void setResult(@NotNull ASTSCState result) {
    Preconditions.checkNotNull(result);
    this.result = result;
  }

  @Override
  public void visit(@NotNull ASTSCState node) {
    Preconditions.checkNotNull(node);
    if (node.equals(this.getState()) && !this.getSuperStates().isEmpty()) {
      this.setResult(this.getSuperStates().peek());
    }
    this.getSuperStates().push(node);
  }

  @Override
  public void endVisit(@NotNull ASTSCState node) {
    Preconditions.checkNotNull(node);
    Preconditions.checkState(node.equals(this.getSuperStates().peek()));
    this.getSuperStates().pop();
  }
}
