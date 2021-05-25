/* (c) https://github.com/MontiCore/monticore */
package montiarc._symboltable;

import com.google.common.base.Preconditions;
import de.monticore.scbasis._ast.ASTNamedStatechart;
import de.monticore.scbasis._symboltable.ISCBasisScope;
import de.monticore.symboltable.ImportStatement;
import de.monticore.types.mcbasictypes._ast.ASTMCImportStatement;
import montiarc.MontiArcMill;
import montiarc._ast.ASTArcStatechart;
import montiarc._ast.ASTMACompilationUnit;
import org.codehaus.commons.nullanalysis.NotNull;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class MontiArcScopesGenitor extends MontiArcScopesGenitorTOP {

  @Override
  public IMontiArcArtifactScope createFromAST(@NotNull ASTMACompilationUnit rootNode) {
    Preconditions.checkArgument(rootNode != null);
    List<ImportStatement> imports = new ArrayList<>();
    for (ASTMCImportStatement importStatement : rootNode.getImportStatementList()) {
      imports.add(new ImportStatement(importStatement.getQName(), importStatement.isStar()));
    }
    IMontiArcArtifactScope artifactScope = MontiArcMill.artifactScope();
    artifactScope.setPackageName(rootNode.isPresentPackage() ? rootNode.getPackage().getQName() : "");
    artifactScope.setImportsList(imports);
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
    Preconditions.checkArgument(node != null);
    Preconditions.checkState(this.getCurrentScope().isPresent());
    super.endVisit(node);
  }

  @Override
  public void endVisit(ASTArcStatechart node) {
    IMontiArcScope arcScope = node.getEnclosingScope();
    Optional<ISCBasisScope> stateChartScope = ((MontiArcScope)arcScope).getStatechartScope();
    if(stateChartScope.isPresent()){
      ISCBasisScope chartScope = stateChartScope.get();
      // add port names to the statechart scope
      // TODO: is this necessary?
      // it would be nicer if the statechart scope could use the monti arc scope to resolve
      arcScope
          .getLocalPortSymbols()
          .stream()
          .map(p -> MontiArcMill
                        .variableSymbolBuilder()
                        .setFullName(p.getFullName())
                        .setType(p.getType())
                        .setEnclosingScope(chartScope)
                        .build())
          .forEach(chartScope::add);
    } else {
      // TODO: deal with named charts
    }
  }


}