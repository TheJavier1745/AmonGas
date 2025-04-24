# 📲 AmonGas

Sistema de monitoreo de gas en tiempo real con ESP32 + Android + Firebase.

---

## 🚀 Instalación y Configuración

### 📦 Requisitos Previos
- Android Studio Giraffe o superior
- Dispositivo Android físico o emulador (API 24+)
- Cuenta en Firebase con proyecto creado
- Configuración de Firebase Authentication (Email/Password)
- ESP32 correctamente conectado y subiendo datos a Firebase

### 🔧 Pasos de Instalación

#### 1. Clonar el Repositorio
```bash
git clone https://github.com/usuario/amongas.git
```

#### 2. Abrir en Android Studio
- `File > Open` y seleccionar el proyecto clonado
- Esperar sincronización de Gradle

#### 3. Configurar Firebase
- Crea un proyecto en [Firebase Console](https://console.firebase.google.com)
- Agrega la app Android con package: `com.amongas.amongas`
- Coloca `google-services.json` en `app/`
- Habilita:
  - Firestore Database
  - Firebase Authentication

#### 4. Dependencias necesarias
```kotlin
implementation 'com.google.firebase:firebase-auth-ktx'
implementation 'com.google.firebase:firebase-firestore-ktx'
implementation 'com.github.PhilJay:MPAndroidChart:v3.1.0'
implementation 'com.jakewharton.threetenabp:threetenabp:1.4.4'
```

#### 5. Ejecutar
- Inicia emulador o conecta dispositivo físico
- Ejecuta desde Android Studio con ▶️ o `Run > Run 'app'`

### ✅ Verificaciones
- Login habilitado (Firebase Auth)
- Datos se leen desde Firestore
- Gráfico se actualiza cada 5s
- Configuración local funciona (umbral, alertas)
- Historial filtrable por fecha

---
