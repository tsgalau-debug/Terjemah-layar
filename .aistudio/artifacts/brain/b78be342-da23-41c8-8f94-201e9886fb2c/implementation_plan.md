# LayarTerjemah - Real-Time Screen Translator (English to Indonesian)

LayarTerjemah is a specialized Android screen translation application featuring a draggable floating overlay button (`Floating Bubble`), full-screen capture translation from English to Indonesian, in-place translated text overlays, expandable translation inspection cards, and a hybrid OCR + Gemini AI engine.

---

## User Review & Critical Decisions

> [!IMPORTANT]
> The following decisions were confirmed through Phase 1 user input and will guide development:

- **Confirmed Decision 1 (Engine Priority)**: Hybrid architecture combining on-device OCR/offline translation heuristics with Gemini 3.5 Flash cloud fallback for high-fidelity contextual translation.
- **Confirmed Decision 2 (Trigger Mode)**: Instant single-tap on the floating overlay button to initiate full-screen capture and translation.
- **Confirmed Decision 3 (Display Mode)**: In-place text overlay positioned directly above detected text regions, accompanied by an expandable detail card for deeper word analysis, audio speech, and copy actions.
- **Simulator & Live Compatibility**: In addition to the Android system-level `SYSTEM_ALERT_WINDOW` floating service and `MediaProjection` APIs, the app includes an interactive built-in Screen Translation Simulation Sandbox. This ensures seamless end-to-end functionality, instant testing, and visual verification directly inside browser and emulator environments.

---

## 1. Overview & Core Concept

- **What It Does**: Allows users to read English games, web pages, comics, chats, and documents in fluent Indonesian by tapping a floating button on their screen. The app captures the screen, recognizes English text, translates it to natural Indonesian in real time, and superimposes the translated text directly onto the screen.
- **Target Audience / Persona**: Indonesian gamers playing untranslated English games, students studying English papers, professionals reading English technical blogs, and casual users reading international news and social media.
- **Key Value**: Zero app-switching friction. Instead of copying text or taking screenshots and switching to an external translator, users get an immediate in-place overlay of Indonesian text with a single tap.

---

## 2. User Experience & Visual Design

### Key User Flows

1. **Dashboard & Service Launch**:
   - Clean, modern Material 3 interface showing floating service status (`Aktif` / `Nonaktif`).
   - One-tap master toggle to start the floating overlay service.
   - Live permission helper for Overlay (`Draw over other apps`) and Screen Capture with friendly status badges.
2. **Floating Overlay Interaction**:
   - Draggable floating bubble with an elegant glow indicator that docks to screen edges.
   - Single tap activates screen capture animation with a subtle scanner line effect.
   - Processing status (`Mendeteksi teks...` -> `Menerjemahkan ke Bahasa Indonesia...`).
3. **In-Place Translation & Detail Card**:
   - Translucent frosted glass badges overlaid directly at text coordinates replacing English with Indonesian.
   - Tapping any badge opens the bottom/floating Detail Sheet with side-by-side English & Indonesian comparison, text-to-speech speaker button, word-by-word vocabulary breakdown, and quick copy button.
4. **Interactive Screen Sandbox (Test Arena)**:
   - Built-in live screen simulator featuring preset English scenarios (Gaming Dialogue, Tech Article, Comic Strip, Social Media Post) plus custom text/image upload.
   - Users can trigger the floating translator directly over the sample screen to preview OCR boxes and in-place Indonesian overlays instantaneously.
5. **History & Saved Vocabulary**:
   - Offline archive of all captured screen translations with search, filter, and export capabilities.

### Visual Identity & Theme

- **Aesthetic Direction**: High-tech yet warm utilitarian design, blending cybernetic precision with accessible readability.
- **Color Palette**:
  - Primary: Emerald Deep / Tech Teal (`#0D9488` / `#14B8A6`) representing clarity and intelligence.
  - Secondary Accent: Electric Amber (`#F59E0B`) for floating triggers and highlighted translation matches.
  - Background: Deep Obsidian Slate (`#0F172A`) for dark mode and Clean Polar White (`#F8FAFC`) for light mode.
  - Overlay Tokens: High-contrast translucent cards (`rgba(15, 23, 42, 0.88)`) with crisp white/teal typography for legible screen reading over any background.
- **Typography & Hierarchy**: Bold geometric headings, clear legible body text with increased line-height for translated Indonesian phrasing.
- **Micro-interactions & Motion**: Smooth spring physics for floating button dragging and snapping, pulse effects during screen scanning, and staggered fade-ins for text overlays.

---

## 3. Key Product Decisions & Trade-Offs

- **Decision 1: Hybrid Translation Engine (On-Device + Gemini 3.5 Flash)**
  - *Chosen Approach*: Instant local dictionary and pattern matching for common UI elements, gaming verbs, and phrases; seamless background dispatch to Gemini 3.5 Flash for complete sentence syntax, idioms, and contextual nuance.
  - *Why*: Delivers sub-second response times while maintaining grammatical accuracy in Bahasa Indonesia.
  - *Alternatives Considered*: Pure cloud-only was rejected due to network latency on casual taps; pure offline-only was rejected due to robotic phrasebook limitations.

- **Decision 2: Dual Execution Model (System Floating Service + In-App Sandbox)**
  - *Chosen Approach*: Complete Android `Service` implementation with `WindowManager.addView` for Android devices, paired with an In-App Interactive Screen Translation Playground.
  - *Why*: Android browser emulator environments often have restricted display projection or overlay permission prompts; having the built-in sandbox guarantees 100% testable, interactive evaluation of all translation and overlay features.

