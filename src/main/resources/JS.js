$(document).ready(function() {
    $.fn.initProgress = function() {
        alert('You have successfully defined the function!');
    }
    $('#pBar')
        .progress({
            text: {
                active  : 'calculating seam {value} of {total} seams',
                success : '{total} Photos Uploaded!'
            }
        })
    ;
    $(".call-btn").click(function(){
        $.fn.myFunction();
    });
});

const readUploadedFileAsText = (inputFile) => {
    const temporaryFileReader = new FileReaderSync();

    return new Promise((resolve, reject) => {
        temporaryFileReader.onerror = () => {
            temporaryFileReader.abort();
            reject(new DOMException("Problem parsing input file."));
        };

        temporaryFileReader.onload = () => {
            resolve(temporaryFileReader.result);
        };
        temporaryFileReader.readAsDataURL(inputFile);
    });
};
const handleUpload = async (er) => {
    const file = er.files[0];

    try {
        const fileContents = await readUploadedFileAsText(file)
        return fileContents
        //console.log(fileContents);
    } catch (e) {
        console.warn(e.message)
        return "Nothing"
    }
}
let getData = function (er) {
    const file = er.files[0];
    const reader = new FileReaderSync();
    return reader.readAsDataURL(file)
}