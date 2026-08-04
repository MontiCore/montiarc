/* (c) https://github.com/MontiCore/monticore */
package de.monticore.sd2arc;

import com.google.common.base.Preconditions;
import de.monticore.class2mc.OOClass2MCResolver;
import de.monticore.lang.sd4components.SD4ComponentsMill;
import de.monticore.lang.sd4components.SD4ComponentsTool;
import de.monticore.lang.sd4components._cocos.SD4ComponentsCoCoChecker;
import de.monticore.lang.sdbasis._ast.ASTSDArtifact;
import de.monticore.sd2arc._cocos.ImpliedConnectorsFitEmbeddingComponentCoCo;
import de.monticore.sd2arc._cocos.ObserveOnUnconnectedPortCoCo;
import de.monticore.sd2arc._cocos.SubcomponentExistsInEmbeddingComponentCoCo;
import de.monticore.sd2arc.codegen.SDGenerator;
import de.monticore.sd2arc.codegen.SDHelper;
import de.monticore.sd2arc.trafo.AddAdjacentSubcomponents;
import de.monticore.sd2arc.trafo.AddDefaultMatchToComponents;
import de.monticore.sd2arc.trafo.CompleteInteractionsTrafo;
import de.monticore.sd2arc.trafo.EmbeddingComponent;
import de.monticore.symbols.compsymbols._symboltable.SubcomponentSymbolDeSer;
import de.monticore.symbols.oosymbols._symboltable.FieldSymbolDeSer;
import de.monticore.symbols.oosymbols._symboltable.MethodSymbolDeSer;
import de.monticore.symbols.oosymbols._symboltable.OOTypeSymbolDeSer;
import montiarc.MontiArcMill;
import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.Option;
import org.apache.commons.cli.Options;
import org.codehaus.commons.nullanalysis.NotNull;

import java.nio.file.Path;
import java.util.List;

public class SD2ArcTool extends SD4ComponentsTool {

  public static void main(String[] args) {
    SD2ArcTool tool = new SD2ArcTool();
    tool.run(args);
  }

  @Override
  public ASTSDArtifact parse(String model) {
    ASTSDArtifact artifact = super.parse(model);
    if (artifact != null) {
      return CompleteInteractionsTrafo.transform(AddAdjacentSubcomponents.transform(EmbeddingComponent.inject(AddDefaultMatchToComponents.transform(artifact))));
    }
    return artifact;
  }

  @Override
  public void runAdditionalCoCos(ASTSDArtifact ast) {
    SD4ComponentsCoCoChecker checker = new SD4ComponentsCoCoChecker();
    checker.addCoCo(new SubcomponentExistsInEmbeddingComponentCoCo());
    checker.addCoCo(new ImpliedConnectorsFitEmbeddingComponentCoCo());
    checker.addCoCo(new ObserveOnUnconnectedPortCoCo());

    checker.checkAll(ast);
  }

  @Override
  public void init() {
    MontiArcMill.init();
    super.init();
  }

  @Override
  public void runAdditionalTasks(CommandLine cmd, List<ASTSDArtifact> inputSDs) {
    if (cmd.hasOption("defaultTicks")) {
      SDHelper.defaultTicks = Long.parseLong(cmd.getOptionValue("defaultTicks"));
    }
    if (cmd.hasOption("output")) {
      SDGenerator generator = new SDGenerator(Path.of(cmd.getOptionValue("output")));
      inputSDs.forEach(generator::generate);
    }
  }

  protected void initializeClass2MC(@NotNull CommandLine cl) {
    Preconditions.checkNotNull(cl);
    if (cl.hasOption("c2mc")) {
      this.initializeClass2MC();
    }
  }

  public void initializeClass2MC() {
    MontiArcMill.globalScope().addAdaptedTypeSymbolResolver(new OOClass2MCResolver());
    MontiArcMill.globalScope().addAdaptedOOTypeSymbolResolver(new OOClass2MCResolver());
  }

  @Override
  protected void initGlobalScope(CommandLine cl) {
    super.initGlobalScope(cl);
    initializeClass2MC(cl);
  }

  @Override
  public void initGlobalScope() {
    super.initGlobalScope();
    SD4ComponentsMill.globalScope().putSymbolDeSer("de.monticore.symbols.compsymbols._symboltable.SubcomponentSymbol", new SubcomponentSymbolDeSer());
    SD4ComponentsMill.globalScope().putSymbolDeSer("de.monticore.cdbasis._symboltable.CDTypeSymbol", new OOTypeSymbolDeSer());
    SD4ComponentsMill.globalScope().putSymbolDeSer("de.monticore.cd4codebasis._symboltable.CDMethodSignatureSymbol", new MethodSymbolDeSer());
    SD4ComponentsMill.globalScope().putSymbolDeSer("de.monticore.cdassociation._symboltable.CDRoleSymbol", new FieldSymbolDeSer());
  }

  @Override
  public Options addStandardOptions(@NotNull Options options) {
    Preconditions.checkNotNull(options);
    options.addOption(org.apache.commons.cli.Option.builder("o")
      .longOpt("output")
      .hasArgs()
      .desc("Sets the target path for the generated files (optional).")
      .get());
    // class2mc
    options.addOption(Option.builder("c2mc")
      .longOpt("class2mc")
      .desc("Enables to resolve java classes in the model path")
      .get());
    // defaultTicks
    options.addOption(Option.builder("t")
      .longOpt("defaultTicks")
      .desc("Sets the default ticks for the generated files (optional).")
      .type(Number.class)
      .numberOfArgs(1)
      .get());
    return super.addStandardOptions(options);
  }
}
