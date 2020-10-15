package seamCarver

import java.awt.Color
import java.awt.image.BufferedImage
import java.io.File
import javax.imageio.ImageIO
import kotlin.math.pow
import kotlin.math.sqrt


class SeamCarver(bufferedImage: BufferedImage) {
    var image: BufferedImage = bufferedImage
        set(value) {
            field = value
            width = value.width
            height = value.height
        }
    var width: Int = bufferedImage.width
    var height: Int = bufferedImage.height
    constructor(imageFullPath: String) : this(ImageIO.read(File(imageFullPath)))
    constructor(imageFile: File) : this(ImageIO.read(imageFile))

    //region Energy Calculation
    private fun getGradient(pixel1: Pixel, pixel2: Pixel): Double {
        val r = pixel1.color!!.red - pixel2.color!!.red
        val g = pixel1.color!!.green - pixel2.color!!.green
        val b = pixel1.color!!.blue - pixel2.color!!.blue
        return r.toDouble().pow(2.0) + g.toDouble().pow(2.0) + b.toDouble().pow(2.0)
    }

    fun energy(x: Int, y: Int): Double {    // energy of pixel at column x and row y
        val posX = when (x) {
            0 -> 1
            width - 1 -> x - 1
            else -> x
        }
        val posY = when (y) {
            0 -> 1
            this.height - 1 -> y - 1
            else -> y
        }
        val left = Pixel(posX - 1, y, image)
        val right = Pixel(posX + 1, y, image)
        val top = Pixel(x, posY - 1, image)
        val down = Pixel(x, posY + 1, image)

        val xGradient = getGradient(left, right)
        val yGradient = getGradient(top, down)
        return sqrt(xGradient + yGradient)
    }

    fun getEnergyMatrix(): Array<DoubleArray> {
        val energyMatrix = Array(width) { DoubleArray(height) }
        repeat(height) { y ->
            repeat(width) { x ->
                energyMatrix[x][y] = energy(x, y)
            }
        }
        return energyMatrix
    }
    //endregion

    //region ImageManipulation
    fun greyscaleEnergyImage() {
        val energyMap: Array<DoubleArray> = getEnergyMatrix()
        val maxEnergyValue: Double? = energyMap.max()
        repeat(width) { x ->
            repeat(height) { y ->
                val intensity = ((255.0 * energyMap[x][y]) / (maxEnergyValue!!)).toInt()
                image.setRGB(x, y, Color(intensity, intensity, intensity).rgb)
            }
        }
    }

    fun invertImage() {
        repeat(this.width) { x ->
            repeat(this.height) { y ->
                val color = Color(image.getRGB(x, y))
                image.setRGB(
                        x, y, Color(255 - color.red, 255 - color.green, 255 - color.blue).rgb
                )
            }
        }
    }

    fun highlightVerticalPixels(array: IntArray, highlightColor: Color = Color(255, 0, 0)) {
        repeat(array.size) { y ->
            image.setRGB(array[y],y, highlightColor.rgb)
        }
    }
    fun highlightHorizontalPixels(array: IntArray, highlightColor: Color = Color(255, 0, 0)) {
        repeat(array.size) { y ->
            image.setRGB(y,array[y], highlightColor.rgb)
        }
    }
    //endregion

    //region WriteToFiles
    fun writeEnergyMap(outFile: File) {
        val energyMatrix = getEnergyMatrix()
        repeat(width) { x ->
            repeat(height) { y ->
                outFile.appendText("${energyMatrix[x][y]}\t")
            }
            outFile.appendText("\n\n")
        }
    }

    fun writePixelCordMap(outFile: File) {
        repeat(height) { x ->
            repeat(width) { y ->
                outFile.appendText("[$x,$y]\t")
            }
            outFile.appendText("\n\n")
        }
    }
    //endregion

    //region Seam Calculations
    fun findVerticalSeam(): IntArray {  // sequence of indices for vertical seam
        val energyMap = getEnergyMatrix()
        val dpEnergy = Array(width) { DoubleArray(height) }
        dpEnergy.copyColumnsFrom(energyMap, 0)
        val seamArray = IntArray(height)
        var minimum: Double
        for (y in 1 until height) {
            repeat(width) { x ->
                minimum = when (x) {  //topMiddle(x,y-1) topLeft(x-1, y-1) topRight(x+1, y-1)
                    0 -> minOf(dpEnergy[x][y - 1], dpEnergy[x + 1][y - 1])
                    width - 1 -> minOf(dpEnergy[x][y - 1], dpEnergy[x - 1][y - 1])
                    else -> minOf(dpEnergy[x - 1][y - 1], dpEnergy[x][y - 1], dpEnergy[x + 1][y - 1])
                }
                dpEnergy[x][y] = energyMap[x][y] + minimum
            }
        }
        //minIndexOfColumnsOfRow = extension function to find index minimum column from the row
        val minIndex = dpEnergy.minIndexOfColumnsOfRow(height - 1)
        var yIndex = height - 1
        var xIndex = minIndex
        seamArray[yIndex] = xIndex
        while (yIndex > 0) {
            when (xIndex) {
                0 -> {
                    if (minOf(dpEnergy[xIndex][yIndex - 1], dpEnergy[xIndex + 1][yIndex - 1]) !=
                            dpEnergy[xIndex][yIndex - 1])
                        xIndex++
                }
                width - 1 -> {
                    if (minOf(dpEnergy[xIndex][yIndex - 1], dpEnergy[xIndex - 1][yIndex - 1]) !=
                            dpEnergy[xIndex][yIndex - 1])
                        xIndex--
                }
                else -> {
                    val minValue = minOf(dpEnergy[xIndex - 1][yIndex - 1], dpEnergy[xIndex][yIndex - 1],
                            dpEnergy[xIndex + 1][yIndex - 1])
                    if (minValue == dpEnergy[xIndex - 1][yIndex - 1])
                        xIndex--
                    else if (minValue != dpEnergy[xIndex][yIndex - 1])
                        xIndex++
                }
            }
            yIndex--
            seamArray[yIndex] = xIndex
        }
        return seamArray
    }

    fun findHorizontalSeam(): IntArray {    // sequence of indices for horizontal seam
        val originalImage = image
        image = image.getTransposed()
        val seam = findVerticalSeam()
        image = originalImage
        return seam
    }
    //endregion

    //region Seam Removal
    fun removeHorizontalSeam(seam: IntArray) {  // remove horizontal seam from picture
        image = image.getTransposed()
        removeVerticalSeam(seam)
        image = image.getTransposed()
    }

    //
    fun removeVerticalSeam(seam: IntArray) {    // remove vertical seam from picture
        val originalImage = image
        val newImage = BufferedImage(width - 1, height, BufferedImage.TYPE_INT_RGB)
        repeat(height) { y ->
            repeat(seam[y]) { x ->
                newImage.setRGB(x, y, originalImage.getRGB(x, y))
            }
            for (x in seam[y] until newImage.width) {
                newImage.setRGB(x, y, originalImage.getRGB(x + 1, y))
            }
        }

        image = newImage
    }
    //endregion
}