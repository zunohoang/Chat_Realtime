

var stompClient = null;
function connectWs() {
    const sock = new SockJS('http://localhost:8080/ws');
    stompClient = Stomp.over(sock);
    stompClient.connect({
        Authorization: "Bearer " + localStorage.getItem("token")
    }, function (frame) {
        console.log('Connected: ' + frame);
        stompClient.subscribe('/user/topic', function (response) {
            let data = JSON.parse(response.body);
            console.log(data);
            let message = data.fromUser + ": " + data.content;
            document.getElementsByClassName("show_ws")[0].innerHTML += "<p>" + message + "</p>";
        });
    });
}

function sendWs() {
    let toUser = document.getElementById("to").value;
    let message = document.getElementById("message").value;
    let data = {
        fromUser: null,
        toUser: toUser,
        content: message
    };
    stompClient.send("/app/chat", {}, JSON.stringify(data));
}