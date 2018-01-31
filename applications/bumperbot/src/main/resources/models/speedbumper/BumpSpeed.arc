package speedbumper;

import speedbumper.Datatypes.SpeedCmd;

component BumpSpeed(Integer defaultSpeed) {
  port
    in SpeedCmd cmd,
    in Integer distance,
    out Integer speed;

  Integer count255;

  automaton BumpSpeed {
    
    state Static, Dynamic;
    initial Dynamic / {count255=0};

	Dynamic -> Static {cmd == SpeedCmd.Static} / {speed = defaultSpeed, count255=0};
	
	Static -> Dynamic [count255 < 10]{cmd == SpeedCmd.Dynamic} / {speed = defaultSpeed, count255=count255+1};
	Static -> Dynamic [count255 >= 10]{cmd == SpeedCmd.Dynamic} / {speed = defaultSpeed>=(int)(((float)distance)/255.0f*100)?defaultSpeed:(int)(((float)distance)/255.0f*100)};
	
	Dynamic -> Dynamic [count255 < 10]{cmd ==  SpeedCmd.Dynamic} / {speed = defaultSpeed, count255=count255+1};
	Dynamic -> Dynamic [count255 >= 10]{cmd ==  SpeedCmd.Dynamic} / {speed = (Integer)(defaultSpeed>=(int)(((float)distance)/255.0f*100)?defaultSpeed:(int)(((float)distance)/255.0f*100))};	
  }
}