# Mundo Amber: Etapa 2 — Mascota Virtual, Opuestos Educativos y Expresividad Táctil

Plan integral para enriquecer la experiencia de juego de Amber (4 años) incorporando una mascota virtual interactiva para alimentar y mimar, un nuevo mini-juego pedagógico de tamaños y opuestos, y animaciones táctiles expresivas con sensación de volumen tridimensional.

### User Review & Critical Decisions

> [!IMPORTANT]
> **Decisiones confirmadas en la Fase 1:**
> - **Nueva actividad principal:** Mascota virtual interactiva (Amber puede elegir entre un *Unicornio Bebé* o un *Gatito Mimado* para alimentarlo con frutas, bañarlo con burbujas y acariciarlo).
> - **Estilo visual e interacción:** Animaciones expresivas y reactivas con perspectiva 3D simulada (parpadeo de ojos, rotaciones elásticas `graphicsLayer`, saltitos y corazones flotantes al interactuar).
> - **Nueva área pedagógica:** Conceptos de tamaños y opuestos (*Grande vs. Pequeño*, *Alto vs. Bajo*, *Muchos vs. Pocos*) con instrucciones de voz y recompensas de estrellas.
> - **Estructura continua:** Mantener el ritmo continuo de 5 preguntas por ronda con diálogo de "Continuar o Salir".

---

### 1. Overview & Core Concept

- **Qué hace:** Expande el universo de juego de Amber con tres grandes pilares:
  1. **El Rincón de la Mascota:** Una mascota viva y cariñosa que reacciona alegremente al ser alimentada con golosinas saludables, peinada con estrellas y bañada con pompas de jabón.
  2. **Tamaños y Opuestos:** Retos visuales adaptados a niños de 4 años para identificar diferencias de tamaño y cualidades contrarias mediante comparaciones táctiles claras.
  3. **Expresividad Visual Aumentada:** Micro-animaciones táctiles en toda la app que dan vida a los animalitos cuando Amber los toca.
- **Público Objetivo:** Amber (4 años), con controles intuitivos de un solo toque y estímulos positivos inmediatos sin frustración.
- **Valor Clave:** Fomenta la empatía, el cuidado y el razonamiento espacial temprano en un entorno 100% seguro, alegre y libre de anuncios.

---

### 2. User Experience & Visual Design

#### Flujos de Usuario Principales

1. **Flujo de la Mascota Virtual ("Cuida a tu Amiguito"):**
   - Amber ingresa desde el menú principal y elige a su compañero (*Gatito Michi* o *Unicornio Mágico*).
   - En la parte inferior hay 3 herramientas circulares grandes (72 dp):
     - 🍓 **Alimentar:** Arrastra o toca fresas, pastelitos y manzanas hacia la boca de la mascota; la mascota abre la boca con animación expresiva, mastica con sonido divertido ("¡Ñam ñam!") y sonríe.
     - 🫧 **Bañar:** Pasa una esponja que llena la pantalla de pompas de jabón brillantes que Amber puede explotar; al final, el animalito queda limpio y reluciente con destellos dorados.
     - 💖 **Acariciar / Cosquillas:** Al frotar o dar toquecitos en la barriguita, la mascota ríe, da saltitos y salen corazones flotantes.
   - La voz de la app celebra: *"¡Qué bien cuidas a tu unicornio, Amber!"*.

2. **Flujo de Tamaños y Opuestos ("Grande y Pequeño"):**
   - La pantalla muestra dos o tres animalitos con diferencias de tamaño evidentes.
   - La voz pregunta con cariño: *"Amber, ¿cuál es el panda más grande?"* o *"Toca la manzana más pequeña"*.
   - Amber toca la figura: si acierta, la figura crece alegremente con estrellas y la voz exclama *"¡Excelente Amber, ese es el más grande!"*.
   - Se mantiene el formato continuo de 5 preguntas antes de ofrecer continuar o salir.

