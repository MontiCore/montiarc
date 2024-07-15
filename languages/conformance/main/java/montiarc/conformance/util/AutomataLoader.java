/* (c) https://github.com/MontiCore/monticore */
package montiarc.conformance.util;

import arcbasis._ast.ASTComponentType;
import com.google.common.base.Preconditions;
import montiarc.conformance.util._cocos.ActionsMustBeAssignmentsCoCo;
import montiarc.conformance.util._cocos.AtLeastOneInAndOutPort;
import montiarc.conformance.util._cocos.UniqueVarAssignmentInTransActions;
import montiarc.conformance.util.trafo.GlobalVariableTrafo;
import de.monticore.cd._symboltable.BuiltInTypes;
import de.monticore.cd4code.CD4CodeMill;
import de.monticore.cd4code._cocos.CD4CodeCoCoChecker;
import de.monticore.cd4code._parser.CD4CodeParser;
import de.monticore.cd4code._symboltable.CD4CodeSymbolTableCompleter;
import de.monticore.cd4code._symboltable.CD4CodeSymbols2Json;
import de.monticore.cd4code._symboltable.ICD4CodeArtifactScope;
import de.monticore.cd4code._symboltable.ICD4CodeScope;
import de.monticore.cd4code.cocos.CD4CodeCoCosDelegator;
import de.monticore.cdbasis._ast.ASTCDCompilationUnit;
import de.monticore.symboltable.ImportStatement;
import de.monticore.types.mcbasictypes.MCBasicTypesMill;
import java.io.File;
import java.io.IOException;
import java.util.*;

import montiarc.MontiArcMill;
import montiarc.MontiArcTool;
import montiarc._ast.ASTMACompilationUnit;
import montiarc._cocos.MontiArcCoCoChecker;
import montiarc._cocos.MontiArcCoCos;
import montiarc._parser.MontiArcParser;
import montiarc._symboltable.MontiArcSymbols2Json;
import org.apache.commons.lang3.tuple.ImmutablePair;
import org.apache.commons.lang3.tuple.Pair;
import scmapping.SCMappingTool;
import scmapping._ast.ASTSCMapping;
import scmapping._cocos.SCMappingCoCoChecker;
import scmapping._cocos.ValidNamesInRulesCoCo;
import scmapping._cocos.ValueRightInEqualExpressionsCoCo;
import scmapping._cocos.ValueRightInNotEqualExpressionsCoCo;

public class AutomataLoader {

  public static void initMills() {

    CD4CodeMill.reset();
    CD4CodeMill.init();
    CD4CodeMill.globalScope().clear();

    MontiArcMill.reset();
    MontiArcMill.init();
    MontiArcMill.globalScope().clear();
  }

  /***Load class Diagram*/
  private static ASTCDCompilationUnit parseCD(String cdFilePath) {
    CD4CodeParser cdParser = new CD4CodeParser();
    final Optional<ASTCDCompilationUnit> optCdAST;
    try {
      optCdAST = cdParser.parse(cdFilePath);
    } catch (IOException e) {
      throw new RuntimeException(e);
    }

    assert (optCdAST.isPresent());
    return optCdAST.get();
  }

  private static void checkCDCoCos(ASTCDCompilationUnit cdAST) {
    CD4CodeCoCoChecker cdChecker = new CD4CodeCoCosDelegator().getCheckerForAllCoCos();
    cdChecker.checkAll(cdAST);
  }

  private static void createCDSymTab(ASTCDCompilationUnit ast) {
    BuiltInTypes.addBuiltInTypes(CD4CodeMill.globalScope());
    ICD4CodeArtifactScope as = CD4CodeMill.scopesGenitorDelegator().createFromAST(ast);
    as.addImports(new ImportStatement("java.lang", true));
    as.addImports(new ImportStatement("java.util", true));
    CD4CodeSymbolTableCompleter c =
        new CD4CodeSymbolTableCompleter(
            ast.getMCImportStatementList(), MCBasicTypesMill.mCQualifiedNameBuilder().build());
    ast.accept(c.getTraverser());
    ast.setEnclosingScope(as);
  }

  public static ASTCDCompilationUnit loadCD(File cdFile) {
    assert cdFile.getName().endsWith(".cd");
    ASTCDCompilationUnit cdAST = parseCD(cdFile.getAbsolutePath());
    createCDSymTab(cdAST);
    checkCDCoCos(cdAST);
    return cdAST;
  }

  /***Load MontiArc Component*/

  private static ASTMACompilationUnit parseMA(File maFile) {
    MontiArcParser parser = MontiArcMill.parser();
    Optional<ASTMACompilationUnit> optAst;
    try {
      optAst = parser.parse(maFile.toString());
    } catch (IOException e) {
      throw new RuntimeException(e);
    }
    assert optAst.isPresent();
    return optAst.get();
  }

  private static void checkMACoCos(ASTMACompilationUnit ast) {
    MontiArcCoCoChecker checker = MontiArcCoCos.afterSymTab();
    checker.addCoCo(new ActionsMustBeAssignmentsCoCo());
    checker.addCoCo(new AtLeastOneInAndOutPort());
    checker.addCoCo(new UniqueVarAssignmentInTransActions(ast));
    checker.checkAll(ast);
  }

  private static void createMASymbolTab(ASTMACompilationUnit ast) {
    MontiArcTool tool = new MontiArcTool();
    Preconditions.checkNotNull(ast);
    tool.initializeClass2MC();
    tool.createSymbolTable(ast);
    tool.runSymbolTablePhase2(ast);
    tool.runSymbolTablePhase3(ast);
    tool.runAfterSymbolTablePhase3Trafos(ast);
  }

  public static Pair<ASTCDCompilationUnit, ASTMACompilationUnit> loadModels(
      File maFile, File cdFile) {
    initMills();
    Set<List<?>> m = new HashSet<>();
    boolean t = m.containsAll((Collection<?>) List.of(1));

    ASTCDCompilationUnit cdAST = loadCD(cdFile);

    String serialized =
        new CD4CodeSymbols2Json().serialize((ICD4CodeScope) cdAST.getEnclosingScope());
    ASTMACompilationUnit maAST = parseMA(maFile);

    MontiArcMill.globalScope().addSubScope(new MontiArcSymbols2Json().deserialize(serialized));
    GlobalVariableTrafo.transform(maAST);
    createMASymbolTab(maAST);
    checkMACoCos(maAST);

    return new ImmutablePair<>(cdAST, maAST);
  }

  public static ASTSCMapping loadMapping(
      String path,
      ASTComponentType refAut,
      ASTCDCompilationUnit refCD,
      ASTComponentType conAut,
      ASTCDCompilationUnit conCD) {
    ASTSCMapping ast = new SCMappingTool().parse(path);


    SCMappingCoCoChecker checker = new SCMappingCoCoChecker();
    checker.addCoCo(new ValidNamesInRulesCoCo(refAut, refCD, conAut, conCD));
    checker.addCoCo(new ValueRightInEqualExpressionsCoCo(refAut, conAut));
    checker.addCoCo(new ValueRightInNotEqualExpressionsCoCo(refAut, conAut));
    checker.checkAll(ast);
    return ast;
  }
}
