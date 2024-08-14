<#-- (c) https://github.com/MontiCore/monticore -->
<#-- ASTComponentType ast -->
<#import "/montiarc/generator/ma2jsim/util/MethodNames.ftl" as MethodNames>
<#import "/montiarc/generator/ma2jsim/util/Util.ftl" as Util>

<#assign modeAutomatonOpt = helper.getModeAutomaton(ast)/>

<#if !modeAutomatonOpt.isPresent()>
  <#list helper.getVariants(ast) as variant>
    protected void <@MethodNames.setupUnconnectedOutPorts/>${helper.variantSuffix(variant)}() {
      this.unconnectedOutputs = java.util.Set.of(
      <#list helper.getUnconnectedOutPortsWithoutModes(variant) as port>
        this.${prefixes.port()}${port.getName()}${helper.portVariantSuffix(ast, port)}()
      <#sep>,
      </#list>
      );
    }
  </#list>
</#if>