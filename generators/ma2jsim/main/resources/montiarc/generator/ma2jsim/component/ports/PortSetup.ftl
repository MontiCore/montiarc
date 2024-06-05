<#-- (c) https://github.com/MontiCore/monticore -->
<#-- ASTComponentType ast -->
<#import "/montiarc/generator/ma2jsim/util/MethodNames.ftl" as MethodNames>
<#import "/montiarc/generator/ma2jsim/util/Util.ftl" as Util>

<#list helper.getVariants(ast) as variant>
  <#assign atomic = variant.isAtomic()>
  protected void <@MethodNames.portSetup/>${helper.variantSuffix(variant)}() {
    ${tc.include("montiarc.generator.ma2jsim.component.ShadowConstants.ftl")}

    <#list ast.getSymbol().getAllIncomingPorts() as port>
      this.${prefixes.port()}${port.getName()}${helper.portVariantSuffix(ast, port)} = new montiarc.rte.port.TimeAwarePortForComposition<>(getName() + ".${port.getName()}", this, scheduler);
    </#list>
    <#list ast.getSymbol().getAllOutgoingPorts() as port>
      this.${prefixes.port()}${port.getName()}${helper.portVariantSuffix(ast, port)} = new montiarc.rte.port.TimeAwarePortForward<>(getName() + ".${port.getName()}", this);
    </#list>

    this.tickPort = new montiarc.rte.port.TimeAwarePortForComposition<>(getName() + ".#tick", this, scheduler);
  }
</#list>