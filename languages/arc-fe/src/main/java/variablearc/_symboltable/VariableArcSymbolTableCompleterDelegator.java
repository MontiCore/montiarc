/* (c) https://github.com/MontiCore/monticore */
package variablearc._symboltable;

import arcbasis.ArcBasisMill;
import arcbasis._ast.ASTArcElement;
import arcbasis._symboltable.ArcBasisSymbolTableCompleter;
import com.google.common.base.Preconditions;
import org.codehaus.commons.nullanalysis.NotNull;
import variablearc.VariableArcMill;
import variablearc._visitor.VariableArcTraverser;

public class VariableArcSymbolTableCompleterDelegator {

  protected IVariableArcGlobalScope globalScope;

  protected VariableArcTraverser traverser;

  protected VariableArcTraverser getTraverser() {
    return this.traverser;
  }

  public VariableArcSymbolTableCompleterDelegator() {
    this.globalScope = VariableArcMill.globalScope();
    this.traverser = VariableArcMill.traverser();
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