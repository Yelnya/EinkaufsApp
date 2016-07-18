package com.examples.pj.einkaufsapp.fragments;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;

import com.examples.pj.einkaufsapp.R;
import com.examples.pj.einkaufsapp.adapters.CurrentListAdapter;
import com.examples.pj.einkaufsapp.dbentities.ShoppingMemo;
import com.examples.pj.einkaufsapp.dbentities.ShoppingMemoDataSource;
import com.examples.pj.einkaufsapp.interfaces.ChangeToolbarInterface;
import com.examples.pj.einkaufsapp.util.SharedPreferencesManager;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import butterknife.Bind;
import butterknife.OnClick;

public class CurrentListFragment extends BaseFragment implements ChangeToolbarInterface {
    public static final String LOG_TAG = CurrentListFragment.class.getSimpleName();

    @Bind(R.id.spinner_category)
    Spinner spinner;
    @Bind(R.id.editText_product)
    EditText editTextProduct;
    @Bind(R.id.currentlist_recycler_view)
    RecyclerView currentListRv;

    private Context context;
    private ShoppingMemoDataSource dataSource;
    private SharedPreferencesManager sharedPreferencesManager;
    private List<ShoppingMemo> currentList;         //items on current shopping list stored in SP
    private String selectedCategory;
    private List<ShoppingMemo> shoppingMemoList;    //all items ever stored in DB
    boolean existingProductFound;
    boolean existingProductInCurrentListFound;
    private CurrentListAdapter currentListAdapter;
    private String toolbarTitle = "";
    private final String toolbarTitleFragment = "Aktuelle Einkaufsliste";
    private final String toolbarTitleEdit = "Löschen/Ändern";
    private boolean showEditAndDeleteIconInToolbar;

    //================================================================================
    // Fragment Instantiation
    //================================================================================

    public CurrentListFragment() {
        super(LOG_TAG, true);   //influences Hamburger Icon HomeUp in Toolbar
    }

    public static BaseFragment createInstance() {
        final BaseFragment fragment = new CurrentListFragment();
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
        return R.layout.fragment_current_list;
    }

    @Override
    protected void setToolbar() {
        getAttachedActivity().setToolbar(toolbar, true, toolbarTitle, showEditAndDeleteIconInToolbar); //Icon displayed, Titel of Toolbar
    }

    @Override
    protected void setToolbarEditAndDeleteIcon(boolean showEditAndDeleteIconInToolbar) {
        toolbarEditIv.setVisibility(showEditAndDeleteIconInToolbar ? View.VISIBLE : View.INVISIBLE);
        toolbarDeleteIv.setVisibility(showEditAndDeleteIconInToolbar ? View.VISIBLE : View.INVISIBLE);
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

        context = getActivity();

        //Shared Preferences
        SharedPreferences SPRfile = getAttachedActivity().getSharedPreferences("SLSPfile", 0);
        SharedPreferences.Editor editor = SPRfile.edit();
        sharedPreferencesManager = new SharedPreferencesManager(SPRfile, editor);

        //Database
        dataSource = new ShoppingMemoDataSource(context);
        dataSource.open(); //open connection to db

        shoppingMemoList = new ArrayList<>();
        currentList = sharedPreferencesManager.loadCurrentShoppingListFromLocalStore();
        currentList = sharedPreferencesManager.loadCurrentShoppingListFromLocalStore();
        initializeShoppingMemosListView();
    }

    @Override
    public void onStart() {
        super.onStart();
    }

    @Override
    public void onStop() {
        super.onStop();
    }

    @Override
    public void onPause() {
        super.onPause();
        dataSource.close(); //Verbindung zur Datenbank geschlossen
    }

