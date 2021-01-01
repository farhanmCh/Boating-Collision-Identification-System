#include <SoftwareSerial.h>
SoftwareSerial BTserial (2,3); //RX  / TX

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
}

void loop() {
  // Read from the Bluetooth module and send to the Arduino Serial
  if(BTserial.available())
  {
    c = BTserial.read();
    Serial.write(c);
  }

  // Read from the Serial Monitor and send to the Bluetooth module
  if(Serial.available())
  {
    c = Serial.read();
    Serial.print(c);
    BTserial.write(c);

//    // Echo the user input to the main window. The ">" character
//    if(NL) { Serial.print(">"); NL = false; }
//    Serial.write(c);
//    if (c==10) { NL = true; }
  }
}
