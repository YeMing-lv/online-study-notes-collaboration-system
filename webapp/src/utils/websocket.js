import { Stomp } from '@stomp/stompjs';
import SockJS from 'sockjs-client/dist/sockjs.min.js';

// 存储STOMP客户端实例
let stompClient = null;

/**
 * 连接WebSocket并完成认证
 * @param {String} token - JWT令牌
 * @param {Function} onMessageReceived - 接收消息的回调函数
 * @param {Function} onError - 错误回调函数
 */
export const connectWebSocket = (token, onMessageReceived, onError) => {
  // 创建SockJS连接
  const socket = new SockJS('http://localhost:8082/ws/message1');
  
  // 创建STOMP客户端
  stompClient = Stomp.over(socket);
  
  // 关闭日志（生产环境建议开启）
  stompClient.debug = () => {};

  // 连接WebSocket，携带认证令牌
  stompClient.connect(
    {
      // 将JWT令牌放入Authorization头，格式为Bearer + 空格 + token
      Authorization: `${token}`
    },
    // 连接成功回调
    (frame) => {
      console.log('WebSocket连接成功:', frame);
      // 订阅公共频道
      // stompClient.subscribe('/topic/public', (message) => {
      //   if (message.body) {
      //     // 解析消息并调用回调
      //     onMessageReceived(JSON.parse(message.body));
      //   }
      // });
    },
    // 连接失败/错误回调
    (error) => {
      console.error('WebSocket连接失败:', error);
      onError(error);
      // 尝试重连（可选）
      // setTimeout(() => connectWebSocket(token, onMessageReceived, onError), 3000);
    }
  );
};

/**
 * 发送消息
 * @param {String} content - 消息内容
 */
export const sendMessage = (content) => {
  if (stompClient && stompClient.connected) {
    // 发送消息到后端/app/send/message路径
    stompClient.send(
      '/app/send/message',
      {},
      JSON.stringify({ content: content })
    );
  } else {
    console.error('WebSocket未连接，无法发送消息');
  }
};

/**
 * 断开WebSocket连接
 */
export const disconnectWebSocket = () => {
  if (stompClient) {
    stompClient.disconnect(() => {
      console.log('WebSocket已断开连接');
    });
  }
};