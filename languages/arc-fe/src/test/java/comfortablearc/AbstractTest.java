/* (c) https://github.com/MontiCore/monticore */
package comfortablearc;

import arcbasis._ast.ASTComponentHead;
import arcbasis._ast.ASTComponentInstantiation;
import arcbasis._ast.ASTComponentType;
import arcbasis.util.ArcError;
import com.google.common.base.Preconditions;
import comfortablearc._ast.ASTArcAutoConnect;
import comfortablearc._ast.ASTArcAutoInstantiate;
import comfortablearc._ast.ASTFullyConnectedComponentInstantiation;
import comfortablearc._ast.ASTPortComplete;
import de.monticore.types.mcbasictypes._ast.ASTMCType;
import org.codehaus.commons.nullanalysis.NotNull;
import org.mockito.Mockito;

import java.util.Arrays;
import java.util.regex.Pattern;

public abstract class AbstractTest extends montiarc.util.AbstractTest {

  protected static ASTComponentType createCompType(@NotNull String type) {
    Preconditions.checkNotNull(type);
    return ComfortableArcMill.componentTypeBuilder().setName(type).setHead(Mockito.mock(ASTComponentHead.class))
      .setBody(ComfortableArcMill.componentBodyBuilder().build()).build();
  }

  protected static ASTComponentInstantiation createCompInstantiation(boolean fullyConnected, @NotNull ASTMCType type,
                                                                     @NotNull String... instances) {
    Preconditions.checkNotNull(type);
    Preconditions.checkNotNull(instances);
    Preconditions.checkArgument(!Arrays.asList(instances).contains(null));
    if (fullyConnected) {
      return createFCCompInstantiation(type, instances);
    } else {
      return createCompInstantiation(type, instances);
    }
  }

  protected static ASTComponentInstantiation createCompInstantiation(@NotNull ASTMCType type,
                                                                     @NotNull String... instances) {
    Preconditions.checkNotNull(type);
    Preconditions.checkNotNull(instances);
    Preconditions.checkArgument(!Arrays.asList(instances).contains(null));
    return ComfortableArcMill.componentInstantiationBuilder().setMCType(type).setComponentInstanceList(instances).build();
  }

  protected static ASTFullyConnectedComponentInstantiation createFCCompInstantiation(@NotNull ASTMCType type,
                                                                                     @NotNull String... instances) {
    Preconditions.checkNotNull(type);
    Preconditions.checkNotNull(instances);
    Preconditions.checkArgument(!Arrays.asList(instances).contains(null));
    return ComfortableArcMill.fullyConnectedComponentInstantiationBuilder()
      .setMCType(type).setMCType(type).setComponentInstanceList(instances).build();
  }

  protected static ASTArcAutoConnect createACOff() {
    return ComfortableArcMill.arcAutoConnectBuilder().setArcACMode(ComfortableArcMill.arcACOffBuilder().build()).build();
  }

  protected static ASTArcAutoConnect createACPort() {
    return ComfortableArcMill.arcAutoConnectBuilder().setArcACMode(ComfortableArcMill.arcACPortBuilder().build()).build();
  }

  protected static ASTArcAutoConnect createACType() {
    return ComfortableArcMill.arcAutoConnectBuilder().setArcACMode(ComfortableArcMill.arcACTypeBuilder().build()).build();
  }

  protected static ASTArcAutoInstantiate createAIOff() {
    return ComfortableArcMill.arcAutoInstantiateBuilder().setAIModeOff().build();
  }

  protected static ASTArcAutoInstantiate createAIOn() {
    return ComfortableArcMill.arcAutoInstantiateBuilder().setAIModeOn().build();
  }

  protected static ASTPortComplete createPC() {
    return ComfortableArcMill.portCompleteBuilder().build();
  }

  @Override
  protected Pattern supplyErrorCodePattern() {
    return ArcError.ERROR_CODE_PATTERN;
  }
}