//package com.jiu;
//
//import com.mxixm.fastboot.weixin.annotation.*;
//import com.mxixm.fastboot.weixin.module.event.WxEvent;
//import com.mxixm.fastboot.weixin.module.message.WxMessage;
//import com.mxixm.fastboot.weixin.module.message.WxMessageBody;
//import com.mxixm.fastboot.weixin.module.user.WxUser;
//import com.mxixm.fastboot.weixin.module.web.WxRequest;
//import com.mxixm.fastboot.weixin.module.web.WxRequestBody;
//import com.mxixm.fastboot.weixin.module.web.session.WxSession;
//import org.springframework.boot.SpringApplication;
//
//@WxApplication
//@WxController
//public class WxApp {
//
//    public static void main(String[] args) throws Exception {
//        SpringApplication.run(WxApp.class, args);
//    }
//    /**
//     * 定义微信菜单
//     */
//    @WxButton(group = WxButton.Group.MIDDLE,type = WxButton.Type.CLICK, main = true, name = "开始测试♥~")
//    @WxAsyncMessage
//    public String middle() {
//       // return WxMessage.newsBuilder().addItem("测试图文消息", "测试", "https://ss0.bdstatic.com/5aV1bjqh_Q23odCf/static/superman/img/logo/logo_white.png", "http://baidu.com").build();
//        return  "傻逼GC";
//    }
//
//    /**
//     * 接受微信事件
//     * @param wxRequest
//     * @param wxUser
//     */
//    @WxEventMapping(type = WxEvent.Type.UNSUBSCRIBE)
//    public void unsubscribe(WxRequest wxRequest, WxUser wxUser) {
//        System.out.println(wxUser.getNickName() + "退订了公众号");
//    }
//    @WxEventMapping(type = WxEvent.Type.SUBSCRIBE)
//    @WxAsyncMessage
//    public String SUBSCRIBE(WxRequest wxRequest, WxUser wxUser) {
//        return  "傻逼GC";
//    }
//
//    @WxEventMapping(type = WxEvent.Type.LOCATION)
//    public void LOCATION(WxRequest wxRequest) {
//       WxRequest.Body body =  wxRequest.getBody();
//        System.err.println(body.getLongitude());
//        System.err.println(body.getLatitude());
//        System.err.println(body.getPrecision());
//    }
//
//    /**
//     * 接受用户文本消息，异步返回文本消息
//     * @param content
//     * @return the result
//     */
//    @WxMessageMapping(type = WxMessage.Type.TEXT)
//    @WxAsyncMessage
//    public String text(WxRequest wxRequest, String content) {
//        WxSession wxSession = wxRequest.getWxSession();
//        if (wxSession != null && wxSession.getAttribute("last") != null) {
//            return "上次收到消息内容为" + wxSession.getAttribute("last");
//        }
//        return "收到消息内容为" + content;
//    }
//
//    /**
//     * 接受用户文本消息，同步返回图文消息
//     * @param content
//     * @return the result
//     */
//    @WxMessageMapping(type = WxMessage.Type.TEXT, wildcard = "1*")
//    public WxMessage message(WxSession wxSession, String content) {
//        wxSession.setAttribute("last", content);
//        return WxMessage.newsBuilder()
//                .addItem(WxMessageBody.News.Item.builder().title(content).description("随便一点")
//                        .picUrl("http://k2.jsqq.net/uploads/allimg/1702/7_170225142233_1.png")
//                        .url("http://baidu.com").build())
//                .addItem(WxMessageBody.News.Item.builder().title("第二条").description("随便二点")
//                        .picUrl("http://k2.jsqq.net/uploads/allimg/1702/7_170225142233_1.png")
//                        .url("http://baidu.com").build())
//                .build();
//    }
//
//    /**
//     * 接受用户文本消息，异步返回文本消息
//     * @param content
//     * @return the result
//     */
//    @WxMessageMapping(type = WxMessage.Type.TEXT, wildcard = "2*")
//    @WxAsyncMessage
//    public String text2(WxRequestBody.Text text, String content) {
//        boolean match = text.getContent().equals(content);
//        return "收到消息内容为" + content + "!结果匹配！" + match;
//    }
//}