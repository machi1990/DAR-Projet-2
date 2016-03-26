$(function () {
    "use strict";
    var toChatRoom = $("#to_chatroom"),
    	form = $("#subscribe"),
    	connectForm = $("#connect");

		function gotoChatPage(xhr) {
			if (xhr.status == 200) {
		    	window.location.href = "/demo/chat.html";
		    } else {
		    	window.location.href = "/demo/home.html";
		    }
		}
		
		function login(data) {
			$.ajax({
	    		  method: "POST",
	    		  url: "/demo/login",
	    		  data: JSON.stringify(data),
	    		  success: function(msg, textStatus, xhr) {
    			  	localStorage.setItem("username",data['username']);
    			  	localStorage.setItem("password",data['password']);
	    		    gotoChatPage(xhr);
	    		    }
	    		}).fail(function(err,textStatus, xhr) {
	    			gotoChatPage(xhr);
	    	});
		}
		
		connectForm.submit(function(event) {
			event.preventDefault();
			var data = new Object();
			data['username'] = $(this).find("#username").val();
			data['password'] = $(this).find("#password").val();
			login(data);
		});
		
		toChatRoom.click(function() {
			var username = localStorage.getItem("username"),
				password = localStorage.getItem("password"),
				data = new Object();
			
				data['username'] = username;
				data['password'] = password;
				
				console.log(data);
				
				login(data);
		});
		
		var image = $("#image"),
			signup = $("#signup"),
			content = "";
		
		image.val("");
		
		image.change(function(event) {
			content = "";
			signup.prop('disabled', true);
			var reader = new FileReader();
			reader.onload = function(event) {
			    content = "data:image/png;base64," + btoa(event.target.result);
			    signup.prop('disabled', false);
			};

			reader.onerror = function(event) {
			    console.error("File could not be read! Code " + event.target.error.code);
			};

			reader.readAsBinaryString(event.currentTarget.files[0]);
		});
		
		form.submit(function(event) {
			event.preventDefault();
			var dataArray = form.serializeArray(),
				data = new Object();
			
			for (var i in dataArray) {
			    data[dataArray[i].name] = dataArray[i].value;
			}

			data['image'] = content;
			image.val("");
			
	    	$.ajax({
	    		  method: "POST",
	    		  url: "/demo/signup",
	    		  data: JSON.stringify(data),
	    		  success: function(msg, textStatus, xhr) {
	    				localStorage.setItem("username",data['username']);
	    			  	localStorage.setItem("password",data['password']);
	    		        gotoChatPage(xhr);
	    		    }
	    		}).fail(function(err,textStatus, xhr) {
	    			gotoChatPage(xhr);
	    	});
		});
		
});




