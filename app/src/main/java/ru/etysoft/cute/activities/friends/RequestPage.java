package ru.etysoft.cute.activities.friends;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import java.util.ArrayList;
import java.util.List;

import ru.etysoft.cute.R;
import ru.etysoft.cute.data.CachedValues;
import ru.etysoft.cuteframework.methods.friend.Friend;
import ru.etysoft.cuteframework.methods.friend.GetRequests.FriendRequestsResponse;
import ru.etysoft.cuteframework.methods.friend.GetRequests.GetIncomingFriendRequests;
import ru.etysoft.cuteframework.methods.friend.GetRequests.GetOutgoingFriendRequests;

public class RequestPage extends Fragment {
    public static final String ARG_PAGE = "ARG_PAGE";

    private List<Friend> requests;
    private FriendRequestAdapter adapter;
    private TextView countRequestView;
    private Context context;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.list_fragment, container, false);
        ListView listView = view.findViewById(R.id.listView);

        requests = new ArrayList<>();
        adapter = new FriendRequestAdapter(getActivity(), requests);
        listView.setAdapter(adapter);

        return view;
    }

    public static RequestPage newInstance(int page, Context context, TextView countRequest) {
        Bundle args = new Bundle();
        args.putInt(ARG_PAGE, page);
        RequestPage fragment = new RequestPage();
        fragment.setArguments(args);
        fragment.context = context;
        fragment.setCountRequestView(countRequest);
        return fragment;
    }

    public void setCountRequestView(TextView countRequest) {
        this.countRequestView = countRequest;
    }

    public void loadFriendRequests(final boolean isIncoming) {
        Thread processServerRequest = new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    final FriendRequestsResponse friendRequestsResponse;
                    if (isIncoming) {
                        friendRequestsResponse = (new GetIncomingFriendRequests(CachedValues.getSessionKey(context))).execute();
                    } else {
                        friendRequestsResponse = (new GetOutgoingFriendRequests(CachedValues.getSessionKey(context))).execute();
                    }

                    if (friendRequestsResponse.isSuccess()) {

                        getActivity().runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                applyData(friendRequestsResponse, isIncoming);
                            }
                        });

                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
        processServerRequest.start();
    }

    public void applyData(FriendRequestsResponse friendRequestsResponse, final boolean isIncoming) {
        try {
            requests.clear();
            adapter.setIncoming(isIncoming);
            requests.addAll(friendRequestsResponse.getRequests());
            adapter.notifyDataSetChanged();
            countRequestView.setText(String.valueOf(friendRequestsResponse.getRequests().size()));
        } catch (Exception e) {
            loadFriendRequests(isIncoming);
            e.printStackTrace();
        }
    }


}
