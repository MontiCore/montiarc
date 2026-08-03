/* (c) https://github.com/MontiCore/monticore */
package montiarc._symboltable;

import arcags.ArcAGsMill;
import arcags._symboltable.ArcAGScopesGenitorP2;
import arcbasis.ArcBasisMill;
import arcbasis._symboltable.ArcBasisScopesGenitorP2;
import com.google.common.base.Preconditions;
import de.monticore.statements.mccommonstatements._symboltable.MCCommonStatementsSymTabCompletion;
import de.monticore.statements.mcvardeclarationstatements._symboltable.MCVarDeclarationStatementsSymTabCompletion;
import montiarc.MontiArcMill;
import montiarc._ast.ASTMACompilationUnit;
import montiarc._visitor.MontiArcTraverser;
import org.codehaus.commons.nullanalysis.NotNull;

import java.util.Collection;

public class MontiArcScopesGenitorP2Delegator {

  protected IMontiArcGlobalScope globalScope;

  protected MontiArcTraverser traverser;

  protected MontiArcTraverser getTraverser() {
    return this.traverser;
  }

  public MontiArcScopesGenitorP2Delegator() {
    this.globalScope = MontiArcMill.globalScope();
    this.traverser = MontiArcMill.traverser();
    this.init();
  }

  protected void init() {
    this.initArcBasis();
    this.initMCCommonStatements();
    this.initMCVarDeclarationStatements();
    this.initArcAG();
  }

  protected void initArcBasis() {
    ArcBasisScopesGenitorP2 scopesGenP2 = ArcBasisMill.scopesGenitorP2();
    this.getTraverser().add4ArcBasis(scopesGenP2);
    this.getTraverser().add4CompSymbols(scopesGenP2);
    this.getTraverser().add4TypeParameters(scopesGenP2);
    this.getTraverser().setArcBasisHandler(scopesGenP2);
  }

  protected void initArcAG(){
    ArcAGScopesGenitorP2 scopesGenP2 = ArcAGsMill.scopesGenitorP2();
    this.getTraverser().add4ArcBasis(scopesGenP2);
    this.getTraverser().add4ArcAGs(scopesGenP2);
  }

  protected void initMCCommonStatements() {
    this.getTraverser().add4MCCommonStatements(new MCCommonStatementsSymTabCompletion());
  }

  protected void initMCVarDeclarationStatements() {
    this.getTraverser().add4MCVarDeclarationStatements(new MCVarDeclarationStatementsSymTabCompletion());
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
