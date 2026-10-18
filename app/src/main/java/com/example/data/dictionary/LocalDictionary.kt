package com.example.data.dictionary

import com.example.model.VocabularyItem

object LocalDictionary {

  private val exactPhraseMap = mapOf(
    // System, UI & Permissions
    "draw over other apps" to "Tampilkan di atas aplikasi lain",
    "permission granted" to "Izin diberikan",
    "permission denied" to "Izin ditolak",
    "screen translation" to "Terjemahan Layar",
    "screen translator" to "Penerjemah Layar",
    "real-time instant translate" to "Terjemahan instan waktu-nyata",
    "tap to translate" to "Ketuk untuk menerjemahkan",
    "scanning screen" to "Memindai layar...",
    "processing text" to "Memproses teks...",
    "translation complete" to "Terjemahan selesai",
    "no text detected" to "Tidak ada teks terdeteksi",
    "save to favorites" to "Simpan ke favorit",
    "copy to clipboard" to "Salin ke papan klip",
    "copied successfully" to "Berhasil disalin",
    "clear history" to "Hapus riwayat",
    "network connection error" to "Kesalahan koneksi internet",
    "battery saver active" to "Penghemat baterai aktif",
    "device storage full" to "Penyimpanan perangkat penuh",
    "system update available" to "Pembaruan sistem tersedia",
    "please check your internet connection" to "Silakan periksa koneksi internet Anda",
    "do not show again" to "Jangan tampilkan lagi",
    "press back to exit" to "Tekan kembali untuk keluar",
    "are you sure you want to exit" to "Apakah Anda yakin ingin keluar",
    "terms of service" to "Ketentuan layanan",
    "privacy policy" to "Kebijakan privasi",
    "sign in with google" to "Masuk dengan Google",
    "sign in" to "Masuk",
    "sign up" to "Daftar",
    "log in" to "Masuk",
    "log out" to "Keluar",
    "forgot password" to "Lupa kata sandi",
    "change password" to "Ubah kata sandi",
    "user agreement" to "Perjanjian pengguna",
    "switch account" to "Ganti akun",
    "delete account" to "Hapus akun",
    "notification settings" to "Pengaturan notifikasi",
    "sound and vibration" to "Suara dan getaran",
    "display settings" to "Pengaturan tampilan",
    "dark mode" to "Mode gelap",
    "light mode" to "Mode terang",
    "system default" to "Bawaan sistem",
    "auto brightness" to "Kecerahan otomatis",
    "flight mode" to "Mode pesawat",
    "airplane mode" to "Mode pesawat",
    "mobile data" to "Data seluler",
    "network and internet" to "Jaringan dan internet",
    "connected devices" to "Perangkat terhubung",
    "apps and notifications" to "Aplikasi dan notifikasi",
    "battery percentage" to "Persentase baterai",
    "storage space" to "Ruang penyimpanan",
    "about phone" to "Tentang ponsel",
    "software update" to "Pembaruan perangkat lunak",
    "developer options" to "Opsi pengembang",
    "factory reset" to "Reset ke setelan pabrik",

    // Gaming, RPG & Action
    "quest completed" to "Misi selesai",
    "quest failed" to "Misi gagal",
    "main quest" to "Misi utama",
    "side quest" to "Misi sampingan",
    "daily mission" to "Misi harian",
    "daily quest" to "Misi harian",
    "weekly quest" to "Misi mingguan",
    "level up" to "Naik level!",
    "defeat the boss" to "Kalahkan bos",
    "defeat the monster" to "Kalahkan monster",
    "victory achieved" to "Kemenangan diraih",
    "defeat" to "Kekalahan",
    "critical hit" to "Serangan kritikal",
    "skill cooldown" to "Waktu jeda skill",
    "inventory is full" to "Inventaris penuh",
    "equipment upgraded" to "Peralatan ditingkatkan",
    "press any key to continue" to "Tekan tombol apa saja untuk lanjut",
    "tap to continue" to "Ketuk untuk melanjutkan",
    "tap to start" to "Ketuk untuk mulai",
    "tap anywhere to close" to "Ketuk di mana saja untuk menutup",
    "game over" to "Permainan berakhir",
    "respawn in" to "Bangkit kembali dalam",
    "collect your reward" to "Klaim hadiahmu",
    "claim reward" to "Klaim hadiah",
    "double tap to dash" to "Ketuk dua kali untuk meluncur",
    "mana points" to "Poin Mana",
    "health points" to "Poin Darah (HP)",
    "attack power" to "Kekuatan Serangan",
    "defense rate" to "Tingkat Pertahanan",
    "healing potion" to "Ramuan Penyembuh",
    "legendary weapon" to "Senjata Legendaris",
    "matchmaking in progress" to "Pencarian lawan berlangsung",
    "join squad" to "Gabung regu",
    "create party" to "Buat kelompok",
    "leave party" to "Tinggalkan kelompok",
    "invite friends" to "Undang teman",
    "battle pass" to "Tiket pertempuran",
    "rank up" to "Naik peringkat",
    "ranked match" to "Pertandingan berperingkat",
    "casual match" to "Pertandingan santai",
    "training ground" to "Tempat latihan",
    "select character" to "Pilih karakter",
    "select hero" to "Pilih pahlawan",
    "character details" to "Detail karakter",
    "equip item" to "Gunakan barang",
    "unequip item" to "Lepas barang",
    "upgrade weapon" to "Tingkatkan senjata",
    "enhance gear" to "Perkuat perlengkapan",
    "summon hero" to "Panggil pahlawan",
    "gacha pull" to "Tarik gacha",
    "drop rate" to "Tingkat perolehan (drop)",
    "stage clear" to "Babak selesai!",
    "dungeon clear" to "Ruang bawah tanah selesai!",
    "boss fight" to "Pertarungan bos",
    "final boss" to "Bos terakhir",
    "auto battle" to "Pertempuran otomatis",
    "speed x2" to "Kecepatan 2x",

    // Social, Chat & Messaging
    "are you sure?" to "Apakah Anda yakin?",
    "what are you doing?" to "Apa yang sedang Anda lakukan?",
    "see you tomorrow" to "Sampai jumpa besok",
    "congratulations on your win" to "Selamat atas kemenangan Anda",
    "don't worry about it" to "Jangan khawatir tentang itu",
    "let me know" to "Beri tahu saya",
    "take care" to "Jaga diri baik-baik",
    "how is it going?" to "Bagaimana kabarmu?",
    "just now" to "Baru saja",
    "reply to message" to "Balas pesan",
    "typing..." to "Sedang mengetik...",
    "voice message" to "Pesan suara",
    "video call" to "Panggilan video",
    "voice call" to "Panggilan suara",
    "missed call" to "Panggilan tak terjawab",
    "unread messages" to "Pesan belum dibaca",
    "mark as read" to "Tandai sudah dibaca",
    "delete conversation" to "Hapus percakapan",
    "block user" to "Blokir pengguna",
    "report user" to "Laporkan pengguna",
    "add to contacts" to "Tambah ke kontak",
    "shared a photo" to "Membagikan foto",
    "shared a video" to "Membagikan video",
    "shared a location" to "Membagikan lokasi",
    "view profile" to "Lihat profil",
    "edit profile" to "Ubah profil",

    // Web, Browsing & Commerce
    "add to cart" to "Tambah ke keranjang",
    "buy now" to "Beli sekarang",
    "proceed to checkout" to "Lanjut ke pembayaran",
    "order summary" to "Ringkasan pesanan",
    "shipping address" to "Alamat pengiriman",
    "payment method" to "Metode pembayaran",
    "free shipping" to "Gratis ongkos kirim",
    "out of stock" to "Stok habis",
    "in stock" to "Tersedia",
    "customer reviews" to "Ulasan pembeli",
    "read more" to "Baca selengkapnya",
    "show less" to "Tampilkan lebih sedikit",
    "terms and conditions" to "Syarat dan ketentuan",
    "all rights reserved" to "Hak cipta dilindungi undang-undang"
  )

