/* 
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

var webSocket;
var serverUrl = "ws://localhost:8080/OWSCallServer/call/"; 
var from = "";
var callId = "";
var vid1, vid2;

//-----------------------------------------------------------------------------
var configuration = {
	"iceServers" : [ {
		"url" : "stun:stun.l.google.com:19302"
	} ]
};
var stream;
var pc = new webkitRTCPeerConnection(configuration);
var connected = false;

var mediaConstraints = {
'	mandatory' : {
		'OfferToReceiveAudio' : true,
		'OfferToReceiveVideo' : true
	}
};


pc.onicecandidate = function(e) {
	if (e.candidate) {
            console.log('send received candidate');
		webSocket.send(JSON.stringify({
			action : 'received_candidate',
			message : {
                                to : from,
				label : e.candidate.sdpMLineIndex,
				id : e.candidate.sdpMid,
				candidate : e.candidate.candidate
			}
		}));
	}
};

pc.onaddstream = function(e) {
	console.log('start remote video stream');
	vid2.src = URL.createObjectURL(e.stream);
	vid2.play();
};


function broadcast(isStart) {
	// gets local video stream and renders to vid1
	navigator.webkitGetUserMedia({
		audio : true,
		video : true
	}, function(s) {
		stream = s;
		pc.addStream(s);
		vid1.src = URL.createObjectURL(s);
		vid1.play();
		// initCall is set in views/index and is based on if there is another
		// person in the room to connect to
		if (isStart)
			start();
	}, function(error) {
		try {
			console.error(error);
		} catch (e) {
		}
	});
}

function start() {
    console.log('send received offer');
	pc.createOffer(function(description) {
		pc.setLocalDescription(description);
		webSocket.send(JSON.stringify({
			action : 'received_offer',
			message : {
                            to: from,
                            data: description
                        }
		}));
	}, null, mediaConstraints);
}

//------------------------------------------------------------------------------------
$(document).ready(function() {
    vid1 = document.getElementById('vid1');
    vid2 = document.getElementById('vid2');

    var name = window.location.href.slice(window.location.href.indexOf('=') + 1) ;
    openConnection(name);
    
    
    $('#btnCall').click(function() {
        call($('#username').val());
    })
    
    $('#btnAccept').click(function() {
        accept();
    })
    
    $('#btnReject').click(function() {
        reject();
    })
    
    $('#btnEnd').click(function() {
        endCall();
    })
    
})


function openConnection(name) {
    if (webSocket !== undefined && webSocket.readyState !== WebSocket.CLOSED) {
        console.log("WebSocket is already opened.");
        return;
    }
    // Create a new instance of the websocket
    webSocket = new WebSocket(serverUrl + name);
    webSocket.onmessage = onMessageReceived;
}

function call(to) {
    var msg = '{' +
                '"action": "call",' +
                '"message": {' +
                  '"to": "' + to + '"' +
                '}' +
              '}';
    $("#btnCall").attr("disabled",true);
    $("#btnEnd").attr("disabled",false);
    webSocket.send(msg);
    console.log('broadcast false');
    broadcast(false);
}

function onMessageReceived(evt) {
    console.log(evt.data);
    var msg = JSON.parse(evt.data);
    switch (msg.action) {
        case "calling" : 
            //Neu co cuoc goi thi
            // - Bat nhac chuong
            from = msg.message.from;
            $("#btnAccept").attr("disabled",false);
            $("#btnReject").attr("disabled",false);
            break;
        case "accepted" :
            callId = msg.message.call_id;
            break;
        case "ended" :
            $("#btnCall").attr("disabled",false);
            $("#btnEnd").attr("disabled",true);
            break;
        case "rejected" :
            $("#btnCall").attr("disabled",false);
            $("#btnEnd").attr("disabled",true);
            break;
        case "busy" :
            $("#btnCall").attr("disabled",false);
            $("#btnEnd").attr("disabled",true);
            break;
        case 'received_offer':
		console.log('received offer', msg);
		pc.setRemoteDescription(new RTCSessionDescription(msg.message.data));
		pc.createAnswer(function(description) {
			console.log('sending answer');
			pc.setLocalDescription(description);
			webSocket.send(JSON.stringify({
				action : 'received_answer',
				message : {
                                    to: $('#username').val(),
                                    data: description
                                }
			}));
		}, null, mediaConstraints);
		break;
	case 'received_answer':
		console.log('received answer');
		if (!connected) {
			pc.setRemoteDescription(new RTCSessionDescription(msg.message.data));
			connected = true;
		}
		break;
	case 'received_candidate':
		console.log('received candidate');
		var candidate = new RTCIceCandidate({
			sdpMLineIndex : msg.message.label,
			candidate : msg.message.candidate
		});
		pc.addIceCandidate(candidate);
		break;

    }
    
}

function reject() {
    var msg = '{' +
                '"action": "reject_call",' +
                '"message": {' +
                  '"to": "' + from + '",' +
                  '"time": "' + new Date() + '"' +
                '}' +
              '}';
    $("#btnAccept").attr("disabled",true);
    $("#btnReject").attr("disabled",true);
    webSocket.send(msg);
}

function accept() {
    var msg = '{' +
                '"action": "accept_call",' +
                '"message": {' +
                  '"to": "' + from + '",' +
                  '"time": "' + new Date() + '"' +
                '}' +
              '}';
    $("#btnAccept").attr("disabled",true);
    $("#btnReject").attr("disabled",true);
    $("#btnCall").attr("disabled",true);
    $("#btnEnd").attr("disabled",false);
    webSocket.send(msg);
    console.log('broadcast true');
    
    broadcast(true);
}

function endCall() {
    console.log('end call');
    var msg = '{' +
                '"action": "end_call",' +
                '"message": {' +
                  '"call_id": "' + callId + '",' +
                  '"time": "' + new Date() + '"' +
                '}' +
              '}';
    webSocket.send(msg);
}