<#-- (c) https://github.com/MontiCore/monticore -->
<#-- ASTComponentType ast -->
${tc.signature("isTop")}

${tc.includeArgs("montiarc.generator.ma2jsim.component.Header.ftl", [isTop])} {

  ${tc.include("montiarc.generator.ma2jsim.component.parameters.Parameters.ftl")}

  ${tc.include("montiarc.generator.ma2jsim.component.features.DetermineVariant.ftl")}

  ${tc.include("montiarc.generator.ma2jsim.component.features.Features.ftl")}

  ${tc.include("montiarc.generator.ma2jsim.component.fields.Fields.ftl")}

  ${tc.include("montiarc.generator.ma2jsim.component.ports.Ports.ftl")}

  ${tc.includeArgs("montiarc.generator.ma2jsim.component.Constructor.ftl", [isTop])}

  ${tc.include("montiarc.generator.ma2jsim.component.handleMessage.HandleMessage.ftl")}

  ${tc.include("montiarc.generator.ma2jsim.component.handleMessage.BuildSyncMessage.ftl")}

  ${tc.include("montiarc.generator.ma2jsim.component.ports.GetValueOfPort.ftl")}

  ${tc.include("montiarc.generator.ma2jsim.component.atomic.Atomic.ftl")}

  ${tc.include("montiarc.generator.ma2jsim.component.subcomponents.Subcomponents.ftl")}

  ${tc.include("montiarc.generator.ma2jsim.component.Connectors.ftl")}

  <#if helper.getModeAutomaton(ast).isPresent()>
    ${tc.include("montiarc.generator.ma2jsim.component.modes.ModeSubcomponents.ftl")}
    ${tc.include("montiarc.generator.ma2jsim.component.modes.ModeSetup.ftl")}
    ${tc.include("montiarc.generator.ma2jsim.component.modes.ModeInit.ftl")}
    ${tc.include("montiarc.generator.ma2jsim.component.modes.ModeTeardown.ftl")}
  </#if>

}
