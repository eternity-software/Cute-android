package ru.etysoft.cute.fragments.dialogs;

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

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;

import ru.etysoft.cute.AppSettings;
import ru.etysoft.cute.R;
import ru.etysoft.cute.activities.ChatsSearch;
import ru.etysoft.cute.activities.CreateConv;
import ru.etysoft.cute.activities.dialogs.DialogAdapter;
import ru.etysoft.cute.activities.dialogs.DialogInfo;
import ru.etysoft.cute.api.APIRunnable;
import ru.etysoft.cute.api.Methods;
import ru.etysoft.cute.api.response.ResponseHandler;
import ru.etysoft.cute.requests.CacheResponse;
import ru.etysoft.cute.utils.CustomToast;
import ru.etysoft.cute.utils.Numbers;
import ru.etysoft.cute.utils.Tooltip;
import ru.etysoft.cute.utils.TooltipScript;


public class DialogsFragment extends Fragment {

    private DialogsViewModel dialogsViewModel;
    private final HashMap<String, DialogInfo> dialogInfos = new HashMap<>();
    private AppSettings appSettings;
    private DialogAdapter adapter;
    private View view;


    // Была ли последняя загрузка произведена из кэша
    private boolean isCached = false;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {

        dialogsViewModel =
                ViewModelProviders.of(this).get(DialogsViewModel.class);
        View root = inflater.inflate(R.layout.activity_dialogs, container, false);

        appSettings = new AppSettings(getActivity());
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


        final Tooltip searchChatTooltip = new Tooltip(getContext());
        searchChatTooltip.setText("Search millions of chats");

        tooltipScript.addTooltip(createChatTooltip, convCreateButton);
        tooltipScript.addTooltip(searchChatTooltip, convSearchButton);

        tooltipScript.start();


        final Button error = view.findViewById(R.id.update_dialoglist);
        error.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final LinearLayout layout = view.findViewById(R.id.error);
                layout.setVisibility(View.INVISIBLE);
                updateDialogList();
            }
        });

        if (!Methods.hasInternet(getContext())) {
            setStatusMessage(getResources().getString(R.string.no_internet));
        }
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


    // Обновляем список диалогов и бесед
    public void updateDialogList() {

        // Если нет интернета просим загрузить кэш
        if (!Methods.hasInternet(getContext())) {
            isCached = false;
        }


        // Инициализируем загрузку и сообщение об отсутствии сообщений
        final ProgressBar frontView = view.findViewById(R.id.loading);
        frontView.setVisibility(View.VISIBLE);
        final LinearLayout noMessages = view.findViewById(R.id.empty);
        noMessages.setVisibility(View.INVISIBLE);
        final LinearLayout error = view.findViewById(R.id.error);

        // Задаём обработчик запроса к API
        APIRunnable apiRunnable = new APIRunnable() {
            @Override
            public void run() {
                try {


                    ResponseHandler responseHandler = new ResponseHandler(getResponse());
                    if (responseHandler.isSuccess()) {
                        dialogInfos.clear();
                        adapter.clear();


                        // Сохраняем успешный запрос в кэш
                        CacheResponse.saveResponseToCache(getUrl(), getResponse(), appSettings);

                        // Обрабатываем ответ
                        JSONObject jsonObject = new JSONObject(getResponse());
                        JSONArray data = jsonObject.getJSONArray("data");

                        boolean hasMessages = false;
                        for (int i = 0; i < data.length(); i++) {
                            JSONObject conv = data.getJSONObject(i);
                            final String name = conv.getString("name");
                            final String message = conv.getString("text");
                            final String cid = conv.getString("id");
                            final String time = conv.getString("time");
                            final String countMembers = conv.getString("countMembers");
                            final int readed = conv.getInt("readed");
                            final int countReaded = conv.getInt("countReaded");
                            final int online = conv.getInt("online");
                            final int personal = conv.getInt("personal");
                            final String cover = conv.getString("cover");

                            boolean isonline;
                            boolean isDialog;

                            if (personal > 0) {
                                isDialog = true;
                            } else {
                                isDialog = false;
                            }

                            isonline = Numbers.getBooleanFromInt(online);

                            String firstLetter = name.substring(0, 1);

                            boolean readst;
                            readst = Numbers.getBooleanFromInt(readed);
                            hasMessages = true;

                            // Добавляем новый диалог в список
                            adapter.add(new DialogInfo(name, message, firstLetter, cid, Numbers.getTimeFromTimestamp(time, getContext()), readst, countReaded, isonline, isDialog, countMembers, cover));
                        }
                        if (!hasMessages) {
                            noMessages.setVisibility(View.VISIBLE);
                        } else {
                            CacheResponse.saveResponseToCache(getUrl(), getResponse(), appSettings);
                        }
                    } else {
                        if (responseHandler.getErrorHandler().getErrorCode() != null) {
                            if (responseHandler.getErrorHandler().getErrorCode().equals("timeout")) {
                                CustomToast.show("Timeout", R.drawable.icon_error, getActivity());
                            } else {
                                error.setVisibility(View.VISIBLE);
                            }
                        } else {
                            error.setVisibility(View.VISIBLE);
                        }
                    }
                } catch (Exception e) {
                    error.setVisibility(View.VISIBLE);
                    e.printStackTrace();
                }


                frontView.setVisibility(View.INVISIBLE);
            }
        };

        // Если в предыдущем обращении мы загрузили из кэша, то в этот раз попробуем отправить запрос на сервер
        if (isCached) {
            Methods.getConversations(appSettings.getString("session"), apiRunnable, getActivity());
        } else {
            Methods.getCacheConversations(appSettings.getString("session"), apiRunnable, getActivity());
            if (Methods.hasInternet(getActivity())) {
                // Если есть интернет обязательно пытаемся обновить данные, даже если уже загрузили из кэша
                Methods.getConversations(appSettings.getString("session"), apiRunnable, getActivity());
            }
            isCached = true;
        }

    }


}