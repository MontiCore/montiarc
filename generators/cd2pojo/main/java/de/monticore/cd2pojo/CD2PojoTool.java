/* (c) https://github.com/MontiCore/monticore */
package de.monticore.cd2pojo;

import de.monticore.CDGeneratorTool;
import de.monticore.cd2pojo.trafo.CDTypePublicVisibilityTrafo;
import de.monticore.cd4analysis._symboltable.ICD4AnalysisScope;
import de.monticore.cd4code.CD4CodeMill;
import de.monticore.cd4code._symboltable.ICD4CodeArtifactScope;
import de.monticore.cd4code._visitor.CD4CodeTraverser;
import de.monticore.cdbasis._ast.ASTCDCompilationUnit;
import de.monticore.cdbasis._symboltable.CDTypeSymbol;
import de.monticore.symboltable.ISymbol;
import de.se_rwth.commons.Names;

public class CD2PojoTool extends CDGeneratorTool {

  public static void main(String[] args) {
    CD2PojoTool tool = new CD2PojoTool();
    tool.run(args);
  }

  @Override
  public CD4CodeTraverser createBeforeCodegenTrafo() {
    CD4CodeTraverser t = super.createBeforeCodegenTrafo();
    // The cd4analysis language currently does not respect visibilities. We
    // make all types public to ensure the generated java code compiles.
    t.add4CDBasis(new CDTypePublicVisibilityTrafo());
    return t;
  }

  @Override
  public void storeSymTab(ICD4CodeArtifactScope scope, String path) {
    for (ICD4AnalysisScope subscope : scope.getSubScopes()) {
      // store each type symbol in its own symbol-table
      ISymbol symbol = subscope.getSpanningSymbol();
      if (symbol instanceof CDTypeSymbol) {
        ICD4CodeArtifactScope as2store = CD4CodeMill.artifactScope();
        as2store.setPackageName(Names.getQualifiedName(scope.getPackageName(), scope.getName()));
        as2store.setName(subscope.getName());
        as2store.add((CDTypeSymbol) subscope.getSpanningSymbol());
        super.storeSymTab(as2store, path);
      }
    }
  }
}