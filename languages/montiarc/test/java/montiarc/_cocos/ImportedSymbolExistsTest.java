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
import org.codehaus.commons.nullanalysis.NotNull;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.junit.jupiter.params.provider.ValueSource;

import java.util.stream.Stream;

import static montiarc.util.MontiArcError.IMPORTED_SYMBOL_MISSING;
import static org.assertj.core.api.Assertions.assertThat;

/**
 * The class under test is {@link ImportedSymbolExists}.
 */
class ImportedSymbolExistsTest extends MontiArcTestBase {

  @BeforeEach
  protected void setUpSymbols() {
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
    "component ValidComp1 {}",
    // import of an existing component
    "import a.Comp;" +
      "component ValidComp2 {}",
    // import of an existing OO type
    "import a.OOType;" +
      "component ValidComp3 {}",
    // import of an existing function
    "import a.func;" +
      "component ValidComp4 {}",
    // import of an existing static method
    "import a.method;" +
      "component ValidComp5 {}",
    // import of an existing variable
    "import a.variable;" +
      "component ValidComp6 {}",
    // import of an existing static field
    "import a.staticField;" +
      "component ValidComp7 {}",
    // import of an existing enum type
    "import a.OnOff;" +
      "component ValidComp8 {}",
    // import of an existing enum constant
    "import a.OnOff.ON;" +
      "component ValidComp9 {}",
    // different package, no import referencing package a's symbols
    "package b;" +
      "component ValidComp10 {}",
    // star import of a non-existing package is ignored
    "import nonExisting.*;" +
      "component ValidComp11 {}",
    // import of a sibling symbol by partial name from within the same package
    "package a;" +
      "import OnOff;" +
      "component ValidComp12 {}"
  })
  void shouldNotReportError(@NotNull String model) {
    Preconditions.checkNotNull(model);

    // Given
    ASTMACompilationUnit ast = compile(model);

    MontiArcCoCoChecker checker = new MontiArcCoCoChecker();
    checker.addCoCo(new ImportedSymbolExists());

    // When
    checker.checkAll(ast);

    // Then
    assertThat(Log.getFindings()).isEmpty();
  }

  @ParameterizedTest
  @MethodSource("invalidModels")
  void shouldReportError(@NotNull String model,
                         @NotNull Error... errors) {
    Preconditions.checkNotNull(model);
    Preconditions.checkNotNull(errors);

    // Given
    ASTMACompilationUnit ast = compile(model);

    MontiArcCoCoChecker checker = new MontiArcCoCoChecker();
    checker.addCoCo(new ImportedSymbolExists());

    // When
    checker.checkAll(ast);

    // Then
    assertThat(getLoggedErrorCodes())
      .containsExactlyInAnyOrder(getErrorCodes(errors));
  }

  protected static Stream<Arguments> invalidModels() {
    return Stream.of(
      // import of a completely unknown package
      arg("import unknown;" +
          "component InvalidComp1 {}",
        IMPORTED_SYMBOL_MISSING),
      // import of a symbol from an unknown package
      arg("import unknown.Comp;" +
          "component InvalidComp2 {}",
        IMPORTED_SYMBOL_MISSING),
      // one unknown import alongside one valid import
      arg("import unknown.Comp;" +
          "import a.OnOff;" +
          "component InvalidComp3 {}",
        IMPORTED_SYMBOL_MISSING),
      // two unknown imports
      arg("import unknown.Comp;" +
          "import unknown.Type;" +
          "component InvalidComp4 {}",
        IMPORTED_SYMBOL_MISSING,
        IMPORTED_SYMBOL_MISSING),
      // import of an unknown symbol from a known package
      arg("import a.Unknown;" +
          "component InvalidComp5 {}",
        IMPORTED_SYMBOL_MISSING),
      // import of a package as if it were a symbol
      arg("import a;" +
          "component InvalidComp6 {}",
        IMPORTED_SYMBOL_MISSING)
    );
  }
}
