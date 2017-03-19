package com.pdyjak.powerampwearcommon;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.wearable.CapabilityApi;
import com.google.android.gms.wearable.CapabilityInfo;
import com.google.android.gms.wearable.Node;
import com.google.android.gms.wearable.Wearable;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

public class NodesResolver {

    public interface Listener {
        void onNodesResolved(@NonNull NodesResolver sender, @NonNull Set<Node> nodes);
        void onFailed();
    }

    private class CapabilityListener implements CapabilityApi.CapabilityListener {
        @Override
        public void onCapabilityChanged(CapabilityInfo capabilityInfo) {
            mNodes.clear();
        }
    }

    @NonNull
    private final GoogleApiClient mGoogleApiClient;
    @NonNull
    private final String mCapability;
    @NonNull
    private final Set<Listener> mListeners = new HashSet<>();
    @NonNull
    private final Set<Node> mNodes = new HashSet<>();
    @NonNull
    private final CapabilityResultCallbackHandler mCapabilityResultCallbackHandler =
            new CapabilityResultCallbackHandler();
    @NonNull
    private final CapabilityListener mCapabilityListener = new CapabilityListener();
    private boolean mResolving;

    public NodesResolver(@NonNull GoogleApiClient client, @NonNull String capability) {
        mGoogleApiClient = client;
        mCapability = capability;
        Wearable.CapabilityApi.addCapabilityListener(mGoogleApiClient, mCapabilityListener,
                capability);
    }

    public void dispose() {
        Wearable.CapabilityApi.removeCapabilityListener(mGoogleApiClient, mCapabilityListener,
                mCapability);
    }

    public void resolveNodes(boolean force, @Nullable Listener listener) {
        if (!force && !mNodes.isEmpty()) {
            if (listener != null) listener.onNodesResolved(this,
                    Collections.unmodifiableSet(mNodes));
            return;
        }
        if (listener != null) mListeners.add(listener);
        if (mResolving) return;
        mResolving = true;
        mNodes.clear();
        Wearable.CapabilityApi
                .getCapability(mGoogleApiClient, mCapability, CapabilityApi.FILTER_REACHABLE)
                .setResultCallback(mCapabilityResultCallbackHandler);
    }

    private class CapabilityResultCallbackHandler
            implements ResultCallback<CapabilityApi.GetCapabilityResult> {

        @Override
        public void onResult(@NonNull CapabilityApi.GetCapabilityResult result) {
            mResolving = false;
            CapabilityInfo info = result.getCapability();
            if (info == null) {
                notifyFailed();
                return;
            }
            Set<Node> nodes = info.getNodes();
            if (nodes == null) {
                notifyFailed();
                return;
            }
            for (Node node : nodes) {
                if (node.isNearby()) mNodes.add(node);
            }
            Set<Listener> copy = new HashSet<>(mListeners);
            mListeners.clear();
            for (Listener l : copy) {
                l.onNodesResolved(NodesResolver.this,
                        Collections.unmodifiableSet(mNodes));
            }
        }

        private void notifyFailed() {
            Set<Listener> copy = new HashSet<>(mListeners);
            mListeners.clear();
            for (Listener l : copy) {
                l.onFailed();
            }
        }
    }
}
