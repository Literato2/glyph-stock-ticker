package com.nothinglondon.sdkdemo.tesla

import android.content.Context
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.drawable.BitmapDrawable
import com.nothing.ketchum.GlyphMatrixUtils

object MatrixPixelFont {

    private const val CANVAS_SIZE = 25
    private const val GLYPH_WIDTH = 3
    private const val GLYPH_HEIGHT = 5
    private const val GLYPH_SPACING = 1

    private val glyphs: Map<Char, Array<String>> = mapOf(
        '0' to rows("111", "101", "101", "101", "111"),
        '1' to rows("010", "110", "010", "010", "111"),
        '2' to rows("111", "001", "111", "100", "111"),
        '3' to rows("111", "001", "111", "001", "111"),
        '4' to rows("101", "101", "111", "001", "001"),
        '5' to rows("111", "100", "111", "001", "111"),
        '6' to rows("111", "100", "111", "101", "111"),
        '7' to rows("111", "001", "010", "010", "010"),
        '8' to rows("111", "101", "111", "101", "111"),
        '9' to rows("111", "101", "111", "001", "111"),
        '.' to rows("000", "000", "000", "000", "010"),
        '+' to rows("000", "010", "111", "010", "000"),
        '-' to rows("000", "000", "111", "000", "000"),
        '%' to rows("100", "001", "010", "100", "001"),
        'E' to rows("111", "100", "110", "100", "111"),
        'R' to rows("110", "101", "110", "101", "101"),
        'T' to rows("111", "010", "010", "010", "010"),
        'S' to rows("111", "100", "111", "001", "111"),
        'A' to rows("010", "101", "111", "101", "101"),
        'B' to rows("110", "101", "110", "101", "110"),
        'C' to rows("011", "100", "100", "100", "011"),
        'D' to rows("110", "101", "101", "101", "110"),
        'N' to rows("101", "110", "011", "101", "101"),
        'V' to rows("101", "101", "101", "101", "010"),
        'L' to rows("100", "100", "100", "100", "111"),
        'Y' to rows("101", "101", "010", "010", "010"),
        'P' to rows("110", "101", "110", "100", "100"),
        'X' to rows("101", "101", "010", "101", "101"),
        'k' to rows("100", "101", "110", "101", "101"),
        'r' to rows("000", "101", "110", "101", "101"),
        'e' to rows("000", "011", "111", "100", "111"),
        'y' to rows("000", "101", "101", "111", "001"),
        't' to rows("010", "111", "010", "010", "001"),
    )

    fun toDrawableBitmap(context: Context, text: String, anchorY: Int): Bitmap {
        val bitmap = Bitmap.createBitmap(CANVAS_SIZE, CANVAS_SIZE, Bitmap.Config.ARGB_8888)
        val canvas = Canvas(bitmap)
        canvas.drawColor(Color.BLACK)

        val cell = GLYPH_WIDTH + GLYPH_SPACING
        var x = MatrixCircle.centerX(text.length)

        val paint = android.graphics.Paint().apply { color = Color.WHITE }

        text.forEach { char ->
            glyphs[char]?.let { glyph ->
                glyph.forEachIndexed { rowIndex, row ->
                    row.forEachIndexed { colIndex, pixel ->
                        if (pixel != '1') return@forEachIndexed
                        val px = x + colIndex
                        val py = anchorY + rowIndex
                        if (!MatrixCircle.isInside(px, py)) return@forEachIndexed
                        canvas.drawRect(
                            px.toFloat(),
                            py.toFloat(),
                            px + 1f,
                            py + 1f,
                            paint,
                        )
                    }
                }
            }
            x += cell
        }

        val drawable = BitmapDrawable(context.resources, bitmap)
        return GlyphMatrixUtils.drawableToBitmap(drawable)
    }

    private fun rows(vararg lines: String): Array<String> = arrayOf(*lines)
}