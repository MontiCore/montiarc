<#-- (c) https://github.com/MontiCore/monticore -->
<#-- ASTComponentType ast -->
<#import "/montiarc/generator/ma2jsim/util/Util.ftl" as Util>
<#import "/montiarc/generator/ma2jsim/util/MethodNames.ftl" as MethodNames>

${tc.include("montiarc.generator.ma2jsim.component.ShadowConstants.ftl")}

<#list ast.getSymbol().getAllIncomingPorts() as inPort>
    <#assign existenceConditions = helper.getExistenceCondition(ast, inPort)/>
    if(<#if existenceConditions?has_content>${prettyPrinter.prettyprint(existenceConditions)} &&</#if> receivingPort.getQualifiedName().equals(${prefixes.port()}${inPort.getName()}${helper.portVariantSuffix(ast, inPort)}().getQualifiedName())) {
      if (${prefixes.port()}${inPort.getName()}${helper.portVariantSuffix(ast, inPort)}().isBufferEmpty()) return;
      else this.schedule(this::<@MethodNames.handleBufferImplementation inPort/>${helper.portVariantSuffix(ast, inPort)});
    }
    <#sep> else </#sep>
</#list>
