/* (c) https://github.com/MontiCore/monticore */
package arcbasis._symboltable;

import arcbasis.ArcBasisMill;
import arcbasis._ast.ASTArcElement;
import arcbasis._visitor.ArcBasisTraverser;
import com.google.common.base.Preconditions;
import org.codehaus.commons.nullanalysis.NotNull;

public class ArcBasisSymbolTableCompleterDelegator {

  protected IArcBasisGlobalScope globalScope;

  protected ArcBasisTraverser traverser;

  protected ArcBasisTraverser getTraverser() {
    return this.traverser;
  }

  public ArcBasisSymbolTableCompleterDelegator() {
    this.globalScope = ArcBasisMill.globalScope();
    this.traverser = ArcBasisMill.traverser();
    this.init();
  }

  protected void init() {
    this.initArcBasis();
  }

  protected void initArcBasis() {
    ArcBasisSymbolTableCompleter arcBasisSymbolTableCompleter = ArcBasisMill.symbolTableCompleter();
    this.getTraverser().add4ArcBasis(arcBasisSymbolTableCompleter);
    this.getTraverser().setArcBasisHandler(arcBasisSymbolTableCompleter);
  }

  public void createFromAST(@NotNull ASTArcElement rootNode) {
    Preconditions.checkNotNull(rootNode);
    rootNode.accept(traverser);
  }
}