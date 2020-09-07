package com.mr.config;

public class ApliPayConfig {

    //应用ID,您的APPID，收款账号既是您的APPID对应支付宝账号
    public static final String APP_ID = "2021000118642365";
    //应用私钥
    public static final String MERCHANT_PRIVATE_KEY = "MIIEvgIBADANBgkqhkiG9w0BAQEFAASCBKgwggSkAgEAAoIBAQDBzb8yXDTzRcqjLCePOzP7JfTTkv1/RVGL5El+9Li+0GdeSrrwDlokhbhN8WYqZtTzeLXDSxPsVe0iTWqDkq6N6rWdNavyeipmEOW9YV7MefUJhadP4wduHUIlocQez2cMQ818KQCdT6dc6rC1f56DwmoOWH904q7rt9nbZdm9E8OwdYX27F3xSUtMTae3lJPGj9Ez0i0w7RKTBsbwWWMw8SonUVzGB6zdN+Z+xo/EXv0B/HBDoWODBrnUxgPjUbXlxT9c57aUKGj2rd7UIgM1U1ZSeXu8HLGOE+Y+zIBn3BOO189aasDC3rzA8R4maMR01paaGDxKzHAVDyWkl6K9AgMBAAECggEACsOasvlcF4bw0k9JgzMOmAxrpLStT7xR4ysyydJd0HtSwnH0IBEsAItMyUd3HTPI8BzsO4SX/rBnE1ipadVjzI4UuD6a5kTSxgh6OiKGWWUMrxK1UEKTzxHg8qYhdnSejJfXeeXtGs+pxAdk+S+1ZiwOJ+3SZnOQs5bqir0FcvLlNHFMJwGcYcGgaewzLSX5OR/oReIqh8drxm3kQPasLn7bnxP2cjsaOKBdZfcbKdrQVkiQ8FliPnNXsR/guYLusHEP5PrabDhqSCrQSy0MYdBpH3GdKiQkNBM2Itl3e3sLs2DEuXlOVQCY8r1SiZHhtD+4XsBzQWDE0aMb0NMmIQKBgQD2ojo4Zs5wn7TAWOHhP3qtTuhSlaPlNmY+7pBwqSy9WAJ9UWm4ohKs+DXZwPmYhMIl5SFOpx+affiGZiXygtxrOHzco9jWP1vmI2fj2csjoGAti+FMCHZ1jtNtAdLA2z8UOGKPDWM+wKmzEczFJWPPpkysPT65DwWwvMMyXk1kZwKBgQDJKef9paeI7rI1vjVnSeJQToCTBzjxMhcssLkaKAYG0feHSLZNnxDRfv0f/Q/XHE8KzFXXR2KoZf+z9kBKGv8frdzl9IEiOZe6fETSnADSKJYeGIwL/dNcnWTjtoh+f36sLaJBRLpwr5MkdL/TvKEjox2QcrwdRH7bTryoKsspOwKBgQDXGPNX32mqVc8pdhtB/L/8SHZcGEABlykjN6LnbtQjKo+5RfjDWy6Yhj1I9YF5oEdHD4e19hGdBsJqTMpJsBGWvYRAVfG2IToAA98GnBsLrkJTVsyeKD9QFcmVJ2LsagqgkvB6PL3Sq+sUzrBdPFfJcQsrqJp8gXqAApDm69HpfwKBgEHGjxGz+bzfd6v0/tdAJFzn3ki1Atxy6Xr2af/weFAAlbRJhTqKOWCW6kNsZqKKohnAyIy0zcCMYXjBmzHf8+2y1tjmAtdjB4a1nXdUBTyygqtikWBWZacR2+xNQgJCw/OelEBNLb0s26r+9b5RHktdyYvyyCm1vNkOvMcr/vQDAoGBAO6shtcU0SXYflHqsbajwoqL0DV/P0hXCKALr6XP1P+ddlrcFYCCralkFinpVcGSlrLtxId7R38cD/NQ3zAUpvy5tRrXlJ7c/AHSZowN2ABNMEuudrCOqHeA3JoLceuedQma+lBplrfnSCtOnNFeaD+YEAaNh9fjxuEV59B2n/qc";
    //支付宝公钥
    public static final String ALIPAY_PUBLIC_KEY = "MIIBIjANBgkqhkiG9w0BAQEFAAOCAQ8AMIIBCgKCAQEAoCZv0tnJL8i8EPl4sx4kSg/vJPZAOWjwr3VXjAs+gzrjwrLmw5AmmipQXDQHwVElgszxFvI2G/VzG24n3I4ZrSW6KINHBgUivC2v2g3jC4+kE8GHRYKDG7AXFzZG5y8PDo2XXeAgjdgvCg1C8D8RJBz5YD2ANKC6JUKtmJJOc4VQ1ieWAmEMGxK3cayfbXCZCtnY1AxmxcX9YC8cCdvhrV4sEJPMLt0HY+G1VJaED3BQaUG6gsrRz/aOB5h4wbNtE/LIiAuL2FmCZoPms9ME0WRcJFMR0EHj7RJNOabTAn226E65/5zaAGXXm2Drc4c2+SZuBHn+3sDzWibEqKGs/QIDAQAB";
    //通知的回调地址 写一个确实支付成功改订单状态等，//必须为公网地址，
    public static final String NOTIFY_URL = "http://127.0.0.1:8089/paySuccess";

    //用户支付完成回调地址//可以写内网地址
    public static final String RETURN_URL = "http://127.0.0.1:8089/callback";
    // 签名方式
    public static String SIGN_TYPE = "RSA2";
    // 字符编码格式
    public static String CHARSET = "gbk";
    // 支付宝网关
    public static String GATEWAYURL = "https://openapi.alipaydev.com/gateway.do";

}