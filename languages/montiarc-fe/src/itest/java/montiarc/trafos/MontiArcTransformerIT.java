package montiarc.trafos;

import arcbasis.ArcBasisMill;
import arcbasis._ast.ASTArcBasisNode;
import arcbasis._ast.ASTComponentType;
import arcbasis.trafos.ArcBasisNodeTransformer;
import arcbasis.trafos.ComponentTypeTransformer;
import arccore._ast.ASTArcCoreNode;
import arccore.trafos.ArcCoreNodeTransformer;
import comfortablearc.ComfortableArcMill;
import comfortablearc._ast.ASTComfortableArcNode;
import comfortablearc.trafos.ComfortableArcNodeTransformer;
import de.monticore.types.mcbasictypes._ast.ASTMCType;
import genericarc.GenericArcMill;
import genericarc._ast.ASTGenericArcNode;
import genericarc.trafos.GenericArcNodeTransformer;
import montiarc.AbstractTest;
import montiarc._ast.ASTMACompilationUnit;
import montiarc._ast.ASTMontiArcNode;
import montiarc._parser.MontiArcParser;
import montiarc._visitor.MontiArcVisitor;
import org.codehaus.commons.nullanalysis.NotNull;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.mockito.Mockito;

import java.io.IOException;
import java.util.Optional;

class MontiArcTransformerIT extends AbstractTest {

  String MODEL_PATH = "src/test/itest/resources/montiarc/";

  protected static ArcBasisNodeTransformer arcBasisTrafo =
    Mockito.mock(ArcBasisNodeTransformer.class);
  protected static ComponentTypeTransformer compTypeTrafo =
    Mockito.mock(ComponentTypeTransformer.class);
  protected static ComfortableArcNodeTransformer comfyArcTrafo =
    Mockito.mock(ComfortableArcNodeTransformer.class);
  protected static GenericArcNodeTransformer genericArcTrafo =
    Mockito.mock(GenericArcNodeTransformer.class);
  protected static ArcCoreNodeTransformer arcCoreTrafo =
    Mockito.mock(ArcCoreNodeTransformer.class);
  protected static MontiArcNodeTransformer montiArcTrafo =
    Mockito.mock(MontiArcNodeTransformer.class);
  protected static MontiArcTransformer transformer = new MontiArcTransformer();

  @BeforeAll
  static void addTrafos() {
    transformer.addTrafo(arcBasisTrafo)
      .addTrafo(compTypeTrafo)
      .addTrafo(comfyArcTrafo)
      .addTrafo(genericArcTrafo)
      .addTrafo(arcCoreTrafo)
      .addTrafo(montiArcTrafo);
  }

  @BeforeAll
  static void reset() {
    Mockito.reset(arcBasisTrafo);
    Mockito.reset(compTypeTrafo);
    Mockito.reset(comfyArcTrafo);
    Mockito.reset(genericArcTrafo);
    Mockito.reset(arcCoreTrafo);
    Mockito.reset(montiArcTrafo);
  }

  @ParameterizedTest
  @ValueSource(strings = { "light.AlarmCheck.arc", "light.Arbiter", "light.DoorEval",
    "light.LightCtrl" })
  void shouldInvokeMethodsOnAllNodes(@NotNull String model) {
    MontiArcParser parser = new MontiArcParser();
    Optional<ASTMACompilationUnit> optAst;
    try {
      optAst = parser.parse(MODEL_PATH + model);
    }
    catch (IOException e) {
      throw new RuntimeException(e);
    }
    Assertions.assertTrue(optAst.isPresent());
    ClassCountVisitor classCount = new ClassCountVisitor();
    classCount.handle(optAst.get());

    transformer.transformAll(optAst.get());

    Mockito.verify(arcBasisTrafo, Mockito.times(classCount.getArcCoreNodeCount()))
      .transform(Mockito.any());
    Mockito.verify(compTypeTrafo, Mockito.times(classCount.getComponentTypeCount()))
      .transform(Mockito.any());
    Mockito.verify(genericArcTrafo, Mockito.times(classCount.getGenericArcNodeCount()))
      .transform(Mockito.any());
    Mockito.verify(comfyArcTrafo, Mockito.times(classCount.getComfortableArcNodeCount()))
      .transform(Mockito.any());
    Mockito.verify(arcCoreTrafo, Mockito.times(classCount.getArcCoreNodeCount()))
      .transform(Mockito.any());
    Mockito.verify(montiArcTrafo, Mockito.times(classCount.getMontiArcNodeCount()))
      .transform(Mockito.any());
  }

