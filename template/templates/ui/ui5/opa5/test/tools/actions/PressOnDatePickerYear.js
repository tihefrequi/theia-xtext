sap.ui.define(['jquery.sap.global', '{app.idpath}/ui/test/tools/actions/GenericPress'], function ($, GenericPress) {
	"use strict";

	/**
	 * The Press action is used to simulate a press interaction on a Control's dom ref.
	 * @class
	 * @extends sap.ui.test.actions.GenericPress
	 * @public
	 * @name sap.ui.test.actions.PressOnDatePickerYear
	 */
	var PressOnDatePickerYear = GenericPress.extend("{app.id}.test.tools.actions.PressOnDatePickerYear", {

		metadata : {
			publicMethods : [ "executeOn" ]
		},

		init: function () {
			GenericPress.prototype.init.apply(this, arguments);
			this.controlAdapters = $.extend(this.controlAdapters);
		},
		/**
		 * Sets focus on given control and triggers a 'tap' event on it (which is
		 * internally translated into a 'press' event).
		 * Logs an error if control is not visible (i.e. has no dom representation)
		 *
		 * @param {sap.ui.core.Control} oControl the control on which the 'press' event is triggered
		 * @param int iYear the year to pick. Valid Values are from the current selected year-10 to current selected year+9 e.g. for 2018 only 2008-2027 are allowed/possible
		 * @public
		 */
		executeOn : function (oControl, iYear) {
			var iCurrentSelectedYear=oControl.$().find("div[aria-selected=true]")[0].innerText;
			//The position of the current selected year is 10
			this.genericExecuteOn(oControl, "div.sapUiCalItem", iYear-iCurrentSelectedYear+10);
		}
	});
	return PressOnDatePickerYear;

});
