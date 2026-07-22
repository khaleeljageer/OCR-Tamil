package com.jskaleel.vizhi_tamil.core.export

import android.content.Context
import android.graphics.Color
import android.graphics.pdf.PdfDocument
import android.net.Uri
import android.text.Layout
import android.text.StaticLayout
import android.text.TextPaint
import androidx.core.content.FileProvider
import com.jskaleel.vizhi_tamil.domain.model.ExportFormat
import com.jskaleel.vizhi_tamil.domain.model.ImageOCR
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.File
import javax.inject.Inject
import javax.inject.Singleton

/** Writes a scan's recognised text to a TXT or PDF file and returns a shareable content Uri. */
@Singleton
class ScanExporter @Inject constructor(
    @ApplicationContext private val context: Context,
) {
    suspend fun export(scan: ImageOCR, format: ExportFormat): Result<Uri> =
        withContext(Dispatchers.IO) {
            runCatching {
                val dir = File(context.cacheDir, EXPORT_DIR).apply { mkdirs() }
                val file = File(dir, "scan_${scan.id}.${format.extension}")
                when (format) {
                    ExportFormat.TXT -> file.writeText(scan.text)
                    ExportFormat.PDF -> writePdf(file, scan.text)
                }
                FileProvider.getUriForFile(context, "${context.packageName}.fileprovider", file)
            }
        }

    private fun writePdf(file: File, text: String) {
        val document = PdfDocument()
        val paint = TextPaint().apply {
            color = Color.BLACK
            textSize = TEXT_SIZE
        }
        val usableWidth = PAGE_WIDTH - 2 * MARGIN

        @Suppress("DEPRECATION")
        val layout = StaticLayout(
            text,
            paint,
            usableWidth,
            Layout.Alignment.ALIGN_NORMAL,
            1f,
            0f,
            false,
        )

        val usableHeight = PAGE_HEIGHT - 2 * MARGIN
        var pageNumber = 1
        var offsetY = 0
        while (offsetY < layout.height) {
            val pageInfo =
                PdfDocument.PageInfo.Builder(PAGE_WIDTH, PAGE_HEIGHT, pageNumber).create()
            val page = document.startPage(pageInfo)
            page.canvas.apply {
                save()
                translate(MARGIN.toFloat(), (MARGIN - offsetY).toFloat())
                layout.draw(this)
                restore()
            }
            document.finishPage(page)
            offsetY += usableHeight
            pageNumber++
        }

        file.outputStream().use { document.writeTo(it) }
        document.close()
    }

    companion object {
        private const val EXPORT_DIR = "exports"
        private const val TEXT_SIZE = 14f
        private const val MARGIN = 40

        // A4 at 72dpi, in points.
        private const val PAGE_WIDTH = 595
        private const val PAGE_HEIGHT = 842
    }
}
