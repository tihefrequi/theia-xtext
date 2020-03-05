/*global sap */

sap.ui.define([
    'sap/base/Log',
    'sap/ui/core/message/Message',
    'sap/ui/core/MessageType',
    'jquery.sap.global', 
    'sap/ui/model/ValidateException',
    '{app.idpath}/tools/validation/AggregationValidator'
], function (Log,Message, MessageType,jQuery,ValidateException, AggregationValidator) {
    "use strict";

    /**
     * @name        {app.namespace}.Validator
     *
     * @class       
     * @classdesc   Validator class.<br/>
     *
     * @version     Oktober 2015 (taken on 2017-03-29 with last change on 3 Feb 2017 from url https://github.com/qualiture/ui5-validator/blob/master/Validator.js )
     * @author      Robin van het Hof
     */
    var Validator = function () {
        this._isValid = true;
        this._isValidationPerformed = false;
    };

    /**
     * Returns true _only_ when the form validation has been performed, and no validation errors were found
     * @memberof {app.namespace}.Validator
     *
     * @returns {boolean}
     */
    Validator.prototype.isValid = function () {
        return this._isValidationPerformed && this._isValid;
    };

    /**
	 * Recursively validates the given oControl and any aggregations (i.e. child controls) it may have
	 * @memberof {app.namespace}.Validator
	 *
	 * @param {(sap.ui.core.Control|sap.ui.layout.form.FormContainer|sap.ui.layout.form.FormElement)} oControl - The control or element to be validated. 
	 * @return {boolean} whether the oControl is valid or not.
	 */
	Validator.prototype.validateSingleControl = function (oControl) {
		this._isValid = true;
		this._resetAdditionalErrorIndicators(oControl);
		this._validate(oControl);
		return this.isValid();
	};
	
    /**
     * Recursively validates the given oControl and any aggregations (i.e. child controls) it may have
     * @memberof {app.namespace}.Validator
     *
     * @param {(sap.ui.core.Control|sap.ui.layout.form.FormContainer|sap.ui.layout.form.FormElement)} oControl - The control or element to be validated.
     * @return {boolean} whether the oControl is valid or not.
     */
    Validator.prototype.validate = function (oControl) {
        this._isValid = true;
        sap.ui.getCore().getMessageManager().removeAllMessages();
        // MODIFICATION 5: call _resetAdditionalErrorIndicators()
        this._resetAdditionalErrorIndicators(oControl);
        this._validate(oControl);
        return this.isValid();
    };

    /**
     * Recursively validates the given oControl and any aggregations (i.e. child controls) it may have
     * @memberof {app.namespace}.Validator
     *
     * @param {(sap.ui.core.Control|sap.ui.layout.form.FormContainer|sap.ui.layout.form.FormElement)} oControl - The control or element to be validated.
     */
    Validator.prototype._validate = function (oControl) {
        var aPossibleAggregations = ["items", "content", "form", "formContainers", "formElements", "fields", "sections", "subSections", "_grid", "headerContainer"],
        aControlAggregation   = null,
        oControlBinding       = null,
        oControlBindingInfo	  = null,
        sMessageText		  = "",
        oType				  = {},
        oAggregationValidator = null,
        aValidateProperties   = ["value", "selectedIndex", "selectedKey", "selectedKeys", "text"], // yes, I want to validate Select and Text controls too
        isValidatedControl    = false,
        oExternalValue, oInternalValue,
        i, j;

	    // only validate controls and elements which have a 'visible' property
	    if (oControl instanceof sap.ui.core.Control ||
	        oControl instanceof sap.m.IconTabFilter /* MODIFICATION storm1: to allow deep dive into TabContainer */ || 
	        oControl instanceof sap.ui.layout.form.FormContainer ||
	        oControl instanceof sap.ui.layout.form.FormElement) {
	
	        // only check visible controls (invisible controls make no sense checking)
	        if (oControl.getVisible()) {
	
	            // MODIFICATION storm 2:
	            // smart controls have no aggregations, but inner controls, that can be checked
	            if (oControl.getInnerControls) {
	                aControlAggregation = oControl.getInnerControls();
	                for (i = 0; i < aControlAggregation.length; i += 1) {
	                    this._validate(aControlAggregation[i]);
	                }
	            } else { 
	                // check control for any properties worth validating 
	                for (i = 0; i < aValidateProperties.length; i += 1) {
	                    if (oControl.getBinding(aValidateProperties[i])) {
	                        // check if a data type exists (which may have validation constraints)
	                        oType = oControl.getBinding(aValidateProperties[i]).getType();
	                        if (oType) {
	                        	
	                        	//MODIFICATION storm 10: reset the error message for this property
                                this._removeValidationErrorMsg(oControl, aValidateProperties[i]);
	                        	
	                            // try validating the bound value
	                            try {
	                                oControlBinding = oControl.getBinding(aValidateProperties[i]);
	                                oExternalValue  = oControl.getProperty(aValidateProperties[i]);
	                                
	                                if (!(oControl instanceof sap.m.Text)) {
	                                	// We want to avoid handling for sap.m.Text, as e.g. during a New Entity Screen, with a -disabled- createdby/at, the empty datefield should not be checked, as this will be done serverside
										let isRequired = (oControl.getRequired instanceof Function && oControl.getRequired()) || (oControl.getMandatory instanceof Function && oControl.getParent() && oControl.getParent().getInnerControls && oControl.getParent().getMandatory());
										let isEmptyExternalValue = (oExternalValue === "" || oExternalValue === undefined || oExternalValue === null);
										let skipValueValidation = !isRequired && isEmptyExternalValue;
	                                	
										// we skip the value validation for fields that are not required and are empty. because otherwise e.g. type integer would not accept a "" empty field
										if (!skipValueValidation) {
											oInternalValue  = oControlBinding.getType().parseValue(oExternalValue, oControlBinding.sInternalType);
											oControlBinding.getType().validateValue(oInternalValue);
										}
		                                
		                	            // MODIFICATION storm 7:
		                                // Additional Checks for dynamic (XML Expression based) required fields and smartControls (they have a 'mandatory' parameter (dynamic) and include a control below.)
		                                // we have to check for existence of a property before, because otherwise this will throw an exception, e.g. on sap.m.Select Box
		                                if (isRequired) {
											let isEmptyInternalValue = (oInternalValue === "" || oInternalValue === undefined || oInternalValue === null);
											if (isEmptyInternalValue || (Array.isArray(oInternalValue) && oInternalValue.length < 1)) { // no Value given or empty Array, but the field is mandatory/required
												sMessageText="Please enter a value";
												if (oType.oFormatOptions && oType.oFormatOptions.mandatoryMessageKey) {
													sMessageText = sap.ui.getCore().getLibraryResourceBundle().getText(oControlBinding.getType().oFormatOptions.mandatoryMessageKey);	                                				
												}
												throw new ValidateException(sMessageText);
											}
		                                }
	                                }
	                            }
	                            // catch any validation errors
	                            catch (ex) {
	                                this._isValid = false;
	                                
	                                //MODIFICATION storm 8: add error message in the separate method	                                
	                                this._addValidationErrorMsg(oControl, aValidateProperties[i], ex.message, oType.oFormatOptions);
	                            }
	
	                            isValidatedControl = true;
	                        } else {
	                        	oControlBindingInfo = oControl.getBindingInfo(aValidateProperties[i]);
	                        	try {
		                        	if (oControl instanceof sap.m.StepInput) {
	    	                        	// Special Case : We have a StepInput which has no Type (because the min/max depend on properties, which may be binded, so we cannot set min/max in constraintOptions e.g. on a Integer Type)
		                                let min=oControl.getProperty("min"), max=oControl.getProperty("max"),value=oControl.getProperty("value");
		                                let key = "";
		                                if (value<min) {
		                                	key = oControlBindingInfo.formatOptions.i18nTooLow;
		                                	sMessageText = oControl.getModel("i18n").getResourceBundle().getText(key, [value, min]);
	    	                                throw new ValidateException(sMessageText);               		
		                                }
		                                if (value>max) {
		                                	key = oControlBindingInfo.formatOptions.i18nTooHigh;
		                                	sMessageText = oControl.getModel("i18n").getResourceBundle().getText(key, [value, max]);
	    	                                throw new ValidateException(sMessageText);               		
		                                }
		                        	}
	                        	}
	                            // catch any validation errors
	                            catch (ex) {
	                                this._isValid = false;
	                                
	                                //MODIFICATION storm 8: add error message in the separate method	                                
	                                this._addValidationErrorMsg(oControl, aValidateProperties[i], ex.message, oControlBindingInfo.formatOptions);
	                            }
	
	                            isValidatedControl = true;
	                        }
	                    }
	                }
	
	                
	                //MODIFICATION storm 9: if the control was not validated by properties, we try to validate aggregation if specific constraints were provided
	                if (!isValidatedControl) {
	                    for (i = 0; i < aPossibleAggregations.length; i += 1) {
	                        oControlBindingInfo = oControl.getBindingInfo(aPossibleAggregations[i]);
	                        if (oControlBindingInfo && typeof(oControlBindingInfo.constraints) !== "undefined") {
	                        	
	                        	//MODIFICATION storm 10: reset the error message for this aggregation
                                this._removeValidationErrorMsg(oControl, aPossibleAggregations[i]);
	                        	
	                        	// try validating the bound value
	                            try {
	                                oControlBinding = oControl.getBinding(aValidateProperties[i]);
	                                aControlAggregation = oControl.getAggregation(aPossibleAggregations[i]);
	                                
	                                oAggregationValidator = new AggregationValidator(oControlBindingInfo.constraints);
	                                oAggregationValidator.validate(aControlAggregation);
	                                
	                            }
	                            // catch any validation errors
	                            catch (ex) {
	                                this._isValid = false;
	                                
	                                //MODIFICATION storm 8: add error message in the separate method	                                
	                                this._addValidationErrorMsg(oControl, aPossibleAggregations[i], ex.message, oControlBindingInfo.formatOptions);
	                            }
	
	                            isValidatedControl = true;
	                        }
	                    }                    
	                	
	                }
	                
	                // if the control could not be validated itself, we check for aggregation items and try to validate them
	                if (!isValidatedControl) {
	                    for (i = 0; i < aPossibleAggregations.length; i += 1) {
	                        aControlAggregation = oControl.getAggregation(aPossibleAggregations[i]);
	
	                        if (aControlAggregation) {
	                            // generally, aggregations are of type Array
	                            if (aControlAggregation instanceof Array) {
	                                for (j = 0; j < aControlAggregation.length; j += 1) {
	                                    this._validate(aControlAggregation[j]);
	                                }
	                            }
	                            // ...however, with sap.ui.layout.form.Form, it is a single object *sigh*
	                            else {
	                                this._validate(aControlAggregation);
	                            }
	                        }
	                    }                    
	                }                	
	            }
	        }
	    }
	    this._isValidationPerformed = true;
    };
    
    
    /**
     * MODIFICATION 4: new function:
     * 
     * Recursively resets all additional Error Indicators like Tab Icon Colors
     * @param {(sap.ui.core.Control|sap.ui.layout.form.FormContainer|sap.ui.layout.form.FormElement)} oControl - The control or element to be validated.
     */
    Validator.prototype._resetAdditionalErrorIndicators = function (oControl) {
        var aPossibleAggregations = ["items", "content", "form", "formContainers", "formElements", "fields", "sections", "subSections", "_grid", "headerContainer"],
            aControlAggregation   = null,
            i, j;

        //If the control had no value state method we should reset the error state manually
        if (!oControl.setValueState) {
			jQuery(oControl.getDomRef()).css({ "border" : ""});
		}

        if (oControl instanceof sap.m.IconTabFilter) {
        	oControl.setIconColor(sap.ui.core.IconColor.Default);
        } else
        // only validate controls and elements which have a 'visible' property
        if (oControl instanceof sap.ui.core.Control ||
            oControl instanceof sap.ui.layout.form.FormContainer ||
            oControl instanceof sap.ui.layout.form.FormElement) {

            // only check visible controls (invisible controls make no sense checking)
            if (oControl.getVisible()) {           	
                // check aggregations
                for (i = 0; i < aPossibleAggregations.length; i += 1) {
                    aControlAggregation = oControl.getAggregation(aPossibleAggregations[i]);

                    if (aControlAggregation) {
                        // generally, aggregations are of type Array
                        if (aControlAggregation instanceof Array) {
                            for (j = 0; j < aControlAggregation.length; j += 1) {
                                this._resetAdditionalErrorIndicators(aControlAggregation[j]);
                            }
                        }
                        // ...however, with sap.ui.layout.form.Form, it is a single object *sigh*
                        else {
                            this._resetAdditionalErrorIndicators(aControlAggregation);
                        }
                    }
                }
                // MODIFICATION storm 2:
                // smartforms have no aggregations, but inner controls, that can be checked
                if (oControl.getInnerControls) {
                    aControlAggregation = oControl.getInnerControls();
                    for (i = 0; i < aControlAggregation.length; i += 1) {
                        this._resetAdditionalErrorIndicators(aControlAggregation[i]);
                    }
                }
            }
        }
    };

    /**
     * MODIFICATION 10: new function
     * 
     * Remove validation error messages for specific property/aggregation
     */
    Validator.prototype._removeValidationErrorMsg = function(oControl, sPropertyOrAggregationName) {
    	var oControlBinding = oControl.getBinding(sPropertyOrAggregationName);
        
        // Model Id Path seem to change between UI5 Controls, to be more precise, between different MessageProcessor  ..., we test both we know of. 
        // sTargetByModelPath = /Posts('id-1526217764040-34')/validUntil
        // sTargetByControlIDAttribute = __xmlview1--ValidUntilId/value
		var sTargetByModelPath = (oControlBinding.getContext() ? oControlBinding.getContext().getPath() + "/" : "") + oControlBinding.getPath(),
			sTargetByControlIDAttribute = oControl.getId() + "/" + sPropertyOrAggregationName,
			aMessagesToRemove=[];

		jQuery.each(sap.ui.getCore().getMessageManager().getMessageModel().getData(),function(i, message) {
			if (message.target === sTargetByModelPath || message.target === sTargetByControlIDAttribute) {
				aMessagesToRemove.push(message);
			}
		});
		
		if (aMessagesToRemove.length > 0) {
			sap.ui.getCore().getMessageManager().removeMessages(aMessagesToRemove);        	
		}		
    }

    /**
     * MODIFICATION 8: new function
     * 
     * Add validation error messages for specific property/aggregation and given format options
     */
    Validator.prototype._addValidationErrorMsg = function(oControl, sPropertyOrAggregationName, sMessage, oFormatOptions) {
    	var oControlBinding = oControl.getBinding(sPropertyOrAggregationName);
        
        // MODIFICATION 3: Mark Tab Container Icons red as well, when errors occur
        /* Detect, if current Field is inside a Tab of a Tab Container, then mark tab Container Icon red */
        // Detect if any Parent if a IconTabFilter 
        var oTabParent=oControl.oParent;
        while (oTabParent !== null && !(oTabParent instanceof sap.m.IconTabFilter)) {
            oTabParent=oTabParent.oParent;
        }
        // If we are in a IconTab Filter, we mark the Icon Tab Filter Icon negative as well
        if (oTabParent !== null) {
			oTabParent.setIconColor(sap.ui.core.IconColor.Negative)
        }
        
		// Some Controls like the RichTextEditor or UploadCollection have no ValueState, so we try to add the border
		if (oControl.setValueState) {
			oControl.setValueState(sap.ui.core.ValueState.Error);
		} else {
			jQuery(oControl.getDomRef()).css({ "border" : "2px solid red"});
		}

        // Model Id Path seem to change between UI5 Controls, to be more precise, between different MessageProcessor  ..., we test both we know of. 
        // sTargetByModelPath = /Posts('id-1526217764040-34')/validUntil
        // sTargetByControlIDAttribute = __xmlview1--ValidUntilId/value
		var sTargetByModelPath = (oControlBinding.getContext() ? oControlBinding.getContext().getPath() + "/" : "") + oControlBinding.getPath(),
			sTargetByControlIDAttribute = oControl.getId() + "/" + sPropertyOrAggregationName, 
			bAlreadyMessagesAvailable=false;
		jQuery.each(sap.ui.getCore().getMessageManager().getMessageModel().getData(),function(i, message) {
			if (message.target === sTargetByModelPath || message.target === sTargetByControlIDAttribute) {
				bAlreadyMessagesAvailable=true;
			}
		});
		if (!bAlreadyMessagesAvailable) {
			// Only add message, if not already one for the same sBindingPath is available. This happens,
			// e.g. when, a Field is places on more than one Tab e.g.
			// Typically, also a Message is placed directly from the UI5 model binding framework itself, so we do not need to do this in most cases
			var oMessageProcessor = new sap.ui.core.message.ControlMessageProcessor(),
				oMessageManager = sap.ui.getCore().getMessageManager(),
				additionalText = "";
			// registering message processor to message manager
			oMessageManager.registerMessageProcessor(oMessageProcessor);
			
			if (oFormatOptions && oFormatOptions.i18nKeyLabel && /^\{[^>]+\>[^\}]+\}$/.test(oFormatOptions.i18nKeyLabel)) {
				// is model expression {i18n>anykey}
				var parts = oFormatOptions.i18nKeyLabel.substr(1,oFormatOptions.i18nKeyLabel.length-2).split(">"),
					i18nModel = parts[0], i18nKey=parts[1],
					text = oControl.getModel(i18nModel ).getResourceBundle().getText(i18nKey);
				 if (text !== i18nKey) { // found
					 additionalText = text;
				 }
			}

			Log.info("Adding Message "+sMessage+" with additionalText " +additionalText +" for path="+oControlBinding.getPath() +" from control "+oControl.sId);
            sap.ui.getCore().getMessageManager().addMessages(
                new Message({
                    message  : sMessage,
                    type     : MessageType.Error,
                    target   : sTargetByControlIDAttribute,
                    processor: oMessageProcessor,
                    additionalText : additionalText
                })
            );
		}			
    };

    return Validator;
});