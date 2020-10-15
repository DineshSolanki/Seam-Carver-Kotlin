package seamCarver

import kweb.*
import kweb.state.KVar
import org.w3c.dom.events.Event

fun main() {
    Kweb(port = 16098, debug = true)
    {
        doc.body.new {
            input(InputType.file).let {
                it.setAttributeRaw("onChange", "previewFile(this);")
                it.setAccept(FileTypes.Audio.toString())
                it.multiple(true)
            }
            val counter = KVar(1)
            val imageString = counter.map { "$it.img" }
            img().setAttribute("src", imageString)
                   .setAttributeRaw("id","imgFile")
            input(type = InputType.number).setAttribute("max", imageString).setAttributeRaw("id","inp") //any element after this are not rendered.
            //input(type = InputType.number).setAttributeRaw("id","inp") //elements will be rendered
//            input(type = InputType.number).let {
//                it.setAttributeRaw("id", "inp")
//                it.setAttribute("max", imageString)
//            } //Elements will be rendered but KVar is somehow not binded
            button().let { button ->
                button.text("Increment")
                button.on.click {
                    counter.value++
                }
            }
            button().text("click me")
        }
    }
}