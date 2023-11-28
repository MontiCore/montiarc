<#-- (c) https://github.com/MontiCore/monticore -->
<#-- ASTComponentType ast -->
${tc.signature("isTop")}

${tc.includeArgs("montiarc.generator.ma2jsim.component.Header.ftl", [isTop])} {

  protected String name;

  @Override
  public String getName() { return name; }

  ${tc.include("montiarc.generator.ma2jsim.component.schedule.SchedulerInComponent.ftl")}

  ${tc.include("montiarc.generator.ma2jsim.component.parameters.Parameters.ftl")}

  ${tc.include("montiarc.generator.ma2jsim.component.features.Features.ftl")}

  ${tc.include("montiarc.generator.ma2jsim.component.fields.Fields.ftl")}

  ${tc.include("montiarc.generator.ma2jsim.component.ports.Ports.ftl")}

  ${tc.includeArgs("montiarc.generator.ma2jsim.component.Constructor.ftl", [isTop])}

  ${tc.include("montiarc.generator.ma2jsim.component.ticks.Ticks.ftl")}

  ${tc.include("montiarc.generator.ma2jsim.component.handleMessage.HandleMessage.ftl")}

  <#if ast.getSymbol().isAtomic()>
    ${tc.include("montiarc.generator.ma2jsim.component.atomic.Atomic.ftl")}
  <#else>
    ${tc.include("montiarc.generator.ma2jsim.component.subcomponents.Subcomponents.ftl")}

    ${tc.include("montiarc.generator.ma2jsim.component.Connectors.ftl")}
  </#if>

  <#if helper.getModeAutomaton(ast).isPresent()>
    ${tc.include("montiarc.generator.ma2jsim.dynamics.componentReferences.DynamicReferencesInComponent.ftl")}
  </#if>

  ${tc.include("montiarc.generator.ma2jsim.component.init.Init.ftl")}
}
