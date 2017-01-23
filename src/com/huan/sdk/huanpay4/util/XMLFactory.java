package com.huan.sdk.huanpay4.util;

import org.xml.sax.Attributes;
import org.xml.sax.InputSource;
import org.xml.sax.XMLReader;
import org.xml.sax.helpers.DefaultHandler;
import com.huan.sdk.huanpay4.been.InitPayResult;

import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

public class XMLFactory {

	private SAXParserFactory factory;
	private SAXParser parser;
	private XMLReader xmlreader;
	private static XMLFactory xmlFactory;

	private XMLFactory() {
		factory = SAXParserFactory.newInstance();
		try {
			parser = factory.newSAXParser();
			xmlreader = parser.getXMLReader();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public synchronized static XMLFactory getInstance() {
		if (xmlFactory == null) {
			xmlFactory = new XMLFactory();
		}
		return xmlFactory;
	}

	/**
	 * 解析订单返回XML
	 * 
	 * @param is
	 * @return
	 * @throws Exception
	 */
	public InitPayResult parseInitPayRequest(InputSource is) throws Exception {
		if (factory == null)
			parser = factory.newSAXParser();
		if (xmlreader == null)
			xmlreader = parser.getXMLReader();

		final InitPayResult initPayResult = new InitPayResult();

		xmlreader.setContentHandler(new DefaultHandler() {

			private StringBuilder sb;

			public void startDocument() {
				sb = new StringBuilder();
			}

			public void characters(char[] ch, int start, int length) {
				sb.append(ch, start, length);
			}

			public void startElement(String uri, String localName, String name,
									 Attributes attributes) {
				sb.setLength(0);
			}

			public void endElement(String uri, String localName, String name) {

				if (name.equals("respResult")) {
					initPayResult.respResult = sb.toString();
				} else if (name.equals("orderNo")) {
					initPayResult.orderNo = sb.toString();
				} else if (name.equals("paymentType")) {
					initPayResult.paymentType = sb.toString();
				} else if (name.equals("orderAmount")) {
					initPayResult.orderAmount = sb.toString();
				} else if (name.equals("payAmount")) {
					initPayResult.payAmount = sb.toString();
				} else if (name.equals("accountBalance")) {
					initPayResult.accountBalance = sb.toString();
				} else if (name.equals("huanAmount")) {
					initPayResult.huanAmount = sb.toString();
				} else if (name.equals("isNewAccount")) {// 是否是新增huanID账户
																// 新增为true，否则为false
					initPayResult.isNewAccount = sb.toString();
				} else if (name.equals("giveHuanAmount")) {// 设置密码赠送欢币数额
																// 为空时，不赠送
					initPayResult.giveHuanAmount = sb.toString();
				} else if (name.equals("payUserInfo")) {// 用户支付相关信息
																// 支付宝一键支付：绑定的支付宝账号
					initPayResult.payUserInfo = sb.toString();
				} else if (name.equals("orderType")) {
					initPayResult.orderType = sb.toString();
				} else if (name.equals("smallPay")) {
					initPayResult.smallPay = sb.toString();
				} else if (name.equals("errorInfo")) {
					initPayResult.errorInfo = sb.toString();
				} else if (name.equals("sign")) {
					initPayResult.sign = sb.toString();
				}
				sb.setLength(0);
			}

			public void endDocument() {
				if (initPayResult.respResult.equals("success"))
					initPayResult.isSuccess = true;
			}

		});

		xmlreader.parse(is);
		return initPayResult;
	}


}
