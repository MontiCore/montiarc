<#-- (c) https://github.com/MontiCore/monticore -->
<#-- ASTComponentType ast -->
${tc.signature("portSymbols", "pollBuffer")}
<#import "/montiarc/generator/ma2jsim/util/Util.ftl" as Util>
<#list portSymbols as port>
  final <@Util.getTypeString port.getType()/> ${port.getName()} = <@unboxChar port/>
  (context.${prefixes.port()}${port.getName()}${helper.portVariantSuffix(ast, port)}().isTickBlocked() ? ${helper.getNullLikeValue(port.getType())} :
    context.${prefixes.port()}${port.getName()}${helper.portVariantSuffix(ast, port)}().<#if pollBuffer>poll<#else>peek</#if>Buffer().getData()<@unboxNumbers port/>);
</#list>

<#macro unboxChar port>
  <#if helper.isUnboxedChar(port.getType())>(char)</#if><#t>
</#macro>

<#macro unboxNumbers port>
  <#if helper.isUnboxedChar(port.getType())>.intValue()
  <#elseif helper.isUnboxedByte(port.getType())>.byteValue()
  <#elseif helper.isUnboxedShort(port.getType())>.shortValue()
  <#elseif helper.isUnboxedInt(port.getType())>.intValue()
  <#elseif helper.isUnboxedLong(port.getType())>.longValue()
  <#elseif helper.isUnboxedFloat(port.getType())>.floatValue()
  <#elseif helper.isUnboxedDouble(port.getType())>.doubleValue()</#if><#t>
</#macro>