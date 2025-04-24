# AmonGas

Este proyecto permite monitorear niveles de gas (LPG) en tiempo real usando un sensor MQ-6 conectado a un ESP32, mostrando los datos en un LCD I2C y enviándolos automáticamente a Firebase Realtime Database.

---

## Instalación y Configuración

### Requisitos Previos
Hardware
- ESP32
- Sensor MQ-6
- Pantalla LCD I2C 16x2
- Buzzer (opcional)
- Cables y breadboard
Software
- Arduino IDE
- Librerías:
    + MQUnifiedsensor
    + LiquidCrystal_I2C
    + WiFi
    + HTTPClient
Cuenta en Firebase con un Realtime Database
### Pasos de Instalación

#### 1. Clonar el Repositorio
En la consola del sistema, utilizar los siguientes comandos, la carpeta se puede llamar de cualquier manera: 
```bash
mkdir nombreProyecto
```
Dirigirse a la carpeta recién creada
```bash
cd nombreProyecto
```
Clonar el repositorio
```bash
git clone https://github.com/TheJavier1745/amongas.git
```

#### 2. Abrir en Android Studio
- `File > Open` y seleccionar el proyecto clonado
- Esperar sincronización de Gradle

#### 3. Configurar Firebase
- Crea un proyecto en [Firebase Console](https://console.firebase.google.com)
- Agrega la app Android con package: `com.amongas.amongas`
- Colocar `google-services.json` en `app/`
- Habilitar:
  - Firestore Database
  - Firebase Authentication

 Ejecutar en la consola de FireBase el siguiente codigo para crear la base de datos:
```Firebase
{
  "dispositivos": {
    "device_001": {
      "nombre": "Sensor Cocina",
      "uid_propietario": "UID_123",
      "wifi": {
        "password": "mi_contrasena",
        "ssid": "MiRed"
      }
    }
  },
  "lecturas": {
    "device_001": {
      "2025-04-23T19:50:32Z": {
        "timestamp": 1713904232000,
        "valor": 234
      },
      "2025-04-23T19:55:32Z": {
        "timestamp": 1713904532000,
        "valor": 198
      }
    }
  },
  "users": {
    "UID_123": {
      "dispositivos": {
        "device_001": true
      },
      "email": "usuario@example.com",
      "nombre": "Usuario Ejemplo"
    }
  }
}
```
 
#### 4. Implementar el codigo de AmonGas.ino en el ESP32 y modificar con tus datos
Edita las siguientes líneas en el archivo .ino:
```Arduino IDE
const char* ssid = "TU_SSID";
const char* password = "TU_PASSWORD_WIFI";

const char* firebaseHost = "https://tuproyecto.firebaseio.com/";
const char* firebasePath = "/lecturas.json";
```

Carga el código al ESP32 desde Arduino IDE.

#### 5. Dependencias necesarias
Revisa el archivo "build.gradle.kts" del modulo :app en Android estudio, si no están las siguientes dependencias, agregalas.
```kotlin
implementation 'com.google.firebase:firebase-auth-ktx'
implementation 'com.google.firebase:firebase-firestore-ktx'
implementation 'com.github.PhilJay:MPAndroidChart:v3.1.0'
implementation 'com.jakewharton.threetenabp:threetenabp:1.4.4'
```
Para utilizar dependencias de github, es posible que necesites modificar el archivo "setting.gradle.kts" y verificar que esté de este modo
```kotlin
dependencyResolutionManagement {
    repositoriesMode.set(RepositoriesMode.FAIL_ON_PROJECT_REPOS)
    repositories {
        google()
        mavenCentral()
        maven(url = "https://jitpack.io")
    }
}
```

#### 6. Ejecutar
- Inicia emulador o conecta dispositivo físico
- Ejecuta desde el botón de Android Studio, o con `Run > Run 'app'`

### Verificaciones
- Login habilitado (Firebase Auth)
- Datos se leen desde Firestore
- Gráfico se actualiza cada 5s
- Configuración local funciona (umbral, alertas)
- Historial filtrable por fecha

### Visualización de los datos
- Los datos se envían cada 5 segundos
- Se almacenan en el nodo /lecturas
- Cada lectura contiene:
```json
  {
    "valor": 245.33,
    "timestamp": 1713904232000
  }
```

Puedes ver las lecturas directamente desde la consola de Firebase en tiempo real.
---
