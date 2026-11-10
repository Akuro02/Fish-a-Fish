#include <BleKeyboard.h>

#define LED 2

BleKeyboard bleKeyboard;

int pinA = 22; //internal switch A
int pinB = 21;  //internal switch B
int pinSW = 23;  //switch (pressed Encoder)
int encoderPosCount = 0; // starts at zero, change if you want

int positionval;
bool switchval; 
int mrotateLast;  
int mrotate;

int blinkTime = 5; // in seconds
int LEDState = HIGH;
double lastBlinkTime = 0.0f;

void setup() { 
  Serial.begin (115200);
  pinMode(pinA, INPUT_PULLUP);
  pinMode(pinB, INPUT_PULLUP);
  pinMode(pinSW, INPUT_PULLUP);
  pinMode(LED, OUTPUT);
  digitalWrite(LED, HIGH);

  int mrotateLast = digitalRead(pinA);     
  delay(50);

  Serial.println("Starting BLE work!");
  bleKeyboard.begin();
} 


void loop() { 
if(bleKeyboard.isConnected()){
  digitalWrite(LED, LEDState);
  readencoder(); 

  if(readswitch() == 1){
  Serial.println("Switch = 1");
  }
} else{
  // blink built-in LED when there is no bluetooth connection
    // Count each second without interrupting (without delay())
  if((millis() - lastBlinkTime) > (blinkTime * 1000)){
    lastBlinkTime = millis();
    // Change LED state
    LEDState = (digitalRead(LED)==HIGH) ? LOW : HIGH;
    digitalWrite(LED, LEDState);
  }
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