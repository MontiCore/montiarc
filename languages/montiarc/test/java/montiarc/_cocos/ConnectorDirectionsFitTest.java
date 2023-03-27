/* (c) https://github.com/MontiCore/monticore */
package montiarc._cocos;

import arcbasis._cocos.ConnectorDirectionsFit;
import com.google.common.base.Preconditions;
import de.monticore.class2mc.OOClass2MCResolver;
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

public class ConnectorDirectionsFitTest extends AbstractCoCoTest {

  protected static final String PACKAGE = "connectorSourceAndTargetDirectionsFit";

  @Override
  @BeforeEach
  public void init() {
    super.init();
    MontiArcMill.globalScope().addAdaptedTypeSymbolResolver(new OOClass2MCResolver());
    MontiArcMill.globalScope().addAdaptedOOTypeSymbolResolver(new OOClass2MCResolver());
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
    Preconditions.checkNotNull(checker).addCoCo(new ConnectorDirectionsFit());
  }

  protected static Stream<Arguments> modelAndExpectedErrorsProvider() {
    return Stream.of(
      arg("EverythingMismatched.arc",
        ArcError.SOURCE_DIRECTION_MISMATCH, ArcError.TARGET_DIRECTION_MISMATCH,
        ArcError.SOURCE_DIRECTION_MISMATCH, ArcError.TARGET_DIRECTION_MISMATCH,
        ArcError.SOURCE_DIRECTION_MISMATCH, ArcError.TARGET_DIRECTION_MISMATCH,
        ArcError.SOURCE_DIRECTION_MISMATCH, ArcError.TARGET_DIRECTION_MISMATCH
      ),
      arg("SourceAndTargetWrongDirection.arc",
        ArcError.SOURCE_DIRECTION_MISMATCH, ArcError.TARGET_DIRECTION_MISMATCH,
        ArcError.SOURCE_DIRECTION_MISMATCH, ArcError.TARGET_DIRECTION_MISMATCH,
        ArcError.SOURCE_DIRECTION_MISMATCH, ArcError.TARGET_DIRECTION_MISMATCH,
        ArcError.SOURCE_DIRECTION_MISMATCH, ArcError.TARGET_DIRECTION_MISMATCH
      ),
      arg("SourceWrongDirection.arc",
        ArcError.SOURCE_DIRECTION_MISMATCH, ArcError.SOURCE_DIRECTION_MISMATCH,
        ArcError.SOURCE_DIRECTION_MISMATCH, ArcError.SOURCE_DIRECTION_MISMATCH
      ),
      arg("TargetWrongDirection.arc",
        ArcError.TARGET_DIRECTION_MISMATCH, ArcError.TARGET_DIRECTION_MISMATCH,
        ArcError.TARGET_DIRECTION_MISMATCH, ArcError.TARGET_DIRECTION_MISMATCH
      )
    );
  }

  @ParameterizedTest
  @ValueSource(strings = {
    "AllSourcesAndTargetsExistAndFit.arc",
    "NoConnectors.arc"})
  public void connectorSourceAndTargetDirectionsShouldFit(@NotNull String model) {
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
