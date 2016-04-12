package com.app.empire.world.server.handler.mail;

import org.apache.log4j.Logger;

import com.app.protocol.data.AbstractData;
import com.app.protocol.handler.IDataHandler;

/**
 * 类 <code> GetMailContentHandler</code>Protocol.MAIL_GetMailContent发送邮件协议处理
 * 
 * @since JDK 1.6
 */
public class SendMailHandler implements IDataHandler {
	private Logger log;

	public SendMailHandler() {
		this.log = Logger.getLogger(SendMailHandler.class);
	}

	public AbstractData handle(AbstractData data) throws Exception {
//		ConnectSession session = (ConnectSession) data.getHandlerSource();
//		WorldPlayer player = session.getPlayer(data.getSessionId());
//		SendMail sendMail = (SendMail) data;
//		try {
//			long nowTime = System.currentTimeMillis();
//			SendMailOk sendMailOk = new SendMailOk(data.getSessionId(), data.getSerial());
//			WorldPlayer receivedPlayer = null;
//			// 意见箱邮件ReceivedId=0，ReveiverName为“”
//			if (sendMail.getReceivedId() == 0 && sendMail.getReveiverName().length() != 0) {
//				receivedPlayer = ServiceManager.getManager().getPlayerService().getWorldPlayerByName(sendMail.getReveiverName());
//				if (receivedPlayer == null) {
//					throw new ProtocolException(ErrorMessages.MAIL_PLAYER_MESSAGE, data.getSerial(), data.getSessionId(), data.getType(),
//							data.getSubType());
//				}
//				sendMail.setReceivedId(receivedPlayer.getId());
//			} else if (sendMail.getReceivedId() != 0 && sendMail.getReveiverName().length() == 0) {
//				receivedPlayer = ServiceManager.getManager().getPlayerService().getWorldPlayerById(sendMail.getReceivedId());
//				if (receivedPlayer == null) {
//					throw new ProtocolException(ErrorMessages.MAIL_PLAYER_MESSAGE, data.getSerial(), data.getSessionId(), data.getType(),
//							data.getSubType());
//				}
//			}
//			if (sendMail.getReceivedId() != 0 && sendMail.getTheme().getBytes("gbk").length > 36) {// 主题18个汉字
//				throw new ProtocolException(ErrorMessages.MAIL_THEMELONG_MESSAGE, data.getSerial(), data.getSessionId(), data.getType(),
//						data.getSubType());
//			}
//			if (sendMail.getContent().getBytes("gbk").length > 250) {// 内容150个汉字
//				throw new ProtocolException(ErrorMessages.MAIL_CONTENTLONG_MESSAGE, data.getSerial(), data.getSessionId(), data.getType(),
//						data.getSubType());
//			}
//			if (sendMail.getContent().trim().length() == 0 || (sendMail.getReceivedId() != 0 && sendMail.getTheme().trim().length() == 0)) {// 邮件主题、内容不能为空
//				throw new ProtocolException(ErrorMessages.MAIL_NOTNULL_MESSAGE, data.getSerial(), data.getSessionId(), data.getType(),
//						data.getSubType());
//			}
//			String content = KeywordsUtil.filterKeywords(sendMail.getContent());
//			String themeStr = sendMail.getTheme().replaceAll("\\pZ", "");
//			if (!(ServiceUtils.checkString(themeStr, true))) {
//				throw new ProtocolException(ErrorMessages.MAIL_NOTRIGHT_MESSAGE, data.getSerial(), data.getSessionId(), data.getType(),
//						data.getSubType());
//			}
////			Friend friend = ServiceManager.getManager().getFriendService()
////					.checkPlayerIsFriend(sendMail.getReceivedId(), sendMail.getSendId());
//			// 保存邮件
//			Mail mail = new Mail();
//			mail.setContent(content);
//			mail.setIsRead(false);
//			mail.setReceivedId(sendMail.getReceivedId());
//			if (null != receivedPlayer) {
//				mail.setReceivedName(receivedPlayer.getName());
//			}
//			mail.setSendId(sendMail.getSendId());
//			mail.setSendName(player.getName());
//			mail.setSendTime(new Date());
//			if (sendMail.getReceivedId() == 0 && sendMail.getReveiverName().length() == 0) {
//				mail.setTheme(TipMessages.OPINION);
//			} else {
//				mail.setTheme(KeywordsUtil.filterKeywords(sendMail.getTheme()));
//			}
//			mail.setType(sendMail.getMailType());
//			// 不是黑名单用户才能接收邮件
////			if (friend == null || !friend.getBlackList()) {
////				mail.setBlackMail(false);
////			} else {
////				mail.setBlackMail(true);// 是黑名单邮件
////			}
//			mail.setIsStick(Common.IS_STICK);
////			ServiceManager.getManager().getMailService().saveMail(mail, friend);
//			session.write(sendMailOk);
//		} catch (ProtocolException ex) {
//			throw ex;
//		} catch (Exception ex) {
//			log.error(ex, ex);
//			throw new ProtocolException(ErrorMessages.MAIL_FAIL_MESSAGE, data.getSerial(), data.getSessionId(), data.getType(),
//					data.getSubType());
//		}
		return null;
	}
}