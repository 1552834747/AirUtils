package com.wxpayutil.util;

import com.github.wxpay.sdk.WXPayUtil;

import java.util.HashMap;
import java.util.Map;

/**
 * 微信支付工具类
 */
public class WXPay {
    //支付类型
    public final String[] tradeType = {"JSAPI","NATIVE","APP"};

    //链接前缀
    private final String URL_PRE = "https://api.mch.weixin.qq.com/";
    //沙箱环境url
    private final String URL_TEST = "sandboxnew/";

    //应用id
    private String APP_ID = "";
    //商户号
    private String MCH_ID = "";
    //密钥
    private String KEY = "";

    private Map<String, String> commonMap;

    public WXPay(String appid, String mchId, String key) {
        this.APP_ID = appid;
        this.MCH_ID = mchId;
        this.KEY = key;
        commonMap = new HashMap<>();
        commonMap.put("appid", APP_ID);                       //应用ip
        commonMap.put("mch_id", MCH_ID);                      //商户号
    }

    /**
     * 统一下单
     * @param body          商品描述
     * @param outTradeNo    商品订单号
     * @param totalFee      标价金额 单位分 整数
     * @param tradeType     交易类型  详细：TradeType
     * @return      trade_type     交易类型
     *               prepay_id      预支付交易会话标识 后继接口调用 有效期2h
     *               code_url       交易类型为NATIVE时有返回，此url用于生成支付二维码，然后提供给用户进行扫码支付。
     */
    public Map<String, String> unifiedorder(String body, String outTradeNo, int totalFee, String tradeType) {
        String URL = URL_PRE+"pay/unifiedorder";
        Map<String, String> param = getParam();
        param.put("body", body);                            //商品描述
        param.put("out_trade_no", outTradeNo);              //商户订单号
        param.put("total_fee", "" + totalFee);                   //标价金额 单位分 不支持小数
        param.put("spbill_create_ip", "127.0.0.1");         //终端IP
        param.put("notify_url", "http://www.baidu.com");    //回调地址 不使用
        param.put("trade_type", tradeType);                 //交易类型
        //发起请求
        return request(param, URL);
    }

    /**
     * 查询订单 参数 二选一 其余填空串
     * @param transactionId     微信订单号
     * @param outTradeNo        商户订单号
     * @return      openid          用户标识
     *               trade_type      交易类型
     *               total_fee       标价金额
     *               transaction_id  微信支付订单号
     *               out_trade_no    商户订单号
     *               time_end        支付完成时间 格式为yyyyMMddHHmmss
     */
    public Map<String, String> orderquery(String transactionId, String outTradeNo) {
        String URL = URL_PRE+"pay/orderquery";
        Map<String, String> param = getParam();
        if (!"".equals(transactionId)) {
            param.put("transaction_id", transactionId);         //	微信的订单号，建议优先使用
        } else {
            param.put("out_trade_no", outTradeNo);              //商户订单号
        }
        Map<String, String> response = request(param, URL);
        if (response != null) {
            if ("SUCCESS".equals(response.get("trade_state"))) {        //trade_state  支付成功
                return response;
            } else {
                //REFUND—转入退款
                // NOTPAY—未支付
                // CLOSED—已关闭
                // REVOKED—已撤销（付款码支付）
                // USERPAYING--用户支付中（付款码支付）
                // PAYERROR--支付失败(其他原因，如银行返回失败)
                return response;
            }
        }
        return null;
    }


    /**
     * 关闭订单
     * @param outTradeNo    商户订单号
     * @return
     */
    public Map<String, String> closeorder(String outTradeNo) {
        String URL = URL_PRE+"pay/closeorder";
        Map<String, String> param = getParam();
        param.put("out_trade_no", outTradeNo);              //商户订单号
        Map<String, String> response = request(param, URL);
        return response;
    }


    /**
     * 申请退款 订单号参数二选一 其余填空串
     * @param transactionId     微信订单号
     * @param outTradeNo        商户订单号
     * @param outRefundNo       商户退款号
     * @param totalFee          订单金额
     * @param refundFee         退款金额
     * @return     transaction_id      微信订单号
     *              out_trade_no        商户订单号
     *              out_refund_no       商户退款单号
     *              refund_id           微信退款单号
     *              refund_fee          退款金额
     *              total_fee           订单金额
     */
    public Map<String, String> refund(String transactionId, String outTradeNo, String outRefundNo, int totalFee, int refundFee) {
        String URL = URL_PRE+"pay/refund";
        Map<String, String> param = getParam();
        if (!"".equals(transactionId)) {
            param.put("transaction_id", transactionId);      //微信订单号
        } else {
            param.put("out_trade_no", outTradeNo);           //商户订单号
        }
        param.put("out_refund_no", outRefundNo);            //商户退款单号
        param.put("total_fee", "" + totalFee);                //订单金额 单位分
        param.put("refund_fee", "" + refundFee);              //退款金额 单位分
        return request(param, URL);
    }


    /**
     * 查询退款 参数四选一 其余填空串
     * @param transactionId 微信订单号        4
     * @param outTradeNo    商户订单号        3
     * @param outRefundNo   商户退款单号      2
     * @param refundId      微信退款单号      1
     * @return     transaction_id      微信订单号
     *              out_trade_no        商户订单号
     *              total_fee           订单金额
     *              refund_count        退款笔数
     *              out_refund_no_$n    商户退款单号      $n为下标,从0开始编号
     *              refund_id_$n        微信退款单号
     *              refund_fee_$n       申请退款金额
     *              refund_status_$n    退款状态 SUCCESS—退款成功 REFUNDCLOSE—退款关闭。PROCESSING—退款处理中 CHANGE—退款异常，
     */
    public Map<String, String> refundquery(String transactionId, String outTradeNo, String outRefundNo, String refundId) {
        String URL = URL_PRE+"pay/refundquery";
        Map<String, String> param = getParam();               //设置请求参数
        if (!"".equals(refundId)) {
            param.put("refund_id", refundId);                //微信退款单号
        } else if (!"".equals(outRefundNo)) {
            param.put("out_refund_no", outTradeNo);           //商户订单号
        } else if (!"".equals(transactionId)) {
            param.put("transaction_id", transactionId);      //微信订单号
        } else if (!"".equals(outTradeNo)) {
            param.put("out_trade_no", outTradeNo);           //商户订单号
        }
        return request(param, URL);
    }


    /**
     * 发起POST请求
     *
     * @param param 请求参数 xml格式
     * @param URL   请求url
     * @return
     */
    private Map<String, String> request(Map<String, String> param, String URL) {
        try {
            //生成要发送的xml
            String requestXml = WXPayUtil.generateSignedXml(param, KEY);
            System.out.println(requestXml);
            HttpClient httpClient = new HttpClient(URL);
            httpClient.setHttps(true);
            httpClient.setXmlParam(requestXml);
            httpClient.post();
            String responseXml = httpClient.getContent();
            Map<String, String> response = WXPayUtil.xmlToMap(responseXml);
            if ("SUCCESS".equals(response.get("return_code"))) {        //通信标识，表示接口层的请求结果
                if ("SUCCESS".equals(response.get("result_code"))) {    //业务结果
                    return response;
                } else {
                    System.out.println(response.get("err_code"));
                    System.out.println(response.get("err_code_des"));
                }
            } else {
                System.out.println(response.get("return_msg"));          //错误原因
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * 获取请求基础参数
     */
    private Map<String,String> getParam(){
        Map<String, String> param = new HashMap<>();
        param.putAll(commonMap);
        param.put("nonce_str", WXPayUtil.generateNonceStr());           //随机字符串
        return param;
    }
}
