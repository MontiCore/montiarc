/* (c) https://github.com/MontiCore/monticore */
package montiarc._cocos;

import com.google.common.base.Preconditions;
import de.monticore.symbols.oosymbols._symboltable.OOTypeSymbol;
import de.monticore.types.check.SymTypeExpressionFactory;
import de.se_rwth.commons.logging.Log;
import montiarc.MontiArcMill;
import montiarc.MontiArcTestBase;
import montiarc._ast.ASTMACompilationUnit;
import montiarc._symboltable.IMontiArcScope;
import montiarc.util.Error;
import montiarc.util.MontiArcError;
import org.codehaus.commons.nullanalysis.NotNull;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.junit.jupiter.params.provider.ValueSource;

import java.io.IOException;
import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;

class ImportedSymbolExistsTest extends MontiArcTestBase {

  @BeforeEach
  public void setUpSymbols() {
    IMontiArcScope packageScope = MontiArcMill.scope();
    packageScope.setName("a");
    MontiArcMill.globalScope().addSubScope(packageScope);
    // Component
    packageScope.add(MontiArcMill.componentTypeSymbolBuilder().setName("Comp").setSpannedScope(MontiArcMill.scope()).build());

    // OO Type
    packageScope.add(MontiArcMill.oOTypeSymbolBuilder().setName("OOType").setSpannedScope(MontiArcMill.scope()).build());

    // Function
    packageScope.add(MontiArcMill.functionSymbolBuilder().setName("func").setSpannedScope(MontiArcMill.scope()).build());

    // Static method
    packageScope.add(MontiArcMill.methodSymbolBuilder().setName("method").setIsStatic(true).setSpannedScope(MontiArcMill.scope()).build());

    // Variable
    packageScope.add(MontiArcMill.variableSymbolBuilder().setName("variable").build());

    // Static field
    packageScope.add(MontiArcMill.fieldSymbolBuilder().setName("staticField").setIsStatic(true).build());

    // Enum
    OOTypeSymbol onOffEnumType = MontiArcMill.oOTypeSymbolBuilder().setIsEnum(true).setName("OnOff").setIsPublic(true).setSpannedScope(MontiArcMill.scope()).build();
    packageScope.addSubScope(onOffEnumType.getSpannedScope());
    onOffEnumType.getSpannedScope().add(MontiArcMill.fieldSymbolBuilder().setName("ON").setIsStatic(true).setIsFinal(true).setIsPublic(true).setIsReadOnly(true).setIsEnumConstant(true).setType(SymTypeExpressionFactory.createTypeObject(onOffEnumType)).build());
    onOffEnumType.getSpannedScope().add(MontiArcMill.fieldSymbolBuilder().setName("OFF").setIsStatic(true).setIsFinal(true).setIsPublic(true).setIsReadOnly(true).setIsEnumConstant(true).setType(SymTypeExpressionFactory.createTypeObject(onOffEnumType)).build());
    packageScope.add(onOffEnumType);
  }

  @ParameterizedTest
  @ValueSource(strings = {
    // no import statement
    "component Comp1 {}",
    "import a.Comp;" +
      "component Comp2 {}",
    "import a.OOType;" +
      "component Comp3 {}",
    "import a.func;" +
      "component Comp4 {}",
    "import a.method;" +
      "component Comp5 {}",
    "import a.variable;" +
      "component Comp6 {}",
    "import a.staticField;" +
      "component Comp7 {}",
    "import a.OnOff;" +
      "component Comp8 {}",
    "import a.OnOff.ON;" +
      "component Comp9 {}",
    "package b;" +
      "component Comp10 {}",
    // Ignore Star imports
    "import nonExisting.*;" +
      "component Comp11 {}",
    // Partial import in same package?
    "package a;" +
      "import OnOff;" +
      "component Comp12 {}"
  })
  public void shouldNotReportError(@NotNull String model) throws IOException {
    Preconditions.checkNotNull(model);

    // Given
    ASTMACompilationUnit ast = compile(model);

    MontiArcCoCoChecker checker = new MontiArcCoCoChecker();
    checker.addCoCo(new ImportedSymbolExists());

    // When
    checker.checkAll(ast);

    // Then
    assertThat(Log.getFindingsCount()).as(Log.getFindings().toString()).isEqualTo(0);
  }

  @ParameterizedTest
  @MethodSource("invalidModels")
  public void shouldReportError(@NotNull String model, @NotNull Error... errors) throws IOException {
    Preconditions.checkNotNull(model);
    Preconditions.checkNotNull(errors);

    // Given
    ASTMACompilationUnit ast = compile(model);

    MontiArcCoCoChecker checker = new MontiArcCoCoChecker();
    checker.addCoCo(new ImportedSymbolExists());

    // When
    checker.checkAll(ast);

    // Then
    assertThat(Log.getFindings()).as(Log.getFindings().toString()).isNotEmpty();
    assertThat(getLoggedErrorCodes())
      .containsExactlyInAnyOrder(getErrorCodes(errors));
  }

  protected static Stream<Arguments> invalidModels() {
    return Stream.of(
      arg("import unknown;" +
          "component Comp1 {}",
        MontiArcError.IMPORTED_SYMBOL_MISSING),
      arg("import unknown.Comp;" +
          "component Comp2 {}",
        MontiArcError.IMPORTED_SYMBOL_MISSING),
      arg("import unknown.Comp;" +
          "import a.OnOff;" +
          "component Comp3 {}",
        MontiArcError.IMPORTED_SYMBOL_MISSING),
      arg("import unknown.Comp;" +
          "import unknown.Type;" +
          "component Comp4 {}",
        MontiArcError.IMPORTED_SYMBOL_MISSING,
        MontiArcError.IMPORTED_SYMBOL_MISSING),
      arg("import a.Unknown;" +
          "component Comp5 {}",
        MontiArcError.IMPORTED_SYMBOL_MISSING),
      arg("import a;" +
          "component Comp6 {}",
        MontiArcError.IMPORTED_SYMBOL_MISSING)
    );
  }
}
