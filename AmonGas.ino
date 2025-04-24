#include <Wire.h>
#include <LiquidCrystal_I2C.h>
#include <WiFi.h>
#include <HTTPClient.h>
#include <MQUnifiedsensor.h>

// ==================== CONFIGURACIÓN WiFi ====================
const char* ssid = "Xuxetu";
const char* password = "12341233";

// ==================== CONFIGURACIÓN FIREBASE ====================
const char* firebaseHost = "https://amongas-e74c6-default-rtdb.firebaseio.com/";
const char* firebasePath = "/lecturas.json";

// ==================== CONFIGURACIÓN DEL SENSOR ====================
#define Board "ESP32"
#define Voltage_Resolution 3.3
#define ADC_Bit_Resolution 12
#define pinSensor 34
#define buzzerPin 25

MQUnifiedsensor MQ6(Board, Voltage_Resolution, ADC_Bit_Resolution, pinSensor, "MQ-6");

// ==================== LCD I2C ====================
LiquidCrystal_I2C lcd(0x27, 16, 2); // Cambia a 0x3F si tu I2C Scanner indica otro

void setup() {
  Serial.begin(115200);
  pinMode(buzzerPin, OUTPUT);

  // Iniciar LCD
  lcd.init();
  lcd.backlight();
  lcd.setCursor(0, 0);
  lcd.print("Iniciando...");

  // Conectar al WiFi
  WiFi.begin(ssid, password);
  lcd.setCursor(0, 1);
  lcd.print("WiFi...");
  int intentos = 0;
  while (WiFi.status() != WL_CONNECTED && intentos < 20) {
    delay(1000);
    Serial.print(".");
    intentos++;
  }

  if (WiFi.status() == WL_CONNECTED) {
    Serial.println("\nConectado a WiFi");
    lcd.setCursor(0, 1);
    lcd.print("WiFi OK        ");
  } else {
    Serial.println("\nNo se pudo conectar");
    lcd.setCursor(0, 1);
    lcd.print("WiFi ERROR     ");
  }

  // Inicializar MQ-6
  MQ6.setRegressionMethod(1); // Método exponencial
  MQ6.setA(1000);
  MQ6.setB(-2.786); // Para LPG
  MQ6.init();
  MQ6.setR0(5);  // Valor calibrado manualmente

  Serial.print("R0 fijo usado: ");
  Serial.println(MQ6.getR0());

  lcd.clear();
  lcd.setCursor(0, 0);
  lcd.print("Listo para lectura de gas");
  delay(2000);
}

void loop() {
  MQ6.update();
  float ppm = MQ6.readSensor();

  // Mostrar en consola
  Serial.print("Nivel de gas: ");
  Serial.print(ppm);
  Serial.println(" ppm");

  // Mostrar en LCD
  lcd.clear();
  lcd.setCursor(0, 0);
  lcd.print("Gas: ");
  lcd.print(ppm, 1);
  lcd.print(" ppm");

  lcd.setCursor(0, 1);
  if (ppm > 300.00) {
    lcd.print("ALTO");
    Serial.println("ALERTA: Gas alto");
    tone(buzzerPin, 1); // 1Hz
  } else {
    lcd.print("Normal     ");
    noTone(buzzerPin); // Apaga el buzzer
  }

  // Enviar a Firebase si conectado
  if (WiFi.status() == WL_CONNECTED) {
    HTTPClient http;
    String url = String(firebaseHost) + firebasePath;
    http.begin(url);
    http.addHeader("Content-Type", "application/json");

    // ✅ ENVIAR VALOR + TIMESTAMP DEL SERVIDOR
    String json = "{\"valor\":" + String(ppm, 2) + ",\"timestamp\":{\".sv\":\"timestamp\"}}";

    int httpResponseCode = http.POST(json);
    if (httpResponseCode > 0) {
      Serial.println("Datos enviados a Firebase");
    } else {
      Serial.print("Error al enviar: ");
      Serial.println(httpResponseCode);
    }

    http.end();
  }

  delay(5000); // esperar 5 segundos
}
