/* (c) https://github.com/MontiCore/monticore */
package montiarc._cocos.monticore;

import arcbasis._symboltable.SymbolService;
import com.google.common.base.Preconditions;
import de.monticore.statements.mcvardeclarationstatements._cocos.VarDeclarationInitializationHasCorrectType;
import de.monticore.symbols.oosymbols._symboltable.OOTypeSymbol;
import de.se_rwth.commons.logging.Finding;
import de.se_rwth.commons.logging.Log;
import montiarc.MontiArcMill;
import montiarc._ast.ASTMACompilationUnit;
import montiarc._cocos.AbstractCoCoTest;
import montiarc._cocos.MontiArcCoCoChecker;
import montiarc.check.MontiArcTypeCalculator;
import montiarc.util.Error;
import montiarc.util.MCError;
import org.codehaus.commons.nullanalysis.NotNull;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.junit.jupiter.params.provider.ValueSource;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * Tests the integration of {@link VarDeclarationInitializationHasCorrectType}, imported from MontiCores Statement
 * grammars.
 */
public class VarDeclarationInitializationHasCorrectTypeTest extends AbstractCoCoTest {
  protected static final String PACKAGE = "monticore/varDeclarationInitializationHasCorrectType";

  @Override
  protected String getPackage() {
    return PACKAGE;
  }

  @Override
  protected void registerCoCos(@NotNull MontiArcCoCoChecker checker) {
    Preconditions.checkNotNull(checker)
      .addCoCo(new VarDeclarationInitializationHasCorrectType(new MontiArcTypeCalculator()));
  }

  @Override
  @BeforeEach
  public void init() {
    super.init();
    initHelperTypes();
  }

  protected static void initHelperTypes() {
    OOTypeSymbol myType = MontiArcMill.oOTypeSymbolBuilder()
      .setName("MyType")
      .setSpannedScope(MontiArcMill.scope())
      .build();

    SymbolService.link(MontiArcMill.globalScope(), myType);
  }

  @ParameterizedTest
  @ValueSource(strings = {"ValidMultiVarDeclaration.arc"})
  void shouldSucceed(@NotNull String model) {
    Preconditions.checkNotNull(model);

    //Given
    ASTMACompilationUnit ast = this.parseAndCreateAndCompleteSymbols(model);

    //When
    this.getChecker().checkAll(ast);

    //Then
    Assertions.assertEquals(0, Log.getErrorCount());
  }

  @ParameterizedTest
  @MethodSource("modelAndExpectedErrorsProvider")
  void shouldFail(@NotNull String model, @NotNull Error... expectedErrors) {
    Preconditions.checkNotNull(model);
    Preconditions.checkNotNull(expectedErrors);

    Preconditions.checkNotNull(model);

    //Given
    ASTMACompilationUnit ast = this.parseAndCreateAndCompleteSymbols(model);
    ast.getEnclosingScope().add(MontiArcMill.oOTypeSymbolBuilder().setSpannedScope(MontiArcMill.scope()).setName("String").build());


    //When
    this.getChecker().checkAll(ast);

    //Then
    List<String> errorFindings = Log.getFindings().stream()
      .filter(Finding::isError)
      .map(f -> f.getMsg().split(" ")[0])
      .collect(Collectors.toList());
    List<String> expectedCodes = Arrays.stream(expectedErrors).map(Error::getErrorCode).collect(Collectors.toList());
    Assertions.assertEquals(expectedCodes, errorFindings);
  }

  protected static Stream<Arguments> modelAndExpectedErrorsProvider() {
    return Stream.of(
      arg("InvalidMultiVarDeclaration.arc",
        MCError.INCOMPATIBILITY, MCError.INCOMPATIBILITY),
      arg("InvalidMultiVarDeclarationWithTypeReference.arc",
          MCError.TYPE_REF_ASSIGNMENT_ERR, MCError.INCOMPATIBILITY)
    );
  }
}
