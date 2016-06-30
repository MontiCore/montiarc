package a;

import java.lang.String;
import java.lang.Boolean;
import java.lang.Object;
import java.lang.Throwable;

component E1 {

  port 
    in String,
    out String,
    in Boolean named,
    out Boolean named,
    in Object,
    in Throwable,
    out Throwable throwable;
}