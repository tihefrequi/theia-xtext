/* eslint no-undef : "off" */
// create storm namespace if missed
if (typeof(storm) === "undefined") {
	window.storm = {};
}

/**
 * General Utils
 *
 * @returns Package of Methods for commonly needed methods for UI5 Apps
 */
storm.utils = function() {

	// Private variables
	var _aImageFileTypes  = ["image/png", "image/jpeg", "image/gif"];
	
	return {
		
		/**
		 * Adds a Query Parameter to a given URL
		 *
		 * @param {String} sURL Base Url
		 * @param {String} sQueryID Query Parameter Name
		 * @param {String} sQueryParam Query Parameter Value
		 * @returns {String} combined given base url with parameters values
		 */
		addQueryParam : function(sURL, sQueryID, sQueryParam) {
			if(sURL !== null && sURL !== undefined){
				if(!sURL.includes("?")){
					return sURL + "?" + sQueryID + "=" + sQueryParam;
				}else{
					return sURL + "&" + sQueryID + "=" + sQueryParam;
				}
			}
			return sURL;
		},
		
		/**
		 * Is an Image of known Type (png/jpeg,gif,...)
		 *
		 * @param {String} sMimeType MimeType e.g. image/png
		 * @returns {Boolean} true if given type exists in Image File Types list, so is a image
		 */
		isImage : function(sMimeType) {
			if(sMimeType && _aImageFileTypes.indexOf(sMimeType) > -1) {
				return true;
			}
			return false;
		},
		
		/**
		 * transform to the relative url if full-qualified
		 * (removes the hostname and port part of an url)
		 *
		 * @param {String} sDocumentUrl fully qualified url
		 * @returns {String} the stripped relative url
         */
		toRelativeUrl : function(sDocumentUrl) {
			if (sDocumentUrl) {
				return sDocumentUrl.replace(/^[a-z]{4,5}\:\/{2}[\w.]{1,}\:[0-9]{1,4}(.*)/, "$1");
			} else {
				return sDocumentUrl;
			}
		},
	
		/**
		 * Convert Date to human readable String 2018-07-13
		 * 
		 * @param {Date} d any Date
		 * @returns {String} the date formatted in 9999-99-99 (year-month-day)
		 */
		formatDateShort : function(d) {
			if (d === null) {
				return d;
			}
			return d.getFullYear() + "-" + ("0"+(d.getMonth()+1)).slice(-2) + "-" + ("0" + d.getDate()).slice(-2) ;
		},

		/**
		 * Convert DateTime to human readable String 2018-07-13 09:46
		 * 
		 * @param {Date} d any Date
		 * @returns {String} the date formatted in 9999-99-99 99:99 (year-month-day HH:MM)
		 */
		formatDateTimeShort : function(d) {
			if (d === null) {
				return d;
			}
			return d.getFullYear() + "-" + ("0"+(d.getMonth()+1)).slice(-2) + "-" + ("0" + d.getDate()).slice(-2) +
		     " " + ("0" + d.getHours()).slice(-2) + ":" + ("0" + d.getMinutes()).slice(-2);
		},
		
		/**
		 * Formats string like MessageFormat in Java language with {0] and {1}
		 *
		 * @param {String} s String can contain {0} ... placeholders
		 * @param {Array} opt For every place in the placeholder, we should have an entry in this array, which gets replaced
		 * @returns {String} the formatted string
		 */
		formatString: function(s, opt){
		    var i = opt.length, result = s;

		    while (i--) {
		        result = result.replace(new RegExp('\\{' + i + '\\}', 'gm'), opt[i]);
		    }
		    return result;
		},
		
		/**
		 * Check if objects are equals (object key order is not important, and values have to be strictly equals, so 1 not equals "1")
		 * Taken from https://stackoverflow.com/questions/201183/how-to-determine-equality-for-two-javascript-objects/16788517#16788517
		 *
		 * @param {Object} x any Object
		 * @param {Object} y any Object
		 * @returns {Boolean} if x and y are deeply equal
		 */
		objectEquals : function(x, y) {
			'use strict';

			if (x === null || x === undefined || y === null || y === undefined) { return x === y; }
			// after this just checking type of one would be enough
			if (x.constructor !== y.constructor) { return false; }
			// if they are functions, they should exactly refer to same one (because of closures)
			if (x instanceof Function) { return x === y; }
			// if they are regexps, they should exactly refer to same one (it is hard to better equality check on current ES)
			if (x instanceof RegExp) { return x === y; }
			if (x === y || x.valueOf() === y.valueOf()) { return true; }
			if (Array.isArray(x) && x.length !== y.length) { return false; }

			// if they are dates, they must had equal valueOf
			if (x instanceof Date) { return false; }

			// if they are strictly equal, they both need to be object at least
			if (!(x instanceof Object)) { return false; }
			if (!(y instanceof Object)) { return false; }

			// recursive object equality check
			var p = Object.keys(x);
			return Object.keys(y).every(function (i) { return p.indexOf(i) !== -1; }) &&
				p.every(function (i) { return storm.utils.objectEquals(x[i], y[i]); });
		},

		/**
		 * Check if objects are equals (object key order is not important, but values can be the javascript == equals)
		 *
		 * @param {Object} x any Object
		 * @param {Object} y any Object
		 * @returns {Boolean} if x and y are deeply equal but only compared with == equals (instead ===)
		 */
		objectEqualsNotStrict : function(x, y) {
			'use strict';
			

			if (x === null || x === undefined || y === null || y === undefined) { return x === y; }
			// ---- START - HERE IS THE DIFFERENCE == instead of === and put at front of all statements ---- 
			/* eslint eqeqeq : "off" */
			if (x == y || x.valueOf() === y.valueOf()) { return true; }
			// ---- END - HERE IS THE DIFFERENCE == instead of === and put at front of all statements ---- 
			// after this just checking type of one would be enough
			if (x.constructor !== y.constructor) { return false; }
			// if they are functions, they should exactly refer to same one (because of closures)
			if (x instanceof Function) { return x === y; }
			// if they are regexps, they should exactly refer to same one (it is hard to better equality check on current ES)
			if (x instanceof RegExp) { return x === y; }
			if (Array.isArray(x) && x.length !== y.length) { return false; }

			// if they are dates, they must had equal valueOf
			if (x instanceof Date) { return false; }

			// if they are strictly equal, they both need to be object at least
			if (!(x instanceof Object)) { return false; }
			if (!(y instanceof Object)) { return false; }

			// recursive object equality check
			var p = Object.keys(x);
			return Object.keys(y).every(function (i) { return p.indexOf(i) !== -1; }) &&
				p.every(function (i) { return storm.utils.objectEqualsNotStrict(x[i], y[i]); });
		}

	}
}();
