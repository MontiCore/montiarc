/* (c) https://github.com/MontiCore/monticore */
package montiarc._cocos;

import arcbasis._cocos.ConnectorTypesFit;
import com.google.common.base.Preconditions;
import de.monticore.class2mc.OOClass2MCResolver;
import de.monticore.symbols.oosymbols._symboltable.OOTypeSymbol;
import de.monticore.types.check.SymTypeExpressionFactory;
import de.monticore.types.check.TypeRelations;
import montiarc.MontiArcMill;
import montiarc.util.ArcError;
import montiarc.util.Error;
import org.codehaus.commons.nullanalysis.NotNull;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.junit.jupiter.params.provider.ValueSource;

import java.util.stream.Stream;

public class ConnectorTypesFitTest extends AbstractCoCoTest {

  protected static final String PACKAGE = "connectorSourceAndTargetTypesFit";

  @Override
  @BeforeEach
  public void setUp() {
    super.setUp();
    MontiArcMill.globalScope().addAdaptedTypeSymbolResolver(new OOClass2MCResolver());
    MontiArcMill.globalScope().addAdaptedOOTypeSymbolResolver(new OOClass2MCResolver());
    this.loadPersonAndStudent();
    this.loadComponentsToInstantiate();
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
    Preconditions.checkNotNull(checker).addCoCo(new ConnectorTypesFit(new TypeRelations()));
  }

  protected static Stream<Arguments> modelAndExpectedErrorsProvider() {
    return Stream.of(
      arg("EverythingMismatched.arc",
        ArcError.CONNECTOR_TYPE_MISMATCH, ArcError.CONNECTOR_TYPE_MISMATCH,
        ArcError.CONNECTOR_TYPE_MISMATCH, ArcError.CONNECTOR_TYPE_MISMATCH
      ),
      arg("SourceAndTargetTypeMismatch.arc",
        ArcError.CONNECTOR_TYPE_MISMATCH, ArcError.CONNECTOR_TYPE_MISMATCH,
        ArcError.CONNECTOR_TYPE_MISMATCH, ArcError.CONNECTOR_TYPE_MISMATCH
      ),
      arg("GenericTypesIncorrect.arc",
        ArcError.CONNECTOR_TYPE_MISMATCH, ArcError.CONNECTOR_TYPE_MISMATCH,
        ArcError.CONNECTOR_TYPE_MISMATCH
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
