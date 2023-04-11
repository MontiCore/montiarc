/* (c) https://github.com/MontiCore/monticore */
package variablearc._symboltable;

import arcbasis.ArcBasisMill;
import arcbasis._ast.ASTArcElement;
import arcbasis._symboltable.ArcBasisScopesGenitorP2;
import com.google.common.base.Preconditions;
import org.codehaus.commons.nullanalysis.NotNull;
import variablearc.VariableArcMill;
import variablearc._visitor.VariableArcTraverser;

public class VariableArcScopesGenitorP2Delegator {

  protected IVariableArcGlobalScope globalScope;

  protected VariableArcTraverser traverser;

  public VariableArcScopesGenitorP2Delegator() {
    this.globalScope = VariableArcMill.globalScope();
    this.traverser = VariableArcMill.traverser();
    this.init();
  }

  protected VariableArcTraverser getTraverser() {
    return this.traverser;
  }

  protected void init() {
    this.initArcBasis();
    this.initVariableArc();
  }

  protected void initArcBasis() {
    ArcBasisScopesGenitorP2 arcBasisScopesGenitorP2 = ArcBasisMill.scopesGenitorP2();
    this.getTraverser().add4ArcBasis(arcBasisScopesGenitorP2);
    this.getTraverser().setArcBasisHandler(arcBasisScopesGenitorP2);
  }

  protected void initVariableArc() {
    VariableArcScopesGenitorP2 variableArcScopesGenitorP2 = VariableArcMill.scopesGenitorP2();
    this.getTraverser().add4ArcBasis(variableArcScopesGenitorP2);
    this.getTraverser().add4VariableArc(variableArcScopesGenitorP2);
    this.getTraverser().setVariableArcHandler(variableArcScopesGenitorP2);
  }

  public void createFromAST(@NotNull ASTArcElement rootNode) {
    Preconditions.checkNotNull(rootNode);
    rootNode.accept(traverser);
  }
}