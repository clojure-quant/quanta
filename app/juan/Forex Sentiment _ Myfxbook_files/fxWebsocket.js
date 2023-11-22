var socket=jQuery.atmosphere,subSocket,CHANNEL_SUSBSCRIBE=1,CHANNEL_UNSUSBSCRIBE=2,CHANNEL_SEND=3,SOCKET_CLOSE=4,connectinTimeout=3e3,showDisconnectMsg=!1,onOpenArray=[],lastCalendarEventTime=0,fxWebsocketData={},foundSalCookie=null,rand=function(){return Math.random().toString(36).substr(2)},token=function(){return rand()+rand()},TOKEN=token(),reconnectTime=null,reconnectTimeDelay=2e4,tokenEvent=jQuery.Deferred(),channels=(jQuery(this).keypress((function(e){27===e.keyCode&&e.preventDefault()})),{_size:0,_channels:{},put:function(e,n){this._channels[e]=n,this._size++},remove:function(e){delete this._channels[e],this._size--},size:function(){return this._size},getAll:function(){return this._channels},get:function(e){return this._channels[e]}}),fxWebsocket={domain:null,parent:null,child:null,iframeId:null,_getChild:function(){return null==this.child&&(this.child=document.getElementById(this.iframeId).contentWindow),this.child},init:function(){function onMessage(event){if(event.origin===$("#atmosphereUrl").val()||event.origin===$("#serverPath").val())try{eval("fxWebsocket."+event.data)}catch(e){}}fxWebsocket.log("[fxWebsocket init] STARTED"),window.addEventListener?addEventListener("message",onMessage,!1):attachEvent("onmessage",onMessage),null!=fxWebsocket.parent&&fxWebsocket.sendToParent("onChildReady()"),fxWebsocket.log("[fxWebsocket init] DONE")},setChild:function(e,n){this.iframeId=e,this.domain=n,fxWebsocket.init()},setParent:function(e,n){this.parent=e,this.domain=n,fxWebsocket.init()},sendToParent:function(e){this.parent.postMessage(e,fxWebsocket.domain)},sendToChild:function(e){this._getChild().postMessage(e,fxWebsocket.domain)},onChildReady:function(){},onOpen:function(){},onClose:function(){fxWebsocket.log("[fxWebsocket onClose] STARTED");var e=$("#connStatusOn");0<e.length&&(e.hide(),$("#connStatusOff").show(),e.attr("connected","false")),fxWebsocket.log("[fxWebsocket onClose] DONE")},onMessage:function(responseBody){try{for(var decodedData=unescape(responseBody),responseArray=decodedData.split("<@>"),data,i=0;i<responseArray.length;i++){var response=responseArray[i],data;fxWebsocket.log("[fxWebsocket onMessage] received message response: "+response),hasText(response)&&("{"==response[0]&&(data=eval("("+response+")")),null!=data?atmosphereObserverManager.notifyAllObserversByKey(data.callback,data):(data=response.split(","),atmosphereObserverManager.notifyAllObserversByKey(data[0],data)))}}catch(e){}},onReconnect:function(){fxWebsocket.log("[fxWebsocket onReconnect]"),null==reconnectTime&&(reconnectTime=(new Date).getTime()),(new Date).getTime()-reconnectTime>reconnectTimeDelay&&connectionLostMsg(),fxWebsocket.onClose()},onShowToolbar:function(e,n){atmosphereObserverManager.notifyAllObserversByKey(atmosphereCallbackTypes.PARAM_TOGGLE_FOOTER_TOOLBAR,{show:e,token:n})},onSaveEmailMinAlert:function(e,n,t,o,s){atmosphereObserverManager.notifyAllObserversByKey(atmosphereCallbackTypes.PARAM_CALENDAR_SAVE_UPDATE_CALLBACK,{type:e,userTimeFormat:n,token:t,calledBy:o,calPeriod:s})},onGetToken:function(e){tokenEvent.resolve(e),tokenEvent=jQuery.Deferred()},connect:function(){initConnectParams(),fxWebsocket.log("[fxWebsocket connect]"),fxWebsocket.sendToChild("connectChild("+JSON.stringify(fxWebsocketData)+")")},subscribe:function(e,n,t,o){fxWebsocket.log("[fxWebsocket subscribe] "+JSON.stringify({channel:e,id:n,time:t,invite:o})),fxWebsocket.subscribeWithMsg(e,n,t,o,"")},subscribeWithMsg:function(e,n,t,o,s){fxWebsocket.log("[fxWebsocket subscribeWithMsg] "+JSON.stringify({channel:e,id:n,time:t,invite:o,message:s})),fxWebsocket.sendToChild("subscribeChild('"+e+"','"+n+"','"+t+"','"+o+"','"+s+"')")},send:function(e,n,t){fxWebsocket.log("[fxWebsocket send] "+JSON.stringify({channel:e,text:n,id:t})),fxWebsocket.sendToChild("sendChild('"+e+"','"+escape(n)+"','"+t+"')")},sendWithInvite:function(e,n,t,o){fxWebsocket.log("[fxWebsocket sendWithInvite] "+JSON.stringify({channel:e,text:n,id:t,invite:o})),fxWebsocket.sendToChild("sendChildWithInvite('"+e+"','"+escape(n)+"','"+t+"','"+o+"')")},disconnect:function(){fxWebsocket.log("[fxWebsocket disconnect]"),fxWebsocket.sendToChild("disconnectChild()")},updateChartData:function(e,n){fxWebsocket.sendToChild("childUpdateChartData('"+e+"','"+n+"')")},reconnectIe:function(){fxWebsocket.sendToChild("onReconnectIe()")},unsubscribe:function(e,n){fxWebsocket.log("[fxWebsocket unsubscribe] "+JSON.stringify({channel:e,id:n})),fxWebsocket.sendToChild("unSubscribeChild('"+e+"','"+n+"')")},updateLastCalendarEventTime:function(e){fxWebsocket.sendToChild("updateLastCalendarEventTimeChild('"+e+"')")},getTokenForToolbar:function(e){fxWebsocket.sendToChild("getTokenForToolbarChild('"+e+"')")},getTokenForsaveEmailMinAlert:function(e,n,t,o){fxWebsocket.sendToChild("getTokenForsaveEmailMinAlertChild('"+e+"','"+n+"','"+t+"','"+o+"')")},sendChatMsg:function(e,n,t,o,s){fxWebsocket.sendToChild("sendChatMsgChild('"+e+"','"+escape(n)+"','"+t+"','"+o+"','"+s+"')")},getToken:function(){return fxWebsocket.sendToChild("getTokenChild()"),tokenEvent},childUpdateChartData:function(e,n){null!=(e=channels.get(e))&&(e.time=n)},connectChild:function(e){fxWebsocketData=e,jQuery(document).ready((function(){var e={url:$("#atmosphereUrl").val()+"/atmosphere/websocket?isModern=true&url="+encodeParameter(gup("url")+"&host="+fxWebsocketData.host),transport:"streaming",trackMessageLength:!0,messageDelimiter:"<@>",reconnectInterval:3e4,contentType:"text/html; charset=utf-8",onReconnect:function(e){TOKEN=token(),fxWebsocket.sendToParent("onReconnect()")},onMessage:function(e){jQuery.atmosphere.connected&&200==e.status&&fxWebsocket.sendToParent("onMessage('"+escape(e.responseBody)+"')")},onOpen:function(e){jQuery.atmosphere.connected&&fxWebsocket.sendToParent("onOpen()")},onClose:function(e){TOKEN=token(),jQuery.browser.msie?fxWebsocket.sendToParent("onReconnect()"):fxWebsocket.sendToParent("onClose()")}};subSocket=socket.subscribe(e)}))},subscribeChild:function(channel,id,time,invite,message){time=eval(time);var channelObject={channel,id,time,invite};channels.put(channel+id,channelObject),fxWebsocket.push(CHANNEL_SUSBSCRIBE,channel,message,id,time,invite)},unSubscribeChild:function(e,n){channels.remove(e+n),fxWebsocket.push(CHANNEL_UNSUSBSCRIBE,e,"",n,null,null)},updateLastCalendarEventTimeChild:function(e){lastCalendarEventTime=e},getTokenForToolbarChild:function(e){fxWebsocket.sendToParent("onShowToolbar('"+e+"','"+TOKEN+"')")},getTokenForsaveEmailMinAlertChild:function(e,n,t,o){fxWebsocket.sendToParent("onSaveEmailMinAlert('"+e+"','"+n+"','"+TOKEN+"','"+t+"','"+o+"')")},getTokenChild:function(){fxWebsocket.sendToParent("onGetToken('"+TOKEN+"')")},sendToChart:function(e,n){hasText($("#userId").val())&&this.send("chart",JSON.stringify(n),e)},sendChild:function(e,n,t){fxWebsocket.push(CHANNEL_SEND,e,unescape(n),t,null,null),jQuery("#text").val(""),jQuery("#text").focus()},sendChildWithInvite:function(e,n,t,o){fxWebsocket.push(CHANNEL_SEND,e,unescape(n),t,null,o)},sendChatMsgChild:function(e,n,t,o,s){fxWebsocket.pushChatMsg(CHANNEL_SEND,e,unescape(n),t,o,s)},onReconnectIe:function(){jQuery.atmosphere.connected=!1,fxWebsocket.sendToParent("onClose()"),fxWebsocket.connectChild(),fxWebsocket.sendToParent("onOpen()")},push:function(e,n,t,o,s,i){fxWebsocket.log("[fxWebsocket push] "+JSON.stringify({type:e,channel:n,message:t,id:o,time:s,invite:i}));var c={};null!=s&&0!=s&&(c.l=s),this.initAndPushBaseParams(c,e,n,t,o,i)},disconnectChild:function(){fxWebsocket.push(SOCKET_CLOSE,"",""),socket.unsubscribe()},pushChatMsg:function(e,n,t,o,s,i){var c={};null!=i&&hasText(i)&&(c.ty=i),this.initAndPushBaseParams(c,e,n,t,o,s)},initAndPushBaseParams:function(e,n,t,o,s,i){e.token=TOKEN,e.t=n,e.c=encodeParameter(t),e.m=o,e.id=s,null!=i&&hasText(i)&&(e.i=i),null!=subSocket&&subSocket.push({data:JSON.stringify(e)})}},atmosphereIsConnected=!1;function encodeParameter(e){return encodeURI(e).replace(/&/g,"%26").replace(/=/g,"%3D").replace(/\+/g,"%2B").replace(/#/g,"%23").replace(/\?/g,"%3F").replace(/\:/g,"%3A")}function getAtmosphereParams(){var e,n="";for(e in fxWebsocketData)n+="&"+e+"="+fxWebsocketData[e];return n}function initConnectParams(){var e=$("#footerToolbar").is(":visible"),n=[];jQuery("#newsTop a[newsId]").each((function(){n.push($(this).attr("newsId"))})),fxWebsocketData.t=e,fxWebsocketData.newsOids=encodeParameter(n),fxWebsocketData.host=encodeParameter(window.location.hostname),e=(e=$("#userId").val())||0,fxWebsocketData.userId=e}function gup(e,n){return e=e.replace(/[\[]/,"\\[").replace(/[\]]/,"\\]"),e=new RegExp("[\\?&]"+e+"=([^&#]*)"),null==(e=null==n?e.exec(window.location.href):e.exec(n))?"":e[1]}function hasText(e){try{if(null!=e)return 0!=(e=(e=String(e)).trim()).length}catch(e){}return!1}fxWebsocket.onChildReady=function(){fxWebsocket.connect()},fxWebsocket.onOpen=function(){atmosphereIsConnected=!0,fxWebsocket.log("[fxWebsocket onOpen] STARTED with onOpenArray with length "+onOpenArray.length);for(var i=0;i<onOpenArray.length;i++)"resetOnReConnect"===onOpenArray[i]?atmosphereObserverManager.notifyAllObserversByKey(atmosphereCallbackTypes.CHART_RECONNECT_CALLBACK):eval(onOpenArray[i]);fxWebsocket.log("[fxWebsocket onOpen] DONE")},fxWebsocket.log=function(e){var n;(foundSalCookie=null===foundSalCookie&&hasText(n=document.cookie)?n.split(";")?.some((e=>!(!(e=e.split("="))||2!==e.length)&&"sal"===e[0].trim())):foundSalCookie)&&jQuery.atmosphere.debug(e)};