#include <SoftwareSerial.h>
SoftwareSerial BTserial (2,3); //RX  / TX
const int AIA = 5; //Motor drive input A-1A
const int AIB = 4; //Motor drive input A-1B
byte speed;

const long baudRate = 9600;
char c= ' ';
boolean NL = false;

void setup() {

  Serial.begin(9600);
  Serial.print("Sketch: ");  Serial.println("Bluetooth_Test_0");
  Serial.print("Uploaded: ");  Serial.println("October 10");
  Serial.println(" ");

  BTserial.begin(baudRate);
  Serial.print("BTSerail started at "); Serial.println(baudRate);
  Serial.println(" ");

  pinMode(AIA, OUTPUT);
  pinMode(AIB, OUTPUT);
}

void loop() {
  // Read from the Bluetooth module and send to the Arduino Serial
  if(BTserial.available())
  {
    c = BTserial.read();
    Serial.print("Direction: "); Serial.println(c);
    if (c == 'l'){
      left();
    }
    else if(c == 'r'){
      right();
    }
  }

  // Read from the Serial Monitor and send to the Bluetooth module
  if(Serial.available())
  {
    c = Serial.read();
    Serial.print(c);
    BTserial.write(c);
  }
}

// Turn motor shaft clockwise
void right(){
  speed = 255;
  analogWrite(AIA, speed);
  analogWrite(AIB, 0);
  delay(250);
  stop();
}

// Turn motor shaft counter-clockwise
void left(){
  speed = 255;
  analogWrite(AIA, 0);
  analogWrite(AIB, speed);
  delay(250);
  stop();
}

// Stop motor shaft rotation
void stop(){
  speed = 0;
  analogWrite(AIA, 0);
  analogWrite(AIB, speed);
}