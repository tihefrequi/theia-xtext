// Provides the base implementation for all model validators
sap.ui.define([
	'sap/ui/base/Object',
	'sap/ui/model/ValidateException'
], function(BaseObject, ValidateException) {

	"use strict";

	/**
	 * Constructor for a new Model Validator.
	 * 
	 * @class This is an abstract base class for model validator objects.
	 * @abstract
	 * 
	 * @extends sap.ui.base.Object
	 * 
	 * @author storm
	 * @version 2017-09-17
	 * 
	 * @constructor
	 * @param {object}
	 *            [oModel] oModel as provided by concrete subclasses
	 * @param {string}
	 *            [sResourceModelId] Resource Model as provided by concrete subclasses, fallback to "i18n"
	 * @public
	 * @alias {app.namespace}.tools.validation.ModelValidator
	 */
	var ModelValidator = BaseObject.extend("{app.namespace}.tools.validation.ModelValidator", /** @lends sap.ui.base.BaseObject.prototype */
	{

		/**
		 * Constructor
		 * 
		 * @param {object}
		 *            [oModel] oModel as provided by concrete subclasses
		 * @param {string}
		 *            [sResourceModelId] Resource Model as provided by concrete subclasses, fallback to "i18n"
		 */
		constructor: function(oModel,sResourceModelId) {
			BaseObject.apply(this, arguments);
			this.sName = "ModelValidator";
			this.setModel(oModel || {});
			this.setResourceModel(sap.ui.getCore().getModel(sResourceModelId || "{app.i18nModelName}"));
		},

		metadata: {
			"abstract": true,
			publicMethods: [
			// methods
					"getName", "setModel", "setResourceModel" , "validate", "handleValidation"]
		}

	});

	/**
	 * Returns the name of this ModelValidator.
	 * 
	 * @function
	 * @name {app.namespace}.tools.validation.ModelValidator.getName
	 * @public
	 * 
	 * @return {String} the name of this ModelValidator
	 */
	ModelValidator.prototype.getName = function() {
		return this.sName;
	};

	/**
	 * Sets model for this model validator. This is meta information used when validating the value, to ensure it meets
	 * certain criteria, e.g. maximum length, minimal amount you can compare any values connected in the model (e.g.
	 * only check one field, if another has a certain value e.g.)
	 * 
	 * @function
	 * @name {app.namespace}.tools.validation.ModelValidator.setModel
	 * 
	 * @public
	 * @param {object}
	 *            oModel the model to set for this validator
	 */
	ModelValidator.prototype.setModel = function(oModel) {
		this.oModel = oModel;
	};

	/**
	 * Sets Resource model for this model validator. This is used for key / validation / error texts 
	 * 
	 * @function
	 * @name {app.namespace}.tools.validation.ModelValidator.setResourceModel
	 * 
	 * @public
	 * @param {object}
	 *            oResourceModel the resource model to set for this validator
	 */
	ModelValidator.prototype.setResourceModel = function(oResourceModel) {
		this.oResourceModel = oResourceModel;
	};

	/**
	 * Validate whether a given value in model representation is valid and meets the defined constraints (if any).
	 * 
	 * @function
	 * @name {app.namespace}.tools.validation.ModelValidator.validateValue
	 *
	 * @returns {Boolean} true if validation is successfull otherwise throws a ValidateException with the known issues
	 * 
	 * @public
	 */
	ModelValidator.prototype.validate = function() {
		return true;
	};

	/**
	 * Build Message from Failure Reason
	 * 
	 * @function
	 * @name {app.namespace}.tools.validation.ModelValidator.buildMessageFromFailureReason
	 * 
	 * @public
	 * @param {object} oFailureReason e.g.: {
	 *            reason: "validation.failure.minstringlen", args: [ "value1" ], }
	 *
	 * @returns {Boolean} true if validation is successfull otherwise throws a ValidateException with the known issues
	 * 
	 */
	ModelValidator.prototype.buildMessageFromFailureReason = function(oFailureReason) {
		var sFallbackMessage = "Model Validation failed: Reason: " + oFailureReason.reason;		
		if (this.oResourceModel === undefined) {
			return sFallbackMessage;
		}
		var sMessage = this.oResourceModel.getResourceBundle().getText(oFailureReason.reason, oFailureReason.args);
		return sMessage;
	};
	
	/**
	 * Throw Message from Failure Reason, if not successfull
	 * 
	 * @function
	 * @name{app.namespace}.tools.validation.ModelValidator.handleValidation
	 * 
	 * @public
	 * @param {boolean} bValidationSuccess only throws a message if this value is true 
	 * @param {object} oFailureReason e.g.: {
	 *            reason: "validation.failure.minstringlen", args: [ "value1" ], }
	 */
	ModelValidator.prototype.handleValidation = function(bValidationSuccess, oFailureReason) {
		if (!bValidationSuccess) {
			var sMessage = this.buildMessageFromFailureReason( oFailureReason );
			throw new ValidateException(sMessage);
		}
	};	

	return ModelValidator;

});
