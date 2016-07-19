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
import android.widget.EditText;
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
    private ProductItemDataSource dataSource;
    private SharedPreferencesManager sharedPreferencesManager;
    private List<ProductItem> currentList;         //items on current shopping list stored in SP
    private String selectedCategory;
    private List<ProductItem> productItemList;    //all items ever stored in DB
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
        if (sharedPreferencesManager == null) {
            sharedPreferencesManager = SharedPreferencesManager.initSharedPreferences((Activity) context);
        }

        //Database
        dataSource = new ProductItemDataSource(context);
        dataSource.open(); //open connection to db

        productItemList = new ArrayList<>();
        currentList = sharedPreferencesManager.loadCurrentShoppingListFromLocalStore();
        initializeProductItemsListView();
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
        dataSource.close(); //close db connection
    }

    @Override
    public void onResume() {
        super.onResume();
        toolbarTitle = toolbarTitleFragment;
        showEditAndDeleteIconInToolbar = false;
        setToolbarEditAndDeleteIcon(showEditAndDeleteIconInToolbar);

        dataSource.open(); //open db connection

        currentList = sharedPreferencesManager.loadCurrentShoppingListFromLocalStore(); //get local List
        productItemList.clear();
        productItemList = dataSource.getAllProductItems();    //get General List

        // Spinner with Array Adapter writes Selection to "selectedCategory"
        drawCategorySpinner();
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
        addNewProductToListIfValid();
    }

    @OnClick(R.id.toolbarDeleteIv)
    public void onToolbarDeleteClick() {
        System.out.println("TrashBin clicked");
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

    @OnClick(R.id.toolbarEditIv)
    public void onToolbarEditClick() {
        System.out.println("EditIcon clicked");
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

    public void addNewProductToListIfValid() {
        ProductItem currentProductObject;
        String currentProductName = "";
        currentProductName = editTextProduct.getText().toString().trim();   //filter empty strings except for space

        if (!"".equals(currentProductName) && !currentProductName.isEmpty()) {  //check if input contains characters
            boolean specialCharacterFound = StringUtils.stringContainsSpecialCharacters(currentProductName);

            if (!specialCharacterFound) {      //only if there are no special characters in input
                currentProductName = editTextProduct.getText().toString().substring(0, 1).toUpperCase() + editTextProduct.getText().toString().substring(1);
                long currentProductID = 0;
                Log.d(LOG_TAG, "Contents of SQLITE DB: " + productItemList.toString());
                existingProductFound = false;
                existingProductInCurrentListFound = false;
                //check if entered productName (lower case) already is stored in SQLiteDB
                for (ProductItem product : productItemList) {
                    if (product.getProduct().toLowerCase().equals(currentProductName.toLowerCase())) {
                        currentProductObject = product;
                        //if product is NOT in list already: add to currentList
                        for (ProductItem currentProduct : currentList) {
                            if (currentProduct.getProduct().equals(currentProductObject.getProduct())) {
                                existingProductInCurrentListFound = true;
                                break;
                            }
                        }
                        if (!existingProductInCurrentListFound) {
                            currentList.add(currentProductObject);
                            currentList = sortListCategoryAndAlphabetical(currentList);

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
                    currentProductObject = new ProductItem(currentProductID, currentProductName, selectedCategory, 0, false, false); //create new object
                    currentList.add(currentProductObject);
                    currentList = sortListCategoryAndAlphabetical(currentList);
                    sharedPreferencesManager.saveCurrentShoppingListToLocalStore(currentList);

                    dataSource.createProductItem(currentProductName, selectedCategory);    //add product to general list
                    Log.d(LOG_TAG, "Added new product to list: " + currentProductName);
                }
                //store currentList to SP
                sharedPreferencesManager.saveCurrentShoppingListToLocalStore(currentList);
                //LOG output
                Log.d(LOG_TAG, "-------------------LOCAL LIST ENTRIES -----------------------");
                for (ProductItem product : currentList) {
                    Log.d(LOG_TAG, "LocalProduct: " + product.toString());
                }
                editTextProduct.setText("");

                //Hide Softkeyboard and refresh list
                ViewUtils.hideKeyboard((Activity) context);
                currentListAdapter.notifyDataSetChanged();   //Anzeigen aller Datenbank Einträge in der ListView
            } else {
                editTextProduct.setError(getString(R.string.editText_errorMessage));    //if string contains special characters, set error message
            }
        } else {
            editTextProduct.setError(getString(R.string.editText_errorMessage));    //if empty string, set error message
        }
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

    private void initializeProductItemsListView() {
        currentListAdapter = new CurrentListAdapter(currentList, context, dataSource, sharedPreferencesManager, this, showEditAndDeleteIconInToolbar);
        final LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getActivity());
        linearLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        currentListRv.setLayoutManager(linearLayoutManager);
        currentListRv.setAdapter(currentListAdapter);
        currentListRv.setHasFixedSize(true);
        currentListRv.setVisibility(View.VISIBLE);
    }

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

    public List<ProductItem> sortListCategoryAndAlphabetical(List<ProductItem> list) {
        Collections.sort(list, new CurrentListAlphabeticalComparator());
        Collections.sort(list, new CurrentListCategoryComparator());
        return list;
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
    // ALERT DIALOGS
    //---------------------------------------------------------------

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
        // set title and message
        alertDialogBuilder.setTitle("Ändern");

        //layout for alert dialog
        LayoutInflater inflater = getActivity().getLayoutInflater();
        View dialogsView = inflater.inflate(R.layout.dialog_edit_product_item, null);

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
                        ProductItem updatedProductItem = dataSource.updateProductItem(itemToChange.getId(), product, selectedCategory, bought, itemToChange.isDone(), itemToChange.isFavourite());
                        // update local list
                        currentList.remove(itemToChange);
                        currentList.add(updatedProductItem);
                        currentList = sortListCategoryAndAlphabetical(currentList);
                        sharedPreferencesManager.saveCurrentShoppingListToLocalStore(currentList); //store to SP
                        Log.d(LOG_TAG, "Alter Eintrag - ID: " + itemToChange.getId() + " Inhalt: " + itemToChange.toString());
                        Log.d(LOG_TAG, "Neuer Eintrag - ID: " + updatedProductItem.getId() + " Inhalt: " + updatedProductItem.toString());

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

    //---------------------------------------------------------------
    // COMPARATORS FOR LIST SORTING
    //---------------------------------------------------------------

    public class CurrentListAlphabeticalComparator implements Comparator<ProductItem> {
        public int compare(ProductItem left, ProductItem right) {
            return left.getProduct().compareTo(right.getProduct());
        }
    }

    public class CurrentListCategoryComparator implements Comparator<ProductItem> {
        public int compare(ProductItem left, ProductItem right) {
            return left.getCategory().compareTo(right.getCategory());
        }
    }
}