    @Override
    public void onResume() {
        super.onResume();
        toolbarTitle = toolbarTitleFragment;
        showEditAndDeleteIconInToolbar = false;
        setToolbarEditAndDeleteIcon(showEditAndDeleteIconInToolbar);

        dataSource.open(); //Verbindung zur Datenbank öffnen

        currentList = sharedPreferencesManager.loadCurrentShoppingListFromLocalStore(); //get local List
        shoppingMemoList.clear();
        shoppingMemoList = dataSource.getAllShoppingMemos();    //get General List

        // Spinner with Array Adapter writes Selection to "selectedCategory"
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(context,
                R.array.categories_array, R.layout.spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
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
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    //---------------------------------------------------------------
    // Butter Knife Methods
    //---------------------------------------------------------------
    @OnClick(R.id.button_add_product)
    public void onPlusButtonClick() {
        if (currentList == null) {
            currentList = new ArrayList<>();
        }
        ShoppingMemo currentProductObject;
        //first letter upper case
        String currentProductName = editTextProduct.getText().toString().substring(0, 1).toUpperCase() + editTextProduct.getText().toString().substring(1);
        long currentProductID = 0;
        if (TextUtils.isEmpty(currentProductName)) {
            editTextProduct.setError(getString(R.string.editText_errorMessage));
            return;
        }
        editTextProduct.setText("");

        Log.d(LOG_TAG, "Contents of SQLITE DB: " + shoppingMemoList.toString());
        existingProductFound = false;
        existingProductInCurrentListFound = false;
        //check if entered productName (lower case) already is stored in SQLiteDB
        for (ShoppingMemo product : shoppingMemoList) {
            if (product.getProduct().toLowerCase().equals(currentProductName.toLowerCase())) {
                currentProductObject = product;
                //if product is NOT in list already: add to currentList
                for (ShoppingMemo currentProduct : currentList) {
                    if (currentProduct.getProduct().equals(currentProductObject.getProduct())) {
                        existingProductInCurrentListFound = true;
                        break;
                    }
                }
                if (!existingProductInCurrentListFound) {
                    currentList.add(currentProductObject);
                    sortListCategoryAndAlphabetical();
                    sharedPreferencesManager.saveCurrentShoppingListToLocalStore(currentList);
                } else {
                    toast("Produkt befindet sich schon in der Liste");
                }

                Log.d(LOG_TAG, "Added existing product to list: " + currentProductName);
                existingProductFound = true;
                break;
            }
        }
        if (!existingProductFound) {
            currentProductID = dataSource.getHighestID() + 1; //if no, get highest ID in DB and +1
            currentProductObject = new ShoppingMemo(currentProductID, currentProductName, selectedCategory, 0, false, false); //create new object
            currentList.add(currentProductObject);
            sortListCategoryAndAlphabetical();
            sharedPreferencesManager.saveCurrentShoppingListToLocalStore(currentList);

            dataSource.createShoppingMemo(currentProductName, selectedCategory);    //add product to general list
            Log.d(LOG_TAG, "Added new product to list: " + currentProductName);
        }
        //store currentList to SP
        sharedPreferencesManager.saveCurrentShoppingListToLocalStore(currentList);
        //LOG output
        Log.d(LOG_TAG, "-------------------LOCAL LIST ENTRIES -----------------------");
        for (ShoppingMemo product : currentList) {
            Log.d(LOG_TAG, "LocalProduct: " + product.toString());
        }
        //Hide Softkeyboard and refresh list
        hideKeyboard();
        currentListAdapter.notifyDataSetChanged();   //Anzeigen aller Datenbank Einträge in der ListView
    }

    @OnClick(R.id.toolbarDeleteIv)
    public void onToolbarDeleteClick() {
        System.out.println("TrashBin clicked");
        ShoppingMemo clickedItem = currentListAdapter.getItemClicked(); //get item clicked from Adapter
        ShoppingMemo matchingItemInLocalList = null;
        for (ShoppingMemo item : currentList) { //find item and delete it from local list
            if (item.getId() == (clickedItem.getId())) {
                matchingItemInLocalList = item;
                break;
            }
        }
        createReallyDeleteDialog(matchingItemInLocalList);
    }

    @OnClick(R.id.toolbarEditIv)
    public void onToolbarEditClick() {
        System.out.println("EditIcon clicked");
        ShoppingMemo clickedItem = currentListAdapter.getItemClicked(); //get item clicked from Adapter
        ShoppingMemo matchingItemInLocalList = null;
        for (ShoppingMemo item : currentList) { //find item and delete it from local list
            if (item.getId() == (clickedItem.getId())) {
                matchingItemInLocalList = item;
                break;
            }
        }
        createEditShoppingMemoDialog(matchingItemInLocalList);
    }

    //---------------------------------------------------------------
    // Other Methods
    //---------------------------------------------------------------

    private void toolbarBackToNormal() {
        showEditAndDeleteIconInToolbar = false;
        toolbarTitle = toolbarTitleFragment;
        setToolbarEditAndDeleteIcon(showEditAndDeleteIconInToolbar);
        currentListAdapter.setEditDeleteToolbarActive(false);
    }

    private void initializeShoppingMemosListView() {
        currentListAdapter = new CurrentListAdapter(currentList, context, dataSource, sharedPreferencesManager, this, showEditAndDeleteIconInToolbar);
        final LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getActivity());
        linearLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        currentListRv.setLayoutManager(linearLayoutManager);
        currentListRv.setAdapter(currentListAdapter);
        currentListRv.setHasFixedSize(true);
        currentListRv.setVisibility(View.VISIBLE);
    }

    //hide keyboard
    private void hideKeyboard() {
        //Hide Softkeyboard
        InputMethodManager inputMethodManager;
        inputMethodManager = (InputMethodManager) getActivity().getSystemService(getActivity().INPUT_METHOD_SERVICE);
        if (getActivity().getCurrentFocus() != null) {
            inputMethodManager.hideSoftInputFromWindow(getActivity().getCurrentFocus().getWindowToken(), 0);
        }
    }

    public void sortListCategoryAndAlphabetical() {
        Collections.sort(currentList, new CurrentListAlphabeticalComparator());
        Collections.sort(currentList, new CurrentListCategoryComparator());

        Log.d(LOG_TAG, "----------------- SORTED LIST ------------------");
        for (ShoppingMemo product : currentList) {
            Log.d(LOG_TAG, "Sorted: " + product.toNiceString());
        }
    }

    //---------------------------------------------------------------
    // ChangeToolbarInterface Methods
    //---------------------------------------------------------------

    @Override
    public void showEditAndDeleteIcon(boolean show) {
        // change Toolbar
        Log.d(LOG_TAG, "Show Edit and Delete Icons in Toolbar: " + show);
        toolbarTitle = show ? toolbarTitleEdit : toolbarTitleFragment;
        showEditAndDeleteIconInToolbar = show;
        setToolbarEditAndDeleteIcon(show);
        setToolbar();
    }

    //---------------------------------------------------------------
    // COMPARATORS FOR LIST SORTING
    //---------------------------------------------------------------

    public class CurrentListAlphabeticalComparator implements Comparator<ShoppingMemo> {
        public int compare(ShoppingMemo left, ShoppingMemo right) {
            return left.getProduct().compareTo(right.getProduct());
        }
    }

    public class CurrentListCategoryComparator implements Comparator<ShoppingMemo> {
        public int compare(ShoppingMemo left, ShoppingMemo right) {
            return left.getCategory().compareTo(right.getCategory());
        }
    }

    //---------------------------------------------------------------
    // ALERT DIALOGS
    //---------------------------------------------------------------

    public void createReallyDeleteDialog(final ShoppingMemo itemToDelete) {
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(context);
        // set title and message
        alertDialogBuilder.setTitle(itemToDelete.getProduct())
                .setMessage(context.getResources().getText(R.string.dialog_message_delete));
        //set yes no fields
        alertDialogBuilder
                .setCancelable(false)
                .setPositiveButton(R.string.dialog_button_delete_positive, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        currentList.remove(itemToDelete);
                        sharedPreferencesManager.saveCurrentShoppingListToLocalStore(currentList); //save changed list to SP
                        currentListAdapter.notifyDataSetChanged();  //refresh Adapter View

                        toolbarBackToNormal();
                        dialog.dismiss();
                    }
                })
                .setNegativeButton(R.string.dialog_button_negative, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        toolbarBackToNormal();
                        dialog.dismiss();
                    }
                });
        AlertDialog alertDialog = alertDialogBuilder.create();
        alertDialog.show();
        hideKeyboard();
    }

    private void createEditShoppingMemoDialog(final ShoppingMemo itemToChange) {
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(context);
        // set title and message
        alertDialogBuilder.setTitle("Ändern");

        //layout for alert dialog
        LayoutInflater inflater = getActivity().getLayoutInflater();
        View dialogsView = inflater.inflate(R.layout.dialog_edit_shopping_memo, null);

        final EditText editTextNewProduct = (EditText) dialogsView.findViewById(R.id.editText_new_product);
        editTextNewProduct.setText(itemToChange.getProduct());

        //spinner
        final Spinner spinner = (Spinner) dialogsView.findViewById(R.id.alert_spinner_category);
        // Create an ArrayAdapter using the string array and a default spinner layout
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(context,
                R.array.categories_array, android.R.layout.simple_spinner_item);
        // Specify the layout to use when the list of choices appears
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        // Apply the adapter to the spinner
        spinner.setAdapter(adapter);
        for (int i = 0; i < spinner.getAdapter().getCount(); i++) {
            if (spinner.getAdapter().getItem(i).toString().equals(itemToChange.getCategory())) {
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
                selectedCategory = context.getResources().getStringArray(R.array.categories_array)[9];    //Default = "Sonstiges"
            }
        });

        //set yes no fields
        alertDialogBuilder.setView(dialogsView)
                .setCancelable(false)
                .setPositiveButton("Fertig", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        String product = editTextNewProduct.getText().toString();
                        int bought = 0;
                        // update SQLite Entry
                        ShoppingMemo updatedShoppingMemo = dataSource.updateShoppingMemo(itemToChange.getId(), product, selectedCategory, bought, itemToChange.isDone(), itemToChange.isFavourite());
                        // update local list
                        currentList.remove(itemToChange);
                        currentList.add(updatedShoppingMemo);
                        sortListCategoryAndAlphabetical();
                        sharedPreferencesManager.saveCurrentShoppingListToLocalStore(currentList); //store to SP
                        Log.d(LOG_TAG, "Alter Eintrag - ID: " + itemToChange.getId() + " Inhalt: " + itemToChange.toString());
                        Log.d(LOG_TAG, "Neuer Eintrag - ID: " + updatedShoppingMemo.getId() + " Inhalt: " + updatedShoppingMemo.toString());

                        sharedPreferencesManager.saveCurrentShoppingListToLocalStore(currentList); //save changed list to SP
                        currentListAdapter.notifyDataSetChanged();  //refresh Adapter View

                        toolbarBackToNormal();
                        dialog.dismiss();
                    }
                })
                .setNegativeButton(R.string.dialog_button_negative, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        toolbarBackToNormal();
                        dialog.dismiss();
                    }
                });
        AlertDialog alertDialog = alertDialogBuilder.create();
        alertDialog.show();
        hideKeyboard();
    }
}