const USER_KEY = "user", TOK_KEY = "tok";
let baseUrl = "";
let rejectCodeList = [400, 401, 500, 403];
function getBaseUrl() {
    return baseUrl;
}
function ajax(method, url, data, hdrs, cancelToken) {
    return new Promise((resolve, reject) => {
        var xhttp = new XMLHttpRequest();
        if(cancelToken) {
            cancelToken.cancel = function() {
                xhttp.abort();
                reject(new Error("Cancelled"));
            };
        }
        xhttp.onreadystatechange = function () {
            if (this.readyState == 4 && this.status == 200) {
                resolve({
                    response: this.responseText,
                    headers: makeHMap(xhttp.getAllResponseHeaders())
                })
            }
            if (this.readyState == 4 && rejectCodeList.includes(this.status)) {
                reject({message: JSON.parse(this.responseText).message, code: this.status});
            }
        };
        xhttp.onerror = function() {
            reject({message: JSON.parse(this.responseText).message, code: this.status});
        }
        xhttp.open(method, `${baseUrl}${url}`, true);
        hdrs && Object.keys(hdrs).forEach(key => xhttp.setRequestHeader(key, hdrs[key]))
        xhttp.send(data);
    });
}
function makeHMap(headers) {
    var arr = headers.trim().split(/[\r\n]+/);
    var headerMap = {};
    arr.forEach(function (line) {
        var parts = line.split(': ');
        var header = parts.shift();
        var value = parts.join(': ');
        headerMap[header] = value;
    });
    return headerMap;
}

function get(url, token) {
    return ajax("GET", url, undefined, {}, token);
}

function last(fn) {
    var lastToken = { cancel: function(){} }; // start with no op
    return function() {
        lastToken.cancel();
        var args = Array.prototype.slice.call(arguments);
        args.push(lastToken);
        return fn.apply(this, args);
    };
}

let getLast = last(get);

function post(url, data, hdrs) {
    return ajax('POST', url, JSON.stringify(data), hdrs);
}
function postFile(url, data, hdrs) {
    return ajax('POST', url, data, hdrs);
}
function delet(url) {
    return ajax('DELETE', url);
}
function setCookie(name, value, days) {
    var expires = "";
    if (days) {
        var date = new Date();
        date.setTime(date.getTime() + (days * 24 * 60 * 60 * 1000));
        expires = "; expires=" + date.toUTCString();
    }
    document.cookie = name + "=" + (value || "") + expires + "; path=/";
}
function getCookie(name) {
    var nameEQ = name + "=";
    var ca = document.cookie.split(';');
    for (var i = 0; i < ca.length; i++) {
        var c = ca[i];
        while (c.charAt(0) == ' ') c = c.substring(1, c.length);
        if (c.indexOf(nameEQ) == 0) return c.substring(nameEQ.length, c.length);
    }
    return null;
}
function eraseCookie(name) {
    document.cookie = name + '=; Max-Age=-99999999;';
}
function g(id) {
    return document.getElementById(id);
}
function c(cl) {
    return document.getElementsByClassName(cl)[0];
}
function getCurrentUser() {
    return getCookie(USER_KEY)
}

let __colors = {
    ERR : { background: '#FF504A', color: 'white', border: 'red' },
    WARN: { background: 'lightorange', color: 'white', border: 'orange' },
    SUCCESS: { background: '#3BB07B', color: 'white', border: 'darkgreen' },
    INFO : {background: '#24326B', color: 'white', border: 'darkblue'}
}

function showError(x) {
    showMessage(x, __colors.ERR)
}

function showSuccess(x) {
    showMessage(x, __colors.SUCCESS)
}

function showWarning(x) {
    showMessage(x, __colors.WARN)
}

function showMessage(x, col) {
    let d = document.createElement('div')
    d.classList = 'fade-out'
    d.style = `position: fixed; right: 10px; top: 10px; background-color: ${col.background}; border: solid 2px ${col.border}; border-radius: 3px; padding: 10px; color: ${col.color}; font-size: 1em`
    d.innerText = x
    document.body.appendChild(d)
    setTimeout(() => d.remove(), 8000)
}