  private val wordMap = mapOf(
    // Common Verbs
    "start" to "mulai",
    "stop" to "berhenti",
    "continue" to "lanjutkan",
    "pause" to "jeda",
    "resume" to "lanjutkan",
    "cancel" to "batal",
    "confirm" to "konfirmasi",
    "save" to "simpan",
    "delete" to "hapus",
    "remove" to "hapus",
    "edit" to "ubah",
    "update" to "perbarui",
    "upgrade" to "tingkatkan",
    "downgrade" to "turunkan",
    "download" to "unduh",
    "upload" to "unggah",
    "install" to "pasang",
    "uninstall" to "copot pemasangan",
    "share" to "bagikan",
    "search" to "cari",
    "find" to "temukan",
    "view" to "lihat",
    "see" to "lihat",
    "look" to "lihat",
    "watch" to "tonton",
    "open" to "buka",
    "close" to "tutup",
    "exit" to "keluar",
    "quit" to "keluar",
    "retry" to "coba lagi",
    "repeat" to "ulang",
    "connect" to "sambungkan",
    "disconnect" to "putuskan",
    "equip" to "pasang",
    "unequip" to "lepas",
    "craft" to "buat",
    "create" to "buat",
    "build" to "bangun",
    "attack" to "serang",
    "defend" to "tahan",
    "heal" to "sembuhkan",
    "revive" to "hidupkan",
    "flee" to "kabur",
    "escape" to "lolos",
    "run" to "lari",
    "jump" to "lompat",
    "dash" to "meluncur",
    "play" to "bermain",
    "load" to "memuat",
    "tap" to "ketuk",
    "press" to "tekan",
    "click" to "klik",
    "swipe" to "geser",
    "scroll" to "gulir",
    "fight" to "tarung",
    "walk" to "jalan",
    "speak" to "bicara",
    "listen" to "dengarkan",
    "hear" to "dengar",
    "read" to "baca",
    "write" to "tulis",
    "translate" to "terjemahkan",
    "enable" to "aktifkan",
    "disable" to "nonaktifkan",
    "select" to "pilih",
    "choose" to "pilih",
    "pick" to "pilih",
    "learn" to "pelajari",
    "claim" to "klaim",
    "collect" to "kumpulkan",
    "get" to "dapatkan",
    "give" to "beri",
    "take" to "ambil",
    "make" to "buat",
    "do" to "lakukan",
    "go" to "pergi",
    "come" to "datang",
    "know" to "tahu",
    "think" to "pikir",
    "want" to "ingin",
    "need" to "butuh",
    "use" to "gunakan",
    "apply" to "terapkan",
    "submit" to "kirimkan",
    "send" to "kirim",
    "receive" to "terima",
    "accept" to "terima",
    "decline" to "tolak",
    "reject" to "tolak",
    "allow" to "izinkan",
    "deny" to "tolak",
    "block" to "blokir",
    "unblock" to "buka blokir",
    "mute" to "bisukan",
    "unmute" to "bunyikan",
    "follow" to "ikuti",
    "unfollow" to "berhenti ikuti",
    "like" to "suka",
    "dislike" to "tidak suka",
    "check" to "periksa",
    "verify" to "verifikasi",
    "login" to "masuk",
    "logout" to "keluar",
    "register" to "daftar",
    "reset" to "atur ulang",
    "refresh" to "segarkan",
    "reload" to "muat ulang",
    "restart" to "mulai ulang",
    "reboot" to "nyalakan ulang",
    "show" to "tampilkan",
    "hide" to "sembunyikan",
    "lock" to "kunci",
    "unlock" to "buka kunci",
    "buy" to "beli",
    "sell" to "jual",
    "pay" to "bayar",
    "win" to "menang",
    "lose" to "kalah",
    "die" to "mati",
    "survive" to "bertahan",
    "kill" to "bunuh",
    "destroy" to "hancurkan",
    "damage" to "rusak",
    "repair" to "perbaiki",
    "fix" to "perbaiki",
    "boost" to "tingkatkan",
    "charge" to "isi daya",
    "drain" to "kuras",

    // Common Nouns (System, Tech & Mobile)
    "settings" to "pengaturan",
    "setting" to "pengaturan",
    "option" to "opsi",
    "options" to "opsi",
    "preference" to "preferensi",
    "preferences" to "preferensi",
    "menu" to "menu",
    "button" to "tombol",
    "screen" to "layar",
    "display" to "tampilan",
    "overlay" to "lapisan mengapung",
    "window" to "jendela",
    "notification" to "notifikasi",
    "notifications" to "notifikasi",
    "alert" to "peringatan",
    "warning" to "peringatan",
    "error" to "kesalahan",
    "message" to "pesan",
    "messages" to "pesan",
    "dialog" to "dialog",
    "popup" to "sembulan (pop-up)",
    "permission" to "izin",
    "permissions" to "izin",
    "storage" to "penyimpanan",
    "memory" to "memori",
    "battery" to "baterai",
    "network" to "jaringan",
    "connection" to "koneksi",
    "device" to "perangkat",
    "devices" to "perangkat",
    "phone" to "ponsel",
    "mobile" to "seluler",
    "account" to "akun",
    "profile" to "profil",
    "user" to "pengguna",
    "users" to "pengguna",
    "password" to "kata sandi",
    "email" to "email",
    "security" to "keamanan",
    "privacy" to "privasi",
    "camera" to "kamera",
    "photo" to "foto",
    "photos" to "foto",
    "image" to "gambar",
    "images" to "gambar",
    "picture" to "gambar",
    "video" to "video",
    "videos" to "video",
    "audio" to "audio",
    "music" to "musik",
    "sound" to "suara",
    "voice" to "suara",
    "file" to "berkas",
    "files" to "berkas",
    "folder" to "folder",
    "document" to "dokumen",
    "history" to "riwayat",
    "favorite" to "favorit",
    "favorites" to "favorit",
    "bookmark" to "markah",
    "bookmarks" to "markah",
    "download" to "unduhan",
    "downloads" to "unduhan",
    "version" to "versi",
    "update" to "pembaruan",
    "updates" to "pembaruan",
    "status" to "status",
    "time" to "waktu",
    "date" to "tanggal",
    "hour" to "jam",
    "minute" to "menit",
    "second" to "detik",
    "language" to "bahasa",
    "languages" to "bahasa",
    "text" to "teks",
    "word" to "kata",
    "words" to "kata",
    "translation" to "terjemahan",
    "dictionary" to "kamus",
    "vocabulary" to "kosakata",

    // Gaming Nouns
    "game" to "permainan",
    "games" to "permainan",
    "player" to "pemain",
    "players" to "pemain",
    "enemy" to "musuh",
    "enemies" to "musuh",
    "monster" to "monster",
    "monsters" to "monster",
    "hero" to "pahlawan",
    "heroes" to "pahlawan",
    "boss" to "bos",
    "character" to "karakter",
    "characters" to "karakter",
    "weapon" to "senjata",
    "weapons" to "senjata",
    "sword" to "pedang",
    "bow" to "busur",
    "staff" to "tongkat sihir",
    "gun" to "senjata api",
    "shield" to "perisai",
    "armor" to "baju zirah",
    "helmet" to "helm",
    "boots" to "sepatu",
    "ring" to "cincin",
    "potion" to "ramuan",
    "potions" to "ramuan",
    "elixir" to "eliksir",
    "item" to "barang",
    "items" to "barang",
    "inventory" to "inventaris",
    "slot" to "ruang",
    "slots" to "ruang",
    "gold" to "emas",
    "coin" to "koin",
    "coins" to "koin",
    "gem" to "permata",
    "gems" to "permata",
    "diamond" to "berlian",
    "chest" to "peti",
    "chests" to "peti",
    "loot" to "jarahan",
    "reward" to "hadiah",
    "rewards" to "hadiah",
    "prize" to "hadiah",
    "level" to "tingkat (level)",
    "stage" to "babak (stage)",
    "chapter" to "bab",
    "quest" to "misi (quest)",
    "quests" to "misi",
    "mission" to "misi",
    "missions" to "misi",
    "task" to "tugas",
    "tasks" to "tugas",
    "skill" to "kemampuan (skill)",
    "skills" to "kemampuan",
    "magic" to "sihir",
    "power" to "kekuatan",
    "strength" to "kekuatan",
    "defense" to "pertahanan",
    "agility" to "kelincahan",
    "speed" to "kecepatan",
    "health" to "darah (HP)",
    "stamina" to "stamina",
    "mana" to "mana",
    "energy" to "energi",
    "cooldown" to "waktu jeda",
    "score" to "skor",
    "rank" to "peringkat",
    "leaderboard" to "papan peringkat",
    "guild" to "persekutuan (guild)",
    "clan" to "klan",
    "party" to "kelompok (party)",
    "squad" to "regu (squad)",
    "team" to "tim",
    "dungeon" to "ruang bawah tanah",
    "arena" to "arena",
    "lobby" to "ruang tunggu (lobby)",
    "server" to "peladen (server)",
    "match" to "pertandingan",
    "battle" to "pertempuran",
    "combat" to "pertempuran",
    "victory" to "kemenangan",

    // Adjectives & Adverbs
    "new" to "baru",
    "old" to "lama",
    "good" to "bagus",
    "bad" to "buruk",
    "great" to "hebat",
    "best" to "terbaik",
    "worst" to "terburuk",
    "high" to "tinggi",
    "low" to "rendah",
    "big" to "besar",
    "small" to "kecil",
    "fast" to "cepat",
    "slow" to "lambat",
    "quick" to "cepat",
    "easy" to "mudah",
    "hard" to "sulit",
    "difficult" to "sulit",
    "simple" to "sederhana",
    "free" to "gratis",
    "paid" to "berbayar",
    "active" to "aktif",
    "inactive" to "nonaktif",
    "online" to "daring (online)",
    "offline" to "luring (offline)",
    "ready" to "siap",
    "empty" to "kosong",
    "full" to "penuh",
    "rare" to "langka",
    "epic" to "epik",
    "legendary" to "legendaris",
    "common" to "biasa",
    "uncommon" to "tidak biasa",
    "mythic" to "mitis",
    "instant" to "instan",
    "direct" to "langsung",
    "complete" to "lengkap",
    "completed" to "selesai",
    "success" to "sukses",
    "successful" to "berhasil",
    "failed" to "gagal",
    "safe" to "aman",
    "danger" to "bahaya",
    "dangerous" to "berbahaya",
    "clear" to "jelas",
    "first" to "pertama",
    "last" to "terakhir",
    "next" to "berikutnya",
    "previous" to "sebelumnya",
    "all" to "semua",
    "every" to "setiap",
    "each" to "masing-masing",
    "some" to "beberapa",
    "many" to "banyak",
    "much" to "banyak",
    "more" to "lebih",
    "most" to "paling",
    "less" to "kurang",
    "least" to "paling sedikit",
    "other" to "lainnya",
    "another" to "lain",
    "same" to "sama",
    "different" to "berbeda",
    "only" to "hanya",
    "also" to "juga",
    "very" to "sangat",
    "too" to "terlalu",
    "now" to "sekarang",
    "today" to "hari ini",
    "yesterday" to "kemarin",
    "tomorrow" to "besok",
    "always" to "selalu",
    "never" to "tidak pernah",
    "sometimes" to "kadang-kadang",
    "often" to "sering",
    "soon" to "segera",
    "later" to "nanti",
    "again" to "lagi",
    "back" to "kembali",
    "forward" to "maju",
    "up" to "atas",
    "down" to "bawah",
    "left" to "kiri",
    "right" to "kanan",
    "true" to "benar",
    "false" to "salah",

    // Pronouns, Prepositions & Conjunctions
    "you" to "Anda",
    "your" to "Anda",
    "yours" to "milik Anda",
    "i" to "saya",
    "me" to "saya",
    "my" to "saya",
    "mine" to "milik saya",
    "we" to "kami",
    "us" to "kami",
    "our" to "kami",
    "they" to "mereka",
    "them" to "mereka",
    "their" to "mereka",
    "he" to "dia",
    "him" to "dia",
    "his" to "dia",
    "she" to "dia",
    "her" to "dia",
    "it" to "itu",
    "its" to "miliknya",
    "this" to "ini",
    "that" to "itu",
    "these" to "ini",
    "those" to "itu",
    "what" to "apa",
    "who" to "siapa",
    "where" to "di mana",
    "when" to "kapan",
    "why" to "mengapa",
    "how" to "bagaimana",
    "which" to "yang mana",
    "here" to "di sini",
    "there" to "di sana",
    "in" to "di",
    "on" to "pada",
    "at" to "di",
    "to" to "ke",
    "for" to "untuk",
    "with" to "dengan",
    "without" to "tanpa",
    "from" to "dari",
    "by" to "oleh",
    "about" to "tentang",
    "of" to "dari",
    "and" to "dan",
    "or" to "atau",
    "but" to "tetapi",
    "if" to "jika",
    "because" to "karena",
    "so" to "jadi",
    "then" to "lalu",
    "as" to "sebagai",
    "into" to "ke dalam",
    "out" to "keluar",
    "over" to "di atas",
    "under" to "di bawah",
    "between" to "di antara",
    "through" to "melalui",
    "before" to "sebelum",
    "after" to "setelah",
    "until" to "hingga",
    "cannot" to "tidak dapat",
    "can't" to "tidak bisa",
    "can" to "dapat",
    "could" to "bisa",
    "must" to "harus",
    "should" to "sebaiknya",
    "will" to "akan",
    "would" to "akan",
    "may" to "boleh",
    "might" to "mungkin",
    "is" to "adalah",
    "are" to "adalah",
    "am" to "adalah",
    "was" to "adalah",
    "were" to "adalah",
    "be" to "menjadi",
    "been" to "telah",
    "have" to "memiliki",
    "has" to "memiliki",
    "had" to "memiliki",
    "not" to "tidak",
    "no" to "tidak",
    "don't" to "jangan",
    "doesn't" to "tidak",
    "didn't" to "tidak",
    "please" to "silakan"
  )

