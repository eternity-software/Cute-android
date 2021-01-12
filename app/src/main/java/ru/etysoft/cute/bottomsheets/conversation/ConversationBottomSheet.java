package ru.etysoft.cute.bottomsheets.conversation;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.cardview.widget.CardView;

import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import ru.etysoft.cute.AlertDialog;
import ru.etysoft.cute.AppSettings;
import ru.etysoft.cute.R;
import ru.etysoft.cute.api.APIRunnable;
import ru.etysoft.cute.api.Methods;
import ru.etysoft.cute.utils.ImagesWorker;
import ru.etysoft.cute.utils.Numbers;

public class ConversationBottomSheet extends BottomSheetDialogFragment {
    private BottomSheetListener mListener;

    // Присваемывый контент
    private String cid;

    // Нажатия на кнопки
    private View.OnClickListener passiveButtonClick = null;
    private View.OnClickListener activeButtonClick = null;

    private View view;


    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Пустой фон
        setStyle(BottomSheetDialogFragment.STYLE_NORMAL, R.style.CustomBottomSheetDialogTheme);
    }


    @SuppressLint("ClickableViewAccessibility")
    @Override
    public void onActivityCreated(Bundle arg0) {
        super.onActivityCreated(arg0);

        getDialog().getWindow()
                .getAttributes().windowAnimations = R.style.DialogAnimation;
        BottomSheetDialog dialog = (BottomSheetDialog) getDialog();


        final FrameLayout bottomSheet = dialog.findViewById(com.google.android.material.R.id.design_bottom_sheet);

        final BottomSheetBehavior bottomSheetBehavior = BottomSheetBehavior.from(bottomSheet);
        bottomSheetBehavior.addBottomSheetCallback(new BottomSheetBehavior.BottomSheetCallback() {
            @Override
            public void onStateChanged(@NonNull View bottomSheet, int newState) {
                final CardView cardView = view.findViewById(R.id.appBar);
                if (newState == BottomSheetBehavior.STATE_EXPANDED) {


                    if (cardView.getRadius() != 0) {
                        Thread animation = new Thread(new Runnable() {
                            @Override
                            public void run() {
                                for (int radius = 20; radius >= 0; radius = radius - 1) {
                                    System.out.print(radius);
                                    final int finalRadius = radius;
                                    try {
                                        Thread.sleep(10);
                                        getActivity().runOnUiThread(new Runnable() {
                                            @Override
                                            public void run() {
                                                cardView.setRadius(Numbers.dpToPx(finalRadius, getContext()));
                                            }
                                        });

                                    } catch (InterruptedException e) {
                                        e.printStackTrace();
                                    }
                                }

                            }
                        });
                        animation.start();
                    }
                } else {
                    cardView.setRadius(Numbers.dpToPx(20, getContext()));
                }
            }

            @Override
            public void onSlide(@NonNull View bottomSheet, float slideOffset) {

            }
        });


    }

    private List<MemberInfo> memberInfos = new ArrayList<>();
    private int membersCount = 0;

    public void loadMembers(JSONArray members) throws JSONException {
        final ListView listView = view.findViewById(R.id.members);
        int creatorid = 0;
        for (int i = 0; i < members.length(); i++) {
            JSONObject member = members.getJSONObject(i);
            int id = member.getInt("id");
            String name = member.getString("nickname");
            String role = member.getString("role");

            MemberInfo memberInfo = new MemberInfo(id, name, role);

            if (role.equals("CREATOR")) {
                memberInfos.add(0, memberInfo);
            } else {
                memberInfos.add(memberInfo);
            }
            membersCount++;
        }
        TextView membersCountTextView = view.findViewById(R.id.membersCount);
        membersCountTextView.setText(membersCount + " " + getString(R.string.members));
        MembersAdapter membersAdapter = new MembersAdapter(getActivity(), memberInfos);
        listView.setAdapter(membersAdapter);
    }

    public void setCid(String conversationId) {
        cid = conversationId;
    }

    // Задаём контент
    private void setContent() {

        APIRunnable apiRunnable = new APIRunnable() {
            @Override
            public void run() {
                if (isSuccess()) {
                    try {
                        JSONObject jsonObject = new JSONObject(getResponse());
                        JSONObject data = jsonObject.getJSONObject("data");
                        String conv_name = data.getString("name");
                        int id = data.getInt("id");
                        String conv_desc = data.getString("description");
                        JSONArray members = data.getJSONArray("members");

                        TextView nameview = view.findViewById(R.id.conv_name);
                        TextView descview = view.findViewById(R.id.conv_desc);
                        TextView acronymview = view.findViewById(R.id.conv_acronym);
                        ImageView icon = view.findViewById(R.id.icon);
                        ImagesWorker.setGradient(icon, id);


                        nameview.setText(conv_name);
                        descview.setText(conv_desc);
                        loadMembers(members);
                        acronymview.setText(conv_name.substring(0, 1).toUpperCase());

                    } catch (JSONException e) {
                        e.printStackTrace();
                        dismiss();
                    }
                } else {
                    // dismiss();
                }
            }
        };
        AppSettings appSettings = new AppSettings(getContext());

        Methods.getConversationInfo(appSettings.getString("session"), cid, apiRunnable, getActivity());
    }


    public void exit() {

        Runnable toRun = new Runnable() {
            @Override
            public void run() {
                AppSettings appSettings = new AppSettings(getContext());

                APIRunnable apiRunnable = new APIRunnable() {
                    @Override
                    public void run() {
                        if (isSuccess()) {
                            dismiss();
                            getActivity().finish();
                        }
                    }
                };

                Methods.leaveConversation(appSettings.getString("session"), cid, apiRunnable, getActivity());
            }
        };
        Runnable cancel = new Runnable() {
            @Override
            public void run() {
                AppSettings appSettings = new AppSettings(getContext());

                APIRunnable apiRunnable = new APIRunnable() {
                    @Override
                    public void run() {
                    }
                };
            }
        };
        AlertDialog cdd = new AlertDialog(getActivity(), getResources().getString(R.string.logout_title), getString(R.string.leave_text), toRun, cancel);
        cdd.show();
    }

    public void delete() {

        Runnable toRun = new Runnable() {
            @Override
            public void run() {
                AppSettings appSettings = new AppSettings(getContext());

                APIRunnable apiRunnable = new APIRunnable() {
                    @Override
                    public void run() {
                        if (isSuccess()) {
                            dismiss();
                            getActivity().finish();
                        }
                    }
                };

                Methods.deleteConversationLocaly(appSettings.getString("session"), cid, apiRunnable, getActivity());
            }
        };
        Runnable cancel = new Runnable() {
            @Override
            public void run() {
                AppSettings appSettings = new AppSettings(getContext());

                APIRunnable apiRunnable = new APIRunnable() {
                    @Override
                    public void run() {
                    }
                };
            }
        };
        AlertDialog cdd = new AlertDialog(getActivity(), getResources().getString(R.string.clear_title), getString(R.string.clear_text), toRun, cancel);
        cdd.show();
    }

    // Отрисовка элементов
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.bottom_sheet_conversation, container, true);
        view = v;
        ImageButton leave = v.findViewById(R.id.conv_exit);
        leave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                exit();
            }
        });
        final ImageButton delete = v.findViewById(R.id.conv_delete);
        delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                delete();
            }
        });
        setContent();

        return v;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        try {
            mListener = (BottomSheetListener) context;
        } catch (ClassCastException e) {
            throw new ClassCastException(context.toString()
                    + " must implement BottomSheetListener");
        }
    }

    public interface BottomSheetListener {
    }


}
