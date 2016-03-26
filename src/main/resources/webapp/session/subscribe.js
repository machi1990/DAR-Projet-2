$(function() {
	var form = $("#subscribe"),
		connectForm = $("#connect");
	
	function gotoIndexPage(xhr) {
		if (xhr.status == 200) {
        	window.location.href = "/session/index";
        }
	}
	
	form.submit(function(event) {
		event.preventDefault();
		var serialize = form.serialize();
    	$.ajax({
    		  method: "POST",
    		  url: "/session/subscribe?"+serialize,
    		  success: function(data, textStatus, xhr) {
    		        gotoIndexPage(xhr);
    		    }
    		}).fail(function(err) {
    	});
	});
	
	connectForm.submit(function(event) {
		event.preventDefault();
		var data = new Object();
		data['username'] = $(this).find("#username").val();
		data['password'] = $(this).find("#password").val();
		
    	$.ajax({
    		  method: "POST",
    		  url: "/session/connect",
    		  data: JSON.stringify(data),
    		  success: function(data, textStatus, xhr) {
    		       gotoIndexPage(xhr);
    		    }
    		}).fail(function(err) {
    	});
	});
});