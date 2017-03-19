package com.pdyjak.powerampwear;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.wearable.MessageApi;
import com.google.android.gms.wearable.MessageEvent;
import com.google.android.gms.wearable.Node;
import com.google.android.gms.wearable.Wearable;
import com.pdyjak.powerampwearcommon.ConnectionState;
import com.pdyjak.powerampwearcommon.Message;
import com.pdyjak.powerampwearcommon.NodesResolver;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;
import java.util.TreeSet;
import java.util.WeakHashMap;

/**
 * Google's Wearable Message API basically sucks.
 * Possible TODO: It would be nice to append some unique id for requests and then
 * use the same id in response from a mobile app. This would allow to create a method
 * like sendRequest(Message m, Listener l), with one-shot listeners.
 */
public class MessageExchangeHelper implements GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener, MessageApi.MessageListener {

    private static final String EXECUTE_POWERAMP_ACTION_CAPABILITY = "execute_poweramp_action";

    private static class Request implements Comparable<Request> {
        @NonNull
        final String path;
        @Nullable
        final Message data;

        Request(@NonNull String path, @Nullable Message data) {
            this.path = path;
            this.data = data;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;

            Request request = (Request) o;
            if (!path.equals(request.path)) return false;
            return data != null ? data.equals(request.data) : request.data == null;
        }

        @Override
        public int hashCode() {
            int result = path.hashCode();
            result = 31 * result + (data != null ? data.hashCode() : 0);
            return result;
        }

        @Override
        public int compareTo(@NonNull Request o) {
            return path.compareTo(o.path);
        }
    }

    @NonNull
    private final Set<MessageListener> mMessageListeners = Collections.newSetFromMap(
            new WeakHashMap<MessageListener, Boolean>());
    @NonNull
    private final GoogleApiClient mGoogleApiClient;
    @NonNull
    private final Set<Request> mRequestsQueue = new TreeSet<>();

    private NodesResolver mNodesResolver;
    private ConnectionState mConnectionState;

    MessageExchangeHelper(@NonNull Context context) {
        mGoogleApiClient = new GoogleApiClient.Builder(context, this, this)
                .addApi(Wearable.API)
                .build();
    }

    void onResume() {
        mGoogleApiClient.connect();
    }

    void onPause() {
        mGoogleApiClient.disconnect();
        Wearable.MessageApi.removeListener(mGoogleApiClient, this);
        mConnectionState = null;
    }

    @Override
    public void onConnected(@Nullable Bundle connectionHint) {
        mConnectionState = ConnectionState.Connected;
        Wearable.MessageApi.addListener(mGoogleApiClient, this);
        mNodesResolver = new NodesResolver(mGoogleApiClient, EXECUTE_POWERAMP_ACTION_CAPABILITY);
        runRequestsInQueue();
    }

    @Override
    public void onConnectionSuspended(int cause) {
        mConnectionState = ConnectionState.Suspended;
        Wearable.MessageApi.removeListener(mGoogleApiClient, this);
        mNodesResolver = null;
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
        mConnectionState = ConnectionState.FailedToConnect;
    }

    public void addMessageListenerWeakly(@NonNull MessageListener listener) {
        mMessageListeners.add(listener);
    }

    public void removeMessageListener(@NonNull MessageListener listener) {
        mMessageListeners.remove(listener);
    }

    @Override
    public void onMessageReceived(MessageEvent messageEvent) {
        Set<MessageListener> copy = new HashSet<>(mMessageListeners);
        for (MessageListener listener : copy) {
            listener.onMessageReceived(messageEvent);
        }
    }

    public void sendRequest(@NonNull String path) {
        sendRequest(path, null);
    }

    public void sendRequest(@NonNull final String path, @Nullable Message data) {
        mRequestsQueue.add(new Request(path, data));
        if (mNodesResolver == null) return;
        runRequestsInQueue();
    }

    private void runRequestsInQueue() {
        if (mRequestsQueue.isEmpty()) return;
        mNodesResolver.resolveNodes(false, new NodesResolver.Listener() {
            @Override
            public void onNodesResolved(@NonNull NodesResolver sender, @NonNull Set<Node> nodes) {
                if (sender != mNodesResolver) return;
                Set<Request> copy = new HashSet<>(mRequestsQueue);
                mRequestsQueue.clear();
                for (Node node : nodes) {
                    for (Request request : copy) {
                        byte[] data = request.data == null ? null : request.data.toBytes();
                        Wearable.MessageApi.sendMessage(mGoogleApiClient, node.getId(),
                                request.path, data);
                    }
                }
            }

            @Override
            public void onFailed() {

            }
        });
    }

    void dispose() {
        if (mConnectionState != null) mGoogleApiClient.disconnect();
    }
}