#### Identidad Visual y Estilo

- **Paleta de Colores:** Mantener los tonos pastel vibrantes (*Rosa Chicle #FF4081*, *Amarillo Sol #FFD600*, *Azul Cielo #00B0FF*, *Verde Menta #00E676*) con degradados suaves en el fondo.
- **Expresividad Tridimensional (Simulación 3D en Compose):**
  - Uso de transformaciones `Modifier.graphicsLayer { rotationY; scaleX; scaleY }` para simular profundidad física cuando Amber toca a los personajes.
  - Efecto *Squash and Stretch* (rebote elástico con resortes `spring(dampingRatio = 0.5f)`).
  - Emisión de partículas flotantes (corazones, brillos, estrellas) que brotan del punto de toque.

---

### 3. Key Product Decisions & Trade-Offs

- **Mascota reactiva sin penalizaciones de abandono:**
  - *Enfoque elegido:* La mascota nunca tiene hambre extrema ni se pone triste si Amber no juega por días; siempre está contenta y lista para recibir cariño.
  - *Por qué:* Para una niña de 4 años, un sistema con penalizaciones genera ansiedad; el juego debe ser siempre un espacio seguro y gratificante.
- **Simulación 3D pura en Jetpack Compose vs. Motor de Juegos externo:**
  - *Enfoque elegido:* Utilizar animaciones nativas de Compose (`Animatable`, `graphicsLayer`, `Canvas` de partículas).
  - *Por qué:* Mantiene la app súper liviana, arranca instantáneamente, consume mínima batería y no añade dependencias pesadas de terceros.
- **Persistencia en SharedPreferences:**
  - Guarda el nivel de felicidad de la mascota, su comida favorita y el récord en el juego de tamaños dentro del perfil de Amber para el panel de padres.

---

### 4. Technical Architecture & Data Strategy

```
┌─────────────────────────────────────────────────────────────┐
│                       Mundo Amber                           │
└──────────────┬───────────────────────────────┬──────────────┘
               │                               │
       ┌───────▼────────┐             ┌────────▼────────┐
       │   Nuevos UI    │             │  Audio & Voz    │
       │   Screens      │             │  (Sound & TTS)  │
       └───────┬────────┘             └────────┬────────┘
               │                               │
   ┌───────────┼───────────────┐               │
   │           │               │               │
┌──▼─────┐ ┌───▼────┐ ┌────────▼──────┐        │
│PetCare │ │Sizes & │ │3D Interactive │        │
│Screen  │ │Opposite│ │Touch Canvas   │        │
└──┬─────┘ └───┬────┘ └────────┬──────┘        │
   │           │               │               │
   └───────────┼───────────────┴───────────────┘
               │
       ┌───────▼────────┐
       │ ProgressRepo   │ ◄─── Guarda progreso de opuestos
       │ & ViewModel    │      y cuidado de mascota
       └────────────────┘
```

#### Entidades de Datos

- **`PetState`**: Tipo de mascota (`CAT`, `UNICORN`), nivel de mimos (0 a 100), comida favorita desbloqueada, accesorios equipados.
- **`OppositeChallenge`**: Objeto/Animal, cualidad evaluada (`GRANDE`, `PEQUENO`, `ALTO`, `BAJO`), opciones comparativas.
- **`ScreenRoute`**: Sumar `PET_CARE` y `SIZES_OPPOSITES` a las rutas de navegación.

#### Mapeo de Componentes Interactivos

- `PetCareScreen.kt`: Área central con el lienzo del animal animado y barra inferior de herramientas (comida, esponja de baño, peine de estrellas).
- `SizesGameScreen.kt`: Cuadrícula limpia de 2 a 3 elementos contrastantes de fácil toque táctil para Amber.
- `AmberTopBar.kt`: Compatible sin cambios, mostrando el botón de repetir voz y el acceso a papás.
