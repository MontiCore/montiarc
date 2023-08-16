<#-- (c) https://github.com/MontiCore/monticore -->
<#-- ASTComponentType ast -->
${tc.signature("className")}
<#list ast.getHead().getArcParameterList() as param>
    <#assign fieldName>${prefixes.parameter()}${param.getName()}</#assign>
    protected ${param.getMCType().printType()} ${fieldName} =
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

    public ${className} ${prefixes.setterMethod()}${fieldName}(${param.getMCType().printType()} ${fieldName}) {
      this.${fieldName} = ${fieldName};
      return this;
    }

    public ${param.getMCType().printType()} ${prefixes.getterMethod()}${fieldName}() { return this.${fieldName}; }
</#list>