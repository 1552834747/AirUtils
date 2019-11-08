package com.alipayutil.utils;

import com.alipay.api.AlipayApiException;
import com.alipay.api.AlipayClient;
import com.alipay.api.AlipayResponse;
import com.alipay.api.DefaultAlipayClient;
import com.alipay.api.domain.*;
import com.alipay.api.request.*;
import com.alipay.api.response.*;

/**
 * 支付宝支付工具类
 */
public class AliPay {

    //支付宝网关
    //private String URL = "https://openapi.alipay.com/gateway.do";
    //沙箱环境网关（测试用）
    private String URL = "https://openapi.alipaydev.com/gateway.do";
    //签名算法类型    需要与密钥类型一致
    private String SIGN_TYPE = "RSA2";//"RSA"
    //请求和签名使用的字符编码格式，支持 GBK和 UTF-8
    private String CHARSET = "UTF-8";
    //参数返回格式
    private final String FORMAT = "json";


    //应用ID
    private String APP_ID;
    //开发者应用私钥，由开发者自己生成
    private String APP_PRIVATE_KEY;
    //支付宝公钥，由支付宝生成
    private String ALIPAY_PUBLIC_KEY;

    private AlipayClient alipayClient;

    /**
     * 创建对象
     *
     * @param appId     应用id
     * @param appKey    应用私钥
     * @param alipayKey 支付宝公钥
     */
    public AliPay(String appId, String appKey, String alipayKey) {
        this.APP_ID = appId;
        this.APP_PRIVATE_KEY = appKey;
        this.ALIPAY_PUBLIC_KEY = alipayKey;
        this.alipayClient = new DefaultAlipayClient(URL, APP_ID, APP_PRIVATE_KEY, FORMAT, CHARSET, ALIPAY_PUBLIC_KEY, SIGN_TYPE);
    }

    public AliPay(String appId, String appKey, String alipayKey, String signType, String charset){
        this.APP_ID = appId;
        this.APP_PRIVATE_KEY = appKey;
        this.ALIPAY_PUBLIC_KEY = alipayKey;
        this.SIGN_TYPE = signType;
        this.CHARSET = charset;
        this.alipayClient = new DefaultAlipayClient(URL, APP_ID, APP_PRIVATE_KEY, FORMAT, CHARSET, ALIPAY_PUBLIC_KEY, SIGN_TYPE);
    }

    /**
     * 统一支付线下交易预创建（扫码支付）
     * @param outTradeNo     订单号
     * @param totalAmount    总金额
     * @param subject        商品标题
     * @param timeoutExpress 最晚支付时间  1m～15d。m-分钟，h-小时，d-天，1c-当天
     */
    public AlipayTradePrecreateResponse precreate(String outTradeNo, String totalAmount, String subject, String timeoutExpress) throws AlipayApiException {
        AlipayTradePrecreateRequest request = new AlipayTradePrecreateRequest();
        AlipayTradePrecreateModel model = new AlipayTradePrecreateModel();
        model.setOutTradeNo(outTradeNo);
        model.setTotalAmount(totalAmount);
        model.setSubject(subject);
        model.setTimeoutExpress(timeoutExpress);
        request.setBizModel(model);
        return alipayClient.execute(request);
    }

    /**
     * 统一支付线下交易预创建（扫码支付）
     * @param model 自定义请求参数 必传：outTradeNo、totalAmount、subject
     */
    public AlipayTradePrecreateResponse precreate(AlipayTradePrecreateModel model) throws AlipayApiException {
        AlipayTradePrecreateRequest request = new AlipayTradePrecreateRequest();
        request.setBizModel(model);
        return alipayClient.execute(request);
    }


    /**
     * 统一线下交易查询接口（订单查询）两参数不能同时为空
     * @param tradeNo    支付宝生成的交易号
     * @param outTradeNo 商家维护的订单号
     */
    public AlipayTradeQueryResponse query(String tradeNo, String outTradeNo) throws AlipayApiException {
        AlipayTradeQueryRequest request = new AlipayTradeQueryRequest();
        AlipayTradeQueryModel model = new AlipayTradeQueryModel();
        if (!"".equals(tradeNo)) {
            model.setTradeNo(tradeNo);
        }
        if (!"".equals(outTradeNo)) {
            model.setOutTradeNo(outTradeNo);
        }
        request.setBizModel(model);
        return alipayClient.execute(request);
    }


