#include <WiFi.h>
#include <BluetoothSerial.h>
#include <Preferences.h>
#include <LiquidCrystal_I2C.h>
#include <MQUnifiedsensor.h>
#include <FirebaseESP32.h>

// === FIREBASE CONFIG ===
#define API_KEY "AIzaSyAd36_GVGwc177UwshaWIC1HLVhiO1Jen4"
#define DATABASE_URL "https://amongas-e74c6-default-rtdb.firebaseio.com"

FirebaseData fbdo;
FirebaseAuth auth;
FirebaseConfig config;

// === LCD + SENSOR CONFIG ===
LiquidCrystal_I2C lcd(0x27, 16, 2);
#define MQ_PIN 34
#define Board "ESP32"
#define Voltage_Resolution 3.3
#define ADC_Bit_Resolution 12
MQUnifiedsensor MQ6(Board, Voltage_Resolution, ADC_Bit_Resolution, MQ_PIN, "MQ-6");

BluetoothSerial SerialBT;
Preferences preferences;

String ssid = "";
String password = "";
bool modoConfiguracion = false;
#define buzzerPin 25

void conectarAWiFi() {
  WiFi.begin(ssid.c_str(), password.c_str());
  lcd.clear(); lcd.setCursor(0, 0); lcd.print("Conectando WiFi");
  Serial.print("Conectando a: "); Serial.println(ssid);

  for (int i = 0; i < 20 && WiFi.status() != WL_CONNECTED; i++) {
    delay(1000); Serial.print(".");
  }

  if (WiFi.status() == WL_CONNECTED) {
    Serial.println("\n✅ Conectado");
    lcd.setCursor(0, 1); lcd.print("WiFi OK       ");
    delay(2000);
  } else {
    Serial.println("\n❌ Error WiFi");
    lcd.setCursor(0, 1); lcd.print("WiFi ERROR    ");
    modoConfiguracion = true;
    delay(2000);
  }
}

String extraerDato(String str, String key) {
  int s = str.indexOf(key) + key.length();
  int e = str.indexOf(';', s);
  return str.substring(s, e == -1 ? str.length() : e);
}

void setup() {
  Serial.begin(115200);
  SerialBT.begin("ConfiguraGas");
  lcd.init(); lcd.backlight();
  pinMode(buzzerPin, OUTPUT);

  lcd.setCursor(0, 0); lcd.print("Iniciando...");

  preferences.begin("wifiCreds", true);
  ssid = preferences.getString("ssid", "");
  password = preferences.getString("pass", "");
  preferences.end();

  if (ssid != "" && password != "") {
    conectarAWiFi();
  } else {
    modoConfiguracion = true;
    lcd.setCursor(0, 1); lcd.print("Esperando BT");
  }

  MQ6.setRegressionMethod(1); MQ6.setA(1000); MQ6.setB(-2.786);
  MQ6.init(); MQ6.setR0(5);

  config.api_key = API_KEY;
  config.database_url = DATABASE_URL;
  config.time_zone = -3; // Chile o ajusta según tu país
  Firebase.begin(&config, &auth);
  Firebase.reconnectWiFi(true);

  if (!Firebase.signUp(&config, &auth, "", "")) {
    Serial.println("❌ Error en signUp:");
    Serial.println(config.signer.signupError.message.c_str());
  } else {
    Serial.println("✅ Firebase SignUp OK");
  }

  lcd.clear(); lcd.print("Sensor listo");
  delay(2000);
}

void loop() {
  // 📡 ESCUCHAR SIEMPRE BLUETOOTH
  if (SerialBT.available()) {
    String datos = SerialBT.readStringUntil('\n');
    datos.trim();

    if (datos.indexOf("BORRAR") >= 0 && datos.indexOf("SSID:") >= 0 && datos.indexOf("PASS:") >= 0) {
      ssid = extraerDato(datos, "SSID:");
      password = extraerDato(datos, "PASS:");

      preferences.begin("wifiCreds", false);
      preferences.putString("ssid", ssid);
      preferences.putString("pass", password);
      preferences.end();

      Serial.println("✅ Nueva WiFi guardada tras BORRAR");
      lcd.clear(); lcd.print("Nueva WiFi OK");
      delay(1500);
      ESP.restart();  // Reinicia para conectar a nueva WiFi
    }
  }

  MQ6.update();
  float ppm = MQ6.readSensor();
  if (!isfinite(ppm)) {
    ppm = 9999.9;
  }

  lcd.clear();
  lcd.setCursor(0, 0); lcd.print("Gas: "); lcd.print(ppm, 1); lcd.print("ppm");

  lcd.setCursor(0, 1);
  if (ppm > 300.0) {
    lcd.print("ALTO");
    tone(buzzerPin, 1);
  } else {
    lcd.print("Normal");
    noTone(buzzerPin);
  }

  Serial.print("PPM: "); Serial.println(ppm);

  if (WiFi.status() == WL_CONNECTED) {
    String path = "/lecturas";
    FirebaseJson data;
    data.set("valor", ppm);
    data.set("timestamp/.sv", "timestamp");

    if (Firebase.pushJSON(fbdo, path.c_str(), data)) {
      Serial.println("✅ Enviado a Firebase");
    } else {
      Serial.print("❌ Firebase error: ");
      Serial.println(fbdo.errorReason());
    }
  } else {
    Serial.println("🌐 No conectado a WiFi");
  }

  delay(5000);
}

