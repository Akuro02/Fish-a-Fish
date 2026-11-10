 int pinA = 22; //internal switch A
 int pinB = 21;  //internal switch B
 int pinSW = 23;  //switch (pressed Encoder)
 int encoderPosCount = 0; // starts at zero, change if you want

 int positionval;
 bool switchval; 
 int mrotateLast;  
 int mrotate;

 void setup() { 
   
   int mrotateLast = digitalRead(pinA);     
   Serial.begin (9600);
   delay(50);
 } 

  
 void loop() { 
  readencoder(); 
  
  if(readswitch() == 1){
  Serial.println("Switch = 1");
  }
 } 

 int readencoder (){
     
   mrotate = digitalRead(pinA);
   
   if (mrotate != mrotateLast){ //knob is rotating
     
     
     if (digitalRead(pinB) != mrotate) {  //switch A changed first -> rotating clockwise
       encoderPosCount ++;
       Serial.println ("rotated clockwise");
        }      
     else {// switch B changed first -> rotating counterclockwise
        encoderPosCount--;
        Serial.println ("rotated counterclockwise");
     }

     
     Serial.print("Encoder Position: ");
     Serial.println(encoderPosCount);
     Serial.println("");
     }
   mrotateLast = mrotate;  
   return encoderPosCount;
 }
 
bool readswitch(){

       if(digitalRead(pinSW)!=0){ // switch is pressed
       while(digitalRead(pinSW)!=0){} //switch is currently pressed
       switchval = 1;
       }
       
       else{switchval = 0;} //switch is unpressed
       return switchval;
      
}