package com.agrovision.sindh;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.agrovision.sindh.databinding.ItemChatAssistantBinding;
import com.agrovision.sindh.databinding.ItemChatUserBinding;

import java.util.List;

public class ChatAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    public interface OnRetryClickListener {
        void onRetryClick(ChatMessage message);
    }

    public interface OnSpeakClickListener {
        void onSpeakClick(ChatMessage message);
    }

    private final List<ChatMessage> messageList;
    private final OnRetryClickListener retryListener;
    private final OnSpeakClickListener speakListener;
    private String currentlySpeakingMessageId = null;

    public ChatAdapter(List<ChatMessage> messageList, OnRetryClickListener retryListener, OnSpeakClickListener speakListener) {
        this.messageList = messageList;
        this.retryListener = retryListener;
        this.speakListener = speakListener;
    }

    public void setCurrentlySpeakingMessageId(String messageId) {
        this.currentlySpeakingMessageId = messageId;
        notifyDataSetChanged();
    }

    @Override
    public int getItemViewType(int position) {
        return messageList.get(position).getType();
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        if (viewType == ChatMessage.TYPE_USER) {
            ItemChatUserBinding binding = ItemChatUserBinding.inflate(inflater, parent, false);
            return new UserViewHolder(binding);
        } else {
            ItemChatAssistantBinding binding = ItemChatAssistantBinding.inflate(inflater, parent, false);
            return new AssistantViewHolder(binding);
        }
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        ChatMessage message = messageList.get(position);
        if (holder instanceof UserViewHolder) {
            ((UserViewHolder) holder).bind(message);
        } else if (holder instanceof AssistantViewHolder) {
            boolean isSpeaking = message.getId().equals(currentlySpeakingMessageId);
            ((AssistantViewHolder) holder).bind(message, retryListener, speakListener, isSpeaking);
        }
    }

    @Override
    public int getItemCount() {
        return messageList.size();
    }

    static class UserViewHolder extends RecyclerView.ViewHolder {
        private final ItemChatUserBinding binding;

        UserViewHolder(ItemChatUserBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }

        void bind(ChatMessage message) {
            binding.tvUserMessage.setText(message.getText());
        }
    }

    static class AssistantViewHolder extends RecyclerView.ViewHolder {
        private final ItemChatAssistantBinding binding;

        AssistantViewHolder(ItemChatAssistantBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }

        void bind(ChatMessage message, OnRetryClickListener retryListener, OnSpeakClickListener speakListener, boolean isSpeaking) {
            binding.tvAssistantMessage.setText(message.getText());
            if (message.isError()) {
                binding.tvRetry.setVisibility(View.VISIBLE);
                binding.layoutAudioControls.setVisibility(View.GONE);
                binding.tvRetry.setOnClickListener(v -> {
                    if (retryListener != null) {
                        retryListener.onRetryClick(message);
                    }
                });
            } else {
                binding.tvRetry.setVisibility(View.GONE);
                binding.layoutAudioControls.setVisibility(View.VISIBLE);

                if (isSpeaking) {
                    binding.btnSpeakMessage.setImageResource(R.drawable.ic_stop);
                    binding.tvSpeakingStatus.setVisibility(View.VISIBLE);
                } else {
                    binding.btnSpeakMessage.setImageResource(R.drawable.ic_volume_up);
                    binding.tvSpeakingStatus.setVisibility(View.GONE);
                }

                binding.btnSpeakMessage.setOnClickListener(v -> {
                    if (speakListener != null) {
                        speakListener.onSpeakClick(message);
                    }
                });
            }
        }
    }
}
