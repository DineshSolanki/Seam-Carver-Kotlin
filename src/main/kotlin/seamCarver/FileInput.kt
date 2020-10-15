package seamCarver

import com.github.salomonbrys.kotson.fromJson
import kweb.*
import kweb.plugins.fomanticUI.fomantic
import kweb.util.gson
import kweb.util.random
import kotlin.math.abs

class FileUpload(
        val fileName: String,
        val fileSize: String,
        val base64Content: String
)

class FileFormInput {

    private lateinit var _inputElement: Element

    val inputElement: Element
        get() = _inputElement


    fun setInputElement(e: Element) {
        this._inputElement = e
    }

    fun setAccept(acceptedTypes: String): Unit = inputElement.execute("${inputElement.jsExpression}.accept=\"$acceptedTypes\";")
    fun isMultiSelect(isMultiple: Boolean): Unit = inputElement.execute("${inputElement.jsExpression}.multiple=\"$isMultiple\";")
    fun onFileSelect(onFileSelectCallback: () -> Unit) {
        inputElement.on.change { evt ->
            logger.info(evt.retrieved)
            onFileSelectCallback()
        }
    }

    fun retrieveFile(onFileRetrieveCallback: (FileUpload) -> Unit) {
        val callbackId = abs(random.nextInt())

        val js = """
                let fd = document.getElementById("${inputElement.id}").files[0]
                let fr = new FileReader()
                fr.readAsDataURL(fd)
                fr.onload = function(){
                    callbackWs($callbackId,{base64Content: fr.result, fileSize: fd.size, fileName: fd.name});
                }
            """.trimIndent()

        inputElement.browser.executeWithCallback(js, callbackId) { result ->
            logger.info("Result is $result")
            onFileRetrieveCallback(gson.fromJson<FileUpload>(result.toString()))
        }
        inputElement.creator?.onCleanup(true) {
            inputElement.browser.removeCallback(callbackId)
        }
    }
}


fun ElementCreator<*>.fileInput(name: String? = null, initialValue: String? = null, size: Int? = null, placeholder: String? = null, attributes: Map<String, Any> = attr): FileFormInput {
    val inputElement = input(InputType.file, name, initialValue, size, placeholder, attributes)
    val formInput = FileFormInput()
    formInput.setInputElement(inputElement)
    return formInput
}

fun main(args: Array<String>) {
    Kweb(port = 16097, buildPage = {
        doc.body.new {
            div(fomantic.ui.input).new {
                val input = fileInput()
                input.onFileSelect {
                    input.retrieveFile {
                        logger.info("Received file " + it.fileName)
                        logger.info("File content Base 64: " + it.base64Content)
                    }
                }
            }

        }
    })
}