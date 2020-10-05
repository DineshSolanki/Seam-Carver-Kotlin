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