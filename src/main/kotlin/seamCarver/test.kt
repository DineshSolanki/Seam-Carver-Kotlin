package seamCarver

import kweb.*
import kweb.state.KVar

fun main() {
    Kweb(port = 16098, debug = true)
    {
        doc.body.new {
            val counter = KVar(1)
            val imageString = counter.map { "$it.img" }
            img().setAttribute("src", imageString)
//                    .setAttributeRaw("id","imgFile")
            //input(type = InputType.number).setAttributeRaw("id","inp").setAttribute("max", imageString) //any element after this are not rendered.
            //input(type = InputType.number).setAttributeRaw("id","inp") //elements will be rendered
            input(type = InputType.number).let {
                it.setAttributeRaw("id","inp")
                it.setAttribute("max", imageString)
            } //Elements will be rendered but KVar is somehow not binded
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