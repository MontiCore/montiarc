<#-- (c) https://github.com/MontiCore/monticore -->
<#-- ASTComponentType ast -->
<#list helper.getFeatures(ast) as feature>
    protected final Boolean ${prefixes.feature()}${feature.getName()};

    public Boolean ${prefixes.getterMethod()}${prefixes.feature()}${feature.getName()}() {
      return this.${prefixes.feature()}${feature.getName()};
    }
</#list>