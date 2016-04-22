// 
// Decompiled by Procyon v0.5.30
// 

package atavism.msgsys;

import java.util.Collection;

public interface IFilter {
	boolean matchMessageType(final Collection<MessageType> p0);

	boolean matchRemaining(final Message p0);

	Collection<MessageType> getMessageTypes();

	boolean applyFilterUpdate(final FilterUpdate p0, final AgentHandle p1, final SubscriptionHandle p2);

	FilterTable getSendFilterTable();

	FilterTable getReceiveFilterTable();

	FilterTable getResponderSendFilterTable();

	FilterTable getResponderReceiveFilterTable();
}
