package com.examples.pj.einkaufsapp.fragments;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;

import com.examples.pj.einkaufsapp.R;
import com.examples.pj.einkaufsapp.adapters.CurrentListAdapter;
import com.examples.pj.einkaufsapp.dbentities.ProductItem;
import com.examples.pj.einkaufsapp.dbentities.ProductItemDataSource;
import com.examples.pj.einkaufsapp.interfaces.ChangeToolbarInterface;
import com.examples.pj.einkaufsapp.util.SharedPreferencesManager;
import com.examples.pj.einkaufsapp.util.StringUtils;
import com.examples.pj.einkaufsapp.util.ViewUtils;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import butterknife.Bind;
import butterknife.OnClick;

/**
 * CLASS: add, delete and edit the productItems in current List
 */
public class CurrentListFragment extends BaseFragment implements ChangeToolbarInterface {
    public static final String LOG_TAG = CurrentListFragment.class.getSimpleName();

    @Bind(R.id.toolbarDeleteIv)
    ImageView toolbarDeleteIv;
    @Bind(R.id.toolbarEditIv)
    ImageView toolbarEditIv;
    @Bind(R.id.spinner_category)
    Spinner spinner;
    @Bind(R.id.currentlist_recycler_view)
    RecyclerView currentListRv;
    @Bind(R.id.autocompletetv_edittext_product)
    AutoCompleteTextView multiAutoCompleteTextView;

    private Context context;
    private ProductItemDataSource dataSource;
    private SharedPreferencesManager sharedPreferencesManager;
    private List<ProductItem> currentList;         //items on current shopping list stored in SP
    private String selectedCategory;
    private List<ProductItem> generalProductItemList;    //all items ever stored in DB
    boolean existingProductFound;
    boolean existingProductInCurrentListFound;
    private CurrentListAdapter currentListAdapter;
    private String toolbarTitle = "";
    private static final String TOOLBAR_TITLE_FRAGMENT = "Aktuelle Einkaufsliste";
    private static final String TOOLBAR_TITLE_EDIT = "Löschen/Ändern";
    private boolean showEditAndDeleteIconInToolbar;
    private boolean showShoppingCartIconInToolbar;

    //================================================================================
    // Fragment Instantiation
    //================================================================================

    /**
     * CONSTRUCTOR: standard
     */
    public CurrentListFragment() {
        super(LOG_TAG, true);   //influences Hamburger Icon HomeUp in Toolbar
    }

    /**
     * CreateInstance: processing bundle arguments
     *
     * @return fragment with arguments in bundle
     */
    public static BaseFragment createInstance() {
        final BaseFragment fragment = new CurrentListFragment();
        final Bundle args = new Bundle();
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
        getAttachedActivity().setToolbar(toolbar, true, toolbarTitle, showEditAndDeleteIconInToolbar, showShoppingCartIconInToolbar); //Icon displayed, Titel of Toolbar
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

    @Override
    protected void onCleanUp() {

    }

    //================================================================================
    // Fragment Lifecycle
    //================================================================================

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        showShoppingCartIconInToolbar = false;
        showEditAndDeleteIconInToolbar = false;

        context = getActivity();

        if (sharedPreferencesManager == null) {
            sharedPreferencesManager = SharedPreferencesManager.initSharedPreferences((Activity) context);
        }

        dataSource = new ProductItemDataSource(context);    //initializing database
        dataSource.open(); //open connection to db

        generalProductItemList = new ArrayList<>();
        currentList = sharedPreferencesManager.loadCurrentShoppingListFromLocalStore();
        initializeProductItemsListView();
    }

    @Override
    public void onPause() {
        super.onPause();
        dataSource.close(); //close db connection
    }

    @Override
    public void onResume() {
        super.onResume();
        toolbarTitle = TOOLBAR_TITLE_FRAGMENT;
        setToolbarEditAndDeleteIcon(showEditAndDeleteIconInToolbar);

        dataSource.open(); //open db connection

        currentList = sharedPreferencesManager.loadCurrentShoppingListFromLocalStore(); //get local List
        generalProductItemList.clear();
        generalProductItemList = dataSource.getAllProductItems();    //get General List

        //auto complete list
        List<String> productNamesGeneral = new ArrayList<>();
        for (ProductItem item : generalProductItemList) {
            productNamesGeneral.add(item.getProduct());
        }
        ArrayAdapter adapter = new ArrayAdapter(context, android.R.layout.simple_list_item_1, productNamesGeneral);
        multiAutoCompleteTextView.setAdapter(adapter);

        // Spinner with Array Adapter writes Selection to "selectedCategory"
        drawCategorySpinner();
    }

