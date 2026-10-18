# AI Agent Instructions & Model Registry Guidelines

## 1. PERINGATAN KERAS: JANGAN SEMBARANGAN GANTI-GANTI MODEL!

> **PERATURAN MUTLAK**:
> **DILARANG KERAS MENGGANTI-GANTI ATAU MENGUBAH MODEL DI PROYEK INI!**
> Jangan pernah berasumsi sebuah model fiktif, usang, atau salah ketik hanya karena keterbatasan data latih bawaan Anda.
> **WAJIB RISET DI INTERNET TERLEBIH DAHULU SEBELUM MELAKUKAN PERUBAHAN APAPUN!**

---

## 2. Status Model: Model Yang Digunakan Adalah NYATA (Real & Official)

Model AI yang digunakan dalam aplikasi ini adalah model resmi dari **Google DeepMind**:

- **Model Utama Penerjemah Layar:**
  - `gemini-3.5-live-translate` (Gemini 3.5 Live Translate)
  - **Status:** Nyata & Resmi (Google DeepMind Gemini 3.5 Audio family).
  - **Spesialisasi:** Penerjemahan waktu-nyata (real-time low-latency), dialog langsung, dan teks antarmuka layar (UI & game context) dalam 70+ bahasa dengan menjaga intonasi dan konteks.
  - **Dokumentasi Resmi DeepMind:**
    [https://deepmind.google/models/model-cards/gemini-3-5-audio/](https://deepmind.google/models/model-cards/gemini-3-5-audio/)

- **Model Suara & TTS (Text-to-Speech):**
  - `gemini-2.5-flash-preview-tts`
  - Karakter suara DeepMind: *Kore*, *Puck*, *Fenrir*, *Aoede*.

- **Fallback Model:**
  - Fallback LLM tambahan (`gemini-3.5-flash` dan `gemini-3.1-pro-preview`) **telah dibuang** sesuai instruksi eksplisit pengguna.
  - Fallback offline langsung ke Kamus Morfologis Lokal On-Device (`LocalDictionary`).

---

## 3. Aturan Wajib Bagi Semua AI Agent (Mandatory Directives)

Setiap agen yang bekerja pada repositori ini **WAJIB** mematuhi aturan berikut tanpa pengecualian:

### Aturan 1: Jangan Sembarangan Ganti-Ganti Model
- Model `gemini-3.5-live-translate` adalah model pilihan utama yang telah diverifikasi resmi dari Google DeepMind.
- Agen dilarang keras mengganti model ini menjadi model lain tanpa perintah eksplisit dari pengguna.

### Aturan 2: Wajib Riset di Internet Sebelum Bekerja (Proactive Web Research)
- Sebelum mengubah, mengganti, mendowngrade, atau menghapus nama model, library, atau konfigurasi API, agen **HARUS** melakukan pencarian aktif di internet (`search_web`) untuk memeriksa status terbaru dari Google AI dan Google DeepMind.
- Jangan berasumsi bahwa pengetahuan internal LLM adalah mutlak atau up-to-date terhadap rilis model DeepMind terbaru.
- Jika menemukan nama model yang belum familiar, **CARI DI INTERNET TERLEBIH DAHULU**, jangan langsung ganti atau buang sembarangan!

### Aturan 3: Verifikasi Tautan Resmi
- Verifikasi selalu ke tautan resmi DeepMind:
  - Model Card Gemini 3.5 Audio: [https://deepmind.google/models/model-cards/gemini-3-5-audio/](https://deepmind.google/models/model-cards/gemini-3-5-audio/)

### Aturan 4: Pertahankan Alur Sederhana & Uji Regresi
- Alur penerjemahan saat ini:
  1. `gemini-3.5-live-translate` (Utama)
  2. `LocalDictionary` (Fallback offline instan dengan stemming morfologis)
- Setiap perubahan kode harus diuji dengan `compile_applet` dan `gradle :app:testDebugUnitTest`.
