package tugce.nytimessearch.fragments;

import android.app.DatePickerDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.text.InputType;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Locale;

import cz.msebera.android.httpclient.util.TextUtils;
import tugce.nytimessearch.R;
import tugce.nytimessearch.activities.SearchActivity;
import tugce.nytimessearch.models.SearchFilterParamethers;

/**
 * Created by Tugce on 10/23/2016.
 */
public class ArticleFilterFragment extends DialogFragment implements View.OnClickListener {
    private EditText etBeginDate;
    private DatePickerDialog dpBeginDate;
    private SimpleDateFormat dateFormatter;
    private Spinner spSortOrder;
    private CheckBox cbArts;
    private CheckBox cbFashionStyle;
    private CheckBox cbSports;
    //  private ArrayList<String> categories;
    private Button btnSave;
    // private String beginDateValue;
    // private String sortValue;

    private static SearchFilterParamethers filters=new SearchFilterParamethers();

    public ArticleFilterFragment() {
        // Empty constructor is required for DialogFragment
        // Make sure not to add arguments to the constructor
        // Use `newInstance` instead as shown below
    }

    public interface SearchFilterDialogListener {
        void onFinishEditDialog(SearchFilterParamethers params);
    }

    @Override
    public void onClick(View view) {
        SearchFilterDialogListener listener = (SearchFilterDialogListener) getActivity();
        listener.onFinishEditDialog(filters);
    }

    // 1. Defines the listener interface with a method passing back data result.
    public interface  ArticleFilterDialogListener {
        void onFinishEditDialog(String inputText);
    }

    public static ArticleFilterFragment newInstance(String title) {
        ArticleFilterFragment frag = new ArticleFilterFragment();
        Bundle args = new Bundle();
        args.putString("search", title);
        frag.setArguments(args);
        return frag;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_article_filter, container);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        // Get field from view
        SharedPreferences preferences = getContext().getSharedPreferences(SearchActivity.PREFERENCES, Context.MODE_PRIVATE);
        final SharedPreferences.Editor editor = preferences.edit();

        dateFormatter = new SimpleDateFormat("MM/dd/yyyy", Locale.US);
        etBeginDate=(EditText)view.findViewById(R.id.etBeginDate);
        etBeginDate.setInputType(InputType.TYPE_NULL);
        etBeginDate.clearFocus();
        setDateTimeField();
        //beginDateValue=etBeginDate.getText().toString();

        spSortOrder=(Spinner)view.findViewById(R.id.spSortOrder);
        //sortValue = spSortOrder.getSelectedItem().toString();

        cbArts=(CheckBox)view.findViewById(R.id.cbArts);
        cbFashionStyle=(CheckBox)view.findViewById(R.id.cbFashionStyle);
        cbFashionStyle.setText("Fashion & Style");
        cbSports=(CheckBox)view.findViewById(R.id.cbSports);


        btnSave=(Button)view.findViewById(R.id.btnSave);
        btnSave.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view) {
                editor.putString(SearchActivity.BEGIN_DATE, etBeginDate.getText().toString());
                editor.putInt(SearchActivity.SORT_ORDER, spSortOrder.getSelectedItemPosition());
                editor.putBoolean(SearchActivity.ART, cbArts.isChecked());
                editor.putBoolean(SearchActivity.FASHION, cbFashionStyle.isChecked());
                editor.putBoolean(SearchActivity.SPORTS, cbSports.isChecked());
                editor.commit();
                dismiss();

                // SearchFilterDialogListener listener = (SearchFilterDialogListener) getActivity();
                // listener.onFinishEditDialog(filters);
            }
        });
        getDialog().getWindow().setSoftInputMode(
                WindowManager.LayoutParams.SOFT_INPUT_STATE_VISIBLE);
    }

    private void setDateTimeField() {

        etBeginDate.setOnTouchListener(new View.OnTouchListener(){
            @Override
            public boolean onTouch(View view, MotionEvent event) {
                if(view == etBeginDate) {
                    dpBeginDate.show();
                }
                return true;
            }
        });

        Calendar newCalendar = Calendar.getInstance();
        dpBeginDate = new DatePickerDialog(getContext(), new DatePickerDialog.OnDateSetListener() {

            public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                Calendar newDate = Calendar.getInstance();
                newDate.set(year, monthOfYear, dayOfMonth);
                etBeginDate.setText(dateFormatter.format(newDate.getTime()));
            }

        }, newCalendar.get(Calendar.YEAR), newCalendar.get(Calendar.MONTH), newCalendar.get(Calendar.DAY_OF_MONTH));

    }
}