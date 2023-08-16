<#-- (c) https://github.com/MontiCore/monticore -->
<#-- ASTComponentType ast -->
${tc.signature("className")}
<#list helper.getFeatures(ast) as feature>
    <#assign fieldName>${prefixes.feature()}${feature.getName()}</#assign>
    protected Boolean ${fieldName} = false;

    public ${className} ${prefixes.setterMethod()}${fieldName}(Boolean ${fieldName}) {
    this.${fieldName} = ${fieldName};
    return this;
    }

    public Boolean ${prefixes.getterMethod()}${fieldName}() { return this.${fieldName}; }
</#list>