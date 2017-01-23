package com.huan.sdk.huanpay4.been;

public class PayInfo {

	public String appSerialNo = "";  //应用流水号
	
	public String productName = "";  //产品名称
	public String productCount = "";  //产品数量
	public String productDescribe = "";  //产品描述
	public String productPrice = "";  //产品价格
	public String productDetailURL = "";  //产品价格
	
	public String orderType = "rmb";  //订单类型
	public String paymentType = "";  //支付渠道类型  可为空字符串

	public String date = "";  //发送方交易传输时间
	public String noticeUrl = "";  //后台通知url

	public String extension = "";  //扩展域  可为空字符串
	

	public String validateType = "";
	public String accountID = "";
	public String validateParam = "";
	
	public String huanID = "" ;
	public String token = "" ;
	public String termUnitNo = "" ;
	public String termUnitParam = "" ;
	
	public String appPayKey = "";
	public String signType = "md5";  //签名类型


/*	requestType	请求类型	Yes	1	固定值3，表示TV端android请求
	appSerialNo	应用流水号	Yes	50	由应用生成和维护，生成规则参照附录5.2参数生成规则
	huanID	欢id	Yes	20	由业务系统传此参数
	token	用户token	Yes	32	由业务系统传此参数
	termUnitNo	终端设备号	Yes	20	由业务系统传此参数
	termUnitParam	终端环境参数	Yes	100	该参数根据需求逐步扩展，由业务系统传此参数，目前需要的具体参数如下：
	1、设备类型：clientType；
	例：
	termUnitParam= TCL-CN-MT36-E5390A-3D
	appPayKey	应用身份标识（业务系统编号）	Yes	32	本系统分发给每个业务应用的唯一编号
	productName	产品名称	Yes	100	产品名称，多个产品以“|”（半角）分割。传参数时需要对该参数进行URLEncoder（UTF-8）编码，签名时使用编码前的原值
	productCount	产品数量	Yes	40	产品数量，多个产品以“|”（半角）分割，与产品名称对应。
	productDescribe	产品描述	No	150	产品描述，多个产品以“|”（半角）分割，与产品名称对应，其中某一项为空的情况请置为空字符串。
	传参数时需要对该参数进行URLEncoder（UTF-8）编码，签名时使用编码前的原值
	productPrice	产品价格	Yes	50	人民币以分为单位，欢币则乘100，多个产品以“|” （半角）分割，与产品名称对应。例：1000代表10元或10欢币
	产品价格必须要大于0
	orderType	订单类型	Yes	5	huan：欢币支付；rmb：人民币支付
	paymentType	支付渠道类型	No	60	用于应用方指定可显示给用户的人民币支付渠道，欢币支付不可指定（根据订单类型自动显示，orderType为欢币时显示，为人民币时不显示）。

	当只指定1个支付渠道时，则不显示支付渠道，直接跳过用户选择支付渠道页面进入指定的支付渠道进行支付；当指定多个（>1，用“|”（半角）分隔）支付渠道时，支付界面会显示指定人民币支付渠道。
	支付渠道以代码形式填写，规则如下：
	00：全部支持的人民币支付渠道；
	01：银联；
	02：快钱充值卡；
	03：支付宝；
	注：参数为空时，显示所有支持的人民币支付渠道；
	date	发送方交易传输时间	Yes	14	格式：yyyyMMddHHmmss，其中yyyy=年份，MM=月份，dd=日，HH=小时，mm=分钟，ss=秒。
	productDetailURL	商品详情页URL	No	400	应用提交的本次订单中商品的详细说明页地址（通常是用户购买时看到的页面），用于用户在支付系统查看消费记录时，点击查看。
	多个产品以“|”（半角）分割，与产品名称对应，其中某一项为空的情况请置为空字符串。
	传参数时需要对该参数进行URLEncoder（UTF-8）编码，签名时使用编码前的原值
	noticeUrl	后台通知url	No	128	计费系统消费支付结束后的响应通知地址，具体接口请求参数参照下面“消费结果响应”。
	传参数时需要对该参数进行URLEncoder（UTF-8）编码，签名时使用编码前的原值
	noticeType	后台通知类型	Yes	20	默认“http”
	extension	扩展域	No	50	计费系统消费支付结束后会在响应通知中回传给业务应用侧，没有特殊需求时为空
	signType	签名类型	No	10	不传该参数或传空字符串时，默认为证书签名；传定值“md5”时，为md5加密
	sign	签名字符串	Yes	180	签名方法参照附录5.1签名函数，根据signType来选择签名方式
	
	validateType	账户验证类型	No	10	账户验证方式，目前支持类型如下：
	不传或为空：欢网用户系统huanID验证方式，该方式使用huanID和token参数进行验证
	不为空时：欢网用户系统dnum或其他验证方式，该方式使用accountID和validateParam参数进行验证
	accountID	账户id	No	50	不支持huanID的终端对接时使用该参数，当validateType不为空时，该参数必填
	当validateType为UA00时，该值为欢网用户系统dnum
	validateParam	账户验证参数	No	100	根据validateType来指定账户验证方式，多个验证参数以“|”分隔，当validateType不为空时，该参数必填。
	当validateType为UA00时，为dnum验证相关，该值为dnum|DIDtoken

	*/
}
