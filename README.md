# Universal Screen Mirror para Motorola 📱📺

Prototipo en Kotlin para transmitir la pantalla de un celular Motorola a cualquier Smart TV mediante red local y servidor HTTP.

## 🛠️ Archivos del proyecto
- **`AndroidManifest.xml`**: Permisos de red y captura de pantalla.
- **`DeviceScanner.kt`**: Rastreos SSDP/UPnP de Smart TVs en la red.
- **`ScreenStreamServer.kt`**: Servidor local HTTP para emisión MJPEG.
- **`MainActivity.kt`**: Interfaz principal y permisos de captura.