    //---------------------------------------------------------------
    // Butter Knife Methods
    //---------------------------------------------------------------

    /**
     * OnClick: Adding new Product when button is clicked
     */
    @OnClick(R.id.button_add_product)
    public void onPlusButtonClick() {
        if (currentList == null) {
            currentList = new ArrayList<>();
        }
        addNewProductToListIfValid();
    }

    /**
     * OnClick: Delete Product when Trashbin Icon in Toolbar is clicked
     */
    @OnClick(R.id.toolbarDeleteIv)
    public void onToolbarDeleteClick() {
        Log.d(LOG_TAG, "TrashBin clicked");

        ProductItem clickedItem = currentListAdapter.getItemClicked(); //get item clicked from Adapter
        ProductItem matchingItemInLocalList = null;
        for (ProductItem item : currentList) { //find item and delete it from local list
            if (item.getId() == (clickedItem.getId())) {
                matchingItemInLocalList = item;
                break;
            }
        }
        createReallyDeleteDialog(matchingItemInLocalList);
    }

    /**
     * OnClick: Edit Product when Edit Icon in Toolbar is clicked
     */
    @OnClick(R.id.toolbarEditIv)
    public void onToolbarEditClick() {
        Log.d(LOG_TAG, "EditIcon clicked");
        ProductItem clickedItem = currentListAdapter.getItemClicked(); //get item clicked from Adapter
        ProductItem matchingItemInLocalList = null;
        for (ProductItem item : currentList) { //find item and delete it from local list
            if (item.getId() == (clickedItem.getId())) {
                matchingItemInLocalList = item;
                break;
            }
        }
        createEditProductItemDialog(matchingItemInLocalList);
    }

    /**
     * addNewProductToListIfValid: check if entered string is valid, then add to current list
     */
    public void addNewProductToListIfValid() {
        String currentProductName;
        currentProductName = multiAutoCompleteTextView.getText().toString().trim();   //filter empty strings except for space

        if (!"".equals(currentProductName) && !currentProductName.isEmpty()) {  //check if input contains characters
            boolean specialCharacterFound = StringUtils.stringContainsSpecialCharacters(currentProductName);

            if (!specialCharacterFound) {      //only if there are no special characters in input
                currentProductName = multiAutoCompleteTextView.getText().toString().substring(0, 1).toUpperCase() + multiAutoCompleteTextView.getText().toString().substring(1);
                Log.d(LOG_TAG, "Contents of SQLITE DB: " + generalProductItemList.toString());
                existingProductFound = false;
                existingProductInCurrentListFound = false;
                //check if entered productName (lower case) already is stored in SQLiteDB
                searchListForProductThenDecideToUpdate(currentProductName);

                multiAutoCompleteTextView.setText("");        //reset textView to blank
                ViewUtils.hideKeyboard((Activity) context);
                currentListAdapter.notifyDataSetChanged();   //refresh ListView
            } else {
                multiAutoCompleteTextView.setError(getString(R.string.editText_errorMessage));    //if string contains special characters, set error message
            }
        } else {
            multiAutoCompleteTextView.setError(getString(R.string.editText_errorMessage));    //if empty string, set error message
        }
    }

    //---------------------------------------------------------------
    // Other Methods
    //---------------------------------------------------------------

    private void toolbarBackToNormal() {
        showEditAndDeleteIconInToolbar = false;
        toolbarTitle = TOOLBAR_TITLE_FRAGMENT;
        setToolbarEditAndDeleteIcon(showEditAndDeleteIconInToolbar);
        currentListAdapter.setEditDeleteToolbarActive(false);
    }

