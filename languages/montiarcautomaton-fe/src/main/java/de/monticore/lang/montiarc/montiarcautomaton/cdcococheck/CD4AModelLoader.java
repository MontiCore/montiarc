package de.monticore.lang.montiarc.montiarcautomaton.cdcococheck;

import de.monticore.symboltable.MutableScope;
import de.monticore.symboltable.ResolvingConfiguration;
import de.monticore.umlcd4a.CD4ACoCos;
import de.monticore.umlcd4a.CD4AnalysisLanguage;
import de.monticore.umlcd4a.CD4AnalysisModelLoader;
import de.monticore.umlcd4a.cd4analysis._ast.ASTCDCompilationUnit;


// TODO Required for performing CoCo checks of CD4A models whenever they are
// loaded. Remove this class if there is a more elegant way in the future.
public class CD4AModelLoader extends CD4AnalysisModelLoader {

  public CD4AModelLoader(CD4AnalysisLanguage language) {
    super(language);
  }
  
  @Override
  protected void createSymbolTableFromAST(ASTCDCompilationUnit arg0, String arg1, MutableScope arg2, ResolvingConfiguration arg3) {
    super.createSymbolTableFromAST(arg0, arg1, arg2, arg3);
    new CD4ACoCos().getCheckerForAllCoCos().checkAll(arg0); // perform coco check after SymTab creation
  }
  
}
