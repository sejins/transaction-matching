$('#messageButton').on('click', function () {
    var form = $('#form').serialize()
    $.ajax({
        url: "new-message",
        data: form,
        type: "POST",
        cache: false,
        success: function (data) {
            $('#list').replaceWith(data)
            let input = document.getElementById("message");
            input.value = '';
            let listDiv = document.getElementById("list");
            listDiv.scrollTop = listDiv.scrollHeight;
        }
    })
});

$('#list').scrollTop($('#list')[0].scrollHeight)

