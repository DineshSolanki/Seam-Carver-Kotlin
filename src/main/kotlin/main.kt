import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.future.await
import kotlinx.coroutines.launch
import kweb.*
import kweb.html.BodyElement
import kweb.plugins.fomanticUI.fomantic
import kweb.plugins.fomanticUI.fomanticUIPlugin
import kweb.state.KVar
import seamCarver.*
import java.awt.image.BufferedImage

var image: BufferedImage? = null
var initialImage: BufferedImage? = null
var imgElement: ImageElement? = null
var imageString = KVar("")
var reduceWidth = KVar("0")
var reduceHeight = KVar("0")
var seamCarver: SeamCarver? = null
var maxWidth: KVar<Int> = KVar(0)
var maxHeight: KVar<Int> = KVar(0)
var loader: DivElement? = null
var loaderText = KVar("Preparing")
var calledBySetImage = false
var bodyElement: BodyElement? = null
val counter = KVar(1)
//var isSeamVisible =KVar("false")
fun main(args: Array<String>) {
    val titleText = KVar("Seam Carver Kotlin")
    initPage(titleText)
}

private fun initPage(titleText: KVar<String>) {

    Kweb(port = 16097, debug = true, plugins = listOf(fomanticUIPlugin))
    {
        doc.head.new {
            // Not required, but recommended by HTML spec
            meta(name = "Description", content = "Seam carver technology")
            title().text = titleText
        }
        doc.head.new { element("script").text(showImage)
        element("script").text(initProgress)}
//        doc.head.new {
//            element("script", mapOf(
//                    "src" to  "resources/JS.js"))
//        }
        bodyElement = doc.body
        bodyElement!!.new {
            val fileInput = input(InputType.file)
            fileInput.setAttributeRaw("onChange", "previewFile();")
            fileInput.setAttributeRaw("hidden", true).setAttributeRaw("id", "openFile")
            div(fomantic.ui.placeholder.segment).new {
                div(fomantic.ui.two.column.very.relaxed.grid).new {
                    div(fomantic.ui.column).new {
                        div(fomantic.ui.secondary.vertical.menu).new {
                            div(fomantic.item).new {
                                label("openFile").new {
                                    div(fomantic.ui.primary.basic.button).text("Open")
                                }
                            }
                            div(fomantic.item).new {
                                div(fomantic.ui.primary.basic.button).text("Save")
                            }
                            div(fomantic.item).new {
                                div(fomantic.ui.labeled.input).new {
                                    div(fomantic.ui.label).text("Reduce Width by: ")
                                    val inp = input(type = InputType.number)
                                    inp.setAttributeRaw("min", "0")
                                    inp.setAttribute("max", maxWidth)
                                    inp.value = reduceWidth
                                }
                            }
                            div(fomantic.item).new {
                                div(fomantic.ui.labeled.input).new {
                                    div(fomantic.ui.label).text("Reduce Height by: ")
                                    val inp = input(type = InputType.number)
                                    inp.setAttributeRaw("min", "0")
                                    inp.setAttribute("max", maxHeight)
                                    inp.value = reduceHeight
                                }
                            }
                            div(fomantic.item).new {
                                div(fomantic.ui.primary.basic.button).text("Show Seam").on.click { onShowClick() }
                            }
                            div(fomantic.item).new {
                                div(fomantic.ui.primary.basic.button).text("Remove Seam").on.click {
                                    onRemoveClick()
                                }
                            }
                            div(fomantic.item).new {
                                div(fomantic.ui.primary.basic.button).text("Reset Image").on.click {
                                    onResetClick()
                                }
                            }
                        }
                    }
                    div(fomantic.ui.column).new {
                        div(fomantic.ui.large.image).new {
                            imgElement = img()
                            val ir =counter.map { "$it.img" }
                            imgElement!!.setAttributeRaw("id","imgfile")
                            imgElement!!.setAttribute("src", ir)
//                            imgElement!!.setAttribute("src", ir)
//                                    .setAttributeRaw("id", "imageFile")
//                                    .on.load {
//                                        getImage()
//                                    }
                            button().let { button ->
                                button.text("Increment")
                                button.on.click {
                                    counter.value++
                                }}
                        }
                    }
                }
                div(fomantic.ui.vertical.divider).text("Image")
                loader = div(fomantic.ui.disabled.dimmer)
                loader!!.new {
                    div(fomantic.ui.text.loader).text = loaderText
                }

            }

        }
    }
}

