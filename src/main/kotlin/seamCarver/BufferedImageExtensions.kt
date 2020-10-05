package seamCarver

import java.awt.Color
import java.awt.image.BufferedImage
import java.io.File
import javax.imageio.ImageIO
import javax.swing.ImageIcon
import javax.swing.JFrame
import javax.swing.JLabel


fun BufferedImage.show() {
    val frame = JFrame()
    val icon = ImageIcon(this)
    val jLabel = JLabel(icon)
    frame.contentPane = jLabel
    // f.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    frame.defaultCloseOperation = JFrame.DISPOSE_ON_CLOSE
    frame.isResizable = false
    frame.pack()
    frame.isVisible = true
    // draw
    frame.repaint()
}
fun BufferedImage.deepCopy(): BufferedImage? {
    val cm = this.colorModel
    val isAlphaPremultiplied = cm.isAlphaPremultiplied
    val raster = this.copyData(null)
    return BufferedImage(cm, raster, isAlphaPremultiplied, null)
}
fun BufferedImage.getTransposed(): BufferedImage {
    val transposeImage = BufferedImage(this.height, this.width, BufferedImage.TYPE_INT_RGB)
    repeat(transposeImage.width) { w ->
        repeat(transposeImage.height) { h ->
            transposeImage.setRGB(w, h, this.getRGB(h, w))
        }
    }
    return transposeImage
}
fun BufferedImage.getJLabel(): JLabel? {
    val icon = ImageIcon(this)
    return JLabel(icon)
}
fun BufferedImage.getColor(x: Int, y: Int): Color {
    if ((x < 0 || x > width) || (y < 0 || y > height)) {
        throw IllegalArgumentException("x should be less than $width, and y should be less than $height")
    }
    return Color(this.getRGB(x, y))
}

fun BufferedImage.save(fileName: String) {
    ImageIO.write(this, "PNG", File(fileName))
}

fun BufferedImage.save(file: File) {
    ImageIO.write(this, "PNG", file)
}
