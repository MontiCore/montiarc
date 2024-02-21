<#-- (c) https://github.com/MontiCore/monticore -->
<#-- ASTMACompilationUnit ast -->
${tc.signature("ast", "isTop", "variant")}
/* (c) https://github.com/MontiCore/monticore */
<#if ast.isPresentPackage()>
    ${tc.include("montiarc.generator.Package.ftl", ast.getPackage())}
</#if>
<#assign ast = variant.getAstNode() />
<#assign compute = helper.getComputeBehavior(variant.getAstNode()).get() />
<#import "/montiarc/generator/ma2jsim/util/Util.ftl" as Util>

${tc.includeArgs("montiarc.generator.ma2jsim.behavior.compute.Header.ftl", ast, [isTop, compute])} {

  protected ${ast.getName()}${suffixes.compute()}${helper.variantSuffix(variant)}<#if isTop>TOP</#if> (
  ${ast.getName()}${suffixes.context()} ${ast.getName()?uncap_first}${suffixes.context()}) {
  super(${ast.getName()?uncap_first}${suffixes.context()});
  }

  @Override
  public void init() {
  ${tc.includeArgs("montiarc/generator/ma2jsim/behavior/ShadowParameters.ftl", ast, [ast.getHead().getArcParameterList()])}
  ${tc.includeArgs("montiarc/generator/ma2jsim/behavior/ShadowFields.ftl", ast, [ast.getFields()])}
  ${tc.includeArgs("montiarc/generator/ma2jsim/behavior/ShadowFeatures.ftl", ast, [helper.getFeatures(ast)])}
  ${tc.includeArgs("montiarc/generator/ma2jsim/behavior/ShadowOutputs.ftl", ast, [ast.getSymbol().getAllOutgoingPorts()])}
  <#if helper.getComputeInit(variant.getAstNode()).isPresent()>
    ${prettyPrinter.prettyprint(helper.getComputeInit(variant.getAstNode()).get().getMCBlockStatement())}
  </#if>

  ${tc.includeArgs("montiarc/generator/ma2jsim/behavior/SendInitialTicksOnDelayedPorts.ftl", ast, [ast.getSymbol().getAllOutgoingPorts()])}
  ${tc.includeArgs("montiarc/generator/ma2jsim/behavior/SetShadowedFields.ftl", ast, [ast.getFields()])}
  }

  @Override
  public void tick() {
  ${tc.includeArgs("montiarc/generator/ma2jsim/behavior/ShadowFields.ftl", ast, [ast.getFields()])}
  ${tc.includeArgs("montiarc/generator/ma2jsim/behavior/ShadowParameters.ftl", ast, [ast.getHead().getArcParameterList()])}
  ${tc.includeArgs("montiarc/generator/ma2jsim/behavior/ShadowFeatures.ftl", ast, [helper.getFeatures(ast)])}
  ${tc.includeArgs("montiarc/generator/ma2jsim/behavior/ShadowInputs.ftl", ast, [ast.getSymbol().getAllIncomingPorts(), true])}
  ${tc.includeArgs("montiarc/generator/ma2jsim/behavior/ShadowOutputs.ftl", ast, [ast.getSymbol().getAllOutgoingPorts()])}
  ${prettyPrinter.prettyprint(compute.getMCBlockStatement())}
  ${tc.includeArgs("montiarc/generator/ma2jsim/behavior/SetShadowedFields.ftl", ast, [ast.getFields()])}
  }

}