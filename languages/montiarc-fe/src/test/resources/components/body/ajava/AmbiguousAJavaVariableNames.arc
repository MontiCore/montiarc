package components.body.ajava;


/*
 * Invalid model.
 * There are multiple variables with the same name declared in the same scope.
 *
 * @implements AJava CoCo
 */
component AmbiguousAJavaVariableNames {

  compute ComputeBlock{
    int x;
    int x;
  }

}