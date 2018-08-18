package com.madfooat.billinquiry;

import java.math.BigDecimal;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import org.json.JSONException;
import org.json.JSONObject;

import com.madfooat.billinquiry.exceptions.InvalidBillInquiryResponse;

public class JSONFieldsValidator {

	/**
	 * ValidateFields
	 * 
	 * @param jsnObj
	 */
	public void validateFields(JSONObject jsnObj) {
		try {
			// Validate required fields
			if (!jsnObj.has("dueAmount")) {
				throw new InvalidBillInquiryResponse();
			}
			if (!jsnObj.has("dueDate")) {
				throw new InvalidBillInquiryResponse();
			}
			validateAmount(jsnObj);
			validateDueDate(jsnObj);

			if (jsnObj.has("fees")) {
				validateFees(jsnObj);
			}
		} catch (JSONException e) {
			e.printStackTrace();
		}

	}

	/**
	 * ValidateFees
	 * 
	 * @param jsnObj
	 */
	private void validateFees(JSONObject jsnObj) throws JSONException {

		BigDecimal fees = new BigDecimal((String) jsnObj.get("fees"));
		BigDecimal dueAmount = new BigDecimal((String) jsnObj.get("dueAmount"));

		if (fees.compareTo(dueAmount) > 0) {
			throw new InvalidBillInquiryResponse();
		}

	}

	/**
	 * ValidateAmount
	 * 
	 * @param jsnObj
	 */
	private void validateAmount(JSONObject jsnObj) throws JSONException {

		BigDecimal dueAmount = new BigDecimal((String) jsnObj.get("dueAmount"));
		// TODO Validate Amount

	}

	/**
	 * ValidateDueDate
	 * 
	 * @param jsnObj
	 */
	private void validateDueDate(JSONObject jsnObj) throws JSONException {

		String dueDate = (String) jsnObj.get("dueDate");
		Date sysDate = new Date();
		String myFormatString = "dd-MM-yy";
		SimpleDateFormat df = new SimpleDateFormat(myFormatString);
		Date givenDate = null;
		try {
			givenDate = df.parse(dueDate);
		} catch (ParseException e) {
			e.printStackTrace();
		}
		Long formattedDueDate = givenDate.getTime();
		Date dueDateObject = new Date(formattedDueDate);
		if (dueDateObject.after(sysDate)) {
			throw new InvalidBillInquiryResponse();
		}
	}

}