    /**
     * 统一线下交易查询接口（订单查询）
     * @param model 自定义参数 必传：tradeNo || outTradeNo 二选一
     */
    public AlipayTradeQueryResponse query(AlipayTradeQueryModel model) throws AlipayApiException {
        AlipayTradeQueryRequest request = new AlipayTradeQueryRequest();
        request.setBizModel(model);
        return alipayClient.execute(request);
    }

    /**
     * 统一收单交易退款接口
     * @param outTradeNo   商户维护的订单id
     * @param tradeNo      支付宝生成的交易号
     * @param refundAmount 需退款的金额 单位元 支持两个小数 不能大于订单金额
     * @param outRequestNo 表示一次退款请求 同一笔交易多次退款需要保证唯一，如果需要部分退款，则此参数必传
     */
    public AlipayTradeRefundResponse refund(String outTradeNo, String tradeNo, String refundAmount, String outRequestNo) throws AlipayApiException {
        AlipayTradeRefundRequest request = new AlipayTradeRefundRequest();
        AlipayTradeRefundModel model = new AlipayTradeRefundModel();
        if (!"".equals(outTradeNo)) {
            model.setOutTradeNo(outTradeNo);
        }
        if (!"".equals(tradeNo)) {
            model.setTradeNo(tradeNo);
        }
        if (!"".equals(refundAmount)) {
            model.setRefundAmount(refundAmount);
        }
        if (!"".equals(outRequestNo)) {
            model.setOutRequestNo(outRequestNo);
        }
        request.setBizModel(model);
        return alipayClient.execute(request);
    }


    /**
     * 统一收单交易退款接口
     * @param model 自定义参数 必传：outTradeNo || tradeNo 、 refundAmount、 outRequestNo（多次退款时必传）
     */
    public AlipayTradeRefundResponse refund(AlipayTradeRefundModel model) throws AlipayApiException {
        AlipayTradeRefundRequest request = new AlipayTradeRefundRequest();
        request.setBizModel(model);
        return alipayClient.execute(request);
    }


    /**
     * 统一收单交易退款查询
     * @param tradeNo      支付宝生成的交易号
     * @param outTradeNo   商家订单号
     * @param outRequestNo 退款请求号 如果在退款请求时未传入，则该值为创建交易时的外部交易号
     */
    public AlipayTradeFastpayRefundQueryResponse fastpayRefundQuery(String tradeNo, String outTradeNo, String outRequestNo) throws AlipayApiException {
        AlipayTradeFastpayRefundQueryRequest request = new AlipayTradeFastpayRefundQueryRequest();
        AlipayTradeFastpayRefundQueryModel model = new AlipayTradeFastpayRefundQueryModel();
        if (!"".equals(tradeNo)) {
            model.setTradeNo(tradeNo);
        }
        if (!"".equals(outTradeNo)) {
            model.setOutTradeNo(outTradeNo);
        }
        model.setOutRequestNo(outRequestNo);
        request.setBizModel(model);
        return alipayClient.execute(request);
    }

    /**
     * 统一收单交易撤销接口:支付交易返回失败或支付系统超时，调用该接口撤销交易。如已经支付会退款
     * @param tradeNo       支付宝生成的交易号
     * @param outTradeNo    商家维护的订单号
     */
    public AlipayTradeCancelResponse cancel(String tradeNo, String outTradeNo) throws AlipayApiException {
        AlipayTradeCancelRequest request = new AlipayTradeCancelRequest();
        AlipayTradeCancelModel model = new AlipayTradeCancelModel();
        if (!"".equals(tradeNo)) {
            model.setTradeNo(tradeNo);
        }
        if (!"".equals(outTradeNo)) {
            model.setOutTradeNo(outTradeNo);
        }
        request.setBizModel(model);
        return alipayClient.execute(request);
    }

    /**
     * 获取成功与否
     */
    public static Boolean getSuccess(AlipayResponse response){
        return response.isSuccess();
    }


}
