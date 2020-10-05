import kweb.html.BodyElement
import kweb.plugins.jqueryCore.executeOnSelf

val showImage: String = """function previewFile() {
  const preview = document.getElementById("imageFile");
  const file = document.getElementById("openFile").files[0];
  const reader = new FileReader();

  reader.addEventListener("load", function () {
    preview.src = reader.result;
  }, false);

  if (file) {
    reader.readAsDataURL(file);
  }
}"""
val initProgress = """${'$'}(document).ready(function() {
    ${'$'}.fn.initProgress = function() {
        alert('You have successfully defined the function!');
    }
    ${'$'}('#pBar')
        .progress({
            text: {
                active  : 'calculating seam {value} of {total} seams',
                success : '{total} Photos Uploaded!'
            }
        })
    ;
    ${'$'}(".call-btn").click(function(){
        ${'$'}.fn.myFunction();
    });
});"""
fun showToast(bodyElement: BodyElement, message: String) {
    val str = """${'$'}('body').toast({class: 'success',message: `${message}`});"""
    bodyElement.execute(str)
}















