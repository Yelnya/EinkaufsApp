package com.examples.pj.einkaufsapp.fragments;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Color;
import android.graphics.Paint;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
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
import com.examples.pj.einkaufsapp.dbentities.ProductItemDbHelper;
import com.examples.pj.einkaufsapp.util.ViewUtils;

import java.util.ArrayList;
import java.util.List;

//http://www.programmierenlernenhq.de/sqlite-datenbank-in-android-app-integrieren/
//SQLite wird standardmässig nach DATA/data/PACKAGE_NAME/databases/FILENAME kopiert.
//hier: /data/data/com.examples.pj.einkaufsapp/databases/shopping_list.db
//verwendete Klassen:
// -> ProductItem – Instanzen dieser Klasse können die Daten eines SQLite-Datensatzes aufnehmen. Sie repräsentieren die Datensätze im Code. Wir werden mit Objekten dieser Klasse den ListView füllen .
// -> ProductItemDbHelper – Sie ist eine Hilfsklasse mit deren Hilfe wir die SQLite-Datenbank erstellen lassen. Sie enthält weiterhin wichtige Konstanten, die wir für die Arbeit mit der Datenbank benötigen, wie den Tabellennamen, die Datenbankversion oder die Namen der Spalten.
// -> ProductItemDataSource – Diese Klasse ist unser Data Access Object und für das Verwalten der Daten verantwortlich. Es unterhält die Datenbankverbindung und ist für das Hinzufügen, Auslesen und Löschen von Datensätzen zuständig. Außerdem wandelt es Datensätze in Java-Objekte für uns um, so dass der Code der Benutzeroberfläche nicht direkt mit den Datensätzen arbeiten muss.

// Öffnen der Datenquelle und Zugriff auf die Datenbank in der WelcomeFragment

public class StatisticFragment extends BaseFragment {

    public static final String LOG_TAG = StatisticFragment.class.getSimpleName();

    private Context context;
    private final String toolbarTitle = "Statistiken";
    private ProductItemDataSource dataSource;
    private ListView mProductItemListView;
    private String selectedCategory;
    private ProductItemDbHelper databaseHelper;
    private boolean showEditAndDeleteIconInToolbar;
    //================================================================================
    // Fragment Instantiation
    //================================================================================

    public StatisticFragment() {
        super(LOG_TAG, true);   //influences Hamburger Icon HomeUp in Toolbar
    }

    public static BaseFragment createInstance() {
        final BaseFragment fragment = new StatisticFragment();
        final Bundle args = new Bundle();
        //args.putParcelable(ARG_ROUTE, route);
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
        getAttachedActivity().setToolbar(toolbar, true, toolbarTitle, showEditAndDeleteIconInToolbar); //Icon displayed, Titel of Toolbar
    }

    @Override
    protected void setToolbarEditAndDeleteIcon(boolean showEditAndDeleteIconInToolbar) {
        toolbarTv.setText(toolbarTitle);
    }

    @Override
    public boolean canGoBack() {
        return true;
    }

    //================================================================================
    // Fragment Lifecycle
    //================================================================================

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        context = this.getActivity();

        Log.d(LOG_TAG, "Das Datenquellen-Objekt wird angelegt.");
        databaseHelper = new ProductItemDbHelper(context);
        dataSource = new ProductItemDataSource(context);

        initializeProductItemsListView();
        activateAddButton();    // + Button
    }

    @Override
    public void onStart() {
        super.onStart();

//        toast("Ich bin im onStart");
    }

    @Override
    public void onStop() {
        super.onStop();
//        toast("Ich bin im onStop");
    }

    @Override
    public void onPause() {
        super.onPause();
        Log.d(LOG_TAG, "Die Datenquelle wird geschlossen.");
        dataSource.close(); //Verbindung zur Datenbank geschlossen
    }

    @Override
    public void onResume() {
        super.onResume();
        showEditAndDeleteIconInToolbar = false;
        setToolbarEditAndDeleteIcon(showEditAndDeleteIconInToolbar);
        Log.d(LOG_TAG, "Die Datenquelle wird geöffnet.");
        dataSource.open();  //Verbindung zur Datenbank geöffnet
        Log.d(LOG_TAG, "Folgende Einträge sind in der Datenbank vorhanden:");
        showAllListEntries();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
//        toast("Ich bin im onDestroy");
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
        final EditText editTextProduct = (EditText) getActivity().findViewById(R.id.editText_product);

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

    //Alert Dialog
    private AlertDialog createEditProductItemDialog(final ProductItem productItem) {

        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        LayoutInflater inflater = getActivity().getLayoutInflater();

        View dialogsView = inflater.inflate(R.layout.dialog_edit_product_item, null);

//        final EditText editTextNewQuantity = (EditText) dialogsView.findViewById(R.id.editText_new_category);
//        editTextNewQuantity.setText(String.valueOf(productItem.getQuantity()));

        final EditText editTextNewProduct = (EditText) dialogsView.findViewById(R.id.editText_new_product);
        editTextNewProduct.setText(productItem.getProduct());

        final Spinner spinner = (Spinner) dialogsView.findViewById(R.id.alert_spinner_category);
        // Create an ArrayAdapter using the string array and a default spinner layout
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(context,
                R.array.categories_array, android.R.layout.simple_spinner_item);
        // Specify the layout to use when the list of choices appears
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        // Apply the adapter to the spinner
        spinner.setAdapter(adapter);
        for (int i= 0; i < spinner.getAdapter().getCount(); i++) {
            if (spinner.getAdapter().getItem(i).toString().equals(productItem.getCategory())) {
                spinner.setSelection(i);
            }
        }
        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                selectedCategory = parent.getItemAtPosition(position).toString();
            }
            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                selectedCategory = getActivity().getResources().getStringArray(R.array.categories_array)[0];    //Default = "Sonstiges"
            }
        });

        builder.setView(dialogsView)
                .setTitle(R.string.dialog_title)
                .setPositiveButton(R.string.dialog_button_change_positive, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int id) {
                        String product = editTextNewProduct.getText().toString();
                        int bought = 0;
                        // An dieser Stelle schreiben wir die geänderten Daten in die SQLite Datenbank
                        ProductItem updatedProductItem = dataSource.updateProductItem(productItem.getId(), product, selectedCategory, bought, productItem.isDone(), productItem.isFavourite());

                        Log.d(LOG_TAG, "Alter Eintrag - ID: " + productItem.getId() + " Inhalt: " + productItem.toString());
                        Log.d(LOG_TAG, "Neuer Eintrag - ID: " + updatedProductItem.getId() + " Inhalt: " + updatedProductItem.toString());

                        showAllListEntries();
                        dialog.dismiss();
                    }
                })
                .setNegativeButton(R.string.dialog_button_negative, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        dialog.cancel();
                    }
                });

        ViewUtils.hideKeyboard((Activity) context);
        return builder.create();
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
                ProductItem updatedProductItem = dataSource.updateProductItem(productItem.getId(), productItem.getProduct(), productItem.getCategory(), productItem.getBought(), (!productItem.isDone()), productItem.isFavourite());
                Log.d(LOG_TAG, "Eintrag: " + updatedProductItem.toString());
                showAllListEntries();
            }
        });
    }
}