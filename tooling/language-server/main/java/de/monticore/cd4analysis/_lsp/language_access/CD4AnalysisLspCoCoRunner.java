/* (c) https://github.com/MontiCore/monticore */
package de.monticore.cd4analysis._lsp.language_access;

import de.mclsg.lsp.document_management.DocumentManager;
import de.monticore.cd4analysis._cocos.CD4AnalysisCoCoChecker;
import de.monticore.cd4analysis.cocos.CD4AnalysisCoCosDelegator;
import de.monticore.cdbasis._ast.ASTCDCompilationUnit;

public class CD4AnalysisLspCoCoRunner extends CD4AnalysisLspCoCoRunnerTOP{

    private final CD4AnalysisCoCoChecker checker;

    public CD4AnalysisLspCoCoRunner(DocumentManager documentManager) {
        this(documentManager, new CD4AnalysisCoCoChecker());
    }

    public CD4AnalysisLspCoCoRunner(DocumentManager documentManager, CD4AnalysisCoCoChecker checker) {
        super(documentManager);
        this.checker = checker;
    }

    @Override
    public boolean needsSymbols() {
        return true;
    }

    @Override
    public void runAllCoCos(ASTCDCompilationUnit ast) {
        checker.checkAll(ast);
    }
}
