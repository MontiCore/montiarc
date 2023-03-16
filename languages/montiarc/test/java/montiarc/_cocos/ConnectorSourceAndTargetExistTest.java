/* (c) https://github.com/MontiCore/monticore */
package montiarc._cocos;

import arcbasis._cocos.ConnectorSourceAndTargetExist;
import com.google.common.base.Preconditions;
import de.monticore.symbols.oosymbols._symboltable.OOTypeSymbol;
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

public class ConnectorSourceAndTargetExistTest extends AbstractCoCoTest {

  protected static final String PACKAGE = "connectorSourceAndTargetExist";

  @Override
  @BeforeEach
  public void init() {
    super.init();
    this.getCLI().initializeBasicOOTypes();
    this.loadList();
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

  @Override
  protected String getPackage() {
    return PACKAGE;
  }

  @Override
  protected void registerCoCos(@NotNull MontiArcCoCoChecker checker) {
    Preconditions.checkNotNull(checker).addCoCo(new ConnectorSourceAndTargetExist());
  }

  protected static Stream<Arguments> modelAndExpectedErrorsProvider() {
    return Stream.of(
      arg("SourceAndTargetDoNotExist.arc",
        ArcError.SOURCE_PORT_NOT_EXISTS, ArcError.TARGET_PORT_NOT_EXISTS,
        ArcError.SOURCE_PORT_NOT_EXISTS, ArcError.TARGET_PORT_NOT_EXISTS,
        ArcError.SOURCE_PORT_NOT_EXISTS, ArcError.TARGET_PORT_NOT_EXISTS,
        ArcError.SOURCE_PORT_NOT_EXISTS, ArcError.TARGET_PORT_NOT_EXISTS,
        ArcError.SOURCE_PORT_COMPONENT_MISSING, ArcError.TARGET_PORT_COMPONENT_MISSING
      ),
      arg("SourceDoesNotExist.arc",
        ArcError.SOURCE_PORT_NOT_EXISTS, ArcError.SOURCE_PORT_NOT_EXISTS,
        ArcError.SOURCE_PORT_NOT_EXISTS, ArcError.SOURCE_PORT_NOT_EXISTS,
        ArcError.SOURCE_PORT_COMPONENT_MISSING
      ),
      arg("TargetDoesNotExist.arc",
        ArcError.TARGET_PORT_NOT_EXISTS, ArcError.TARGET_PORT_NOT_EXISTS,
        ArcError.TARGET_PORT_NOT_EXISTS, ArcError.TARGET_PORT_NOT_EXISTS,
        ArcError.TARGET_PORT_COMPONENT_MISSING
      )
    );
  }

  @ParameterizedTest
  @ValueSource(strings = {
    "AllSourcesAndTargetsExistAndFit.arc",
    "NoConnectors.arc"})
  public void connectorSourceAndTargetShouldExist(@NotNull String model) {
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
