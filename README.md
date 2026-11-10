# username-pbo2026-springboot-cashflow

Aplikasi web **Cashflow (Catatan Keuangan)** berbasis Spring Boot 4.1.2, Maven, dan Vaadin 25.3.4 dengan
Clean Architecture (domain, usecase, adapter, framework). Data disimpan **in-memory** (hilang saat restart).

- Group: `org.delcom`
- Package: `org.delcom.usernamepbo2026springbootcashflow`
- Java: 25

## Ganti username (jika belum)

Folder/proyek ini memakai username `username`. Ganti dengan username Anda:

```
./rename.sh username_anda          # Linux / macOS / Git Bash
.\rename.ps1 -NewUser username_anda # PowerShell (Windows)
```

Lalu ganti nama folder proyek menjadi `{username}-pbo2026-springboot-cashflow`.

## Menjalankan

Butuh JDK 25 dan koneksi internet (pertama kali, untuk unduh dependency + Maven).

```
.\mvnw.cmd spring-boot:run        # Windows
./mvnw spring-boot:run            # Linux / macOS
```

Buka http://localhost:8080 (port diatur lewat `APP_PORT` di file `.env`).

Build & jalankan JAR (wajib profil production Vaadin):

```
.\mvnw.cmd clean package -Pproduction
java -jar target\username-pbo2026-springboot-cashflow-0.0.1-SNAPSHOT.jar
```

Test + laporan coverage Jacoco: `.\mvnw.cmd clean verify` (laporan: `target/site/jacoco/index.html`).

Jika memakai Maven bawaan IntelliJ, cukup jalankan `spring-boot:run` dari panel Maven atau class `App`.

## Struktur

```
domain/      entity (Transaction, TransactionType, SortOption), exception, repository (port)
usecase/     FinanceUseCase (tambah, hapus, cari, urutkan, getBalance)
adapter/     InMemoryRepository, TransactionRepository, FinancePresenter
framework/   view/FinanceView (Vaadin)
config/      AppConfig (composition root)
```

## Fitur

Tambah pemasukan, tambah pengeluaran, cari keterangan, urutkan (header kolom / SortOption), saldo,
hapus dengan konfirmasi, dan validasi (keterangan kosong, jumlah non-numerik / non-positif, ID tidak ditemukan).
Format uang: `Rp 1,000,000.00`.
