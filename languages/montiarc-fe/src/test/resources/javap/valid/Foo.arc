package javap.valid;

component Foo {
  port
    in double distance,
    out String hulu;

  compute increaseHulu {    
    distance++;
    hulu = distance;
  }
}