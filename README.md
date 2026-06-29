# LAB 9 - APIs REST en Jetpack Compose: Consumo de Datos

App Android construida con Jetpack Compose que consume la API REST pública
[FakeStoreAPI](https://fakestoreapi.com/) y muestra el catálogo real de productos
con sus imágenes oficiales.

## Objetivos cumplidos

a. Integración de **Retrofit** en un proyecto Compose existente.
b. Consumo de datos desde una API REST pública (`https://fakestoreapi.com/products`).
c. Arquitectura **MVVM** con `Repository` para desacoplar la lógica de red.
d. Gestión explícita de los estados **Loading / Success / Error** con `sealed interface`.
e. Renderizado dinámico de datos en Compose, incluyendo **imágenes reales** del producto
   cargadas con Coil.

## Arquitectura

```
Compose UI  ──▶  ViewModel (StateFlow)  ──▶  Repository  ──▶  Retrofit (ProductApi)  ──▶  FakeStoreAPI
```

- `domain/Product.kt` — modelo de dominio con `@SerializedName`.
- `data/remote/ProductApi.kt` — interfaz Retrofit con `@GET`.
- `data/remote/NetworkModule.kt` — Hilt + OkHttp + Gson.
- `data/repository/ProductRepository.kt` — capa de acceso a datos.
- `ui/viewmodel/ProductsViewModel.kt` — expone `StateFlow<ProductsUiState>`.
- `ui/state/UiState.kt` — `Loading | Success | Error`.
- `ui/screens/ProductsScreen.kt` — lista, filtro por categoría, navegación.
- `ui/screens/ProductDetailScreen.kt` — detalle con imagen, precio y descripción.

## Librerías clave

| Librería | Uso |
|----------|-----|
| Retrofit 2.11 + Gson | Cliente REST y deserialización JSON |
| OkHttp Logging | Trazas de red en debug |
| Kotlin Coroutines | Operaciones asíncronas |
| Hilt | Inyección de dependencias |
| Coil | Carga de imágenes reales del producto |
| Jetpack Compose + Material 3 | UI declarativa |
| Navigation Compose | Navegación entre pantallas |

## Cómo ejecutar

1. Abrir el proyecto en Android Studio.
2. Sync Gradle.
3. Ejecutar en un emulador o dispositivo con acceso a internet.
