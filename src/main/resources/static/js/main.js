'use strict';


var messageForm = document.querySelector('#messageForm');
var messageInput = document.querySelector('#message');
var messageArea = document.querySelector('#messageArea');
var connectingElement = document.querySelector('#connecting');

var stompClient = null;
var username = null;


function connect() {
    username = document.querySelector('#username').innerText.trim();

    var socket = new SockJS('http://localhost:8080/jponline');
    stompClient = Stomp.over(socket);

    stompClient.connect({"user" : userId}, onConnected, onError);


}

// Connect to WebSocket Server.
if (stompClient === null ) {
    connect();
}


function onConnected() {
    //subcriber to the notification
    stompClient.subscribe('/topic/notification', function (payload) {
        var noti = JSON.parse(payload.body);
        var dhtml = '';
        dhtml += '<input type="radio" name="user" value="ALL" checked="checked">All Members<br>';
        $.each(noti.data, function( i, v ){
            if (userId != v) {
                dhtml += '<input type="radio" name="user" value="' + v + '">' + i + '<br>';
            }
        });
        $('#onlines').html(dhtml);
    });

    // Subscribe to the Public Topic
    stompClient.subscribe('/topic/publicChatRoom', onMessageReceived);

    // Tell your username to the server
    stompClient.send("/app/chat.addUser",
        {},
        JSON.stringify({sender: username, type: 'JOIN'})
    );

    //Subscribe and listen to the event is sent to
    stompClient.subscribe('/user/queue/reply', function(payload) {
        var message = JSON.parse(payload.body);
        var messageElement = document.createElement('li');
        if (message.type === 'FILE') {
            messageElement.classList.add('chat-message');
            var usernameElement = document.createElement('strong');
            usernameElement.classList.add('nickname');
            var usernameText = document.createTextNode(message.sender);
            var fnameElement = document.createElement('a');
            fnameElement.href = "http://" + window.location.host + "/download?pathFile=" + message.fpath
            var filename = document.createTextNode(message.fname);
            var textElement = document.createElement('span');
            var messageText = document.createTextNode(message.content);
            textElement.appendChild(messageText);
            usernameElement.appendChild(usernameText);
            fnameElement.appendChild(filename);
            messageElement.appendChild(usernameElement);
            messageElement.appendChild(textElement);
            messageElement.appendChild(fnameElement);

        } else {
            messageElement.classList.add('chat-message');
            var usernameElement = document.createElement('strong');
            usernameElement.classList.add('nickname');
            var usernameText = document.createTextNode(message.sender);
            var usernameText = document.createTextNode(message.sender);
            usernameElement.appendChild(usernameText);
            messageElement.appendChild(usernameElement);
            var textElement = document.createElement('span');
            var messageText = document.createTextNode(message.content);
            textElement.appendChild(messageText);

            messageElement.appendChild(textElement);
        }

        messageArea.appendChild(messageElement);
    });

    connectingElement.classList.add('hidden');
}


function onError(error) {
    connectingElement.textContent = 'Could not connect to WebSocket server. Please refresh this page to try again!';
    connectingElement.style.color = 'red';
}


function uploadFileWS(file){
    console.log('WSClient Sending '+file.name);
    var uploadSessionId = getUniqueSessionId(file.name);
    var ws = new WebSocket('ws://'+window.location.host+'/binary?uploadSessionId='+uploadSessionId);
    ws.onmessage = function(response){
        console.log('WSClient',response);
        if(stompClient) {
            var chatMessage = {
                sender: username,
                content: 'send a file',
                fname: file.name,
                fpath: response.data,
                type: 'FILE'
            };
            //broadcast message to all
            stompClient.send("/app/chat.sendMessage", {}, JSON.stringify(chatMessage));
        }
        ws.close();

    };

    ws.onopen = function() {
        ws.send(file);
        console.log('WSClient Finished sending '+file.name);
    };
}

function uploadFileSingleWS(file){
    console.log('WSClient Sending '+file.name);
    var uploadSessionId = getUniqueSessionId(file.name);
    var ws = new WebSocket('ws://'+window.location.host+'/binary?uploadSessionId='+uploadSessionId);
    ws.onmessage = function(response){
        console.log('WSClient',response);
        if(stompClient) {
            //broadcast to user
            stompClient.send("/app/sendTo", {}, JSON.stringify({
                name: $("input:radio[name ='user']:checked").val(),
                toUser: $("input:radio[name ='user']:checked").val(),
                sender: username,
                fname: file.name,
                fpath: response.data,
                content: 'send a file',
                type: 'FILE'
            }));
            //broadcast your self
            stompClient.send("/app/sendTo", {}, JSON.stringify({
                name: userId,
                toUser: userId,
                sender: username,
                fname: file.name,
                fpath: response.data,
                content: '',
                type: 'FILE'
            }));
        }
        ws.close();

    };

    ws.onopen = function() {
        ws.send(file);
        console.log('WSClient Finished sending '+file.name);
    };
}


