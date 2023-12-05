console.log("BONGO IS HERE")

        function logURL(requestDetails) {
          console.log("Loading: " + requestDetails.url);
          console.log("documentUrl: " + requestDetails.documentUrl);
          console.log("method: " + requestDetails.method);
          console.log("formData: " + requestDetails.requestBody.formData);
          window.xxx = requestDetails;
          window.bongo = requestDetails.requestBody.formData;
          console.log(JSON.stringify(requestDetails));
          if (requestDetails.requestBody.raw != null) {
            console.log("we got raw data!")
            //var postedString = decodeURIComponent(String.fromCharCode.apply(null,   new Uint8Array(requestDetails.requestBody.raw[0].bytes)));
            //console.log(postedString);
          }
          var details = requestDetails;
          if(details.requestBody && details.requestBody.raw){
                          for(var i = 0; i < details.requestBody.raw.length; ++i){
                              if(details.requestBody.raw[i].file){
                                  console.log("FILE");
                                  console.log(details.requestBody.raw[i].file);
                              }
                              if(details.requestBody.raw[i].bytes){
                                  console.log("RAW");
                                  console.log(details.requestBody.raw[i].bytes.byteLength);
                                  var dv = new DataView(details.requestBody.raw[i].bytes);
                                  var result = "";
                                  for(var j = 0; j < dv.byteLength; ++j){
                                      result += (String.fromCharCode( dv.getInt8(j) ));
                                  }
                                  console.log(result);
                              }
                          }
                      }



          return redirect(requestDetails);
        }

        function redirect(requestDetails) {
          console.log("Redirecting: " + requestDetails.url);
          return { redirectUrl: "https://quant.hoertlehner.com/dump" };
        }

        chrome.webRequest.onBeforeRequest.addListener(logURL,
          {urls: ["https://www.tradingview.com/savechart/"]},
          ["requestBody"]);

        /*browser.webRequest.onCompleted.addListener(
          logURL,  {urls: ["https://www.tradingview.com/savechart/"
                  // "<all_urls>"
                 ]
          }//,
          // ["requestBody"]
        );*/

        // chrome.cookies.getAll