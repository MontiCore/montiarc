<#-- (c) https://github.com/MontiCore/monticore -->
<#-- ASTComponentType ast -->
${tc.signature("isTop")}
<#import "/montiarc/generator/ma2jsim/util/Util.ftl" as Util>

${tc.includeArgs("montiarc.generator.ma2jsim.component.Header.ftl", [isTop])} {

  protected String name;

  @Override
  public String getName() { return name; }

  ${tc.include("montiarc.generator.ma2jsim.component.parameters.Parameters.ftl")}

  ${tc.include("montiarc.generator.ma2jsim.component.features.Features.ftl")}

  ${tc.include("montiarc.generator.ma2jsim.component.fields.Fields.ftl")}

  ${tc.include("montiarc.generator.ma2jsim.component.ports.Ports.ftl")}

  ${tc.includeArgs("montiarc.generator.ma2jsim.component.Constructor.ftl", [isTop])}

  <#if ast.getSymbol().isAtomic()>
    ${tc.include("montiarc.generator.ma2jsim.component.atomic.Atomic.ftl")}
  <#else>
    ${tc.include("montiarc.generator.ma2jsim.component.subcomponents.Subcomponents.ftl")}

    ${tc.include("montiarc.generator.ma2jsim.component.Connectors.ftl")}
  </#if>

  ${tc.include("montiarc.generator.ma2jsim.component.init.Init.ftl")}
}
