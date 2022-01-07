package ru.etysoft.cute.components;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.DecelerateInterpolator;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import ru.etysoft.cute.R;
import ru.etysoft.cute.lang.CustomLanguage;
import ru.etysoft.cute.lang.StringsRepository;
import ru.etysoft.cuteframework.methods.messages.Message;

public class ForwardedMessage extends RelativeLayout {

    private TextView senderTextView;
    private TextView messageView;
    private View rootView;


    public ForwardedMessage(Context context, AttributeSet attrs) {
        super(context, attrs);

    }

    public boolean wasInitialized = false;

    public void initComponent(boolean isWhite) {

        if (!wasInitialized) {
            LayoutInflater inflater = (LayoutInflater) getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            if (isWhite) {
                rootView = inflater.inflate(R.layout.forwarded_message_white, this);
            } else {
                rootView = inflater.inflate(R.layout.forwarded_message, this);
            }


            messageView = findViewById(R.id.forwardedMessageText);
            senderTextView = findViewById(R.id.senderDisplayName);
            wasInitialized = true;
        }
    }


    public void setContent(Message forwardedMessage, Context context) {
        try {
            messageView = findViewById(R.id.forwardedMessageText);
            senderTextView = findViewById(R.id.senderDisplayName);
            String fwdMessage;

            if (forwardedMessage.getText() == null) {
                if (forwardedMessage.getAttachmentData() != null) {
                    fwdMessage = CustomLanguage.getStringsRepository().getOrDefault(R.string.image, context);
                } else {
                    if (forwardedMessage.getForwardedMessage() != null) {
                        fwdMessage = CustomLanguage.getStringsRepository().getOrDefault(R.string.fwd_message, context);
                    } else {
                        fwdMessage = CustomLanguage.getStringsRepository().getOrDefault(R.string.empty_message, context);
                    }

                }
            } else {
                fwdMessage = forwardedMessage.getText();
            }
            messageView.setText(fwdMessage);
            senderTextView.setText(forwardedMessage.getSender().getDisplayName());
        } catch (Exception e)
        {
            e.printStackTrace();
        }
    }


    @Override
    public View getRootView() {
        return rootView;
    }

}
