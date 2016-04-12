// 
// Decompiled by Procyon v0.5.30
// 

package atavism.server.engine;

import atavism.msgsys.GenericResponseMessage;
import atavism.msgsys.ResponseMessage;
import atavism.msgsys.Message;
import java.util.LinkedList;
import atavism.msgsys.ResponseCallback;
import java.util.HashMap;
import atavism.server.util.Log;
import atavism.msgsys.MessageCallback;
import atavism.msgsys.IFilter;
import atavism.server.messages.SearchMessageFilter;
import atavism.server.messages.SearchMessage;
import java.util.Collection;
import atavism.server.objects.ObjectType;
import java.util.Map;

public class SearchManager
{
    static Map<MatcherKey, MatcherFactory> matchers;
    
    public static Collection searchObjects(final ObjectType objectType, final SearchClause searchClause, final SearchSelection selection) {
        final SearchMessage message = new SearchMessage(objectType, searchClause, selection);
        final Collector collector = new Collector(message);
        return collector.getResults();
    }
    
    public static void registerSearchable(final ObjectType objectType, final Searchable searchable) {
        final SearchMessageFilter filter = new SearchMessageFilter(objectType);
        Engine.getAgent().createSubscription(filter, new SearchMessageCallback(searchable), 8);
    }
    
    public static void registerMatcher(final Class searchClauseClass, final Class instanceClass, final MatcherFactory matcherFactory) {
        SearchManager.matchers.put(new MatcherKey(searchClauseClass, instanceClass), matcherFactory);
    }
    
    public static Matcher getMatcher(final SearchClause searchClause, final Class instanceClass) {
        final MatcherFactory matcherFactory = SearchManager.matchers.get(new MatcherKey(searchClause.getClass(), instanceClass));
        if (matcherFactory == null) {
            Log.error("runSearch: No matcher for " + searchClause.getClass() + " " + instanceClass);
            return null;
        }
        return matcherFactory.createMatcher(searchClause);
    }
    
    static {
        SearchManager.matchers = new HashMap<MatcherKey, MatcherFactory>();
    }
    
    static class Collector implements ResponseCallback
    {
        Collection results;
        SearchMessage searchMessage;
        int responders;
        
        public Collector(final SearchMessage message) {
            this.results = new LinkedList();
            this.responders = 0;
            this.searchMessage = message;
        }
        
        public Collection getResults() {
            final int expectedResponses = Engine.getAgent().sendBroadcastRPC(this.searchMessage, this);
            synchronized (this) {
                this.responders += expectedResponses;
                while (this.responders != 0) {
                    try {
                        this.wait();
                    }
                    catch (InterruptedException e) {}
                }
            }
            return this.results;
        }
        
        @Override
        public synchronized void handleResponse(final ResponseMessage rr) {
            --this.responders;
            final GenericResponseMessage response = (GenericResponseMessage)rr;
            final Collection list = (Collection)response.getData();
            if (list != null) {
                this.results.addAll(list);
            }
            if (this.responders == 0) {
                this.notify();
            }
        }
    }
    
    static class MatcherKey
    {
        public Class queryType;
        public Class instanceType;
        
        public MatcherKey(final Class qt, final Class it) {
            this.queryType = qt;
            this.instanceType = it;
        }
        
        @Override
        public boolean equals(final Object key) {
            return ((MatcherKey)key).queryType == this.queryType && ((MatcherKey)key).instanceType == this.instanceType;
        }
        
        @Override
        public int hashCode() {
            return this.queryType.hashCode() + this.instanceType.hashCode();
        }
    }
    
    static class SearchMessageCallback implements MessageCallback
    {
        Searchable searchable;
        
        public SearchMessageCallback(final Searchable searchable) {
            this.searchable = searchable;
        }
        
        @Override
        public void handleMessage(final Message msg, final int flags) {
            final SearchMessage message = (SearchMessage)msg;
            Collection result = null;
            try {
                result = this.searchable.runSearch(message.getSearchClause(), message.getSearchSelection());
            }
            catch (Exception e) {
                Log.exception("runSearch failed", e);
            }
            Engine.getAgent().sendObjectResponse(message, result);
        }
    }
}
