/* (c) https://github.com/MontiCore/monticore */
package montiarc._cocos;

import arcbasis._cocos.ParameterTypeExists;
import arcbasis.util.ArcError;
import com.google.common.base.Preconditions;
import de.monticore.symbols.basicsymbols._symboltable.TypeSymbol;
import de.monticore.symbols.basicsymbols._symboltable.TypeVarSymbol;
import de.se_rwth.commons.logging.Log;
import montiarc.MontiArcMill;
import montiarc._ast.ASTMACompilationUnit;
import montiarc._cocos.util.CheckTypeExistence4MontiArc;
import montiarc._symboltable.IMontiArcScope;
import montiarc.util.Error;
import org.codehaus.commons.nullanalysis.NotNull;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.junit.jupiter.params.provider.ValueSource;

import java.util.stream.Stream;

public class ParameterTypeExistsTest extends AbstractCoCoTest {

  protected static String PACKAGE = "parameterTypeExists";

  @Override
  @BeforeEach
  public void init() {
    super.init();
    addListToGlobalScope();
    addFooToGlobalScope();
    addPersonToGlobalScope();
    addQualTypesToGlobalScope();
  }

  protected static void addPersonToGlobalScope() {
    MontiArcMill.globalScope()
      .add(MontiArcMill.typeSymbolBuilder().setName("Person")
        .setEnclosingScope(MontiArcMill.globalScope())
        .build());
  }

  protected static void addListToGlobalScope() {
    TypeSymbol listSym = MontiArcMill.typeSymbolBuilder().setName("List")
      .setSpannedScope(MontiArcMill.scope())
      .build();
    TypeVarSymbol tVar = MontiArcMill.typeVarSymbolBuilder().setName("T").build();
    listSym.addTypeVarSymbol(tVar);

    MontiArcMill.globalScope().add(listSym);
    MontiArcMill.globalScope().addSubScope(listSym.getSpannedScope());
  }

  protected static void addFooToGlobalScope() {
    TypeSymbol fooSym = MontiArcMill.typeSymbolBuilder().setName("Foo")
      .setSpannedScope(MontiArcMill.scope())
      .build();
    TypeVarSymbol uVar = MontiArcMill.typeVarSymbolBuilder().setName("U").build();
    fooSym.addTypeVarSymbol(uVar);

    MontiArcMill.globalScope().add(fooSym);
    MontiArcMill.globalScope().addSubScope(fooSym.getSpannedScope());
  }

  protected static void addQualTypesToGlobalScope() {
    IMontiArcScope scope = MontiArcMill.scope();
    scope.setName("qual");
    MontiArcMill.globalScope().addSubScope(scope);

    scope.add(MontiArcMill.typeSymbolBuilder().setName("Typee")
      .setEnclosingScope(scope)
      .build());

    TypeSymbol genericSym = MontiArcMill.typeSymbolBuilder().setName("Generic")
      .setSpannedScope(MontiArcMill.scope())
      .build();
    TypeVarSymbol wVar = MontiArcMill.typeVarSymbolBuilder().setName("W").build();
    genericSym.addTypeVarSymbol(wVar);

    scope.add(genericSym);
    scope.addSubScope(genericSym.getSpannedScope());
  }

  @Override
  protected String getPackage() {
    return PACKAGE;
  }

  @Override
  protected void registerCoCos(@NotNull MontiArcCoCoChecker checker) {
    Preconditions.checkNotNull(checker);
    checker.addCoCo(new ParameterTypeExists(new CheckTypeExistence4MontiArc()));
  }


  @ParameterizedTest
  @ValueSource(strings = {"HasValidTypes.arc"})
  public void shouldApproveValidTypes(@NotNull String model) {
    Preconditions.checkNotNull(model);

    //Given
    ASTMACompilationUnit ast = this.parseAndCreateAndCompleteSymbols(model);

    //When
    this.getChecker().checkAll(ast);

    //Then
    Assertions.assertEquals(0, Log.getFindingsCount());
  }

  protected static Stream<Arguments> modelAndExpectedErrorsProvider() {
    return Stream.of(
      arg("HasInvalidTypes.arc",
        ArcError.MISSING_TYPE, ArcError.MISSING_TYPE, ArcError.MISSING_TYPE, ArcError.MISSING_TYPE,
        ArcError.MISSING_TYPE, ArcError.MISSING_TYPE)
    );
  }

  @ParameterizedTest
  @MethodSource("modelAndExpectedErrorsProvider")
  public void shouldFindInvalidTypes(@NotNull String model, @NotNull Error... errors) {
    Preconditions.checkNotNull(model);
    Preconditions.checkNotNull(errors);

    //Given
    ASTMACompilationUnit ast = this.parseAndCreateSymbols(model);

    //When
    this.getChecker().checkAll(ast);

    //Then
    this.checkOnlyExpectedErrorsPresent(errors);
  }
}
