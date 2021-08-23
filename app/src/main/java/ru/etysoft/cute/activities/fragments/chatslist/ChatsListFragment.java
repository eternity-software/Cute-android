package ru.etysoft.cute.activities.fragments.chatslist;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;

import java.util.ArrayList;

import ru.etysoft.cute.R;
import ru.etysoft.cute.activities.ChatsSearch;
import ru.etysoft.cute.activities.CreateChatActivity;
import ru.etysoft.cute.activities.chatslist.ChatSnippetInfo;
import ru.etysoft.cute.activities.chatslist.ChatsListAdapter;
import ru.etysoft.cute.components.ErrorPanel;


public class ChatsListFragment extends Fragment implements ChatsListContact.View {

    private ChatsListAdapter adapter;
    private ProgressBar progressBar;
    private View view;
    private ErrorPanel errorPanel;
    private ChatsListContact.Presenter presenter;
    private Toolbar toolbar;

    @SuppressLint("ClickableViewAccessibility")
    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.activity_chats, container, false);
        view = root;
        presenter = new ChatsListPresenter(getActivity(), this);
        initViews();
        presenter.updateChatsList(progressBar, adapter, toolbar, errorPanel, (LinearLayout) view.findViewById(R.id.empty), (LinearLayout) view.findViewById(R.id.error));

        return root;
    }

    @Override
    public void onResume() {
        super.onResume();
        presenter.updateChatsList(progressBar, adapter, toolbar, errorPanel, (LinearLayout) view.findViewById(R.id.empty), (LinearLayout) view.findViewById(R.id.error));
    }

    @Override
    public void initViews() {
        ListView listView = view.findViewById(R.id.listView);
        toolbar = view.findViewById(R.id.toolbar);
        adapter = new ChatsListAdapter(getActivity(), new ArrayList<ChatSnippetInfo>());
        listView.setAdapter(adapter);

        progressBar = view.findViewById(R.id.loading);
        errorPanel = view.findViewById(R.id.error_panel);
        errorPanel.getRootView().setVisibility(View.INVISIBLE);

        final LinearLayout error = view.findViewById(R.id.error);
        error.setVisibility(View.INVISIBLE);
        errorPanel.setReloadAction(new Runnable() {
            @Override
            public void run() {
                errorPanel.hide(new Runnable() {
                    @Override
                    public void run() {
                        presenter.updateChatsList(progressBar, adapter, toolbar, errorPanel, (LinearLayout) view.findViewById(R.id.empty), (LinearLayout) view.findViewById(R.id.error));
                        error.setVisibility(View.INVISIBLE);

                    }
                });
            }
        });

        ImageView chatCreateButton = view.findViewById(R.id.convCreateButton);
        chatCreateButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), CreateChatActivity.class);
                startActivity(intent);
            }
        });

        ImageView chatsSearchButton = view.findViewById(R.id.convSearchButton);
        chatsSearchButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), ChatsSearch.class);
                startActivity(intent);
            }
        });
    }

    @Override
    public void setStatusMessage(String statusMessage) {
        if (view != null) {
            TextView status = view.findViewById(R.id.statusv);
            if (status != null) {
                status.setText(statusMessage);
            }
        }

    }

}