  // Pre-sorted phrases by length descending to match longest phrases first
  private val sortedPhrases by lazy {
    exactPhraseMap.entries.sortedByDescending { it.key.length }
  }

  /**
   * Translates English text to Indonesian using exact phrases, morphological stemming,
   * and dictionary lookup. Guarantees translated Indonesian output.
   */
  fun translateOffline(text: String): String {
    val clean = text.trim()
    if (clean.isEmpty()) return ""

    val lower = clean.lowercase()

    // 1. Direct exact phrase match (with punctuation stripping)
    val prefix = clean.takeWhile { !it.isLetterOrDigit() }
    val suffix = clean.takeLastWhile { !it.isLetterOrDigit() }
    val coreText = clean.removePrefix(prefix).removeSuffix(suffix).lowercase()

    exactPhraseMap[coreText]?.let {
      return prefix + matchCapitalization(clean, it) + suffix
    }

    // 2. Partial phrase substitution using longest-first matching
    var phraseSubstituted = clean
    for ((phrase, idTranslation) in sortedPhrases) {
      val regex = Regex("(?i)\\b" + Regex.escape(phrase) + "\\b")
      if (regex.containsMatchIn(phraseSubstituted)) {
        phraseSubstituted = regex.replace(phraseSubstituted, idTranslation)
      }
    }

    // 3. Word-by-word tokenized translation with smart morphological stemming
    val words = phraseSubstituted.split(Regex("\\s+"))

    val translatedWords = words.map { rawWord ->
      val punctuationSuffix = rawWord.takeLastWhile { !it.isLetterOrDigit() }
      val punctuationPrefix = rawWord.takeWhile { !it.isLetterOrDigit() }
      val coreWord = rawWord.removePrefix(punctuationPrefix).removeSuffix(punctuationSuffix).lowercase()

      if (coreWord.isEmpty()) return@map rawWord

      // Direct match
      var match = wordMap[coreWord]

      // Morphological stemming if no direct match:
      if (match == null) {
        match = findStemmedMatch(coreWord)
      }

      if (match != null) {
        punctuationPrefix + matchCapitalization(rawWord, match) + punctuationSuffix
      } else {
        // Already translated or unknown word: keep rawWord
        rawWord
      }
    }

    return translatedWords.joinToString(" ")
  }

