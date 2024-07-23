let ws;
function connectWebSocket() {
    ws = new WebSocket("ws://" + window.location.host + "/ws");

    ws.onopen = function() {
        console.log("Connected to the chat server.");
    };

    ws.onmessage = function(event) {
        const chatMessage = document.getElementById("chatMessages");
        const chatElement = document.createElement("p");
        chatElement.innerText = event.data;
        chatMessage.appendChild(chatElement);
        chatMessage.scrollTop = chatMessage.scrollHeight;
    };

    ws.onclose = function(e) {
        console.log('Socket is closed. Reconnect will be attempted in 1 second.', e.reason);
        setTimeout(function() {
            connectWebSocket();
        }, 1000);
    };

    ws.onerror = function(err) {
        console.error('Socket encountered error: ', err.message, 'Closing socket');
        ws.close();
    };
}

window.onload = function() {
    connectWebSocket();
    document.getElementById("message").focus();
};
