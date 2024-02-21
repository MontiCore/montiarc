<#-- (c) https://github.com/MontiCore/monticore -->
<#-- ASTComponentType ast -->
${tc.signature("className")}
<#list helper.getFeatures(ast) as feature>
    <#assign fieldName>${prefixes.feature()}${feature.getName()}</#assign>
    protected boolean ${fieldName} = false;

    public ${className} ${prefixes.setterMethod()}${fieldName}(boolean ${fieldName}) {
        this.${fieldName} = ${fieldName};
        return this;
    }

    public boolean ${prefixes.getterMethod()}${fieldName}() { return this.${fieldName}; }
</#list>