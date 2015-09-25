/* 
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

function OwsRTC(config) {

    if (OwsRTC.instance === null) {
        OwsRTC.instance = this;
    } else {
        return OwsRTC.instance;
    }
    
    this.signaling = new WebSocket(config.wsURL);

    this.init = function() {
        
    };
    
    this.init();
};

OwsRTC.instance = null;

OwsRTC.onReady = function () {
    console.log('It is highly recommended to override method NextRTC.onReady');
};

if (document.addEventListener) {
    document.addEventListener('DOMContentLoaded', function () {
        OwsRTC.onReady();
    });
}

var error = function (error) {
    console.log('error ' + JSON.stringify(error));
};

var success = function (success) {
    console.log('success ' + JSON.stringify(success));
};