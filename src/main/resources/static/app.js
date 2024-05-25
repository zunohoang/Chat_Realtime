if(localStorage.getItem("token") == null) {
    window.location.href = "login.html";
}


document.getElementsByClassName("username_now")[0].innerHTML = localStorage.getItem("token");

var stompClient = null;
var listUserOnline = [
    {
        username: "NguoiDungMau",
        messages: [
            {
                type: 0,
                content: "Hello"
            },
            {
                type: 1,
                content: "How are you?"
            }
        ]
    }
];
var toUser = null;
function connectWs() {
    const sock = new SockJS('/ws');
    stompClient = Stomp.over(sock);
    stompClient.connect({
        Authorization: "Bearer " + localStorage.getItem("token")
    }, function (frame) {
        console.log('Connected: ' + frame);
        stompClient.subscribe('/user/topic/message', function (response) {
            let data = JSON.parse(response.body);
            let message = data.fromUser + ": " + data.content;
            console.log(message);
            let indexUser = listUserOnline.findIndex(user => user.username === data.fromUser);
            listUserOnline[indexUser].messages.push({
                type: 0,
                content: data.content
            });
            renderChat();
        });
        stompClient.subscribe('/user/topic/onlineUsers', function (response) {
            listUserOnline = [];
            data = JSON.parse(response.body);
            data.forEach(element => {
                listUserOnline.push({
                    username: element,
                    messages: []
                });
            });
            renderUserOnline();
        });
        stompClient.subscribe("/topic/online", function (response) {
            let data = response.body;
            listUserOnline.push({
                username: data,
                messages: []
            });
            console.log(data);
            renderUserOnline();
        });
        stompClient.subscribe("/topic/offline", function (response) {
            let data = response.body;
            listUserOnline = listUserOnline.filter(user => user.username !== data);
            renderUserOnline();
        });
        getOnlineUsers();
    });
}

function renderUserOnline() {
    document.getElementsByClassName("list_user_content")[0].innerHTML = "";
    for (let i = 0; i < listUserOnline.length; i++) {
        if (listUserOnline[i].username == localStorage.getItem("token")) continue;
        document.getElementsByClassName("list_user_content")[0].innerHTML += `<div class="user" id="${i}" onclick="setToUser('${listUserOnline[i].username}')">
        <img src="https://www.w3schools.com/howto/img_avatar.png" alt="Avatar" class="avatar">
        <p>${listUserOnline[i].username}</p>
        <div class="status_color_on"></div>
    </div>`;
    };
}


function renderChat() {
    if (toUser == null) return;
    let chat = document.getElementsByClassName("chat")[0];
    let username_room = document.getElementsByClassName("username_room")[0];
    username_room.innerHTML = toUser;
    let chat_message = chat.getElementsByClassName("chat_message")[0];
    chat_message.innerHTML = "";
    let indexUser = listUserOnline.findIndex(user => user.username === toUser);
    let userData = listUserOnline[indexUser];
    let typeMessage = [toUser, "Bạn"]
    let left_right = ["autoright", "autoleft"];
    userData.messages.forEach(message => {
        chat_message.innerHTML += `<div class="message ${left_right[message.type]}">
        <div class="message_content">
            <b>${typeMessage[message.type]} :</b>
            <p>${message.content}</p>
        </div>
    </div>`;
    });
    chat_message.scrollTop = chat_message.scrollHeight;
}

function sendMessage() {
    if (toUser == null) {
        alert("Khong xac dinh duoc nguoi gui");
        return;
    }
    let message = document.getElementById("chat_send_message").value;
    let data = {
        fromUser: null,
        toUser: toUser,
        content: message
    };
    stompClient.send("/app/chat", {}, JSON.stringify(data));
    let indexUser = listUserOnline.findIndex(user => user.username === toUser);
    listUserOnline[indexUser].messages.push({
        type: 1,
        content: message
    });
    let left_right = ["autoright", "autoleft"];
    let chat_message = document.getElementsByClassName("chat_message")[0];
    chat_message.innerHTML += `<div class="message  ${left_right[1]}">
    <div class="message_content">
        <b>Bạn :</b>
        <p>${message}</p>
    </div>
</div>`;
    chat_message.scrollTop = chat_message.scrollHeight;
}

function setToUser(user) {
    toUser = user;
    renderChat();
}

function getOnlineUsers() {
    stompClient.send("/app/getOnlineUsers", {}, {});
}

connectWs();

function logout() {
    localStorage.removeItem("token");
    window.location.href = "login.html";
}