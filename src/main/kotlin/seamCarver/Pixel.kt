package seamCarver

import java.awt.Color
import java.awt.image.BufferedImage

class Pixel(X: Int, Y: Int, img: BufferedImage) {
    var x: Int = X
    var y: Int = Y
    var color: Color? = null

    init {
        updateColor(img)
    }

    private fun updateColor(img: BufferedImage) {
        val colorValue = img.getRGB(x, y)
        color = Color(colorValue)
    }
}

