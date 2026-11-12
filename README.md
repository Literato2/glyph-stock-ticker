> Repositorio migrado desde `github.com/literato1987`, cuenta anterior del mismo autor,
> sin acceso desde agosto de 2026. El historial de commits se conserva íntegro.

# Glyph Stock Ticker

Glyph Toy for the **Nothing Phone (3)** that shows live stock and crypto prices on the 25×25 Glyph Matrix.

Built with the [Glyph Matrix Developer Kit](https://github.com/Nothing-Developer-Programme/GlyphMatrix-Developer-Kit), based on the [Example Project](https://github.com/Nothing-Developer-Programme/GlyphMatrix-Example-Project).

**Device:** Nothing Phone (3) · `Glyph.DEVICE_23112` · Android 15+

## Preview

<p align="center">
  <img src="docs/preview-github.png?v=2" alt="TSLA, BTC, and NVDA on the Glyph Matrix" width="640">
</p>

Long-press cycles symbol: **TSLA** → **SPCX** (SpaceX) → **NVDA** → **BTC**.

> **TSLA, BTC and NVDA are examples**, not a fixed list. You can add your own tickers (see below). Previews use the [official Phone (3) LED allocation](https://github.com/Nothing-Developer-Programme/GlyphMatrix-Developer-Kit/blob/main/image/23111_25111_LED_allocation.svg) via [glyph-matrix-simulator](https://github.com/Literato2/glyph-matrix-simulator). BTC example uses compact `87k` in the README — full integer BTC prices are wider on the matrix.

## Features

- **Default symbol:** Tesla (`TSLA`)
- **Long-press Glyph Button:** cycle through configured symbols
- **Display:** symbol / integer price / integer daily change (e.g. `TSLA` / `421` / `+2%`)
- **Auto-refresh:** every 5 minutes, on AOD ticks, and immediately on symbol switch
- **No API key** — Yahoo Finance public chart endpoint

## Install (prebuilt APK)

1. Download [`releases/v1.4.2/glyph-stock-ticker-v1.4.2.apk`](releases/v1.4.2/glyph-stock-ticker-v1.4.2.apk)
2. Install: `adb install -r glyph-stock-ticker-v1.4.2.apk` (or tap the file on the phone)
3. Open **Glyph Stock Ticker** → **Activate Glyph Toy**
4. In **Settings → Glyph Interface → Glyph Toys**, drag **Stock Ticker** to **Active**
5. Flip the phone or use Glyph Touch to view; **long-press** the Glyph Button to switch symbol

## Custom tickers

The shipped symbols are **examples**. To track something else:

### 1. Add the asset

Edit `app/src/main/java/.../tesla/StockFetcher.kt` — enum `StockAsset`:

```kotlin
enum class StockAsset(val displaySymbol: String, val yahooSymbol: String) {
    TESLA("TSLA", "TSLA"),
    BITCOIN("BTC", "BTC-USD"),
    NVIDIA("NVDA", "NVDA"),
    APPLE("AAPL", "AAPL"),   // example: add your own
    ;
    fun next(): StockAsset = entries[(ordinal + 1) % entries.size]
}
```

| Field | Meaning |
|---|---|
| `displaySymbol` | Text on the matrix (top line). **Max ~4 characters** on Phone (3). |
| `yahooSymbol` | [Yahoo Finance](https://finance.yahoo.com/) ticker. Stocks: `AAPL`. Crypto: `ETH-USD`. Indices: `^GSPC`. |

Look up the Yahoo symbol on finance.yahoo.com (URL slug after `/quote/`).

### 2. Check it fits on the matrix

The 25×25 grid is tiny. Before building, preview with [glyph-matrix-simulator](https://github.com/Literato2/glyph-matrix-simulator):

```bash
pip install Pillow
python preview.py --top AAPL --mid 228 --low +1% -o preview.png --crop --no-grid
```

If the symbol is too wide, shorten the display (e.g. `BTC` instead of `BITCOIN`, or `87k` for large prices).

### 3. Add missing letters (if needed)

Symbols are drawn with a custom 3×5 pixel font in `MatrixPixelFont.kt`. If a letter is missing, it is **silently skipped** — you may see only part of the word (e.g. `T` instead of `BTC`).

Add glyphs to `MatrixPixelFont.kt` and mirror them in [glyph-matrix-simulator](https://github.com/Literato2/glyph-matrix-simulator) `preview.py` → `GLYPHS`.

**Letter traps on a 3×5 font:**
- `N` needs a **diagonal** — a filled centre (`###`) reads as `M`
- Similar glyphs (`M`/`N`, `O`/`0`) need distinct shapes

### 4. Rebuild and install

```bash
export JAVA_HOME=/path/to/jdk-17
./gradlew assembleDebug
adb install -r app/build/outputs/apk/debug/app-debug.apk
```

Long-press cycles through all entries in `StockAsset` in enum order.

## Build from source

Requires **JDK 17** (not just a JRE).

```bash
export JAVA_HOME=/path/to/jdk-17
cd glyph-stock-ticker
./gradlew assembleDebug
adb install -r app/build/outputs/apk/debug/app-debug.apk
```

## Project layout

| Path | Purpose |
|---|---|
| `app/.../tesla/TeslaStockService.kt` | Glyph Toy service |
| `app/.../tesla/StockFetcher.kt` | Yahoo Finance + symbol list |
| `app/.../tesla/MatrixPixelFont.kt` | 3×5 pixel font (symbol / `%` lines) |
| `app/.../tesla/MatrixLedMask.kt` | Official 621-LED mask |
| `app/.../demos/GlyphMatrixService.kt` | Lifecycle wrapper (from Example Project) |

## Data & disclaimer

- Prices from [Yahoo Finance](https://finance.yahoo.com/) chart API (unofficial, no warranty).
- **Not financial advice.** For personal / hobby use.
- Yahoo may rate-limit or change the API without notice.

## License

MIT — see [LICENSE](LICENSE). Based on Nothing's Example Project; stock-ticker logic is original.

## Related

- [glyph-matrix-simulator](https://github.com/Literato2/glyph-matrix-simulator) — preview layouts with the official 621-LED map
- [GlyphMatrix Developer Kit](https://github.com/Nothing-Developer-Programme/GlyphMatrix-Developer-Kit)
- [GlyphMatrixEditor](https://github.com/pauwma/GlyphMatrixEditor) — full web editor for matrix art and animation