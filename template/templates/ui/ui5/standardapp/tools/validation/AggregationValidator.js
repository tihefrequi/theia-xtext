// Provides the base implementation for aggregation validator
sap.ui.define([
	'sap/ui/base/Object',
	'sap/ui/model/ValidateException'
], function(BaseObject, ValidateException) {

	"use strict";

	/**
	 * Constructor for a new Aggregation Validator.
	 * 
	 * @class Validator for the all types of aggregations
	 * 
	 * @extends sap.ui.base.Object
	 * 
	 * @author storm
	 * 
	 * @constructor
	 * @param {object}
	 *            [oModel] oModel as provided by concrete subclasses
	 * @param {string}
	 *            [sResourceModelId] Resource Model as provided by concrete subclasses, fallback to "i18n"
	 * @public
	 * @alias {app.namespace}.tools.validation.AggregationValidator
	 */
	var AggregationValidator = BaseObject.extend("{app.namespace}.tools.validation.AggregationValidator", /** @lends sap.ui.base.BaseObject.prototype */
	{

		/**
		 * Constructor
		 * 
		 * @param {object}
		 *            [oConstraints] Constraints for the validation
		 * @param {string}
		 *            [sResourceModelId] Resource Model as provided by concrete subclasses, fallback to "i18n"
		 */
		constructor: function(oConstraints, sResourceModelId) {
			BaseObject.apply(this, arguments);
			this.sName = "AggregationValidator";
			this.oConstraints = oConstraints;
			this.oBundle = sap.ui.getCore().getModel(sResourceModelId || "{app.id}.i18n.i18n").getResourceBundle();			

		},

		metadata: {
			publicMethods: [
			// methods
				"validate"]
		}

	});

	/**
	 * Validate whether a given value in model representation is valid and meets the defined constraints (if any).
	 * 
	 * @function
	 * @name {app.namespace}.tools.validation.ModelValidator.validateValue
	 *
	 * @param {aAggregation} aAggregation UI5 Aggregation
	 * @returns {Boolean} true if validation is successfull otherwise throws a ValidateException with the known issues
	 * 
	 * @public
	 */
	AggregationValidator.prototype.validate = function(aAggregation) {
		if (this.oConstraints) {
		 	var aViolatedConstraints = [],
		 		aMessages = [],
		 		that = this;

		 	jQuery.each(this.oConstraints, function(sName, oContent) {
		 		switch (sName) {
		 			case "exactLength":  // expects int
		 				if ( (aAggregation === null && oContent > 0)  || (aAggregation !== null && aAggregation.length !== oContent) ) {
		 					aViolatedConstraints.push("exactLength");
		 					aMessages.push(that.oBundle.getText("AggregationValidator.ExactLength."+(oContent === 1 ? "Singular":"Plural"), oContent ));
		 				}
		 				break;
		 			case "maxLength":  // expects int
		 				if (aAggregation !== null && aAggregation.length > oContent) {
		 					aViolatedConstraints.push("maxLength");
		 					aMessages.push(that.oBundle.getText("AggregationValidator.MaxLength."+(oContent === 1 ? "Singular":"Plural"), oContent ));
		 				}
		 				break;
		 			case "minLength":  // expects int
		 				if ((aAggregation === null && oContent > 0)  || (aAggregation !== null && aAggregation.length < oContent)) {
		 					aViolatedConstraints.push("minLength");
		 					aMessages.push(that.oBundle.getText("AggregationValidator.MinLength."+(oContent === 1 ? "Singular":"Plural"), oContent ));
		 				}
		 				break;
		 		}
		 	});
		 	if (aViolatedConstraints.length > 0) {
		 		throw new ValidateException(aMessages.join(" "), aViolatedConstraints);
		 	}
		}
   	   return true;
	};

	return AggregationValidator;

});
