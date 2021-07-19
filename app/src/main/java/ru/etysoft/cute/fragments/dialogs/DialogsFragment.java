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

import java.util.ArrayList;
import java.util.HashMap;

import ru.etysoft.cute.AppSettings;
import ru.etysoft.cute.R;
import ru.etysoft.cute.activities.ChatsSearch;
import ru.etysoft.cute.activities.CreateConv;
import ru.etysoft.cute.activities.dialogs.DialogAdapter;
import ru.etysoft.cute.activities.dialogs.DialogInfo;
import ru.etysoft.cute.tooltip.Tooltip;
import ru.etysoft.cute.tooltip.TooltipScript;


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


        // Инициализируем загрузку и сообщение об отсутствии сообщений
        final ProgressBar frontView = view.findViewById(R.id.loading);
        frontView.setVisibility(View.VISIBLE);
        final LinearLayout noMessages = view.findViewById(R.id.empty);
        noMessages.setVisibility(View.INVISIBLE);
        final LinearLayout error = view.findViewById(R.id.error);

        // Задаём обработчик запроса к API


        // Если в предыдущем обращении мы загрузили из кэша, то в этот раз попробуем отправить запрос на сервер


    }


}