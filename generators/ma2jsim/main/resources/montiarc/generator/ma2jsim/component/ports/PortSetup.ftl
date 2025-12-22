<#-- (c) https://github.com/MontiCore/monticore -->
<#-- @ftlvariable name="ast" type=" arcbasis._ast.ASTArcComponentType" -->
<#-- @ftlvariable name="helper" type="montiarc.generator.util.Helper" -->
<#import "/montiarc/generator/ma2jsim/util/MethodNames.ftl" as MethodNames>
<#import "/montiarc/generator/ma2jsim/util/Util.ftl" as Util>

<#list helper.getVariantHelper().getVariants(ast) as variant>
  <#assign atomic = variant.isAtomic()>
  protected void <@MethodNames.portSetup/>${helper.getVariantHelper().variantSuffix(variant)}() {
    ${tc.include("montiarc.generator.ma2jsim.component.ShadowConstants.ftl")}

    <#list variant.getAllIncomingPorts() as port>
      this.${prefixes.port()}${port.getName()}${helper.getVariantHelper().portVariantSuffix(ast, port)} = new montiarc.rte.port.ScheduledPort<>(getName() + ".${port.getName()}", this, scheduler);
    </#list>
    <#list variant.getAllOutgoingPorts() as port>
      this.${prefixes.port()}${port.getName()}${helper.getVariantHelper().portVariantSuffix(ast, port)} = new montiarc.rte.port.PortForward<>(getName() + ".${port.getName()}", this);
    </#list>
  }
</#list>