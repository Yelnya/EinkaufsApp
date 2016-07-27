package com.examples.pj.einkaufsapp.fragments;

import android.app.Activity;
import android.content.Context;
import android.graphics.Color;
import android.graphics.Paint;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;

import com.examples.pj.einkaufsapp.R;
import com.examples.pj.einkaufsapp.dbentities.ProductItem;
import com.examples.pj.einkaufsapp.dbentities.ProductItemDataSource;
import com.examples.pj.einkaufsapp.util.ViewUtils;

import java.util.ArrayList;
import java.util.List;

/**
 * Overview for all done shopping events with statistic function
 */
public class StatisticFragment extends BaseFragment {

    public static final String LOG_TAG = StatisticFragment.class.getSimpleName();

    private Context context;
    private static final String TOOLBAR_TITLE = "Statistiken";
    private ProductItemDataSource dataSource;
    private ListView mProductItemListView;
    private String selectedCategory;
    private boolean showEditAndDeleteIconInToolbar;
    private boolean showShoppingCartIconInToolbar;
    //================================================================================
    // Fragment Instantiation
    //================================================================================

    /**
     * Constructor
     */
    public StatisticFragment() {
        super(LOG_TAG, true);   //influences Hamburger Icon HomeUp in Toolbar
    }

    /**
     * createInstance contains arguments in bundle
     *
     * @return fragment
     */
    public static BaseFragment createInstance() {
        final BaseFragment fragment = new StatisticFragment();
        final Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    //================================================================================
    // Base Methods
    //================================================================================

    @Override
    protected int getLayoutId() {
        return R.layout.fragment_statistic;
    }

    @Override
    protected void setToolbar() {
        getAttachedActivity().setToolbar(toolbar, true, TOOLBAR_TITLE, showEditAndDeleteIconInToolbar, showShoppingCartIconInToolbar); //Icon displayed, Titel of Toolbar
    }

    @Override
    protected void setToolbarEditAndDeleteIcon(boolean showEditAndDeleteIconInToolbar) {
        toolbarTv.setText(TOOLBAR_TITLE);
    }

    @Override
    public boolean canGoBack() {
        return true;
    }

    @Override
    protected void onCleanUp() {

    }

    //================================================================================
    // Fragment Lifecycle
    //================================================================================

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        context = this.getActivity();

        Log.d(LOG_TAG, "Das Datenquellen-Objekt wird angelegt.");
        dataSource = new ProductItemDataSource(context);

        initializeProductItemsListView();
        activateAddButton();    // + Button
    }

    @Override
    public void onPause() {
        super.onPause();
        Log.d(LOG_TAG, "Die Datenquelle wird geschlossen.");
        dataSource.close(); //close db connection
    }

    @Override
    public void onResume() {
        super.onResume();
        showShoppingCartIconInToolbar = false;
        showEditAndDeleteIconInToolbar = false;
        setToolbarEditAndDeleteIcon(showEditAndDeleteIconInToolbar);
        Log.d(LOG_TAG, "Die Datenquelle wird geöffnet.");
        dataSource.open();  //Verbindung zur Datenbank geöffnet
        Log.d(LOG_TAG, "Folgende Einträge sind in der Datenbank vorhanden:");
        showAllListEntries();
    }

    //holen einer Liste mit allen Einträgen aus der DB
    private void showAllListEntries() {
        List<ProductItem> productItemList = dataSource.getAllProductItems();

        ArrayAdapter<ProductItem> adapter = (ArrayAdapter<ProductItem>) mProductItemListView.getAdapter();

        adapter.clear();
        adapter.addAll(productItemList);
        adapter.notifyDataSetChanged();
    }

    //Beim Drücken des + Buttons
    private void activateAddButton() {
        Button buttonAddProduct = (Button) getActivity().findViewById(R.id.button_add_product);
        final EditText editTextProduct = (EditText) getActivity().findViewById(R.id.autocompletetv_edittext_product);

        final Spinner spinner = (Spinner) getActivity().findViewById(R.id.spinner_category);
        // Create an ArrayAdapter using the string array and a default spinner layout
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(context,
                R.array.categories_array, R.layout.spinner_item);
        // Specify the layout to use when the list of choices appears
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        // Apply the adapter to the spinner
        spinner.setAdapter(adapter);
        spinner.setSelection(0);
        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                selectedCategory = parent.getItemAtPosition(position).toString();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                //do nothing
            }
        });

        buttonAddProduct.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String product = editTextProduct.getText().toString();
                if (TextUtils.isEmpty(product)) {
                    editTextProduct.setError(getString(R.string.editText_errorMessage));
                    return;
                }
                editTextProduct.setText("");

                dataSource.createProductItem(product, selectedCategory);
                ViewUtils.hideKeyboard((Activity) context);

                showAllListEntries();   //Anzeigen aller Datenbank Einträge in der ListView
            }
        });
    }


    //Initialisierung der ListView
    private void initializeProductItemsListView() {
        List<ProductItem> emptyListForInitialization = new ArrayList<>();

        mProductItemListView = (ListView) getActivity().findViewById(R.id.listview_product_items);

        // Erstellen des ArrayAdapters für der ListView
        ArrayAdapter<ProductItem> productItemArrayAdapter = new ArrayAdapter<ProductItem>(
                getActivity(),
                android.R.layout.simple_list_item_multiple_choice,
                emptyListForInitialization) {

            // Wird immer dann aufgerufen, wenn der übergeordnete ListView die Zeile neu zeichnen muss
            @Override
            public View getView(int position, View convertView, ViewGroup parent) {

                View view = super.getView(position, convertView, parent);
                TextView textView = (TextView) view;
                ProductItem productItem = (ProductItem) mProductItemListView.getItemAtPosition(position);

                textView.setText(productItem.toNiceString());

                // Hier prüfen, ob Eintrag abgehakt ist. Falls ja, Text durchstreichen
                if (productItem.isDone()) {
                    textView.setPaintFlags(textView.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);
                    textView.setTextColor(Color.rgb(175, 175, 175));
                } else {
                    textView.setPaintFlags(textView.getPaintFlags() & (~Paint.STRIKE_THRU_TEXT_FLAG));
                    textView.setTextColor(Color.DKGRAY);
                }
                return view;
            }
        };

        mProductItemListView.setAdapter(productItemArrayAdapter);

        mProductItemListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long id) {
                ProductItem productItem = (ProductItem) adapterView.getItemAtPosition(position);

                // Hier den checked-Wert des ProductItem-Objekts umkehren, bspw. von true auf false
                // Dann ListView neu zeichnen mit showAllListEntries()
                ProductItem updatedProductItem = dataSource.updateProductItem(productItem.getId(), productItem.getProduct(), productItem.getCategory(), productItem.getBought(), !productItem.isDone(), productItem.isFavourite());
                Log.d(LOG_TAG, "Eintrag: " + updatedProductItem.toString());
                showAllListEntries();
            }
        });
    }
}