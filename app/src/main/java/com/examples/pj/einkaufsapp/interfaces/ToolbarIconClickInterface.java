package com.examples.pj.einkaufsapp.interfaces;

/**
 * Interface is listening if one of the toolbar icons has been clicked
 */
public interface ToolbarIconClickInterface {

    /**
     * Boolean method if edit icon has been clicked
     * @param clicked
     */
    void toolbarEditIconClicked(boolean clicked);

    /**
     * Boolean method if delete icon has been clicked
     * @param clicked
     */
    void toolbarDeleteIconClicked(boolean clicked);

}
