/* (c) https://github.com/MontiCore/monticore */
package montiarc._cocos;

import arcbasis._cocos.PortsConnected;
import montiarc.util.ArcError;
import com.google.common.base.Preconditions;
import de.se_rwth.commons.logging.Log;
import montiarc._ast.ASTMACompilationUnit;
import montiarc.util.Error;
import org.codehaus.commons.nullanalysis.NotNull;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.junit.jupiter.params.provider.ValueSource;

import java.util.stream.Stream;

public class PortsConnectedTest extends AbstractCoCoTest {

    protected static String PACKAGE = "portsConnected";

    @Override
    @BeforeEach
    public void init() {
        super.init();
    }

    @Override
    protected String getPackage() {
        return PACKAGE;
    }

    @Override
    protected void registerCoCos(@NotNull MontiArcCoCoChecker checker) {
        Preconditions.checkNotNull(checker);
        checker.addCoCo(new PortsConnected());
    }

    @ParameterizedTest
    @ValueSource(strings = {"AllPortsConnected.arc"})
    public void shouldApproveValidPortConnections(@NotNull String model) {
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
                arg("IncomingPortNotConnected.arc", ArcError.INCOMING_PORT_NO_FORWARD),
                arg("OutgoingPortNotConnected.arc", ArcError.OUTGOING_PORT_NO_FORWARD),
                arg("MissingPortConnections.arc",
                        ArcError.INCOMING_PORT_NO_FORWARD, ArcError.OUTGOING_PORT_NO_FORWARD)
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

