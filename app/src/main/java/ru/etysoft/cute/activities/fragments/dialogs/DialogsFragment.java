package ru.etysoft.cute.activities.fragments.dialogs;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProviders;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import ru.etysoft.cute.R;
import ru.etysoft.cute.activities.ChatsSearch;
import ru.etysoft.cute.activities.CreateConv;
import ru.etysoft.cute.activities.dialogs.DialogAdapter;
import ru.etysoft.cute.activities.dialogs.DialogInfo;
import ru.etysoft.cute.data.CacheUtils;
import ru.etysoft.cute.data.CachedValues;
import ru.etysoft.cute.exceptions.NotCachedException;
import ru.etysoft.cute.tooltip.Tooltip;
import ru.etysoft.cute.tooltip.TooltipScript;
import ru.etysoft.cute.utils.Numbers;
import ru.etysoft.cuteframework.Methods;
import ru.etysoft.cuteframework.exceptions.ResponseException;
import ru.etysoft.cuteframework.methods.chat.Chat;
import ru.etysoft.cuteframework.methods.chat.ChatList.ChatListResponse;


public class DialogsFragment extends Fragment {

    private DialogsViewModel dialogsViewModel;
    private final HashMap<String, DialogInfo> dialogInfos = new HashMap<>();
    private CacheUtils cacheUtils;
    private DialogAdapter adapter;
    private View view;


    // Была ли последняя загрузка произведена из кэша
    private boolean isCached = false;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {

        dialogsViewModel =
                ViewModelProviders.of(this).get(DialogsViewModel.class);
        View root = inflater.inflate(R.layout.activity_dialogs, container, false);

        cacheUtils = CacheUtils.getInstance();
        view = root;
        final ListView listView = view.findViewById(R.id.listView);

        adapter = new DialogAdapter(getActivity(), new ArrayList<DialogInfo>());
        listView.setAdapter(adapter);

        // Инициализируем диалоги в первый раз
        updateDialogList();

        // Задаём обработчик нажатия на кнопку создания диалога
        ImageView convCreateButton = view.findViewById(R.id.convCreateButton);
        convCreateButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), CreateConv.class);
                startActivity(intent);
            }
        });

        ImageView convSearchButton = view.findViewById(R.id.convSearchButton);
        convSearchButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), ChatsSearch.class);
                startActivity(intent);
            }
        });

        TooltipScript tooltipScript = new TooltipScript();

        final Tooltip createChatTooltip = new Tooltip(getContext());
        createChatTooltip.setText("Here you can start a new chat!");


//        final Tooltip searchChatTooltip = new Tooltip(getContext());
//        searchChatTooltip.setText("Search millions of chats");
//
//        tooltipScript.addTooltip(createChatTooltip, convCreateButton);
//        tooltipScript.addTooltip(searchChatTooltip, convSearchButton);
//
//        tooltipScript.start();


        final Button error = view.findViewById(R.id.update_dialoglist);
        error.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final LinearLayout layout = view.findViewById(R.id.error);
                layout.setVisibility(View.INVISIBLE);
                updateDialogList();
            }
        });



        return root;
    }

    public void setStatusMessage(String statusMessage) {
        if (view != null) {
            TextView status = view.findViewById(R.id.statusv);
            if (status != null) {
                status.setText(statusMessage);
            }
        }

    }

    public void updateDialogList() {

        // Если нет интернета просим загрузить кэш


        // Инициализируем загрузку и сообщение об отсутствии сообщений
        final ProgressBar frontView = view.findViewById(R.id.loading);
        frontView.setVisibility(View.VISIBLE);
        final LinearLayout noMessages = view.findViewById(R.id.empty);
        noMessages.setVisibility(View.INVISIBLE);
        final LinearLayout error = view.findViewById(R.id.error);


        // Задаём обработчик запроса к API
        final ListView listView = view.findViewById(R.id.listView);
        Thread thread = new Thread(new Runnable() {
            @Override
            public void run() {
                try {
//                    (new ChatCreateRequest(CachedValues.getSessionKey(getActivity()), "asdasd", "sssss"
//                    , Chat.Types.CONVERSATION)).execute();
                    ChatListResponse chatListResponse = Methods.getChatList(CachedValues.getSessionKey(getActivity()));
                    List<Chat> chats = chatListResponse.getChats();
                    System.out.println(chats.size());
                    for (int i = 0; i < chats.size(); i++) {
                        final Chat chat = chats.get(i);
                        getActivity().runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                System.out.println(chat.getName());
                                adapter.add(new DialogInfo(chat.getName(), chat.getName(), chat.getName().substring(0, 1),
                                        String.valueOf(chat.getId()),
                                        Numbers.getTimeFromTimestamp("" + System.currentTimeMillis(), getContext()),
                                        false, 0, true, false, "0", "null"));
                            }
                        });


                    }

                    getActivity().runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            adapter.notifyDataSetChanged();

                        }
                    });

                } catch (NotCachedException e) {
                    e.printStackTrace();
                } catch (ResponseException e) {
                    e.printStackTrace();
                }
            }
        });
        thread.start();


        // Если в предыдущем обращении мы загрузили из кэша, то в этот раз попробуем отправить запрос на сервер


    }


}