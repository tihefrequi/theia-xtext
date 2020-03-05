sap.ui.define(['jquery.sap.global', 'sap/ui/test/actions/Action'], function ($, Action) {
  'use strict'

	/**
	 * The Press action is used to simulate a press interaction on a Control's dom ref.
	 *
	 * @class
	 * @extends sap.ui.test.actions.Action
	 * @public
	 * @name sap.ui.test.actions.GenericPress
	 */
  var GenericPress = Action.extend('{app.id}.test.tools.actions.GenericPress', {

    metadata: {
      publicMethods: [ 'genericExecuteOn' ]
    },

    init: function () {
      Action.prototype.init.apply(this, arguments)
      this.controlAdapters = $.extend(this.controlAdapters)
    },

		/**
		 * Sets focus on given control and triggers a 'tap' event on it (which is
		 * internally translated into a 'press' event).
		 * Logs an error if control is not visible (i.e. has no dom representation)
		 *
		 * @param {sap.ui.core.Control} oControl the control on which the 'press' event is triggered
		 * @param sSelector JQuery selector for the find method of JQuery
		 * @param iSelectedElementIndex index of the given JQuery find-selector, has to be >=0
		 * @public
		 */
    genericExecuteOn: function (oControl, sSelector, iSelectedElementIndex) {
      var $ActionDomRef = oControl.$().find(sSelector),
			    oActionDomRef
      if (typeof (iSelectedElementIndex) !== 'undefined') {
        if ($ActionDomRef.length > iSelectedElementIndex) {
          oActionDomRef = $ActionDomRef[iSelectedElementIndex]
        } else {
					// TODO error
        }
      } else {
        oActionDomRef = $ActionDomRef[0]
      }
      if ($ActionDomRef.length) {
        $.sap.log.debug('Pressed the control ' + oControl, this._sLogPrefix)
        this._tryOrSimulateFocusin($ActionDomRef, oControl)

				// the missing events like saptouchstart and tap will be fired by the event simulation
        this._createAndDispatchMouseEvent('mousedown', oActionDomRef)
        this.getUtils().triggerEvent('selectstart', oActionDomRef)
        this._createAndDispatchMouseEvent('mouseup', oActionDomRef)
        this._createAndDispatchMouseEvent('click', oActionDomRef)
				// Focusout simulation removed in order to fix Press action behavior
				// since in real scenario manual press action does not fire focusout event
      }
    }
  })
  return GenericPress
})
