package com.madfooat.billinquiry;

import java.math.BigDecimal;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import org.json.JSONException;
import org.w3c.dom.CharacterData;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import com.madfooat.billinquiry.exceptions.InvalidBillInquiryResponse;

public class XMLFieldsValidator {

	/**
	 * ValidateFields
	 * 
	 * @param jsnObj
	 */
	public void validateFields(Element element) {
		try {
			// Validate required fields
			if (element.getElementsByTagName("dueAmount") == null) {
				throw new InvalidBillInquiryResponse();
			}
			if (element.getElementsByTagName("dueDate") == null) {
				throw new InvalidBillInquiryResponse();
			}
			validateAmount(element);
			validateDueDate(element);

			if (element.getElementsByTagName("fees") != null) {
				validateFees(element);
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
	private void validateFees(Element element) throws JSONException {

		NodeList feesNode = element.getElementsByTagName("fees");
		Element line = (Element) feesNode.item(0);
		String feesStr = getCharacterDataFromElement(line);

		NodeList dueAmountNode = element.getElementsByTagName("dueAmount");
		Element dueAmountLine = (Element) dueAmountNode.item(0);
		String dueAmountStr = getCharacterDataFromElement(line);

		BigDecimal fees = new BigDecimal(feesStr);
		BigDecimal dueAmount = new BigDecimal(dueAmountStr);

		if (fees.compareTo(dueAmount) > 0) {
			throw new InvalidBillInquiryResponse();
		}
	}

	/**
	 * ValidateAmount
	 * 
	 * @param jsnObj
	 */
	private void validateAmount(Element element) throws JSONException {

		NodeList name = element.getElementsByTagName("dueAmount");
		Element line = (Element) name.item(0);
		String amount = getCharacterDataFromElement(line);
		
	}

	/**
	 * ValidateDueDate
	 * 
	 * @param jsnObj
	 */
	private void validateDueDate(Element element) throws JSONException {

		NodeList name = element.getElementsByTagName("dueDate");
		Element line = (Element) name.item(0);
		String dueDate = getCharacterDataFromElement(line);

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

	public static String getCharacterDataFromElement(Element e) {
		Node child = e.getFirstChild();
		if (child instanceof CharacterData) {
			CharacterData cd = (CharacterData) child;
			return cd.getData();
		}
		return "?";
	}

}
