package com.madfooat.billinquiry;

import com.madfooat.billinquiry.domain.Bill;
import com.madfooat.billinquiry.exceptions.InvalidBillInquiryResponse;

import java.io.IOException;
import java.io.StringReader;
import java.math.BigDecimal;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.json.JSONException;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

public class XMLParseBillInquiryResponse implements ParseBillInquiryResponse {

	/*
	 * Constants
	 */

	/** Validator */
	private static final XMLFieldsValidator validator = new XMLFieldsValidator();

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

			DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
			DocumentBuilder db = dbf.newDocumentBuilder();
			InputSource is = new InputSource();
			is.setCharacterStream(new StringReader(billerResponse));

			Document doc = db.parse(is);
			NodeList nodes = doc.getElementsByTagName("bill");

			if (nodes == null || nodes.getLength() == 0) {
				return bills;
			}
			// iterate the employees
			for (int i = 0; i < nodes.getLength(); i++) {
				Element element = (Element) nodes.item(i);
				validator.validateFields(element);
				Bill bill = constructBillbject(element);
				bills.add(bill);

			}
		} catch (JSONException | ParseException | ParserConfigurationException | SAXException | IOException e) {
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
	private Bill constructBillbject(Element element) throws JSONException, ParseException {
		Bill bill = new Bill();

		NodeList dueAmountNode = element.getElementsByTagName("dueAmount");
		Element dueAmountLine = (Element) dueAmountNode.item(0);

		bill.setDueAmount(new BigDecimal(XMLFieldsValidator.getCharacterDataFromElement(dueAmountLine)));

		NodeList dueDateNode = element.getElementsByTagName("dueDate");
		Element dueDateLine = (Element) dueDateNode.item(0);

		String dueDateStr = XMLFieldsValidator.getCharacterDataFromElement(dueDateLine);
		Date sysDate = new Date();
		String myFormatString = "dd-MM-yy";
		SimpleDateFormat df = new SimpleDateFormat(myFormatString);
		Date dueDate = null;
		dueDate = df.parse(dueDateStr);
		bill.setDueDate(dueDate);

		if (element.getElementsByTagName("fees") != null) {
			NodeList feesNode = element.getElementsByTagName("fees");
			Element feesLine = (Element) feesNode.item(0);
			bill.setFees(new BigDecimal(XMLFieldsValidator.getCharacterDataFromElement(feesLine)));
		}

		return bill;
	}
}
