#include <BleKeyboard.h>

#define LED 2

BleKeyboard bleKeyboard;

const int pinA  = 18;
const int pinB  = 19;
const int pinSW = 21;

int encoderPosCount = 0;

int blinkTime = 5;            // seconds
int LEDState = HIGH;
unsigned long lastBlinkTime = 0;

void setup() {
  Serial.begin(115200);
  pinMode(pinA, INPUT_PULLUP);
  pinMode(pinB, INPUT_PULLUP);
  pinMode(pinSW, INPUT_PULLUP);
  pinMode(LED, OUTPUT);
  digitalWrite(LED, HIGH);

  Serial.println("Starting BLE work!");
  bleKeyboard.begin();
}

void loop() {
  if (bleKeyboard.isConnected()) {
    digitalWrite(LED, HIGH);

    int dir = readEncoder();   // -1, 0, or +1 per detent
    if (dir != 0) {
      encoderPosCount += dir;
      Serial.print(dir > 0 ? "rotated clockwise" : "rotated counterclockwise");
      Serial.print(" | Position: ");
      Serial.println(encoderPosCount);
      bleKeyboard.write(' '); 
      // Example: bleKeyboard.write(dir > 0 ? KEY_MEDIA_VOLUME_UP : KEY_MEDIA_VOLUME_DOWN);
    }

    if (switchPressed()) {
      Serial.println("Switch pressed");
    }
  } else {
    // blink LED while not connected
    if (millis() - lastBlinkTime > (unsigned long)blinkTime * 1000) {
      lastBlinkTime = millis();
      LEDState = !LEDState;
      digitalWrite(LED, LEDState);
    }
  }
}

// Quadrature decoder using a state table (handles bounce well)
int readEncoder() {
  static uint8_t state = 0;
  static int8_t accum = 0;
  static const int8_t table[16] = {0,-1,1,0, 1,0,0,-1, -1,0,0,1, 0,1,-1,0};

  state = ((state << 2) | (digitalRead(pinA) << 1) | digitalRead(pinB)) & 0x0F;
  accum += table[state];

  if (accum >= 4)  { accum = 0; return 1; }   // one full detent
  if (accum <= -4) { accum = 0; return -1; }
  return 0;
}

// Non-blocking, returns true once per press
bool switchPressed() {
  static bool lastState = HIGH;
  static unsigned long lastChange = 0;

  bool now = digitalRead(pinSW);
  bool pressed = false;

  if (now != lastState && millis() - lastChange > 20) {  // 20 ms debounce
    lastChange = millis();
    if (now == LOW) pressed = true;   // LOW = pressed with INPUT_PULLUP
    lastState = now;
  }
  return pressed;
}