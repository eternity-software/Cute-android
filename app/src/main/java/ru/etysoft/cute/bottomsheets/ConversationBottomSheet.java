package ru.etysoft.cute.bottomsheets;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.annotation.Nullable;

import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;

import org.json.JSONException;
import org.json.JSONObject;

import ru.etysoft.cute.AlertDialog;
import ru.etysoft.cute.AppSettings;
import ru.etysoft.cute.R;
import ru.etysoft.cute.api.APIRunnable;
import ru.etysoft.cute.api.Methods;

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


    @Override
    public void onActivityCreated(Bundle arg0) {
        super.onActivityCreated(arg0);

        // Кастомная анимация
        getDialog().getWindow()
                .getAttributes().windowAnimations = R.style.DialogAnimation;
        BottomSheetDialog dialog = (BottomSheetDialog) getDialog();

        // Получение поведения
        final FrameLayout bottomSheet = dialog.findViewById(R.id.design_bottom_sheet);

        // Ловим COLLAPSED и не даём промежуточному положению существовать, а так же убираем слайд при невозможности отменить
        final BottomSheetBehavior bottomSheetBehavior = BottomSheetBehavior.from(bottomSheet);


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
                        String conv_desc = data.getString("description");

                        TextView nameview = view.findViewById(R.id.conv_name);
                        TextView descview = view.findViewById(R.id.conv_desc);
                        TextView acronymview = view.findViewById(R.id.conv_acronym);

                        nameview.setText(conv_name);
                        descview.setText(conv_desc);
                        acronymview.setText(conv_name.substring(0, 1).toUpperCase());

                    } catch (JSONException e) {
                        e.printStackTrace();
                        dismiss();
                    }
                } else {
                    dismiss();
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
        AlertDialog cdd = new AlertDialog(getActivity(), getResources().getString(R.string.leave_title), getString(R.string.leave_text), toRun, cancel);
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
