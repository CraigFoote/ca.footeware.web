function deleteJoke(id) {
	if (confirm("Are you sure you want to delete this joke?")) {
		$.ajax({
			url: window.location.protocol + "//" + window.location.hostname + ":" + window.location.port + '/jokes/' + id,
			type: 'DELETE',
			success: function(result) {
				window.location.href = window.location.protocol + "//" + window.location.hostname + ":" + window.location.port + "/jokes";
			},
			error: function(request, msg, error, status) {
				alert(msg);
				console.log("request=" + request + ", msg=" + msg, "error=" + error, " status=" + status);
			}
		});
	}
}

function updateJoke() {
	var form = window.document.getElementById("editJokeForm");
	const formData = new FormData(form);
	var href = window.location.protocol + "//" + window.location.hostname + ":" + window.location.port + "/editjoke";
	$.ajax({
		url: href,
		type: "PUT",
		data: formData,
		processData: false,
		contentType: "application/json",
		success: function(result) {
			console.log("result=" + result);
			window.location.href = window.location.protocol + "//" + window.location.hostname + ":" + window.location.port + "/jokes";
		},
		fail: function(result) {
			console.log("failed result=" + result);
		},
		error: function(xhr, status, error) {
			console.log("An error occured: " + xhr.status + " " + xhr.statusText);
			console.log("status=" + status + ", error=" + error);
		}
	});
}