package ru.etysoft.cute.fragments.dialogs;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ProgressBar;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProviders;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import ru.etysoft.cute.AppSettings;
import ru.etysoft.cute.R;
import ru.etysoft.cute.activities.CreateConv;
import ru.etysoft.cute.activities.dialogs.DialogAdapter;
import ru.etysoft.cute.activities.dialogs.DialogInfo;
import ru.etysoft.cute.api.APIRunnable;
import ru.etysoft.cute.api.Methods;
import ru.etysoft.cute.requests.CacheResponse;
import ru.etysoft.cute.utils.CustomToast;


public class DialogsFragment extends Fragment {

    private DialogsViewModel dialogsViewModel;
    private List<DialogInfo> dialogInfos = new ArrayList<>();
    private AppSettings appSettings;
    private View view;

    public static DialogsFragment newInstance() {
        return new DialogsFragment();
    }

    private boolean isCached = false;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {

        dialogsViewModel =
                ViewModelProviders.of(this).get(DialogsViewModel.class);
        View root = inflater.inflate(R.layout.activity_dialogs, container, false);

        appSettings = new AppSettings(getActivity());
        view = root;
        final ListView listView = view.findViewById(R.id.listView);
        DialogAdapter adapter = new DialogAdapter(getActivity(), dialogInfos);
        listView.setAdapter(adapter);

        updateDialogList();

        ImageView convCreateButton = view.findViewById(R.id.convCreateButton);
        convCreateButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), CreateConv.class);
                startActivity(intent);
            }
        });

        return root;
    }


    public void updateDialogList() {
        if (!Methods.hasInternet(getContext())) {
            isCached = false;
        }
        final ProgressBar frontView = view.findViewById(R.id.loading);
        frontView.setVisibility(View.VISIBLE);
        final LinearLayout noMessages = view.findViewById(R.id.empty);
        noMessages.setVisibility(View.INVISIBLE);
        APIRunnable apiRunnable = new APIRunnable() {
            @Override
            public void run() {
                try {
                    final ListView listView = view.findViewById(R.id.listView);

                    if (isSuccess()) {
                        dialogInfos.clear();
                        CacheResponse.saveResponseToCache(getUrl(), getResponse(), appSettings);
                        JSONObject jsonObject = new JSONObject(getResponse());
                        JSONArray data = jsonObject.getJSONArray("data");

                        for (int i = 0; i < data.length(); i++) {
                            JSONObject conv = data.getJSONObject(i);
                            final String name = conv.getString("name");
                            final String message = conv.getString("text");
                            final String cid = conv.getString("id");
                            String firstLetter = name.substring(0, 1);
                            dialogInfos.add(new DialogInfo(name, message, firstLetter, cid));

                        }
                    } else {
                        try {
                            if (getErrorCode().equals("timeout")) {
                                CustomToast.show("Timeout", R.drawable.icon_error, getActivity());
                            }
                            if (getErrorCode().equals("#CM003.1")) {
                                CacheResponse.saveResponseToCache(getUrl(), getResponse(), appSettings);
                            }
                            noMessages.setVisibility(View.VISIBLE);
                        } catch (Exception e) {
                        }

                    }
                    DialogAdapter adapter = new DialogAdapter(getActivity(), dialogInfos);
                    listView.setAdapter(adapter);
                } catch (JSONException e) {
                    e.printStackTrace();
                    CustomToast.show(getString(R.string.err_json), R.drawable.icon_error, getActivity());
                }
                frontView.setVisibility(View.INVISIBLE);
            }
        };
        if (isCached) {
            Methods.getConversations(appSettings.getString("session"), apiRunnable, getActivity());
        } else {
            Methods.getCacheConversations(appSettings.getString("session"), apiRunnable, getActivity());
            if (Methods.hasInternet(getActivity())) {
                Methods.getConversations(appSettings.getString("session"), apiRunnable, getActivity());
            }
            isCached = true;
        }

    }


}