/* (c) https://github.com/MontiCore/monticore */
package montiarc._symboltable;

import arcbasis.ArcBasisMill;
import arcbasis._symboltable.ArcBasisScopesGenitor;
import com.google.common.base.Preconditions;
import montiarc._ast.ASTMACompilationUnit;
import org.codehaus.commons.nullanalysis.NotNull;
import variablearc.VariableArcMill;
import variablearc._symboltable.VariableArcScopesGenitor;

import java.util.ArrayList;
import java.util.Collection;

public class MontiArcScopesGenitorDelegator extends MontiArcScopesGenitorDelegatorTOP {

  public MontiArcScopesGenitorDelegator() {
    super();
    traverser.getArcBasisVisitorList().clear();
    traverser.getVariableArcVisitorList().clear();
    this.initArcBasisGenitor();
    this.initVariableArcGenitor();
  }

  public Collection<IMontiArcArtifactScope> createFromAST(@NotNull Collection<ASTMACompilationUnit> rootNodes) {
    Preconditions.checkNotNull(rootNodes);
    Collection<IMontiArcArtifactScope> scopes = new ArrayList<>(rootNodes.size());
    for (ASTMACompilationUnit rootNode : rootNodes) {
      scopes.add(this.createFromAST(rootNode));
    }
    return scopes;
  }

  protected void initArcBasisGenitor() {
    ArcBasisScopesGenitor genitor = ArcBasisMill.scopesGenitor();
    genitor.setScopeStack(scopeStack);

    traverser.setArcBasisHandler(genitor);
    traverser.add4ArcBasis(genitor);
  }

  protected void initVariableArcGenitor() {
    VariableArcScopesGenitor genitor = VariableArcMill.scopesGenitor();
    genitor.setScopeStack(scopeStack);

    traverser.setVariableArcHandler(genitor);
    traverser.add4VariableArc(genitor);
    traverser.add4ArcBasis(genitor);
  }
}