#include <BleKeyboard.h>

BleKeyboard bleKeyboard;

#define LED 2
#define PUSHBUTTON 4

#define ENCODER_A 22
#define ENCODER_B 21
#define ENCODER_BUTTON 23

// Volatile variables used within the Interrupt Service Routine (ISR)
volatile int encoderCounter = 0;
volatile bool lastEncoderAState = HIGH;

// Variables for switch debouncing
unsigned long lastButtonPress = 0;
const unsigned long DEBOUNCE_DELAY = 50; // Milliseconds

void IRAM_ATTR readEncoder() {
  // Read the current state of CLK
  bool currentClkState = digitalRead(ENCODER_A);

  // If CLK has changed state, a pulse happened
  if (currentClkState != lastEncoderAState) {
    // If the DT state is different than the CLK state, the encoder is rotating clockwise
    if (digitalRead(ENCODER_B) != currentClkState) {
      encoderCounter++;
    } else {
      encoderCounter--;
    }
    // Update the last state of CLK
    lastEncoderAState = currentClkState;
  }
}


void setup() {
    Serial.begin(115200);
    pinMode(LED, OUTPUT);
    pinMode(PUSHBUTTON, INPUT);

    pinMode(ENCODER_A, INPUT_PULLUP);
    pinMode(ENCODER_B, INPUT_PULLUP);
    pinMode(ENCODER_BUTTON, INPUT_PULLUP);

    lastEncoderAState = digitalRead(ENCODER_A);

    attachInterrupt(digitalPinToInterrupt(ENCODER_A), readEncoder, CHANGE);
    attachInterrupt(digitalPinToInterrupt(ENCODER_B), readEncoder, CHANGE);

    Serial.println("Starting BLE work!");
    bleKeyboard.begin();
}

void loop() {
  if(bleKeyboard.isConnected()) {
    // checkForPushButtonPress();

    digitalWrite(LED, HIGH);

    static int lastEncoderAState = 0;
    if(encoderCounter != lastEncoderAState){
      lastEncoderAState = encoderCounter;
      Serial.print("Position: ");
      Serial.println(encoderCounter);
    }

    if(digitalRead(ENCODER_BUTTON) == LOW){
      if(millis() - lastButtonPress > DEBOUNCE_DELAY){
        Serial.println("Encoder button pressed!");
        noInterrupts(); // Disable interrupts to prevent encoder reading during button press
        encoderCounter = 0; // Reset the encoder counter
        interrupts(); // Re-enable interrupts
        lastButtonPress = millis();
      }
    }

  }
  else {
    Serial.println("Not connected. Waiting 5 seconds...");
    delay(5000);
  }

}


void checkForPushButtonPress(){
  int previousState = LOW;
  int currentState = digitalRead(PUSHBUTTON);
  if(currentState == HIGH && previousState == LOW) {
    Serial.println("Button pressed!");
    bleKeyboard.write(KEY_RETURN);
  }
  previousState = currentState;
}