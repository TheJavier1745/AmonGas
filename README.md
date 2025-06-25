# Manual de Instalación y Ejecución – Amongas

**Amongas** es un sistema de monitoreo de gas portátil basado en ESP32 y una aplicación Android, conectado a Firebase para registrar y visualizar los niveles de gas.

---

## Requisitos

### Hardware

- ESP32 DevKit v1  
- Sensor de gases MQ-6  
- Pantalla LCD 16x2 con interfaz I2C  
- Zumbador pasivo  
- Protoboard y cables Dupont  

### Software

- Arduino IDE (versión 2.0 o superior)  
- Plataforma ESP32 instalada en el IDE  
- Android Studio (versión estable)  
- Cuenta en Firebase Console  
- Dispositivo Android físico con Android 8.0 (API 26) o superior  

---

## Instalación del Firmware en el ESP32

1. Abre **Arduino IDE** y selecciona la placa `ESP32 Dev Module` desde el menú **Herramientas**.

2. Instala las siguientes bibliotecas desde el Gestor de Librerías:

```
LiquidCrystal_I2C  
MQUnifiedsensor  
FirebaseESP32  
BluetoothSerial  
Preferences  
FirebaseJson
```

3. Clona o abre el archivo `amongas.ino` y configura las siguientes constantes al inicio del archivo:

```cpp
#define API_KEY "TU_API_KEY_FIREBASE"
#define DATABASE_URL "https://TU_PROYECTO.firebaseio.com"
```

4. Conecta el ESP32 vía USB y sube el código desde Arduino IDE.

5. Abre el Monitor Serial para verificar que el dispositivo arranque correctamente y espere conexión WiFi o datos por Bluetooth.

---

## Instalación de la Aplicación Android

1. Clona este repositorio:

```bash
git clone https://github.com/usuario/amongas.git
```

2. Abre el proyecto en **Android Studio**.

3. Importa el archivo `google-services.json` dentro de la carpeta `/app`.

4. Verifica que en `AndroidManifest.xml` estén los siguientes permisos:

```xml
<uses-permission android:name="android.permission.INTERNET"/>
<uses-permission android:name="android.permission.BLUETOOTH"/>
<uses-permission android:name="android.permission.BLUETOOTH_CONNECT"/>
<uses-permission android:name="android.permission.POST_NOTIFICATIONS"/>
```

5. Compila y ejecuta la app en un **dispositivo Android físico** (el emulador no es recomendable por limitaciones de Bluetooth).

---

## Vinculación del Dispositivo

- Al iniciar la app, si no hay red WiFi configurada, se solicitará vincular el dispositivo vía Bluetooth.  
- El dispositivo ESP32 aparecerá como **"ConfiguraGas"**.  
- Ingresa el SSID y contraseña de tu red WiFi desde la app.  
- El ESP32 se reiniciará y se conectará automáticamente a Internet.

---

## Configuración del Entorno Firebase

1. Accede a [Firebase Console](https://console.firebase.google.com/) y crea un nuevo proyecto.

2. Activa **Realtime Database** y copia la URL del proyecto.

3. En la sección **Autenticación**, habilita el método **Inicio de sesión anónimo**.

4. Copia la **API Key** del proyecto y reemplázala en el archivo `.ino` del ESP32.

---

## Variables de Entorno (opcional)

Si trabajas con múltiples entornos, puedes usar variables de entorno para mantener tus claves seguras:

```env
API_KEY=xxx
FIREBASE_URL=xxx
```

---

## Consideraciones Finales

- Este sistema **no utiliza Docker ni npm**.  
- Está diseñado para funcionar con hardware embebido (ESP32) y aplicaciones móviles nativas (Android Studio).  
- Para proyectos más complejos, se recomienda almacenar las credenciales en archivos separados o como variables de entorno.  

---

## Verificación

El ESP32 debe mostrar el estado de conexión y valores del sensor en la pantalla LCD.  
La app debe mostrar el nivel de gas en tiempo real y alertar si el nivel supera el umbral configurado.  