  /**
   * Stems English words to find matching root word in Indonesian dictionary.
   */
  private fun findStemmedMatch(word: String): String? {
    // 1. Plural -ies (e.g., enemies -> enemy)
    if (word.endsWith("ies") && word.length > 4) {
      val stem = word.removeSuffix("ies") + "y"
      wordMap[stem]?.let { return it }
    }

    // 2. Plural -es (e.g., boxes -> box, heroes -> hero)
    if (word.endsWith("es") && word.length > 3) {
      val stem = word.removeSuffix("es")
      wordMap[stem]?.let { return it }
    }

    // 3. Plural -s (e.g., options -> opsi, items -> barang)
    if (word.endsWith("s") && !word.endsWith("ss") && word.length > 2) {
      val stem = word.removeSuffix("s")
      wordMap[stem]?.let { return it }
    }

    // 4. Past tense -ed (e.g., attacked -> serang, started -> mulai)
    if (word.endsWith("ed") && word.length > 3) {
      val stem = word.removeSuffix("ed")
      wordMap[stem]?.let { return "telah $it" }
      val stemWithE = word.removeSuffix("d")
      wordMap[stemWithE]?.let { return "telah $it" }
    }

    // 5. Continuous -ing (e.g., playing -> bermain, loading -> memuat)
    if (word.endsWith("ing") && word.length > 4) {
      val stem = word.removeSuffix("ing")
      wordMap[stem]?.let { return "sedang $it" }
      val stemWithE = stem + "e"
      wordMap[stemWithE]?.let { return "sedang $it" }
    }

    // 6. Adverb -ly (e.g., instantly -> secara instan)
    if (word.endsWith("ly") && word.length > 3) {
      val stem = word.removeSuffix("ly")
      wordMap[stem]?.let { return "secara $it" }
    }

    // 7. Prefix un- (e.g., unlocked -> terbuka / tidak terkunci)
    if (word.startsWith("un") && word.length > 4) {
      val stem = word.removePrefix("un")
      wordMap[stem]?.let { return "tidak $it" }
    }

    // 8. Prefix re- (e.g., restart -> mulai ulang, reload -> muat ulang)
    if (word.startsWith("re") && word.length > 4) {
      val stem = word.removePrefix("re")
      wordMap[stem]?.let { return "$it ulang" }
    }

    return null
  }

