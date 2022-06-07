package com.zjy.pdfview.widget;


public interface IPDFController {

    void addOperateListener(OperateListener listener);

    void setPageIndexText(String text);

    interface OperateListener {

        void clickPrevious();

        void clickNext();
    }

}