fun getImage() {
    GlobalScope.launch {
        //imageString.value = imgElement!!.read.attribute("src").await().toString()
        image = decodeToImage(imageString.value)
        maxWidth.value = image!!.width
        maxHeight.value = image!!.height
        if (!calledBySetImage) {
            initialImage = image!!.deepCopy()
        }
        calledBySetImage = false
    }

}

fun setImage(pic: BufferedImage, type: String) {
    calledBySetImage = true
    imageString.value = encodeToString(pic, type)!!
    //imgElement!!.execute("document.getElementById('imageFile').src='${imageString.value}'")

}

fun onShowClick() {
    processSeamCarving(Operations.HighlightSeams, useLoader = false)
}

private fun processSeamCarving(op: Operations, useLoader: Boolean) {
    getImage()
    if (image != null) {
        if (useLoader)
            loader!!.setClasses("ui active dimmer")
        seamCarver = SeamCarver(image!!)
        repeat(reduceWidth.value.toInt()) {
            val seam = seamCarver!!.findVerticalSeam()
            println("Calculated vertical seam ->${it + 1}")
            when (op) {
                Operations.HighlightSeams -> {
                    seamCarver!!.highlightVerticalPixels(seam)
                    loaderText.value = "Highlighted vertical seam ->${it + 1}"
                }
                else -> {
                    seamCarver!!.removeVerticalSeam(seam)
                    loaderText.value = "Removed vertical seam ->${it + 1}"
                }
            }
            setImage(seamCarver!!.image, "PNG")
        }
        repeat(reduceHeight.value.toInt()) {
            //updateImageFrame(jFrame, obj.image.getJLabel()!!)
            val seam = seamCarver!!.findHorizontalSeam()
            println("Calculated horizontal seam ->${it + 1}")
            when (op) {
                Operations.HighlightSeams -> {
                    seamCarver!!.highlightHorizontalPixels(seam)
                    loaderText.value = "Highlighted horizontal seam ->${it + 1}"
                }
                else -> {
                    seamCarver!!.removeHorizontalSeam(seam)
                    loaderText.value = "Removed horizontal seam ->${it + 1}"
                    setImage(seamCarver!!.image, "PNG")
                }
            }
            setImage(seamCarver!!.image, "PNG")
        }
        showToast(bodyElement!!, "Process finished !")
        loaderText.value = "Completed!"
//        setImage(seamCarver!!.image, "PNG")
        loader!!.setClasses("ui disabled dimmer")
    }
}

fun onResetClick() {
    setImage(initialImage!!, "PNG")
}

fun onRemoveClick() {
    processSeamCarving(Operations.RemoveSeams,false)
}
/*fun getImage(fileInput: ImageElement) {
    GlobalScope.launch {
//        val fileChooser = JFileChooser()
//        val jFrame =JFrame()
//        val result = fileChooser.showOpenDialog(jFrame)

        val r=fileInput.read.attribute("src").await().toString()
        image=decodeToImage(r)
        maxWidth.value = image!!.width
        maxHeight.value = image!!.height
        imageString.value = encodeToString(ImageIO.read(File("""C:\Users\Drona\IdeaProjects\Seam Carving\Seam Carving\task\test\blue-seam.png""")),"PNG")!!
        fileInput.setAttributeRaw("src",imageString.value)
        println(r)

    }
}*/