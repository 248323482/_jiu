package com.jiu.websocket.endpoint;

import com.jiu.websocket.BaseWebSocketEndpoint;
import org.springframework.stereotype.Component;

import javax.websocket.server.ServerEndpoint;
import java.util.Base64;

/**
 * 2.标注@Component @ServerEndpoint
 */
@ServerEndpoint(value ="/websocket/connect/{identifier}")
public class WebSocketEndpoint extends BaseWebSocketEndpoint {
}
