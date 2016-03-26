$(function () {
    "use strict";

    // for better performance - to avoid searching in DOM
    var content = $('#content'),
    	input = $('#input'),
    	status = $('#status'),
    	home = $('#home'),
    	logout = $("#logout"),
    	scrollbar = $('body > section:first').tinyscrollbar();
        

    const myColor = "red",senderColor = "green",
    	myName = localStorage.getItem("username");

    input.focus();
    
    function slideScrollbar() {
        scrollbar.update();
        scrollbar.move(Math.max(0, content.find('> p').length) * 20);
    }
    
    function gotoHome() {
    	window.location.href = "/demo/home.html";
    }
    
    function update() {
    	$.ajax({
    		  method: "GET",
    		  url: "/demo/list/messages",
    		  success: function(data, textStatus, xhr) {
		        if (xhr.status == 200) {
		        	content.html("");
		        	for (var i in data) {
		        		var item = data[i];
		        		addMessage(item['postedBy'],item['content'],(item['postedBy'] == myName?myColor:senderColor),new Date(item['postedAt']));
		        	}
		        	slideScrollbar();
		        } else {
				    gotoHome();
		        }
    		   }
    		}).fail(function(err) {
    			gotoHome();
    	});
    }
    
    update();
    var interval = setInterval(function(){ update() }, 4000);
    
    /**
     * Send mesage when user presses Enter key
     */
    
    function sendMessage(message) {
    	$.ajax({
  		  method: "POST",
  		  url: "/demo/message",
  		  data: JSON.stringify(message),
  		  success: function(data, textStatus, xhr) {
  		        
  		    }
  		}).fail(function(err) {
  			gotoHome();
  	});
    }
    
    input.keydown(function(e) {
    	if (e.keyCode === 13) {
            var msg = $(this).val(),
            	data = new Object();
            
            data['content'] = msg;
            data['postedBy'] = myName;
            
            addMessage(myName,msg,myColor,new Date());
            sendMessage(data);
            input.focus();
            $(this).val('');
        }
    });    
    
    /**
     * Add message to the chat window
     */
    function addMessage(author, message, color, datetime) {
        content.append('<p><span style="color:' + color + '">' + author + '</span> @ ' +
                      + (datetime.getHours() < 10 ? '0' + datetime.getHours() : datetime.getHours()) + ':'
                      + (datetime.getMinutes() < 10 ? '0' + datetime.getMinutes() : datetime.getMinutes())
                      + ': ' + message + '</p>');
        content.emoticonize();
    }
    
    home.click(function(event) {
    	clearInterval(interval);
    	window.location = "/demo/home.html";
    });
    
    logout.click(function(event) {
    	clearInterval(interval);
    	localStorage.removeItem("username");
    	localStorage.removeItem("password");
    	window.location = "/demo/logout";
    });
    
});




