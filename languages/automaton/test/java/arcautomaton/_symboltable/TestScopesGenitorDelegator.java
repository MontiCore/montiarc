/* (c) https://github.com/MontiCore/monticore */
package arcautomaton._symboltable;

import arcbasis._ast.ASTComponentType;
import com.google.common.base.Preconditions;
import org.codehaus.commons.nullanalysis.NotNull;

public class TestScopesGenitorDelegator extends ArcAutomatonScopesGenitorDelegator {

  public void handle(@NotNull ASTComponentType ast) {
    Preconditions.checkNotNull(ast);
    ast.accept(this.traverser);
  }
}
