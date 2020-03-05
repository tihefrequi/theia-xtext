sap.ui.define(['jquery.sap.global', '{app.idpath}/ui/test/tools/actions/GenericPress'], function ($, GenericPress) {
	"use strict";

	/**
	 * The Press action is used to simulate a press interaction on a Control's dom ref.
	 * @class
	 * @extends sap.ui.test.actions.GenericPress
	 * @public
	 * @name sap.ui.test.actions.PressOnDatePickerMonth
	 */
	var PressOnDatePickerMonth = GenericPress.extend("{app.id}.test.tools.actions.PressOnDatePickerMonth", {

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
		 * @param int iMonth the month to pick from 1-12
		 * @public
		 */
		executeOn : function (oControl, iMonth) {
			this.genericExecuteOn(oControl, "div.sapUiCalItem", iMonth-1);
		}
	});
	return PressOnDatePickerMonth;

});
