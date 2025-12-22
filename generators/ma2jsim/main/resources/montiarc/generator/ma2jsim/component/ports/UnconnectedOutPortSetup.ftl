<#-- (c) https://github.com/MontiCore/monticore -->
<#-- @ftlvariable name="ast" type=" arcbasis._ast.ASTArcComponentType" -->
<#-- @ftlvariable name="helper" type="montiarc.generator.util.Helper" -->
<#import "/montiarc/generator/ma2jsim/util/MethodNames.ftl" as MethodNames>
<#import "/montiarc/generator/ma2jsim/util/Util.ftl" as Util>

<#assign modeAutomatonOpt = helper.getComponentHelper().getModeAutomaton(ast)/>

<#if !modeAutomatonOpt.isPresent()>
  <#list helper.getVariantHelper().getVariants(ast) as variant>
    protected void <@MethodNames.setupUnconnectedOutPorts/>${helper.getVariantHelper().variantSuffix(variant)}() {
      this.unconnectedOutputs = java.util.Set.of(
      <#list helper.getComponentHelper().getUnconnectedOutPortsWithoutModes(variant) as port>
        this.${prefixes.port()}${port.getName()}${helper.getVariantHelper().portVariantSuffix(ast, port)}()
      <#sep>,
      </#list>
      );
    }
  </#list>
</#if>