<#-- (c) https://github.com/MontiCore/monticore -->
<#-- ASTComponentType ast -->
${tc.signature("className")}
<#import "/montiarc/generator/ma2jsim/util/Util.ftl" as Util>
<#list ast.getHead().getArcParameterList() as param>
    <#assign fieldName>${prefixes.parameter()}${param.getName()}</#assign>
    protected <@Util.getTypeString param.getSymbol().getType()/> ${fieldName} =
    <#if param.isPresentDefault()>
        ${prettyPrinter.prettyprint(param.getDefault())}
    <#else>
        <#if !param.getSymbol().getType().isPrimitive()>
            null
        <#elseif param.getSymbol().getType().isIntegralType() || param.getSymbol().getType().isNumericType()>
            0
        <#else>
            false
        </#if> // implicit default value added
    </#if>;

    public ${className} ${prefixes.setterMethod()}${fieldName}(<@Util.getTypeString param.getSymbol().getType()/> ${fieldName}) {
      this.${fieldName} = ${fieldName};
      return this;
    }

    public <@Util.getTypeString param.getSymbol().getType()/> ${prefixes.getterMethod()}${fieldName}() { return this.${fieldName}; }
</#list>