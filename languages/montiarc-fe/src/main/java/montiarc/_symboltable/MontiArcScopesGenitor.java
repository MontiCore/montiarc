/* (c) https://github.com/MontiCore/monticore */
package montiarc._symboltable;

import com.google.common.base.Preconditions;
import de.monticore.symboltable.ImportStatement;
import de.monticore.types.mcbasictypes._ast.ASTMCImportStatement;
import montiarc.MontiArcMill;
import montiarc._ast.ASTMACompilationUnit;
import org.codehaus.commons.nullanalysis.NotNull;

import java.util.ArrayList;
import java.util.List;

public class MontiArcScopesGenitor extends MontiArcScopesGenitorTOP {

  @Override
  public IMontiArcArtifactScope createFromAST(@NotNull ASTMACompilationUnit rootNode) {
    Preconditions.checkNotNull(rootNode);
    List<ImportStatement> imports = new ArrayList<>();
    for (ASTMCImportStatement importStatement : rootNode.getImportStatementList()) {
      imports.add(new ImportStatement(importStatement.getQName(), importStatement.isStar()));
    }
    IMontiArcArtifactScope artifactScope = MontiArcMill.artifactScope();
    artifactScope.setPackageName(rootNode.isPresentPackage() ? rootNode.getPackage().getQName() : "");
    artifactScope.setImportsList(imports);
    if (this.getCurrentScope().isPresent()) rootNode.setEnclosingScope(this.getCurrentScope().get());
    putOnStack(artifactScope);
    rootNode.setSpannedScope(artifactScope);
    artifactScope.setAstNode(rootNode);
    rootNode.accept(getTraverser());
    return artifactScope;
  }

  @Override
  public void visit(ASTMACompilationUnit node) {
  }

  @Override
  public void endVisit(@NotNull ASTMACompilationUnit node) {
    Preconditions.checkNotNull(node);
    Preconditions.checkState(this.getCurrentScope().isPresent());
    super.endVisit(node);
  }
}