<#-- (c) https://github.com/MontiCore/monticore -->
<#-- @ftlvariable name="ast" type=" arcbasis._ast.ASTArcComponentType" -->
<#-- @ftlvariable name="helper" type="montiarc.generator.util.Helper" -->
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