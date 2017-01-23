package com.huan.sdk.huanpay4.been;

public class InitPayResult {

	/**
	 * 此类融合了初始化订单的返回和刷新订单的返回
	 */
	
/*	respResult	响应结果	结果：success；failure
	orderNo	订单号	服务器端生成的订单号
	orderAmount	订单金额	根据orderType来判断，如果人民币订单，为人民币金额；欢币订单，为欢币数额
	paymentType	可用支付渠道	人民币订单时，根据第三方应用指定及服务器端配置的可用支付渠道交集例：01|02|03
	payAmount	支付金额	人民币订单时，返回各支付渠道需要支付的金额例：1000|1100|1050
	accountBalance	欢币余额	在android端显示账户欢币余额用
	huanAmount	支付人民币订单需要的欢币数额	人民币订单时，该项有值
	isNewAccount	是否是新增huanID账户	新增为true，否则为false
	smallPay	小额支付	true为不需要输入支付密码、false为需要输入支付密码 
	errorInfo	错误信息	参数错误、系统异常等错误信息
	giveHuanAmount 设置密码赠送欢币数额  为空时，不赠送
	payUserInfo  用户支付相关信息 支付宝一键支付：绑定的支付宝账号
	当结果为failure时，errorInfo有值
	sign	签名字符串	返回数据<data></data>中xml字符串进行签名*/

/*	respResult	响应结果	结果：success；failure；orderCompleted
	返回orderCompleted表示订单已经支付
	orderNo	订单号	服务器端生成的订单号
	orderType	订单类型	huan：欢币支付；rmb：人民币支付
	orderAmount	订单金额	根据orderType来判断，如果人民币订单，为人民币金额；欢币订单，为欢币数额
	paymentType	可用支付渠道	人民币订单时，根据第三方应用指定及服务器端配置的可用支付渠道交集例：01|02|03
	payAmount	支付金额	人民币订单时，返回各支付渠道需要支付的金额例：1000|1100|1050
	accountBalance	欢币余额	在android端显示账户欢币余额用
	huanAmount	支付人民币订单需要的欢币数额	人民币订单时，该项有值
	smallPay	小额支付	true为不需要输入支付密码、false为需要输入支付密码 
	errorInfo	错误信息	参数错误、系统异常等错误信息
	当结果为failure时，errorInfo有值
	sign	签名字符串	返回数据<data></data>中xml字符串进行签名*/

	
	public String respResult="";
	public String orderNo;
	public String orderAmount;
	public String paymentType;
	public String payAmount;
	public String accountBalance;
	public String smallPay;
	public String errorInfo;
	public String giveHuanAmount;
	public String payUserInfo;
	public String sign;
	
	public String huanAmount;
	
	public String isNewAccount="";
	public String orderType;
	
	public boolean isSuccess;
	public boolean signSuccess;
	public String payOrderNo;

	public String getRespResult() {
		return respResult;
	}

	public void setRespResult(String respResult) {
		this.respResult = respResult;
	}

	public String getOrderNo() {
		return orderNo;
	}

	public void setOrderNo(String orderNo) {
		this.orderNo = orderNo;
	}

	public String getOrderAmount() {
		return orderAmount;
	}

	public void setOrderAmount(String orderAmount) {
		this.orderAmount = orderAmount;
	}

	public String getPaymentType() {
		return paymentType;
	}

	public void setPaymentType(String paymentType) {
		this.paymentType = paymentType;
	}

	public String getPayAmount() {
		return payAmount;
	}

	public void setPayAmount(String payAmount) {
		this.payAmount = payAmount;
	}

	public String getAccountBalance() {
		return accountBalance;
	}

	public void setAccountBalance(String accountBalance) {
		this.accountBalance = accountBalance;
	}

	public String getSmallPay() {
		return smallPay;
	}

	public void setSmallPay(String smallPay) {
		this.smallPay = smallPay;
	}

	public String getErrorInfo() {
		return errorInfo;
	}

	public void setErrorInfo(String errorInfo) {
		this.errorInfo = errorInfo;
	}

	public String getGiveHuanAmount() {
		return giveHuanAmount;
	}

	public void setGiveHuanAmount(String giveHuanAmount) {
		this.giveHuanAmount = giveHuanAmount;
	}

	public String getPayUserInfo() {
		return payUserInfo;
	}

	public void setPayUserInfo(String payUserInfo) {
		this.payUserInfo = payUserInfo;
	}

	public String getSign() {
		return sign;
	}

	public void setSign(String sign) {
		this.sign = sign;
	}

	public String getHuanAmount() {
		return huanAmount;
	}

	public void setHuanAmount(String huanAmount) {
		this.huanAmount = huanAmount;
	}

	public String getIsNewAccount() {
		return isNewAccount;
	}

	public void setIsNewAccount(String isNewAccount) {
		this.isNewAccount = isNewAccount;
	}

	public String getOrderType() {
		return orderType;
	}

	public void setOrderType(String orderType) {
		this.orderType = orderType;
	}

	public boolean isSuccess() {
		return isSuccess;
	}

	public void setSuccess(boolean success) {
		isSuccess = success;
	}

	public boolean isSignSuccess() {
		return signSuccess;
	}

	public void setSignSuccess(boolean signSuccess) {
		this.signSuccess = signSuccess;
	}

	public String getPayOrderNo() {
		return payOrderNo;
	}

	public void setPayOrderNo(String payOrderNo) {
		this.payOrderNo = payOrderNo;
	}
}