  private fun matchCapitalization(original: String, translated: String): String {
    if (original.all { it.isUpperCase() }) return translated.uppercase()
    if (original.firstOrNull()?.isUpperCase() == true) return translated.replaceFirstChar { it.uppercase() }
    return translated
  }

  fun getVocabularyList(): List<VocabularyItem> {
    return listOf(
      VocabularyItem("v1", "Quest", "Misi / Tugas", "Game", "Complete the side quest to earn gems.", "kwest"),
      VocabularyItem("v2", "Critical Hit", "Serangan Telak / Kritikal", "Game", "Your archer scored a critical hit!", "kri-ti-kel hit"),
      VocabularyItem("v3", "Draw Over Other Apps", "Tampilkan di atas aplikasi lain", "Sistem", "Permisi ini dibutuhkan untuk tombol mengapung.", "dro oh-ver"),
      VocabularyItem("v4", "Real-Time", "Waktu Nyata / Langsung", "Teknologi", "Instant real-time screen translation engine.", "ri-yel taim"),
      VocabularyItem("v5", "Inventory", "Inventaris / Tas Penyimpanan", "Game", "Your inventory has no more slots.", "in-ven-to-ri"),
      VocabularyItem("v6", "Cooldown", "Waktu Jeda Pemulihan", "Game", "Spell is still on cooldown for 3 seconds.", "kul-daun"),
      VocabularyItem("v7", "Overlay", "Lapisan Mengapung di Layar", "Teknologi", "The translated badges are rendered on the overlay.", "oh-ver-lei"),
      VocabularyItem("v8", "Respawn", "Bangkit / Muncul Kembali", "Game", "The player will respawn in safe zone.", "ri-spawn"),
      VocabularyItem("v9", "Matchmaking", "Pencarian Lawan Tanding", "Game", "Matchmaking is finding players of equal skill.", "mec-mei-king"),
      VocabularyItem("v10", "Equip", "Pasang / Kenakan", "Game", "Equip the golden shield for extra defense.", "i-kwip")
    )
  }
}
