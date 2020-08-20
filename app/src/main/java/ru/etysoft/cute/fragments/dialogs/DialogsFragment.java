package ru.etysoft.cute.fragments.dialogs;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

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
import ru.etysoft.cute.activities.dialogs.DialogAdapter;
import ru.etysoft.cute.activities.dialogs.DialogInfo;
import ru.etysoft.cute.api.Methods;
import ru.etysoft.cute.requests.APIRunnable;
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


        return root;
    }

    public void updateDialogList() {
        if (!Methods.hasInternet(getContext())) {
            isCached = false;
        }
        APIRunnable apiRunnable = new APIRunnable() {
            @Override
            public void run() {
                try {
                    final ListView listView = view.findViewById(R.id.listView);
                    dialogInfos.clear();
                    if (isSuccess) {
                        CacheResponse.saveResponseToCache(url, getResponse(), appSettings);
                        JSONObject jsonObject = new JSONObject(getResponse());
                        JSONArray data = jsonObject.getJSONArray("data");

                        for (int i = 0; i < data.length(); i++) {
                            JSONObject conv = data.getJSONObject(i);
                            final String name = conv.getString("name");
                            final String message = conv.getString("text");
                            dialogInfos.add(new DialogInfo(name, message));
                        }
                    } else {

                        dialogInfos.add(new DialogInfo("no", "empty"));

                    }
                    DialogAdapter adapter = new DialogAdapter(getActivity(), dialogInfos);
                    listView.setAdapter(adapter);
                } catch (JSONException e) {
                    e.printStackTrace();
                    CustomToast.show(getString(R.string.err_json), R.drawable.icon_error, getActivity());
                }

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