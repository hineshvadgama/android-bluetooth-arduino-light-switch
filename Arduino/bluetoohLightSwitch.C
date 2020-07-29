char data;

// Set corresponding LED pin
int LED = 13;

void setup() {
  // put your setup code here, to run once:
  Serial.begin(9600);
  pinMode(LED, OUTPUT);
  digitalWrite(LED, LOW);
}

void loop() {
  // put your main code here, to run repeatedly:
  if (Serial.available()) {
    data = Serial.read();
    if (data == 'y') {
      digitalWrite(LED, HIGH);
    } else if (data == 'n') {
      digitalWrite(LED, LOW);
    }
  }
}
