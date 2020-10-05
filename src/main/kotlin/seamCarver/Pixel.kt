package seamCarver

import java.awt.Color
import java.awt.image.BufferedImage

class Pixel {
    var x: Int
    var y: Int
    var color: Color? = null

    constructor(X: Int, Y: Int, img: BufferedImage) {
        x = X
        y = Y
        updateColor(img)
    }

    constructor(X: Int, Y: Int, COLOR: Color) {
        x = X
        y = Y
        color = COLOR
    }

    constructor(X: Int, Y: Int) {
        x = X
        y = Y
    }

    private fun updateColor(img: BufferedImage) {
        val colorValue = img.getRGB(x, y)
        color = Color(colorValue)
    }
}
