<#-- (c) https://github.com/MontiCore/monticore -->
<#-- AST IGNORED -->
${tc.signature("portSymbols", "pollBuffer")}
<#import "/montiarc/generator/ma2jsim/util/Util.ftl" as Util>
<#list portSymbols as port>
  final <@Util.getTypeString port.getType()/> ${port.getName()} =
    context.${prefixes.port()}${port.getName()}().<#if pollBuffer>poll<#else>peek</#if>Buffer().getData();
</#list>