function uploadBinaryWS(files) {
    var file = files[0];
    if($("input:radio[name ='user']:checked").val() === 'ALL') {
        uploadFileWS(file);
    } else {
        uploadFileSingleWS(file);
    }
}


function onMessageReceived(payload) {
    var message = JSON.parse(payload.body);

        var messageElement = document.createElement('li');
        if (message.type === 'JOIN') {
            if (message.fist) {
                messageElement.classList.add('event-message');
                message.content = message.sender + ' joined!';
                var messageText = document.createTextNode(message.content);
                messageElement.appendChild(messageText);
                messageArea.appendChild(messageElement);
                messageArea.scrollTop = messageArea.scrollHeight;
            }

        } else if (message.type === 'LEAVE') {
            messageElement.classList.add('event-message');
            message.content = message.sender + ' left!';
            var messageText = document.createTextNode(message.content);
            messageElement.appendChild(messageText);
            messageArea.appendChild(messageElement);
            messageArea.scrollTop = messageArea.scrollHeight;
        } else if (message.type === 'FILE') {
            messageElement.classList.add('chat-message');
            var usernameElement = document.createElement('strong');
            usernameElement.classList.add('nickname');
            var usernameText = document.createTextNode(message.sender);
            var fnameElement = document.createElement('a');
            fnameElement.href = "http://" + window.location.host + "/download?pathFile=" + message.fpath
            var filename = document.createTextNode(message.fname);
            var textElement = document.createElement('span');
            var messageText = document.createTextNode(message.content);
            textElement.appendChild(messageText);
            usernameElement.appendChild(usernameText);
            fnameElement.appendChild(filename);
            messageElement.appendChild(usernameElement);
            messageElement.appendChild(textElement);
            messageElement.appendChild(fnameElement);
            messageArea.appendChild(messageElement);
            messageArea.scrollTop = messageArea.scrollHeight;
        } else {
            messageElement.classList.add('chat-message');
            var usernameElement = document.createElement('strong');
            usernameElement.classList.add('nickname');
            var usernameText = document.createTextNode(message.sender);
            var usernameText = document.createTextNode(message.sender);
            usernameElement.appendChild(usernameText);
            messageElement.appendChild(usernameElement);
            var textElement = document.createElement('span');
            var messageText = document.createTextNode(message.content);
            textElement.appendChild(messageText);

            messageElement.appendChild(textElement);
            messageArea.appendChild(messageElement);
            messageArea.scrollTop = messageArea.scrollHeight;
        }


}

function sendMessage(event) {
    if($("input:radio[name ='user']:checked").val() === 'ALL') {
        var messageContent = messageInput.value.trim();
        if (messageContent && stompClient) {
            var chatMessage = {
                sender: username,
                content: messageInput.value,
                type: 'CHAT'
            };
            stompClient.send("/app/chat.sendMessage", {}, JSON.stringify(chatMessage));
            messageInput.value = '';
        }
    } else {
        var messageContent = messageInput.value.trim();
        if (messageContent && stompClient) {
            //broadcast to user
            stompClient.send("/app/sendTo", {}, JSON.stringify({
                name: $("input:radio[name ='user']:checked").val(),
                toUser: $("input:radio[name ='user']:checked").val(),
                sender: username,
                content: messageContent,
                type: 'CHAT'
            }));

            //broadcast your self
            stompClient.send("/app/sendTo", {}, JSON.stringify({
                name: userId,
                toUser: userId,
                sender: username,
                content: messageContent,
                type: 'CHAT'
            }));

            messageInput.value = '';
        }

    }
    event.preventDefault();
}



messageForm.addEventListener('submit', sendMessage, true);

//generate uuid
function guid() {
    function s4() {
        return Math.floor((1 + Math.random()) * 0x10000)
            .toString(16)
            .substring(1);
    }
    return s4() + s4() + '-' + s4() + '-' + s4() + '-' +
        s4() + '-' + s4() + s4() + s4();
}
//get Unique SessionId
function getUniqueSessionId(additionalValue) {
    return btoa(guid()+'\\'+additionalValue);
}

