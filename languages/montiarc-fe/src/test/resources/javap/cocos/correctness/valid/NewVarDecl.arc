package javap.cocos.correctness.valid;

component NewVarDecl {
  port
    in double distance,
    out String hulu;

  compute HelloWorld {
    int s = "hello world";
  }

}