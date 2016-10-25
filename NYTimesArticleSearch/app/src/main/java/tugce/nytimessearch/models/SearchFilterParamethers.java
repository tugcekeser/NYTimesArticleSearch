package tugce.nytimessearch.models;

import android.text.TextUtils;

import org.parceler.Parcel;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import static java.util.Arrays.asList;

/**
 * Created by Tugce on 10/24/2016.
 */
@Parcel
public class SearchFilterParamethers {
    String beginDate;
    String sortOrder;
    ArrayList<String> categories;


    public SearchFilterParamethers() {
    }

    public void setBeginDate(String beginDate) {
        this.beginDate = beginDate;
    }

    public void setSortOrder(String sortOrder) {
        this.sortOrder = sortOrder.toLowerCase();
    }

    public void setCategories(ArrayList<String> categories) {
        this.categories = categories;
    }

    public String getBeginDate() {
        return beginDate;
    }

    public String getSortOrder() {
        return sortOrder;
    }

    public ArrayList<String> getCategories() {
        return categories;
    }

    SimpleDateFormat DATE_FORMAT = new SimpleDateFormat("yyyyMMdd");

}