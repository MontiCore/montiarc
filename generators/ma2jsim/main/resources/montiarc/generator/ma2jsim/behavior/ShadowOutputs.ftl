<#-- (c) https://github.com/MontiCore/monticore -->
<#-- @ftlvariable name="helper" type="montiarc.generator.util.Helper" -->
<#-- AST IGNORED -->
${tc.signature("portSymbols")}
<#import "/montiarc/generator/ma2jsim/util/Util.ftl" as Util>
<#list portSymbols as port>
    <#assign type = port.getType()/>
    <@Util.getTypeString type/> ${port.getName()} =
    <#if !type.isPrimitive()>null<#elseif type.isIntegralType() || type.isNumericType()>0<#else>false</#if>;
</#list>
