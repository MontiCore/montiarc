/* (c) https://github.com/MontiCore/monticore */
package montiarc._cocos;

import arcbasis._cocos.ConnectorSourceAndTargetTypesFit;
import arcbasis.util.ArcError;
import com.google.common.base.Preconditions;
import de.monticore.symbols.oosymbols._symboltable.OOTypeSymbol;
import de.monticore.types.check.SymTypeExpressionFactory;
import montiarc.MontiArcMill;
import arcbasis.util.Error;
import org.codehaus.commons.nullanalysis.NotNull;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.junit.jupiter.params.provider.ValueSource;

import java.util.stream.Stream;

public class ConnectorSourceAndTargetTypesFitTest extends AbstractCoCoTest {

  protected static final String PACKAGE = "connectorSourceAndTargetTypesFit";

  @Override
  @BeforeEach
  public void init() {
    super.init();
    this.getCLI().initializeBasicOOTypes();
    this.loadList();
    this.loadPersonAndStudent();
    this.loadComponentsToInstantiate();
  }

  public void loadList() {
    OOTypeSymbol listSym = MontiArcMill.oOTypeSymbolBuilder()
      .setName("List")
      .setSpannedScope(MontiArcMill.scope())
      .build();
    listSym.addTypeVarSymbol(MontiArcMill.typeVarSymbolBuilder().setName("T").build());
    MontiArcMill.globalScope().add(listSym);
    MontiArcMill.globalScope().addSubScope(listSym.getSpannedScope());
  }

  public void loadPersonAndStudent() {
    OOTypeSymbol personSym = MontiArcMill.oOTypeSymbolBuilder()
      .setName("Person")
      .setSpannedScope(MontiArcMill.scope())
      .build();
    MontiArcMill.globalScope().add(personSym);
    MontiArcMill.globalScope().addSubScope(personSym.getSpannedScope());

    OOTypeSymbol studentSym = MontiArcMill.oOTypeSymbolBuilder()
      .setName("Student")
      .setSpannedScope(MontiArcMill.scope())
      .addSuperTypes(SymTypeExpressionFactory.createTypeObject(personSym))
      .build();
    MontiArcMill.globalScope().add(studentSym);
    MontiArcMill.globalScope().addSubScope(studentSym.getSpannedScope());
  }

  public void loadComponentsToInstantiate() {
    // loading helper models into the symboltable
    this.parseAndCreateAndCompleteSymbols("subcomponentDefinitions/Generic.arc");
  }

  @Override
  protected String getPackage() {
    return PACKAGE;
  }

  @Override
  protected void registerCoCos(@NotNull MontiArcCoCoChecker checker) {
    Preconditions.checkNotNull(checker).addCoCo(new ConnectorSourceAndTargetTypesFit());
  }

  protected static Stream<Arguments> modelAndExpectedErrorsProvider() {
    return Stream.of(
      arg("EverythingMismatched.arc",
        ArcError.SOURCE_AND_TARGET_TYPE_MISMATCH, ArcError.SOURCE_AND_TARGET_TYPE_MISMATCH,
        ArcError.SOURCE_AND_TARGET_TYPE_MISMATCH, ArcError.SOURCE_AND_TARGET_TYPE_MISMATCH
      ),
      arg("SourceAndTargetTypeMismatch.arc",
        ArcError.SOURCE_AND_TARGET_TYPE_MISMATCH, ArcError.SOURCE_AND_TARGET_TYPE_MISMATCH,
        ArcError.SOURCE_AND_TARGET_TYPE_MISMATCH, ArcError.SOURCE_AND_TARGET_TYPE_MISMATCH
      ),
      arg("GenericTypesIncorrect.arc",
        ArcError.SOURCE_AND_TARGET_TYPE_MISMATCH, ArcError.SOURCE_AND_TARGET_TYPE_MISMATCH,
        ArcError.SOURCE_AND_TARGET_TYPE_MISMATCH
      )
    );
  }

  @ParameterizedTest
  @ValueSource(strings = {
    "AllSourcesAndTargetsExistAndFit.arc",
    "NoConnectors.arc",
    "GenericTypesCorrect.arc",
    "SubTypeCompatibility.arc"})
  public void connectorSourceAndTargetTypesShouldFit(@NotNull String model) {
    Preconditions.checkNotNull(model);

    testModel(model);
  }

  @ParameterizedTest
  @MethodSource("modelAndExpectedErrorsProvider")
  public void shouldFind(@NotNull String model, @NotNull Error... errors) {
    Preconditions.checkNotNull(model);
    Preconditions.checkNotNull(errors);

    testModel(model, errors);
  }
}
