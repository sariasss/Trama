# Trama — Tu Cartelera Inteligente

**Trama** es una app nativa para Android (Material 3) que conecta la API de **TMDb** con **Firebase** para ofrecer una cartelera interactiva con funciones de red social cinéfila.

---

| Pantalla      | Descripción Clave |
|:--------------| :--- |
| **Inicio**    | *Feed* dinámico con reseñas de seguidos, películas destacadas y tendencias. |
| **Buscador**  | Filtros por género. Usa **`@`** para cambiar de buscar películas a buscar usuarios. |
| **Detalles**  | Ficha técnica con sinopsis, marcadores rápidos (Favoritos/Vistas) y opiniones. |
| **Perfil**    | Gestión de seguidores, solicitudes y control total (*CRUD*) de tus críticas. |
| **Conexión**| Monitor en segundo plano que pausa la app de forma limpia si te quedas sin internet. |
---

## Arquitectura
* **UI / Arquitectura:** Kotlin + Jetpack Compose (Material 3) + MVVM.
* **Backend / Datos:** Firebase (Auth, One Tap Sign-In y Realtime Database).
* **APIs / Librerías:** Retrofit 2 (TMDb), OkHttp y Coil (Carga de imágenes).

---

## 🔧 Instalación y Configuración

Para clonar y compilar este proyecto localmente, necesitarás seguir estos pasos:

### 1. Clonar el repositorio
```bash
git clone https://github.com/sariasss/Trama.git
cd trama
```

## 2. Estructura de Paquetes Básica
```
com.example.trama/
├── Data/
│   ├── Model/       # Clases de datos (User, Movie, Review...)
├── ViewModel/       # Lógica de negocio (AuthViewModel, MovieViewModel, UserViewModel)
├── Screen/          # Componentes de UI en Jetpack Compose (Inicio, Buscador, Detalle, Perfil...)
├── Components/      # Clases utilitarias y asistentes de hardware (NetworkMonitor)
└── ui/theme/        # Configuración de tipografías y paleta de colores de Material 3
```


