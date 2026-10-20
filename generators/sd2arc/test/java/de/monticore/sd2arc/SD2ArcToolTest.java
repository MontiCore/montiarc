/* (c) https://github.com/MontiCore/monticore */
package de.monticore.sd2arc;

import com.google.common.base.Preconditions;
import de.monticore.io.paths.MCPath;
import de.monticore.lang.sd4components.SD4ComponentsMill;
import de.monticore.lang.sd4components._ast.ASTSDVariableDeclaration;
import de.monticore.lang.sdbasis._ast.ASTSDArtifact;
import de.monticore.sd2arc.util.SD2ArcError;
import de.se_rwth.commons.logging.Log;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class SD2ArcToolTest extends SD2ArcTestBase {

  Path output = Paths.get("build/sd2arc");

  @Test
  public void testCorrectEnd2End() {
    String[] args = new String[]{
      "--coco",
      "-path", SYMBOL_PATH,
      "--input", SYMBOL_PATH + "/sd2arc/correct",
      "--output", output.toAbsolutePath().toString(),
      "--defaultTicks", String.valueOf(20),
    };

    SD2ArcTool.main(args);
  }

  @Test
  public void shouldResolveClassesOnSymbolPathWithClass2MC() throws Exception {
    Path jar = Path.of(Preconditions.class.getProtectionDomain().getCodeSource().getLocation().toURI());

    SD2ArcTool tool = new SD2ArcTool();
    tool.init();
    tool.setClass2MC(true);
    tool.initGlobalScope();
    SD4ComponentsMill.globalScope().setSymbolPath(new MCPath(jar));
    tool.initBuiltInSymbols();

    Assertions.assertTrue(SD4ComponentsMill.globalScope().resolveOOType(Preconditions.class.getName()).isPresent());
  }

  @Test
  public void shouldNotAddBuiltInStringWithClass2MC() {
    SD2ArcTool tool = new SD2ArcTool();
    tool.init();
    tool.setClass2MC(true);
    tool.initGlobalScope();
    tool.initBuiltInSymbols();

    Assertions.assertTrue(SD4ComponentsMill.globalScope().resolveType("java.lang.String").isPresent());
    Assertions.assertTrue(SD4ComponentsMill.globalScope().getLocalTypeSymbols().stream()
      .noneMatch(type -> type.getName().equals("String")));
  }

  @Test
  public void shouldImportJavaLangWithClass2MC() {
    SD2ArcTool tool = new SD2ArcTool();
    tool.init();
    tool.setClass2MC(true);
    tool.initGlobalScope();
    SD4ComponentsMill.globalScope().setSymbolPath(new MCPath(SYMBOL_PATH));
    tool.initBuiltInSymbols();
    Log.enableFailQuick(false);

    ASTSDArtifact ast = tool.parse(SYMBOL_PATH + "/sd2arc/class2mc/JavaLangSD.sd");
    Assertions.assertNotNull(ast, Log.getFindings().toString());
    tool.createSymbolTable(ast);
    tool.runDefaultCoCos(ast);
    tool.runAdditionalCoCos(ast);

    Assertions.assertTrue(Log.getFindings().isEmpty(), Log.getFindings().toString());
    Map<String, String> types = ast.getSequenceDiagram().getSDBody().getSDElementList().stream()
      .filter(ASTSDVariableDeclaration.class::isInstance)
      .map(ASTSDVariableDeclaration.class::cast)
      .collect(Collectors.toMap(ASTSDVariableDeclaration::getName,
        v -> v.getSymbol().getType().getTypeInfo().getFullName()));
    Assertions.assertEquals(Map.of("i", "java.lang.Integer", "s", "java.lang.String"), types);
    // The default import must not end up in the generated components
    Assertions.assertEquals(1, ast.getMCImportStatementList().size());
  }

  @ParameterizedTest
  @MethodSource("provideIncorrectAndErrorCodes")
  public void toolCocosOnlyExpectedErrorTest(String model, String[] errors) {
    SD2ArcTool tool = new SD2ArcTool();

    tool.init();
    tool.initGlobalScope();
    SD4ComponentsMill.globalScope().setSymbolPath(new MCPath(SYMBOL_PATH));
    tool.initBuiltInSymbols();
    Log.enableFailQuick(false);

    ASTSDArtifact ast = tool.parse(SYMBOL_PATH + "/sd2arc/incorrect/" + model);
    Assertions.assertNotNull(ast, Log.getFindings().toString());
    tool.createSymbolTable(ast);

    tool.runDefaultCoCos(ast);
    tool.runAdditionalCoCos(ast);

    checkOnlyExpectedErrorsPresent(errors);
  }

  protected static Stream<Arguments> provideIncorrectAndErrorCodes() {
    return Stream.of(
      Arguments.of("ImpliedConnectorsFitEmbeddingComponent.sd", new String[]{SD2ArcError.IMPLIED_CONNECTORS_FIT.getErrorCode()}),
      Arguments.of("ObserveOnUnconnectedPort.sd", new String[]{SD2ArcError.OBSERVE_ON_UNCONNECTED_PORT.getErrorCode()}),
      Arguments.of("ObserveOnUnconnectedPortEmbedded.sd", new String[]{SD2ArcError.OBSERVE_ON_UNCONNECTED_PORT.getErrorCode()}),
      Arguments.of("SubcomponentExistsInEmbeddingComponent.sd", new String[]{SD2ArcError.SUBCOMPONENT_NOT_EXISTS.getErrorCode()})
    );
  }
}
