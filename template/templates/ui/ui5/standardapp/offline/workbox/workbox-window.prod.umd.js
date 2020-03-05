!function(n,t){"object"==typeof exports&&"undefined"!=typeof module?t(exports):"function"==typeof define&&define.amd?define(["exports"],t):t((n=n||self).workbox={})}(this,function(n){"use strict";try{self["workbox:window:5.0.0-beta.1"]&&_()}catch(n){}function t(n,t){return new Promise(function(e){var r=new MessageChannel;r.port1.onmessage=function(n){e(n.data)},n.postMessage(t,[r.port2])})}function e(n,t){for(var e=0;e<t.length;e++){var r=t[e];r.enumerable=r.enumerable||!1,r.configurable=!0,"value"in r&&(r.writable=!0),Object.defineProperty(n,r.key,r)}}try{self["workbox:core:5.0.0-rc.0"]&&_()}catch(n){}var r=function(){var n=this;this.promise=new Promise(function(t,e){n.resolve=t,n.reject=e})};function i(n,t){var e=location.href;return new URL(n,e).href===new URL(t,e).href}var o=function(n,t){this.type=n,Object.assign(this,t)};function u(n,t,e){return e?t?t(n):n:(n&&n.then||(n=Promise.resolve(n)),t?n.then(t):n)}var a=200;function c(n){return function(){for(var t=[],e=0;e<arguments.length;e++)t[e]=arguments[e];try{return Promise.resolve(n.apply(this,t))}catch(n){return Promise.reject(n)}}}var f=6e4;function s(){}var v=function(n){var s,v;function l(t,e){var s;return void 0===e&&(e={}),(s=n.call(this)||this).t={},s.i=0,s.o=new r,s.u=new r,s.s=new r,s.v=0,s.h=new Set,s.l=function(){var n=s.g,t=n.installing;s.i>0||!i(t.scriptURL,s.m)||performance.now()>s.v+f?(s.p=t,n.removeEventListener("updatefound",s.l)):(s.P=t,s.h.add(t),s.o.resolve(t)),++s.i,t.addEventListener("statechange",s.j)},s.j=function(n){var t=s.g,e=n.target,r=e.state,i=e===s.p,u=i?"external":"",c={sw:e,originalEvent:n};!i&&s.k&&(c.isUpdate=!0),s.dispatchEvent(new o(u+r,c)),"installed"===r?s.O=self.setTimeout(function(){"installed"===r&&t.waiting===e&&s.dispatchEvent(new o(u+"waiting",c))},a):"activating"===r&&(clearTimeout(s.O),i||s.u.resolve(e))},s._=function(n){var t=s.P;t===navigator.serviceWorker.controller&&(s.dispatchEvent(new o("controlling",{sw:t,originalEvent:n,isUpdate:s.k})),s.s.resolve(t))},s.L=c(function(n){var t=n.data,e=n.source;return u(s.getSW(),function(){s.h.has(e)&&s.dispatchEvent(new o("message",{data:t,sw:e,originalEvent:n}))})}),s.m=t,s.t=e,navigator.serviceWorker.addEventListener("message",s.L),s}v=n,(s=l).prototype=Object.create(v.prototype),s.prototype.constructor=s,s.__proto__=v;var d,w,g,m=l.prototype;return m.register=c(function(n){var t=this,e=(void 0===n?{}:n).immediate,r=void 0!==e&&e;return function(n,t){var e=n();if(e&&e.then)return e.then(t);return t(e)}(function(){if(!r&&"complete"!==document.readyState)return h(new Promise(function(n){return addEventListener("load",n)}))},function(){return t.k=Boolean(navigator.serviceWorker.controller),t.M=t.R(),u(t.S(),function(n){t.g=n,t.M&&(t.P=t.M,t.u.resolve(t.M),t.s.resolve(t.M),t.M.addEventListener("statechange",t.j,{once:!0}));var e=t.g.waiting;return e&&i(e.scriptURL,t.m)&&(t.P=e,Promise.resolve().then(function(){t.dispatchEvent(new o("waiting",{sw:e,wasWaitingBeforeRegister:!0}))}).then(function(){})),t.P&&(t.o.resolve(t.P),t.h.add(t.P)),t.g.addEventListener("updatefound",t.l),navigator.serviceWorker.addEventListener("controllerchange",t._,{once:!0}),t.g})})}),m.update=c(function(){if(this.g)return h(this.g.update())}),m.getSW=c(function(){return void 0!==this.P?this.P:this.o.promise}),m.messageSW=c(function(n){return u(this.getSW(),function(e){return t(e,n)})}),m.R=function(){var n=navigator.serviceWorker.controller;return n&&i(n.scriptURL,this.m)?n:void 0},m.S=c(function(){var n=this;return function(n,t){try{var e=n()}catch(n){return t(n)}if(e&&e.then)return e.then(void 0,t);return e}(function(){return u(navigator.serviceWorker.register(n.m,n.t),function(t){return n.v=performance.now(),t})},function(n){throw n})}),d=l,(w=[{key:"active",get:function(){return this.u.promise}},{key:"controlling",get:function(){return this.s.promise}}])&&e(d.prototype,w),g&&e(d,g),l}(function(){function n(){this.U=new Map}var t=n.prototype;return t.addEventListener=function(n,t){this.B(n).add(t)},t.removeEventListener=function(n,t){this.B(n).delete(t)},t.dispatchEvent=function(n){n.target=this;var t=this.B(n.type),e=Array.isArray(t),r=0;for(t=e?t:t[Symbol.iterator]();;){var i;if(e){if(r>=t.length)break;i=t[r++]}else{if((r=t.next()).done)break;i=r.value}i(n)}},t.B=function(n){return this.U.has(n)||this.U.set(n,new Set),this.U.get(n)},n}());function h(n,t){if(!t)return n&&n.then?n.then(s):Promise.resolve()}n.Workbox=v,n.messageSW=t,Object.defineProperty(n,"__esModule",{value:!0})});
//# sourceMappingURL=workbox-window.prod.umd.js.map
