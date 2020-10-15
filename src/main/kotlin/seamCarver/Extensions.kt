package seamCarver

import kweb.Element
import kweb.InputElement
import java.awt.image.BufferedImage
import java.io.ByteArrayInputStream
import java.io.ByteArrayOutputStream
import java.io.IOException
import java.util.*
import java.util.concurrent.CompletableFuture
import javax.imageio.ImageIO

fun MutableList<Double>.minIndex(): Int {
    return this.indexOf(Collections.min(this))
}

fun Array<DoubleArray>.copyColumnsFrom(arr: Array<DoubleArray>, rowNumber: Int) {
    if (rowNumber >= this[0].size) {
        throw IllegalArgumentException("rowNumber must be less than ${this[0].size - 1}")
    }
    for (row in this.indices) {
        for (col in 0..rowNumber) {
            this[row][col] = arr[row][col]
        }
    }
}

fun DoubleArray.minIndex(): Int {
    return this.indexOfFirst { it == this.minOrNull()!! }
}

fun Array<DoubleArray>.minIndexOfColumnsOfRow(rowNumber: Int): Int {
    var minNum = this[0][rowNumber]
    var minIndex = 0
    repeat(this.size)
    { x ->
        if (minNum > this[x][rowNumber]) {
            minIndex = x
            minNum = this[x][rowNumber]
        }
    }
    return minIndex
}

fun Array<DoubleArray>.max(): Double {
    var max = 0.0
    for (i in this) {
        val temp = i.maxOrNull()!!
        if (temp > max)
            max = temp
    }
    return max
}
fun encodeToString(image: BufferedImage?, type: String?): String? {
    var imageString: String? = null
    val bos = ByteArrayOutputStream()
    try {
        ImageIO.write(image, type, bos)
        val imageBytes: ByteArray = bos.toByteArray()
        val encoder = Base64.getEncoder()
        imageString = encoder.encodeToString(imageBytes)
        bos.close()
    } catch (e: IOException) {
        e.printStackTrace()
    }
    return "data:image/png;base64,$imageString"
}
fun decodeToImage(imageString: String?): BufferedImage? {
    var image: BufferedImage? = null
    val imageByte: ByteArray
    val imagestr = imageString!!.drop(22)
    try {
        val decoder = Base64.getDecoder()
        imageByte = decoder.decode(imagestr)
        val bis = ByteArrayInputStream(imageByte)
        image = ImageIO.read(bis)
        bis.close()
    } catch (e: Exception) {
        e.printStackTrace()
    }
    return image
}