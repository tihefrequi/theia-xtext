sap.ui.define([
   'jquery.sap.global',
   'sap/ui/test/matchers/Matcher'
], function($, Matcher) {
   "use strict";

   /**
    * TableColumnCellPropertyValueMatcher - checks if an cell has the correct value in in the table.
    *
    * @class TableColumnCellPropertyValueMatcher - checks if an cell has the correct value in in the table.
    * @extends sap.ui.test.matchers.Matcher
    * @param {object} [mSettings] optional map/JSON-object with initial settings for the new TableColumnCellPropertyValueMatcher
    * @public
    * @name {app.id}.test.tools.matchers.TableColumnCellPropertyValueMatcher
    * @author Tobias Pawlik
    */
   return Matcher.extend("{app.id}.test.tools.matchers.TableColumnCellPropertyValueMatcher", {

      metadata: {
         publicMethods: ["isMatching"],
         properties: {

            /**
             * The ID of the column that is used for matching.
             */
            columnId: {
               type: "string"
            },
            /**
             * The name of the property that is used for matching.
             */
            propertyName: {
               type: "string"
            },
            /**
             * The value of the cell that is used for matching.
             */
            cellValue: {
               type: "any"
            }
         }
      },

      /**
       * Checks if the table has a filled cell that have a property equaling propertyName/Value.
       *
       * @param {sap.ui.core.Control} oControl the control that is checked by the matcher
       * @return {boolean} true if the cell has the correct value, false if it is not.
       * @public
       */
      isMatching: function(oControl) {

         var sColumnId = this.getColumnId(),
            sPropertyName = this.getPropertyName(),
            vCellValue = this.getCellValue(),
            oView = this.getViewFromControl(oControl),
            bMatches = false,
            fullColumnId,
            aColumns = oControl.getColumns(),
            columnIndex =null,
            oCell,
            propertyGetter,
            that = this;

         //Checks if the sColumnId exists in the view
         if (!oView.byId(sColumnId)) {
            that._oLogger.error("The table '" + oControl + "' does not have an column with the ID '" + sColumnId + "'");
            return false;
         }
         fullColumnId = oView.byId(sColumnId).getId();

         //Detects the column index
         for (var i = 0; i < aColumns.length; i++) {
            if (aColumns[i].getId() == fullColumnId) {
               columnIndex = i;
               break;
            }
         }
         //Checks if the columnIndex is defined
         if (columnIndex==null) {
            that._oLogger.error("The table '" + oControl + "' does not have an column with the ID '" + sColumnId + "'");
            return false;
         }

         //Detects a item with a cell with the value vCellValue
         for (var j = 0; j < oControl.getItems().length; j++) {
            oCell = oControl.getItems()[j].getCells()[columnIndex];
            //Creates a getter for the sPropertyName property
            propertyGetter = oCell["get" + $.sap.charToUpperCase(sPropertyName, 0)];
            if (!propertyGetter) {
               that._oLogger.error("Control '" + oControl + "' does not have an property called '" + sPropertyName + "'");
               return false;
            }
            //Checks if the cell value matches with vCellValue
            if (propertyGetter.call(oCell) == vCellValue) {
               bMatches = true;
               break;
            }
         }
         //Checks if bMatches is false
         if (!bMatches) {
            this._oLogger.debug("The Table '" + oControl + "' has no property with the ID'" + sColumnId);
         }

         return bMatches;
      },
      /**
       * Detects the view from the parent hierarchy.
       *
       * @param {sap.ui.core.Control} oControl the control that is checked by the matcher
       * @return {sap.ui.core.mvc.View} the parent view where the oControl is contained in
       * @public
       */
      getViewFromControl: function(oControl) {
         var oView = oControl;
         while (!oView.getViewName /*&& oView.getParent()*/ ) {
            oView = oView.getParent();
         }
         return oView;
      }

   });

}, /* bExport= */ true);