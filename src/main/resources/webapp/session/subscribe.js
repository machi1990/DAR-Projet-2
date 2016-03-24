$(function() {
	var form = $("#subscribe"),
		connectForm = $("#connect");
	
	form.submit(function(event) {
		event.preventDefault();
		var serialize = form.serialize();
    	$.ajax({
    		  method: "POST",
    		  url: "/session/subscribe?"+serialize,
    		}).done(function( msg ) {
    		    
    	}).fail(function(err) {
    		
    	});
	});
	
	connectForm.submit(function(event) {
		event.preventDefault();
		var data = new Object();
		data['username'] = $(this).find("#username").val();
		data['password'] = $(this).find("#password").val();
		
		console.log($(this).find("#username").val());
		
    	$.ajax({
    		  method: "POST",
    		  url: "/session/connect",
    		  data: JSON.stringify(data)
    		}).done(function( msg ) {
    		    
    	}).fail(function(err) {
    		
    	});
	});
});