sap.ui.define(['jquery.sap.global', '{app.idpath}/ui/test/tools/actions/GenericPress'], function ($, GenericPress) {
	"use strict";

	/**
	 * The Press action is used to simulate a press interaction on a Control's dom ref.
	 * @class
	 * @extends sap.ui.test.actions.GenericPress
	 * @public
	 * @name sap.ui.test.actions.PressOnDatePickerYearSelector
	 */
	var PressOnDatePickerYearSelector = GenericPress.extend("{app.id}.test.tools.actions.PressOnDatePickerYearSelector", {

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
		 * @public
		 */
		executeOn : function (oControl) {
			this.genericExecuteOn(oControl, "button.sapUiCalHeadBLast");
		}
	});
	return PressOnDatePickerYearSelector;

});
