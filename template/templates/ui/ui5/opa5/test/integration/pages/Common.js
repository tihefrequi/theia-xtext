sap.ui.define([
   'sap/ui/test/Opa5',
   'sap/ui/test/actions/Press',
   'sap/ui/test/actions/EnterText',
   '{app.idpath}/ui/test/tools/actions/PressMultiComboBoxArrow',
   "{app.idpath}/ui/test/tools/actions/PressOnDatePickerIcon",
   "{app.idpath}/ui/test/tools/actions/PressOnDatePickerDay",
   "{app.idpath}/ui/test/tools/actions/PressOnDatePickerPreviousMonth",
   "{app.idpath}/ui/test/tools/actions/PressOnDatePickerNextMonth",
   "{app.idpath}/ui/test/tools/actions/PressOnDatePickerMonth",
   "{app.idpath}/ui/test/tools/actions/PressOnDatePickerMonthSelector",
   "{app.idpath}/ui/test/tools/actions/PressOnDatePickerYearSelector",
   "{app.idpath}/ui/test/tools/actions/PressOnDatePickerYear"
],
function(Opa5,
		Press,
		EnterText,
		PressMultiComboBoxArrow,
		PressOnDatePickerIcon,
		PressOnDatePickerDay,
		PressOnDatePickerPreviousMonth,
		PressOnDatePickerNextMonth,
		PressOnDatePickerMonth,
		PressOnDatePickerMonthSelector,
		PressOnDatePickerYearSelector,
		PressOnDatePickerYear
		) {
   "use strict";

   function getFrameUrl(sHash, sUrlParameters) {
      sHash = sHash || "";
      var sUrl = jQuery.sap.getResourcePath("{app.idpath}/ui", "/index.html");

      if (sUrlParameters) {
         sUrlParameters = "?" + sUrlParameters;
      }

      return sUrl + sUrlParameters + "#" + sHash;
   }

   return Opa5.extend("{app.id}.test.integration.pages.Common", {

         constructor: function(oConfig) {
            Opa5.apply(this, arguments);

            this._oConfig = oConfig;
         },
         iStartMyApp: function(oOptions) {
            var sUrlParameters;
            oOptions = oOptions || {
               delay: 0,
               useLastEntity: false,
               entityName: "",
               entityAction: ""
            };

            sUrlParameters = "serverDelay=" + oOptions.delay;

            this.iStartMyAppInAFrame(getFrameUrl(oOptions.hash + (oOptions.useLastEntity ? "/" + opa5TestEntities[entityName][entityAction] : ""), sUrlParameters));
         },

         iLookAtTheScreen: function() {
            return this;
         },
         iSetTheErrorListEmpty: function() {
            return this.waitFor({
               actions: function() {
                  window.frames[0].clientErrorTracker.data = [];
               }
            });
         },

         iAssumeIHaveNoErrors: function() {
            var sErrorMessage,
               bHasErrors;
            return this.waitFor({
               check: function() {
                  bHasErrors = window.frames[0].clientErrorTracker.hasErrors();
                  sErrorMessage = window.frames[0].clientErrorTracker.getAllErrorsAsString();
                  return true;
               },
               success: function() {
                  Opa5.assert.ok(!bHasErrors, "Errors listed in the error list: " + sErrorMessage);
               }
            });
         },
         iPressOnTheButtonWithTheId: function(sViewName, sId) {
            return this.waitFor({
               controlType: "sap.m.Button",
               id: sId,
               viewName: sViewName,
               actions: new Press(),
               errorMessage: "Found no Button with the ID " + sId
            });
         },
         iEnterTheTextInTheFieldWithTheID: function(sText, sViewName, sId){
        	 return this.waitFor({
                 viewName: sViewName,
                 id: sId,
                 actions: new EnterText({
                          text: sText
                 }),
                 errorMessage: "Found no Field with the ID " + sId
            });
         },

      iEnterTheTextInTheBasicSearchFieldWithTheID: function(sText, sViewName, sId) {
         // We had the situation that a Basic Search field was not found by id, so we check manually with controlType for the id
         return this.waitFor({
            controlType: "sap.m.SearchField",
            viewName: sViewName,
            check: function(oControls) {
               for (var i = 0; i < oControls.length; i++) {
                  return oControls[i].getId() == sId;
               }
            },
            actions: [new EnterText({
               text: sText
            })],
            errorMessage: "No Basic Search Field with the ID " + sId +
               " in view " + sViewName + " and Text [" +
               sText + "] was found."
         });
      },
      


      iSelectTheItemWithTheTextInTheComboBoxWithTheId: function(sItemToSelect, sViewName, sId) {
         var oItemToSelect;
          return this.waitFor({
        	 viewName: sViewName,
        	 id: sId,
             check : function(oComboBox) {
					return oComboBox.getItems().filter(function(oItem) {
						if (oItem.getText() !== sItemToSelect) {
							return false;
						}
						oItemToSelect = oItem;
						return true;
					});
				},
             success: function(oComboBox) {
            	 new Press().executeOn(oComboBox),
                   this.waitFor({
                	   success: function() {
                         oItemToSelect.$().trigger("tap");
                      },
                      errorMessage: "Found no Item with the Text " + sItemToSelect
                   });
             },
          });
       },

//      iSelectOnTheFrameTheItemWithTheTextInTheMultiComboBoxWithTheId: function(sViewName, aItemsToSelect, sFragmentId, sId){
//    	  if (aItemsToSelect instanceof String){
//    		  this.iSelectTheItemInTheMultiComboBoxWithTheId(sViewName, sItemToSelect, sap.ui.core.Fragment.createId(sFragmentId, sId));
//    	  }else{
//        	  this.iSelectTheItemsOfTheArrayInTheMultiComboBoxWithTheId(sViewName, aItemsToSelect, sap.ui.core.Fragment.createId(sFragmentId, sId));
//    	  }
//      },
//      iSelectTheItemsOfTheArrayInTheMultiComboBoxWithTheId: function(sViewName, aItemsToSelect, sId) {
//   	   for (var i=0; i<aItemsToSelect.length;i++){
//		   this.iSelectTheItemInTheMultiComboBoxWithTheId(sViewName, aItemsToSelect[i], sId);
//	   }
//      },
      iSelectTheItemWithTheTextInTheMultiComboBoxWithTheId: function(sItemToSelect, sViewName, sId) {
         var oItemToSelect;
         return this.waitFor({
            viewName: sViewName,
            id: sId,
            actions: new PressMultiComboBoxArrow(),
            success: function() {
               this.waitFor({
                  controlType: "sap.m.StandardListItem",
                  check: function(oItems) {
                     return oItems.filter(function(oItem) {
                        if (oItem.getTitle() !== sItemToSelect) {
                           return false;
                        }
                        oItemToSelect = oItem;
                        return true;

                     });
                  },
                  success: function() {
                     oItemToSelect.$().trigger("tap");
                  },
                  errorMessage: "Found no Item with the Text:" + sItemToSelect
               });
            },
            errorMessage: "Found no Multi Combo Box with the ID " + sId
         });
      },
      iSelectTheDateInTheDatePickerWithTheId: function(sDate, sPath) {
    	   var dDate=new Date(sDate),
    	   	   sDay=dDate.getDate(),
    	   	   sMonth=dDate.getMonth()+1,
    	   	   sYear=dDate.getFullYear();
    	   
				return this.iPressOnTheDatePickerIcon(sPath).and
				.iPressOnYearSelector().and
				.iPressOnYear(sYear).and
				.iPressOnMonthSelector().and
				.iPressOnMonth(sMonth).and
				.iPressOnTheDay(sDay);
       },
      iPressOnTheDatePickerIcon: function(sPath) {
          var oItemToSelect;
          return this.waitFor({
             controlType: "sap.m.DatePicker",
             check: function(oControl) {

                return oControl.filter(function(oItem) {
                   if (oItem.mBindingInfos.value.binding.sPath !== sPath) {
                      return false;
                   }
                   oItemToSelect = oItem;
                   return true;
                });
             },
             success: function() {
                new PressOnDatePickerIcon().executeOn(oItemToSelect);
             },
             errorMessage: "No Field was found."
          });
       },
       iPressOnNextMonth: function() {
          var oItemToSelect;
          return this.waitFor({
             controlType: "sap.ui.unified.calendar.Header",
             check: function(oControl) {
                if (oControl.length !== 1) {
                   return false;
                }
                oItemToSelect = oControl[0];
                return true;
             },
             success: function() {
                new PressOnDatePickerNextMonth().executeOn(oItemToSelect);
             },
             errorMessage: "No Field was found."
          });
       },
       iPressOnPreviousMonth: function() {
          var oItemToSelect;
          return this.waitFor({
             controlType: "sap.ui.unified.calendar.Header",
             check: function(oControl) {
                if (oControl.length !== 1) {
                   return false;
                }
                oItemToSelect = oControl[0];
                return true;
             },
             success: function() {
                new PressOnDatePickerPreviousMonth().executeOn(oItemToSelect);
             },
             errorMessage: "No Field was found."
          });
       },
       iPressOnMonthSelector: function() {
          var oItemToSelect;
          return this.waitFor({
             controlType: "sap.ui.unified.calendar.Header",
             check: function(oControl) {
                if (oControl.length !== 1) {
                   return false;
                }
                oItemToSelect = oControl[0];
                return true;
             },
             success: function() {
                new PressOnDatePickerMonthSelector().executeOn(oItemToSelect);
             },
             errorMessage: "No Field was found."
          });
       },

       iPressOnMonth: function(iMonth) {
          var oItemToSelect;
          return this.waitFor({
             controlType: "sap.ui.unified.calendar.MonthPicker",
             check: function(oControl) {
                if (oControl.length !== 1) {
                   return false;
                }
                oItemToSelect = oControl[0];
                return true;
             },
             success: function() {
                new PressOnDatePickerMonth().executeOn(oItemToSelect, iMonth);
             },
             errorMessage: "No Field was found."
          });
       },
       iPressOnYearSelector: function() {
          var oItemToSelect;
          return this.waitFor({
             controlType: "sap.ui.unified.calendar.Header",
             check: function(oControl) {
                if (oControl.length !== 1) {
                   return false;
                }
                oItemToSelect = oControl[0];
                return true;
             },
             success: function() {
                new PressOnDatePickerYearSelector().executeOn(oItemToSelect);
             },
             errorMessage: "No Field was found."
          });
       },
       iPressOnYear: function(iYear) {
          var oItemToSelect;
          return this.waitFor({
             controlType: "sap.ui.unified.calendar.YearPicker",
             check: function(oControl) {
                if (oControl.length !== 1) {
                   return false;
                }
                oItemToSelect = oControl[0];
                return true;
             },
             success: function() {
                new PressOnDatePickerYear().executeOn(oItemToSelect, iYear);
             },
             errorMessage: "No Field was found."
          });
       },
       iPressOnTheDay: function(iDay) {
          var oItemToSelect;
          return this.waitFor({
             controlType: "sap.ui.unified.calendar.Month",
             check: function(oControl) {
                if (oControl.length !== 1) {
                   return false;
                }
                oItemToSelect = oControl[0];
                return true;
             },
             success: function() {
                new PressOnDatePickerDay().executeOn(oItemToSelect, iDay);
             },
             errorMessage: "No Field was found."
          });
       },
       iWriteInTheRichTextFieldWithTheID: function(sText, sId) {
          return this.waitFor({
             id: sId,
             viewName: sViewName,
             actions: [new EnterTextInRichTextEditor({
                text: sText
             })],
             errorMessage: "No Field with the ID " + sId +
                " was found."
          });
       },
       iSelectLocation: function(sId) {
          var oItemToSelect = null;
          var oPressArrow = new Press();
          return this.waitFor({

             controlType: "sap.m.ComboBox",
             viewName: sViewName,
             id: sId,

             check: function(oComboBox) {
                return oComboBox.getItems().filter(function(oItem) {
                   if (oItem.getText() !== "LocationTitle1") {
                      return false;
                   }

                   oItemToSelect = oItem;
                   return true;
                });
             },
             success: function(oComboBox) {

                oPressArrow.executeOn(oComboBox),

                   this.waitFor({
                      success: function() {
                         oItemToSelect.$().trigger("tap");
                      },
                      errorMessage: "Did not find the Item'"
                   });
             },
          });
       },
       
       //TODO Generischere Funktion für RadioButton
       iPressTheRadioButtonWithTheKey: function(sKey) {
          var oItemToSelect = null;
          return this.waitFor({
             viewName: sViewName,
             check: function(oRadioButtonGroup) {
                return oRadioButtonGroup.getButtons().filter(function(oButton) {
                   if (oButton.getBindingContext().getProperty("offerType") !== sKey) {
                      return false;
                   }

                   oItemToSelect = oButton;
                   return true;
                });
             },
             success: function() {
                oItemToSelect.$().trigger("tap");
             },
             errorMessage: "Did not find the Radio Button with the Id " + sKey
          });
       },

       iChooseTheDateInTheField: function(sDate, sPath) {
    	   var dDate=new Date(sDate),
    	   	   sDay=dDate.getDate(),
    	   	   sMonth=dDate.getMonth()+1,
    	   	   sYear=dDate.getFullYear();
    	   
			this.iPressOnTheDatePickerIcon(sPath);
			this.iPressOnYearSelector();
			this.iPressOnYear(sYear);
			this.iPressOnMonthSelector();
			this.iPressOnMonth(sMonth);
			return this.iPressOnTheDay(sDay);
       }
   });
});