  @Test
  public void shouldInvokeMethodsOnArcBasisNodes() {
    ASTArcBasisNode ast = ArcBasisMill.componentHeadBuilder()
      .addArcParameters(ArcBasisMill.arcParameterBuilder()
        .setMCType(Mockito.mock(ASTMCType.class)).setName("foo").build())
      .build();

    transformer.transformAll(ast);

    Mockito.verify(genericArcTrafo, Mockito.times(2)).transform(Mockito.any());
  }

  @Test
  public void shouldInvokeMethodsOnGenericArcNodes() {
    ASTGenericArcNode ast = GenericArcMill.genericComponentHeadBuilder()
      .addArcTypeParameters(GenericArcMill.arcTypeParameterBuilder()
        .setName("T").build())
      .build();

    transformer.transformAll(ast);

    Mockito.verify(genericArcTrafo, Mockito.times(2)).transform(Mockito.any());
  }

  @Test
  public void shouldInvokeMethodsOnComfortableArcNodes() {
    ASTComfortableArcNode ast = ComfortableArcMill.arcAutoConnectBuilder()
      .setArcACMode(ComfortableArcMill.arcACOffBuilder().build()).build();

    transformer.transformAll(ast);

    Mockito.verify(comfyArcTrafo, Mockito.times(2)).transform(Mockito.any());
  }

  @Test
  public void shouldInvokeMethodsOnArcCoreNodes() {
    ASTArcCoreNode ast = Mockito.mock(ASTArcCoreNode.class);

    ClassCountVisitor classCounter = new ClassCountVisitor();
    classCounter.handle(ast);

    transformer.transformAll(ast);

    Mockito.verify(arcCoreTrafo, Mockito.times(classCounter.getArcCoreNodeCount()))
      .transform(Mockito.any());
  }

  protected static class ClassCountVisitor implements MontiArcVisitor {

    protected int arcBasisNodeCount = 0;
    protected int componentTypeNodeCount = 0;
    protected int genericArcNodeCount = 0;
    protected int comfortableArcNodeCount = 0;
    protected int arcCoreNodeCount = 0;
    protected int montiArcNodeCount = 0;

    protected void reset() {
      arcBasisNodeCount = 0;
      componentTypeNodeCount = 0;
      genericArcNodeCount = 0;
      comfortableArcNodeCount = 0;
      arcCoreNodeCount = 0;
      montiArcNodeCount = 0;
    }

    public int getArcBasisNodeCount() {
      return montiArcNodeCount;
    }

    public int getComponentTypeCount() {
      return componentTypeNodeCount;
    }

    public int getGenericArcNodeCount() {
      return genericArcNodeCount;
    }

    public int getComfortableArcNodeCount() {
      return comfortableArcNodeCount;
    }

    public int getArcCoreNodeCount() {
      return arcCoreNodeCount;
    }

    public int getMontiArcNodeCount() {
      return montiArcNodeCount;
    }

    @Override
    public void visit(@NotNull ASTArcBasisNode node) {
      arcBasisNodeCount++;
    }

    @Override
    public void visit(@NotNull ASTComponentType node) {
      componentTypeNodeCount++;
    }

    @Override
    public void visit(@NotNull ASTGenericArcNode node) {
      genericArcNodeCount++;
    }

    @Override
    public void visit(@NotNull ASTComfortableArcNode node) {
      comfortableArcNodeCount++;
    }

    @Override
    public void visit(@NotNull ASTArcCoreNode node) {
      arcCoreNodeCount++;
    }

    @Override
    public void visit(@NotNull ASTMontiArcNode node) {
      montiArcNodeCount++;
    }
  }
}