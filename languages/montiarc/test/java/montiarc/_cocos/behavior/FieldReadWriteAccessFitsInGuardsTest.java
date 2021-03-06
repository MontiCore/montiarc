/* (c) https://github.com/MontiCore/monticore */
package montiarc._cocos.behavior;

import arcautomaton._cocos.FieldReadWriteAccessFitsInGuards;
import montiarc.util.ArcError;
import com.google.common.base.Preconditions;
import de.monticore.symbols.oosymbols._symboltable.FieldSymbol;
import de.monticore.symbols.oosymbols._symboltable.MethodSymbol;
import de.monticore.symbols.oosymbols._symboltable.OOTypeSymbol;
import de.monticore.types.check.SymTypeExpressionFactory;
import montiarc.MontiArcMill;
import montiarc._cocos.AbstractCoCoTest;
import montiarc._cocos.MontiArcCoCoChecker;
import montiarc.util.Error;
import org.codehaus.commons.nullanalysis.NotNull;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.junit.jupiter.params.provider.ValueSource;

import java.util.stream.Stream;

class FieldReadWriteAccessFitsInGuardsTest extends AbstractCoCoTest {

  @BeforeEach
  public void loadLType() {
    OOTypeSymbol lSym = MontiArcMill.oOTypeSymbolBuilder()
      .setName("L")
      .setSpannedScope(MontiArcMill.scope())
      .build();
    MontiArcMill.globalScope().add(lSym);
    MontiArcMill.globalScope().addSubScope(lSym.getSpannedScope());

    // add fields lField, lFieldStatic, set the last one to be static
    FieldSymbol lField = MontiArcMill.fieldSymbolBuilder()
      .setName("lField")
      .setType(SymTypeExpressionFactory.createPrimitive("int"))
      .build();
    FieldSymbol lFieldStatic = MontiArcMill.fieldSymbolBuilder()
      .setName("lFieldStatic")
      .setType(SymTypeExpressionFactory.createPrimitive("int"))
      .setIsStatic(true)
      .build();

    lSym.addFieldSymbol(lField);
    lSym.addFieldSymbol(lFieldStatic);

    // add methods lMethod, lMethodStatic, set the last one to be static
    MethodSymbol lMethod = MontiArcMill.methodSymbolBuilder()
      .setName("lMethod")
      .setType(SymTypeExpressionFactory.createPrimitive("int"))
      .build();
    MethodSymbol lMethodStatic = MontiArcMill.methodSymbolBuilder()
      .setName("lMethodStatic")
      .setType(SymTypeExpressionFactory.createPrimitive("int"))
      .setIsStatic(true)
      .build();

    lSym.addMethodSymbol(lMethod);
    lSym.addMethodSymbol(lMethodStatic);
  }


  @Override
  protected String getPackage() {
    return "behavior/fieldReadWriteAccessFitsInGuards";
  }

  protected static Stream<Arguments> modelAndExpectedErrorsProvider() {
    return Stream.of(
      arg("DeepIncrement.arc", ArcError.WRITE_TO_INCOMING_PORT, ArcError.READ_FROM_OUTGOING_PORT),
      arg("IncrementPort.arc", ArcError.WRITE_TO_INCOMING_PORT, ArcError.READ_FROM_OUTGOING_PORT),
      arg("InvalidAssignment.arc", ArcError.WRITE_TO_INCOMING_PORT, ArcError.WRITE_TO_INCOMING_PORT),
      arg("ReadOutgoingPort.arc", ArcError.READ_FROM_OUTGOING_PORT, ArcError.READ_FROM_OUTGOING_PORT)
    );
  }

  @ParameterizedTest
  @ValueSource(strings = {"StrangeAssignment.arc", "UsedAllInGuard.arc", "ValidIncrement.arc",
    // "WithImportedMethodAccess.arc",
    // TODO: add ^this^ test case when MontiCore's NameToCallExpressionVisitor is fixed and we have refactored the coco
    "WithQualifiedFieldAndMethodAccess.arc"
  })
  void succeed(@NotNull String model) {
    Preconditions.checkNotNull(model);

    testModel(model);
  }

  @ParameterizedTest
  @MethodSource("modelAndExpectedErrorsProvider")
  void fail(@NotNull String model, @NotNull Error... errors) {
    Preconditions.checkNotNull(model);
    Preconditions.checkNotNull(errors);

    testModel(model, errors);
  }

  @Override
  protected void registerCoCos(@NotNull MontiArcCoCoChecker checker) {
    Preconditions.checkNotNull(checker);
    checker.addCoCo(new FieldReadWriteAccessFitsInGuards());
  }
}