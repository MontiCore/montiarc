/* (c) https://github.com/MontiCore/monticore */
package de.monticore.sd2arc;

import com.google.common.base.Preconditions;
import de.monticore.class2mc.OOClass2MCResolver;
import de.monticore.lang.sd4components.SD4ComponentsMill;
import de.monticore.lang.sd4components.SD4ComponentsTool;
import de.monticore.lang.sd4components._cocos.SD4ComponentsCoCoChecker;
import de.monticore.lang.sd4components._symboltable.ISD4ComponentsArtifactScope;
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
import de.monticore.symbols.basicsymbols.BasicSymbolsMill;
import de.monticore.symbols.compsymbols._symboltable.SubcomponentSymbolDeSer;
import de.monticore.symbols.oosymbols._symboltable.FieldSymbolDeSer;
import de.monticore.symbols.oosymbols._symboltable.MethodSymbolDeSer;
import de.monticore.symbols.oosymbols._symboltable.OOTypeSymbolDeSer;
import de.monticore.types.mcbasictypes._ast.ASTMCImportStatement;
import montiarc.MontiArcMill;
import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.Option;
import org.apache.commons.cli.Options;
import org.codehaus.commons.nullanalysis.NotNull;

import java.nio.file.Path;
import java.util.List;

public class SD2ArcTool extends SD4ComponentsTool {

  /**
   * Whether Java types are resolved via Class2MC.
   */
  protected boolean c2mc = false;

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

  /**
   * Creates the symbol table of {@code ast}. If Class2MC is enabled, the types of `java.lang` are imported by default (as
   * done by the MontiArc tool). The default import is only added for creating the symbol table, so that it does not
   * end up in the generated components.
   */
  @Override
  public ISD4ComponentsArtifactScope createSymbolTable(@NotNull ASTSDArtifact ast) {
    Preconditions.checkNotNull(ast);
    if (!c2mc) {
      return super.createSymbolTable(ast);
    }

    ASTMCImportStatement javaLang = SD4ComponentsMill.mCImportStatementBuilder()
      .setMCQualifiedName(SD4ComponentsMill.mCQualifiedNameBuilder()
        .setPartsList(List.of("java", "lang"))
        .build())
      .setStar(true)
      .build();
    ast.addMCImportStatement(javaLang);
    try {
      return super.createSymbolTable(ast);
    } finally {
      ast.removeMCImportStatement(javaLang);
    }
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

  public void initializeClass2MC() {
    SD4ComponentsMill.globalScope().addAdaptedTypeSymbolResolver(new OOClass2MCResolver());
    SD4ComponentsMill.globalScope().addAdaptedOOTypeSymbolResolver(new OOClass2MCResolver());
  }

  public boolean isClass2MC() {
    return c2mc;
  }

  public void setClass2MC(boolean c2mc) {
    this.c2mc = c2mc;
  }

  @Override
  protected void initGlobalScope(@NotNull CommandLine cl) {
    Preconditions.checkNotNull(cl);
    this.setClass2MC(cl.hasOption("c2mc"));
    super.initGlobalScope(cl);
    this.initBuiltInSymbols();
  }

  /**
   * Initializes the global scope without the built-in symbols, which are initialized by {@link #initBuiltInSymbols()}
   * once the symbol path is set.
   */
  @Override
  public void initGlobalScope() {
    SD4ComponentsMill.globalScope().clear();
    SD4ComponentsMill.globalScope().init();
    SD4ComponentsMill.globalScope().putSymbolDeSer("de.monticore.symbols.compsymbols._symboltable.SubcomponentSymbol", new SubcomponentSymbolDeSer());
    SD4ComponentsMill.globalScope().putSymbolDeSer("de.monticore.cdbasis._symboltable.CDTypeSymbol", new OOTypeSymbolDeSer());
    SD4ComponentsMill.globalScope().putSymbolDeSer("de.monticore.cd4codebasis._symboltable.CDMethodSignatureSymbol", new MethodSymbolDeSer());
    SD4ComponentsMill.globalScope().putSymbolDeSer("de.monticore.cdassociation._symboltable.CDRoleSymbol", new FieldSymbolDeSer());
  }

  /**
   * Initializes the built-in symbols. If Class2MC is enabled, Java types are resolved via Class2MC. The built-in
   * `String` type is only added if `java.lang.String` cannot be resolved (as done by the MontiArc tool). Hence, this
   * must be called after the symbol path of the global scope is set.
   */
  public void initBuiltInSymbols() {
    BasicSymbolsMill.initializePrimitives();
    if (c2mc) {
      this.initializeClass2MC();
    }
    if (SD4ComponentsMill.globalScope().resolveType("java.lang.String").isEmpty()) {
      BasicSymbolsMill.initializeString();
    }
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
