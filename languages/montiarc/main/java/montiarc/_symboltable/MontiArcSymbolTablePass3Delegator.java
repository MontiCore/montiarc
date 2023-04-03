/* (c) https://github.com/MontiCore/monticore */
package montiarc._symboltable;

import arcbasis.ArcBasisMill;
import arcbasis._symboltable.ArcBasisSymbolTablePass3;
import arcbasis._symboltable.IArcBasisGlobalScope;
import arcbasis._visitor.ArcBasisTraverser;
import com.google.common.base.Preconditions;
import montiarc._ast.ASTMACompilationUnit;
import org.codehaus.commons.nullanalysis.NotNull;

import java.util.Collection;

public class MontiArcSymbolTablePass3Delegator {

  protected IArcBasisGlobalScope globalScope;

  protected ArcBasisTraverser traverser;

  protected ArcBasisTraverser getTraverser() {
    return this.traverser;
  }

  public MontiArcSymbolTablePass3Delegator() {
    this.globalScope = ArcBasisMill.globalScope();
    this.traverser = ArcBasisMill.traverser();
    this.init();
  }

  protected void init() {
    this.initArcBasis();
  }

  protected void initArcBasis() {
    ArcBasisSymbolTablePass3 arcBasisSymbolTableCompleter = ArcBasisMill.symbolTablePass3();
    this.getTraverser().add4ArcBasis(arcBasisSymbolTableCompleter);
  }

  public void createFromAST(@NotNull ASTMACompilationUnit rootNode) {
    Preconditions.checkNotNull(rootNode);
    rootNode.accept(traverser);
  }

  public void createFromAST(@NotNull Collection<ASTMACompilationUnit> rootNodes) {
    Preconditions.checkNotNull(rootNodes);
    for (ASTMACompilationUnit rootNode : rootNodes) {
      this.createFromAST(rootNode);
    }
  }
}