    private void initializeProductItemsListView() {
        currentListAdapter = new CurrentListAdapter(currentList, context, dataSource, sharedPreferencesManager, this, showEditAndDeleteIconInToolbar);
        final LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getActivity());
        linearLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        currentListRv.setLayoutManager(linearLayoutManager);
        currentListRv.setAdapter(currentListAdapter);
        currentListRv.setHasFixedSize(true);
        currentListRv.setVisibility(View.VISIBLE);
    }

    /**
     * drawCategorySpinner: add category spinner to view
     */
    public void drawCategorySpinner() {
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

    /**
     * sortListCategoryAndAlphabetical: sort items from currentList alphabetically
     *
     * @param list: currentList
     * @return currentList, sorted alphabetically and referring to categories
     */
    public List<ProductItem> sortListCategoryAndAlphabetical(List<ProductItem> list) {
        Collections.sort(list, new StringUtils.CurrentListAlphabeticalComparator());
        Collections.sort(list, new StringUtils.CurrentListCategoryComparator());
        return list;
    }

    private void searchListForProductThenDecideToUpdate(String currentProductName) {
        ProductItem currentProductObject;
        //search generalProductItemList: was product ever stored before?
        for (ProductItem product : generalProductItemList) {
            if (product.getProduct().equalsIgnoreCase(currentProductName)) {
                currentProductObject = product;

                //if found in general list -> search current list: if product is NOT in list already: add to currentList
                for (ProductItem currentProduct : currentList) {
                    if (currentProduct.getProduct().equals(currentProductObject.getProduct())) {
                        existingProductInCurrentListFound = true;   //product already is in current list -> found duplicate
                        break;
                    }
                }
                if (!existingProductInCurrentListFound) {
                    currentProductObject.setDone(false);
                    currentList.add(currentProductObject);  //if no duplicate found, add item to current list, refresh and store new list
                    currentList = sortListCategoryAndAlphabetical(currentList);
                    sharedPreferencesManager.saveCurrentShoppingListToLocalStore(currentList);
                } else {
                    toast("Produkt befindet sich schon in der Liste");  //found duplicate in list: inform user, but do nothing
                }
                existingProductFound = true;
                break;
            }
        }

        if (!existingProductFound) {    //if no duplicate found, create new productItem and store in currentList and generalList
            long currentProductID = dataSource.getHighestID() + 1; //new ID: get highest ID in DB and +1
            currentProductObject = new ProductItem(currentProductID, currentProductName, selectedCategory, 0, false, false, false); //create new object
            generalProductItemList.add(currentProductObject);   //update generalList
            currentList.add(currentProductObject);  //update currentList
            currentList = sortListCategoryAndAlphabetical(currentList);
            sharedPreferencesManager.saveCurrentShoppingListToLocalStore(currentList);  //store locally

            dataSource.createProductItem(currentProductName, selectedCategory);    //add product to database
        }
    }

    //---------------------------------------------------------------
    // ChangeToolbarInterface Methods
    //---------------------------------------------------------------

    @Override
    public void showEditAndDeleteIcon(boolean show) {
        toolbarTitle = show ? TOOLBAR_TITLE_EDIT : TOOLBAR_TITLE_FRAGMENT;
        showEditAndDeleteIconInToolbar = show;
        setToolbarEditAndDeleteIcon(show);
        setToolbar();
    }



    //---------------------------------------------------------------
    // ALERT DIALOGS
    //---------------------------------------------------------------

    /**
     * createReallyDeleteDialog: create Alert Dialog Before Deleting Icon from current List
     *
     * @param itemToDelete the productItem selected by the user to be deleted
     */
    public void createReallyDeleteDialog(final ProductItem itemToDelete) {
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
        ViewUtils.hideKeyboard((Activity) context);
    }

    private void createEditProductItemDialog(final ProductItem itemToChange) {
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(context);

        alertDialogBuilder.setTitle("Ändern");  //set title

        //layout for alert dialog
        LayoutInflater inflater = getActivity().getLayoutInflater();
        View dialogsView = inflater.inflate(R.layout.dialog_edit_product_item, null);

        final EditText editTextNewProduct = (EditText) dialogsView.findViewById(R.id.editText_new_product);
        editTextNewProduct.setText(itemToChange.getProduct());

        // Category Spinner: Create an ArrayAdapter using the string array and a default spinner layout
        final Spinner alertSpinner = (Spinner) dialogsView.findViewById(R.id.alert_spinner_category);
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(context,
                R.array.categories_array, android.R.layout.simple_spinner_item);
        // Specify the layout to use when the list of choices appears
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        // Apply the adapter to the spinner
        alertSpinner.setAdapter(adapter);
        for (int i = 0; i < alertSpinner.getAdapter().getCount(); i++) {
            if (alertSpinner.getAdapter().getItem(i).toString().equals(itemToChange.getCategory())) {
                alertSpinner.setSelection(i);
            }
        }
        alertSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
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
                        dataSource.updateProductItem(itemToChange.getId(), product, selectedCategory, bought, itemToChange.isDone(), itemToChange.isFavourite());
                        ProductItem updatedProductItem = dataSource.getProductItemFromDB(itemToChange.getId());
                        currentList.remove(itemToChange); // update local list
                        currentList.add(updatedProductItem);
                        currentList = sortListCategoryAndAlphabetical(currentList);
                        sharedPreferencesManager.saveCurrentShoppingListToLocalStore(currentList); //store to SP
                        Log.d(LOG_TAG, "Alter Eintrag - ID: " + itemToChange.getId() + " Inhalt: " + itemToChange.toString());
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
        ViewUtils.hideKeyboard((Activity) context);
    }
}