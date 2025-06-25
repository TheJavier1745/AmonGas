### Manual de Instalación y Ejecución – Amongas

Amongas es un sistema de monitoreo de gas portátil basado en ESP32 y una aplicación Android, conectado a Firebase para registrar y visualizar los niveles de gas.

### Requisitos

#### Hardware

* Placa **ESP32 DevKit**
* Sensor **MQ-6**
* Pantalla LCD **I2C 16x2**
* Módulo buzzer pasivo
* Cableado básico

#### Software

* **Arduino IDE** (v2.0+ recomendado)
* **Plataforma ESP32 para Arduino**
* **Firebase Realtime Database** (proyecto configurado)
* **Android Studio**
* **Dispositivo Android** con Android 8.0 o superior

---

### Configuración del ESP32

1. **Instalar librerías necesarias** en el Arduino IDE:

```
LiquidCrystal_I2C
MQUnifiedsensor
BluetoothSerial
Preferences
FirebaseESP32
FirebaseJson
```

2. **Configurar el archivo del ESP32:**

   * Abre el archivo `.ino` del ESP32 (por ejemplo: `amongas.ino`)
   * Asegúrate de tener definidas las siguientes constantes:

```cpp
#define API_KEY "TU_API_KEY_FIREBASE"
#define DATABASE_URL "https://TU-URL.firebaseio.com"
```

3. **Sube el código** al ESP32 mediante USB.

---

### Instalación de la App Android

1. Clona este repositorio:

```bash
git clone https://github.com/TheJavier1745/amongas.git
```

2. Abre el proyecto en **Android Studio**.

3. Revisa los siguientes archivos para personalizar las claves:

   * `google-services.json` (debes descargarlo desde Firebase Console)
   * `AndroidManifest.xml` (asegúrate de tener permisos de Bluetooth e Internet)

4. Compila y ejecuta la app en un dispositivo Android (emulador no recomendado).

---

### Vinculación de Dispositivo

* La primera vez que se abre la app, si no hay WiFi guardada, el dispositivo entra en modo **Bluetooth** (`ConfiguraGas`)
* Desde la app, presiona **"Vincular dispositivo"** y envía los datos de red WiFi.
* El ESP32 se reiniciará y conectará automáticamente.

---

### Firebase Setup

1. Crea un nuevo proyecto en [Firebase Console](https://console.firebase.google.com/)
2. Habilita **Realtime Database** y copia la URL.
3. En la pestaña de autenticación, habilita **Anonymous sign-in** (opcional).
4. Copia tu `API Key` y la `Database URL` en el código del ESP32.

---

### Variables de Entorno (si aplica)

Si usas entorno de producción/desarrollo:

```env
# .env (solo si se maneja desde Android gradle)
API_KEY=xxx
FIREBASE_URL=xxx
```

> ¡OJO! Este proyecto **no requiere `npm install` ni `docker`**.

---

### Verificación

Al ejecutar todo correctamente:

* El ESP32 mostrará el estado de conexión y valores en LCD.
* La app mostrará el nivel de gas, gráficos históricos y alerta si el gas sobrepasa el umbral definido.
