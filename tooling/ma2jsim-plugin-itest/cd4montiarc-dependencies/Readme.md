<!-- (c) https://github.com/MontiCore/monticore -->
## Test cases
The integration tests implemented by this project all have a similar setup.
There are projects `a`, `b`, `c`, and `d`. The dependency graph among them has the form
```d -> c -> b -> a``` where `->` means _dependsOn_.
* `a` and `b` are always cd projects.
* `c` and `d` are tested each in two different variants:
  * Only applying the `montiarc` plugin, and
  * Applying both the `montiarc` and `cd2pojo` plugin
* Note that `d`'s project follows the naming scheme of `end-consumer` 

Each of these possible combinations is tested. Moreover, one can pull _cd_ dependencies using different ways:
* Cd models depend on other cd projects using the _cd2pojo_ configuration. If the own project also applies the montiarc
  plugin, then the cd models will be available there, too.
* MontiArc models depend on other cd2pojo projects using the _cd2pojo4montiarc_ configuration.
* MontiArc models depend on other MontiArc models of another project X. Then the cd dependencies of X's MontiArc models
  will be loaded as transitive dependencies.

Therefore, the test cases in this project also test all different possible configurations to load cd dependencies.
E.g., all the following variants are tested:
* d uses the _montiarc_ config to depend on c
* d uses the _cd2pojo_ config to depend on c
* d uses the _cd2pojo4montiarc_ config to depend on c
