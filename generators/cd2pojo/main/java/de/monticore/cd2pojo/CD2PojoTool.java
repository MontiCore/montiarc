/* (c) https://github.com/MontiCore/monticore */
package de.monticore.cd2pojo;

import de.monticore.CDGeneratorTool;
import de.monticore.cd4analysis._symboltable.ICD4AnalysisScope;
import de.monticore.cd4code.CD4CodeMill;
import de.monticore.cd4code._symboltable.ICD4CodeArtifactScope;
import de.monticore.cdbasis._symboltable.CDTypeSymbol;
import de.se_rwth.commons.Names;

public class CD2PojoTool extends CDGeneratorTool {

  public static void main(String[] args) {
    CD2PojoTool tool = new CD2PojoTool();
    tool.run(args);
  }

  @Override
  public void storeSymTab(ICD4CodeArtifactScope scope, String path) {
    for (ICD4AnalysisScope subscope : scope.getSubScopes()) {
      // store each type symbol in its own symbol-table
      ICD4CodeArtifactScope as2store = CD4CodeMill.artifactScope();
      as2store.setPackageName(Names.getQualifiedName(scope.getPackageName(), scope.getName()));
      as2store.setName(subscope.getName());
      as2store.add((CDTypeSymbol) subscope.getSpanningSymbol());
      super.storeSymTab(as2store, path);
    }
  }
}