- **Decision 3: Local Data Storage with Room**
  - *Chosen Approach*: Room Database for persistent translation history, custom glossary entries, and user preferences.
  - *Why*: Translation logs are personal and local-first, needing zero login friction while ensuring instant offline access.

---

## 4. Technical Architecture & Data Strategy

```
┌─────────────────────────────────────────────────────────────────┐
│                       LayarTerjemah UI                          │
│                                                                 │
│  ┌───────────────────────┐           ┌───────────────────────┐  │
│  │   Home & Dashboard    │           │ Translation Sandbox   │  │
│  │ • Service Controller  │           │ • English Game UI     │  │
│  │ • Permissions Status  │           │ • Tech Article UI     │  │
│  │ • Quick Settings      │           │ • Social Chat UI      │  │
│  └───────────┬───────────┘           └───────────┬───────────┘  │
│              │                                   │              │
│  ┌───────────▼───────────────────────────────────▼───────────┐  │
│  │           Floating Overlay & In-Place Display             │  │
│  │  • Floating Draggable Bubble (Single-tap trigger)         │  │
│  │  • Screen Scan Animation & Detection Coordinate Boxes     │  │
│  │  • In-Place Indonesian Overlay Badges                     │  │
│  │  • Expandable Detail Card (Side-by-side, TTS, Copy)       │  │
│  └───────────────────────────────┬───────────────────────────┘  │
└──────────────────────────────────┼──────────────────────────────┘
                                   │
┌──────────────────────────────────▼──────────────────────────────┐
│                    Translation & OCR Engine                     │
│                                                                 │
│  ┌─────────────────────────────┐   ┌─────────────────────────┐  │
│  │      On-Device OCR &        │   │    Gemini 3.5 Flash     │  │
│  │     Instant Lexicon         │   │       Cloud API         │  │
│  │ • Text block segmentation   │   │ • Contextual Indonesian │  │
│  │ • Instant gaming/tech vocab │   │ • Idiom & slang nuance  │  │
│  └──────────────┬──────────────┘   └────────────┬────────────┘  │
│                 └──────────────┬────────────────┘               │
│                                ▼                                │
│                  Translation Repository & Cache                 │
└──────────────────────────────────┬──────────────────────────────┘
                                   │
┌──────────────────────────────────▼──────────────────────────────┐
│                   Room Database & Preferences                   │
│  • Translation History (Original EN, Translated ID, Timestamp)  │
│  • Saved Starred Translations & Custom Glossary                 │
│  • Settings (Overlay Opacity, Speech Pitch, Engine Mode)        │
└─────────────────────────────────────────────────────────────────┘
```

### Data Model & State

- **`TranslationBlock`**: `id`, `originalText`, `translatedText`, `bounds` (x, y, width, height), `confidence`, `detectedLanguage`.
- **`TranslationSession`**: `sessionId`, `timestamp`, `sourceContext`, `blocks: List<TranslationBlock>`, `isSaved`.
- **`OverlayUiState`**: `isServiceRunning`, `isScanning`, `blocks`, `selectedBlock`, `overlayAlpha`, `engineMode`.
- **`HistoryEntity`**: Room entity storing past translations with search indexing.

### Interactive Component & State Mapping

- **Single Tap on Bubble**: Dispatches snapshot capture -> invokes OCR parser -> dispatches translation -> yields bounding box list -> renders animated in-place badges.
- **Tap on Overlay Badge**: Highlights selected region -> expands detail bottom card with TTS audio synthesis (`TextToSpeech.speak()`), copy button with feedback toast, and dictionary definition.
- **Engine Mode Selector**: Lets users toggle between `Otomatis (Hybrid)`, `Hanya Offline (Instan)`, and `Gemini AI (Akurasi Tinggi)`.

---

## 5. Implementation Steps

1. **Project Setup & Dependencies**:
   - Update `app/build.gradle.kts` and Version Catalog for Kotlin serialization, Room, and Material Icons.
   - Configure `.env.example` with `GEMINI_API_KEY` placeholder.
   - Set app name to `LayarTerjemah` in `metadata.json` and `strings.xml`.
2. **Translation & OCR Engine**:
   - Implement `GeminiApiService` and REST client for `gemini-3.5-flash`.
   - Build offline English-to-Indonesian dictionary & heuristic translator engine with gaming, UI, and tech domain terms.
   - Create unified `ScreenTranslatorRepository` managing hybrid fallbacks and caching.
3. **Database Layer**:
   - Set up Room Database (`TranslationDatabase`), DAO, and entities for history and favorites.
4. **Floating Overlay & System Service**:
   - Implement `FloatingOverlayService` with window manager touch listener, draggable physics, and overlay layout.
   - Add permission check helper (`Settings.canDrawOverlays`).
5. **Interactive Compose UI**:
   - **Dashboard**: Engine toggle, overlay switch, permissions card, stats.
   - **Screen Translation Sandbox**: Interactive simulated screens allowing instant one-tap full-screen translation testing with in-place overlays and detail inspector.
   - **History & Vocabulary Screen**: Filterable list of past screen translations with export and favorite toggles.
   - **Settings Screen**: Custom overlay styling, opacity slider, TTS voice settings, and API status.
6. **Verification & Regression Testing**:
   - Verify with `compile_applet` and execute local unit tests.
