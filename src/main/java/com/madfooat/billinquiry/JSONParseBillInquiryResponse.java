package com.madfooat.billinquiry;

import com.madfooat.billinquiry.domain.Bill;
import com.madfooat.billinquiry.exceptions.InvalidBillInquiryResponse;

import java.math.BigDecimal;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class JSONParseBillInquiryResponse implements ParseBillInquiryResponse {

	
	/*
	 * Constants  
	 */
	
	/** Validator */
	private static final JSONFieldsValidator validator = new JSONFieldsValidator();

	/**
	 * Parse
	 * 
	 * @param billerResponse
	 * @return List of bills
	 * @throws InvalidBillInquiryResponse
	 */
	@Override
	public List<Bill> parse(String billerResponse) throws InvalidBillInquiryResponse {

		List<Bill> bills = null;
		try {
			bills = new ArrayList<>();
			JSONArray jsonArray = new JSONArray(billerResponse);
			for (int i = 0; i < jsonArray.length(); i++) {
				if (jsonArray.get(i) instanceof JSONObject) {
					JSONObject jsnObj = (JSONObject) jsonArray.get(i);
					validator.validateFields(jsnObj);
					Bill bill = constructBillbject(jsnObj);
					bills.add(bill);
				}
			}
		} catch (JSONException | ParseException e) {
			e.printStackTrace();
		}
		return bills;
	}

	/**
	 * constructBillbject
	 * 
	 * @param jsnObj
	 * @return Bill object
	 * @throws JSONException
	 */
	private Bill constructBillbject(JSONObject jsnObj) throws JSONException , ParseException{
		Bill bill = new Bill();
		
		bill.setDueAmount(new BigDecimal((String) jsnObj.get("dueAmount")));

		String dueDateStr = (String) jsnObj.get("dueDate");
		Date sysDate = new Date();
		String myFormatString = "dd-MM-yy";
		SimpleDateFormat df = new SimpleDateFormat(myFormatString);
		Date dueDate = null;
        dueDate = df.parse(dueDateStr);
		bill.setDueDate(dueDate);
		
		if (jsnObj.has("fees")) {
			bill.setFees(new BigDecimal((String) jsnObj.get("fees")));
		}
		
		return bill;
	}
}
