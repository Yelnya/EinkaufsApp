package com.examples.pj.einkaufsapp.fragments;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ExpandableListView;
import android.widget.ExpandableListView.OnChildClickListener;
import android.widget.Toast;

import com.examples.pj.einkaufsapp.ChildInfo;
import com.examples.pj.einkaufsapp.HeaderInfo;
import com.examples.pj.einkaufsapp.R;
import com.examples.pj.einkaufsapp.adapters.MyListAdapter;

import java.util.ArrayList;
import java.util.LinkedHashMap;

/**
 * Class as Container for Test Information
 */
public class TestFragment extends BaseFragment {
    public static final String LOG_TAG = TestFragment.class.getSimpleName();

    private boolean showEditAndDeleteIconInToolbar;
    private static final String TOOLBAR_TITLE = "Test";

    Context context;

    private LinkedHashMap<String, HeaderInfo> myDepartments = new LinkedHashMap<String, HeaderInfo>();
    private ArrayList<HeaderInfo> deptList = new ArrayList<HeaderInfo>();

    private MyListAdapter listAdapter;
    private ExpandableListView myList;

    //================================================================================
    // Fragment Instantiation
    //================================================================================

    /**
     * Constructor
     */
    public TestFragment() {
        super(LOG_TAG, true);   //influences Hamburger Icon HomeUp in Toolbar
    }

    /**
     * createInstance as container for bundle arguments
     *
     * @return fragment
     */
    public static BaseFragment createInstance() {
        final BaseFragment fragment = new TestFragment();
        final Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    //================================================================================
    // Base Methods
    //================================================================================

    @Override
    protected int getLayoutId() {
        return R.layout.fragment_test;
    }

    @Override
    protected void setToolbar() {
        getAttachedActivity().setToolbar(toolbar, true, TOOLBAR_TITLE, showEditAndDeleteIconInToolbar); //Icon displayed, Titel of Toolbar
    }

//    @Override
//    protected void setToolbarEditAndDeleteIcon(boolean showEditAndDeleteIconInToolbar) {
//        toolbarTv.setText(TOOLBAR_TITLE);
//    }

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
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_test, null);

        context = super.getActivity();

        //Just add some data to start with
        loadData();

        //get reference to the ExpandableListView
        myList = (ExpandableListView) v.findViewById(R.id.myList);
        //create the adapter by passing your ArrayList data
        listAdapter = new MyListAdapter(getActivity(), deptList);
        //attach the adapter to the list
        myList.setAdapter(listAdapter);

        //expand all Groups
        expandAll();

        //listener for child row click
        myList.setOnChildClickListener(myListItemClicked);
        //listener for group heading click
        myList.setOnGroupClickListener(myListGroupClicked);



        return v;
    }

    @Override
    public void onResume() {
        super.onResume();

        showEditAndDeleteIconInToolbar = false;
        setToolbarEditAndDeleteIcon(showEditAndDeleteIconInToolbar);
    }

    //method to expand all groups
    private void expandAll() {
        int count = listAdapter.getGroupCount();
        for (int i = 0; i < count; i++){
            myList.expandGroup(i);
        }
    }

    //method to collapse all groups
    private void collapseAll() {
        int count = listAdapter.getGroupCount();
        for (int i = 0; i < count; i++){
            myList.collapseGroup(i);
        }
    }

    //load some initial data into out list
    private void loadData(){

        addProduct("Apparel","Activewear");
        addProduct("Apparel","Jackets");
        addProduct("Apparel","Shorts");

        addProduct("Beauty","Fragrances");
        addProduct("Beauty","Makeup");

    }

    //our child listener
    private OnChildClickListener myListItemClicked =  new OnChildClickListener() {

        public boolean onChildClick(ExpandableListView parent, View v,
                                    int groupPosition, int childPosition, long id) {

            //get the group header
            HeaderInfo headerInfo = deptList.get(groupPosition);

            //get the child info
            ChildInfo detailInfo =  headerInfo.getProductList().get(childPosition);
            //display it or do something with it
            Toast.makeText(context, "Clicked on Detail " + headerInfo.getName()
                    + "/" + detailInfo.getName(), Toast.LENGTH_LONG).show();
            return false;
        }

    };


    //our group listener
    private ExpandableListView.OnGroupClickListener myListGroupClicked =  new ExpandableListView.OnGroupClickListener() {

        public boolean onGroupClick(ExpandableListView parent, View v,
                                    int groupPosition, long id) {

            //get the group header
            HeaderInfo headerInfo = deptList.get(groupPosition);
            //display it or do something with it
            Toast.makeText(context, "Child on Header " + headerInfo.getName(),
                    Toast.LENGTH_LONG).show();

            return false;
        }

    };

    //here we maintain our products in various departments
    private int addProduct(String department, String product){

        int groupPosition = 0;

        //check the hash map if the group already exists
        HeaderInfo headerInfo = myDepartments.get(department);
        //add the group if doesn't exists
        if(headerInfo == null){
            headerInfo = new HeaderInfo();
            headerInfo.setName(department);
            myDepartments.put(department, headerInfo);
            deptList.add(headerInfo);
        }

        //get the children for the group
        ArrayList<ChildInfo> productList = headerInfo.getProductList();
        //size of the children list
        int listSize = productList.size();
        //add to the counter
        listSize++;

        //create a new child and add that to the group
        ChildInfo detailInfo = new ChildInfo();
        detailInfo.setSequence(String.valueOf(listSize));
        detailInfo.setName(product);
        productList.add(detailInfo);
        headerInfo.setProductList(productList);

        //find the group position inside the list
        groupPosition = deptList.indexOf(headerInfo);
        return groupPosition;
    }

}