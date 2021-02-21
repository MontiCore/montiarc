package de.monticore.cd2pojo;

import de.monticore.cd4code.CD4CodeMill;
import de.monticore.cd4code._parser.CD4CodeParser;
import de.monticore.cd4code._symboltable.CD4CodeGlobalScope;
import de.monticore.cd4code._symboltable.ICD4CodeGlobalScope;
import de.monticore.cd4code.trafo.CD4CodeAfterParseTrafo;
import de.monticore.cdbasis._ast.ASTCDCompilationUnit;
import de.monticore.io.paths.ModelPath;
import de.se_rwth.commons.logging.Log;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.junit.jupiter.params.provider.ValueSource;

import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Collection;
import java.util.Optional;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.*;

public class SymTabBuildingTest {
  
  protected static Path modelsPath = Paths.get("src/test/resources");
  
  protected static Stream<Arguments> allTestModels() {
    return Stream.of(
      Arguments.of("cocos/AmbiguousAssociations.cd"),
      Arguments.of("cocos/AmbiguousExplicitRolesLeft.cd"),
      Arguments.of("cocos/AmbiguousExplicitRolesRight.cd"),
      Arguments.of("cocos/AmbiguousImplicitRolesLeft.cd"),
      Arguments.of("cocos/AmbiguousImplicitRolesRight.cd"),
      Arguments.of("models/domain/RoadNetwork.cd"),
      Arguments.of("models/simple/Double.cd"));
  }
  
  @ParameterizedTest
  @MethodSource("allTestModels")
  public void parseAndTransformModelAndBuildSymTab(String pathToModel) throws IOException {
    CD4CodeParser p = CD4CodeMill.parser();
    Optional<ASTCDCompilationUnit> optCDCU = p.parse(modelsPath.resolve(pathToModel).toString());
    assertTrue(optCDCU.isPresent());
    ASTCDCompilationUnit compUnit = optCDCU.get();
    new CD4CodeAfterParseTrafo().transform(compUnit);
    CD4CodeMill.scopesGenitorDelegator().createFromAST(compUnit);
    assertEquals(0, Log.getErrorCount());
  }
  
  @ParameterizedTest
  @MethodSource("allTestModels")
  public void toolShouldParseAndTransformModel(String pathToModel) {
    Optional<ASTCDCompilationUnit> optCDCU = POJOGeneratorTool
      .parseAndTransformCD(
        modelsPath.resolve(pathToModel).toString());
    assertTrue(optCDCU.isPresent());
  }
  
  @ParameterizedTest
  @ValueSource(strings = {"models", "cocos", ""})
  public void shouldBuildSymTabForModelsParsedAndTransformedByToolFromGlobalScope(String pathToModels) {
    ICD4CodeGlobalScope globalScope = CD4CodeMill.globalScope();
    globalScope.clear();
    globalScope.init();
    globalScope.setModelPath(new ModelPath(modelsPath.resolve(pathToModels)));
    if(globalScope instanceof CD4CodeGlobalScope) {
      ((CD4CodeGlobalScope) globalScope).addBuiltInTypes();
    }
    Collection<ASTCDCompilationUnit> compUnits = POJOGeneratorTool.parseAndTransformModels(globalScope);
    assertFalse(compUnits.isEmpty());
    for(ASTCDCompilationUnit compUnit : compUnits) {
      CD4CodeMill.scopesGenitorDelegator().createFromAST(compUnit);
    }
  }
  
  @ParameterizedTest
  @ValueSource(strings = {"models", "cocos", ""})
  public void toolShouldBuildSymTabForParsedAndTransformedModel(String pathToModels) {
    ICD4CodeGlobalScope globalScope = CD4CodeMill.globalScope();
    globalScope.clear();
    globalScope.init();
    globalScope.setModelPath(new ModelPath(modelsPath.resolve(pathToModels)));
    if(globalScope instanceof CD4CodeGlobalScope) {
      ((CD4CodeGlobalScope) globalScope).addBuiltInTypes();
    }
    POJOGeneratorTool.createSymbolTable(globalScope);
  }
}