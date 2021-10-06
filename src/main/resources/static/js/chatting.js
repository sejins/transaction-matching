$('#messageButton').on('click', function () {
    var form = $('#form').serialize()
    $.ajax({
        url: "new-message",
        data: form,
        type: "POST",
        cache: false,
        success: function (data) {
            $('#list').replaceWith(data)
        }
    })
});

